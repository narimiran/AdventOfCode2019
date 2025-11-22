(ns day07
  (:require
   [aoc-utils.core :refer [empty-queue]]
   [clojure.math.combinatorics :as combo]
   [intcode :as ic]))


(defn initialize-amps [amps phase-setting-sequence]
  (into empty-queue
        (map (fn [amp phase]
               (ic/send-to-in-queue amp [phase]))
             amps
             phase-setting-sequence)))

(defn run-amps [amps]
  (loop [amps   amps
         output 0]
    (if (every? #{:halted} (map :status amps))
      output
      (let [curr-amp (ic/in-run-out (peek amps) output)]
        (recur (conj (pop amps) curr-amp)
               (:output curr-amp))))))

(defn find-highest-signal [amps phase-settings]
  (reduce
   (fn [highest-signal phase-setting-sequence]
     (-> amps
         (initialize-amps phase-setting-sequence)
         run-amps
         (max highest-signal)))
   0
   (combo/permutations phase-settings)))


(defn solve [filename]
  (let [computer (ic/initialize-from-file filename)
        amps     (repeat 5 computer)]
    [(find-highest-signal amps (range 5))
     (find-highest-signal amps (range 5 10))]))


(solve 7)
