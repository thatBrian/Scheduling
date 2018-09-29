# CPU Scheduling
### This project simulates CPU scheduling in order to see how the time required depends on the scheduling algorithm and request patterns.

## How to compile?
__javac Scheduling.java__

## How does it work?
This program takes in 1 argument
1. input file name

*A process is characterized in the program by just four non-negative integers, A, B, C, and IO. A is arrival time and C is total CPU time needed. B is the max CPU burst time while IO is the max I/O burst times. Both are used to generate uniformly distributed random integers from 0 to the max burst times.*

This program prints for each process:
1. A,B,C, and IO
2. Finishing time.
3. Turnaround time.
4. I/O time.
5. Waiting time.

The program then prints the following summary data.
1. Finishing time(For all processes)
2. CPU Utilization(%)
3. I/O Utilization(%)
4. Throughput, expressed in processes completed per hundred units.
5. Average turn around time.
6. Average Waiting time.



## Example

__./java Scheduling "input file"__
