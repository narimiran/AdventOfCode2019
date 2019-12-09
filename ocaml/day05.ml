let instructions = Intcode.read_instructions "inputs/05.txt"

let solve ~part instructions =
  let input = if part = 1 then 1 else 5 in
  let ram_size = List.length instructions in
  instructions
  |> Intcode.initialize_computer ~input ~ram_size
  |> Intcode.run_until_halt
  |> Intcode.get_output_value
  |> Printf.printf "%d\n"

let () =
  instructions |> solve ~part:1;
  instructions |> solve ~part:2
