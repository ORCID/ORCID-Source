--
-- =============================================================================
--
-- ORCID (R) Open Source
-- http://orcid.org
--
-- Copyright (c) 2012-2013 ORCID, Inc.
-- Licensed under an MIT-Style License (MIT)
-- http://orcid.org/open-source-license
--
-- This copyright and license information (including a link to the full license)
-- shall be included in its entirety in all copies or substantial portion of
-- the software.
--
-- =============================================================================
--

-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: /db/updates/work-external-ids-as-json.xml
-- Ran at: 30/05/14 18:44
-- Against: orcid@jdbc:postgresql://localhost:5432/empty
-- Liquibase version: 2.0.3
-- *********************************************************************

-- Changeset /db/updates/work-external-ids-as-json.xml::ADD-WORK-EXTERNAL-IDS-JSON-COLUMN::Will Simpson::(Checksum: 3:354ae24ffe3257b439236971b63189b5)
ALTER TABLE work ADD external_ids_json TEXT;

INSERT INTO databasechangelog (AUTHOR, COMMENTS, DATEEXECUTED, DESCRIPTION, EXECTYPE, FILENAME, ID, LIQUIBASE, MD5SUM, ORDEREXECUTED) VALUES ('Will Simpson', '', NOW(), 'Add Column', 'EXECUTED', '/db/updates/work-external-ids-as-json.xml', 'ADD-WORK-EXTERNAL-IDS-JSON-COLUMN', '2.0.3', '3:354ae24ffe3257b439236971b63189b5', 1);

-- Changeset /db/updates/work-external-ids-as-json.xml::CONVERT-TEXT-TO-JSON::Will Simpson::(Checksum: 3:03eea45c69465defb9c83509dbc3d449)
ALTER TABLE work ALTER COLUMN contributors_json TYPE json USING contributors_json::JSON;

ALTER TABLE work ALTER COLUMN external_ids_json TYPE json USING external_ids_json::JSON;

CREATE OR REPLACE FUNCTION json_intext(text) RETURNS json AS $$
SELECT json_in($1::cstring);
$$ LANGUAGE SQL IMMUTABLE;
ALTER FUNCTION json_intext(text) OWNER TO orcid;

CREATE OR REPLACE FUNCTION extract_doi(json) RETURNS varchar
AS $$
SELECT j->'workExternalIdentifierId'->>'content'
FROM (SELECT json_array_elements(json_extract_path($1, 'workExternalIdentifier')) AS j) AS a
WHERE j->>'workExternalIdentifierType' = 'DOI'
ORDER BY length(j->'workExternalIdentifierId'->>'content') DESC
LIMIT 1;
$$ LANGUAGE SQL
IMMUTABLE
RETURNS NULL ON NULL INPUT;
ALTER FUNCTION extract_doi(json) OWNER TO orcid;

CREATE INDEX work_doi_idx ON work(extract_doi(external_ids_json));

INSERT INTO databasechangelog (AUTHOR, COMMENTS, DATEEXECUTED, DESCRIPTION, EXECTYPE, FILENAME, ID, LIQUIBASE, MD5SUM, ORDEREXECUTED) VALUES ('Will Simpson', '', NOW(), 'Custom SQL (x2), Create Procedure (x2), Create Index', 'EXECUTED', '/db/updates/work-external-ids-as-json.xml', 'CONVERT-TEXT-TO-JSON', '2.0.3', '3:03eea45c69465defb9c83509dbc3d449', 2);

-- Changeset /db/updates/work-external-ids-as-json.xml::ADD-JSON-CAST::Will Simpson::(Checksum: 3:316804177e4751fdab4abcae6166d55a)
CREATE CAST (character varying AS json) WITH FUNCTION json_intext(text) AS IMPLICIT;

INSERT INTO databasechangelog (AUTHOR, COMMENTS, DATEEXECUTED, DESCRIPTION, EXECTYPE, FILENAME, ID, LIQUIBASE, MD5SUM, ORDEREXECUTED) VALUES ('Will Simpson', '', NOW(), 'Custom SQL', 'EXECUTED', '/db/updates/work-external-ids-as-json.xml', 'ADD-JSON-CAST', '2.0.3', '3:316804177e4751fdab4abcae6166d55a', 3);

