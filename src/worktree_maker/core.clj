(ns worktree-maker.core
  (:require
   [babashka.cli :refer [parse-args]]
   [worktree-maker.git :as git]
   [worktree-maker.process :refer [execute-processes]]))

(defn checkout-worktree [branch-name]
  (let [result (execute-processes
                 (git/fetch-remote-branches)
                 (git/ensure-branch-exists branch-name)
                 (git/add-worktree branch-name))]
    (println (:err result))))

(def cli-spec
  {:args [:command :branch]
   :args->opts [:command :branch]
   :options {}})

(defn -main [& args]
  (let [{:keys [branch command]} (:opts (parse-args args cli-spec))]
    (case command
      "create" (checkout-worktree branch)
      "remove" (git/remove-worktree branch)

      :else
      "Unsupported option.")))

(comment
  (def args '("create" "patrick/CAN-6019-update-pull-terminology-to-submission2")))
