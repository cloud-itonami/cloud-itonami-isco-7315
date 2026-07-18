(ns glasscoord.store
  "SSoT for the ISCO-08 7315 glass makers, cutters, grinders and
  finishers glass-workshop scheduling/logistics coordination actor
  (itonami actor pattern, ADR-2607121000 / CLAUDE.md Actors section;
  README's 'Robotics premise' — a glass-workshop scheduling/logistics
  coordination robot performs crew scheduling, task/materials-usage/
  progress record logging and glass-materials supply-order
  coordination for a glass-making, -cutting, -grinding and -finishing
  crew under this advisor/governor pair, which never dispatches
  hardware itself, never performs glass-forming or -cutting work
  itself, and never finalizes a glass-forming/cutting-execution
  decision or overrides a workshop safety officer's judgment — those
  remain the workshop safety officer's exclusive judgment). Modeled
  closely on cloud-itonami-isco-7211's foundrycoord.store (closest
  domain shape — hot-process/heat-exposure workshop safety pattern).

  Domain:

    glassworker — a registered glass-making/cutting/grinding/
                  finishing crew member (:glassworker-id, :name)
    workshop    — a registered glass workshop site {:workshop-id
                  :name :max-supply-cost number}. `:max-supply-cost`
                  is an informational registered ceiling used only to
                  decide whether a `:coordinate-supply-order`
                  proposal escalates to human sign-off (the governor
                  never blocks a within-threshold order outright; it
                  only decides commit vs. escalate).
    record      — a committed operating record (a logged task/
                  materials-usage/progress entry, a scheduled crew/
                  furnace operation, a flagged safety concern, or a
                  coordinated glass-materials supply order) — written
                  ONLY via commit-record!.
    ledger      — append-only audit trail, commit or hold.")

(defprotocol Store
  (glassworker [s glassworker-id])
  (workshop [s workshop-id])
  (records-of [s glassworker-id])
  (ledger [s])
  (register-glassworker! [s glassworker])
  (register-workshop! [s workshop])
  (commit-record! [s record])
  (append-ledger! [s fact]))

(defrecord MemStore [a]
  Store
  (glassworker [_ glassworker-id] (get-in @a [:glassworkers glassworker-id]))
  (workshop [_ workshop-id] (get-in @a [:workshops workshop-id]))
  (records-of [_ glassworker-id] (filter #(= glassworker-id (:glassworker-id %)) (:records @a)))
  (ledger [_] (:ledger @a))
  (register-glassworker! [s g]
    (swap! a assoc-in [:glassworkers (:glassworker-id g)] g) s)
  (register-workshop! [s w]
    (swap! a assoc-in [:workshops (:workshop-id w)] w) s)
  (commit-record! [s record]
    (swap! a update :records (fnil conj []) record) s)
  (append-ledger! [s fact]
    (swap! a update :ledger (fnil conj []) fact) s))

(defn mem-store
  ([] (mem-store {}))
  ([seed] (->MemStore (atom (merge {:glassworkers {} :workshops {} :records [] :ledger []}
                                    seed)))))
