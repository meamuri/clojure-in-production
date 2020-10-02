(ns basics)

(defn weird-stuff []
  (try
    (/ 1 0)
    (catch ArithmeticException e
      (println e))))

(comment
  (weird-stuff))
