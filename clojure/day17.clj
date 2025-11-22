(ns day17
  (:require [aoc-utils.core :as aoc]
            [intcode :as ic]
            [clojure.string :as str]))


(defn- create-grid [computer]
  (->> computer
       ic/run-until-halt
       :out-queue
       (map char)
       (apply str)
       str/split-lines
       (mapv vec)))

(def robots {\^ [0 -1]
             \v [0  1]
             \< [-1 0]
             \> [ 1 0]})

(defn- find-robot [grid]
  (some (fn [[j line]]
          (some (fn [[i c]]
                  (when-let [robot-dir (robots c)]
                    [[i j] robot-dir]))
                (map-indexed vector line)))
        (map-indexed vector grid)))

(defn- at [grid i j]
  ((grid i) j))

(defn- scaffold?
  ([grid [i j]] (scaffold? grid i j))
  ([grid i j]
   (= \# (at grid i j))))

(defn- part-1 [grid]
  (let [h (count grid)
        w (count (first grid))]
    (reduce +
            (for [i (range 1 (dec h))
                  j (range 1 (dec w))
                  :when (and (scaffold? grid i j)
                             (every? #(apply scaffold? grid %)
                                     (aoc/neighbours-4 [i j])))]
              (* i j)))))


(defn trace-movements [grid]
  (let [[pos dir] (find-robot grid)
        h (count grid)
        w (count (first grid))]
    (loop [[x y] pos
           [dx dy :as dir] dir
           acc []]
      (let [[sx sy] [(+ x dx) (+ y dy)]
            [lx ly] [(+ x dy) (- y dx)]
            [rx ry] [(- x dy) (+ y dx)]]
        (cond
          (and (aoc/inside? w h sx sy) (scaffold? grid sy sx))
          (recur [sx sy] dir (conj (pop acc) (inc (peek acc))))

          (and (aoc/inside? w h lx ly) (scaffold? grid ly lx))
          (recur [lx ly] [dy (- dx)] (conj acc \L 1))

          (and (aoc/inside? w h rx ry) (scaffold? grid ry rx))
          (recur [rx ry] [(- dy) dx] (conj acc \R 1))

          :else
          (str/join "," acc))))))


; from `trace-movements` and then some manual search and replace
(def movement-functions
  ["A,A,B,C,B,C,B,C,B,A"
   "L,10,L,8,R,8,L,8,R,6"
   "R,6,R,8,R,8"
   "R,6,R,6,L,8,L,10"
   "n"])


(defn- part-2 [computer]
  (-> computer
      (ic/modify-ram [0 2])
      (ic/in-run-result (ic/to-machine-code movement-functions))))


(defn solve [filename]
  (let [computer (ic/initialize-from-file filename 8192)
        grid (create-grid computer)]
    [(part-1 grid)
     (part-2 computer)]))


(solve 17)
