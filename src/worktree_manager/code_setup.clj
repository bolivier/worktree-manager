(ns worktree-manager.code-setup
  (:require
   [worktree-manager.config :refer [config]]
   [worktree-manager.git :as git]
   [babashka.fs :as fs]
   [babashka.process :refer [shell]]))

(defn get-job-type
  "Get the type of this job.

  Valid job types are strings, maps with a :dir key to run in a single
  directory, or maps with a :dirs key that run the same command in multiple
  directories."
  [job]
  (cond
    (string? job)       :string
    (some? (:dirs job)) :multi-dir
    :else               :single-dir))

(defmulti validate-job!
  "Validates a job type.

  Either returns true, or prints an error to stdout and exits with code 1."
  get-job-type)

(defmethod validate-job! :string [job]
  true)

(defmethod validate-job! :multi-dir [job]
  (cond
    (not (every? (fn [dir] (string? dir))
                 (:dirs job)))
    (do (println "Config error: setup jobs must have string directories")
        (System/exit 1))

    (not (string? (:command job)))
    (do (println "Config error: Command must be a string")
        (System/exit 1))

    :else
    true))

(defmethod validate-job! :single-dir [job]
  (cond
    (not (string?  (:dir job)))
    (do (println "Config error: setup jobs must have string directories")
        (System/exit 1))

    (not (string? (:command job)))
    (do (println "Config error: Command must be a string")
        (System/exit 1))

    :else
    true))

(defmethod validate-job! :default [job]
  (println "Config error: saw unknown job type: " job)
  (System/exit 1))

(defmulti parse-job get-job-type)

(defmethod parse-job :string [command]
  {:command command
   :dir "."})

(defmethod parse-job :multi-dir [{:keys [dirs command]}]
  (mapv (fn [dir]
          {:command command
           :dir dir})
        dirs))

(defmethod parse-job :default [job] job)

(defn parse-jobs [config-jobs]
  (flatten (map parse-job config-jobs)))

(defn run-jobs [branch-name]
  (let [workspace-root (git/branch-name->path branch-name)
        futures (for [job (parse-jobs (:jobs config))
                      :let [{:keys [command dir]} job]]
                  (future
                    (shell {:dir (fs/path workspace-root dir)} command)))]
    (mapv deref futures)))
