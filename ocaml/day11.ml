open CCFun

module Turn = struct
  type t = Left | Right

  let of_int = function
    | 0 -> Left
    | 1 -> Right
    | _ -> failwith "invalid turn"

  let rotate (x, y) = function
    | Left -> (y, -x)
    | Right -> (-y, x)
end

module Coord = struct
  type t = int * int

  let (+) (x1, y1) (x2, y2) = (x1+x2, y1+y2)

  let compare (x1, y1) (x2, y2) =
    match Int.compare x1 x2 with
    | 0 -> Int.compare y1 y2
    | v -> v

end

module PanelMap = CCMap.Make(Coord)


let paint_hull starting_color instructions =
  let comp = Intcode.initialize_computer instructions in
  let panels = PanelMap.singleton (0, 0) starting_color in

  let rec paint panels pos dir comp =
    if Intcode.get_state comp = Intcode.Halted then panels
    else
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
  in
  paint panels (0, 0) (0, -1) comp


let part_1 =
  paint_hull 0
  %> PanelMap.cardinal
  %> Printf.printf "%d\n"

let part_2 instructions =
  let registration_id = Array.make_matrix 6 48 ' ' in
  let put_letters a =
    PanelMap.iter
      (fun (x, y) v ->
         let c = if v = 1 then '#' else ' ' in
         a.(y).(x) <- c)
  in
  let show = Array.iter (CCString.of_array %> Printf.printf "%s\n")
  in
  instructions |> paint_hull 1 |> put_letters registration_id;
  show registration_id


let instructions = Intcode.read_instructions "inputs/11.txt"

let () =
  instructions |> part_1;
  instructions |> part_2
