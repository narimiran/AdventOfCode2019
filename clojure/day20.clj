(ns day20
  (:require aoc))


(defn- at [grid i j]
  ((grid i) j))

(defn- between [grid i1 i2 j1 j2]
  (str (at grid i1 j1) (at grid i2 j2)))

(defn- find-portals [grid]
  (let [portal-locations (atom {})
        portal-connections (atom {})
        ends (atom {})
        h (count grid)
        w (count (first grid))]
    (doseq [i (range 2 (- h 2))
            j (range 2 (- w 2))
            :when (= \. (at grid i j))
            nb [(between grid (- i 2) (- i 1) j j)
                (between grid (+ i 1) (+ i 2) j j)
                (between grid i i (- j 2) (- j 1))
                (between grid i i (+ j 1) (+ j 2))]
            :when (re-matches #"[A-Z]+" nb)]
      (when (#{"AA" "ZZ"} nb)
        (swap! ends assoc nb [i j]))
      (if-let [conn (@portal-locations nb)]
        (do
          (swap! portal-connections assoc [i j] conn)
          (swap! portal-connections assoc conn [i j]))
        (swap! portal-locations assoc nb [i j])))
    [@ends @portal-connections]))


(defn- make-conn-map [conns]
  (reduce (fn [acc [[pt1 pt2] dist]]
            (-> acc
                (update pt1 conj [pt2 dist])
                (update pt2 conj [pt1 dist])))
          {}
          conns))

(defn- find-connections [grid portals]
  (->> (for [start portals
             end portals
             :while (not= start end)]
         (loop [queue (conj aoc/empty-queue [start 0])
                seen #{start}]
           (let [[curr dist] (peek queue)]
             (cond
               (nil? curr) nil
               (= curr end) [[start end] dist]
               :else (let [nbs (aoc/neighbours 4 curr (fn [pt]
                                                        (and (not (seen pt))
                                                             (= \. (apply at grid pt)))))
                           nbs+dist (map (fn [pt] [pt (inc dist)]) nbs)]
                         (recur (into (pop queue) nbs+dist)
                                (into seen nbs)))))))
       (remove nil?)
       make-conn-map))


(defn- traverse-1 [ends conns jumps]
  (let [start (ends "AA")
        end (ends "ZZ")]
    (loop [queue (conj aoc/empty-queue [start 0])
           seen #{start}]
      (let [[curr dist] (peek queue)]
        (if (= end curr)
          dist
          (let [nbs (for [[pt d] (conns curr)
                            :let [dist' (+ dist d 1)]
                            :when (not (seen pt))]
                        (if (= end pt)
                          [end (dec dist')]
                          [(jumps pt) dist']))]
              (recur (into (pop queue) nbs)
                     (into seen (map first nbs)))))))))


(defn- traverse-2 [h w ends conns jumps]
  (let [start       (ends "AA")
        end         (ends "ZZ")
        depth-limit 25
        outer-i     #{2 (- h 3)}
        outer-j     #{2 (- w 3)}]
    (loop [queue (conj aoc/empty-queue [start 0 0])
           seen  #{[start 0]}]
      (let [[current dist depth] (peek queue)]
        (if (and (zero? depth) (= end current))
          dist
          (let [nbs (->> (for [[pt d] (conns current)
                               :let [dist' (+ dist d 1)
                                     outer? (or (outer-i (first pt))
                                                (outer-j (second pt)))
                                     jmp (jumps pt)]
                               :when (not (seen [pt depth]))]
                           (cond
                             (= end pt) [pt (dec dist') depth]
                             (and outer? (pos? depth)) [jmp dist' (dec depth)]
                             (and (not outer?) (< depth depth-limit)) [jmp dist' (inc depth)]))
                         (remove nil?))]
            (recur (into (pop queue) nbs)
                   (into seen (map (fn [[pt _ dpth]] [pt dpth]) nbs)))))))))


(defn solve [filename]
  (let [data (aoc/parse-input (aoc/read-file filename) :chars)
        h (count data)
        w (count (first data))
        [ends jumps] (find-portals data)
        connections (find-connections data (into (keys jumps) (vals ends)))]
    [(traverse-1 ends connections jumps)
     (traverse-2 h w ends connections jumps)]))


(solve 20)
