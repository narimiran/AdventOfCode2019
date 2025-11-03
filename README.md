# Advent of Code 2019

All my Advent of Code repos:

* [AoC 2015 in Nim, Python](https://github.com/narimiran/advent_of_code_2015)
* [AoC 2016 in Python, Clojure (+ visualizations)](https://github.com/narimiran/advent_of_code_2016)
* [AoC 2017 in Nim, OCaml, Python](https://github.com/narimiran/AdventOfCode2017)
* [AoC 2018 in Nim, Python, Racket](https://github.com/narimiran/AdventOfCode2018)
* [AoC 2019 in OCaml, Python, Clojure](https://github.com/narimiran/AdventOfCode2019) (this repo)
* [AoC 2020 in Nim, one liner-y Python, Racket](https://github.com/narimiran/AdventOfCode2020)
* [AoC 2021 in Python, Racket](https://github.com/narimiran/AdventOfCode2021)
* [AoC 2022 in Python, Clojure](https://github.com/narimiran/AdventOfCode2022)
* [AoC 2023 in Clojure](https://github.com/narimiran/AdventOfCode2023)
* [AoC 2024 in Clojure (Clerk notebooks), Python, Elixir](https://github.com/narimiran/aoc2024)


&nbsp;


## Solutions, highlights and thoughts

My first time solving AoC puzzles in OCaml from the get-go.
(I've used OCaml for [AoC 2017](https://github.com/narimiran/AdventOfCode2017),
but only after I've solved all tasks with other languages first -- as a preparation for this year.)

To keep this readme at a reasonable vertical size when rendered,
highlights and thoughts for each day are hidden behind the "details" flag.
Click on it if you want to read my ramblings.

----

[Python solutions](python/) were added at a much later date.

And [Clojure solutions](clojure/) were added at even "mucher" later date, in October 2025, when I
decided to chase the missing stars on the way to all 500 stars in first ten years of AoC.\
The Clojure solutions contain all IntCode tasks (both OCaml and Python solution are missing some tasks), plus some more of not-already-solved tasks.


----



### Day 1

[The Tyranny of the Rocket Equation](http://adventofcode.com/2019/day/1) || [day01.ml](ocaml/day01.ml) || Runtime: 0.6 ms

<details>

We have two slightly different functions (`f`) for each part, so counting the total boils down to:
```ocaml
List.fold_left (fun acc x -> acc + f x) 0
```

</details>



### Day 2

[1202 Program Alarm](http://adventofcode.com/2019/day/2) || [day02.ml](ocaml/day02.ml) || Runtime: 0.9 ms

<details>

Our inputs are such that there is no need to iterate through all possible `verb`s,
we can always leave `verb` at zero and later on calculate it from the difference between the desired and given output.
```ocaml
let result = intcode |> set_up [(1, noun)] |> run in
let verb = output - result in
if verb < 100 then
  100 * noun + verb
```

</details>



### Day 3

[Crossed Wires](http://adventofcode.com/2019/day/3) || [day03.ml](ocaml/day03.ml) || Runtime: 50 ms

<details>

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

</details>



### Day 4

[Secure Container](http://adventofcode.com/2019/day/4) || [day04.ml](ocaml/day04.ml) || Runtime: 0.6 ms

<details>

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

</details>



### Day 5

[Sunny with a Chance of Asteroids](http://adventofcode.com/2019/day/5) || [day05.ml](ocaml/day05.ml) || Runtime: 0.7 ms

<details>

Below are the notes from the original version of `day05.ml`, together with the bug that
had bit me later on Day 9 (`let dest = a.(ip+3)`).
After Day 9 was released, the common logic from days 2, 5, 7 and 9 was extracted to
`intcode` module, and the solutions for those days were vastly simplified.
(See ["Intcode int'mezzo"](#intcode-intmezzo) below for more details.)

----

After you finally manage to read and understand what the instructions want from you,
the task becomes quite straight-forward.
Not counting the warm-up task on Day 1, I would say this was the easiest one so far:
Take Day 2, add new opcodes, change some details, and you're done.

Our "intcode computer" is starting to evolve and soon enough (but not yet)
these things should be probably put in a separate module which will be used by multiple tasks.

The initial solution had 8 separate branches for 8 separate opcodes.
Simple and straightforward, but lots of unnecessary duplication:
I've noticed that I can group together opcodes 1&2, 5&6, and 7&8 —
the only difference between them was the function/operation involved,
so the logical thing to do was to define custom operator for each group:
```ocaml
let ( +|* ) = if op = 1 then ( + ) else ( * ) in
a.(dest) <- n +|* v

let ( <>|= ) = if op = 5 then ( <> ) else ( = ) in
if n <>|= 0 then v else ip+3

let ( <|= ) = if op = 7 then ( < ) else ( = ) in
a.(dest) <- if n <|= v then 1 else 0
```

Operation deduplication half way done.
Groups 1&2 and 7&8 still had a lot of things in common
(they both read two parameters, have the same destination location (`ip+3`), do the same jump (`ip+4`))
so in the end they were put in the same branch to cut duplicated stuff some more:
```ocaml
let noun = read_param 1 in
match a.(ip) mod 100 with
| 1 | 2 | 7 | 8 as op ->
  let verb = read_param 2 in
  let dest = a.(ip+3) in
  a.(dest) <-
    (match op with
     | 1 -> noun + verb
     | 2 -> noun * verb
     | 7 -> CCBool.to_int (noun < verb)
     | 8 -> CCBool.to_int (noun = verb)
     | _ -> failwith "ocaml, you silly");
  ip+4
```

Yes I've removed some of the custom operators defined above, so some duplication is reintroduced.
I find it more readable this way.

</details>



### Day 6

[Universal Orbit Map](http://adventofcode.com/2019/day/6) || [day06.ml](ocaml/day06.ml) || Runtime: 3 ms

<details>

Ungh! Quest for my future self:
can you understand all the functions your previous self has written here?

We go through the input and create two `Map`s, one (used for the first part of the task)
containing `parent -> children` relationships (called `p2c`),
and the other containing `kid -> parent` relationships (called `k2p`) for the second part.

Part 1 is the recursive traversal through `"COM"`'s children, their children,
their children's children, ... counting the total distance to `"COM"`:
```ocaml
let rec traverse n key =
  match children with
  | [] -> n
  | _ ->
    let children_distances = List.map (traverse (n+1)) children in
    n + List.fold_left (+) 0 children_distances
```

The second part first builds the list of all ancestors for `"YOU"` and `"SAN"`:
```ocaml
let rec traverse relations acc = function
  | "COM" -> acc
  | kid ->
    let parent = relations |> RelationMap.find kid in
    traverse relations (parent::acc) parent
```

Both of those lists start with `"COM"` and all the common ancestors for both `"YOU"` and `"SAN"`.
We need to remove those, and what remains is the answer for the second part:
```ocaml
let rec calc_orbital_transfers you san =
  match you, san with
  | x::xs, y::ys when x = y -> calc_orbital_transfers xs ys
  | _, _ -> List.length you + List.length san
```

</details>



### Intcode int'mezzo

[intcode.ml](ocaml/lib/intcode.ml)

<details>

From Day 5 notes:

> Our "intcode computer" is starting to evolve and soon enough (but not yet)
> these things should be probably put in a separate module which will be used
> by multiple tasks.

The refactoring time has come.

When Day 7 was released, I wasn't at home so I couldn't solve it at that time.
I've read the task and realised that my current implementation from Day 5 won't fit for it,
I would need to refactor it so the state between runs remains preserved.

In the mean time, Day 9 was released, and with it our "intcode computer" implementation
is complete.
A perfect time to extract all the useful functions in a separate module.

Our computer can be in three states:
1. running - executing instructions until one of two things happen:
2. waiting - computer's input queue is empty and it can't continue until it receives an input
3. halted - computer has reached intcode 99

The computer is now represented as a `record`:
```ocaml
type state = Running | Waiting | Halted

type computer = {
  ram : int array;
  ip : int;
  rp : int;
  state : state;
  in_queue : int Queue.t;
  out_queue : int Queue.t;
}
```
where `ip` and `rp` are instruction and relative pointers, respectively, and
`in_queue` and `out_queue` are FIFO queues.

Computer initialization supports specifying arbitrary RAM size (with the 4096 as the default).
```ocaml
let initialize_computer ?(ram_size=4096) instructions =
  let ram = initialize_memory ram_size instructions in
  let in_queue = Queue.create () in
  let out_queue = Queue.create () in
  { ram; ip = 0; rp = 0; state = Running; in_queue; out_queue }
```

We can write to `in_queue` and read from `out_queue`, either the next output value
(days 7, 9, 11) or the last value in the queue (Day 5).
```ocaml
let receive value comp =
  Queue.add value comp.in_queue;
  comp

let get_next_output comp =
  Queue.take comp.out_queue

let get_last_output comp =
  comp.out_queue
  |> Queue.to_seq
  |> OSeq.reduce (fun _ v -> v)
```

When the computer has stopped (either waiting or halted), the whole state is
returned as different tasks need different values from it.
```ocaml
let run_until_halt comp =
  let rec run comp =
    match comp.state with
    | Halted | Waiting -> comp
    | Running -> comp |> execute_opcode |> run
  in
  { comp with state = Running } |> run
```

</details>



### Day 7

[Amplification Circuit](http://adventofcode.com/2019/day/7) || [day07.ml](ocaml/day07.ml) || Runtime: 50 ms

<details>

Now that [my intcode module](ocaml/lib/intcode.ml) is complete, the main problem
of this task becomes how to repeatedly loop through the amplifiers until one
of them halts.

[`CCList.fold_map`](https://c-cube.github.io/ocaml-containers/dev/containers/CCList/index.html#val-fold_map)
proved to be very useful for this:
It takes an accumulator (just like the regular `fold_left`), which in our case is
the output of the previous computer/amplifier; and it returns a tuple containing
both the accumulator and the modified list (like `map`), which are our computers/amplifiers
after they had run this time.
We need both of those outputs.

Using that function, we can recursively run all of our computers until one of them halted:
```ocaml
let rec get_output (score, computers) =
  if some_halted computers then score
  else
    computers
    |> CCList.fold_map
      (fun last_output comp ->
         let comp' =
           comp
           |> Intcode.receive last_output
           |> Intcode.run_until_halt in
         comp'.output, comp')
      score
    |> get_output
```

With that in place, finding the solution for both parts is just a matter of running
all the permutations of phase settings, and finding the maximal output:
```ocaml
let solve =
  permutations
  %> List.fold_left
    (fun acc perm ->
       let computers = create_computers perm in
       (0, computers) |> get_output |> max acc)
    0
```

</details>



### Day 8

[Space Image Format](http://adventofcode.com/2019/day/8) || [day08.ml](ocaml/day08.ml) || Runtime: 2 ms

<details>

This is an easy one after Day 7, which was the hardest one so far for me.

The first part is boring:
We count the number of each digit per layer, and find the layer with the fewest zeros.

The second part is more interesting.
For each pixel, we recursively try to find its color, starting from the top-most
layer and until we reach a layer with a non-transparent pixel.
Not a very efficient way of doing things (we repeatedly go through the list of layers,
and then for each layer we go to `nth` pixel), but it wins for its simplicity:
```ocaml
let rec pixel_color layers pixel =
  match layers with
  | [] -> failwith "pixel is transparent"
  | layer :: below ->
    (match List.nth layer pixel with
     | '0' -> ' '
     | '1' -> '#'
     | '2' -> pixel_color below pixel
     | _ -> failwith "invalid input")
```

</details>



### Day 9

[Sensor Boost](http://adventofcode.com/2019/day/9) || [day09.ml](ocaml/day09.ml) || Runtime: 21 ms

<details>

This task has brought a relative pointer (`rp`) and a new mode (`2`):
```ocaml
  match mode with
  | 0 -> param_val
  | 1 -> ip + param
  | 2 -> rp + param_val
```

With that in place, the [intcode computer](ocaml/lib/intcode.ml) is now complete.

This felt even easier than Day 5.

</details>



### Day 10

[Monitoring Station](http://adventofcode.com/2019/day/10) || [day10.ml](ocaml/day10.ml) || Runtime: 33 ms

<details>

At first I didn't even bother to solve this one because all I could think of
was some lousy `O(n^2)` algorithm, and "Surely, that can't be it, I need
something more clever than that!".
As it turns out, there's no need to be more clever than that
(and don't call me Shirley!).

Initially I solved the first part without `atan2` because I was afraid of
floating point errors:
```ocaml
let slope (x1, y1) (x2, y2) =
  let dx = x2 - x1 in
  let dy = y2 - y1 in
  let d = gcd dx dy |> abs in
  let dx' = dx / d in
  let dy' = dy / d in
  (dx', dy')
```

Later on, for the second part, I decided to use `atan2` to keep things simple,
so I refactored everything to use it.

The first thing to notice in the task is that we are *not* in the usual
right-hand Cartesian coordinate system (where y-axis is counter-clockwise
from the x-axis), but in the left-hand one (y-axis points downwards).
Or to put it differently, instead of the usual `x-y` coordinate system,
we are in the rotated-clockwise `y-x` (right-hand!) coordinate system.

This means we can still have the meaningful results of `atan2` by providing
its arguments in reverse (`atan2 x y` instead of the usual `atan2 y x`).

To find the 200th asteroid in the clockwise direction starting from pointing
upwards, we need to sort the angles from the largest to smallest:
```ocaml
let angle_cmp (phi1, _) (phi2, _) = - Float.compare phi1 phi2
```

Fortunately, `Float.compare` correctly deals with any floating point inaccuracies,
so we're able to filter out all asteroids with the same relative angle to
our monitoring station:
```ocaml
asterioids
|> relative_locations station
|> OSeq.sort_uniq ~cmp:angle_cmp
|> OSeq.nth 199
```

</details>



### Day 11

[Space Police](http://adventofcode.com/2019/day/11) || [day11.ml](ocaml/day11.ml) || Runtime: 13 ms

<details>

We are once again in left-hand Cartesian coordinate system with y-axis pointing downwards,
and we must take that into an account when making turns.

A direction is defined as `(x, y)` tuple with possible values `(1, 0)` (right),
`(-1, 0)` (left), `(0, 1)` (down!), and `(0, -1)` (up!).
To make a turn we use the following function:
```ocaml
let rotate (x, y) = function
  | Left -> (y, -x)
  | Right -> (-y, x)
```

Painting the hull is a matter of recursively following the rules of the task,
until the computer halts:

1. read input from the current position
1. run computer until it can't run no more (`Waiting` state)
1. read the two outputs (`color` and `turn`, respectively)
1. paint the current position
1. change direction
1. move to the next position

```ocaml
let input = panels |> PanelMap.get_or ~default:0 pos in
let comp' =
  comp
  |> Intcode.receive input
  |> Intcode.run_until_halt in
let color = comp' |> Intcode.get_next_output in
let turn = comp' |> Intcode.get_next_output |> Turn.of_int in
let panels' = panels |> PanelMap.add pos color in
let dir' = turn |> Turn.rotate dir in
let pos' = Coord.(pos + dir') in
paint panels' pos' dir' comp'
```

</details>



### Day 12

[The N-Body Problem](http://adventofcode.com/2019/day/12) || [day12.ml](ocaml/day12.ml) || Runtime: 40 ms

<details>

The famous n-body problem! Woohoo!

When I saw the input, I immediately remembered a
[similar task ("Particle Swarm") from AoC 2017](https://adventofcode.com/2017/day/20).
Not because I have a really good memory (I don't), but because I've solved AoC 2017 just
one month ago, as a preparation for this year.

So let's reuse some data-types from
[that solution](https://github.com/narimiran/AdventOfCode2017/blob/master/ocaml/day20.ml),
it might be an overkill but who knows, it may be useful for the second part:
```ocaml
type coord = { x : int; y : int; z : int }
type moon = { p : coord; v : coord }
```

There is also no need for regex, `sscanf` does the job very well for these kinds
of inputs:
```ocaml
let zeros = { x = 0; y = 0; z = 0 }

let create_moon x y z = {
  p = { x; y; z };
  v = zeros;
}

let parse_line line =
  Scanf.sscanf
    line
    "<x=%d, y=%d, z=%d>"
    create_moon
```

Following the instructions on how to first calculate and then apply the gravity
to velocity, followed by applying velocity to positions, the first part is just
running the simulation with 1000 time steps, and calculating the total energy
of the system:
```ocaml
let time_step moons =
  moons
  |> List.map (apply_gravity moons)
  |> List.map apply_velocity

let part_1 =
  let open CCFun in
  simulate 1000
  %> List.map total_energy
  %> List.fold_left (+) 0
```

And then... the second part appears.
The first thing to do is just to ignore the instructions, which say
*"the universe might last for a very long time before repeating"*.

After a while, a change of mind.
Ok, we might want to listen to the instructions after all :)

A thought comes to my mind:
There are multiple bodies which behave periodically, but each (probably) has
its own period.
We need to break this problem down into smaller ones, calculate the periods
for each smaller problem and then find the least common multiple.

The question remains: what are the smaller problems here?
I had several wrong guesses before it dawned on me: the directions
are independent of each other.

That discovery is the best part of the solution for part 2.
The code is quite ugly and I won't be posting it here.

There are several parts where you can potentially make your code faster.
- The first thing to notice is that you don't have to save every configuration,
  the system will return at its original configuration.
- The second thing that speeds things up is that you can only look for the time
  where the velocities for each moon will return to its original values (zeros).
  This happens twice per period (the initial state, and then the "farthest" state),
  so we can find when this happens and then multiply the result by 2.

</details>



### Day 13

[Care Package](http://adventofcode.com/2019/day/13) || [day13.ml](ocaml/day13.ml) || Runtime: 38 ms

<details>

Another odd day, another intcode task.

The first part is straight-forward.
Run the computer until it is halted, and then go through the outputs to count
how many times you encounter a `Block` tile.
This could have been done simply by counting number of `2`s seen, but that
felt like a magic number, so I created a `Tile` module, so that there is no
confusion about it:
```ocaml
module Tile = struct
  type t = Empty | Wall | Block | Paddle | Ball

  let of_int = function
    | 0 -> Empty
    | 1 -> Wall
    | 2 -> Block
    | 3 -> Paddle
    | 4 -> Ball
    | _ -> failwith "invalid tile"
end
```

The second part was very problematic for me because I didn't understand that we
need to read *all* the outputs every time we halt.
I first thought it was just three outputs each time, and it took me a lot of
time to figure that one out.
I would have appreciated a more detailed instructions for the second part.

Having a `Tile` module also makes finding a paddle and a ball more readable
and understandable than just having some "magic numbers" like 3 and 4:

```ocaml
let (x, y, t) = comp |> Intcode.get_next_3_outputs in
if (x, y) = (-1, 0) then score := t
else
  match Tile.of_int t with
  | Tile.Paddle -> paddle_pos := x
  | Tile.Ball -> ball_pos := x
  | _ -> ()
```

</details>
