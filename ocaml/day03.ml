let parse filename =
  CCIO.(with_in filename read_lines_l)
  |> List.map (String.split_on_char ',')

let separate_wires wires =
  List.hd wires, List.hd (List.tl wires)

let dxs = CCHashtbl.of_list [ ('U', 0); ('D', 0); ('L', -1); ('R', 1) ]
let dys = CCHashtbl.of_list [ ('U', 1); ('D', -1); ('L', 0); ('R', 0) ]

let visited = Hashtbl.create 160_000
let intersections = Hashtbl.create 60


let iterate ~f =
  let x = ref 0 in
  let y = ref 0 in
  let dist = ref 0 in
  List.iter
    (fun s ->
       let dir = s.[0] in
       let amount = int_of_string (CCString.drop 1 s) in
       let dx = Hashtbl.find dxs dir in
       let dy = Hashtbl.find dys dir in
       let line = OSeq.init ~n:amount (fun i ->
           let j = i+1 in
           let px = !x + j*dx in
           let py = !y + j*dy in
           incr dist;
           ((px, py), !dist)) in
       f line;
       x := !x + amount*dx;
       y := !y + amount*dy;
    )

let add_intersections (coord, dist) =
  if Hashtbl.mem visited coord then
    let tot = Hashtbl.find visited coord + dist in
    Hashtbl.add intersections coord tot

let visit_all_points = iterate ~f:(Hashtbl.add_seq visited)
let find_intersections = iterate ~f:(OSeq.iter add_intersections)


let closest k _ acc =
  let manhattan (x, y) = abs x + abs y in
  let dist = manhattan k in
  if dist < acc then dist else acc

let shortest _ v acc =
  if v < acc then v else acc

let find f points =
  Hashtbl.fold f points Int.max_int
  |> Printf.printf "%d\n"


let wire_a, wire_b = parse "inputs/03.txt" |> separate_wires

let () =
  wire_a |> visit_all_points;
  wire_b |> find_intersections;

  intersections |> find closest;
  intersections |> find shortest
