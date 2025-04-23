(ns worktree-maker.core
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [babashka.process :refer [process shell *defaults*]]
   [babashka.fs :as fs]))

(def worktree-dir "/Users/brandon/work/usecanopy-worktrees")
(def main-worktree-dir "/Users/brandon/work/usecanopy.com")

(alter-var-root
 (var *defaults*)
 (fn [defaults]
   (-> defaults
       (assoc :continue true)
       (assoc :dir main-worktree-dir)
       (assoc :out :stream))))

(defn command [& command-partials]
  (str/join " " command-partials))
(def space-join command)

(def test-branch-name "patrick/CAN-6019-update-pull-terminology-to-submission")

(defn fetch-remote-branches []
  @(process "git fetch origin"))

(defn branch-exists? [branch-name]
  (let [command (str "git show-ref --verify --quiet refs/heads/" branch-name)]
    (->> (process command)
         deref
         :exit
         zero?)))

(defn create-local-branch [branch-name]
  (let [remote-branch-name (str "origin/" branch-name)
        result @(process (command "git branch " branch-name remote-branch-name))]
    (when-not (zero? (:exit result))
        (println (slurp (:err result))))))

(defn ensure-branch-exists [branch-name]
  (when-not (branch-exists? branch-name)
    (create-local-branch branch-name)))

(defn checkout-worktree
  "Note, this assumes that the branch exists locally. Be sure to create that
  branch first."
  [branch-name]
  (let [path (str worktree-dir "/" branch-name)
        command (space-join "git worktree add"
                            "-B " branch-name
                            path
                            branch-name)]
    (-> (process command)
        deref
        :out
        :slurp)))

(defn remove-worktree [branch-name]
  (let [dirname (str worktree-dir "/" branch-name)]
    (:out (shell {:out :string
                  :dir main-worktree-dir}
                 (str "git worktree remove " dirname)))))
