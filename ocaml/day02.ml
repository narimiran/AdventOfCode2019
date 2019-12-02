let input =
  CCIO.(with_in "inputs/02.txt" read_all)
  |> CCString.rtrim
  |> String.split_on_char ','
  |> List.map int_of_string

let output = 19690720


let set_up n v l =
  let a = Array.of_list l in
  a.(1) <- n;
  a.(2) <- v;
  a

let execute_opcode_at i a =
  let noun, verb = a.(a.(i+1)), a.(a.(i+2)) in
  let dest = a.(i+3) in
  match a.(i) with
  | 1 -> a.(dest) <- noun + verb
  | 2 -> a.(dest) <- noun * verb
  | _ -> failwith "invalid input"

let run_until_halt a =
  let rec goto i =
    match a.(i) with
    | 99 -> a.(0)
    | _ ->
      a |> execute_opcode_at i;
      goto (i+4)
  in
  goto 0


let part_1 =
  let open CCFun in
  set_up 12 2
  %> run_until_halt
  %> Printf.printf "%d\n"

let part_2 intcode =
  let noun = ref (-1) in
  let verb = ref (-1) in
  let s = ref 0 in
  while !s <> output do
    incr noun;
    verb := -1;
    while !verb < 100 && !s <> output do
      incr verb;
      s := intcode |> set_up !noun !verb |> run_until_halt;
    done;
  done;
  100 * !noun + !verb |> Printf.printf "%d\n"


let () =
  input |> part_1;
  input |> part_2;
