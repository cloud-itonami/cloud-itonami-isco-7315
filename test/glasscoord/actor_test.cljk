(ns glasscoord.actor-test
  (:require [clojure.test :refer [deftest is testing]]
            [glasscoord.actor :as actor]
            [glasscoord.store :as store]))

(defn- fresh-store []
  (let [st (store/mem-store)]
    (store/register-glassworker! st {:glassworker-id "glassworker-1" :name "Kobo Yamada"})
    (store/register-workshop! st {:workshop-id "W-1" :name "Kobo Glassworks" :max-supply-cost 2000})
    st))

(deftest commits-a-registered-work-log
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        request {:glassworker-id "glassworker-1" :op :log-work-record :stake :low
                  :workshop-id "W-1" :task "glass-cutting-batch progress log"}
        result (actor/run-request! graph request {} "thread-1")]
    (is (= :done (:status result)))
    (is (some? (get-in result [:state :record])))
    (is (= 1 (count (store/records-of st "glassworker-1"))))))

(deftest holds-an-unregistered-workshop-proposal
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        request {:glassworker-id "glassworker-1" :op :log-work-record :stake :low
                  :workshop-id "W-ghost" :task "glass-cutting-batch progress log"}
        result (actor/run-request! graph request {} "thread-2")]
    (is (= :hold (:disposition (:state result))))
    (is (empty? (store/records-of st "glassworker-1")))))

(deftest interrupts-then-approves-safety-concern-on-human-approval
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        request {:glassworker-id "glassworker-1" :op :flag-safety-concern :stake :low
                  :workshop-id "W-1" :hazard-type :burn-hazard-risk}
        interrupted (actor/run-request! graph request {} "thread-3")]
    (is (= :interrupted (:status interrupted)))
    (is (empty? (store/records-of st "glassworker-1")))
    (let [resumed (actor/approve! graph "thread-3")]
      (is (= :done (:status resumed)))
      (is (= 1 (count (store/records-of st "glassworker-1")))))))

(deftest holds-a-scope-excluded-op-even-at-high-confidence
  (testing "an actor run can never commit a proposal that would finalize a glass-forming/cutting-execution decision, regardless of disposition path"
    (let [st (fresh-store)
          graph (actor/build-graph {:store st})
          request {:glassworker-id "glassworker-1" :op :finalize-glass-forming-decision :stake :low
                    :workshop-id "W-1" :task "glass-forming decision"}
          result (actor/run-request! graph request {} "thread-4")]
      (is (= :done (:status result)))
      (is (= :hold (:disposition (:state result))))
      (is (empty? (store/records-of st "glassworker-1"))))))
