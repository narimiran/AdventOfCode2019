(ns day09
  (:require [intcode :as ic]))


(defn solve [filename]
  (let [computer (ic/initialize-from-file filename)]
    [(ic/in-run-result computer 1)
     (ic/in-run-result computer 2)]))

(solve 9)
