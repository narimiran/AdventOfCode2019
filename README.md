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
