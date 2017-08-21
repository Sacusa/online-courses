// ***** 0. Documentation Section *****
// TableTrafficLight.c for Lab 10
// Runs on LM4F120/TM4C123
// Index implementation of a Moore finite state machine to operate a traffic light.  
// Daniel Valvano, Jonathan Valvano
// January 15, 2016

// east/west red light connected to PB5
// east/west yellow light connected to PB4
// east/west green light connected to PB3
// north/south facing red light connected to PB2
// north/south facing yellow light connected to PB1
// north/south facing green light connected to PB0
// pedestrian detector connected to PE2 (1=pedestrian present)
// north/south car detector connected to PE1 (1=car present)
// east/west car detector connected to PE0 (1=car present)
// "walk" light connected to PF3 (built-in green LED)
// "don't walk" light connected to PF1 (built-in red LED)

// ***** 1. Pre-processor Directives Section *****
#include <stdint.h>
#include "TExaS.h"
#include "tm4c123gh6pm.h"
#define NVIC_ST_CTRL_R      (*((volatile unsigned long *)0xE000E010))
#define NVIC_ST_RELOAD_R    (*((volatile unsigned long *)0xE000E014))
#define NVIC_ST_CURRENT_R   (*((volatile unsigned long *)0xE000E018))

#define West_Light       (*((volatile unsigned long *)0x400050E0))
#define South_Light      (*((volatile unsigned long *)0x4000501C))
#define Ped_Light        (*((volatile unsigned long *)0x40025028))
#define Traffic_Detector (*((volatile unsigned long *)0x4002401C))

struct TrafficLight
{
	uint32_t delay;		// delay in ms
	uint8_t output;		// encodes as PB5-0,PF3,PF0
	uint8_t next[8];
};

#define LIGHT_INTERVAL 50
#define GoSouth        0
#define PrepareSouth   1
#define GoWest         2
#define PrepareWest    3
#define GoPed          4
#define StopPedOn1     5
#define StopPedOff1    6
#define StopPedOn2     7
#define StopPedOff2    8
#define StopPedOn3     9
#define StopPedOff3    10
#define AllStop        11

const struct TrafficLight Traffic[12] = {
	{LIGHT_INTERVAL, 0x32, {GoSouth, PrepareSouth, PrepareSouth, AllStop, GoSouth, AllStop, AllStop, PrepareSouth}},
	{LIGHT_INTERVAL, 0x52, {PrepareSouth, GoPed, GoWest, AllStop, GoSouth, AllStop, AllStop, GoWest}},
	{LIGHT_INTERVAL, 0x86, {GoWest, PrepareWest, GoWest, AllStop, PrepareWest, AllStop, AllStop, PrepareWest}},
	{LIGHT_INTERVAL, 0x8A, {PrepareWest, GoPed, GoWest, AllStop, GoSouth, AllStop, AllStop, GoPed}},
	{LIGHT_INTERVAL, 0x91, {GoPed, StopPedOn1, StopPedOn1, AllStop, StopPedOn1, AllStop, AllStop, StopPedOn1}},
	{LIGHT_INTERVAL, 0x92, {StopPedOn1, StopPedOff1, StopPedOff1, AllStop, StopPedOff1, AllStop, AllStop, StopPedOff1}},
	{LIGHT_INTERVAL, 0x90, {StopPedOff1, StopPedOn2, StopPedOn2, AllStop, StopPedOn2, AllStop, AllStop, StopPedOn2}},
	{LIGHT_INTERVAL, 0x92, {StopPedOn2, StopPedOff2, StopPedOff2, AllStop, StopPedOff2, AllStop, AllStop, StopPedOff2}},
	{LIGHT_INTERVAL, 0x90, {StopPedOff2, StopPedOn3, StopPedOn3, AllStop, StopPedOn3, AllStop, AllStop, StopPedOn3}},
	{LIGHT_INTERVAL, 0x92, {StopPedOn3, StopPedOff3, StopPedOff3, AllStop, StopPedOff3, AllStop, AllStop, StopPedOff3}},
	{LIGHT_INTERVAL, 0x90, {StopPedOff3, GoPed, GoWest, AllStop, GoSouth, AllStop, AllStop, GoSouth}},
	{LIGHT_INTERVAL, 0x92, {AllStop, GoPed, GoWest, AllStop, GoSouth, AllStop, AllStop, GoSouth}}
};

// ***** 2. Global Declarations Section *****

// FUNCTION PROTOTYPES: Each subroutine defined
void DisableInterrupts(void); // Disable interrupts
void EnableInterrupts(void);  // Enable interrupts
void PortB_Init(void);
void PortE_Init(void);
void PortF_Init(void);
void SysTick_Init(void);
void SysTick_Wait(unsigned long delay);
void SysTick_Wait10ms(unsigned long delay);
void Output_Traffic(uint8_t Current_State);
uint8_t Input_Sensor(void);

// ***** 3. Subroutines Section *****

int main(void)
{
  TExaS_Init(SW_PIN_PE210, LED_PIN_PB543210, ScopeOff); // activate grader and set system clock to 80 MHz
	SysTick_Init();
  EnableInterrupts();
	PortB_Init();
	PortE_Init();
	PortF_Init();
	
	uint8_t Current_State = AllStop;
	
  while(1) {
		// output LEDs
		Output_Traffic(Current_State);
		
    // wait 500ms
		SysTick_Wait10ms(Traffic[Current_State].delay);
		
		// input from sensors
		Current_State = Traffic[Current_State].next[Input_Sensor()];
  }
}

void PortB_Init(void)
{
	volatile unsigned long delay;
	SYSCTL_RCGC2_R |= 0x00000002;      // B clock
  delay = SYSCTL_RCGC2_R;            // delay to allow clock to stabilize     
  GPIO_PORTB_AMSEL_R = 0x00;         // disable analog function
  GPIO_PORTB_PCTL_R = 0x00000000;    // GPIO clear bit PCTL  
  GPIO_PORTB_DIR_R = 0x3F;           // PB0-5 output
  GPIO_PORTB_AFSEL_R = 0x00;         // no alternate function
  GPIO_PORTB_PUR_R = ~0x3F;          // disable pullup resistor on PB0-5
  GPIO_PORTB_DEN_R = 0x3F;           // enable digital pins PB0-5
}

void PortE_Init(void)
{
	volatile unsigned long delay;
	SYSCTL_RCGC2_R |= 0x00000010;      // E clock
  delay = SYSCTL_RCGC2_R;            // delay to allow clock to stabilize     
  GPIO_PORTE_AMSEL_R = 0x00;         // disable analog function
  GPIO_PORTE_PCTL_R = 0x00000000;    // GPIO clear bit PCTL  
  GPIO_PORTE_DIR_R = ~0x07;          // PE0-2 input
  GPIO_PORTE_AFSEL_R = 0x00;         // no alternate function
  GPIO_PORTE_PUR_R = ~0x07;          // disable pullup resistor on PE0-3
  GPIO_PORTE_DEN_R = 0x07;           // enable digital pins PE0-3
}

void PortF_Init(void)
{
	volatile unsigned long delay;
  SYSCTL_RCGC2_R |= 0x00000020;     // F clock
  delay = SYSCTL_RCGC2_R;           // delay
	GPIO_PORTF_LOCK_R = 0x4C4F434B;   // unlock PortF
  GPIO_PORTF_CR_R = 0x0A;           // allow changes to PF1,PF3
  GPIO_PORTF_AMSEL_R = 0x00;        // disable analog function
  GPIO_PORTF_PCTL_R = 0x00000000;   // GPIO clear bit PCTL
  GPIO_PORTF_DIR_R = 0x0A;          // PF1,PF3 output
  GPIO_PORTF_AFSEL_R = 0x00;        // no alternate function
  GPIO_PORTF_DEN_R = 0x0A;          // enable digital pins PF1,PF3
}

void SysTick_Init(void)
{
  NVIC_ST_CTRL_R = 0;              // disable SysTick during setup
  NVIC_ST_CTRL_R = 0x00000005;     // enable SysTick with core clock
}

void SysTick_Wait(unsigned long delay)
{
  NVIC_ST_RELOAD_R = delay-1;  // number of counts to wait
  NVIC_ST_CURRENT_R = 0;       // any value written to CURRENT clears
  while((NVIC_ST_CTRL_R&0x00010000) == 0) {} // wait for count flag
}

void SysTick_Wait10ms(unsigned long delay)
{
  unsigned long i;
  for(i=0; i<delay; i++){
    SysTick_Wait(800000);  // wait 10ms
  }
}

void Output_Traffic(uint8_t Current_State)
{
	// output south LEDs
	South_Light = (Traffic[Current_State].output & 0xE0) >> 5;
	
	// output west LEDs
	West_Light = (Traffic[Current_State].output & 0x1C) << 1;
	
	// output pedestrian LEDs
	Ped_Light = ((Traffic[Current_State].output & 0x01) << 3)
					  +  (Traffic[Current_State].output & 0x02);
}

uint8_t Input_Sensor(void)
{
	return (((Traffic_Detector & 0x3) << 1) + ((Traffic_Detector & 0x4) >> 2));
}
