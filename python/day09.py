from intcode import *


solve = lambda instr, part: Computer(instr).receive_run_output(part)

instructions = read_intcode_input(9)

print(solve(instructions, 1))
print(solve(instructions, 2))
