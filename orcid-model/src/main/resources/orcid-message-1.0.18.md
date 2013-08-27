SELECT *  FROM webhook w JOIN profile p ON p.orcid = w.orcid AND (p.last_modified >= w.last_sent OR (w.last_sent IS NULL AND p.last_modified >= w.date_created)) JOIN client_details c ON c.client_details_id = w.client_details_id AND c.webhooks_enabled = 'true' WHERE w.enabled = 'true' AND (w.failed_attempt_count = 0 OR unix_timestamp(w.last_failed) + w.failed_attempt_count * 5 * 60 < unix_timestamp(now())) ORDER BY p.last_modified;    



SELECT * FROM (select * from webhook w0 JOIN client_details c ON c.client_details_id = w0.client_details_id AND c.webhooks_enabled = 'true' WHERE w0.enabled = 'true' AND (w0.failed_attempt_count = 0 OR unix_timestamp(w0.last_failed) + w0.failed_attempt_count * 5 * 60 < unix_timestamp(now()))
) w JOIN (select orcid, last_modified from profile) p ON p.orcid = w.orcid AND (p.last_modified >= w.last_sent OR (w.last_sent IS NULL AND p.last_modified >= w0.date_created)) ORDER BY p.last_modified;


SELECT  w.orcid  FROM webhook w JOIN (select orcid, last_modified from profile) p ON p.orcid = w.orcid AND (p.last_modified >= w.last_sent OR (w.last_sent IS NULL AND p.last_modified >= w.date_created)) WHERE  (w.failed_attempt_count = 0 OR unix_timestamp(w.last_failed) + w.failed_attempt_count * 5 * 60 < unix_timestamp(now())) ORDER BY p.last_modified;    




SELECT *  FROM webhook w JOIN profile p ON p.orcid = w.orcid 
AND (p.last_modified >= w.last_sent OR (w.last_sent IS NULL AND p.last_modified >= w.date_created)) 
JOIN client_details c ON c.client_details_id = w.client_details_id  
AND (w.failed_attempt_count = 0 OR w.last_failed   < now() - '5 minute'::interval ) ORDER BY p.last_modified;


SELECT *  FROM webhook w JOIN profile p ON p.orcid = w.orcid AND (p.last_modified >= w.last_sent OR (w.last_sent IS NULL AND p.last_modified >= w.date_created)) JOIN client_details c ON c.client_details_id = w.client_details_id AND c.webhooks_enabled = 'true' WHERE w.enabled = 'true' AND (w.failed_attempt_count = 0 OR unix_timestamp(w.last_failed) + w.failed_attempt_count * 5 * 60 < unix_timestamp(now())) ORDER BY p.last_modified;    






SELECT *  FROM webhook w JOIN profile p ON p.orcid = w.orcid 
AND (p.last_modified >= w.last_sent OR (w.last_sent IS NULL AND p.last_modified >= w.date_created)) 
JOIN client_details c ON c.client_details_id = w.client_details_id  
AND (w.failed_attempt_count = 0 OR w.last_failed   < now() - '5 minute'::interval ) ORDER BY p.last_modified;









select * from webhook as w JOIN profile p ON p.orcid = w.orcid JOIN client_details c ON c.client_details_id = w.client_details_id where 
c.webhooks_enabled = 'true' and w.enabled = 'true' 
and (p.last_modified >= w.last_sent OR (w.last_sent IS NULL AND p.last_modified >= w.date_created))  
AND (w.failed_attempt_count = 0 OR unix_timestamp(w.last_failed) + w.failed_attempt_count * 5 * 60 < unix_timestamp(now())) ORDER BY p.last_modified;

select * from webhook as w JOIN profile p ON p.orcid = w.orcid JOIN client_details c ON c.client_details_id = w.client_details_id where 
c.webhooks_enabled = 'true' and w.enabled = 'true' 
and (p.last_modified >= w.last_sent OR (w.last_sent IS NULL AND p.last_modified >= w.date_created))  
AND (w.failed_attempt_count = 0 OR w.last_failed > now() + '5 minute'::interval) ORDER BY p.last_modified;




select orcid from profile where 



CREATE INDEX profile_last_modified_idx ON profile USING btree(last_modified);

CREATE INDEX webhook_last_sent_idx ON webhook USING btree(last_sent);

CREATE INDEX webhook_date_created_idx ON webhook USING btree(date_created);

CREATE INDEX webhook_last_failed_idx ON webhook USING btree(last_failed);

CREATE INDEX webhook_client_details_idx ON webhook USING btree(client_details_id);






