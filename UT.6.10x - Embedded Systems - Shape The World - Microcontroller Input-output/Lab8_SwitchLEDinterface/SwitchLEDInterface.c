// ***** 0. Documentation Section *****
// SwitchLEDInterface.c for Lab 8
// Runs on LM4F120/TM4C123
// Use simple programming structures in C to toggle an LED
// while a button is pressed and turn the LED on when the
// button is released.  This lab requires external hardware
// to be wired to the LaunchPad using the prototyping board.
// January 15, 2016
//      Jon Valvano and Ramesh Yerraballi

// ***** 1. Pre-processor Directives Section *****
#include "TExaS.h"
#include "tm4c123gh6pm.h"

// Constant declarations to access PORTE pins
#define SWITCH (*((volatile unsigned long *)0x40024004))	// SWITCH is on PE0
#define LED    (*((volatile unsigned long *)0x40024008))  // LED    is on PE1

// ***** 2. Global Declarations Section *****

// FUNCTION PROTOTYPES: Each subroutine defined
void PortE_Init(void);        	// Initialize PORTE
void Delay(unsigned long msec); // Wait msec milliseconds
int Switch_Pressed(void);				// Tell whether the SWITCH is pressed or not
void Toggle_LED(void);					// Toggles LED
void DisableInterrupts(void); 	// Disable interrupts
void EnableInterrupts(void);  	// Enable interrupts

// ***** 3. Subroutines Section *****

// PE0, PB0, or PA2 connected to positive logic momentary switch using 10k ohm pull down resistor
// PE1, PB1, or PA3 connected to positive logic LED through 470 ohm current limiting resistor
// To avoid damaging your hardware, ensure that your circuits match the schematic
// shown in Lab8_artist.sch (PCB Artist schematic file) or 
// Lab8_artist.pdf (compatible with many various readers like Adobe Acrobat).
int main(void){ 
//**********************************************************************
// The following version tests input on PE0 and output on PE1
//**********************************************************************
  TExaS_Init(SW_PIN_PE0, LED_PIN_PE1, ScopeOn);  // activate grader and set system clock to 80 MHz
	PortE_Init();
  EnableInterrupts();           // enable interrupts for the grader
	
	// switch LED on
	LED = 0x02;
	
  while(1){
    // wait 100ms
		Delay(100);
		
		if(Switch_Pressed()){
			Toggle_LED();
		}
		else{
			LED = 0x02;
		}
  }
}

// Initialize PORTE with PE0 as input and PE1 as output
void PortE_Init(void){
	volatile unsigned long delay;
	SYSCTL_RCGC2_R |= 0x00000010;      // 1) F clock
  delay = SYSCTL_RCGC2_R;            // delay to allow clock to stabilize     
  GPIO_PORTE_AMSEL_R &= 0x00;        // 2) disable analog function
  GPIO_PORTE_PCTL_R &= 0x00000000;   // 3) GPIO clear bit PCTL  
  GPIO_PORTE_DIR_R &= ~0x01;         // 4.1) PE0 input,
  GPIO_PORTE_DIR_R |= 0x02;          // 4.2) PE1 output
  GPIO_PORTE_AFSEL_R &= 0x00;        // 5) no alternate function
  GPIO_PORTE_PUR_R &= ~0x01;         // 6) disable pullup resistor on PE0
  GPIO_PORTE_DEN_R |= 0x03;          // 7) enable digital pins PE0,PE1
}

// Subroutine to delay in units of milliseconds
// Inputs:  Number of milliseconds to delay
// Outputs: None
// Notes:   assumes 80 MHz clock
void Delay(unsigned long msec){
	unsigned long i;
  while(msec > 0) {
    i = 13333;  // this number means 1ms
    while(i > 0){
      i = i - 1;
    }
    msec = msec - 1; // decrements every 1 ms
  }
}

int Switch_Pressed(void){
	return (SWITCH == 0x01);
}

void Toggle_LED(void){
	LED ^= 0x02;
}
