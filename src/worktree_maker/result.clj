(ns worktree-maker.result)

(defn ok? [result]
  (try
    (= ::ok (first result))
    (catch Exception _
      false)))

(def error? (complement ok?))

(defn ok
  ([] (ok nil))
  ([value]
   (case (get value 0 value)
     ::ok    value
     ::error value

     [::ok value])))

(defn error [message]
  [::error message])

(defn message [error-result]
  (second error-result))

(defmacro ->result
  {:style/indent 1}
  [& forms]
  (letfn [(step [forms]
            (let [s (gensym "step")]
              (if (= 1 (count forms))
                `(let [~s ~(first forms)]
                   ~s)
                `(let [~s ~(first forms)]
                   (if (r/ok? ~s)
                     ~(step (rest forms))
                     ~s)))))]
    (step forms)))
