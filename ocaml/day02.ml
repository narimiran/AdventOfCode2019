open CCFun

let instructions = Intcode.read_instructions "inputs/02.txt"
let output = 19690720

let set_up values instructions =
  let ram_size = List.length instructions in
  instructions
  |> Intcode.initialize_computer ~ram_size
  |> Intcode.set_positions values

let run = Intcode.run_until_halt %> Intcode.read_ram_pos 0


let part_1 =
  set_up [(1, 12); (2, 2)]
  %> run
  %> Printf.printf "%d\n"

let rec part_2 ?(noun=0) intcode =
  let result = intcode |> set_up [(1, noun)] |> run in
  let verb = output - result in
  if verb < 100 then
    100 * noun + verb |> Printf.printf "%d\n"
  else
    part_2 ~noun:(noun+1) intcode


let () =
  instructions |> part_1;
  instructions |> part_2
