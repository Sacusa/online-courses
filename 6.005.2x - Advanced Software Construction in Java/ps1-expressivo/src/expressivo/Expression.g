/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */

// grammar Expression;

/*
 *
 * You should make sure you have one rule that describes the entire input.
 * This is the "start rule". Below, "root" is the start rule.
 *
 * For more information, see the parsers reading.
 */
root ::= sum;
@skip whitespace{
	sum ::= product ('+' product)*;
	product ::= primitive ('*' primitive)*;
	primitive ::= number | variable | '(' sum ')' | '(' product ')';
}
number ::= ([0-9]+ ('.' [0-9]+)?) | ('.' [0-9]+);
variable ::= [a-zA-Z]+;

whitespace ::= [ ]+;
