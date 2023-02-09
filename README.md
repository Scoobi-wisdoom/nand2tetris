# Overview
This README is about notes on nand2tetris courses from [coursera](https://www.coursera.org/learn/build-a-computer).

Also projects in the current directory are assignments of the courses.

# Struggles
project 2: ALU.hdl  
project 3: Bit.hdl, RAM8.hdl, PC.hdl  
project 4: Fill.asm

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

The state is either 1 or 0. Gates that can flip between two states are called Flip-Flops. A flip flop gate is a device of an abstraction to store information over time.

The comparison between DFF and Bit chips is as follows. Also, keep that in mind that a bit chip is called as a 1-bit register.
>DFF always stores the “in” bit, while Bit only stores it if “load” is set to 1. DFF can store information for one time unit only, while Bit can store it for many cycles.

The DFF is a bottom building block of all the memory chips such as registers, RAMs, counters, etc. in the computer. In the computer, all of those DFFs are connected to the single clock, which enables the computer to do synchronization.

The atomic element of a memory is a register, which consists of many bit chips. Also, register's state means the value currently stored in the register.

Registers are used to build a RAM. All chips built directly or indirectly from DFFs are time dependent.
>At any given point of time, only one register in the RAM is selected.
>Width is the amount of data a single register holds, address is the location of the register within a larger chip.

The reason why RAM is named as "Random Access Memory" is because however many do registers exist inside the memory, the access time is always the same. How is this possible? It is because even though registers are sequential, access to them through address is combinational. The addresses do not belong to the chip hardware but are a kind of a logic gate.

A counter is an implementation of an abstraction to track instructions for the next fetch and execution.

# Week 4
>Machine language is specification of the hardware/software interface.

There are three types of machine operations such as arithmetic operations, logical operations and flow control. Be aware of that a machine language does not necessarily have the same set of operations or data types (64 bit width, floating point and so on) with others do.

Memory Hierarchy was introduced by Von Neumann to cope with expensive cost of accessing memories caused by both of supplying a long address and fetching values in the memory into the CPU.

Memory Hierarchy is about a tradeoff between distance from the CPU and size of memories. The closest to the CPU and the smallest registers could perform according to addressing modes such as register, direct, indirect, immediate ones.

Since there are an enormous number of inputs and outputs, the CPU needs a protocol to comprehend them so that which type of inputs or outputs are accessible in which address of the memory.

When it comes to Flow Control, machine instructions are executed by the CPU in sequence, if not "jump."

The Hack computer to be built in this course consists of instruction memories (ROM), CPU, and data memories (RAM) with instruction bus, data bus and address buses. Also, the Hack computer has three kinds of registers such as D, A and M registers.

>The Hack program is a sequence of instructions written in the Hack machine language.

The Hack computer is controlled by using the reset button to load the ROM with a Hack program.
>The reset button is only used once per program.

Ram has a screen memory map for output and keyboard memory map for input.

Be aware that a computer never ends executing a program when turned on, which means that even when there is no instruction, i.e. nope instruction, a computer executes that instruction. A hacker may hide a malicious instruction after a number of lines of nope instructions. To prevent this, you are recommended to end the instruction by an infinite loop to take control.

There are some examples of the contract of instructions in the Hack program.
> - A reference to a symbol that has no corresponding label declaration is treated as a reference to a variable.
> - Variables are allocated to the RAM from address 16 onward.
> - Variables that store memory addresses, like **arr** and **i**, are called *pointers*.

# References
-  Noam Nisan, Shimon Schocken. (2021) The Elements of Computing Systems (2nd ed.). The MIT Press
