let low, high = 183564, 657474

let group_lengths l =
  (* this is a specialised and optimized version of CCList.group_succ *)
  let rec aux acc cur amnt l =
    match cur, l with
    | _, [] -> amnt :: acc
    | y, x :: tl when Int.equal x y -> aux acc x (amnt+1) tl
    | _, x :: tl -> aux (amnt :: acc) x 1 tl
  in
  aux [] 0 0 l

let has_multiples = List.exists (( <= ) 2)
let has_duplicates = List.exists (( = ) 2)

let solve () =
  let part_1 = ref 0 in
  let part_2 = ref 0 in
  for a = 1 to 5 do
    let a' = if a = 1
      then 8 else a in
    for b = a' to 9 do
      for c = b to 9 do
        for d = c to 9 do
          for e = d to 9 do
            for f = e to 9 do
              let nr = a*100_000 + b*10_000 + c*1_000 + d*100 + e*10 + f in
              if low <= nr && nr <= high then
                let digit_groups = [ a; b; c; d; e; f ] |> group_lengths in
                if digit_groups |> has_multiples then incr part_1;
                if digit_groups |> has_duplicates then incr part_2
            done
          done
        done
      done
    done
  done;
  !part_1, !part_2


let () =
  let solution = solve () in
  Printf.printf "%d\n%d\n" (fst solution) (snd solution)
