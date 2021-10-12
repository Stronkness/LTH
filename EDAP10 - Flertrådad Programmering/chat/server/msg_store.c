#include <pthread.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "fail.h"
#include "list.h"
#include "msg_store.h"

struct msg_store {
  // A list of topics. Each element points to another list,
  // holding the messages in that topic.
  struct list *topics;
  pthread_mutex_t mutex;
  pthread_cond_t cond;
};

// one list item, representing a message including metadata
struct message {
  char *username;
  char *text;
};

// ============================================================================

// local helper function: returns true if more topics available to the client
static bool
any_topic_available(struct msg_store *store,
                    struct client_state *client)
{
  return (list_size(client->message_counts) < list_size(store->topics));
}

// ----------------------------------------------------------------------------

// local helper function: returns true if any more messages are available
static bool
any_message_available(struct msg_store *store,
                      struct client_state *client)
{
  for (int i = 0; i < list_size(client->message_counts); i++) {
    struct list *messages_in_topic = list_get(store->topics, i);
    if (list_size(messages_in_topic) > list_get_int(client->message_counts, i)) {
      return true;
    }
  }

  return false;
}

// ----------------------------------------------------------------------------

// local helper function: returns true if the client is now logging out
static bool
client_is_logging_out(struct client_state *client)
{
  return (client->current_topic_id == TOPIC_STATE_LOGOUT_REQUESTED
          || client->current_topic_id == TOPIC_STATE_DISCONNECTED);
}

// ============================================================================

struct msg_store *
msg_store_create()
{
  struct msg_store *store = malloc(sizeof(struct msg_store));

  store->topics = list_create();
  pthread_mutex_init(&(store->mutex), NULL);

  pthread_cond_init(&(store->cond), NULL);

  return store;
}

// ----------------------------------------------------------------------------

int
msg_store_add_topic(struct msg_store *store,
                    char *username,
                    char *text)
{

  pthread_mutex_lock(&(store->mutex));
  struct message *m = malloc(sizeof(struct message));
  m->username = strdup(username);
  m->text = strdup(text);

  struct list *topic = list_create();
  list_add(topic, m);
  list_add(store->topics, topic);
  int topic_id = list_size(store->topics) - 1;

  pthread_cond_broadcast(&(store->cond));
  pthread_mutex_unlock(&(store->mutex));
  return topic_id;
}

// ----------------------------------------------------------------------------

void
msg_store_add_message(struct msg_store *store,
                      struct client_state *client,
                      char *username,
                      char *text)
{
  pthread_mutex_lock(&(store->mutex));
  struct message *m = malloc(sizeof(struct message));
  m->username = strdup(username);
  m->text = strdup(text);

  struct list *topic = list_get(store->topics, client->current_topic_id);
  list_add(topic, m);

  pthread_cond_broadcast(&(store->cond));
  pthread_mutex_unlock(&(store->mutex));
}

// ----------------------------------------------------------------------------

bool
msg_store_check_for_new_topic(struct msg_store *store,
                              struct client_state *client,
                              int *topic_id,
                              char **username,
                              char **text)
{

  bool available = any_topic_available(store, client);
  if (available) {
    list_add_int(client->message_counts, 0);
    int last_topic_published = list_size(client->message_counts) - 1;
    *topic_id = last_topic_published;
    struct list *topic = list_get(store->topics, last_topic_published);

    // the first message in the topic becomes the heading for the topic
    struct message *m = list_get(topic, 0);
    *username = m->username;
    *text = m->text;
  }

  return available;
}

// ----------------------------------------------------------------------------

bool
msg_store_check_for_new_message(struct msg_store *store,
                                struct client_state *client,
                                int *topic_id,
                                int *message_id,
                                char **username,
                                char **text)
{

  bool available = (client->current_topic_id >= 0)
                   && (client->current_topic_id < list_size(store->topics));
  if (available) {
    struct list *topic = list_get(store->topics, client->current_topic_id);
    if (client->nbr_read >= list_size(topic)) {
      available = false;
    } else {
      struct message *m = list_get(topic, client->nbr_read);
      *topic_id = client->current_topic_id;
      *message_id = client->nbr_read;
      *username = m->username;
      *text = m->text;

      client->nbr_read++;
    }
  }
  return available;
}

// ----------------------------------------------------------------------------

bool
msg_store_check_for_updated_message_count(struct msg_store *store,
                                          struct client_state *client,
                                          int *topic_id,
                                          int *message_count)
{

  bool available = false;
  for (int i = 0; i < list_size(client->message_counts); i++) {
    struct list *messages = list_get(store->topics, i);
    int count = list_size(messages);
    if (list_get_int(client->message_counts, i) != count) {
      list_set_int(client->message_counts, i, count);
      *topic_id = i;
      *message_count = count;
      available = true;
      break;
    }
  }

  return available;
}

// ----------------------------------------------------------------------------

int
msg_store_await_message_or_topic(struct msg_store *store,
                                 struct client_state *client)
{
  pthread_mutex_lock(&(store->mutex));
  while (! any_topic_available(store, client)
      && ! any_message_available(store, client)
      && ! client_is_logging_out(client))
  {
    pthread_cond_wait(&(store->cond), &(store->mutex));
  }

  int reading_state = client->current_topic_id;

  pthread_mutex_unlock(&(store->mutex));
  return reading_state;
}

// ----------------------------------------------------------------------------

void
msg_store_init_client(struct msg_store *store,
                      struct client_state *client)
{

  client->current_topic_id = TOPIC_STATE_NO_TOPIC;
  client->message_counts = list_create();

}

// ----------------------------------------------------------------------------

void
msg_store_dispose_client(struct msg_store *store,
                         struct client_state *client)
{

  list_destroy(client->message_counts);
}

// ----------------------------------------------------------------------------

void
msg_store_select_topic(struct msg_store *store,
                       struct client_state *client,
                       int topic_id)
{

  client->current_topic_id = topic_id;
  if (topic_id >= 0) {
    list_set_int(client->message_counts, topic_id, 0);
  }
  client->nbr_read = 0;

  pthread_mutex_lock(&(store->mutex));
  pthread_cond_broadcast(&(store->cond));
  pthread_mutex_unlock(&(store->mutex));

}
