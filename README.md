# Advent of Code 2019

All my Advent of Code repos:

* [AoC 2015 in Nim](https://github.com/narimiran/advent_of_code_2015)
* [AoC 2016 in Python](https://github.com/narimiran/advent_of_code_2016)
* [AoC 2017 in Nim, OCaml, Python](https://github.com/narimiran/AdventOfCode2017)
* [AoC 2018 in Nim](https://github.com/narimiran/AdventOfCode2018)
* [AoC 2019 in OCaml](https://github.com/narimiran/AdventOfCode2019) (this repo)


&nbsp;


## Solutions

My first time solving AoC puzzles in OCaml from the get-go.
(I've used OCaml for [AoC 2017](https://github.com/narimiran/AdventOfCode2017),
but only after I've solved all tasks with other languages first -- as a preparation for this year.)



### Day 1

[The Tyranny of the Rocket Equation](http://adventofcode.com/2019/day/1) || [day01.ml](ocaml/day01.ml) || Runtime: 0.6 ms

We have two slightly different functions (`f`) for each part, so counting the total boils down to:
```ocaml
List.fold_left (fun acc x -> acc + f x) 0
```


### Day 2

[1202 Program Alarm](http://adventofcode.com/2019/day/2) || [day02.ml](ocaml/day02.ml) || Runtime: 0.6 ms

Our inputs are such that there is no need to iterate through all possible `verb`s,
we can always leave `verb` at zero and later on calculate it from the difference between the desired and given output.
```ocaml
let result = intcode |> set_up noun |> run_until_halt in
let verb = output - result in
if verb < 100 then
  100 * noun + verb
```


### Day 3

[Crossed Wires](http://adventofcode.com/2019/day/3) || [day03.ml](ocaml/day03.ml) || Runtime: 50 ms

Initial idea was to create a `Map` for every wire and then to find intersections via `Map.merge`:
```ocaml
let path_a = follow wire_a
let path_b = follow wire_b

let intersections = find_intersections path_a path_b

let () =
  intersections |> find closest;
  intersections |> find shortest
```
Although elegant, it was very inefficient. (Runtime around 180 ms)

Current solution treats wires differently:
The first wire is used to populate a `Hashtbl` with all visited points as keys and the number of steps taken as values.
The second wire is used only to check for intersections.
This gives 3.5x performance boost.
```ocaml
let () =
  wire_a |> visit_all_points;
  wire_b |> find_intersections;

  intersections |> find closest;
  intersections |> find shortest
```


### Day 4

[Secure Container](http://adventofcode.com/2019/day/4) || [day04.ml](ocaml/day04.ml) || Runtime: 0.6 ms

The initial solution iterated through the whole range between `low` and `high`,
converting each number to `String` or `OSeq` (I've tried both versions):
```ocaml
let solve ~part =
  let f = if part = 1 then ( >= ) else ( = ) in
  let res = ref 0 in
  for i = low to high do
    let sq = String.to_seq (string_of_int i) in
    if not_decr sq then
      let groups = sq |> OSeq.group ~eq:Char.equal in
      if OSeq.exists (fun g -> f (OSeq.length g) 2) groups then incr res
  done;
  !res
```
This had 1.3 **b**illion instructions and its runtime was around 130 ms.

Current solution is much uglier: it has six for-loops to iterate on each digit,
where lower bound for each digit is dependant on the digit before it.
Also, we iterate over potential candidates only once, and test for both parts:
```ocaml
let digit_groups = [ a; b; c; d; e; f ] |> group_lengths in
if digit_groups |> has_multiples then incr part_1;
if digit_groups |> has_duplicates then incr part_2
```
where `group_lengths` is a specialised and optimized version of `CCList.group_succ`
just for this task:
No unnecessary creations of lists of groups and `List.rev`,
we're interested only in number of members of each group of the same digit:
```ocaml
let group_lengths l =
  let rec aux acc cur amnt l =
    match cur, l with
    | _, [] -> amnt :: acc
    | y, x :: tl when Int.equal x y -> aux acc x (amnt+1) tl
    | _, x :: tl -> aux (amnt :: acc) x 1 tl
  in
  aux [] 0 0 l
```

The result of changing the algorithm and applying these micro-optimisations?
900k instructions (1500 times less than original!) and its runtime is 0.6 ms.
If it is ugly but it works... :)

Bonus: the main function looks like `>>=`.
