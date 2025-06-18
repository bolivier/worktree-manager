(ns worktree-manager.code-setup-test
  (:require [worktree-manager.code-setup :as sut]
            [expectations.clojure.test :refer [defexpect expect]]
            [clojure.test :refer [deftest is testing]]))

(defexpect job-types
  (expect :string (sut/get-job-type "foobar"))

  (expect :multi-dir (sut/get-job-type {:command "foo"
                                        :dirs ["."]}))

  (expect :single-dir (sut/get-job-type {:command "foo"
                                         :dir "."}))

  (expect :multi-dir (sut/get-job-type {:command "foo"
                                        :dirs []
                                        :dir "."})))

(defexpect parsing-jobs
  (expect {:command "foo"
           :dir "."}
          (sut/parse-job "foo"))

  (expect {:command "foo"
           :dir "."}
          (sut/parse-job {:command "foo"}))

  (expect {:command "foo"
           :dir "bar"}
          (sut/parse-job {:command "foo"
                          :dir "bar"}))

  (expect [{:command "foo"
            :dir "bar"}
           {:command "foo"
            :dir "baz"}]
          (sut/parse-job {:command "foo"
                          :dirs ["bar" "baz"]})))
