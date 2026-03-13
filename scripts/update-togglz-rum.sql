-- Update RUM feature in local Postgres (Togglz table).
-- Run against the 'features' database, e.g.:
--   psql -U orcid -d features -f scripts/update-togglz-rum.sql
-- Or from psql: \c features  then \i scripts/update-togglz-rum.sql

-- Ensure RUM row exists, then set Gradual rollout with a percentage.
-- Adjust the percentage (e.g. 25 = 25% of users) as needed.

INSERT INTO togglz (feature_name, feature_enabled, strategy_id, strategy_params)
VALUES (
  'RUM',
  1,
  'GradualRollout',
  'percentage=25'
)
ON CONFLICT (feature_name)
DO UPDATE SET
  feature_enabled = EXCLUDED.feature_enabled,
  strategy_id = EXCLUDED.strategy_id,
  strategy_params = EXCLUDED.strategy_params;
