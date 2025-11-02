(ns day18
  (:require aoc
            [clojure.data.priority-map :refer [priority-map]]))


(defrecord Grid [walls robots doors locks])


(defn- parse-data [data]
  (let [walls (aoc/grid->point-set data #{\#})
        robots (aoc/grid->point-map data #{\@})
        doors (aoc/grid->point-map data Character/isUpperCase)
        locks (aoc/grid->point-map data Character/isLowerCase)]
    (->Grid walls robots doors locks)))


(defn- path [pt1 pt2 {:keys [walls doors locks]}]
  (let [not-walls (complement walls)]
    (loop [queue (conj aoc/empty-queue [pt1 0 #{} []])
           seen #{}]
      (let [[curr dist drs lcks] (peek queue)]
        (cond
          (nil? curr) nil
          (= curr pt2) [dist drs lcks]
          :else (let [seen' (conj seen curr)
                      dist' (inc dist)
                      nbs (aoc/neighbours 4 curr (every-pred not-walls (complement seen)))
                      queue' (reduce (fn [q nb]
                                       (conj q [nb
                                                dist'
                                                (if-let [d (doors nb)]
                                                  (conj drs (Character/toLowerCase d))
                                                  drs)
                                                (if-let [l (and (not= nb pt2) (locks nb))]
                                                  (conj lcks l)
                                                  lcks)]))
                                     (pop queue)
                                     nbs)]
                  (recur queue' seen')))))))


(defn find-paths [{:keys [locks robots] :as data} [pt1 c1]]
  (let [paths (atom {})]
    (doseq [[pt2 c2] locks
            :while (not= pt1 pt2)
            :let [p (path pt1 pt2 data)]
            :when p]
      (swap! paths update c1 (fnil conj {}) [c2 p])
      (when (not (robots pt1))
        (swap! paths update c2 (fnil conj {}) [c1 p])))
    @paths))

(defn all-paths [{:keys [robots locks] :as data}]
  (->> (into locks robots)
       (pmap #(find-paths data %))
       (reduce (fn [acc r]
                 (reduce-kv (fn [acc k v]
                              (update acc k (fnil conj {}) v))
                            acc
                            r)))))


(defn- collect [start paths total-locks]
  (loop [queue (priority-map [start #{}] 0)]
    (let [[[curr seen] dist] (peek queue)]
      (if (= total-locks (count seen))
        dist
        (let [queue' (pop queue)
              nbs (for [[nb [d reqs along]] (paths curr)
                        :when (and (every? seen reqs) (not (seen nb)))
                        :let [dist' (+ dist d)
                              k [nb (conj (into seen along) nb)]]
                        :when (< dist' (queue' k 9999))]
                    [k dist'])]
          (recur (into queue' nbs)))))))


(defn- modify-grid [{:keys [walls robots doors locks]}]
  (let [robot-coord (ffirst robots)
        new-walls (aoc/neighbours 5 robot-coord)
        new-robots {(aoc/pt+ robot-coord [-1 -1]) \1
                    (aoc/pt+ robot-coord [ 1 -1]) \2
                    (aoc/pt+ robot-coord [-1  1]) \3
                    (aoc/pt+ robot-coord [ 1  1]) \4}]
    (->Grid (into walls new-walls) new-robots doors locks)))


(defn- collect-2 [start paths doors]
  (loop [queue (conj aoc/empty-queue [0 start doors])
         acc {}]
    (let [[dist curr seen] (peek queue)
          queue' (pop queue)
          nbs (for [[nb [d reqs along]] (paths curr)
                    :when (and (every? seen reqs)
                               (not (seen nb)))]
                [(+ dist d) nb (conj (into seen along) nb)])]
       (cond
         (empty? queue) acc
         (empty? nbs) (recur queue' (update acc [curr seen] (fnil min 99999) dist))
         :else (recur (into queue' nbs) acc)))))


(defn part-2 [paths total-locks]
  (loop [queue (priority-map [[\1 \2 \3 \4] #{}] 0)
         visited {}]
    (let [[[robots seen] dist] (peek queue)]
      (if (= total-locks (count seen))
        dist
        (let [new-stuff (for [[i robot] (map-indexed vector robots)
                              [[robot' seen'] dist'] (collect-2 robot paths seen)
                              :let [robots' (assoc robots i robot')
                                    dist'' (+ dist dist')]
                              :when (and (not= robot robot')
                                         (< dist'' (visited [robots' seen'] 99999)))]
                          [[robots' seen'] dist''])]
          (recur (into (pop queue) new-stuff)
                 (into visited new-stuff)))))))


(defn solve [filename]
  (let [data (parse-data (aoc/parse-input (aoc/read-file filename)))
        paths (all-paths data)
        pt2-data (modify-grid data)
        pt2-paths (all-paths pt2-data)
        total-locks (count (:locks data))]
    [(collect \@ paths total-locks)
     (part-2 pt2-paths total-locks)]))


(solve 18)
