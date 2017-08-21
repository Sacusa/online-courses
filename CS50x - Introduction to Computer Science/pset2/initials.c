#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <cs50.h>

int main(void)
{    
    string str = GetString();
    
    printf("%c", toupper(str[0]));
    for(int i = 1, n = strlen(str); i < n; ++i)
    {
        if (str[i] == ' ')
        {
            printf("%c", toupper(str[i + 1]));
        }
    }
    
    printf("\n");
    
    return 0;
}
