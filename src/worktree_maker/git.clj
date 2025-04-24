(ns worktree-maker.git
  (:require
   [babashka.process :as p :refer [*defaults*]]
   [clojure.string :as str]))

(def worktree-dir "/Users/brandon/work/usecanopy-worktrees")
(def main-worktree-dir "/Users/brandon/work/usecanopy.com")

(alter-var-root
 (var *defaults*)
 (fn [defaults]
   (-> defaults
       (assoc :continue true)
       (assoc :dir main-worktree-dir)
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

(defn ensure-branch-exists [branch-name]
  {:command (branch-exists? branch-name)
   :on-failure (create-local-branch branch-name)})

(defn add-worktree [branch-name]
  (let [path (str worktree-dir "/" branch-name)]
   {:command (join "git worktree add"
               "-B " branch-name
               path
               branch-name)
    :pre-check (branch-exists? branch-name)}))

(defn remove-worktree [branch-name]
  (let [dirname (str worktree-dir "/" branch-name)]
    (str "git worktree remove " dirname)))

(comment
  (def test-branch-name "patrick/CAN-6019-update-pull-termidnology-to-submission"))
