(ns intcode-test
  (:require [intcode :as ic]
            [clojure.string :as str]
            [clojure.test :refer [deftest testing is]]
            day02 day05 day07 day09 day11 day13
            day15 day17 day19 day21 day23 day25))


(defn- run-program [instructions]
  (-> instructions
      ic/initialize-computer
      ic/run-until-halt
      :ram))

(deftest day02-examples
  (is (= [2,0,0,0,99] (take 5 (run-program "1,0,0,0,99"))))
  (is (= [2,3,0,6,99] (take 5 (run-program "2,3,0,3,99"))))
  (is (= [2,4,4,5,99,9801] (take 6 (run-program "2,4,4,5,99,0"))))
  (is (= [30,1,1,4,2,5,6,0,99] (take 9 (run-program "1,1,1,4,99,5,6,0,99")))))


(deftest day05-examples
  (testing "position mode, equal"
    (let [computer (ic/initialize-computer "3,9,8,9,10,9,4,9,99,-1,8")
          results  (mapv #(ic/in-run-result computer %) [-1 0 7 8 9])]
      (is (= [0 0 0 1 0] results))))
  (testing "position mode, less than"
    (let [computer (ic/initialize-computer "3,9,7,9,10,9,4,9,99,-1,8")
          results  (mapv #(ic/in-run-result computer %) [-1 0 7 8 9])]
      (is (= [1 1 1 0 0] results))))
  (testing "immediate mode, equal"
    (let [computer (ic/initialize-computer "3,3,1108,-1,8,3,4,3,99")
          results  (mapv #(ic/in-run-result computer %) [-1 0 7 8 9])]
      (is (= [0 0 0 1 0] results))))
  (testing "immediate mode, less than"
    (let [computer (ic/initialize-computer "3,3,1107,-1,8,3,4,3,99")
          results  (mapv #(ic/in-run-result computer %) [-1 0 7 8 9])]
      (is (= [1 1 1 0 0] results))))
  (testing "position mode, jump"
    (let [computer (ic/initialize-computer "3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9")
          results  (mapv #(ic/in-run-result computer %) [-9 -1 0 1 9])]
      (is (= [1 1 0 1 1] results))))
  (testing "immediate mode, jump"
    (let [computer (ic/initialize-computer "3,3,1105,-1,9,1101,0,0,12,4,12,99,1")
          results  (mapv #(ic/in-run-result computer %) [-9 -1 0 1 9])]
      (is (= [1 1 0 1 1] results))))
  (testing "larger example"
    (let [computer (ic/initialize-computer
                    (str/join "," [3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
                                   1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
                                   999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99]))
          results  (mapv #(ic/in-run-result computer %) [-1 0 7 8 9])]
      (is (= [999 999 999 1000 1001] results)))))



(deftest day09-examples
  (is (= [109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99]
         (-> "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99"
             ic/initialize-computer
             ic/run-until-halt
             :out-queue
             (->> (take 16)))))
  (is (= 1219070632396864
         (-> "1102,34915192,34915192,7,4,7,99,0" ic/initialize-computer (ic/in-run-result []))))
  (is (= 1125899906842624
         (-> "104,1125899906842624,99" ic/initialize-computer (ic/in-run-result [])))))



(deftest solutions
  (is (= [7594646 3376] (day02/solve 2)))
  (is (= [13294380 11460760] (day05/solve 5)))
  (is (= [67023 7818398] (day07/solve 7)))
  (is (= [3546494377 47253] (day09/solve 9)))
  (is (= [1909 (str/join
                "\n"
                ["   ██ █  █ ████ ████ █  █ █  █ ███  █  █"
                 "    █ █  █ █    █    █ █  █  █ █  █ █  █"
                 "    █ █  █ ███  ███  ██   ████ █  █ ████"
                 "    █ █  █ █    █    █ █  █  █ ███  █  █"
                 " █  █ █  █ █    █    █ █  █  █ █    █  █"
                 "  ██   ██  █    ████ █  █ █  █ █    █  █"])]
         (day11/solve 11)))
  (is (= [270 12535] (day13/solve 13)))
  (is (= [246 376] (day15/solve 15)))
  (is (= [10064 1197725] (day17/solve 17)))
  (is (= [189 7621042] (day19/solve)))
  (is (= [19357544 1144498646] (day21/solve 21)))
  (is (= [17740 12567] (day23/solve 23)))
  (is (= 11534338 (day25/solve 25))))
