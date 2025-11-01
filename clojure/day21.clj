(ns day21
  (:require [intcode :as ic]))


(def instructions-1
  ["OR A J"
   "AND B J"
   "AND C J"
   "NOT J J"
   "AND D J"])

(def instructions-2
  (into instructions-1
        ["OR E T"
         "OR H T"
         "AND T J"]))

(defn run [filename instructions command]
  (-> (ic/initialize-from-file filename)
      (ic/in-run-result (ic/to-machine-code (conj instructions command)))))


(defn solve [filename]
  [(run filename instructions-1 "WALK")
   (run filename instructions-2 "RUN")])


(solve 21)
