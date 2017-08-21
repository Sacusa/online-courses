#include "..//tm4c123gh6pm.h"
#include "Switch.h"

// Initializes switches on PE0 and PE1
void Switch_Init(void)
{
	volatile unsigned long delay;
	SYSCTL_RCGC2_R |= 0x00000010;			// activate clock for Port E
  delay = SYSCTL_RCGC2_R;       		// allow time for clock to stabilize
  GPIO_PORTE_AMSEL_R &= ~0x03;      // disable analog function
  GPIO_PORTE_PCTL_R &= ~0x00000003;	// GPIO clear bit PCTL  
  GPIO_PORTE_DIR_R &= ~0x03;        // PE0,PE1 input
  GPIO_PORTE_AFSEL_R &= ~0x03;      // no alternate function
  GPIO_PORTE_DEN_R |= 0x03;         // enable digital I/O
}

// Returns the current value of PE1 and PE0 as a long value, in that order.
unsigned long Switch_In(void)
{
	return (GPIO_PORTE_DATA_R & 0x03);
}
