let input =
  CCIO.(with_in "inputs/01.txt" read_lines_l)
  |> List.map int_of_string


let fuel_required x =
  x / 3 - 2

let rec total_fuel_required tot x =
  let x' = fuel_required x in
  if x' <= 0 then tot
  else total_fuel_required (tot + x') x'


let solve ~part input =
  let f = if part = 1 then fuel_required else (total_fuel_required 0) in
  input
  |> List.fold_left (fun acc x -> acc + f x) 0
  |> Printf.printf "%d\n"


let () =
  input |> solve ~part:1;
  input |> solve ~part:2
