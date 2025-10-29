(ns day15
  (:require [intcode :as ic]
            aoc))

(def directions
  {1 [0 -1]
   2 [0 1]
   3 [-1 0]
   4 [1 0]})

(defn- run [computer]
  (loop [queue (conj aoc/empty-queue [0 [0 0] 1 computer])
         seen #{}]
    (let [[steps pos status computer] (peek queue)]
      (if (= 2 status) [steps computer] ; part 1
          (let [seen' (conj seen pos)
                queue' (reduce (fn [q [i dir]]
                                 (let [pos' (aoc/pt+ pos dir)
                                       comp' (ic/in-run-out computer i)
                                       out (:output comp')]
                                   (if (or (seen pos') (zero? out)) q
                                       (conj q [(inc steps) pos' out comp']))))
                               (pop queue)
                               directions)]
            (if (empty? queue')
              steps ; part 2
              (recur queue' seen')))))))

(defn solve [filename]
  (let [computer (ic/initialize-from-file filename)
        [pt1 comp'] (run computer)]
    [pt1 (run comp')]))


(solve 15)
