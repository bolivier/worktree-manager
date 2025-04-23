(ns worktree-maker.git
  (:require
   [babashka.process :refer [*defaults* process shell]]
   [clojure.string :as str]))

(def worktree-dir "/Users/brandon/work/usecanopy-worktrees")
(def main-worktree-dir "/Users/brandon/work/usecanopy.com")

(alter-var-root
 (var *defaults*)
 (fn [defaults]
   (-> defaults
       (assoc :continue true)
       (assoc :dir main-worktree-dir)
       (assoc :out :stream))))

(defn join [& partials]
  (str/join " " partials))

(comment
  (def test-branch-name "patrick/CAN-6019-update-pull-terminology-to-submission"))

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
        result @(process (join "git branch " branch-name remote-branch-name))]
    (when-not (zero? (:exit result))
        (println (slurp (:err result))))))

(defn ensure-branch-exists [branch-name]
  (when-not (branch-exists? branch-name)
    (create-local-branch branch-name)))

(defn add-worktree
  "Note, this assumes that the branch exists locally. Be sure to create that
  branch first."
  [branch-name]
  (let [path (str worktree-dir "/" branch-name)
        command (join "git worktree add"
                      "-B " branch-name
                      path
                      branch-name)
        exit (:exit @command)]
    (when-not (zero? exit)
      (println (slurp (:err command))))))

(defn remove-worktree [branch-name]
  (let [dirname (str worktree-dir "/" branch-name)]
    (:out (shell {:out :string
                  :dir main-worktree-dir}
                 (str "git worktree remove " dirname)))))
