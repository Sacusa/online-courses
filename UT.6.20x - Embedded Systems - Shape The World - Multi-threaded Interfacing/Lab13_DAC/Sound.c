// Sound.c
// Runs on LM4F120 or TM4C123, 
// edX lab 13 
// Use the SysTick timer to request interrupts at a particular period.
// Daniel Valvano, Jonathan Valvano
// December 29, 2014
// This routine calls the 4-bit DAC

#include "Sound.h"
#include "DAC.h"
#include "..//tm4c123gh6pm.h"

#define NUMBER_OF_NOTES    4
#define NUMBER_OF_SAMPLES 30

// Frequency order: C4, D4, E4, G4
const float noteFrequencies[NUMBER_OF_NOTES] = \
	{523.251, 587.330, 659.255, 783.991};
const unsigned long sineWave[NUMBER_OF_SAMPLES] = \
	{8, 9, 10, 11, 12, 13, 14, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 1, 2, 3, 4, 5, 6, 7};
unsigned long currentSample = 0;

void DisableInterrupts(void); // Disable interrupts
void EnableInterrupts(void);  // Enable interrupts

// **************Sound_Init*********************
// Initialize Systick periodic interrupts
// Also calls DAC_Init() to initialize DAC
// Input: none
// Output: none
void Sound_Init(void){
  DAC_Init();          					// Port B is DAC
  NVIC_ST_CTRL_R = 0;        		 	// disable SysTick during setup
  NVIC_ST_RELOAD_R = 181816;		  	// default value (440Hz sound)
  NVIC_ST_CURRENT_R = 0;      			// any write to current clears it
  NVIC_SYS_PRI3_R = (NVIC_SYS_PRI3_R&0x00FFFFFF)|0x20000000;  // priority 1
  NVIC_ST_CTRL_R = 0x0007; 				// enable, core clock, and interrupts
}

// **************Sound_Tone*********************
// Change Systick periodic interrupts to start sound output
// Input: interrupt period
//           Units of period are 12.5ns
//           Maximum is 2^24-1
//           Minimum is determined by length of ISR
// Output: none
void Sound_Tone(unsigned long period){
	// this routine sets the RELOAD and starts SysTick
	NVIC_ST_RELOAD_R = period - 1;	// set reload
	NVIC_ST_CURRENT_R = 0;			// re-start SysTick
}


// **************Sound_Play*********************
// Plays the sound of the given note
// Input: Note value, given as
//        0 for C
//        1 for D
//        2 for E
//        3 for G
// Output: none					
void Sound_Play(unsigned long Note){
	float frequency = noteFrequencies[Note];			// determine frequency of the note
	Sound_Tone(1000000000 / (frequency * 12.5 * 30));	// configure SysTick for the note
}


// **************Sound_Off*********************
// stop outputing to DAC
// Output: none
void Sound_Off(void){
	NVIC_ST_RELOAD_R = 0;	  // stop SysTick interrupts
	DAC_Out(0);							// 0 means no sound
	currentSample = 0;			// reset current sample
}


// Interrupt service routine
// Executed every 12.5ns*(period)
void SysTick_Handler(void){
	DAC_Out(sineWave[currentSample]);
	currentSample = (currentSample + 1) % NUMBER_OF_SAMPLES;
}
