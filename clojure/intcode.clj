(ns intcode
  (:require
   aoc
   [clojure.math :refer [pow]]
   [clojure.string :as str]))


(def ^:dynamic *debug* false)
(def debug-file "intcode.log")
(def debug-length 500)
(def debug-cnt (atom 0))

(defn- quoted-val [x]
  `(str (quote ~x) " -> " ~x \newline))

(defmacro dbg [& xs]
 `(str ~@(map quoted-val xs)))

(defmacro dbglet
  {:clj-kondo/lint-as 'clojure.core/let}
  [bindings & body]
  (let [bndgs (map first (partition 2 bindings))]
   `(let ~bindings
      (when *debug*
        (tap> (dbg ~@bndgs)))
      ~@body)))

(defn- log [xs]
  (spit debug-file (str xs \newline) :append true))

(comment
  (add-tap #'log)
  (remove-tap #'log))






(defn- bool->int [b]
  (if b 1 0))

(defn- read-param [param {:keys [ram ip rp]}]
  (let [digit (long (pow 10 (inc param)))
        mode  (case (-> (ram ip)
                        (quot digit)
                        (mod 10))
                0 :position-mode
                1 :immediate-mode
                2 :relative-mode)
        loc   (+ ip param)]
    (case mode
      :position-mode  (ram loc)
      :immediate-mode loc
      :relative-mode  (+ rp (ram loc)))))

(defn- assoc-ram [computer loc v]
  (assoc-in computer [:ram loc] v))


(defn- binary-op [{:keys [ram] :as computer} opcode]
  (tap> "binary op")
  (dbglet [[noun verb dest] (map #(read-param % computer) [1 2 3])
           operation        (case opcode
                              1 +
                              2 *
                              7 (comp bool->int <)
                              8 (comp bool->int =))
           result           (operation (ram noun) (ram verb))]
    (-> computer
        (assoc-ram dest result)
        (update :ip + 4))))

(defn- unary-op [{:keys [ram ip] :as computer} opcode]
  (tap> "unary op")
  (dbglet [[noun verb] (map #(read-param % computer) [1 2])
           operation   (case opcode
                         5 (comp not zero?)
                         6 zero?)
           result      (operation (ram noun))
           ip'         (if result (ram verb) (+ ip 3))]
    (assoc computer :ip ip')))

(defn- read-from-in-queue [computer]
  (tap> "read from in queue")
  (if-let [x (peek (:in-queue computer))]
    (dbglet [noun (read-param 1 computer)]
      (-> computer
          (assoc-ram noun x)
          (update :in-queue pop)
          (assoc :status :running)
          (update :ip + 2)))
    (assoc computer :status :waiting)))

(defn- put-to-out-queue [computer]
  (tap> "put to out queue")
  (dbglet [noun (read-param 1 computer)
           result ((:ram computer) noun)]
    (-> computer
        (update :out-queue conj result)
        (update :ip + 2))))

(defn- adjust-relative-base [computer]
  (let [noun (read-param 1 computer)
        result ((:ram computer) noun)]
    (-> computer
        (update :rp + result)
        (update :ip + 2))))

(defn- halt [computer]
  (assoc computer :status :halted))

(defn- execute-opcode [{:keys [ram ip] :as computer}]
  (let [opcode (mod (ram ip) 100)]
    (when *debug*
      (if (>= @debug-cnt debug-length)
        (throw (Exception. (format "IntCode: evaluation stopped after %d iterations" @debug-cnt)))
        (do
          (swap! debug-cnt inc)
          (tap> "execute opcode")
          (tap> (dbg ip (ram ip) opcode)))))
    (case opcode
      (1 2 7 8) (binary-op computer opcode)
      (5 6)     (unary-op computer opcode)
      3         (read-from-in-queue computer)
      4         (put-to-out-queue computer)
      9         (adjust-relative-base computer)
      99        (halt computer)
      (throw (Exception. "IntCode: invalid opcode")))))





(defn modify-ram [computer [pos v]]
  (assoc-ram computer pos v))

(defn send-to-in-queue [computer input]
  (let [f (if (sequential? input) into conj)]
    (-> computer
        (update :in-queue f input)
        (assoc :status :running))))

(defn pop-out-queue [computer]
  (tap> "pop out queue")
  (dbglet [v (peek (:out-queue computer))
           _q (pop (:out-queue computer))]
    (-> computer
        (assoc :output v)
        (update :out-queue pop))))

(defn run-until-halt [computer]
  (if (#{:halted :waiting} (:status computer))
    computer
    (recur (execute-opcode computer))))

(defn in-run-out [computer input]
  (-> computer
      (send-to-in-queue input)
      run-until-halt
      pop-out-queue))

(defn in-run-result [computer input]
  (-> computer
      (in-run-out input)
      :output))

(defn load-instructions
  ([input] (load-instructions input 4096))
  ([input size]
   (let [instrs (aoc/parse-input-line input :ints)]
     (into instrs (repeat (- size (count instrs)) 0)))))

(defn to-machine-code [input]
  (-> (str/join "\n" input)
      (str "\n")
      (->> (mapv int))))

(defn initialize-computer
  ([input] (initialize-computer input 4096))
  ([input ram-size]
   (when *debug*
     (reset! debug-cnt 0)
     (spit debug-file ""))
   {:ram       (load-instructions input ram-size)
    :in-queue  aoc/empty-queue
    :out-queue []
    :output    nil
    :status    :running
    :ip        0
    :rp        0}))

(defn initialize-from-file
  ([filename] (initialize-computer (aoc/read-file filename)))
  ([filename ram-size]
   (initialize-computer (aoc/read-file filename) ram-size)))
