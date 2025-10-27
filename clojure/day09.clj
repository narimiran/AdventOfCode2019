(ns day09
  (:require [intcode :as ic]))


(defn run [computer input]
  (-> computer
      (ic/in-run-out input)
      :output))


(defn solve [filename]
  (let [computer (ic/initialize-from-file filename)]
    [(run computer 1)
     (run computer 2)]))

(solve 9)
