type dir = X | Y | Z
type coord = { x : int; y : int; z : int }
type moon = { p : coord; v : coord }

let zeros = { x = 0; y = 0; z = 0 }

let create_moon x y z = {
  p = { x; y; z };
  v = zeros;
}

let parse_line line =
  Scanf.sscanf
    line
    "<x=%d, y=%d, z=%d>"
    create_moon

let potential moon =
  abs moon.p.x + abs moon.p.y + abs moon.p.z

let kinetic moon =
  abs moon.v.x + abs moon.v.y + abs moon.v.z

let total_energy moon =
  (potential moon) * (kinetic moon)

let calc_gravity moon =
  List.fold_left
    (fun vel other -> {
         x = vel.x + Int.compare other.p.x moon.p.x;
         y = vel.y + Int.compare other.p.y moon.p.y;
         z = vel.z + Int.compare other.p.z moon.p.z;
       } )
    zeros

let apply_gravity moons moon =
  let gravity = calc_gravity moon moons in
  let new_v = {
    x = moon.v.x + gravity.x;
    y = moon.v.y + gravity.y;
    z = moon.v.z + gravity.z;
  }
  in
  { moon with v = new_v }

let apply_velocity moon =
  let new_p = {
    x = moon.p.x + moon.v.x;
    y = moon.p.y + moon.v.y;
    z = moon.p.z + moon.v.z;
  }
  in
  { moon with p = new_p }


let time_step moons =
  moons
  |> List.map (apply_gravity moons)
  |> List.map apply_velocity

let rec simulate n moons =
  if n = 0 then moons
  else moons |> time_step |> simulate (n-1)

let part_1 =
  let open CCFun in
  simulate 1000
  %> List.map total_energy
  %> List.fold_left (+) 0


let lcm a b =
  let rec gcd a b =
    if b = 0 then a else gcd b (a mod b) in
  (a / gcd a b) * b

let extract_direction dir {v; _} =
  match dir with
  | X -> v.x
  | Y -> v.y
  | Z -> v.z

let part_2 moons =
  let x_period = ref 0 in
  let y_period = ref 0 in
  let z_period = ref 0 in
  let is_initial_vel dir moons =
    let current_vel = moons |> List.map (extract_direction dir) in
    current_vel = [ 0; 0; 0; 0 ]
  in
  let rec check_periods n moons =
    if !x_period <> 0 && !y_period <> 0 && !z_period <> 0 then ()
    else begin
      if !x_period = 0 then
        if is_initial_vel X moons then x_period := n;
      if !y_period = 0 then
        if is_initial_vel Y moons then y_period := n;
      if !z_period = 0 then
        if is_initial_vel Z moons then z_period := n;
      moons |> time_step |> check_periods (n+1)
    end
  in
  check_periods 0 moons;
  2 * (lcm (lcm !x_period !y_period) !z_period)


let moons =
  CCIO.(with_in "inputs/12.txt" read_lines_l)
  |> List.map parse_line

let () =
  moons |> part_1 |> Printf.printf "%d\n";
  moons |> part_2 |> Printf.printf "%d\n"
