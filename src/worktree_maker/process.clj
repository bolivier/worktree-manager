(ns worktree-maker.process
  "This is for working with processes.

  A `process` here is a result from calling `babashka.process/process`. It
  conceptually wraps a subprocess and has keys like `:exit` `:err` and `:out`.

  NOTE: `nil` is considered a valid process. It is a null process and anything
  that handles processes, needs to handle `nil`. This should mostly be
  automatic."
  (:require [babashka.process :as p]))

(defn ok? [process]
  (or
    (nil? process)
    (zero? (:exit @process))))

(defprotocol ExecuteCommand
  (execute [this] "Run the command with pre-checks"))

(defn execute-processes
  "Executes the processes. Returns nil on success, returns the final failed
  process on failure.

  Options for processes are
  - `:command` the command to run
  - `:pre-check` a check to run that will short-circuit
  - `:on-failure` a process to replace the original one if `:command` fails
  "
  [& commands]
  (when-let [command (first commands)]
    (let [process (execute command)
          {:keys [exit]} process]
      (if (zero? exit)
        (if (empty? (rest commands))
          process
          (recur (rest commands)))
        (if-let [on-failure (:on-failure command)]
          (recur (conj (rest commands) on-failure))
          process)))))

(extend-protocol ExecuteCommand
  String
  (execute [string] @(p/process string))

  clojure.lang.IPersistentMap
  (execute [{:keys [pre-check command]}]
    (let [pre-check (execute-processes pre-check)]
      (if (ok? pre-check)
        (execute command)
        pre-check))))
