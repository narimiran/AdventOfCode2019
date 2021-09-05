from intcode import *


def solve(instr, part):
    input_instruction = 1 if part == 1 else 5
    comp = Computer(instr, len(instr))
    comp.receive(input_instruction)
    comp.run()
    return comp.out_queue[-1]


instructions = read_intcode_input(5)

print(solve(instructions, 1))
print(solve(instructions, 2))
