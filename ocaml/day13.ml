open CCFun

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


let count_blocks =
  let rec aux blocks comp =
    if comp |> Intcode.is_out_empty then blocks
    else
      let (_, _, t) = comp |> Intcode.get_next_3_outputs in
      let blocks = blocks + CCBool.to_int (Tile.of_int t = Tile.Block) in
      comp |> aux blocks
  in
  Intcode.run_until_halt %> aux 0


let play =
  let paddle_pos = ref 0 in
  let ball_pos = ref 0 in
  let score = ref 0 in
  let rec next_move comp =
    match Intcode.get_state comp with
    | Halted -> !score
    | _ ->
      let comp = comp |> Intcode.run_until_halt in
      while not (Intcode.is_out_empty comp) do
        let (x, y, t) = comp |> Intcode.get_next_3_outputs in
        if (x, y) = (-1, 0) then score := t
        else
          match Tile.of_int t with
          | Tile.Paddle -> paddle_pos := x
          | Tile.Ball -> ball_pos := x
          | _ -> ()
      done;
      let joystick = Int.compare !ball_pos !paddle_pos in
      comp |> Intcode.receive joystick |> next_move
  in
  Intcode.set_positions [ (0, 2) ] %> next_move


let game =
  Intcode.read_instructions "inputs/13.txt"
  |> Intcode.initialize_computer

let () =
  game |> count_blocks |> Printf.printf "%d\n";
  game |> play |> Printf.printf "%d\n"
