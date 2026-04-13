-- Migration: Add last_modified column to togglz table
-- Run against the features database: psql -d features -f add_togglz_last_modified.sql
-- Safe to run on DBs that already have this column (uses IF NOT EXISTS)

\c features

ALTER TABLE public.togglz ADD COLUMN IF NOT EXISTS last_modified timestamp with time zone;

UPDATE public.togglz SET last_modified = NOW() WHERE last_modified IS NULL;

CREATE OR REPLACE FUNCTION update_togglz_last_modified()
RETURNS TRIGGER AS $$
BEGIN
  NEW.last_modified = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS togglz_last_modified_trigger ON public.togglz;
CREATE TRIGGER togglz_last_modified_trigger
BEFORE INSERT OR UPDATE ON public.togglz
FOR EACH ROW
EXECUTE FUNCTION update_togglz_last_modified();
