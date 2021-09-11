from intcode import *
from collections import defaultdict
from helpers import cat


def paint(instr, initial):
    painted = defaultdict(int)
    pos = 0j
    painted[pos] = initial
    d = -1j
    turns = [-1j, 1j]
    comp = Computer(instr)
    while comp.state != State.Halted:
        color = comp.receive_run_output(painted[pos])
        turn = turns[comp.get_from_out_queue()]
        painted[pos] = color
        d *= turn
        pos += d
    return painted

def part1(instr):
    return len(paint(instr, 0))

def part2(instr):
    painted = paint(instr, 1)
    registration = [40 * [' '] for _ in range(6)]
    for p in painted:
        if painted[p]:
            registration[int(p.imag)][int(p.real)] = '#'
    return registration

def pretty_print(registration):
    for row in registration:
        print(cat(row))


instr = read_intcode_input(11)

print(part1(instr))
pretty_print(part2(instr))
