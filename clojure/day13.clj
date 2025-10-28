(ns day13
  (:require [intcode :as ic]
            aoc))


(defn part-1 [computer]
  (loop [queue (:out-queue (ic/run-until-halt computer))
         blocks 0]
    (if-let [[_ _ tile & queue'] queue]
      (recur queue'
             (if (= tile 2) (inc blocks) blocks))
      blocks)))

(defn- sign [x]
  (cond
    (pos? x) 1
    (neg? x) -1
    :else 0))

(defn- render-turn [state]
  (let [comp' (ic/run-until-halt (:computer state))]
    (loop [queue (:out-queue comp')
           state (assoc state :computer comp')]
      (cond
        (and (empty? queue) (= :halted (-> state :computer :status))) state
        (empty? queue) (let [joystick (sign (- (:ball-x state) (:paddle-x state)))]
                         (-> state
                             (assoc-in [:computer :out-queue] aoc/empty-queue)
                             (update :computer ic/send-to-in-queue [joystick])))
        :else (let [[x y tile & queue'] queue
                    state' (cond-> state
                             (and (= x -1) (= y 0)) (assoc :score tile)
                             (= tile 3) (assoc :paddle-x x)
                             (= tile 4) (assoc :ball-x x))]
                (recur queue' state'))))))


(defn part-2 [computer]
  (loop [state {:computer (-> computer (ic/modify-ram [0 2]))
                :score 0
                :ball-x 0
                :paddle-x 0}]
    (if (= :halted (-> state :computer :status))
      (:score state)
      (recur (render-turn state)))))


(defn solve [filename]
  (let [computer (ic/initialize-from-file filename)]
    [(part-1 computer)
     (part-2 computer)]))


(solve 13)
