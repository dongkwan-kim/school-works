/* 
 * CS:APP Data Lab 
 * 
 * <Please put your name and userid here>
 * 
 * bits.c - Source file with your solutions to the Lab.
 *          This is the file you will hand in to your instructor.
 *
 * WARNING: Do not include the <stdio.h> header; it confuses the dlc
 * compiler. You can still use printf for debugging without including
 * <stdio.h>, although you might get a compiler warning. In general,
 * it's not good practice to ignore compiler warnings, but in this
 * case it's OK.  
 */

#if 0
/*
 * Instructions to Students:
 *
 * STEP 1: Read the following instructions carefully.
 */

You will provide your solution to the Data Lab by
editing the collection of functions in this source file.

INTEGER CODING RULES:
 
  Replace the "return" statement in each function with one
  or more lines of C code that implements the function. Your code 
  must conform to the following style:
 
  int Funct(arg1, arg2, ...) {
      /* brief description of how your implementation works */
      int var1 = Expr1;
      ...
      int varM = ExprM;

      varJ = ExprJ;
      ...
      varN = ExprN;
      return ExprR;
  }

  Each "Expr" is an expression using ONLY the following:
  1. Integer constants 0 through 255 (0xFF), inclusive. You are
      not allowed to use big constants such as 0xffffffff.
  2. Function arguments and local variables (no global variables).
  3. Unary integer operations ! ~
  4. Binary integer operations & ^ | + << >>
    
  Some of the problems restrict the set of allowed operators even further.
  Each "Expr" may consist of multiple operators. You are not restricted to
  one operator per line.

  You are expressly forbidden to:
  1. Use any control constructs such as if, do, while, for, switch, etc.
  2. Define or use any macros.
  3. Define any additional functions in this file.
  4. Call any functions.
  5. Use any other operations, such as &&, ||, -, or ?:
  6. Use any form of casting.
  7. Use any data type other than int.  This implies that you
     cannot use arrays, structs, or unions.

 
  You may assume that your machine:
  1. Uses 2s complement, 32-bit representations of integers.
  2. Performs right shifts arithmetically.
  3. Has unpredictable behavior when shifting an integer by more
     than the word size.

EXAMPLES OF ACCEPTABLE CODING STYLE:
  /*
   * pow2plus1 - returns 2^x + 1, where 0 <= x <= 31
   */
  int pow2plus1(int x) {
     /* exploit ability of shifts to compute powers of 2 */
     return (1 << x) + 1;
  }

  /*
   * pow2plus4 - returns 2^x + 4, where 0 <= x <= 31
   */
  int pow2plus4(int x) {
     /* exploit ability of shifts to compute powers of 2 */
     int result = (1 << x);
     result += 4;
     return result;
  }

FLOATING POINT CODING RULES

For the problems that require you to implent floating-point operations,
the coding rules are less strict.  You are allowed to use looping and
conditional control.  You are allowed to use both ints and unsigneds.
You can use arbitrary integer and unsigned constants.

You are expressly forbidden to:
  1. Define or use any macros.
  2. Define any additional functions in this file.
  3. Call any functions.
  4. Use any form of casting.
  5. Use any data type other than int or unsigned.  This means that you
     cannot use arrays, structs, or unions.
  6. Use any floating point data types, operations, or constants.


NOTES:
  1. Use the dlc (data lab checker) compiler (described in the handout) to 
     check the legality of your solutions.
  2. Each function has a maximum number of operators (! ~ & ^ | + << >>)
     that you are allowed to use for your implementation of the function. 
     The max operator count is checked by dlc. Note that '=' is not 
     counted; you may use as many of these as you want without penalty.
  3. Use the btest test harness to check your functions for correctness.
  4. Use the BDD checker to formally verify your functions
  5. The maximum number of ops for each function is given in the
     header comment for each function. If there are any inconsistencies 
     between the maximum ops in the writeup and in this file, consider
     this file the authoritative source.

/*
 * STEP 2: Modify the following functions according the coding rules.
 * 
 *   IMPORTANT. TO AVOID GRADING SURPRISES:
 *   1. Use the dlc compiler to check that your solutions conform
 *      to the coding rules.
 *   2. Use the BDD checker to formally verify that your solutions produce 
 *      the correct answers.
 */


#endif
/* 
 * bitNor - ~(x|y) using only ~ and & 
 *   Example: bitNor(0x6, 0x5) = 0xFFFFFFF8
 *   Legal ops: ~ &
 *   Max ops: 8
 *   Rating: 1
 */
int bitNor(int x, int y) {
	return (~x) & (~y);
}

/* 
 * copyLSB - set all bits of result to least significant bit of x
 *   Example: copyLSB(5) = 0xFFFFFFFF, copyLSB(6) = 0x00000000
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 5
 *   Rating: 2
 */
int copyLSB(int x) {
	return ((x<<31)>>31);
}

/* 
 * isEqual - return 1 if x == y, and 0 otherwise 
 *   Examples: isEqual(5,5) = 1, isEqual(4,5) = 0
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 5
 *   Rating: 2
 */
int isEqual(int x, int y) {
	return !(x^y);
}

/* 
 * bitMask - Generate a mask consisting of all 1's 
 *   lowbit and highbit
 *   Examples: bitMask(5,3) = 0x38
 *   Assume 0 <= lowbit <= 31, and 0 <= highbit <= 31
 *   If lowbit > highbit, then mask should be all 0's
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 16
 *   Rating: 3
 */
int bitMask(int highbit, int lowbit) {

	int sign = ~((highbit + (~lowbit + 1)) >> 31);
	/*  sign of (highbit - lowbit)  */

	int shift = ((~0) << (lowbit)) ^ (((~0) << (highbit)) << 1);
	/*		((~0) << (lowbit)) == 111...1(lowbit=1)0...000
			(((~0) << (highbit)) << 1) == 111...1(highbit=0)0...000    */

	return sign&shift;
}
/*
 * bitCount - returns count of number of 1's in word
 *   Examples: bitCount(5) = 2, bitCount(7) = 3
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 40
 *   Rating: 4
 */
int bitCount(int x) {

	int mask = 17 + (17 << 8) + (17 << 16) + (17 << 24);
	/*  0x11111111  */
	int rSum;
	
	/* Save the number of bits in 4n+1 bit of pSum */
	int pSum = x&mask;
	x = x >> 1;
	pSum += x&mask;
	x = x >> 1;
	pSum += x&mask;
	x = x >> 1;
	pSum += x&mask;
	
	/* 15 = 0xF */
	/* Save the sum of bits in rSum */
	rSum = pSum & 15;
	pSum = pSum >> 4;
	rSum += pSum & 15;
	pSum = pSum >> 4;
	rSum += pSum & 15;
	pSum = pSum >> 4;
	rSum += pSum & 15;
	pSum = pSum >> 4;
	rSum += pSum & 15;
	pSum = pSum >> 4;
	rSum += pSum & 15;
	pSum = pSum >> 4;
	rSum += pSum & 15;
	pSum = pSum >> 4;
	rSum += pSum & 15;

	return rSum;
}

/* 
 * TMax - return maximum two's complement integer 
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 4
 *   Rating: 1
 */
int tmax(void) {
  return ~(1<<31);
  /*  0x80000000  */
}

/* 
 * isNonNegative - return 1 if x >= 0, return 0 otherwise 
 *   Example: isNonNegative(-1) = 0.  isNonNegative(0) = 1.
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 6
 *   Rating: 3
 */
int isNonNegative(int x) {
	return !(x >> 31);
}

/* 
 * addOK - Determine if can compute x+y without overflow
 *   Example: addOK(0x80000000,0x80000000) = 0,
 *            addOK(0x80000000,0x70000000) = 1, 
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 20
 *   Rating: 3
 */
int addOK(int x, int y) {

	int sum = x + y;

	int xSgn = x >> 31;
	int ySgn = y >> 31;
	int sSgn = sum >> 31;
	/*		if xSgn != ySgn, there is not an overflow 
			+ if xSgn != sSgn, there is an overflow      */

	return !!((xSgn^ySgn) | (!(xSgn^sSgn)));


}

/* 
 * rempwr2 - Compute x%(2^n), for 0 <= n <= 30
 *   Negative arguments should yield negative remainders
 *   Examples: rempwr2(15,2) = 3, rempwr2(-35,3) = -3
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 20
 *   Rating: 3
 */
int rempwr2(int x, int n) {

	int sign = (x >> 31);
	int posV = x&((~0) + (1 << n));
	/* bits from 0 to n == general positive remainder(GPR) */

	int isposVnzero = ~(((!posV) << 31) >> 31);
	/* if posV != 0 -> 0xFFFFFFFF
	   if posV == 0 -> 0x00000000	*/

	return  posV + ((isposVnzero&sign)&(~(1 << n) + 1));
	/* if posV != 0 && sign == 1 -> remainder = GPR - 2^n
	   else -> remainder = GPR								*/
}

/* 
 * isLess - if x < y  then return 1, else return 0 
 *   Example: isLess(4,5) = 1.
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 24
 *   Rating: 3
 */
int isLess(int x, int y) {

	int xSgn = x >> 31;
	int ySgn = y >> 31;
	int mSgn = (x + (~y + 1)) >> 31;
	/* sign of x - y */

	int cond = xSgn^ySgn;
	return !!(cond&xSgn) + !!((~cond)&mSgn);
	/* if xSgn == ySgn -> cond == 0 -> mSgn
	   if xSgn != ySgn -> cond != 0 -> xSgn */
}

/* 
 * absVal - absolute value of x
 *   Example: absVal(-1) = 1.
 *   You may assume -TMax <= x <= TMax
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 10
 *   Rating: 4
 */
int absVal(int x) {

	int sign = (x >> 31);
	return (sign&(~x + 1)) + ((~sign)&(x));
	/* if sign == 1 -> -x
	   if sign == 0 -> x	*/
}

/*
 * isPower2 - returns 1 if x is a power of 2, and 0 otherwise
 *   Examples: isPower2(5) = 0, isPower2(8) = 1, isPower2(0) = 0
 *   Note that no negative number is a power of 2.
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 20
 *   Rating: 4
 */
int isPower2(int x) {

	int sign = x >> 31;

	int Fplusx = (~0) + (x);
	int plusV = Fplusx + x;
	int xorV = Fplusx^x;

	return (!!x)&(~sign)&(!(plusV^xorV));
	/* sign == 1 -> return 0
	   x == 0 -> return 0
	   plusV == xorV -> there is only one 1*/
}

/* 
 * float_neg - Return bit-level equivalent of expression -f for
 *   floating point argument f.
 *   Both the argument and result are passed as unsigned int's, but
 *   they are to be interpreted as the bit-level representations of
 *   single-precision floating point values.
 *   When argument is NaN, return argument.
 *   Legal ops: Any integer/unsigned operations incl. ||, &&. also if, while
 *   Max ops: 10
 *   Rating: 2
 */
unsigned float_neg(unsigned uf) {

	unsigned exp = uf & (0x7F800000);
	unsigned frac = uf & (0x7FFFFF);

	/*NaN*/
	if ((!(exp ^ 0x7F800000) && (!!frac))){
		return uf;
	}

	return uf ^ (0x80000000);
}

/* 
 * float_half - Return bit-level equivalent of expression 0.5*f for
 *   floating point argument f.
 *   Both the argument and result are passed as unsigned int's, but
 *   they are to be interpreted as the bit-level representation of
 *   single-precision floating point values.
 *   When argument is NaN, return argument
 *   Legal ops: Any integer/unsigned operations incl. ||, &&. also if, while
 *   Max ops: 30
 *   Rating: 4
 */
unsigned float_half(unsigned uf) {

	unsigned sgn = (uf >> 31) << 31;

	/*0xFF000000 = 2s complement of 1*/
	unsigned half_nor = (((uf << 1) + (0xFF000000)) >> 1) | sgn;

	unsigned exp = uf & (0x7F800000);
	unsigned frac = uf & (0x7FFFFF);

	/*if last two bits == 11(2) -> rounding */
	unsigned frac_round = ((!((frac & 0x3) ^ (0x3))) + frac);
	unsigned half_denor = (frac_round >> 1) | sgn;

	/* +0 or -0 */
	if (!(uf << 1)){
		return uf;
	}

	/* NaN */
	if (!(exp ^ 0x7F800000)){
		return uf;
	}

	/* exp = 0, denormalized */
	if (!exp){
		return half_denor;
	}

	/* exp = 1, wiil be denormalized */
	if (!(exp ^ 0x800000)){
		return half_denor + 0x400000;
	}

	/* normalized */
	return half_nor;
}



/* 
 * float_i2f - Return bit-level equivalent of expression (float) x
 *   Result is returned as unsigned int, but
 *   it is to be interpreted as the bit-level representation of a
 *   single-precision floating point values.
 *   Legal ops: Any integer/unsigned operations incl. ||, &&. also if, while
 *   Max ops: 30
 *   Rating: 4
 */
unsigned float_i2f(int x) {

	int norFrac, expBias, bitMarker;
	unsigned tmpX;
	int tmpR, r25_, r24, r23, round;

	int sgn = x & (0x80000000);

	/* Consider positive number */
	if (x<0){
		x = -x;
	}

	/* bitMarker = highest 1 bit of x */
	tmpX = x;
	bitMarker = 0;
	while ((tmpX = tmpX >> 1)){
		bitMarker = bitMarker + 1;
	}

	/* (int)x == 0 -> (float)x == 0 */
	if (x == 0x0){
		return 0;
	}

	expBias = (bitMarker + 127) << 23;

	if (23 >= bitMarker){
		norFrac = (x << (23 - bitMarker))&(0x7FFFFF);
	} else{
		/* if bitMarker > 23 -> rounding
		   because frac is 23 bits		*/
		tmpR = bitMarker - 24;
		r25_ = x&((1 << tmpR) - 1);
		r23 = x&(1 << (tmpR + 1));
		r24 = x&(1 << tmpR);
		round = r24 && (r23 || r25_);
		/* r25_ : masking from 0 to (bitMarker - 25)
		   r23 : masking (bitMarker - 23)
		   r24: masking (bitMarker - 24)			*/
		/* (r24 && r23) or (r24 && r25_) -> up rounding  */

		norFrac = (((x >> (bitMarker - 23)))&(0x7FFFFF)) + round;
	}

	return sgn | (norFrac + expBias);
}
