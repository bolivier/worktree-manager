(ns worktree-maker.git
  (:require
   [babashka.process :as p :refer [*defaults*]]
   [worktree-maker.result :as r]
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
  (try
    @(p/process "git fetch origin")
    (r/ok)
    (catch Exception _
      (r/error "Failed to fetch origin."))))

(defn branch-exists? [branch-name]
  (let [command (str "git show-ref --verify --quiet refs/heads/" branch-name)]
    (->> (p/process command)
         deref
         :exit
         zero?)))

(defn create-local-branch [branch-name]
  (let [remote-branch-name (str "origin/" branch-name)
        result @(p/process (join "git branch " branch-name remote-branch-name))]
    (if-not (zero? (:exit result))
      (r/error (slurp (:err result)))
      (r/ok))))

(defn ensure-branch-exists [branch-name]
  (if (branch-exists? branch-name)
    (r/ok nil)
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

        process @(p/process command)

        {:keys [exit err]} process]
    (if (zero? exit)
      (r/ok)
      (r/error (slurp err)))))

(defn remove-worktree [branch-name]
  (let [dirname (str worktree-dir "/" branch-name)
        process (p/process (str "git worktree remove " dirname))
          {:keys [exit err]} @process]
    (if (zero? exit)
      (r/ok)
      (r/error (slurp err)))))
