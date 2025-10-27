(ns day05
  (:require [intcode :as ic]))


(defn diagnostic-program [computer v]
  (-> computer
      (ic/in-run-out v)
      :output))

(defn solve [filename]
  (let [computer (ic/initialize-from-file filename)]
    [(diagnostic-program computer 1)
     (diagnostic-program computer 5)]))


(solve 5)
