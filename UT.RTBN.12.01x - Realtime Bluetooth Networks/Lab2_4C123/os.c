// os.c
// Runs on LM4F120/TM4C123/MSP432
// Lab 2 starter file.
// Daniel Valvano
// February 20, 2016

#include <stdint.h>
#include "os.h"
#include "../inc/CortexM.h"
#include "../inc/BSP.h"

// function definitions in osasm.s
void StartOS(void);


tcbType tcbs[NUMTHREADS];
tcbType *RunPt;
int32_t Stacks[NUMTHREADS][STACKSIZE];
uint32_t time;
void(*eventThread1)(void);
void(*eventThread2)(void);
uint32_t eventPeriods[2];


// ******** OS_Init ************
// Initialize operating system, disable interrupts
// Initialize OS controlled I/O: systick, bus clock as fast as possible
// Initialize OS global variables
// Inputs:  none
// Outputs: none
void OS_Init(void){
  DisableInterrupts();
  time = 0;
  BSP_Clock_InitFastest();  // set processor clock to fastest speed
}

void SetInitialStack(int i){
  tcbs[i].sp = &Stacks[i][STACKSIZE-16]; // thread stack pointer
  Stacks[i][STACKSIZE-1] = 0x01000000;   // thumb bit
  Stacks[i][STACKSIZE-3] = 0x14141414;   // R14
  Stacks[i][STACKSIZE-4] = 0x12121212;   // R12
  Stacks[i][STACKSIZE-5] = 0x03030303;   // R3
  Stacks[i][STACKSIZE-6] = 0x02020202;   // R2
  Stacks[i][STACKSIZE-7] = 0x01010101;   // R1
  Stacks[i][STACKSIZE-8] = 0x00000000;   // R0
  Stacks[i][STACKSIZE-9] = 0x11111111;   // R11
  Stacks[i][STACKSIZE-10] = 0x10101010;  // R10
  Stacks[i][STACKSIZE-11] = 0x09090909;  // R9
  Stacks[i][STACKSIZE-12] = 0x08080808;  // R8
  Stacks[i][STACKSIZE-13] = 0x07070707;  // R7
  Stacks[i][STACKSIZE-14] = 0x06060606;  // R6
  Stacks[i][STACKSIZE-15] = 0x05050505;  // R5
  Stacks[i][STACKSIZE-16] = 0x04040404;  // R4
}

//******** OS_AddThreads ***************
// Add four main threads to the scheduler
// Inputs: function pointers to four void/void main threads
// Outputs: 1 if successful, 0 if this thread can not be added
// This function will only be called once, after OS_Init and before OS_Launch
int OS_AddThreads(void(*thread0)(void),
                  void(*thread1)(void),
                  void(*thread2)(void),
                  void(*thread3)(void)){
  int32_t status = StartCritical();
  
  // initialize stacks and linked list
  for (int i = 0; i < 4; ++i) {
    SetInitialStack(i);
    tcbs[i].next = &tcbs[(i + 1) % 4];
  }
  
  // initialize PCs
  Stacks[0][STACKSIZE-2] = (int32_t)thread0;
  Stacks[1][STACKSIZE-2] = (int32_t)thread1;
  Stacks[2][STACKSIZE-2] = (int32_t)thread2;
  Stacks[3][STACKSIZE-2] = (int32_t)thread3;
  
  // set initial thread
  RunPt = &tcbs[0];

  EndCritical(status);
  return 1;
}

//******** OS_AddThreads3 ***************
// add three foregound threads to the scheduler
// This is needed during debugging and not part of final solution
// Inputs: three pointers to a void/void foreground tasks
// Outputs: 1 if successful, 0 if this thread can not be added
int OS_AddThreads3(void(*task0)(void),
                 void(*task1)(void),
                 void(*task2)(void)){ 
  // initialize stacks and linked list
  for (int i = 0; i < 3; ++i) {
    SetInitialStack(i);
    tcbs[i].next = &tcbs[(i + 1) % 3];
  }
  
  // initialize PCs
  Stacks[0][STACKSIZE-2] = (int32_t)task0;
  Stacks[1][STACKSIZE-2] = (int32_t)task1;
  Stacks[2][STACKSIZE-2] = (int32_t)task2;
  
  // set initial thread
  RunPt = &tcbs[0];

  return 1;
}
                 
//******** OS_AddPeriodicEventThreads ***************
// Add two background periodic event threads
// Typically this function receives the highest priority
// Inputs: pointers to a void/void event thread function2
//         periods given in units of OS_Launch (Lab 2 this will be msec)
// Outputs: 1 if successful, 0 if this thread cannot be added
// It is assumed that the event threads will run to completion and return
// It is assumed the time to run these event threads is short compared to 1 msec
// These threads cannot spin, block, loop, sleep, or kill
// These threads can call OS_Signal
int OS_AddPeriodicEventThreads(void(*thread1)(void), uint32_t period1,
                               void(*thread2)(void), uint32_t period2){
  eventThread1 = thread1;
  eventThread2 = thread2;
  eventPeriods[0] = period1;
  eventPeriods[1] = period2;
  
  return 1;
}

//******** OS_Launch ***************
// Start the scheduler, enable interrupts
// Inputs: number of clock cycles for each time slice
// Outputs: none (does not return)
// Errors: theTimeSlice must be less than 16,777,216
void OS_Launch(uint32_t theTimeSlice){
  STCTRL = 0;                  // disable SysTick during setup
  STCURRENT = 0;               // any write to CURRENT clears it
  SYSPRI3 =(SYSPRI3&0x00FFFFFF)|0xE0000000; // priority 7
  STRELOAD = theTimeSlice - 1; // reload value
  STCTRL = 0x00000007;         // enable, core clock and interrupt arm
  StartOS();                   // start on the first task
}

// runs every ms
void Scheduler(void){
  ++time;
  
  if ((time % eventPeriods[0]) == 0) {
    eventThread1();
  }
  if ((time % eventPeriods[1]) == 0) {
    eventThread2();
  }
  
  RunPt = RunPt->next;
}

// ******** OS_InitSemaphore ************
// Initialize counting semaphore
// Inputs:  pointer to a semaphore
//          initial value of semaphore
// Outputs: none
void OS_InitSemaphore(int32_t *semaPt, int32_t value){
  *semaPt = value;
}

// ******** OS_Wait ************
// Decrement semaphore
// Lab2 spinlock (does not suspend while spinning)
// Lab3 block if less than zero
// Inputs:  pointer to a counting semaphore
// Outputs: none
void OS_Wait(int32_t *semaPt){
  while (*semaPt <= 0);
  *semaPt -= 1;
}

// ******** OS_Signal ************
// Increment semaphore
// Lab2 spinlock
// Lab3 wakeup blocked thread if appropriate
// Inputs:  pointer to a counting semaphore
// Outputs: none
void OS_Signal(int32_t *semaPt){
  *semaPt += 1;
}


uint32_t MailBox;           // mailbox for communication
uint32_t MailBoxSemaphore;  // binary semaphore for synchronization

// ******** OS_MailBox_Init ************
// Initialize communication channel
// Producer is an event thread, consumer is a main thread
// Inputs:  none
// Outputs: none
void OS_MailBox_Init(void){
  MailBox = 0;
  MailBoxSemaphore = 0;
}

// ******** OS_MailBox_Send ************
// Enter data into the MailBox, do not spin/block if full
// Use semaphore to synchronize with OS_MailBox_Recv
// Inputs:  data to be sent
// Outputs: none
// Errors: data lost if MailBox already has data
void OS_MailBox_Send(uint32_t data){
  MailBox = data;
  MailBoxSemaphore = 1;
}

// ******** OS_MailBox_Recv ************
// retreive mail from the MailBox
// Use semaphore to synchronize with OS_MailBox_Send
// Lab 2 spin on semaphore if mailbox empty
// Lab 3 block on semaphore if mailbox empty
// Inputs:  none
// Outputs: data retreived
// Errors:  none
uint32_t OS_MailBox_Recv(void){
  uint32_t data;
  
  while (MailBoxSemaphore == 0);
  data = MailBox;
  MailBoxSemaphore = 0;
  
  return data;
}


