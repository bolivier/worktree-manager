(ns worktree-manager.git
  (:require
   [babashka.process :as p :refer [*defaults*]]
   [worktree-manager.config :as config]
   [clojure.string :as str]))

(alter-var-root
 (var *defaults*)
 (fn [defaults]
   (-> defaults
       (assoc :continue true)
       (assoc :dir config/main-worktree-dir)
       (assoc :out :string)
       (assoc :err :string))))

(defn join [& partials]
  (str/join " " partials))

(defn fetch-remote-branches []
  "git fetch origin")

(defn branch-exists? [branch-name]
  (str "git show-ref --verify --quiet refs/heads/" branch-name))

(defn create-local-branch [branch-name]
  (let [remote-branch-name (str "origin/" branch-name)]
    (join "git branch " branch-name remote-branch-name)))

(defn branch-name->path [branch-name]
  (str config/worktree-dir "/" branch-name))

(defn ensure-branch-exists [branch-name]
  {:command (branch-exists? branch-name)
   :on-failure (create-local-branch branch-name)})

(defn add-worktree [branch-name]
  (let [path (branch-name->path branch-name)]
   {:command (join "git worktree add"
               "-B " branch-name
               path
               branch-name)
    :pre-check (branch-exists? branch-name)}))

(defn remove-worktree [path]
  (str "git worktree remove --force " path))

(defn list-worktrees []
  "git worktree list")

(defn extract-worktree-paths
  "Takes the output from `git worktree list` and splits out the paths of the
  worktrees"
  [worktree-cmd-output]
  (->> worktree-cmd-output
    str/split-lines
    (map #(first (str/split % #" ")))))

(defn list-branches []
  "git for-each-ref --format='%(refname:short)' refs/heads/")
