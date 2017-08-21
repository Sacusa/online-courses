#include <stdio.h>
#include <cs50.h>

int main(void)
{
    float change = -1;
    int change_int = 0, no_of_coins = 0;
    
    while(change < 0)
    {
    	printf("\nHow much change is owed?\n");
    	change = GetFloat();
    }
	change_int = change;
    
    no_of_coins = (change_int * 4);
    change -= change_int;
    
    change *= 100;
    
    change_int = change;
    if(((change - change_int) * 100) > 50)
        ++change_int;
        
    while(change_int >= 1)
    {
    	if ((change_int - 25) >= 0)
    	{
    		change_int -= 25;
    		++no_of_coins;
    	}
    	else if ((change_int - 10) >= 0)
    	{
    		change_int -= 10;
    		++no_of_coins;
    	}
    	else if ((change_int - 5) >= 0)
    	{
    		change_int -= 5;
    		++no_of_coins;
    	}
    	else if ((change_int - 1) >= 0)
    	{
    		change_int -= 1;
    		++no_of_coins;
    	}
    }
    
    printf("%i\n", no_of_coins);
    
    return 0;
}
