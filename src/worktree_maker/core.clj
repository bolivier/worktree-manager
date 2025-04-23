(ns worktree-maker.core
  (:require
   [babashka.cli :refer [parse-args parse-opts]]
   [worktree-maker.result :as r :refer [->result]]
   [worktree-maker.git :as git]))

(defn checkout-worktree [branch-name]
  (let [result (->result (git/fetch-remote-branches)
                         (git/ensure-branch-exists branch-name)
                         (git/add-worktree branch-name))]
    (when (r/error? result)
      (println (r/message result)))))

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
