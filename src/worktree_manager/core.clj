(ns worktree-manager.core
  (:require
   [babashka.cli :refer [parse-args]]
   [clojure.string :as str]
   [worktree-manager.config :as config]
   [worktree-manager.code-setup  :as code-setup]
   [worktree-manager.git :as git]
   [worktree-manager.process :refer [execute-processes]]))

(defn checkout-worktree [branch-name]
  (let [result (execute-processes (git/fetch-remote-branches)
                                  (git/ensure-branch-exists branch-name)
                                  (git/add-worktree branch-name))]
    (if (zero? (:exit @result))
      (try
       (code-setup/npm-ci branch-name)
       (catch Exception _
         "Could not install dependencies.  Please handle manually.  Sorry for the inconvenience."))
      (println (:err result)))))

(def available-worktrees
  (let [process (execute-processes (git/list-worktrees))
        worktrees (git/extract-worktree-paths (:out process))]
    (->> worktrees
         (remove #(= % config/main-worktree-dir))
         (str/join "\n"))))

(def available-branches
  (:out (execute-processes (git/list-branches))))

(defn complete [args]
  (let [[prev curr] (take-last 2 (conj args ""))]
    (cond
      (or (= prev "remove")
          (= curr "remove"))
      (println available-worktrees)

      (or (= prev "create")
          (= curr "create"))
      (println available-branches)


      (= curr "wtm")
      (println "create\nremove")

      :else
      (println ""))))

(defn delete-worktree [branch-name]
  (let [result (execute-processes (git/remove-worktree branch-name))]
    (when-not (zero? (:exit result))
      (println (:err result)))))

(def cli-spec
  {:args [:command :branch]
   :args->opts [:command :branch]
   :alias {:h :help}
   :options {:help {:desc "Show this help menu"
                    :coerce :boolean}}})

(defn print-help-menu []
  (println "Usage: wtm <command> [options]")
  (println)
  (println "Commands:")
  (println "  create <branch>     Create a new worktree for the given branch")
  (println "  remove <wt-path>    Remove an existing worktree")
  (println)
  (println "Options:")
  (println "  --help              Print this help menu"))

(defn -main [& args]
  (let [{:keys [branch command help]} (:opts (parse-args args cli-spec))]
    (cond
      ;; This must be first to make completions work correctly
      (= command "_complete") (complete args)

      help (print-help-menu)
      (= command "create") (checkout-worktree branch)
      (= command "remove") (delete-worktree branch)

      :else
      (println "Unsupported option"))))
