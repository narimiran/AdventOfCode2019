(ns day25
  (:require [intcode :as ic]))


(defn play-manually [computer]
  (loop [c (ic/run-until-halt computer)]
    (->> c
         :out-queue
         (map char)
         (apply str)
         (println))
    (recur (-> c
               (assoc :out-queue [])
               (ic/send-to-in-queue (ic/to-machine-code [(read-line)]))
               (ic/run-until-halt)))))



; Manually played and explored the map.
; This is the route which collects all usable items:
(def inputs ["south" "take mutex" "south" "west" "west" "take klein bottle" "east" "east" "north"
             "east" "take mug" "east" "take polygon" "north" "north" "take hypercube" "south" "south"
             "east" "east" "east" "south" "west" "inv"])

; Manually dropped one by one item in the inventory, and the correct weight was from:
; klein bottle + mutex + hypercube + mug.
; The method: if, when one item is dropped, the weight is below the target, that item
; must be part of the solution.
(def inputs-2 ["drop polygon" "west"])


(defn play-game [computer]
  (loop [[instr & inputs'] (into inputs inputs-2)
         c (ic/run-until-halt computer)]
    (if (nil? instr)
      (->> c
         :out-queue
         (map char)
         (apply str)
         (re-find #"\d+")
         parse-long)
      (recur inputs'
             (-> c
                 (assoc :out-queue [])
                 (ic/send-to-in-queue (ic/to-machine-code [instr]))
                 (ic/run-until-halt))))))


(defn solve [filename]
  (let [computer (ic/initialize-from-file filename 8192)]
    (play-game computer)))


(solve 25)
