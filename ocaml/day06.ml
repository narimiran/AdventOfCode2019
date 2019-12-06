(* k = kid; string
 * p = parent; string
 * c = children; string list
 * k2p = kid to parent; Map
 * p2c = parent to children; Map *)


module RelationMap = CCMap.Make(String)

let parse filename =
  let add = RelationMap.add in
  let get = RelationMap.get_or ~default:[]
  in
  CCIO.(with_in filename read_lines_l)
  |> List.map (String.split_on_char ')')
  |> List.fold_left
    (fun (p2c, k2p) -> function
       | [ p; k ] ->
         let c = p2c |> get p in
         ( p2c |> add p (k :: c),
           k2p |> add k p )
       | _ -> failwith "invalid input")
    (RelationMap.empty, RelationMap.empty)


let part_1 p2c =
  let rec traverse n key =
    let children = p2c |> RelationMap.get_or ~default:[] key in
    match children with
    | [] -> n
    | _ ->
      let children_distances = List.map (traverse (n+1)) children in
      n + List.fold_left (+) 0 children_distances
  in
  traverse 0 "COM" |> Printf.printf "%d\n"


let part_2 k2p =
  let find_all_ancestors =
    let rec traverse relations acc = function
      | "COM" -> acc
      | kid ->
        let parent = relations |> RelationMap.find kid in
        traverse relations (parent::acc) parent
    in
    traverse k2p []
  in
  let rec calc_orbital_transfers you san =
    match you, san with
    | x::xs, y::ys when x = y -> calc_orbital_transfers xs ys
    | _, _ -> List.length you + List.length san
  in
  let you = find_all_ancestors "YOU" in
  let san = find_all_ancestors "SAN" in
  calc_orbital_transfers you san |> Printf.printf "%d\n"


let p2c, k2p = parse "inputs/06.txt"

let () =
  p2c |> part_1;
  k2p |> part_2
