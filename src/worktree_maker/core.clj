(ns worktree-maker.core
  (:require
   [babashka.cli :refer [parse-args]]
   [clojure.string :as str]
   [worktree-maker.code-setup  :as code-setup]
   [worktree-maker.git :as git]
   [worktree-maker.process :refer [execute-processes]]))

(defn checkout-worktree [branch-name]
  (let [result (execute-processes
                (git/fetch-remote-branches)
                (git/ensure-branch-exists branch-name)
                (git/add-worktree branch-name))]
    (if (zero? (:exit @result))
      (code-setup/npm-ci branch-name)
      (println (:err result)))))

(def available-worktrees
  (remove
   #(= % git/main-worktree-dir)
   (let [process (execute-processes (git/list-worktrees))]
     (git/extract-worktree-paths (:out process)))))

(def available-branches
  (:out (execute-processes (git/list-branches))))

(defn complete [args]
  (let [[prev curr] (take-last 2 (conj args ""))]
    (cond
      (or (= prev "remove")
          (= curr "remove"))
      (println (str/join "\n" available-worktrees))

      (or (= prev "create")
          (= curr "create"))
      (println available-branches)

      :else
      (println ""))))

(defn delete-worktree [branch-name]
  (let [result (execute-processes (git/remove-worktree branch-name))]
    (when-not (zero? (:exit result))
      (println (:err result)))))

(def cli-spec
  {:args [:command :branch]
   :args->opts [:command :branch :help]
   :alias {:h :help}
   :options {:help {:desc "Show this help menu"
                    :coerce :boolean}}})

(defn print-help-menu []
  (println "Usage: wtm <command> [options]")
  (println)
  (println "Commands:")
  (println "  create <branch>     Create a new worktree for the given branch")
  (println "  remove <wt-path>    Remove an existing worktree"))

(defn -main [& args]
  (let [{:keys [branch command help]} (:opts (parse-args args cli-spec))]
    (cond
      help (print-help-menu)

      (= command "_complete") (complete args)
      (= command "create") (checkout-worktree branch)
      (= command "remove") (delete-worktree branch)


      :else
      (println "Unsupported option"))))

(comment
  (def args '("create" "patrick/CAN-6019-update-pull-terminology-to-submission2")))
