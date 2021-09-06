from helpers import read_input
from collections import defaultdict


def count_ancestors(k):
    if k not in orbits: return 0
    return 1 + count_ancestors(orbits[k])

def part2(orbits):
    def ancestors(k, ances=set()):
        if k not in orbits: return ances
        return ancestors(p := orbits[k], ances | {p})
    return len(ancestors('YOU') ^ ancestors('SAN'))


instructions = (l.split(')') for l in read_input(6))
orbits = {kid: parent for parent, kid in instructions}

print(sum(map(count_ancestors, orbits)))
print(part2(orbits))
