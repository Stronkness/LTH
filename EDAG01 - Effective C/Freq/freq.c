#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

// These defines are made according to the tests
#define N (9)   // Max string length
#define M (153) // Unique string count
#define QUICKIE_STRCMP(a, b)  (*(a) != *(b) ? (int) ((unsigned char) *(a) - (unsigned char) *(b)) : strcmp((a), (b)))


int isPrime(int n);
int check_word_index(char* curr_word, char** words);

int main (void){
    int line = 0;                             // As stated in the assignment, start at 1 (see before isPrime call)
    char* curr_word = malloc(N*sizeof(char)); // Current word in iteration
    int max_count = 0;                        // Keeps track of the highest amount of the most frequent word
    char* max_word = malloc(N*sizeof(char));  // The word with highest amount of most frequent words
    int* count_index;                         // Get count of word to determine which operation to do
    int* count = calloc(M, sizeof(int));      // Count all unique words
    char** words;                             // Keep words in a matrix

    // Allocate memory to words matrix
    words = calloc(M, sizeof(char*));
    for(int i = 0; i < M; i++){
        words[i] = calloc(N, sizeof(char));
    }

    while (scanf("%s\n", curr_word) != EOF) {
        count_index = &count[check_word_index(curr_word, words)]; // Keeps address to the words total count

        line++; // Initially start from one and increment every loop
        if(isPrime(line)){
          printf("trying to delete %s: ", curr_word);
          
          if(*count_index == 0) printf("not found\n");
          else{
            printf("deleted\n");
            *count_index = 0;
          }

        }else{
          if(*count_index == 0) printf("added %s\n", curr_word); // Is not prime and word is not added
          else printf("counted %s\n", curr_word);                // Count word

          *count_index += 1;
        }
    }

    // Check the maximum word
    for(int i = 0; i < M; i++){
      if(count[i] > max_count){
        max_count = count[i];
        strcpy(max_word, words[i]);
      }
    }

    // Print result
    printf("result: %s %d\n", max_word, max_count);

    // Free as otherwise it will result in memory leak
    for(int i = 0; i < M; i++) free(words[i]);
    free(words);
    free(curr_word);
    free(max_word);
    free(count);
}

// Check current word from stdin if its a new word or it exists, return index of the words placement
int check_word_index(char* curr_word, char** words) {
    for(int i = 0; i < M; i++) {
        if(QUICKIE_STRCMP(words[i], curr_word) == 0) return i; // Found word, return index
        else if(QUICKIE_STRCMP(words[i], "") == 0) { // New word acquired, fill position
            strcpy(words[i], curr_word);
            return i;
        }
    }
    return -1; // This shouldn't happen
}

// Check if n is prime, simply
int isPrime(int n) {
  for (int i = 2; i <= n / 2; i++) {
    if (n % i == 0) return 0;
  }
  return n != 1;
}