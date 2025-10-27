(ns day02
  (:require [intcode :as ic]))


(def ^:const wanted 19690720)


(defn- modify-and-run [computer noun verb]
  (-> (reduce ic/modify-ram computer [[1 noun] [2 verb]])
      ic/run-until-halt
      :ram
      first))

(defn part-2 [computer]
  (first
   (for [noun  (range 100)
         verb  (range 100)
         :when (= wanted (modify-and-run computer noun verb))]
     (+ (* 100 noun) verb))))

(defn solve [filename]
  (let [computer (ic/initialize-from-file filename)]
    [(modify-and-run computer 12 2)
     (part-2 computer)]))


(solve 2)
