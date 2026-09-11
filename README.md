# cloud-itonami-isco-7315

Open Occupation Blueprint for **ISCO-08 7315**: Glass Makers, Cutters, Grinders and Finishers.

This repository designs a forkable OSS business for a glass-workshop scheduling and logistics coordination practice: a glass-workshop scheduling and supply-coordination robot manages crew/task records under a governor-gated actor, so a glass-making, -cutting, -grinding and -finishing crew keeps its own operating records instead of renting a closed workforce-management SaaS.

**Maturity: `:implemented`.** `src/glasscoord/` implements the
`GlassCoordActor` as a `langgraph.graph/state-graph`
(`glasscoord.actor`) wired to a `Glass Workshop Coordination Advisor`
(`glasscoord.advisor`) and an independent `GlassCoordGovernor`
(`glasscoord.governor`), following the itonami actor pattern
(ADR-2607121000): `:intake -> :advise -> :govern -> :decide -+-> :commit
(:ok?) +-> :request-approval (:escalate?, human-in-the-loop interrupt)
+-> :hold (:hard?)`. 21 tests / 45 assertions green (`kbb -M:test`).
HARD invariants (always hold, never overridable): glassworker provenance,
workshop provenance, no-actuation (`:effect` must be `:propose`), a closed
op-allowlist (`:log-work-record`, `:schedule-crew-operation`,
`:flag-safety-concern`, `:coordinate-supply-order` — nothing else may
ever be proposed), and a permanent, unconditional block on any
proposal that would directly finalize a glass-forming/cutting-execution
decision (e.g. deciding to proceed with a specific glass-forming or
glass-cutting operation) or override a workshop safety officer's
judgment. Always-escalate paths (human sign-off regardless of
confidence, mapping this repo's Trust Controls in
[`docs/business-model.md`](docs/business-model.md)):
`:flag-safety-concern` (always) and `:coordinate-supply-order` above
the registered cost threshold.

## Robotics premise

All cloud-itonami verticals are designed on the premise that a **robot performs
the physical domain work**. Here a glass-workshop scheduling/logistics coordination robot performs crew scheduling, task/materials-usage/progress-record logging and glass-materials supply-order coordination for a glass-making, -cutting, -grinding and -finishing crew, under an actor that proposes actions and an independent **Glass Workshop Coordination Governor** that gates them. The governor never
dispatches hardware itself, never performs glass-forming or -cutting work on the workshop floor, and never finalizes a glass-forming/cutting-execution decision or overrides a workshop safety officer's judgment; `:high`/`:safety-critical` actions (such as a flagged burn-hazard/cut-hazard/furnace-condition concern, or an above-threshold supply order) require human sign-off. **This actor coordinates glass-workshop scheduling/logistics only — it never performs glass-forming, -cutting, -grinding or -finishing work itself.**

## Core Contract

```text
crew roster + workshop registration + safety-reporting policy
        |
        v
Glass Workshop Coordination Advisor -> GlassCoordGovernor -> log/schedule/coordinate, or human sign-off
        |
        v
robot actions (gated) + operating records + audit ledger
```

No automated advice can dispatch a robot action the governor refuses, finalize
a glass-forming/cutting-execution decision, override a workshop safety officer's
judgment, suppress an operating record, or disclose sensitive data
without governor approval and audit evidence.

## Capability layer

Resolves via [`kotoba-lang/occupation`](https://github.com/kotoba-lang/occupation)
(ISCO-08 `7315`). Required capabilities:

- :robotics
- :identity
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.
