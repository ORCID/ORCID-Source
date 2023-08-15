-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: /db/updates/work-external-ids-as-json.xml
-- Ran at: 18/06/14 17:25
-- Against: orcid@jdbc:postgresql://localhost:5432/empty
-- Liquibase version: 3.2.0
-- *********************************************************************

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('ADD-WORK-EXTERNAL-IDS-JSON-COLUMN', 'Will Simpson', '/db/updates/work-external-ids-as-json.xml', NOW(), 1, '7:e1cba068309ff6ae561ad4a46f9209dc', 'addColumn', '', 'EXECUTED', '3.2.0');

-- Changeset /db/updates/work-external-ids-as-json.xml::CONVERT-TEXT-TO-JSON::Will Simpson
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

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('CONVERT-TEXT-TO-JSON', 'Will Simpson', '/db/updates/work-external-ids-as-json.xml', NOW(), 2, '7:59d7f7aeb42e754db84a5c2dd7044cdb', 'sql (x2), createProcedure (x2), createIndex', '', 'EXECUTED', '3.2.0');

-- Changeset /db/updates/work-external-ids-as-json.xml::ADD-JSON-CAST::Will Simpson
CREATE CAST (character varying AS json) WITH FUNCTION json_intext(text) AS IMPLICIT;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('ADD-JSON-CAST', 'Will Simpson', '/db/updates/work-external-ids-as-json.xml', NOW(), 3, '7:ca804e7ec59fac915f72fc02b2f4fb57', 'sql', '', 'EXECUTED', '3.2.0');

