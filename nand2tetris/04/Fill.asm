// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, the
// program clears the screen, i.e. writes "white" in every pixel.

(PROGRAM_LOOP)
@SCREEN
D=A
@addr
M=D             // load the screen's base address
@8193
D=A
@i
M=D             // set i to number of rows (256)

@KBD
D=M;            // read from keyboard
@CLEAR_LOOP
D;JEQ
@FILL_LOOP
D;JNE

(CLEAR_LOOP)
@i
M=M-1
D=M
@PROGRAM_LOOP
D;JEQ           // exit if printing is complete

@addr
A=M
M=0            // fill the pixels at RAM[addr]

@addr
M=M+1           // update address

@CLEAR_LOOP
0;JEQ

(FILL_LOOP)
@i
M=M-1
D=M
@PROGRAM_LOOP
D;JEQ           // exit if printing is complete

@addr
A=M
M=-1            // fill the pixels at RAM[addr]

@addr
M=M+1           // update address

@FILL_LOOP
0;JEQ