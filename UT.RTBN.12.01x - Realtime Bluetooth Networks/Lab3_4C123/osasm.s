;/*****************************************************************************/
; OSasm.s: low-level OS commands, written in assembly                       */
; Runs on LM4F120/TM4C123/MSP432
; Lab 3 starter file
; March 2, 2016




        AREA |.text|, CODE, READONLY, ALIGN=2
        THUMB
        REQUIRE8
        PRESERVE8

        EXTERN  RunPt            ; currently running thread
        EXPORT  StartOS
        EXPORT  SysTick_Handler
        IMPORT  Scheduler


SysTick_Handler                ; 1) Saves R0-R3,R12,LR,PC,PSR
    CPSID   I                  ; Prevent interrupt during switch
    PUSH    {R4-R11}           ; Save remaining regs r4-11
    LDR     R0, =RunPt         ; R0 = pointer to RunPt, old thread
    LDR     R1, [R0]           ; R1 = RunPt
    STR     SP, [R1]           ; Save SP into TCB
    PUSH    {R0, LR}           ; Save LR
    BL      Scheduler          ; RunPt = next thread
    POP     {R0, LR}           ; Restore LR
    LDR     R1, [R0]           ; R1 = RunPt, new thread
    LDR     SP, [R1]           ; new thread SP; SP = RunPt->sp;
    POP     {R4-R11}           ; restore regs r4-11
    CPSIE   I                  ; tasks run with interrupts enabled
    BX      LR                 ; restore R0-R3,R12,LR,PC,PSR

StartOS
    LDR     R0, =RunPt         ; currently running thread
    LDR     R2, [R0]           ; R2 = value of RunPt
    LDR     SP, [R2]           ; new thread SP; SP = RunPt->stackPointer;
    POP     {R4-R11}           ; restore regs r4-11
    POP     {R0-R3}            ; restore regs r0-3
    POP     {R12}
    ADD     SP,SP,#4           ; discard LR from initial stack
    POP     {LR}               ; start location
    ADD     SP,SP,#4           ; discard PSR
    CPSIE   I                  ; Enable interrupts at processor level
    BX      LR                 ; start first thread

    ALIGN
    END
