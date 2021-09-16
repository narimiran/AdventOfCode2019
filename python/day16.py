from helpers import digits, cat
from itertools import accumulate


def digit_calc(signal, n):
    m = n+1
    s = sum(sum(signal[d:d+m]) - sum(signal[d+2*m:d+3*m])
            for d in range(n, len(signal), 4*m))
    return abs(s) % 10

def phase(signal):
    return [digit_calc(signal, n) for n in range(len(signal))]

def fft(signal, offset=0):
    for _ in range(100):
        signal = phase(signal)
    return cat(map(str, signal[:8]))

def part2(signal):
    offset = int(cat(map(str, signal[:7])))
    signal = (10000 * signal)[offset:]
    signal.reverse()
    cumsum = lambda x, y: (x + y) % 10
    for _ in range(100):
        signal = accumulate(signal, cumsum)
    signal = list(signal)[::-1]
    return cat(map(str, signal[:8]))


signal = digits(16)

print(fft(signal))
print(part2(signal))
