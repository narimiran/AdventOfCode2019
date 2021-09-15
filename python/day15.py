from intcode import *
from copy import deepcopy

def solve(c, part):
    seen = set()
    dirs = [-1j, 1j, -1, 1]
    pos = 0j
    steps = 0
    queue = deque([(steps, pos, c)])
    while queue:
        steps, pos, c = queue.popleft()
        c.run()
        for i in range(4):
            npos = pos + dirs[i]
            if npos not in seen:
                nsteps = steps+1
                seen.add(npos)
                nc = deepcopy(c)
                nc.receive(i+1)
                nc.run()
                status = nc.get_from_out_queue()
                if status == 1:
                    queue.append((nsteps, npos, nc))
                elif status == 2 and part == 1:
                    return nsteps, nc
    return steps


c = Computer(read_intcode_input(15))
steps, comp = solve(c, 1)
flood = solve(comp, 2)

print(steps)
print(flood)
