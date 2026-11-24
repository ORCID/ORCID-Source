---
sessionId: session-261104-091437-1a2m
---

# Requirements

### Goal
Improve UI response times by testing larger caches for the highest-churn aliases, without changing freshness, privacy, or behavior in other deployments.

### Scope
- Tune `fundings`, `peer-reviews`, and the six `public-*` person-detail caches shown in the production snapshot.
- Use existing Jolokia, filesystem, JVM, request-latency, and database metrics; do not add a monitoring service or JMX bridge.
- Leave `biography`, `emails`, unrelated caches, programmatic caches, and other deployments unchanged initially. Their metrics remain useful comparison points.
- Treat the supplied cumulative counters and allocated disk files as clues, not proof that capacity is the cause or that allocated space equals live occupancy.

# Technical Design

### Current behavior
`orcid-core/src/main/resources/ehcache_orcid-web.xml` gives `fundings` and `peer-reviews` the `activitiesTemplate` (4,500 heap entries, 128 MB disk) and the six public person-detail aliases the `bioTemplate` (4,500 entries, 64 MB disk). Both use a 15-minute time-to-idle and `IdentityCopier`. `PeerReviewDaoImpl`, `ProfileFundingDaoImpl`, and person-detail DAOs cache by ORCID plus profile last-modified time, so record changes create new keys; old versions remain until expiry or eviction. `OrgAffiliationRelationEntityRetriver` belongs to the separate programmatic affiliation cache and is not part of this tuning.

### Proposed change
In the UI XML only, override resources on each affected alias while retaining its template, identity copier, 15-minute expiry, entry-sized heap, and nonpersistent disk:
- `fundings` and `peer-reviews`: trial 9,000 entries and 256 MB disk each.
- `public-external-identifiers`, `public-researcher-urls`, `public-keywords`, `public-address`, `public-other-names`, and `public-emails`: trial 9,000 entries and 128 MB disk each.

These are experimental starting limits, not proven optimal values. They add up to 640 MB of configured disk capacity and 36,000 possible heap entries per UI instance; actual heap consumption is not capped in MB. Do not switch entity caches to byte-sized heaps because object sizing can traverse detached Hibernate collections. Preserve all existing changes in `orcid-core-cache-config.xml`.

### Rollout and measurement
Document an opt-in canary: establish free disk and JVM heap/GC headroom before deploying; compare a warmed-up UI instance with an unchanged, similarly loaded instance over representative traffic and at least one full daily cycle. Calculate interval deltas for hits, misses, puts and evictions per cache; compare request p95, error rate, database read load, heap/GC and allocated disk. Do not infer per-tier occupancy or eviction from JCache totals. Proceed only if latency or database reads improve without unacceptable resource or error regressions; otherwise revert the UI XML and restart the canary. Nonpersistent caches restart cold; exclude warm-up from comparison. Do not log ORCIDs, emails or cache values.

### Files
- Modify `orcid-core/src/main/resources/ehcache_orcid-web.xml`.
- Add `orcid-core/src/test/java/org/orcid/core/utils/UiEhcacheConfigTest.java` for effective configuration and runtime checks.
- Add `orcid-core/CACHE_TUNING.md` with baseline, headroom, canary, comparison and rollback instructions.

# Testing

### Automated checks
- Load the actual UI XML with a temporary persistence directory and inspect effective resource pools, 15-minute expiry and copier configuration for all eight aliases; confirm unrelated aliases and another deployment's XML remain unchanged.
- Exercise representative activity and public-detail caches with real Ehcache, including a detached lazy collection, cache hit, eviction/disk read, and expiry; do not assume eviction of a particular key without verifying tier movement.
- Check versioned keys return fresh results after last-modified changes, and public/private cache aliases remain distinct. Preserve the existing copier-configuration tests.
- Run targeted core tests using the Maven reactor and IDE build where feasible; report unavailable dependencies or infrastructure rather than claiming tests passed.

### Canary acceptance
Compare interval metrics after warm-up over representative matched traffic. Favor a demonstrable p95 or database-read improvement with no increased errors or unsafe disk, heap or GC pressure; roll back if the improvement is absent or resource limits are breached.

# Delivery Steps

### ✓ Step 1: Tune activity-cache resources
The UI activity caches have individually bounded trial capacities without changing their expiry or copy behavior.

- Override `fundings` and `peer-reviews` in `ehcache_orcid-web.xml` to 9,000 heap entries and 256 MB nonpersistent disk each.
- Add effective-configuration and real-cache regression checks in `UiEhcacheConfigTest`, including detached lazy collections and versioned-key freshness.

### ✓ Step 2: Tune public person-detail caches
The six high-churn public person-detail caches have larger UI-only trial capacities.

- Override each named `public-*` alias to 9,000 heap entries and 128 MB nonpersistent disk while retaining `bioTemplate` behavior.
- Extend tests to verify all six aliases, public/private separation, unchanged unrelated caches, expiry and disk-tier retrieval.
- Run targeted core tests and build validation when dependencies are available.

### ✓ Step 3: Document the controlled canary
Operators have a repeatable, resource-gated way to decide whether the trial improves response time.

- Add `orcid-core/CACHE_TUNING.md` with headroom checks, warm-up, matched baseline and interval-based Jolokia calculations.
- Document latency, database, JVM and filesystem comparisons, including the limits of JCache and allocated-file metrics.
- Define rollback and restart instructions; keep production rollout conditional on observed benefit and safe resource usage.