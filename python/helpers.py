# heavily inspired by the stuff from Peter Norvig


cat = ''.join

def read_input(filename, datatype=str, sep='\n'):
    filename = f"{filename:02d}" if isinstance(filename, int) else filename
    with open(f"inputs/{filename}.txt") as f:
        contents = f.read().strip().split(sep)
        return list(map(datatype, contents))

def digits(filename):
    return list(map(int, read_input(filename)[0]))

def count(iterable, pred=bool):
    return sum(1 for item in iterable if pred(item))

def first(iterable, default=None):
    return next(iter(iterable), default)

def filter_first(iterable, pred):
    return first(el for el in iterable if pred(el))

def sign(n):
    if n > 0: return 1
    elif n < 0: return -1
    else: return 0

def line_print(lines):
    for line in lines:
        print(cat(line))
