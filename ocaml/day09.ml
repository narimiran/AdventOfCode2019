open CCFun

let instructions = Intcode.read_instructions "inputs/09.txt"

let solve ~part =
  Intcode.initialize_computer
  %> Intcode.receive part
  %> Intcode.run_until_halt
  %> Intcode.get_next_output
  %> Printf.printf "%d\n"

let () =
  instructions |> solve ~part:1;
  instructions |> solve ~part:2
