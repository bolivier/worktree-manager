(ns worktree-maker.core
  (:require
   [babashka.cli :refer [parse-args]]
   [clojure.string :as str]
   [worktree-maker.git :as git]
   [worktree-maker.process :refer [execute-processes]]))

(defn checkout-worktree [branch-name]
  (let [result (execute-processes
                 (git/fetch-remote-branches)
                 (git/ensure-branch-exists branch-name)
                 (git/add-worktree branch-name))]
    (println (:err result))))

(def available-worktrees
  (remove
   #(= % git/main-worktree-dir)
   (let [process (execute-processes (git/list-worktrees))]
     (git/extract-worktree-paths (:out process)))))

(defn complete [args]
  (let [[prev curr] (take-last 2 (conj args ""))]
    (cond
      (or (= prev "remove")
          (= curr "remove"))
      (println (str/join "\n" available-worktrees))

      :else
      (println ""))))


(def cli-spec
  {:args [:command :branch]
   :args->opts [:command :branch]
   :options {}})

(defn -main [& args]
  (let [{:keys [branch command]} (:opts (parse-args args cli-spec))]
    (case command
      "_complete" (complete args)
      "create" (checkout-worktree branch)
      "remove" (git/remove-worktree branch)

      :else
      "Unsupported option.")))

(comment
  (def args '("create" "patrick/CAN-6019-update-pull-terminology-to-submission2")))
