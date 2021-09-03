from helpers import read_input


directions = {'U': 1j, 'D': -1j, 'L': -1, 'R': 1}
visited = {}
intersections = {}


def visit(p, dist):
    visited[p] = dist

def find_intersections(p, dist):
    if v := visited.get(p):
        intersections[p] = v + dist

def follow_wire(wire, func):
    p = dist = 0
    for w in wire:
        amount = int(w[1:])
        d = directions[w[0]]
        for _ in range(amount):
            func(p := p+d, dist := dist+1)


wire_a, wire_b = (w.split(',') for w in read_input(3))
follow_wire(wire_a, visit)
follow_wire(wire_b, find_intersections)

manhattan = lambda p: int(abs(p.real) + abs(p.imag))

print(min(map(manhattan, intersections)))
print(min(intersections.values()))
