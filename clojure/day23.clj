(ns day23
  (:require [intcode :as ic]))


(defn- create-network [computer]
  (into {}
        (for [i (range 50)]
          [i (-> computer
                 (ic/send-to-in-queue i)
                 ic/run-until-halt)])))


(defn- send-packets [network i]
  (loop [[dest x y & queue'] (:out-queue (network i))
         network network]
    (cond
      (nil? dest) (assoc-in network [i :out-queue] [])
      (= 255 dest) (recur queue' (assoc network 255 [x y]))
      :else (recur queue' (update network dest ic/send-to-in-queue [x y])))))

(defn- run-comp [network i]
  (cond-> network
    (empty? (:in-queue (network i))) (update i ic/send-to-in-queue -1)
    true (update i ic/run-until-halt)
    true (send-packets i)))

(defn- run-network [network]
  (reduce (fn [acc i]
            (run-comp acc i))
          network
          (range 50)))


(defn- part-1 [network]
  (loop [network network]
    (if-let [[_ y] (network 255)]
      y
      (recur (run-network network)))))


(defn- part-2 [network]
  (loop [network network
         prev-y nil]
    (let [network' (run-network network)]
      (if (every? (comp empty? :in-queue second) network')
        (let [[x y] (network' 255)]
          (if (= y prev-y)
            y
            (recur (update network' 0 ic/send-to-in-queue [x y]) y)))
        (recur network' prev-y)))))


(defn solve [filename]
  (let [network (create-network (ic/initialize-from-file filename))]
    [(part-1 network) (part-2 network)]))


(solve 23)
