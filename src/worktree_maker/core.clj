(ns worktree-maker.core
  (:require
   [worktree-maker.git :as git]))

(defn checkout-worktree [branch-name]
  (git/fetch-remote-branches)
  (git/ensure-branch-exists branch-name)
  (git/add-worktree branch-name))

(defn -main [args]
  (prn args))
