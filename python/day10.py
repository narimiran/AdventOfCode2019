from helpers import read_input
from math import atan2
from collections import defaultdict


def get_asteroid_positions(instr):
    return [(col, row)
            for row, line in enumerate(instr)
            for col, c in enumerate(line)
            if c == '#']

rel_dist = lambda x1, y1, x2, y2: (x2-x1, y2-y1)
angle = lambda dx, dy: atan2(dx, dy) # switched order

def find_best_position(asteroids):
    best = (0, (0, 0))
    for a in asteroids:
        seen_angles = {angle(*rel_dist(*a, *b))
                       for b in asteroids if b != a}
        if (cnt := len(seen_angles)) > best[0]:
            best = (cnt, a)
    return best

def relative_positions(asteroids, station):
    rels = defaultdict(set)
    for a in asteroids:
        rel_pos = rel_dist(*station, *a)
        phi = angle(*rel_pos)
        rels[phi].add(rel_pos)
    return [v for k, v in sorted(rels.items(), reverse=True)]

def find_200th(station):
    vapor_order = relative_positions(asteroids, station)
    th = vapor_order[199].pop()
    x, y = map(sum, zip(station, th))
    return 100*x + y


instr = read_input(10)
asteroids = get_asteroid_positions(instr)
amount, position = find_best_position(asteroids)

print(amount)
print(find_200th(position))
