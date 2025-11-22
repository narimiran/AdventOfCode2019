(ns day11
  (:require [intcode :as ic]
            [aoc-utils.core :as aoc]))


(defn- paint [computer initial]
  (let [turns [aoc/left-turn aoc/right-turn]]
    (loop [comp computer
           pos [0 0]
           dir [0 -1]
           painted {pos initial}]
      (if (= :halted (:status comp))
        painted
        (let [comp' (ic/in-run-out comp (painted pos 0))
              turn (:output comp')
              comp'' (ic/pop-out-queue comp')
              color (:output comp'')
              dir' ((turns turn) dir)]
          (recur comp''
                 (aoc/pt+ pos dir')
                 dir'
                 (assoc painted pos color)))))))


(defn solve [filename]
  (let [computer (ic/initialize-from-file filename)]
    [(count (paint computer 0))
     (->> (paint computer 1)
          (keep (fn [[pt v]] (when (not (zero? v)) pt)))
          set
          aoc/show-grid
          #_println)]))


(solve 11)
