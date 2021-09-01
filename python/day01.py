from helpers import read_input


required_fuel = lambda x: x//3 - 2

def total_required_fuel(x):
    tot = 0
    while True:
        x = required_fuel(x)
        if x <= 0: return tot
        tot += x

def solve(fuels, part):
    func = required_fuel if part == 1 else total_required_fuel
    return sum(func(fuel) for fuel in fuels)


fuels = read_input(1, int)

print(solve(fuels, 1))
print(solve(fuels, 2))
