#include "..//tm4c123gh6pm.h"
#include "LED.h"

#define LED (*((volatile unsigned long *)0x400050C0))

// Initializes LED on PB5 and PB4
void LED_Init(void)
{
	volatile unsigned long delay;
	SYSCTL_RCGC2_R |= 0x00000002;				// activate clock for Port B
  delay = SYSCTL_RCGC2_R;       			// allow time for clock to stabilize
  GPIO_PORTB_AMSEL_R &= ~0x30;      	// disable analog function
  GPIO_PORTB_PCTL_R &= ~0x000000030;	// GPIO clear bit PCTL  
  GPIO_PORTB_DIR_R |= 0x30; 	   	    // PB5,PB4 output
  GPIO_PORTB_AFSEL_R &= ~0x30;    	  // no alternate function
  GPIO_PORTB_PUR_R &= ~0x30;         	// enable pullup resistor
  GPIO_PORTB_DEN_R |= 0x30;         	// enable digital I/O
}

// Sets the LED output to the value in data
void LED_Out(unsigned long data)
{
	LED = (data << 4);
}
