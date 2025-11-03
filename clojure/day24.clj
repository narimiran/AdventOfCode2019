(ns day24
  (:require aoc
            [clojure.math :refer [pow]]))


(defn- grid->3d [grid]
  (set (map (fn [[x y]] [x y 0]) grid)))

(defn- biodiversity [grid]
  (long (reduce + (map (fn [[x y]] (pow 2 (+ x (* 5 y)))) grid))))

(defn- neighbours-2d [x y z]
  [[(dec x) y z] [(inc x) y z] [x (dec y) z] [x (inc y) z]])

(defn- neighbours-3d [x y z]
  (let [pt [x y]
        dz (dec z)
        iz (inc z)
        W  [(dec x) y z]
        E  [(inc x) y z]
        N  [x (dec y) z]
        S  [x (inc y) z]]
    (cond
      (and (zero? x) (#{1 2 3} y))    [[1 2 dz] E N S]
      (and (= 4 x) (#{1 2 3} y))      [W [3 2 dz] N S]
      (and (zero? y) (#{1 2 3} x))    [W E [2 1 dz] S]
      (and (= 4 y) (#{1 2 3} x))      [W E N [2 3 dz]]
      (= [0 0] pt)                    [[1 2 dz] E [2 1 dz] S]
      (= [0 4] pt)                    [[1 2 dz] E N [2 3 dz]]
      (= [4 0] pt)                    [W [3 2 dz] [2 1 dz] S]
      (= [4 4] pt)                    [W [3 2 dz] N [2 3 dz]]
      (#{[1 1] [3 3] [1 3] [3 1]} pt) [W E N S]
      (= [2 1] pt)                    [W E N [0 0 iz] [1 0 iz] [2 0 iz] [3 0 iz] [4 0 iz]]
      (= [2 3] pt)                    [W E [0 4 iz] [1 4 iz] [2 4 iz] [3 4 iz] [4 4 iz] S]
      (= [1 2] pt)                    [W [0 0 iz] [0 1 iz] [0 2 iz] [0 3 iz] [0 4 iz] N S]
      (= [3 2] pt)                    [[4 0 iz] [4 1 iz] [4 2 iz] [4 3 iz] [4 4 iz] E N S])))


(defn play-turn [bugs nb-func n]
  (set (for [z (range (- n) (inc n))
             x (range 5)
             y (range 5)
             :let [pt [x y z]
                   nbs (count (filter bugs (nb-func x y z)))
                   bug (bugs pt)]
             :when (or (and bug (= 1 nbs))
                       (and (not bug) (#{1 2} nbs)))]
         pt)))

(defn part-1 [bugs]
  (loop [bugs bugs
         seen #{(biodiversity bugs)}]
    (let [bugs' (play-turn bugs neighbours-2d 0)
          bd (biodiversity bugs')]
      (if (seen bd) bd
          (recur bugs' (conj seen bd))))))


(defn part-2 [bugs]
  (->> (range 1 201)
       (reduce (fn [bugs n]
                 (play-turn bugs neighbours-3d n))
               bugs)
       count))


(defn solve [filename]
  (let [bugs (-> (aoc/read-file filename)
                 aoc/parse-input
                 (aoc/grid->point-set #{\#})
                 grid->3d)]
    [(part-1 bugs) (part-2 bugs)]))


(solve 24)
