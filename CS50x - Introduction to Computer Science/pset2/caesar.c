#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <cs50.h>

int main(int argc, char* argv[])
{
    if(argc != 2)
    {
        printf("No key provided. Exiting . . . \n");
        return 1;
    }
    
    int k = 0, i = 0, n = 0;
    char orig_str[20], enc_str[20];
    
    k = atoi(argv[1]);
    fgets(orig_str, 20, stdin);
    
    while(k >= 26)
        k = k % 26;
    
    for(i = 0, n = strlen(orig_str); i < n; ++i)
    {
        if(isalpha(orig_str[i]))
        {
            if((orig_str[i] + k) > 122)
                enc_str[i] = 96 + ((orig_str[i] + k) % 122);
            else if((orig_str[i] + k) > 97)
                enc_str[i] = 97 + ((orig_str[i] + k) % 97);
            else if((orig_str[i] + k) > 90)
                enc_str[i] = 64 + ((orig_str[i] + k) % 90);
            else
                enc_str[i] = 65 + ((orig_str[i] + k) % 65);
        }
        else
            enc_str[i] = orig_str[i];
    }
    enc_str[i] = '\0';
    
    printf("%s", enc_str);
    
    return 0;
}
