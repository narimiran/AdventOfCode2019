from intcode import *


def setup_and_run(instr, vals):
    comp = Computer(instr, len(instr))
    comp.set_positions(vals)
    comp.run()
    return comp.read_ram(0)

def part1(instr):
    return setup_and_run(instr, [(1, 12), (2, 2)])

def part2(instr):
    OUTPUT = 19690720
    for noun in range(100):
        result = setup_and_run(instr, [(1, noun)])
        verb = OUTPUT - result
        if verb < 100:
            return 100*noun + verb


instructions = read_intcode_input(2)

print(part1(instructions))
print(part2(instructions))
