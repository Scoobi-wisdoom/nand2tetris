# Overview
This README is about notes on nand2tetris courses from [coursera - part 1](https://www.coursera.org/learn/build-a-computer) and [coursera - part 2](https://www.coursera.org/learn/nand2tetris2).

Also projects in the current directory are assignments of the courses.

# Struggles
project 2: ALU.hdl  
project 3: Bit.hdl, RAM8.hdl, PC.hdl  
project 4: Fill.asm  
project 5: CPU.hdl   
project 7
# Part 1
## Week 1
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

## Week 2
ALU -> CPU
binary number to decimal representation
If a binary arithmetic operation exceeds the word size, the hardware just ignores the exceeding parts, which makes the binary different from human expectations.

Half Adder, Full Adder, Multi-bit Adder exist

Above only positive integers are concerned. How about negative integers? 2^n -x is the key, by representing negative by the bit in the most left. You may wonder why one bit of all bits is not used to represent either positive or negative, 0 for positive while 1 for negative, for example. This is not a good idea since you do not want to encounter -0 vs +0 problem.

Arithmetic Logic Unit
ALU computes a function, and this function is pre-determined for arithmetic and logical operations.
In ALU, there are two inputs, and control bits (zx, nx, zy, ny, f, no; zr, ng).

## Week 3
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

## Week 4
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

## Week 5
There are three kinds of information flows in computer: control, address and data.
>The ALU loads information from the Data bus and manipulates it using the Control bits.

<!-- This may not be true. -->
Fetch means putting the next instruction inside the address of a memory and then reading that location with exploitation of Program Counter.

ALU does calculations and at the same time it sends the result to D, A and M registers. This does not mean that all of those three receive the result. The receipt is decided by the instruction's *destination bits*.

>Program Counter emits the address of the next instruction.

There are two kinds of computers when it comes to purpose: general-purpose and single-purpose ones. The general-purpose one is a computer meant to execute various programs. For examples, personal computers and cell phones. The single-purpose computer has a single program. For example, embedded computers in cameras, cars etc. You should be aware that those two kinds of computers have the same architecture in foundation as follows.
>stored programs, fetch-decode-execute logic, CPU, registers, and counters.

Computers have either a single address space or two separate address spaces to store data and instructions. The latter is called Harvard architecture which is used in embedded computers. 

When using a single memory space there is a clash between fetch and execute cycle, because the fetch cycle reads from the program memory while the execute cycle reads from the data memory and those two cycles share the same memory.

## Week 6
> The Assembler is the first softwaere layer above the hardware.

> Machine languages are typically specified in two flavors: *binary* and *symbolic*.

Each aseembly command is mapped to integer numbers of binary form.

Assembler symbols except predefined ones, i.e. labels and variables, must be replaced with addresses in RAM. Variables are assigned to the first next empty cell of RAM which is to be looked up later. Labels are defined so that the program always knows which should be executed next time. Now there is a challenge. What if a label has not been defined yet, but the jump should be executed towards it? There are two ways of handling this complication. One, you have two mapping tables, one of which is the real mapping table and the other is a temporary one. Two, at first you complete reading the instruction only to focusing on labels and variables. And then actually execute the commands. Keep in mind that this problem doese not occur with predefined symbols.

The assembler does not generate binary code for label declarations in an instruction. So the labels are also called *pseudo-instructions*.
# Part 2
# Week 1
A compiler converts high-level program into low-level code. The problem is that there are too many types of computers or devices in which the same high-level program have different low-level code. This problem is solved by a principle "write once run anywhere." According to this principle, a compiler generates VM code to run on an abstract virtual machine and this VM code is translated (compiled further) into a machine language. 

Java is a good example of this principle. Java compiler makes Java bytecode (i.e. VM code) from Java program. Then a JVM implementation known as JRE (i.e. translator) converts VM code into machine language suitable to the computer or device. Of course, however, at some point the VM abstraction must be implemented on a specific hardware platform. For this, there are implementation guidelines published by VM architects which are called as *standard mappings*.
   
The translator should meet two objectives: first, the translated code should be close to high-level code so that the differences are manageable and second, the same to low-level. Stack Machine is a result of getting balance between those two objectives and this is an abstract architecture on which VM language has base.

Stack machine works with memory segments using `pop` and `push`. The purpose of memory segments is to preserve role semantics of variables in high-level code. There are eight memoery segments such as *argument*, *local*, *static*, *constant*, *this*, *that*, *pointer*, and *temp*. Notice that there is no symbolic variables here. VM only knows references not symbolic variables.

Among memory segments, *argument*, *local*, *this*, and *that* have its pointer which contains the base address of its segment. *Constant* segment only exists virtually, in other words, it does not occupy any physical RAM space. *Static* segment is also different from other segments since it is handled as symbolic variables. *Temp* segment is for compiler's own use. *Pointer* segment is to track down *this* and *that* base addresses.   

Stack machine necessarily has garbage on its stack memory which should be recycled. Except the garbage, all other stack memory addresses are inplay.

# projects done in different repositories
- [assembler](https://github.com/Scoobi-wisdoom/assembler)
- [VMTranslator](https://github.com/Scoobi-wisdoom/VMTranslator)

# References
-  Noam Nisan, Shimon Schocken. (2021) The Elements of Computing Systems (2nd ed.). The MIT Press
