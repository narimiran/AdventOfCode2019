from enum import Enum
from collections import deque
import operator as ops
from helpers import read_input


class State(Enum):
    Running = 1
    Waiting = 2
    Halted = 3


class Computer:
    def __init__(self, instructions, ram_size=4096):
        self.ram = self.initialize_memory(instructions, ram_size)
        self.ip = 0
        self.rp = 0
        self.state = State.Running
        self.in_queue = deque([])
        self.out_queue = deque([])

    def initialize_memory(self, instructions, ram_size):
        ram = [0 for _ in range(ram_size)]
        ram[:len(instructions)] = instructions[:]
        return ram

    def set_positions(self, values):
        for (pos, val) in values:
            self.ram[pos] = val

    def read_ram(self, pos):
        return self.ram[pos]

    def set_ram(self, pos, val):
        self.ram[pos] = val

    def read_param(self, param):
        digit = 10**(param+1)
        mode = (self.ram[self.ip] // digit) % 10
        pos = self.ip + param
        pos_val = self.ram[pos]
        return [pos_val, pos, self.rp+pos_val][mode]

    def receive(self, value):
        self.in_queue.append(value)

    def binary_operation(self, op):
        noun = self.read_param(1)
        verb = self.read_param(2)
        dest = self.read_param(3)
        case = {1: lambda: int(self.ram[noun] + self.ram[verb]),
                2: lambda: int(self.ram[noun] * self.ram[verb]),
                7: lambda: int(self.ram[noun] < self.ram[verb]),
                8: lambda: int(self.ram[noun] == self.ram[verb])}
        self.ram[dest] = case[op]()
        self.ip += 4

    def conditional_jumps(self, op):
        noun = self.read_param(1)
        verb = self.read_param(2)
        neq = ops.ne if op == 5 else ops.eq
        self.ip = self.ram[verb] if neq(self.ram[noun], 0) else self.ip+3

    def adjust_rp(self):
        noun = self.read_param(1)
        self.rp += self.ram[noun]
        self.ip += 2

    def read_from_in_queue(self):
        noun = self.read_param(1)
        if self.in_queue:
            self.ram[noun] = self.in_queue.popleft()
            self.state = State.Running
            self.ip += 2
        else:
            self.state = State.Waiting

    def put_to_out_queue(self):
        noun = self.read_param(1)
        self.out_queue.append(self.ram[noun])
        self.ip += 2

    def get_from_out_queue(self):
        return self.out_queue.popleft()

    def get_3_from_output(self):
        return (self.get_from_out_queue() for _ in range(3))

    def receive_run_output(self, value):
        self.receive(value)
        self.run()
        return self.get_from_out_queue()

    def execute_opcode(self):
        op = self.ram[self.ip] % 100
        if op in {1, 2, 7, 8}:
            self.binary_operation(op)
        elif op == 3:
            self.read_from_in_queue()
        elif op == 4:
            self.put_to_out_queue()
        elif op in {5, 6}:
            self.conditional_jumps(op)
        elif op == 9:
            self.adjust_rp()
        elif op == 99:
            self.state = State.Halted

    def run(self):
        self.state = State.Running
        while self.state == State.Running:
            self.execute_opcode()


def read_intcode_input(filename):
    return read_input(filename, int, ',')
