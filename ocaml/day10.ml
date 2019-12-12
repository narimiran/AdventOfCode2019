let asteroid_positions =
  CCList.foldi
    (fun acc1 row ->
       CCList.foldi
         (fun acc2 col c -> if c = '#' then OSeq.cons (col, row) acc2 else acc2)
         acc1)
    OSeq.empty

let parse filename =
  CCIO.(with_in filename read_lines_l)
  |> List.map CCString.to_list
  |> asteroid_positions


module AngleSet = Set.Make(Float)

let get_distance (x1, y1) (x2, y2) =
  (x2 - x1), (y2 - y1)

let get_angle dx dy =
  (* note the order of parameters for atan2 is switched *)
  atan2 (float_of_int dx) (float_of_int dy)


let find_best asteroids =
  asteroids
  |> OSeq.fold
    (fun (best_coord, largest_nr) a ->
       asteroids
       |> OSeq.fold
         (fun angles_seen b ->
            if a <> b then
              let dx, dy = get_distance a b in
              let phi = get_angle dx dy in
              AngleSet.add phi angles_seen
            else
              angles_seen)
         AngleSet.empty
       |> AngleSet.cardinal
       |> (fun n ->
           if n > largest_nr then (a, n)
           else (best_coord, largest_nr)) )
    ((0, 0), 0)


let relative_locations station =
  OSeq.fold
    (fun acc other ->
       let dx, dy = get_distance station other in
       let phi = get_angle dx dy in
       OSeq.cons (phi, (dx, dy)) acc)
    OSeq.empty


let find_200th station asterioids =
  let angle_cmp (phi1, _) (phi2, _) = - Float.compare phi1 phi2 in
  asterioids
  |> relative_locations station
  |> OSeq.sort_uniq ~cmp:angle_cmp
  |> OSeq.nth 199
  |> (fun (_, (other_x, other_y)) ->
      let (station_x, station_y) = station in
      100 * (station_x + other_x) + (station_y + other_y))


let asteroids = parse "inputs/10.txt"

let monitoring_station, nr_of_visible = asteroids |> find_best
let pos_of_200th = asteroids |> find_200th monitoring_station

let () =
  Printf.printf "%d\n%d\n"
    nr_of_visible
    pos_of_200th
