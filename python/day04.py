from collections import Counter


LOW = 183564
HIGH = 657474

part_1 = part_2 = 0

for n in range(LOW, HIGH):
    s = str(n)
    if all(a <= b for a, b in zip(s, s[1:])):
        digit_counts = Counter(s).values()
        part_1 += any(c >= 2 for c in digit_counts)
        part_2 += any(c == 2 for c in digit_counts)

print(part_1)
print(part_2)
