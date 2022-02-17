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
#include <unistd.h>
#include <signal.h>

/* Define some constants. */
#define USERNAME_SIZE (32)
#define NOUSER (-1)
#define PROGRAM "/usr/bin/xterm"

int setgid(gid_t);
int setegid(gid_t);
int setuid(uid_t);
int seteuid(uid_t);
pid_t waitpid(pid_t, int *, int);

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
    printf("Unseccesful logins: %d\n", p->pw_failed);
    printf("Succesful logins: %d\n", p->pw_age);
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

void update_IDs(stuct pwdb_passwd *p){
	setgid(p->pw_gid);
	setegid(p->pw_gid);
	setuid(p->pw_uid);
	seteuid(p->pw_uid);
}

void open_user_shell(struct pwdb_passwd *p){
  pid_t pid; 
  int status;

  pid = fork();

  if (pid==0) {
    /* This is the child process. Run an xterm window */
    execl(PROGRAM,PROGRAM,"-e",p->pw_shell,"-l",NULL);

    /* if child returns we must inform parent.
     * Always exit a child process with _exit() and not return() or exit().
     */
    _exit(-1);
  } else if (pid < 0) { /* Fork failed */
    printf("Fork faild\n");
    status = -1;
  } else {
    /* This is parent process. Wait for child to complete */
	if (waitpid(pid, &status, 0) != pid) {
	  status = -1;
	}
  }
}


int main(int argc, char **argv)
{
  signal(SIGINT, SIG_IGN);
  char username[USERNAME_SIZE];

  while(1){
    /* 
    * Write "login: " and read user input. Copies the username to the
    * username variable.
    */
    read_username(username);
    struct pwdb_passwd *p = pwdb_getpwnam(username);

    /* Show user info from our local pwfile. */
    if (print_info(username) == NOUSER) {
        /* if there are no user with that username... */
        printf("\nUnknown user or password.\n");
   
    }else{
      if(p->pw_failed >= 5){
      	printf("Your account has been locked. Please contact the Administrator!\n");
      	return 0;
      }
	
      const char* user_password = get_password(username);

      const char* input_password = getpass("password: ");
      char salt[2];
      strncpy(salt, user_password, 2);
      const char* encrypted_password = crypt(input_password, salt);

      if(strcmp(user_password, encrypted_password) == 0){ // Identical passwords
        printf("\nUser authenticated succesfully!\n");
        p->pw_failed = 0;
        p->pw_age++;

        if(p->pw_age >= 10){
          printf("You have used this password for a while. Please update your password for better securtiy!\n");
        }

        int update = pwdb_update_user(p);
        
	      update_IDs(p);

        open_user_shell(p);

      }else{
        p->pw_failed++;

        if(p->pw_failed >= 5){
          printf("Too many login attempts! Please contact the Administrator!\n");
          int update = pwdb_update_user(p);
          return 0;
        }

        int update = pwdb_update_user(p);
        printf("\nUnvalid username or password.\n");
      }
    }
  }
  
}
  

  
