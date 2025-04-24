(ns worktree-maker.code-setup
  (:require
   [worktree-maker.git :as git]
   [babashka.process :refer [shell]]))

(defn npm-ci [branch-name]
  (let [path (git/branch-name->path branch-name)
        futures (for [dir [""
                            "/dashboard"
                            "/react-app"]]
                  (future
                    (shell {:dir (str path dir)} "npm ci")))]
    (mapv deref futures)))
