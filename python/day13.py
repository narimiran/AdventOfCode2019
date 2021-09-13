from intcode import *
from helpers import sign


def part1(instr):
    comp = Computer(instr)
    comp.run()
    blocks = 0
    while comp.out_queue:
        _, _, tile = comp.get_3_from_output()
        blocks += tile == 2
    return blocks

def part2(instr):
    comp = Computer(instr)
    comp.set_ram(0, 2)
    score = 0
    ball_pos = 0
    paddle_pos = 0
    while comp.state != State.Halted:
        comp.run()
        while comp.out_queue:
            x, y, t = comp.get_3_from_output()
            if (x, y) == (-1, 0):
                score = t
            else:
                if t == 3:
                    paddle_pos = x
                elif t == 4:
                    ball_pos = x
        joystick = sign(ball_pos - paddle_pos)
        comp.receive(joystick)
    return score


instr = read_intcode_input(13)
print(part1(instr))
print(part2(instr))
