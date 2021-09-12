import re
from helpers import read_input, sign
from math import gcd
from functools import reduce


def step(pos, vel):
    for i in range(4):
        for j in range(4):
            vel[i] += sign(pos[j] - pos[i])
    for i in range(4):
        pos[i] += vel[i]

def simulate(positions):
    pos = list(positions)
    vel = [0] * 4
    for _ in range(1000):
        step(pos, vel)
    return pos + vel

def part1(coords):
    final = list(zip(*(simulate(coord) for coord in coords)))
    positions, velocities = final[:4], final[4:]
    energy = lambda x: sum(map(abs, x))
    return sum(energy(p) * energy(v) for p, v in zip(positions, velocities))

def find_zero_vel(positions):
    pos = list(positions)
    vel = [0] * 4
    n = 0
    while True:
        n += 1
        step(pos, vel)
        if not any(vel):
            return n

def part2(coords):
    periods = [2 * find_zero_vel(coord) for coord in coords]
    lcm = lambda a, b: a * b // gcd(a, b)
    return reduce(lcm, periods)


coords = list(zip(*(map(int, re.findall(r'(-*\d+)', line)) for line in read_input(12))))

print(part1(coords))
print(part2(coords))
