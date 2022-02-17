/*
 * Shows user info from local pwfile.
 *  
 * Usage: userinfo username
 */

#define _XOPEN_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "pwdblib.h"   /* include header declarations for pwdblib.c */
#include <crypt.h>

/* Define some constants. */
#define USERNAME_SIZE (32)
#define NOUSER (-1)


int print_info(const char *username)
{
  struct pwdb_passwd *p = pwdb_getpwnam(username);
  if (p != NULL) {
    printf("Name: %s\n", p->pw_name);
    printf("Passwd: %s\n", p->pw_passwd);
    printf("Uid: %u\n", p->pw_uid);
    printf("Gid: %u\n", p->pw_gid);
    printf("Real name: %s\n", p->pw_gecos);
    printf("Home dir: %s\n",p->pw_dir);
    printf("Shell: %s\n", p->pw_shell);
	return 0;
  } else {
    return NOUSER;
  }
}

char* get_password(const char *username)
{
  struct pwdb_passwd *p = pwdb_getpwnam(username);
  if (p != NULL) {
	  return p->pw_passwd;
  } 
}

void read_username(char *username)
{
  printf("login: ");
  fgets(username, USERNAME_SIZE, stdin);

  /* remove the newline included by getline() */
  username[strlen(username) - 1] = '\0';
}

int main(int argc, char **argv)
{
  char username[USERNAME_SIZE];
  
  while(1){
    /* 
    * Write "login: " and read user input. Copies the username to the
    * username variable.
    */
    read_username(username);

    /* Show user info from our local pwfile. */
    if (print_info(username) == NOUSER) {
        /* if there are no user with that username... */
        printf("\nUnknown user or incorrect password.\n");
   
    }else{
      const char* user_password = get_password(username);
      const char* input_password = getpass("password: ");

      char salt[2];
      strncpy(salt, user_password, 2);
      const char* encrypted_password = crypt(input_password, salt);

      if(strcmp(user_password, encrypted_password) == 0){ // Identical passwords
        printf("\nUser authenticated succesfully!\n");
        return 0;
      }else{
        printf("\nIncorrect password, start over.\n");
      }
    }
  }
  
}
  

  
