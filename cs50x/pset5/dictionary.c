/**
 * dictionary.c
 *
 * Computer Science 50
 * Problem Set 5
 *
 * Implements a dictionary's functionality.
 */

#include <stdbool.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <ctype.h>
#include "dictionary.h"

#define TABLE_SIZE 18954

/**
 * hash table structure
 */
typedef struct node
{
    char word[LENGTH + 2];
    struct node *next;
}
node;

// hash table
node *hash_table[TABLE_SIZE];

// global variable to count number of words
unsigned int no_of_words = 0;

/**
 * hashing function
 */
int hash_func(const char *word)
{
    int hash_value = 0, len = strlen(word);
    
    if (len == 1) {
        hash_value += (word[0] - 97) * 729;
    }
    else if (len == 2) {
        for (int i = 0; i < len; ++i) {
            hash_value += (word[i] - 97) * pow(27, 2 - i);
        }
        
        // add index for whitespaces
        hash_value += (word[1] - 97);
        
        // add index for 'a' at 2nd position
        if (word[1] == 'a') {
            ++hash_value;
        }
    }
    else {
        for (int i = 0; (i < len) && (i < 3); ++i) {
            hash_value += (word[i] - 97) * pow(27, 2 - i);
        }
        
        // add index for whitespaces
        hash_value += (word[1] - 97);
        
        // add index for 'a' at 2nd position
        if (word[1] == 'a') {
            ++hash_value;
        }
        
        // add index for letter at 3rd position
        ++hash_value;
    }
    
    return hash_value;
}

/**
 * Returns true if word is in dictionary else false.
 */
bool check(const char* word)
{
    char *word2;
    word2 = malloc(sizeof(word));
    
    strcpy(word2, word);
    
    for (int i = 0; word[i] != '\0'; ++i) {
        word2[i] = tolower(word[i]);
    }
    
    int hash_value = hash_func(word2);
    
    if (hash_table[hash_value] != NULL) {
        node *ptr = hash_table[hash_value];
        
        while (ptr != NULL) {
            if (strcmp(ptr->word, word2) == 0) {
                return true;
            }
            
            ptr = ptr->next;
        }
    }
    
    return false;
}

/**
 * Loads dictionary into memory.  Returns true if successful else false.
 */
bool load(const char* dictionary)
{
    FILE* dict = fopen(dictionary, "r");
    
    // check if dictionary is loaded
    if (dict == NULL) {
        return false;
    }
    
    node *new_node = malloc(sizeof(node));
    
    // check if there is memory for 'new_node'
    if (new_node == NULL) {
        return false;
    }
    
    // store NULL in all 'hash_table' entries
    for (int i = 0; i < TABLE_SIZE; ++i) {
        hash_table[i] = NULL;
    }
    
    while (fscanf(dict, "%s", new_node->word) != EOF) {
        // store NULL in 'next' of 'new_node'
        new_node->next = NULL;
        
        int hash_value = hash_func(new_node->word);
        
        if (hash_table[hash_value] == NULL) {
            hash_table[hash_value] = new_node;
        }
        else {
            node *ptr = hash_table[hash_value];
            
            // traverse to the end of linked list
            while (ptr->next != NULL) {
                ptr = ptr->next;
            }
            
            ptr->next = new_node;
        }
        
        // allocate space for next word
        new_node = malloc(sizeof(node));
        
        // check if there is memory for 'new_node'
        if (new_node == NULL) {
            return false;
        }
        
        // increase count of words
        ++no_of_words;
    }
    
    free(new_node);
    fclose(dict);
    
    return true;
}

/**
 * Returns number of words in dictionary if loaded else 0 if not yet loaded.
 */
unsigned int size(void)
{
    return no_of_words;
}

/**
 * Unloads dictionary from memory.  Returns true if successful else false.
 */
bool unload(void)
{
    node *ptr;
    
    for (int i = 0; i < TABLE_SIZE; ++i) {
        if (hash_table[i] == NULL) {
            continue;
        }
        
        ptr = hash_table[i];
        
        while (ptr != NULL) {
            node *temp = ptr;
            ptr = ptr->next;
            free(temp);
        }
    }
    
    return true;
}
