from helpers import read_input, cat, filter_first


H = 6
W = 25


def part1(layers):
    least_zero = min(layers, key=lambda x: x.count('0'))
    return least_zero.count('1') * least_zero.count('2')

def part2(layers):
    return ['#' if filter_first(pixel, lambda x: x != '2') == '1' else ' '
            for pixel in zip(*layers)]

def pretty_print(image):
    for i in range(0, H*W, W):
        print(cat(image[i:i+W]))


instr = cat(read_input(8))
layers = [instr[i:i+H*W] for i in range(0, len(instr), H*W)]

print(part1(layers))
pretty_print(part2(layers))
