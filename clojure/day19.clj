(ns day19
  (:require [intcode :as ic]))


(def computer (ic/initialize-from-file 19))

(defn- check-pt [x y]
  (-> computer
      (ic/in-run-out [x y])
      :output))

(defn- part-1 []
  (reduce +
          (for [x (range 50)
                y (range 50)]
            (check-pt x y))))

(defn- square-fits? [x y]
  (= 1 (check-pt (+ x 99) (- y 99))))

(defn- part-2 []
  (loop [x 0
         y 100]
    (if (= 1 (check-pt x y))
      (if (square-fits? x y)
        (+ (* 10000 x) (- y 99))
        (recur x (inc y)))
      (recur (inc x) y))))


(defn solve []
  [(part-1) (part-2)])


(solve)
