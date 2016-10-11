#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <cs50.h>

int main(int argc, char* argv[])
{
    if(argc != 2)
    {
        printf("No key provided. Exiting . . .\n");
        return 1;
    }
    for(int i = 0, n = strlen(argv[1]); i < n; ++i)
    {
        if(!isalpha(argv[1][i]))
        {
            printf("Invalid input. Exiting . . .\n");
            return 1;
        }
    }
    
    int i = 0, j = 0, m = 0, n = 0;
    char orig_str[50], enc_str[50], key[50];
    
    strcpy(key, argv[1]);
    fgets(orig_str, 50, stdin);
    
    for(i = 0, m = strlen(key); i < m; ++i)
        key[i] = tolower(key[i]);
    
    for(i = 0, j = 0, m = strlen(orig_str), n = strlen(key); i < m; ++i)
    {
        if(isalpha(orig_str[i]))
        {
            j = j % n;
            if(islower(orig_str[i]))
            {
                if((orig_str[i] + (key[j] - 97)) > 122)
                    enc_str[i] = 96 + ((orig_str[i] + (key[j] - 97)) % 122);
                else
                    enc_str[i] = 97 + ((orig_str[i] + (key[j] - 97)) % 97);
            }
            else
            {
                if((orig_str[i] + (key[j] - 97)) > 90)
                    enc_str[i] = 64 + ((orig_str[i] + (key[j] - 97)) % 90);
                else
                    enc_str[i] = 65 + ((orig_str[i] + (key[j] - 97)) % 65);
            }
            ++j;
        }
        else
            enc_str[i] = orig_str[i];
    }
    enc_str[i] = '\0';
    
    printf("%s", enc_str);
    
    return 0;
}
