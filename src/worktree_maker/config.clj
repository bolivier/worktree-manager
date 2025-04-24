(ns worktree-maker.config
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [cheshire.core :as json]))

(defn get-config-path []
  (let [xdg-home (System/getenv "XDG_CONFIG_HOME")
        home     (System/getProperty "user.home")
        xdg-path (fs/path xdg-home "worktree-manager" "config.json")
        dot-path (fs/path home ".worktree-manager")]
    (cond
      (and xdg-home (fs/exists? xdg-path)) xdg-path
      (fs/exists? dot-path) dot-path
      :else nil)))

(defn read-config []
  (if-let [path (get-config-path)]
    (try
      (with-open [r (io/reader (str path))]
        (json/parse-stream r true))
      (catch Exception e
        (println "⚠️ Failed to read config:" (.getMessage e))
        (System/exit 1)))
    (do
      (println "⚠️ Could not find config at ~/.config/worktree-manager/config.json or ~/.worktree-manager")
      (println "   This file is required to set  `worktree-dir` and `main-worktree-dir`")
      (System/exit 1))))

(def config (read-config))
(def worktree-dir (:worktree-dir config))
(def main-worktree-dir (:main-worktree-dir config))
