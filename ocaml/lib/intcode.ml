type state = Running | Waiting | Halted

type computer = {
  ram : int array;
  ip : int;
  rp : int;
  state : state;
  in_queue : int Queue.t;
  output : int;
}


let read_instructions filename =
  CCIO.(with_in filename read_all)
  |> CCString.rtrim
  |> String.split_on_char ','
  |> List.map int_of_string


let read_param param { ram ; ip ; rp ; _ } =
  let digit = CCInt.pow 10 (param+1) in
  let mode = (ram.(ip) / digit) mod 10 in
  let param_val = ram.(ip + param) in
  match mode with
  | 0 -> param_val
  | 1 -> ip + param
  | 2 -> rp + param_val
  | _ -> failwith "invalid parameter"


let execute_opcode ({ ram; ip; rp; in_queue; _ } as comp) =
  let noun = comp |> read_param 1 in
  match ram.(ip) mod 100 with
  | 1 | 2 | 7 | 8 as op ->
    let verb = comp |> read_param 2 in
    let dest = comp |> read_param 3 in
    ram.(dest) <-
      (match op with
       | 1 -> ram.(noun) + ram.(verb)
       | 2 -> ram.(noun) * ram.(verb)
       | 7 -> CCBool.to_int (ram.(noun) < ram.(verb))
       | 8 -> CCBool.to_int (ram.(noun) = ram.(verb))
       | _ -> failwith "ocaml, you silly");
    { comp with ip = ip+4 }
  | 3 ->
    (match Queue.take_opt in_queue with
     | Some x ->
       ram.(noun) <- x;
       { comp with state = Running; ip = ip+2 }
     | None ->
       { comp with state = Waiting })
  | 4 ->
    { comp with ip = ip+2; output = ram.(noun) }
  | 5 | 6 as op ->
    let verb = comp |> read_param 2 in
    let ( <>|= ) = if op = 5 then ( <> ) else ( = ) in
    let ip' = if ram.(noun) <>|= 0 then ram.(verb) else ip+3 in
    { comp with ip = ip' }
  | 9 ->
    { comp with rp = (rp + ram.(noun)); ip = ip+2 }
  | 99 ->
    { comp with state = Halted }
  | _ -> failwith "invalid input"


let initialize_memory size instructions =
  let ram = Array.init size (fun _ -> 0) in
  let instr = Array.of_list instructions in
  let l = Array.length instr in
  Array.blit instr 0 ram 0 l;
  ram

let initialize_in_queue input =
  let in_queue = Queue.create () in
  Queue.add input in_queue;
  in_queue

let initialize_computer ?(input=0) ?(ram_size=4096) instructions =
  let ram = initialize_memory ram_size instructions in
  let in_queue = initialize_in_queue input in
  { ram; ip = 0; rp = 0; state = Running; in_queue; output = 0 }

let run_until_halt comp =
  let rec run comp =
    match comp.state with
    | Halted | Waiting -> comp
    | Running -> comp |> execute_opcode |> run
  in
  { comp with state = Running } |> run


let receive value comp =
  Queue.add value comp.in_queue;
  comp

let get_state comp = comp.state

let get_output_value comp =
  comp.output

let set_positions l comp =
  List.iter (fun (p, v) -> comp.ram.(p) <- v) l;
  comp

let read_ram_pos pos comp =
  comp.ram.(pos)
