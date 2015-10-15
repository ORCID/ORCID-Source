-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: src/main/resources/db/updates/add_administrative_changes_option.xml
-- Ran at: 16/10/15 00:11
-- Against: orcid@jdbc:postgresql://localhost:5432/orcid
-- Liquibase version: 3.2.0
-- *********************************************************************

-- Lock Database
-- Changeset src/main/resources/db/updates/add_administrative_changes_option.xml::ADD-ADMINISTRATIVE-CHANGES-OPTION-COLUMN::Will Simpson
ALTER TABLE profile ADD send_administrative_change_notifications BOOLEAN;

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('ADD-ADMINISTRATIVE-CHANGES-OPTION-COLUMN', 'Will Simpson', '/db/updates/add_administrative_changes_option.xml', NOW(), 305, '7:ee8b84eb13f9564f19f07af088e57fb2', 'addColumn', '', 'EXECUTED', '3.2.0');

-- Changeset src/main/resources/db/updates/add_administrative_changes_option.xml::POPULATE-ADMINISTRATIVE-CHANGES-OPTION-COLUMN::Will Simposn
CREATE OR REPLACE FUNCTION populate_send_administrative_change_notifications() RETURNS VOID AS $$
DECLARE
    orcid_to_update VARCHAR;
    orcid_cursor CURSOR FOR SELECT orcid FROM profile WHERE send_administrative_change_notifications IS NULL AND send_change_notifications IS NOT NULL;
BEGIN
    RAISE NOTICE 'Populating send administrative change notifications option...';
    FOR orcid_record IN orcid_cursor
    LOOP
        orcid_to_update := orcid_record.orcid;
        RAISE NOTICE 'Updating % ', orcid_to_update;
        EXECUTE 'UPDATE profile set send_administrative_change_notifications = send_change_notifications WHERE orcid = $1' USING orcid_to_update;
    END LOOP;

    RAISE NOTICE 'Finished populating send administrative change notifications option.';
    RETURN;
END;
$$ LANGUAGE plpgsql;

select populate_send_administrative_change_notifications();

INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('POPULATE-ADMINISTRATIVE-CHANGES-OPTION-COLUMN', 'Will Simposn', '/db/updates/add_administrative_changes_option.xml', NOW(), 306, '7:4dcea6b91d36bc6df1af79daff61cb54', 'createProcedure, sql', '', 'EXECUTED', '3.2.0');

-- Release Database Lock
