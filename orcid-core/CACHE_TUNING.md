# UI cache capacity trial

## Purpose and scope

This is an opt-in canary for the UI cache configuration in `ehcache_orcid-web.xml`. The trial increases capacity for the highest-churn aliases from the supplied production snapshot; the sizes are experimental starting limits, not known optimal values. Do not roll the change to production broadly until the canary shows an improvement without unacceptable resource or error regressions.

| Cache aliases | Current heap / disk per cache | Trial heap / disk per cache |
| --- | ---: | ---: |
| `fundings`, `peer-reviews` | 4,500 entries / 128 MB | 9,000 entries / 256 MB |
| `public-external-identifiers`, `public-researcher-urls`, `public-keywords`, `public-address`, `public-other-names`, `public-emails` | 4,500 entries / 64 MB | 9,000 entries / 128 MB |

Both templates retain a 15-minute time-to-idle and `IdentityCopier`; disk remains nonpersistent. The eight trial caches allow 72,000 heap entries per UI instance, up from 36,000, for an increase of 36,000 entries. Entry counts do not cap heap use in MB. Configured disk capacity totals 1,280 MB per instance, up from 640 MB, for an increase of 640 MB. Actual disk use and heap occupancy can be lower or vary with the workload.

Do not change `biography`, `emails`, private `dao-*` caches, unrelated UI caches, programmatic caches, or other deployments as part of this initial trial. Keep existing changes in `orcid-core-cache-config.xml`. Do not change entity-cache heaps to byte sizing: deep object sizing can traverse detached Hibernate collections.

## Production snapshot baseline

The supplied Jolokia snapshot is cumulative; its collection interval and cache uptime were not provided. Treat these numbers as indicators of churn, not rates or proof that capacity is the cause of low reuse.

| Cache | Evictions | Puts | Reported hit % |
| --- | ---: | ---: | ---: |
| `peer-reviews` | 471,722 | 496,219 | 6 |
| `fundings` | 423,271 | 496,200 | 7 |
| `public-external-identifiers` | 354,345 | 442,491 | 3 |
| `public-researcher-urls` | 326,048 | 442,119 | 2 |
| `public-keywords` | 325,472 | 442,118 | 2 |
| `public-address` | 310,802 | 442,117 | 2 |
| `public-other-names` | 281,472 | 442,117 | 2 |
| `public-emails` | 275,846 | 442,119 | 2 |

The filesystem snapshot showed allocated files near the then-configured limits (about 128 MB for each activity cache and 64 MB for each public-detail cache). Allocated file size is not live occupancy. JCache counters do not identify which tier served or evicted an entry, so neither the file listing nor total evictions demonstrates per-tier occupancy or proves a capacity bottleneck.

## Canary preparation and headroom gate

1. Select one UI canary and an unchanged UI comparison instance with similar traffic, version, JVM sizing, and database access. Record the deployed UI XML revision and verify that no other cache tuning is included.
2. Before deploying, record free space on the actual Ehcache persistence filesystem, including its existing programmatic use and normal operational reserve. The UI trial permits up to 1,280 MB of disk capacity per instance across these eight caches; do not proceed unless the filesystem has safe headroom for that capacity and expected growth.
3. Record JVM max and used heap, old-generation pressure, GC frequency and pause time, and the applicable service limits. The possible heap-entry limit rises from 36,000 to 72,000 and has no fixed MB cost; ensure heap and GC headroom from observed behavior rather than treating entry count as a memory budget.
4. Capture baseline request p95 and error rate, database read load, filesystem free space, and the same JVM/GC measures for both instances. Use existing dashboards and collectors; this change adds no monitoring service or JMX bridge.
5. Do not proceed if disk, heap, GC, database, or error headroom is already unsafe or if the comparison instance is not sufficiently similar to make a useful comparison.

## Canary deployment and measurement

1. Deploy the changed UI XML to the canary only and restart it so the configured resources take effect. These caches are nonpersistent and will start cold.
2. Warm the canary with representative traffic. Exclude restart and warm-up from the comparison window; do not compare a cold canary against a warm control.
3. After warm-up, compare the canary and control over representative, similarly loaded traffic for at least one full daily cycle. Align time windows and account for major workload or database differences.
4. At the start and end of each measurement interval, collect the existing Jolokia hit, miss, put, and eviction counters for each of the eight aliases. For each counter calculate an interval delta: `delta = ending cumulative counter - starting cumulative counter`. If both hit and miss deltas are available and use consistent definitions, calculate interval hit rate as `delta hits / (delta hits + delta misses)`. Check for counter resets or process restarts; do not use a delta spanning a reset.
5. Compare interval cache deltas alongside request p95, error rate, database read load, JVM heap/GC pressure, and filesystem free space and allocated cache files. Use the same intervals and traffic context for canary and control. Do not interpret aggregate JCache evictions as heap-only or disk-only eviction, and do not infer live occupancy from allocated file sizes.
6. Do not log ORCIDs, email addresses, cache keys, or cached values in canary notes or diagnostic output.

## Acceptance and rollback

Keep the trial only if matched, post-warm-up observations show a meaningful request-p95 improvement or lower database read load, with no unacceptable increase in errors and no breach of established disk, heap, GC, or service limits. A higher hit rate alone is not sufficient evidence of user benefit. If the improvement is absent, inconclusive after the measurement window, or accompanied by unsafe resource pressure or errors, roll back.

To roll back, restore the original `activitiesTemplate`/`bioTemplate` resource behavior in `ehcache_orcid-web.xml` (4,500 heap entries with 128 MB disk for activity caches and 64 MB disk for the six public-detail caches), then restart the canary. The nonpersistent caches will restart cold; warm them again and verify normal error, latency, disk, and JVM/GC behavior. Keep the unchanged comparison instance available until rollback is confirmed.
