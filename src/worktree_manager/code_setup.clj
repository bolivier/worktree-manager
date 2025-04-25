(ns worktree-manager.code-setup
  (:require
   [worktree-manager.git :as git]
   [babashka.process :refer [shell]]))

(def npm-install-command
  "This i s preferred to the normal `npm i` or `npm ci` for for performance
  reasons."
  "npm install --prefer-offline --no-audit")

(defn npm-ci [branch-name]
  (let [path (git/branch-name->path branch-name)
        futures (for [dir ["" "/dashboard" "/react-app"]]
                  (future
                    (shell {:dir (str path dir)} npm-install-command)))]
    (mapv deref futures)))
