from intcode import *
from itertools import permutations


def create_computer(instr, i):
    comp = Computer(instr[:], len(instr))
    comp.receive(i)
    return comp

def solve(instr, ranger):
    res = 0
    for perm in permutations(ranger):
        computers = [create_computer(instr, perm[i]) for i in range(5)]
        output = 0
        while all(c.state != State.Halted for c in computers):
            for c in computers:
                output = c.receive_run_output(output)
        res = max(res, output)
    return res


instructions = read_intcode_input(7)

print(solve(instructions, range(5)))
print(solve(instructions, range(5, 10)))
