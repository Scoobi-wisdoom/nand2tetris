# Week 1
Computer systems are about abstractions and implementations.
Upon abstractions one is allowed to build something while not taking care of the actual implementation, and that something built already would also be taken into account as an abstraction in turn.

Two big abstractions: Hardware Platform and Software Hierarchy.

Boolean functions are represented by an expression consisting of AND, OR, and NOT operations. But OR is replaced with AND and NOT, since (x OR y) = NOT(NOT(x) AND NOT(y)). So when you do boolean operations, you only need AND and NOT.

NAND is another boolean operation to simplify boolean functions even more. The definition of NAND is below:
(x NAND y) = NOT(x AND y)

All boolean functions are represented by NAND, since
NOT(x) = (x NAND x),
(x AND y) = NOT(x NAND y)

Gate logic refers to a technique for implementing Boolean functions using logic gates. Logic gates are simple chips for functionalities. Those functionalities are categorized into two of elementary (Nand, And, Or, Not, ...) and composite (Mux, Adder, ...).

Hardware simulation is done through three of interactive, script-based and with/without output and compare files simulation.


Multi-bit are arrays of bits.
If A is a 16-bit bus, then A[0] is the right-most bit, and A[15] is the left-most bit.

# Week 2
ALU -> CPU
binary number to decimal representation
If a binary arithmetic operation exceeds the word size, the hardware just ignores the exceeding parts, which makes the binary different from human expectations.

Half Adder, Full Adder, Multi-bit Adder exist

Above only positive integers are concerned. How about negative integers? 2^n -x is the key, by representing negative by the bit in the most left. You may wonder why one bit of all bits is not used to represent either positive or negative, 0 for positive while 1 for negative, for example. This is not a good idea since you do not want to encounter -0 vs +0 problem.

Arithmetic Logic Unit
ALU computes a function, and this function is pre-determined for arithmetic and logical operations.
In ALU, there are two inputs, and control bits (zx, nx, zy, ny, f, no; zr, ng).
