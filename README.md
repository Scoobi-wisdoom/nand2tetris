# Overview
This README is about notes on nand2tetris courses from [coursera](https://www.coursera.org/learn/build-a-computer).

Also projects in the current directory are assignments of the courses.

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

# Week 3
Memory logic is sequential or clock-based while computer's processing chips are on combinational logic.

Combinatorial: out[t] = function(in[t])
Sequential: state[t] = function(state[t-1])

Physical time is continuous but computer's time is not but discrete. To deal with this difference a time unit (clock) is used. Discrete time steps are used to ensure the system state is stabilized. This is done by both of ignoring a signal from the beginning of the time unit and considering the later state of the time unit.

The state is either 1 or 0. Gates that can flip between two states are called Flip-Flops.

The comparison between DFF and Bit chips is as follows.
>DFF always stores the “in” bit, while Bit only stores it if “load” is set to 1. DFF can store information for one time unit only, while Bit can store it for many cycles.

The atomic element of a memory is a register, which consists of many bit chips. Also, register's state means the value currently stored in the register.

Registers are used to build a RAM.
>At any given point of time, only one register in the RAM is selected.
>Width is the amount of data a single register holds, address is the location of the register within a larger chip.

The reason why RAM is named as "Random Access Memory" is because however many do registers exist inside the memory, the access time is always the same.

A counter is an implementation of an abstraction to track instructions for the next fetch and execution.
