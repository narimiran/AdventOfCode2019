let instructions =
  CCIO.(with_in "inputs/05.txt" read_all)
  |> CCString.rtrim
  |> String.split_on_char ','
  |> List.map int_of_string

let input = ref 0
let output = ref 0


let get_param_value a ip param =
  let instr = a.(ip) in
  let param_val = a.(ip + param) in
  let digit = CCInt.pow 10 (param+1) in
  let mode = (instr / digit) mod 10 in
  match mode with
  | 0 -> a.(param_val)
  | 1 -> param_val
  | _ -> failwith "invalid parameter"


let execute_opcode_at ip a =
  let read_param = get_param_value a ip in
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
  | 3 ->
    a.(a.(ip+1)) <- !input;
    ip+2
  | 4 ->
    output := noun;
    ip+2
  | 5 | 6 as op ->
    let verb = read_param 2 in
    let ( <>|= ) = if op = 5 then ( <> ) else ( = ) in
    if noun <>|= 0 then verb else ip+3
  | _ -> failwith "invalid input"


let run_until_halt instr =
  let a = Array.of_list instr in
  let rec goto ip =
    match a.(ip) with
    | 99 -> ()
    | _ -> a |> execute_opcode_at ip |> goto
  in
  goto 0


let solve ~part =
  input := if part = 1 then 1 else 5;
  instructions |> run_until_halt;
  Printf.printf "%d\n" !output


let () =
  solve ~part:1;
  solve ~part:2
