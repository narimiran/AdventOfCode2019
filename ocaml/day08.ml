let h, w = 6, 25

type digits = { zeros : int; ones : int; twos: int }

let layers =
  CCIO.(with_in "inputs/08.txt" read_all)
  |> CCString.to_list
  |> CCList.sublists_of_len (h*w)

let count_digits x =
  let zeros = x |> CCList.count ((=) '0') in
  let ones  = x |> CCList.count ((=) '1') in
  let twos  = x |> CCList.count ((=) '2') in
  { zeros; ones; twos }

let part_1 =
  let open CCFun in
  List.map count_digits
  %> List.fold_left
    (fun (zc, res) { zeros; ones; twos } ->
       if zeros < zc then zeros, (ones * twos) else zc, res)
    (Int.max_int, 0)
  %> snd
  %> Printf.printf "%d\n"

let rec pixel_color layers pixel =
  match layers with
  | [] -> failwith "pixel is transparent"
  | layer :: below ->
    (match List.nth layer pixel with
     | '0' -> ' '
     | '1' -> '#'
     | '2' -> pixel_color below pixel
     | _ -> failwith "invalid input")

let part_2 layers =
  CCList.(0 --^ (h*w))
  |> List.map (pixel_color layers)
  |> CCList.sublists_of_len w
  |> List.map CCString.of_list
  |> List.iter (Printf.printf "%s\n")

let () =
  layers |> part_1;
  layers |> part_2
