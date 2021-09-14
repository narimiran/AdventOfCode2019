from collections import namedtuple, defaultdict, deque
from math import ceil
from helpers import read_input


Chemical = namedtuple('Chemical', ['amount', 'name'])
Equation = namedtuple('Equation', ['produced', 'ingredients'])


def parse_line(line):
    to_chemical = lambda x: Chemical(int(x[0]), x[1])
    ins, out = line.split(" => ")
    ins = frozenset(map(to_chemical, map(str.split, ins.split(', '))))
    out = to_chemical(out.split())
    return (out.name, Equation(out.amount, ins))

def needed_ore(equations, amount=1):
    surpluses = defaultdict(int)
    queue = deque([Chemical(amount, "FUEL")])
    res = 0
    while queue:
        amount, name = queue.popleft()
        surplus = surpluses[name]
        missing = amount - surplus
        if name == 'ORE':
            res += missing
        else:
            produced, ingredients = equations[name]
            needed_reactions = ceil(missing / produced)
            surplus = needed_reactions * produced - missing
            surpluses[name] = surplus
            for chem in ingredients:
                queue.append(Chemical(needed_reactions * chem.amount, chem.name))
    return res

def part2(equations, start):
    TRILLION = 1_000_000_000_000
    lo = TRILLION // start
    hi = 2 * lo
    while lo < hi - 1:
        mid = (lo + hi) // 2
        ore = needed_ore(equations, mid)
        if ore < TRILLION:
            lo = mid
        else:
            hi = mid
    return mid



equations = dict(parse_line(line) for line in read_input(14))
ore_for_one_fuel = needed_ore(equations)

print(ore_for_one_fuel)
print(part2(equations, ore_for_one_fuel))
