open CCFun

let instructions = Intcode.read_instructions "inputs/07.txt"

let all_permutations =
  (* https://codereview.stackexchange.com/questions/125173/generating-permutations-in-ocaml *)
  let rec aux result other = function
    | [] -> [result]
    | hd :: tl ->
      let r = aux (hd :: result) [] (other @ tl) in
      if tl <> [] then
        r @ aux result (hd :: other) tl
      else r
  in
  aux [] []


let create_computers =
  List.map
    (fun phase ->
      instructions
      |> Intcode.initialize_computer
      |> Intcode.receive phase)

let some_halted =
  List.exists (fun comp -> Intcode.get_state comp = Intcode.Halted)

let rec get_output (score, computers) =
  if some_halted computers then score
  else
    computers
    |> CCList.fold_map
      (fun last_output comp ->
           comp
           |> Intcode.receive last_output
           |> Intcode.run_until_halt
           |> fun comp -> ((Intcode.get_next_output comp), comp) )
      score
    |> get_output


let solve =
  all_permutations
  %> List.fold_left
    (fun acc perm ->
       let computers = create_computers perm in
       (0, computers) |> get_output |> max acc)
    0
  %> Printf.printf "%d\n"


let () =
  CCList.(0 -- 4) |> solve;
  CCList.(5 -- 9) |> solve
