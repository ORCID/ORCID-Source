--
-- PostgreSQL database dump
--

-- Dumped from database version 13.10 (Ubuntu 13.10-1.pgdg20.04+1)
-- Dumped by pg_dump version 15.2 (Ubuntu 15.2-1.pgdg20.04+1)

\c orcid

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--


ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: org_disambiguated_descendent; Type: TYPE; Schema: public; Owner: orcid
--

CREATE TYPE public.org_disambiguated_descendent AS (
	id bigint,
	source_id character varying,
	source_parent_id character varying,
	org_type character varying,
	name character varying,
	city character varying,
	region character varying,
	country character varying,
	level integer
);


ALTER TYPE public.org_disambiguated_descendent OWNER TO orcid;

--
-- Name: json_intext(text); Type: FUNCTION; Schema: public; Owner: orcid
--

CREATE FUNCTION public.json_intext(text) RETURNS json
    LANGUAGE sql IMMUTABLE
    AS $_$
SELECT json_in($1::cstring);
$_$;


ALTER FUNCTION public.json_intext(text) OWNER TO orcid;

--
-- Name: extract_doi(json); Type: FUNCTION; Schema: public; Owner: orcid
--

CREATE FUNCTION public.extract_doi(json) RETURNS character varying
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
SELECT j->'workExternalIdentifierId'->>'content'
FROM (SELECT json_array_elements(json_extract_path($1, 'workExternalIdentifier')) AS j) AS a
WHERE j->>'workExternalIdentifierType' = 'DOI'
ORDER BY length(j->'workExternalIdentifierId'->>'content') DESC
LIMIT 1;
$_$;


ALTER FUNCTION public.extract_doi(json) OWNER TO orcid;

--
-- Name: find_org_disambiguated_descendents(character varying, character varying); Type: FUNCTION; Schema: public; Owner: orcid
--

CREATE FUNCTION public.find_org_disambiguated_descendents(source_id character varying, source_type character varying) RETURNS SETOF public.org_disambiguated_descendent
    LANGUAGE sql IMMUTABLE STRICT
    AS $$
SELECT * FROM find_org_disambiguated_descendents(source_id, source_type, 1)
ORDER BY level, source_parent_id, name;
$$;


ALTER FUNCTION public.find_org_disambiguated_descendents(source_id character varying, source_type character varying) OWNER TO orcid;

--
-- Name: find_org_disambiguated_descendents(character varying, character varying, integer); Type: FUNCTION; Schema: public; Owner: orcid
--

CREATE FUNCTION public.find_org_disambiguated_descendents(required_source_id character varying, required_source_type character varying, current_level integer) RETURNS SETOF public.org_disambiguated_descendent
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $$
DECLARE
    current_result org_disambiguated_descendent;
BEGIN
FOR current_result IN SELECT p1.id, p1.source_id, p1.source_parent_id, p1.org_type, p1.name, p1.city, p1.region, p1.country, current_level AS level FROM org_disambiguated p1 WHERE p1.source_parent_id = required_source_id AND p1.source_type = required_source_type LOOP
    RETURN NEXT current_result;
    RETURN QUERY SELECT * FROM find_org_disambiguated_descendents(current_result.source_id, required_source_type, current_level + 1);    
END LOOP;
END
$$;


ALTER FUNCTION public.find_org_disambiguated_descendents(required_source_id character varying, required_source_type character varying, current_level integer) OWNER TO orcid;

--
-- Name: insert_notification_scope(); Type: FUNCTION; Schema: public; Owner: orcid
--

CREATE FUNCTION public.insert_notification_scope() RETURNS void
    LANGUAGE plpgsql
    AS $_$
DECLARE
    client_id VARCHAR;
BEGIN
    RAISE NOTICE 'Inserting notification scopes...';

    FOR client_id IN SELECT * FROM client_details cd LEFT JOIN client_scope cs ON cs.client_details_id = cd.client_details_id AND cs.scope_type = '/notification' WHERE cd.client_type IS NOT NULL AND cs.client_details_id IS NULL
    LOOP
        RAISE NOTICE 'Found member % without notification scope', client_id;
        EXECUTE 'INSERT INTO client_scope (client_details_id, scope_type, date_created, last_modified) VALUES ($1, ''/notification'', now(), now())' USING client_id;
    END LOOP;

    RAISE NOTICE 'Finished inserting notification scopes';
    RETURN;
END;
$_$;


ALTER FUNCTION public.insert_notification_scope() OWNER TO orcid;

--
-- Name: insert_scope_for_premium_members(character varying); Type: FUNCTION; Schema: public; Owner: orcid
--

CREATE FUNCTION public.insert_scope_for_premium_members(scope_to_add character varying) RETURNS void
    LANGUAGE plpgsql
    AS $_$
DECLARE
    client_id VARCHAR;
BEGIN
    RAISE NOTICE 'Inserting scope...';

    FOR client_id IN SELECT * FROM client_details cd LEFT JOIN client_scope cs ON cs.client_details_id = cd.client_details_id AND cs.scope_type = scope_to_add WHERE cd.client_type IN ('PREMIUM_CREATOR', 'PREMIUM_UPDATER') AND cs.client_details_id IS NULL
    LOOP
        RAISE NOTICE 'Found member % without % scope', client_id, scope_to_add;
        EXECUTE 'INSERT INTO client_scope (client_details_id, scope_type, date_created, last_modified) VALUES ($1, $2, now(), now())' USING client_id, scope_to_add;
    END LOOP;

    RAISE NOTICE 'Finished inserting scope';
    RETURN;
END;
$_$;


ALTER FUNCTION public.insert_scope_for_premium_members(scope_to_add character varying) OWNER TO orcid;

--
-- Name: populate_send_administrative_change_notifications(); Type: FUNCTION; Schema: public; Owner: orcid
--

CREATE FUNCTION public.populate_send_administrative_change_notifications() RETURNS void
    LANGUAGE plpgsql
    AS $_$
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
$_$;


ALTER FUNCTION public.populate_send_administrative_change_notifications() OWNER TO orcid;

--
-- Name: set_sequence_starts(); Type: FUNCTION; Schema: public; Owner: orcid
--

CREATE FUNCTION public.set_sequence_starts() RETURNS void
    LANGUAGE plpgsql
    AS $_$
DECLARE
    seq VARCHAR;
    next_val BIGINT;
    min_val BIGINT := 1000;
BEGIN
    RAISE NOTICE 'Setting values of sequences to minimum value...';

    FOR seq IN SELECT c.relname FROM pg_class c WHERE c.relkind = 'S' LOOP
        next_val := nextval(seq);
        RAISE NOTICE 'Found sequence % with next value = %', seq, next_val;
        IF next_val < min_val THEN
            RAISE NOTICE 'Increasing value of sequence % to %', seq, min_val;
            EXECUTE 'SELECT setval($1, $2)' USING seq, min_val;
        END IF;
    END LOOP;

    RAISE NOTICE 'Finished setting values of sequences to minimum value';
    RETURN;
END;
$_$;


ALTER FUNCTION public.set_sequence_starts() OWNER TO orcid;

--
-- Name: unix_timestamp(timestamp with time zone); Type: FUNCTION; Schema: public; Owner: orcid
--

CREATE FUNCTION public.unix_timestamp(timestamp with time zone) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT EXTRACT(epoch FROM $1) $_$;


ALTER FUNCTION public.unix_timestamp(timestamp with time zone) OWNER TO orcid;

--
-- Name: access_token_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.access_token_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.access_token_seq OWNER TO orcid;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: address; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.address (
    id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    address_line_1 character varying(350),
    address_line_2 character varying(350),
    city character varying(150),
    postal_code character varying(15),
    state_or_province character varying(150),
    orcid character varying(19),
    is_primary boolean DEFAULT false NOT NULL,
    iso2_country character varying(2),
    visibility character varying(19),
    source_id character varying(19),
    client_source_id character varying(20),
    display_index bigint DEFAULT 0,
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20)
);


ALTER TABLE public.address OWNER TO orcid;

--
-- Name: address_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.address_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.address_seq OWNER TO orcid;

--
-- Name: affiliation; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.affiliation (
    institution_id bigint NOT NULL,
    orcid character varying(255) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    role_title character varying(255),
    start_date timestamp without time zone,
    affiliation_details_visibility character varying(20),
    end_date date,
    affiliation_type character varying(100),
    department_name character varying(400),
    affiliation_address_visibility character varying(20)
);


ALTER TABLE public.affiliation OWNER TO orcid;

--
-- Name: org; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.org (
    id bigint NOT NULL,
    name character varying(4000) NOT NULL,
    city character varying(4000) NOT NULL,
    region character varying(4000) NOT NULL,
    country character varying(2) NOT NULL,
    url character varying(2000),
    source_id character varying(255),
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    org_disambiguated_id bigint,
    client_source_id character varying(20)
);


ALTER TABLE public.org OWNER TO orcid;

--
-- Name: org_affiliation_relation; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.org_affiliation_relation (
    id bigint NOT NULL,
    org_id bigint NOT NULL,
    orcid character varying(255) NOT NULL,
    org_affiliation_relation_role text,
    org_affiliation_relation_title text,
    department text,
    start_day integer,
    start_month integer,
    start_year integer,
    end_day integer,
    end_month integer,
    end_year integer,
    visibility character varying(20),
    source_id character varying(255),
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    client_source_id character varying(20),
    url text,
    external_ids_json json,
    display_index bigint DEFAULT 0,
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20)
);


ALTER TABLE public.org_affiliation_relation OWNER TO orcid;

--
-- Name: ambiguous_org; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.ambiguous_org AS
 SELECT o.id,
    o.name,
    o.city,
    o.region,
    o.country,
    o.url,
    o.source_id,
    o.date_created,
    o.last_modified,
    count(*) AS used_count
   FROM (public.org o
     LEFT JOIN public.org_affiliation_relation oar ON ((oar.org_id = o.id)))
  WHERE (o.org_disambiguated_id IS NULL)
  GROUP BY o.id, o.name, o.city, o.region, o.country, o.url, o.source_id, o.date_created, o.last_modified;


ALTER TABLE public.ambiguous_org OWNER TO orcid;

--
-- Name: author_other_name_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.author_other_name_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.author_other_name_seq OWNER TO orcid;

--
-- Name: backup_code; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.backup_code (
    id bigint NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    orcid character varying(19) NOT NULL,
    used_date timestamp with time zone,
    hashed_code character varying(255)
);


ALTER TABLE public.backup_code OWNER TO orcid;

--
-- Name: backup_code_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.backup_code_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.backup_code_seq OWNER TO orcid;

--
-- Name: biography; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.biography (
    id bigint NOT NULL,
    orcid character varying(255) NOT NULL,
    biography text,
    visibility character varying(20),
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.biography OWNER TO orcid;

--
-- Name: biography_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.biography_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.biography_seq OWNER TO orcid;

--
-- Name: client_authorised_grant_type; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.client_authorised_grant_type (
    client_details_id character varying(150) NOT NULL,
    grant_type character varying(150) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone
);


ALTER TABLE public.client_authorised_grant_type OWNER TO orcid;

--
-- Name: client_details; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.client_details (
    client_details_id character varying(150) NOT NULL,
    client_secret character varying(150),
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    client_name text,
    webhooks_enabled boolean DEFAULT true NOT NULL,
    client_description text,
    client_website text,
    persistent_tokens_enabled boolean DEFAULT false,
    group_orcid character varying(19),
    client_type character varying(25),
    authentication_provider_id character varying(1000),
    allow_auto_deprecate boolean DEFAULT false,
    email_access_reason text,
    user_obo_enabled boolean DEFAULT false,
    deactivated_date timestamp with time zone,
    deactivated_by character varying(19)
);


ALTER TABLE public.client_details OWNER TO orcid;

--
-- Name: client_granted_authority; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.client_granted_authority (
    client_details_id character varying(150) NOT NULL,
    granted_authority character varying(150) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone
);


ALTER TABLE public.client_granted_authority OWNER TO orcid;

--
-- Name: client_redirect_uri; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.client_redirect_uri (
    client_details_id character varying(150) NOT NULL,
    redirect_uri text NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    predefined_client_redirect_scope text,
    redirect_uri_type text DEFAULT 'default'::character varying NOT NULL,
    uri_act_type json DEFAULT '{"import-works-wizard" : ["Articles"]}'::json,
    uri_geo_area json DEFAULT '{"import-works-wizard" : ["Global"]}'::json,
    status character varying(200) DEFAULT 'OK'::character varying
);


ALTER TABLE public.client_redirect_uri OWNER TO orcid;

--
-- Name: client_resource_id; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.client_resource_id (
    client_details_id character varying(150) NOT NULL,
    resource_id character varying(175) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone
);


ALTER TABLE public.client_resource_id OWNER TO orcid;

--
-- Name: client_scope; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.client_scope (
    client_details_id character varying(150) NOT NULL,
    scope_type character varying(150) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone
);


ALTER TABLE public.client_scope OWNER TO orcid;

--
-- Name: client_secret; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.client_secret (
    client_details_id character varying(255) NOT NULL,
    client_secret character varying(150) NOT NULL,
    date_created timestamp with time zone NOT NULL,
    last_modified timestamp with time zone NOT NULL,
    is_primary boolean DEFAULT true
);


ALTER TABLE public.client_secret OWNER TO orcid;

--
-- Name: country_reference_data; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.country_reference_data (
    country_iso_code character varying(2) NOT NULL,
    country_name character varying(255),
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.country_reference_data OWNER TO orcid;

--
-- Name: custom_email; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.custom_email (
    client_details_id character varying(255) NOT NULL,
    email_type character varying(255) NOT NULL,
    content text NOT NULL,
    sender text,
    subject text,
    is_html boolean DEFAULT true,
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.custom_email OWNER TO orcid;

--
-- Name: databasechangelog; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.databasechangelog (
    id character varying(63) NOT NULL,
    author character varying(63) NOT NULL,
    filename character varying(200) NOT NULL,
    dateexecuted timestamp with time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20),
    contexts character varying(255),
    labels character varying(255),
    deployment_id character varying(10)
);


ALTER TABLE public.databasechangelog OWNER TO orcid;

--
-- Name: databasechangeloglock; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp with time zone,
    lockedby character varying(255)
);


ALTER TABLE public.databasechangeloglock OWNER TO orcid;

--
-- Name: dw_active_users; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.dw_active_users (
    date_calculated timestamp without time zone NOT NULL,
    last_day integer NOT NULL,
    last_thirty_days integer NOT NULL,
    last_quarter integer NOT NULL,
    last_year integer NOT NULL
);


ALTER TABLE public.dw_active_users OWNER TO orcid;

--
-- Name: dw_address; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_address AS
 SELECT address.id AS db_id,
    address.orcid,
    address.iso2_country,
    address.visibility,
        CASE
            WHEN ((address.orcid)::text = (address.source_id)::text) THEN true
            ELSE false
        END AS self_asserted,
    address.client_source_id,
    address.date_created,
    address.last_modified
   FROM public.address
  WHERE (address.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_address OWNER TO orcid;

--
-- Name: dw_biography; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_biography AS
 SELECT biography.id AS db_id,
    biography.orcid,
    biography.biography,
    biography.visibility,
    (biography.date_created)::timestamp without time zone AS date_created,
    (biography.last_modified)::timestamp without time zone AS last_modified
   FROM public.biography
  WHERE (biography.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_biography OWNER TO orcid;

--
-- Name: dw_client_details; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_client_details AS
 SELECT client_details.client_details_id,
    client_details.client_name,
    client_details.client_description,
    client_details.client_website,
    client_details.group_orcid,
    client_details.client_type,
    client_details.user_obo_enabled,
    client_details.date_created,
    client_details.last_modified
   FROM public.client_details
  WHERE (client_details.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_client_details OWNER TO orcid;

--
-- Name: dw_client_redirect_uri; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_client_redirect_uri AS
 SELECT client_redirect_uri.client_details_id,
    client_redirect_uri.redirect_uri,
    client_redirect_uri.date_created,
    client_redirect_uri.last_modified
   FROM public.client_redirect_uri
  WHERE (client_redirect_uri.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_client_redirect_uri OWNER TO orcid;

--
-- Name: email; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.email (
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    email text,
    orcid character varying(255) NOT NULL,
    visibility character varying(20) DEFAULT 'PRIVATE'::character varying NOT NULL,
    is_primary boolean DEFAULT true NOT NULL,
    is_current boolean DEFAULT true NOT NULL,
    is_verified boolean DEFAULT false NOT NULL,
    source_id character varying(255),
    client_source_id character varying(20),
    email_hash character varying(256) NOT NULL,
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20)
);


ALTER TABLE public.email OWNER TO orcid;

--
-- Name: dw_email; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_email AS
 SELECT "substring"(email.email, '@(.*)$'::text) AS email,
    email.orcid,
    email.is_primary,
    email.is_verified,
    email.visibility,
    (email.date_created)::timestamp without time zone AS date_created,
    (email.last_modified)::timestamp without time zone AS last_modified
   FROM public.email
  WHERE (email.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_email OWNER TO orcid;

--
-- Name: event_stats; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.event_stats (
    id bigint NOT NULL,
    event_type character varying(20),
    client_id character varying(255),
    count bigint,
    date timestamp without time zone,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    ip character varying(60)
);


ALTER TABLE public.event_stats OWNER TO orcid;

--
-- Name: dw_event_stats; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_event_stats AS
 SELECT event_stats.event_type,
    event_stats.client_id,
    event_stats.count,
    date_trunc('day'::text, event_stats.date) AS date_trunc,
    date_trunc('day'::text, event_stats.date) AS last_modified
   FROM public.event_stats
  WHERE ((event_stats.event_type)::text <> 'Public-API'::text)
  ORDER BY (date_trunc('day'::text, event_stats.date_created)) DESC;


ALTER TABLE public.dw_event_stats OWNER TO orcid;

--
-- Name: external_identifier; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.external_identifier (
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    orcid character varying(19) NOT NULL,
    external_id_reference text NOT NULL,
    external_id_type text,
    external_id_url text,
    source_id character varying(19),
    client_source_id character varying(20),
    id bigint NOT NULL,
    visibility character varying(19),
    display_index bigint DEFAULT 0,
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20)
);


ALTER TABLE public.external_identifier OWNER TO orcid;

--
-- Name: dw_external_identifier; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_external_identifier AS
 SELECT external_identifier.id AS db_id,
    external_identifier.orcid,
    external_identifier.external_id_reference,
    external_identifier.external_id_type,
    external_identifier.external_id_url,
    external_identifier.visibility,
        CASE
            WHEN ((external_identifier.orcid)::text = (external_identifier.source_id)::text) THEN true
            ELSE false
        END AS self_asserted,
    external_identifier.client_source_id,
    external_identifier.date_created,
    external_identifier.last_modified
   FROM public.external_identifier
  WHERE (external_identifier.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_external_identifier OWNER TO orcid;

--
-- Name: given_permission_to; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.given_permission_to (
    receiver_orcid character varying(19) NOT NULL,
    giver_orcid character varying(19) NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    approval_date timestamp with time zone,
    given_permission_to_id bigint NOT NULL
);


ALTER TABLE public.given_permission_to OWNER TO orcid;

--
-- Name: dw_given_permission_to; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_given_permission_to AS
 SELECT given_permission_to.given_permission_to_id,
    given_permission_to.receiver_orcid,
    given_permission_to.giver_orcid,
    (given_permission_to.approval_date)::timestamp without time zone AS approval_date,
    (given_permission_to.date_created)::timestamp without time zone AS date_created,
    (given_permission_to.last_modified)::timestamp without time zone AS last_modified
   FROM public.given_permission_to
  WHERE (given_permission_to.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_given_permission_to OWNER TO orcid;

--
-- Name: group_id_record; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.group_id_record (
    id bigint NOT NULL,
    group_id text NOT NULL,
    group_name text NOT NULL,
    group_description text,
    group_type text NOT NULL,
    source_id character varying(255),
    client_source_id character varying(20),
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20),
    issn_loader_fail_count integer DEFAULT 0,
    fail_reason character varying(50),
    sync_date timestamp without time zone
);


ALTER TABLE public.group_id_record OWNER TO orcid;

--
-- Name: dw_group_id_record; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_group_id_record AS
 SELECT group_id_record.id AS db_id,
    group_id_record.group_id,
    group_id_record.group_name,
    group_id_record.group_type,
    group_id_record.client_source_id,
    (group_id_record.date_created)::timestamp without time zone AS date_created,
    (group_id_record.last_modified)::timestamp without time zone AS last_modified
   FROM public.group_id_record
  WHERE (group_id_record.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_group_id_record OWNER TO orcid;

--
-- Name: identifier_type; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.identifier_type (
    id bigint NOT NULL,
    id_name text NOT NULL,
    id_validation_regex text,
    id_resolution_prefix text,
    id_deprecated boolean DEFAULT false NOT NULL,
    client_source_id character varying(20),
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    primary_use text DEFAULT 'work'::character varying NOT NULL,
    case_sensitive boolean DEFAULT false NOT NULL
);


ALTER TABLE public.identifier_type OWNER TO orcid;

--
-- Name: dw_identifier_type; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_identifier_type AS
 SELECT identifier_type.id AS db_id,
    identifier_type.id_name,
    identifier_type.id_validation_regex,
    identifier_type.id_resolution_prefix,
    identifier_type.id_deprecated,
    identifier_type.primary_use,
    identifier_type.case_sensitive,
    (identifier_type.date_created)::timestamp without time zone AS date_created,
    (identifier_type.last_modified)::timestamp without time zone AS last_modified
   FROM public.identifier_type
  WHERE (identifier_type.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_identifier_type OWNER TO orcid;

--
-- Name: identity_provider; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.identity_provider (
    id bigint NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    providerid text NOT NULL,
    display_name text,
    support_email text,
    admin_email text,
    tech_email text,
    last_failed timestamp with time zone,
    failed_count integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.identity_provider OWNER TO orcid;

--
-- Name: dw_identity_provider; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_identity_provider AS
 SELECT identity_provider.id AS db_id,
    identity_provider.providerid,
    identity_provider.display_name,
    (identity_provider.last_failed)::timestamp without time zone AS last_failed,
    identity_provider.failed_count,
    (identity_provider.date_created)::timestamp without time zone AS date_created,
    (identity_provider.last_modified)::timestamp without time zone AS last_modified
   FROM public.identity_provider
  WHERE (identity_provider.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_identity_provider OWNER TO orcid;

--
-- Name: notification; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.notification (
    id bigint NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    orcid character varying(19) NOT NULL,
    notification_type text NOT NULL,
    subject text,
    body_text text,
    body_html text,
    sent_date timestamp with time zone,
    read_date timestamp with time zone,
    archived_date timestamp with time zone,
    sendable boolean DEFAULT true NOT NULL,
    source_id character varying(19),
    client_source_id character varying(20),
    authorization_url text,
    lang text,
    amended_section text,
    actioned_date timestamp with time zone,
    notification_subject text,
    notification_intro text,
    authentication_provider_id text,
    retry_count integer,
    notification_family character varying(50),
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20)
);


ALTER TABLE public.notification OWNER TO orcid;

--
-- Name: dw_notification; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_notification AS
 SELECT notification.id AS db_id,
    notification.notification_type,
    notification.orcid,
    notification.client_source_id,
    notification.date_created,
    notification.sent_date,
    notification.read_date,
    notification.actioned_date,
    notification.archived_date,
    notification.last_modified
   FROM public.notification
  WHERE ((notification.notification_type = 'PERMISSION'::text) AND (notification.client_source_id IS NOT NULL) AND (notification.last_modified > date_trunc('day'::text, (now() - '1 year'::interval))));


ALTER TABLE public.dw_notification OWNER TO orcid;

--
-- Name: oauth2_token_detail; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.oauth2_token_detail (
    token_value character varying(155),
    token_type character varying(50),
    token_expiration timestamp without time zone,
    user_orcid character varying(19),
    client_details_id character varying(20),
    is_approved boolean,
    redirect_uri character varying(350),
    response_type character varying(100),
    state character varying(40),
    scope_type character varying(500),
    resource_id character varying(50),
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    authentication_key character varying(150),
    id bigint DEFAULT nextval('public.access_token_seq'::regclass) NOT NULL,
    refresh_token_expiration timestamp without time zone,
    refresh_token_value character varying(150),
    token_disabled boolean DEFAULT false,
    persistent boolean DEFAULT false,
    version bigint DEFAULT (0)::bigint,
    authorization_code character varying(255),
    revocation_date timestamp with time zone,
    revoke_reason character varying(30),
    obo_client_details_id character varying(20)
);


ALTER TABLE public.oauth2_token_detail OWNER TO orcid;

--
-- Name: dw_oauth2_token_detail; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_oauth2_token_detail AS
 SELECT oauth2_token_detail.id AS db_id,
    oauth2_token_detail.token_type,
    oauth2_token_detail.user_orcid,
    oauth2_token_detail.client_details_id,
    "substring"((oauth2_token_detail.redirect_uri)::text, '.*://([^/]*)'::text) AS redirect_uri,
    oauth2_token_detail.scope_type,
    oauth2_token_detail.obo_client_details_id,
    oauth2_token_detail.token_expiration,
    oauth2_token_detail.revocation_date,
    oauth2_token_detail.date_created,
    oauth2_token_detail.last_modified
   FROM public.oauth2_token_detail
  WHERE (oauth2_token_detail.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_oauth2_token_detail OWNER TO orcid;

--
-- Name: dw_org; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_org AS
 SELECT org.id AS db_id,
    org.name,
    org.city,
    org.region,
    org.country,
    org.url,
    org.org_disambiguated_id,
    (org.date_created)::timestamp without time zone AS date_created,
    (org.last_modified)::timestamp without time zone AS last_modified
   FROM public.org
  WHERE (org.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_org OWNER TO orcid;

--
-- Name: dw_org_affiliation_relation; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_org_affiliation_relation AS
 SELECT org_affiliation_relation.id AS db_id,
    org_affiliation_relation.org_id,
    org_affiliation_relation.orcid,
    org_affiliation_relation.assertion_origin_client_source_id,
    org_affiliation_relation.org_affiliation_relation_role,
    org_affiliation_relation.org_affiliation_relation_title,
    org_affiliation_relation.department,
    org_affiliation_relation.start_day,
    org_affiliation_relation.start_month,
    org_affiliation_relation.start_year,
    org_affiliation_relation.end_day,
    org_affiliation_relation.end_month,
    org_affiliation_relation.end_year,
    org_affiliation_relation.visibility,
        CASE
            WHEN ((org_affiliation_relation.orcid)::text = (org_affiliation_relation.source_id)::text) THEN true
            ELSE false
        END AS self_asserted,
    org_affiliation_relation.client_source_id,
    org_affiliation_relation.url,
    org_affiliation_relation.external_ids_json,
    (org_affiliation_relation.date_created)::timestamp without time zone AS date_created,
    (org_affiliation_relation.last_modified)::timestamp without time zone AS last_modified
   FROM public.org_affiliation_relation
  WHERE (org_affiliation_relation.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_org_affiliation_relation OWNER TO orcid;

--
-- Name: org_disambiguated; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.org_disambiguated (
    id bigint NOT NULL,
    source_id character varying(255),
    source_url character varying(2000),
    source_type character varying(255),
    org_type character varying(4000),
    name character varying(4000),
    city character varying(4000),
    region character varying(4000),
    country character varying(2),
    url character varying(2000),
    status character varying(255),
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    indexing_status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    last_indexed_date timestamp with time zone,
    popularity integer DEFAULT 0 NOT NULL,
    source_parent_id character varying(255),
    locations_json json,
    names_json json
);


ALTER TABLE public.org_disambiguated OWNER TO orcid;

--
-- Name: dw_org_disambiguated; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_org_disambiguated AS
 SELECT org_disambiguated.id AS db_id,
    org_disambiguated.source_id,
    org_disambiguated.source_url,
    org_disambiguated.source_type,
    org_disambiguated.org_type,
    org_disambiguated.name,
    org_disambiguated.city,
    org_disambiguated.region,
    org_disambiguated.country,
    org_disambiguated.url,
    org_disambiguated.status,
    (org_disambiguated.date_created)::timestamp without time zone AS date_created,
    (org_disambiguated.last_modified)::timestamp without time zone AS last_modified,
    org_disambiguated.popularity
   FROM public.org_disambiguated
  WHERE (org_disambiguated.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_org_disambiguated OWNER TO orcid;

--
-- Name: org_disambiguated_external_identifier; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.org_disambiguated_external_identifier (
    id bigint NOT NULL,
    org_disambiguated_id bigint,
    identifier character varying(4000),
    identifier_type character varying(4000),
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    preferred boolean DEFAULT false
);


ALTER TABLE public.org_disambiguated_external_identifier OWNER TO orcid;

--
-- Name: dw_org_disambiguated_external_identifier; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_org_disambiguated_external_identifier AS
 SELECT org_disambiguated_external_identifier.id AS db_id,
    org_disambiguated_external_identifier.org_disambiguated_id,
    org_disambiguated_external_identifier.identifier,
    org_disambiguated_external_identifier.identifier_type,
    org_disambiguated_external_identifier.preferred,
    (org_disambiguated_external_identifier.date_created)::timestamp without time zone AS date_created,
    (org_disambiguated_external_identifier.last_modified)::timestamp without time zone AS last_modified
   FROM public.org_disambiguated_external_identifier
  WHERE (org_disambiguated_external_identifier.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_org_disambiguated_external_identifier OWNER TO orcid;

--
-- Name: other_name; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.other_name (
    other_name_id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    display_name text,
    orcid character varying(19) NOT NULL,
    visibility character varying(19),
    source_id character varying(19),
    client_source_id character varying(20),
    display_index bigint DEFAULT 0,
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20)
);


ALTER TABLE public.other_name OWNER TO orcid;

--
-- Name: dw_other_name; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_other_name AS
 SELECT other_name.other_name_id,
    other_name.orcid,
    other_name.display_name,
    other_name.visibility,
        CASE
            WHEN ((other_name.orcid)::text = (other_name.source_id)::text) THEN true
            ELSE false
        END AS self_asserted,
    other_name.client_source_id,
    other_name.date_created,
    other_name.last_modified
   FROM public.other_name
  WHERE (other_name.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_other_name OWNER TO orcid;

--
-- Name: dw_papi_event_stats; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_papi_event_stats AS
 SELECT event_stats.event_type,
    event_stats.client_id,
    event_stats.count,
    date_trunc('day'::text, event_stats.date) AS date_trunc,
    date_trunc('day'::text, event_stats.date) AS last_modified
   FROM public.event_stats
  WHERE ((event_stats.event_type)::text = 'Public-API'::text)
  ORDER BY (date_trunc('day'::text, event_stats.date_created)) DESC;


ALTER TABLE public.dw_papi_event_stats OWNER TO orcid;

--
-- Name: peer_review; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.peer_review (
    id bigint NOT NULL,
    orcid character varying(255) NOT NULL,
    peer_review_subject_id bigint,
    external_identifiers_json json NOT NULL,
    org_id bigint NOT NULL,
    peer_review_role text NOT NULL,
    peer_review_type text NOT NULL,
    completion_day integer,
    completion_month integer,
    completion_year integer,
    source_id character varying(255),
    url text,
    visibility character varying(20),
    client_source_id character varying(20),
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    display_index bigint DEFAULT 0,
    subject_external_identifiers_json text,
    subject_type text,
    subject_container_name text,
    subject_name text,
    subject_translated_name text,
    subject_translated_name_language_code text,
    subject_url text,
    group_id text,
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20)
);


ALTER TABLE public.peer_review OWNER TO orcid;

--
-- Name: dw_peer_review; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_peer_review AS
 SELECT peer_review.id AS db_id,
    peer_review.orcid,
    peer_review.peer_review_subject_id,
    peer_review.external_identifiers_json,
    peer_review.org_id,
    peer_review.peer_review_role,
    peer_review.peer_review_type,
    peer_review.completion_day,
    peer_review.completion_month,
    peer_review.completion_year,
    peer_review.url,
    peer_review.visibility,
    peer_review.subject_external_identifiers_json,
    peer_review.subject_type,
    peer_review.subject_container_name,
    peer_review.subject_name,
    peer_review.subject_url,
    peer_review.group_id,
    peer_review.client_source_id,
    (peer_review.date_created)::timestamp without time zone AS date_created,
    (peer_review.last_modified)::timestamp without time zone AS last_modified
   FROM public.peer_review
  WHERE (peer_review.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_peer_review OWNER TO orcid;

--
-- Name: profile; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.profile (
    orcid character varying(19) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    account_expiry timestamp without time zone,
    completed_date timestamp without time zone,
    claimed boolean DEFAULT false,
    creation_method character varying(20),
    enabled boolean DEFAULT true,
    encrypted_password character varying(255),
    is_selectable_sponsor boolean,
    source_id character varying(19),
    orcid_type character varying(20),
    submission_date timestamp with time zone DEFAULT now() NOT NULL,
    indexing_status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    profile_deactivation_date timestamp without time zone,
    activities_visibility_default character varying(20) DEFAULT 'PRIVATE'::character varying NOT NULL,
    last_indexed_date timestamp with time zone,
    locale character varying(12) DEFAULT 'EN'::character varying NOT NULL,
    primary_record character varying(19),
    deprecated_date timestamp with time zone,
    group_type character varying(25),
    referred_by character varying(20),
    enable_developer_tools boolean DEFAULT false,
    salesforce_id character varying(15),
    client_source_id character varying(20),
    developer_tools_enabled_date timestamp with time zone,
    record_locked boolean DEFAULT false NOT NULL,
    used_captcha_on_registration boolean,
    user_last_ip character varying(50),
    reviewed boolean DEFAULT false NOT NULL,
    reason_locked text,
    reason_locked_description text,
    hashed_orcid character varying(256),
    last_login timestamp without time zone,
    secret_for_2fa character varying(255),
    using_2fa boolean DEFAULT false,
    deprecating_admin character varying(19),
    deprecated_method character varying(20),
    record_locked_date timestamp without time zone,
    record_locked_admin_id character varying(19),
    signin_lock_start timestamp without time zone,
    signin_lock_last_attempt timestamp without time zone,
    signin_lock_count integer,
    auto_lock_date timestamp without time zone
);


ALTER TABLE public.profile OWNER TO orcid;

--
-- Name: dw_profile; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_profile AS
 SELECT profile.orcid,
    profile.orcid_type,
    profile.record_locked,
    profile.group_type,
    profile.salesforce_id,
    profile.date_created,
    profile.last_modified,
    profile.profile_deactivation_date,
    profile.enable_developer_tools,
    profile.last_login,
    profile.using_2fa,
    profile.reason_locked,
    profile.auto_lock_date,
    profile.locale,
    profile.reviewed,
    profile.creation_method
   FROM public.profile
  WHERE (profile.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_profile OWNER TO orcid;

--
-- Name: profile_email_domain; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.profile_email_domain (
    id bigint NOT NULL,
    orcid character varying(19) NOT NULL,
    email_domain character varying(254) NOT NULL,
    visibility character varying(20),
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.profile_email_domain OWNER TO orcid;

--
-- Name: dw_profile_email_domain; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_profile_email_domain AS
 SELECT profile_email_domain.id AS db_id,
    profile_email_domain.orcid,
    profile_email_domain.email_domain,
    profile_email_domain.visibility,
    profile_email_domain.date_created,
    profile_email_domain.last_modified
   FROM public.profile_email_domain
  WHERE (profile_email_domain.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_profile_email_domain OWNER TO orcid;

--
-- Name: profile_funding; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.profile_funding (
    id bigint NOT NULL,
    org_id bigint NOT NULL,
    orcid character varying(255) NOT NULL,
    title text NOT NULL,
    type text NOT NULL,
    currency_code character varying(3),
    translated_title text,
    translated_title_language_code text,
    description text,
    start_day integer,
    start_month integer,
    start_year integer,
    end_day integer,
    end_month integer,
    end_year integer,
    url text,
    contributors_json json,
    visibility character varying(20),
    source_id character varying(255),
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    organization_defined_type text DEFAULT 'default'::character varying,
    numeric_amount numeric,
    display_index bigint DEFAULT (0)::bigint,
    client_source_id character varying(20),
    external_identifiers_json json,
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20)
);


ALTER TABLE public.profile_funding OWNER TO orcid;

--
-- Name: dw_profile_funding; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_profile_funding AS
 SELECT profile_funding.id AS db_id,
    profile_funding.orcid,
    profile_funding.org_id,
    profile_funding.title,
    profile_funding.type,
    profile_funding.currency_code,
    profile_funding.numeric_amount,
    profile_funding.description,
    profile_funding.start_day,
    profile_funding.start_month,
    profile_funding.start_year,
    profile_funding.end_day,
    profile_funding.end_month,
    profile_funding.end_year,
    profile_funding.url,
    profile_funding.contributors_json,
    profile_funding.organization_defined_type,
    profile_funding.external_identifiers_json,
    profile_funding.visibility,
        CASE
            WHEN ((profile_funding.orcid)::text = (profile_funding.source_id)::text) THEN true
            ELSE false
        END AS self_asserted,
    profile_funding.client_source_id,
    (profile_funding.date_created)::timestamp without time zone AS date_created,
    (profile_funding.last_modified)::timestamp without time zone AS last_modified
   FROM public.profile_funding
  WHERE (profile_funding.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_profile_funding OWNER TO orcid;

--
-- Name: profile_history_event; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.profile_history_event (
    id bigint NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    orcid character varying(19) NOT NULL,
    event_type character varying(50),
    comment text
);


ALTER TABLE public.profile_history_event OWNER TO orcid;

--
-- Name: dw_profile_history_event; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_profile_history_event AS
 SELECT profile_history_event.id AS db_id,
    profile_history_event.orcid,
    profile_history_event.event_type,
    (profile_history_event.date_created)::timestamp without time zone AS date_created,
    (profile_history_event.last_modified)::timestamp without time zone AS last_modified
   FROM public.profile_history_event
  WHERE (profile_history_event.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_profile_history_event OWNER TO orcid;

--
-- Name: profile_keyword; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.profile_keyword (
    profile_orcid character varying(19) NOT NULL,
    keywords_name text NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    id bigint NOT NULL,
    visibility character varying(19),
    source_id character varying(19),
    client_source_id character varying(20),
    display_index bigint DEFAULT 0,
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20)
);


ALTER TABLE public.profile_keyword OWNER TO orcid;

--
-- Name: dw_profile_keyword; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_profile_keyword AS
 SELECT profile_keyword.id AS db_id,
    profile_keyword.profile_orcid,
    btrim(kwc.kwc) AS keyword,
    profile_keyword.visibility,
        CASE
            WHEN ((profile_keyword.profile_orcid)::text = (profile_keyword.source_id)::text) THEN true
            ELSE false
        END AS self_asserted,
    profile_keyword.client_source_id,
    profile_keyword.date_created,
    profile_keyword.last_modified
   FROM public.profile_keyword,
    LATERAL regexp_split_to_table(profile_keyword.keywords_name, '[,;\.]'::text) kwc(kwc)
  WHERE (profile_keyword.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_profile_keyword OWNER TO orcid;

--
-- Name: record_name; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.record_name (
    id bigint NOT NULL,
    orcid character varying(255) NOT NULL,
    credit_name text,
    family_name text,
    given_names text,
    visibility character varying(20),
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.record_name OWNER TO orcid;

--
-- Name: dw_record_name; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_record_name AS
 SELECT record_name.id AS db_id,
    record_name.orcid,
    record_name.credit_name,
    record_name.family_name,
    record_name.given_names,
    record_name.visibility,
    (record_name.date_created)::timestamp without time zone AS date_created,
    (record_name.last_modified)::timestamp without time zone AS last_modified
   FROM public.record_name
  WHERE (record_name.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_record_name OWNER TO orcid;

--
-- Name: research_resource; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.research_resource (
    id bigint NOT NULL,
    orcid character varying(255) NOT NULL,
    source_id character varying(255),
    client_source_id character varying(20),
    proposal_type character varying(150) NOT NULL,
    external_identifiers_json text NOT NULL,
    title character varying(1000) NOT NULL,
    translated_title character varying(1000),
    translated_title_language_code character varying(10),
    url character varying(350),
    display_index integer,
    start_day integer,
    start_month integer,
    start_year integer,
    end_day integer,
    end_month integer,
    end_year integer,
    visibility character varying(20),
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20)
);


ALTER TABLE public.research_resource OWNER TO orcid;

--
-- Name: dw_research_resource; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_research_resource AS
 SELECT research_resource.id AS db_id,
    research_resource.orcid,
    research_resource.proposal_type,
    research_resource.external_identifiers_json,
    research_resource.title,
    research_resource.url,
    research_resource.start_day,
    research_resource.start_month,
    research_resource.start_year,
    research_resource.end_day,
    research_resource.end_month,
    research_resource.end_year,
    research_resource.visibility,
    research_resource.client_source_id,
    research_resource.date_created,
    research_resource.last_modified
   FROM public.research_resource
  WHERE (research_resource.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_research_resource OWNER TO orcid;

--
-- Name: research_resource_item; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.research_resource_item (
    id bigint NOT NULL,
    research_resource_id bigint NOT NULL,
    resource_name character varying(1000) NOT NULL,
    resource_type character varying(150) NOT NULL,
    external_identifiers_json text NOT NULL,
    url character varying(350),
    item_index bigint NOT NULL
);


ALTER TABLE public.research_resource_item OWNER TO orcid;

--
-- Name: dw_research_resource_item; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_research_resource_item AS
 SELECT a.id AS db_id,
    a.research_resource_id,
    a.resource_name,
    a.resource_type,
    a.external_identifiers_json,
    a.url,
    (b.date_created)::timestamp without time zone AS date_created,
    (b.last_modified)::timestamp without time zone AS last_modified
   FROM (public.research_resource_item a
     JOIN public.research_resource b ON ((a.research_resource_id = b.id)))
  WHERE (b.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_research_resource_item OWNER TO orcid;

--
-- Name: research_resource_item_org; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.research_resource_item_org (
    research_resource_item_id bigint NOT NULL,
    org_id bigint NOT NULL,
    org_index bigint NOT NULL
);


ALTER TABLE public.research_resource_item_org OWNER TO orcid;

--
-- Name: dw_research_resource_item_org; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_research_resource_item_org AS
 SELECT a.research_resource_item_id,
    a.org_id,
    c.date_created,
    c.last_modified
   FROM ((public.research_resource_item_org a
     JOIN public.research_resource_item b ON ((a.research_resource_item_id = b.id)))
     JOIN public.research_resource c ON ((b.research_resource_id = c.id)))
  WHERE (c.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_research_resource_item_org OWNER TO orcid;

--
-- Name: research_resource_org; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.research_resource_org (
    research_resource_id bigint NOT NULL,
    org_id bigint NOT NULL,
    org_index bigint NOT NULL
);


ALTER TABLE public.research_resource_org OWNER TO orcid;

--
-- Name: dw_research_resource_org; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_research_resource_org AS
 SELECT a.research_resource_id,
    a.org_id,
    (b.date_created)::timestamp without time zone AS date_created,
    (b.last_modified)::timestamp without time zone AS last_modified
   FROM (public.research_resource_org a
     JOIN public.research_resource b ON ((a.research_resource_id = b.id)))
  WHERE (b.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_research_resource_org OWNER TO orcid;

--
-- Name: researcher_url_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.researcher_url_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.researcher_url_seq OWNER TO orcid;

--
-- Name: researcher_url; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.researcher_url (
    url text NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    orcid character varying(19) NOT NULL,
    id bigint DEFAULT nextval('public.researcher_url_seq'::regclass) NOT NULL,
    url_name text,
    visibility character varying(19),
    source_id character varying(19),
    client_source_id character varying(20),
    display_index bigint DEFAULT 0,
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20)
);


ALTER TABLE public.researcher_url OWNER TO orcid;

--
-- Name: dw_researcher_url; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_researcher_url AS
 SELECT researcher_url.id AS db_id,
    researcher_url.orcid,
    "substring"(researcher_url.url, '[http[s]*://]?([^/]+)'::text) AS domain,
    researcher_url.url_name,
    researcher_url.visibility,
        CASE
            WHEN ((researcher_url.orcid)::text = (researcher_url.source_id)::text) THEN true
            ELSE false
        END AS self_asserted,
    researcher_url.client_source_id,
    researcher_url.date_created,
    researcher_url.last_modified
   FROM public.researcher_url
  WHERE (researcher_url.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_researcher_url OWNER TO orcid;

--
-- Name: userconnection; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.userconnection (
    userid text NOT NULL,
    email text,
    orcid character varying(19),
    providerid text NOT NULL,
    provideruserid text NOT NULL,
    rank integer NOT NULL,
    displayname text,
    profileurl text,
    imageurl text,
    accesstoken text,
    secret text,
    refreshtoken text,
    expiretime bigint,
    is_linked boolean DEFAULT false,
    last_login timestamp with time zone,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    id_type text,
    status text DEFAULT 'STARTED'::character varying,
    headers_json json
);


ALTER TABLE public.userconnection OWNER TO orcid;

--
-- Name: dw_userconnection; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_userconnection AS
 SELECT userconnection.orcid,
    userconnection.providerid,
    userconnection.is_linked,
    userconnection.last_login,
    userconnection.id_type,
    (userconnection.date_created)::timestamp without time zone AS date_created,
    (userconnection.last_modified)::timestamp without time zone AS last_modified
   FROM public.userconnection
  WHERE ((userconnection.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval))) AND (btrim((userconnection.orcid)::text) <> ''::text));


ALTER TABLE public.dw_userconnection OWNER TO orcid;

--
-- Name: validated_public_profile; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.validated_public_profile (
    orcid character varying(19) NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    error text,
    valid boolean
);


ALTER TABLE public.validated_public_profile OWNER TO orcid;

--
-- Name: dw_validated_public_profile; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_validated_public_profile AS
 SELECT min(validated_public_profile.date_created) AS date_from,
    max(validated_public_profile.date_created) AS date_to,
    (((( SELECT count(*) AS count
           FROM public.validated_public_profile validated_public_profile_1
          WHERE (validated_public_profile_1.valid IS TRUE)))::double precision / (( SELECT count(*) AS count
           FROM public.validated_public_profile validated_public_profile_1))::double precision) * (100)::double precision) AS percent_valid,
    ( SELECT dr.error
           FROM ( SELECT validated_public_profile_1.error,
                    count(*) AS errorcount
                   FROM public.validated_public_profile validated_public_profile_1
                  WHERE (validated_public_profile_1.valid IS FALSE)
                  GROUP BY validated_public_profile_1.error
                  ORDER BY (count(*)) DESC
                 LIMIT 1) dr) AS most_common_error,
    (((( SELECT count(*) AS count
           FROM public.validated_public_profile validated_public_profile_1
          WHERE ((validated_public_profile_1.valid IS FALSE) AND (validated_public_profile_1.error = ( SELECT dr.error
                   FROM ( SELECT validated_public_profile_2.error,
                            count(*) AS errorcount
                           FROM public.validated_public_profile validated_public_profile_2
                          WHERE (validated_public_profile_2.valid IS FALSE)
                          GROUP BY validated_public_profile_2.error
                          ORDER BY (count(*)) DESC
                         LIMIT 1) dr)))))::double precision / (( SELECT count(*) AS count
           FROM public.validated_public_profile validated_public_profile_1))::double precision) * (100)::double precision) AS percent_affected_by_most_common_error,
    max(validated_public_profile.last_modified) AS last_modified
   FROM public.validated_public_profile
  WHERE (validated_public_profile.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_validated_public_profile OWNER TO orcid;

--
-- Name: work; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.work (
    work_id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    publication_day integer,
    publication_month integer,
    publication_year integer,
    title text,
    subtitle text,
    description text,
    work_url text,
    citation text,
    work_type text,
    citation_type text,
    contributors_json json,
    journal_title text,
    language_code text,
    translated_title text,
    translated_title_language_code text,
    iso2_country text,
    external_ids_json json,
    orcid character varying(19),
    added_to_profile_date timestamp without time zone,
    visibility character varying(19),
    display_index bigint DEFAULT (0)::bigint,
    source_id character varying(19),
    client_source_id character varying(20),
    assertion_origin_source_id character varying(19),
    assertion_origin_client_source_id character varying(20),
    top_contributors_json text
);


ALTER TABLE public.work OWNER TO orcid;

--
-- Name: dw_work; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_work AS
 SELECT work.work_id,
    work.orcid,
    work.publication_day,
    work.publication_month,
    work.publication_year,
    work.title,
    work.subtitle,
    work.description,
    work.work_url,
    work.citation,
    work.citation_type,
    work.work_type,
    work.journal_title,
    work.language_code,
    work.translated_title,
    work.translated_title_language_code,
    work.iso2_country,
    work.visibility,
        CASE
            WHEN ((work.orcid)::text = (work.source_id)::text) THEN true
            ELSE false
        END AS self_asserted,
    work.client_source_id,
    work.date_created,
    work.last_modified
   FROM public.work
  WHERE (work.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)));


ALTER TABLE public.dw_work OWNER TO orcid;

--
-- Name: dw_work_external_id; Type: VIEW; Schema: public; Owner: orcid
--

CREATE VIEW public.dw_work_external_id AS
 WITH t AS (
         SELECT work.work_id,
            work.orcid,
            work.date_created,
            work.last_modified,
            json_array_elements((work.external_ids_json -> 'workExternalIdentifier'::text)) AS external_json
           FROM public.work
        )
 SELECT t.work_id,
    t.orcid,
    t.date_created,
    t.last_modified,
    ((t.external_json -> 'workExternalIdentifierId'::text) ->> 'content'::text) AS workexternalidentifierid,
    (t.external_json ->> 'relationship'::text) AS relationship,
    ((t.external_json -> 'url'::text) ->> 'value'::text) AS url,
    (t.external_json ->> 'workExternalIdentifierType'::text) AS workexternalidentifiertype
   FROM t
  WHERE (t.last_modified > date_trunc('day'::text, (now() - '4 mons'::interval)))
  ORDER BY t.last_modified;


ALTER TABLE public.dw_work_external_id OWNER TO orcid;

--
-- Name: email_domain; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.email_domain (
    id bigint NOT NULL,
    email_domain character varying(254) NOT NULL,
    category character varying(16) NOT NULL,
    ror_id character varying(30),
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.email_domain OWNER TO orcid;

--
-- Name: email_domain_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.email_domain_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.email_domain_seq OWNER TO orcid;

--
-- Name: email_domain_to_org_id_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.email_domain_to_org_id_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.email_domain_to_org_id_seq OWNER TO orcid;

--
-- Name: email_event; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.email_event (
    id bigint NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    email text NOT NULL,
    email_event_type character varying(255) NOT NULL
);


ALTER TABLE public.email_event OWNER TO orcid;

--
-- Name: email_event_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.email_event_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.email_event_seq OWNER TO orcid;

--
-- Name: email_frequency; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.email_frequency (
    id character varying(255) NOT NULL,
    orcid character varying(255) NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    send_administrative_change_notifications double precision DEFAULT 7.0 NOT NULL,
    send_change_notifications double precision DEFAULT 7.0 NOT NULL,
    send_member_update_requests double precision DEFAULT 7.0 NOT NULL,
    send_quarterly_tips boolean DEFAULT true NOT NULL
);


ALTER TABLE public.email_frequency OWNER TO orcid;

--
-- Name: email_schedule; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.email_schedule (
    id bigint NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    schedule_start timestamp with time zone,
    schedule_end timestamp with time zone,
    latest_sent timestamp with time zone,
    schedule_interval bigint,
    comments character varying(100),
    paused boolean DEFAULT false NOT NULL
);


ALTER TABLE public.email_schedule OWNER TO orcid;

--
-- Name: email_schedule_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.email_schedule_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.email_schedule_seq OWNER TO orcid;

--
-- Name: event; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.event (
    id bigint NOT NULL,
    event_type character varying(20),
    client_id character varying(255),
    label character varying(255),
    date_created timestamp with time zone,
    ip character varying(60)
);


ALTER TABLE public.event OWNER TO orcid;

--
-- Name: event_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.event_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.event_seq OWNER TO orcid;

--
-- Name: event_stats_id_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

ALTER TABLE public.event_stats ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.event_stats_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: event_stats_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.event_stats_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.event_stats_seq OWNER TO orcid;

--
-- Name: external_identifier_id_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.external_identifier_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.external_identifier_id_seq OWNER TO orcid;

--
-- Name: external_identifier_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: orcid
--

ALTER SEQUENCE public.external_identifier_id_seq OWNED BY public.external_identifier.id;


--
-- Name: find_my_stuff_history; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.find_my_stuff_history (
    orcid character varying(255) NOT NULL,
    finder_name character varying(255) NOT NULL,
    last_count integer,
    opt_out boolean,
    actioned boolean,
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.find_my_stuff_history OWNER TO orcid;

--
-- Name: funding_external_identifier; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.funding_external_identifier (
    funding_external_identifier_id bigint NOT NULL,
    profile_funding_id bigint NOT NULL,
    ext_type character varying(255),
    ext_value character varying(2084),
    ext_url character varying(350),
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.funding_external_identifier OWNER TO orcid;

--
-- Name: funding_external_identifier_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.funding_external_identifier_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.funding_external_identifier_seq OWNER TO orcid;

--
-- Name: funding_subtype_to_index; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.funding_subtype_to_index (
    orcid character varying(255) NOT NULL,
    subtype text NOT NULL
);


ALTER TABLE public.funding_subtype_to_index OWNER TO orcid;

--
-- Name: given_permission_to_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.given_permission_to_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.given_permission_to_seq OWNER TO orcid;

--
-- Name: grant_contributor_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.grant_contributor_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.grant_contributor_seq OWNER TO orcid;

--
-- Name: grant_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.grant_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.grant_seq OWNER TO orcid;

--
-- Name: granted_authority; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.granted_authority (
    authority character varying(255) NOT NULL,
    orcid character varying(255) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone
);


ALTER TABLE public.granted_authority OWNER TO orcid;

--
-- Name: group_id_record_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.group_id_record_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.group_id_record_seq OWNER TO orcid;

--
-- Name: identifier_type_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.identifier_type_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.identifier_type_seq OWNER TO orcid;

--
-- Name: identity_provider_name; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.identity_provider_name (
    id bigint NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    identity_provider_id bigint,
    display_name text,
    lang text
);


ALTER TABLE public.identity_provider_name OWNER TO orcid;

--
-- Name: identity_provider_name_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.identity_provider_name_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.identity_provider_name_seq OWNER TO orcid;

--
-- Name: identity_provider_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.identity_provider_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.identity_provider_seq OWNER TO orcid;

--
-- Name: institution; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.institution (
    id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    institution_name character varying(350),
    address_id bigint
);


ALTER TABLE public.institution OWNER TO orcid;

--
-- Name: institution_department_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.institution_department_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.institution_department_seq OWNER TO orcid;

--
-- Name: institution_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.institution_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.institution_seq OWNER TO orcid;

--
-- Name: internal_sso; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.internal_sso (
    orcid character varying(19) NOT NULL,
    token character varying(60) NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.internal_sso OWNER TO orcid;

--
-- Name: invalid_issn_group_id_record; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.invalid_issn_group_id_record (
    id bigint NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    notes text
);


ALTER TABLE public.invalid_issn_group_id_record OWNER TO orcid;

--
-- Name: invalid_record_change_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.invalid_record_change_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.invalid_record_change_seq OWNER TO orcid;

--
-- Name: invalid_record_data_changes; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.invalid_record_data_changes (
    sql_used_to_update text NOT NULL,
    description text NOT NULL,
    num_changed bigint NOT NULL,
    type text NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    id bigint DEFAULT nextval('public.invalid_record_change_seq'::regclass) NOT NULL
);


ALTER TABLE public.invalid_record_data_changes OWNER TO orcid;

--
-- Name: key_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.key_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.key_seq OWNER TO orcid;

--
-- Name: keyword_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.keyword_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.keyword_seq OWNER TO orcid;

--
-- Name: member_chosen_org_disambiguated; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.member_chosen_org_disambiguated (
    org_disambiguated_id bigint NOT NULL
);


ALTER TABLE public.member_chosen_org_disambiguated OWNER TO orcid;

--
-- Name: member_obo_whitelisted_client; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.member_obo_whitelisted_client (
    id bigint NOT NULL,
    client_details_id character varying(150),
    whitelisted_client_details_id character varying(150),
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.member_obo_whitelisted_client OWNER TO orcid;

--
-- Name: notification_item; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.notification_item (
    id bigint NOT NULL,
    notification_id bigint,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    item_type text,
    item_name text,
    external_id_type text,
    external_id_value text,
    action_type character varying(10),
    additional_info json,
    external_id_url character varying(255),
    external_id_relationship character varying(255)
);


ALTER TABLE public.notification_item OWNER TO orcid;

--
-- Name: notification_item_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.notification_item_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.notification_item_seq OWNER TO orcid;

--
-- Name: notification_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.notification_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.notification_seq OWNER TO orcid;

--
-- Name: notification_work; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.notification_work (
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    notification_id bigint NOT NULL,
    work_id bigint NOT NULL
);


ALTER TABLE public.notification_work OWNER TO orcid;

--
-- Name: oauth2_authoriziation_code_detail; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.oauth2_authoriziation_code_detail (
    authoriziation_code_value character varying(255) NOT NULL,
    is_aproved boolean,
    orcid character varying(19),
    redirect_uri character varying(355),
    response_type character varying(55),
    state character varying(2000),
    client_details_id character varying(150),
    session_id character varying(100),
    is_authenticated boolean,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    persistent boolean DEFAULT false,
    version bigint DEFAULT (0)::bigint,
    nonce character varying(2000)
);


ALTER TABLE public.oauth2_authoriziation_code_detail OWNER TO orcid;

--
-- Name: orcid_props; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.orcid_props (
    key character varying(255) NOT NULL,
    prop_value text,
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.orcid_props OWNER TO orcid;

--
-- Name: orcid_social; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.orcid_social (
    orcid character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    encrypted_credentials text NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    last_run timestamp with time zone
);


ALTER TABLE public.orcid_social OWNER TO orcid;

--
-- Name: orcidoauth2authoriziationcodedetail_authorities; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.orcidoauth2authoriziationcodedetail_authorities (
    orcidoauth2authoriziationcodedetail_authoriziation_code_value character varying(255) NOT NULL,
    authorities character varying(255) NOT NULL
);


ALTER TABLE public.orcidoauth2authoriziationcodedetail_authorities OWNER TO orcid;

--
-- Name: orcidoauth2authoriziationcodedetail_resourceids; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.orcidoauth2authoriziationcodedetail_resourceids (
    orcidoauth2authoriziationcodedetail_authoriziation_code_value character varying(255) NOT NULL,
    resourceids character varying(255) NOT NULL
);


ALTER TABLE public.orcidoauth2authoriziationcodedetail_resourceids OWNER TO orcid;

--
-- Name: orcidoauth2authoriziationcodedetail_scopes; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.orcidoauth2authoriziationcodedetail_scopes (
    orcidoauth2authoriziationcodedetail_authoriziation_code_value character varying(255) NOT NULL,
    scopes character varying(255) NOT NULL
);


ALTER TABLE public.orcidoauth2authoriziationcodedetail_scopes OWNER TO orcid;

--
-- Name: org_affiliation_relation_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.org_affiliation_relation_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.org_affiliation_relation_seq OWNER TO orcid;

--
-- Name: org_disambiguated_external_identifier_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.org_disambiguated_external_identifier_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.org_disambiguated_external_identifier_seq OWNER TO orcid;

--
-- Name: org_disambiguated_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.org_disambiguated_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.org_disambiguated_seq OWNER TO orcid;

--
-- Name: org_import_log; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.org_import_log (
    id bigint NOT NULL,
    start_time timestamp with time zone NOT NULL,
    end_time timestamp with time zone NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    source_type character varying(19) NOT NULL,
    successful boolean NOT NULL
);


ALTER TABLE public.org_import_log OWNER TO orcid;

--
-- Name: org_import_log_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.org_import_log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.org_import_log_seq OWNER TO orcid;

--
-- Name: org_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.org_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.org_seq OWNER TO orcid;

--
-- Name: other_name_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.other_name_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.other_name_seq OWNER TO orcid;

--
-- Name: patent; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.patent (
    patent_id bigint NOT NULL,
    issuing_country character varying(155),
    patent_no character varying(60),
    short_description character varying(550),
    issue_date date,
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.patent OWNER TO orcid;

--
-- Name: patent_contributor; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.patent_contributor (
    patent_contributor_id bigint NOT NULL,
    orcid character varying(19),
    patent_id bigint,
    credit_name character varying(450),
    contributor_role character varying(90),
    contributor_sequence character varying(90),
    contributor_email character varying(300),
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.patent_contributor OWNER TO orcid;

--
-- Name: patent_contributor_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.patent_contributor_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.patent_contributor_seq OWNER TO orcid;

--
-- Name: patent_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.patent_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.patent_seq OWNER TO orcid;

--
-- Name: patent_source; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.patent_source (
    orcid character varying(19) NOT NULL,
    patent_id bigint NOT NULL,
    source_orcid character varying(19) NOT NULL,
    deposited_date date,
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.patent_source OWNER TO orcid;

--
-- Name: peer_review_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.peer_review_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.peer_review_seq OWNER TO orcid;

--
-- Name: peer_review_subject; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.peer_review_subject (
    id bigint NOT NULL,
    external_identifiers_json json NOT NULL,
    title text NOT NULL,
    work_type text NOT NULL,
    sub_title text,
    translated_title text,
    translated_title_language_code text,
    url text,
    journal_title text,
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.peer_review_subject OWNER TO orcid;

--
-- Name: peer_review_subject_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.peer_review_subject_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.peer_review_subject_seq OWNER TO orcid;

--
-- Name: profile_email_domain_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.profile_email_domain_seq
    START WITH 100000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.profile_email_domain_seq OWNER TO orcid;

--
-- Name: profile_event; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.profile_event (
    id bigint NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    orcid character varying(19) NOT NULL,
    profile_event_type character varying(255) NOT NULL,
    comment text
);


ALTER TABLE public.profile_event OWNER TO orcid;

--
-- Name: profile_event_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.profile_event_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.profile_event_seq OWNER TO orcid;

--
-- Name: profile_funding_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.profile_funding_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.profile_funding_seq OWNER TO orcid;

--
-- Name: profile_history_event_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.profile_history_event_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.profile_history_event_seq OWNER TO orcid;

--
-- Name: profile_patent; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.profile_patent (
    orcid character varying(19) NOT NULL,
    patent_id bigint NOT NULL,
    added_to_profile_date date,
    visibility character varying(20),
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.profile_patent OWNER TO orcid;

--
-- Name: profile_subject; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.profile_subject (
    profile_orcid character varying(19) NOT NULL,
    subjects_name character varying(255) NOT NULL
);


ALTER TABLE public.profile_subject OWNER TO orcid;

--
-- Name: record_name_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.record_name_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.record_name_seq OWNER TO orcid;

--
-- Name: reference_data; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.reference_data (
    id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    ref_data_key character varying(255),
    ref_data_value character varying(255)
);


ALTER TABLE public.reference_data OWNER TO orcid;

--
-- Name: reference_data_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.reference_data_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.reference_data_seq OWNER TO orcid;

--
-- Name: rejected_grouping_suggestion; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.rejected_grouping_suggestion (
    put_codes character varying(255) NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    orcid character varying(19) NOT NULL
);


ALTER TABLE public.rejected_grouping_suggestion OWNER TO orcid;

--
-- Name: related_url_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.related_url_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.related_url_seq OWNER TO orcid;

--
-- Name: research_resource_item_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.research_resource_item_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.research_resource_item_seq OWNER TO orcid;

--
-- Name: research_resource_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.research_resource_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.research_resource_seq OWNER TO orcid;

--
-- Name: salesforce_connection; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.salesforce_connection (
    id bigint NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    orcid character varying(19) NOT NULL,
    email text NOT NULL,
    salesforce_account_id text NOT NULL,
    is_primary boolean DEFAULT true NOT NULL
);


ALTER TABLE public.salesforce_connection OWNER TO orcid;

--
-- Name: salesforce_connection_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.salesforce_connection_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.salesforce_connection_seq OWNER TO orcid;

--
-- Name: shibboleth_account; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.shibboleth_account (
    id bigint NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    orcid character varying(19) NOT NULL,
    remote_user text NOT NULL,
    shib_identity_provider text NOT NULL
);


ALTER TABLE public.shibboleth_account OWNER TO orcid;

--
-- Name: shibboleth_account_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.shibboleth_account_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.shibboleth_account_seq OWNER TO orcid;

--
-- Name: spam; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.spam (
    id bigint NOT NULL,
    orcid character varying(255) NOT NULL,
    source_type character varying(20),
    spam_counter integer,
    date_created timestamp with time zone,
    last_modified timestamp with time zone
);


ALTER TABLE public.spam OWNER TO orcid;

--
-- Name: spam_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.spam_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.spam_seq OWNER TO orcid;

--
-- Name: statistic_key; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.statistic_key (
    id bigint NOT NULL,
    generation_date timestamp with time zone
);


ALTER TABLE public.statistic_key OWNER TO orcid;

--
-- Name: statistic_values; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.statistic_values (
    id bigint NOT NULL,
    key_id bigint NOT NULL,
    statistic_name character varying(255),
    statistic_value bigint
);


ALTER TABLE public.statistic_values OWNER TO orcid;

--
-- Name: subject; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.subject (
    name text NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone
);


ALTER TABLE public.subject OWNER TO orcid;

--
-- Name: values_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.values_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.values_seq OWNER TO orcid;

--
-- Name: webhook; Type: TABLE; Schema: public; Owner: orcid
--

CREATE TABLE public.webhook (
    orcid character varying(255) NOT NULL,
    client_details_id character varying(255) NOT NULL,
    uri text NOT NULL,
    date_created timestamp with time zone,
    last_modified timestamp with time zone,
    last_failed timestamp with time zone,
    failed_attempt_count integer DEFAULT 0 NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    disabled_date timestamp with time zone,
    disabled_comments text,
    last_sent timestamp with time zone,
    profile_last_modified timestamp without time zone
);


ALTER TABLE public.webhook OWNER TO orcid;

--
-- Name: work_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE public.work_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.work_seq OWNER TO orcid;

--
-- Name: external_identifier id; Type: DEFAULT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.external_identifier ALTER COLUMN id SET DEFAULT nextval('public.external_identifier_id_seq'::regclass);


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- Name: backup_code backup_code_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.backup_code
    ADD CONSTRAINT backup_code_pkey PRIMARY KEY (id);


--
-- Name: biography biography_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.biography
    ADD CONSTRAINT biography_pkey PRIMARY KEY (id);


--
-- Name: client_authorised_grant_type client_authorised_grant_type_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_authorised_grant_type
    ADD CONSTRAINT client_authorised_grant_type_pkey PRIMARY KEY (client_details_id, grant_type);


--
-- Name: client_details client_details_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_details
    ADD CONSTRAINT client_details_pkey PRIMARY KEY (client_details_id);


--
-- Name: client_granted_authority client_granted_authority_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_granted_authority
    ADD CONSTRAINT client_granted_authority_pkey PRIMARY KEY (client_details_id, granted_authority);


--
-- Name: client_redirect_uri client_redirect_uri_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_redirect_uri
    ADD CONSTRAINT client_redirect_uri_pkey PRIMARY KEY (client_details_id, redirect_uri, redirect_uri_type);


--
-- Name: client_resource_id client_resource_id_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_resource_id
    ADD CONSTRAINT client_resource_id_pkey PRIMARY KEY (client_details_id, resource_id);


--
-- Name: client_scope client_scope_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_scope
    ADD CONSTRAINT client_scope_pkey PRIMARY KEY (client_details_id, scope_type);


--
-- Name: client_secret client_secret_pk; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_secret
    ADD CONSTRAINT client_secret_pk PRIMARY KEY (client_details_id, client_secret);


--
-- Name: country_reference_data country_id_id_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.country_reference_data
    ADD CONSTRAINT country_id_id_pkey PRIMARY KEY (country_iso_code);


--
-- Name: dw_active_users dw_active_users_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.dw_active_users
    ADD CONSTRAINT dw_active_users_pkey PRIMARY KEY (date_calculated);


--
-- Name: email_domain email_domain_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.email_domain
    ADD CONSTRAINT email_domain_pkey PRIMARY KEY (id);


--
-- Name: email_event email_event_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.email_event
    ADD CONSTRAINT email_event_pkey PRIMARY KEY (id);


--
-- Name: email_frequency email_frequency_orcid_unique; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.email_frequency
    ADD CONSTRAINT email_frequency_orcid_unique UNIQUE (orcid);


--
-- Name: email_frequency email_frequency_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.email_frequency
    ADD CONSTRAINT email_frequency_pkey PRIMARY KEY (id);


--
-- Name: email email_primary_key; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.email
    ADD CONSTRAINT email_primary_key PRIMARY KEY (email_hash);


--
-- Name: email_schedule email_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.email_schedule
    ADD CONSTRAINT email_schedule_pkey PRIMARY KEY (id);


--
-- Name: event event_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_pkey PRIMARY KEY (id);


--
-- Name: event_stats event_stats_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.event_stats
    ADD CONSTRAINT event_stats_pkey PRIMARY KEY (id);


--
-- Name: external_identifier external_identifier_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.external_identifier
    ADD CONSTRAINT external_identifier_pkey PRIMARY KEY (id);


--
-- Name: find_my_stuff_history find_my_stuff_history_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.find_my_stuff_history
    ADD CONSTRAINT find_my_stuff_history_pkey PRIMARY KEY (orcid, finder_name);


--
-- Name: funding_external_identifier funding_external_identifier_constraints; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.funding_external_identifier
    ADD CONSTRAINT funding_external_identifier_constraints UNIQUE (profile_funding_id, ext_type, ext_value, ext_url);


--
-- Name: funding_external_identifier funding_external_identifier_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.funding_external_identifier
    ADD CONSTRAINT funding_external_identifier_pkey PRIMARY KEY (funding_external_identifier_id);


--
-- Name: given_permission_to given_permission_to_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.given_permission_to
    ADD CONSTRAINT given_permission_to_pkey PRIMARY KEY (given_permission_to_id);


--
-- Name: granted_authority granted_authority_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.granted_authority
    ADD CONSTRAINT granted_authority_pkey PRIMARY KEY (authority, orcid);


--
-- Name: group_id_record group_id_record_group_id_key; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.group_id_record
    ADD CONSTRAINT group_id_record_group_id_key UNIQUE (group_id);


--
-- Name: group_id_record group_id_record_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.group_id_record
    ADD CONSTRAINT group_id_record_pkey PRIMARY KEY (id);


--
-- Name: identifier_type identifier_type_id_name_key; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.identifier_type
    ADD CONSTRAINT identifier_type_id_name_key UNIQUE (id_name);


--
-- Name: identifier_type identifier_type_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.identifier_type
    ADD CONSTRAINT identifier_type_pkey PRIMARY KEY (id);


--
-- Name: identity_provider_name identity_provider_name_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.identity_provider_name
    ADD CONSTRAINT identity_provider_name_pkey PRIMARY KEY (id);


--
-- Name: identity_provider identity_provider_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.identity_provider
    ADD CONSTRAINT identity_provider_pkey PRIMARY KEY (id);


--
-- Name: identity_provider identity_provider_providerid_unique; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.identity_provider
    ADD CONSTRAINT identity_provider_providerid_unique UNIQUE (providerid);


--
-- Name: institution institution_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.institution
    ADD CONSTRAINT institution_pkey PRIMARY KEY (id);


--
-- Name: internal_sso internal_sso_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.internal_sso
    ADD CONSTRAINT internal_sso_pkey PRIMARY KEY (orcid);


--
-- Name: invalid_issn_group_id_record invalid_issn_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.invalid_issn_group_id_record
    ADD CONSTRAINT invalid_issn_pkey PRIMARY KEY (id);


--
-- Name: invalid_record_data_changes invalid_record_data_changes_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.invalid_record_data_changes
    ADD CONSTRAINT invalid_record_data_changes_pkey PRIMARY KEY (id);


--
-- Name: member_chosen_org_disambiguated member_chosen_org_disambiguated_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.member_chosen_org_disambiguated
    ADD CONSTRAINT member_chosen_org_disambiguated_pkey PRIMARY KEY (org_disambiguated_id);


--
-- Name: member_obo_whitelisted_client member_obo_whitelisted_client_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.member_obo_whitelisted_client
    ADD CONSTRAINT member_obo_whitelisted_client_pkey PRIMARY KEY (id);


--
-- Name: member_obo_whitelisted_client member_obo_whitelisted_clients_unique_constraint; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.member_obo_whitelisted_client
    ADD CONSTRAINT member_obo_whitelisted_clients_unique_constraint UNIQUE (client_details_id, whitelisted_client_details_id);


--
-- Name: notification_item notification_activity_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.notification_item
    ADD CONSTRAINT notification_activity_pkey PRIMARY KEY (id);


--
-- Name: notification notification_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (id);


--
-- Name: notification_work notification_work_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.notification_work
    ADD CONSTRAINT notification_work_pkey PRIMARY KEY (notification_id, work_id);


--
-- Name: oauth2_authoriziation_code_detail oauth2_authoriziation_code_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.oauth2_authoriziation_code_detail
    ADD CONSTRAINT oauth2_authoriziation_code_detail_pkey PRIMARY KEY (authoriziation_code_value);


--
-- Name: oauth2_token_detail oauth2_token_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.oauth2_token_detail
    ADD CONSTRAINT oauth2_token_detail_pkey PRIMARY KEY (id);


--
-- Name: oauth2_token_detail oauth2_token_detail_refresh_token_value_key; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.oauth2_token_detail
    ADD CONSTRAINT oauth2_token_detail_refresh_token_value_key UNIQUE (refresh_token_value);


--
-- Name: orcidoauth2authoriziationcodedetail_authorities orcidoauth2authoriziationcodedetail_authorities_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.orcidoauth2authoriziationcodedetail_authorities
    ADD CONSTRAINT orcidoauth2authoriziationcodedetail_authorities_pkey PRIMARY KEY (orcidoauth2authoriziationcodedetail_authoriziation_code_value, authorities);


--
-- Name: orcidoauth2authoriziationcodedetail_resourceids orcidoauth2authoriziationcodedetail_resourceids_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.orcidoauth2authoriziationcodedetail_resourceids
    ADD CONSTRAINT orcidoauth2authoriziationcodedetail_resourceids_pkey PRIMARY KEY (orcidoauth2authoriziationcodedetail_authoriziation_code_value, resourceids);


--
-- Name: orcidoauth2authoriziationcodedetail_scopes orcidoauth2authoriziationcodedetail_scopes_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.orcidoauth2authoriziationcodedetail_scopes
    ADD CONSTRAINT orcidoauth2authoriziationcodedetail_scopes_pkey PRIMARY KEY (orcidoauth2authoriziationcodedetail_authoriziation_code_value, scopes);


--
-- Name: org_affiliation_relation org_affiliation_relation_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org_affiliation_relation
    ADD CONSTRAINT org_affiliation_relation_pkey PRIMARY KEY (id);


--
-- Name: org_disambiguated_external_identifier org_disambiguated_external_identifier_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org_disambiguated_external_identifier
    ADD CONSTRAINT org_disambiguated_external_identifier_pkey PRIMARY KEY (id);


--
-- Name: org_disambiguated org_disambiguated_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org_disambiguated
    ADD CONSTRAINT org_disambiguated_pkey PRIMARY KEY (id);


--
-- Name: org_import_log org_import_log_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org_import_log
    ADD CONSTRAINT org_import_log_pkey PRIMARY KEY (id);


--
-- Name: org org_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org
    ADD CONSTRAINT org_pkey PRIMARY KEY (id);


--
-- Name: org org_unique_constraints; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org
    ADD CONSTRAINT org_unique_constraints UNIQUE (name, city, region, country, org_disambiguated_id);


--
-- Name: other_name other_name_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.other_name
    ADD CONSTRAINT other_name_pkey PRIMARY KEY (other_name_id);


--
-- Name: patent_contributor patent_contributor_pk; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.patent_contributor
    ADD CONSTRAINT patent_contributor_pk PRIMARY KEY (patent_contributor_id);


--
-- Name: patent patent_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.patent
    ADD CONSTRAINT patent_pkey PRIMARY KEY (patent_id);


--
-- Name: patent_source patent_source_pk; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.patent_source
    ADD CONSTRAINT patent_source_pk PRIMARY KEY (orcid, patent_id, source_orcid);


--
-- Name: peer_review peer_review_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.peer_review
    ADD CONSTRAINT peer_review_pkey PRIMARY KEY (id);


--
-- Name: peer_review_subject peer_review_subject_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.peer_review_subject
    ADD CONSTRAINT peer_review_subject_pkey PRIMARY KEY (id);


--
-- Name: custom_email pk_custom_email; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.custom_email
    ADD CONSTRAINT pk_custom_email PRIMARY KEY (client_details_id, email_type);


--
-- Name: databasechangelog pk_databasechangelog; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.databasechangelog
    ADD CONSTRAINT pk_databasechangelog PRIMARY KEY (id, author, filename);


--
-- Name: databasechangeloglock pk_databasechangeloglock; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.databasechangeloglock
    ADD CONSTRAINT pk_databasechangeloglock PRIMARY KEY (id);


--
-- Name: funding_subtype_to_index pk_funding_subtype_to_index; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.funding_subtype_to_index
    ADD CONSTRAINT pk_funding_subtype_to_index PRIMARY KEY (orcid, subtype);


--
-- Name: orcid_social pk_orcid_social; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.orcid_social
    ADD CONSTRAINT pk_orcid_social PRIMARY KEY (orcid, type);


--
-- Name: affiliation primary_profile_institution_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.affiliation
    ADD CONSTRAINT primary_profile_institution_pkey PRIMARY KEY (institution_id, orcid);


--
-- Name: profile_email_domain profile_email_domain_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_email_domain
    ADD CONSTRAINT profile_email_domain_pkey PRIMARY KEY (id);


--
-- Name: profile_event profile_event_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_event
    ADD CONSTRAINT profile_event_pkey PRIMARY KEY (id);


--
-- Name: profile_funding profile_funding_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_funding
    ADD CONSTRAINT profile_funding_pkey PRIMARY KEY (id);


--
-- Name: profile_history_event profile_history_event_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_history_event
    ADD CONSTRAINT profile_history_event_pkey PRIMARY KEY (id);


--
-- Name: profile_keyword profile_keyword_numeric_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_keyword
    ADD CONSTRAINT profile_keyword_numeric_pkey PRIMARY KEY (id);


--
-- Name: profile_patent profile_patent_pk; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_patent
    ADD CONSTRAINT profile_patent_pk PRIMARY KEY (orcid, patent_id);


--
-- Name: profile profile_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile
    ADD CONSTRAINT profile_pkey PRIMARY KEY (orcid);


--
-- Name: profile_subject profile_subject_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_subject
    ADD CONSTRAINT profile_subject_pkey PRIMARY KEY (profile_orcid, subjects_name);


--
-- Name: record_name record_name_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.record_name
    ADD CONSTRAINT record_name_pkey PRIMARY KEY (id);


--
-- Name: reference_data reference_data_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.reference_data
    ADD CONSTRAINT reference_data_pkey PRIMARY KEY (id);


--
-- Name: rejected_grouping_suggestion rejected_grouping_suggestion_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.rejected_grouping_suggestion
    ADD CONSTRAINT rejected_grouping_suggestion_pkey PRIMARY KEY (put_codes);


--
-- Name: research_resource_item_org research_resource_item_org_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.research_resource_item_org
    ADD CONSTRAINT research_resource_item_org_pkey PRIMARY KEY (research_resource_item_id, org_id);


--
-- Name: research_resource_item research_resource_item_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.research_resource_item
    ADD CONSTRAINT research_resource_item_pkey PRIMARY KEY (id);


--
-- Name: research_resource_org research_resource_org_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.research_resource_org
    ADD CONSTRAINT research_resource_org_pkey PRIMARY KEY (research_resource_id, org_id);


--
-- Name: research_resource research_resource_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.research_resource
    ADD CONSTRAINT research_resource_pkey PRIMARY KEY (id);


--
-- Name: researcher_url researcher_url_orcid_client_source_unique_key; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.researcher_url
    ADD CONSTRAINT researcher_url_orcid_client_source_unique_key UNIQUE (url, orcid, client_source_id);


--
-- Name: researcher_url researcher_url_orcid_source_unique_key; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.researcher_url
    ADD CONSTRAINT researcher_url_orcid_source_unique_key UNIQUE (url, orcid, source_id);


--
-- Name: researcher_url researcher_url_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.researcher_url
    ADD CONSTRAINT researcher_url_pkey PRIMARY KEY (id);


--
-- Name: salesforce_connection salesforce_connection_orcid_salesforce_account_id_unique; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.salesforce_connection
    ADD CONSTRAINT salesforce_connection_orcid_salesforce_account_id_unique UNIQUE (orcid, salesforce_account_id);


--
-- Name: salesforce_connection salesforce_connection_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.salesforce_connection
    ADD CONSTRAINT salesforce_connection_pkey PRIMARY KEY (id);


--
-- Name: shibboleth_account shibboleth_account_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.shibboleth_account
    ADD CONSTRAINT shibboleth_account_pkey PRIMARY KEY (id);


--
-- Name: shibboleth_account shibboleth_account_remote_user_idp_unique; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.shibboleth_account
    ADD CONSTRAINT shibboleth_account_remote_user_idp_unique UNIQUE (remote_user, shib_identity_provider);


--
-- Name: spam spam_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.spam
    ADD CONSTRAINT spam_pkey PRIMARY KEY (id);


--
-- Name: orcid_props statistic_key_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.orcid_props
    ADD CONSTRAINT statistic_key_pkey PRIMARY KEY (key);


--
-- Name: statistic_values statistic_values_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.statistic_values
    ADD CONSTRAINT statistic_values_pkey PRIMARY KEY (id);


--
-- Name: statistic_key stats_key_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.statistic_key
    ADD CONSTRAINT stats_key_pkey PRIMARY KEY (id);


--
-- Name: subject subject_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.subject
    ADD CONSTRAINT subject_pkey PRIMARY KEY (name);


--
-- Name: external_identifier unique_external_identifiers_allowing_multiple_sources; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.external_identifier
    ADD CONSTRAINT unique_external_identifiers_allowing_multiple_sources UNIQUE (orcid, external_id_reference, external_id_type, source_id, client_source_id);


--
-- Name: oauth2_token_detail unique_token_value; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.oauth2_token_detail
    ADD CONSTRAINT unique_token_value UNIQUE (token_value);


--
-- Name: org_disambiguated_external_identifier uq_org_disambiguated_identifier_type; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org_disambiguated_external_identifier
    ADD CONSTRAINT uq_org_disambiguated_identifier_type UNIQUE (org_disambiguated_id, identifier, identifier_type);


--
-- Name: userconnection userconnection_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.userconnection
    ADD CONSTRAINT userconnection_pkey PRIMARY KEY (userid, providerid, provideruserid);


--
-- Name: validated_public_profile validated_public_profile_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.validated_public_profile
    ADD CONSTRAINT validated_public_profile_pkey PRIMARY KEY (orcid);


--
-- Name: webhook webhook_pk; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.webhook
    ADD CONSTRAINT webhook_pk PRIMARY KEY (orcid, uri);


--
-- Name: work work_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.work
    ADD CONSTRAINT work_pkey PRIMARY KEY (work_id);


--
-- Name: address_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX address_orcid_idx ON public.address USING btree (orcid);


--
-- Name: biography_orcid_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX biography_orcid_index ON public.biography USING btree (orcid);


--
-- Name: client_authorised_grant_type_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX client_authorised_grant_type_id_idx ON public.client_authorised_grant_type USING btree (client_details_id, grant_type);


--
-- Name: client_details_group_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX client_details_group_orcid_idx ON public.client_details USING btree (group_orcid);


--
-- Name: client_details_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX client_details_id_idx ON public.client_details USING btree (client_details_id, client_secret);


--
-- Name: client_granted_authority_client_details_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX client_granted_authority_client_details_id_idx ON public.client_granted_authority USING btree (client_details_id);


--
-- Name: client_granted_authority_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX client_granted_authority_id_idx ON public.client_granted_authority USING btree (client_details_id, granted_authority);


--
-- Name: client_redirect_uri_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX client_redirect_uri_id_idx ON public.client_redirect_uri USING btree (client_details_id, redirect_uri);


--
-- Name: client_resource_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX client_resource_id_idx ON public.client_resource_id USING btree (client_details_id, resource_id);


--
-- Name: client_scope_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX client_scope_id_idx ON public.client_scope USING btree (client_details_id, scope_type);


--
-- Name: email_domain_domain_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX email_domain_domain_index ON public.email_domain USING btree (email_domain);


--
-- Name: email_domain_ror_id_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX email_domain_ror_id_index ON public.email_domain USING btree (ror_id);


--
-- Name: email_event_email_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX email_event_email_idx ON public.email_event USING btree (email);


--
-- Name: email_frequency_orcid_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX email_frequency_orcid_index ON public.email_frequency USING btree (orcid);


--
-- Name: event_client_id_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX event_client_id_index ON public.event USING btree (client_id);


--
-- Name: event_date_created_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX event_date_created_idx ON public.event USING btree (date_created);


--
-- Name: event_type_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX event_type_index ON public.event USING btree (event_type);


--
-- Name: external_identifier_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX external_identifier_orcid_idx ON public.external_identifier USING btree (orcid);


--
-- Name: given_permission_to_giver_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX given_permission_to_giver_orcid_idx ON public.given_permission_to USING btree (giver_orcid);


--
-- Name: given_permission_to_receiver_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX given_permission_to_receiver_orcid_idx ON public.given_permission_to USING btree (receiver_orcid);


--
-- Name: granted_authority_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX granted_authority_orcid_idx ON public.granted_authority USING btree (orcid);


--
-- Name: group_id_lowercase_unique_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE UNIQUE INDEX group_id_lowercase_unique_idx ON public.group_id_record USING btree (lower(group_id));


--
-- Name: group_id_record_date_created_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX group_id_record_date_created_idx ON public.group_id_record USING btree (date_created);


--
-- Name: group_id_record_group_type_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX group_id_record_group_type_idx ON public.group_id_record USING btree (group_type);


--
-- Name: group_id_record_issn_loader_fail_count_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX group_id_record_issn_loader_fail_count_index ON public.group_id_record USING btree (issn_loader_fail_count);


--
-- Name: group_id_record_sync_date_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX group_id_record_sync_date_index ON public.group_id_record USING btree (sync_date);


--
-- Name: internal_sso_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX internal_sso_orcid_idx ON public.internal_sso USING btree (orcid);


--
-- Name: invalid_record_data_changes_date_created_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX invalid_record_data_changes_date_created_index ON public.invalid_record_data_changes USING btree (date_created);


--
-- Name: invalid_record_data_changes_seq_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX invalid_record_data_changes_seq_index ON public.invalid_record_data_changes USING btree (id);


--
-- Name: lower_case_email_unique2; Type: INDEX; Schema: public; Owner: orcid
--

CREATE UNIQUE INDEX lower_case_email_unique2 ON public.email USING btree (lower(email));


--
-- Name: notification_archived_date_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX notification_archived_date_index ON public.notification USING btree (archived_date);


--
-- Name: notification_authentication_provider_id; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX notification_authentication_provider_id ON public.notification USING btree (authentication_provider_id);


--
-- Name: notification_client_source_id; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX notification_client_source_id ON public.notification USING btree (client_source_id);


--
-- Name: notification_date_created_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX notification_date_created_index ON public.notification USING btree (date_created);


--
-- Name: notification_item_notification_id_index_v2; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX notification_item_notification_id_index_v2 ON public.notification_item USING btree (notification_id);


--
-- Name: notification_orcid_index_v2; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX notification_orcid_index_v2 ON public.notification USING btree (orcid);


--
-- Name: notification_read_date_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX notification_read_date_index ON public.notification USING btree (read_date);


--
-- Name: notification_sent_date_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX notification_sent_date_index ON public.notification USING btree (sent_date);


--
-- Name: notification_type_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX notification_type_index ON public.notification USING btree (notification_type);


--
-- Name: oauth2_token_detail_authorization_code_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX oauth2_token_detail_authorization_code_idx ON public.oauth2_token_detail USING btree (authorization_code);


--
-- Name: oauth2_token_detail_last_modified_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX oauth2_token_detail_last_modified_idx ON public.oauth2_token_detail USING btree (last_modified);


--
-- Name: oauth2_token_detail_obo_client_details_id_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX oauth2_token_detail_obo_client_details_id_index ON public.oauth2_token_detail USING btree (obo_client_details_id);


--
-- Name: orcidoauth2authoriziationcodedetail_authoriziation_code_value_i; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX orcidoauth2authoriziationcodedetail_authoriziation_code_value_i ON public.orcidoauth2authoriziationcodedetail_authorities USING btree (orcidoauth2authoriziationcodedetail_authoriziation_code_value);


--
-- Name: orcidoauth2authoriziationcodedetail_resourceids_code_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX orcidoauth2authoriziationcodedetail_resourceids_code_idx ON public.orcidoauth2authoriziationcodedetail_resourceids USING btree (orcidoauth2authoriziationcodedetail_authoriziation_code_value);


--
-- Name: orcidoauth2authoriziationcodedetail_scopes_code_value_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX orcidoauth2authoriziationcodedetail_scopes_code_value_idx ON public.orcidoauth2authoriziationcodedetail_scopes USING btree (orcidoauth2authoriziationcodedetail_authoriziation_code_value);


--
-- Name: org_affiliation_relation_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX org_affiliation_relation_orcid_idx ON public.org_affiliation_relation USING btree (orcid);


--
-- Name: org_disambiguated_external_identifier_org_disambiguated_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX org_disambiguated_external_identifier_org_disambiguated_id_idx ON public.org_disambiguated_external_identifier USING btree (org_disambiguated_id);


--
-- Name: org_disambiguated_source_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX org_disambiguated_source_id_idx ON public.org_disambiguated USING btree (source_id);


--
-- Name: org_disambiguated_source_parent_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX org_disambiguated_source_parent_id_idx ON public.org_disambiguated USING btree (source_parent_id);


--
-- Name: org_disambiguated_source_type_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX org_disambiguated_source_type_idx ON public.org_disambiguated USING btree (source_type);


--
-- Name: other_name_orcid_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX other_name_orcid_index ON public.other_name USING btree (orcid);


--
-- Name: peer_review_display_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX peer_review_display_index ON public.peer_review USING btree (display_index);


--
-- Name: peer_review_orcid_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX peer_review_orcid_index ON public.peer_review USING btree (orcid);


--
-- Name: primary_profile_institution_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX primary_profile_institution_orcid_idx ON public.affiliation USING btree (orcid);


--
-- Name: profile_email_domain_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX profile_email_domain_index ON public.profile_email_domain USING btree (email_domain);


--
-- Name: profile_email_domain_orcid_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX profile_email_domain_orcid_index ON public.profile_email_domain USING btree (orcid);


--
-- Name: profile_funding_display_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX profile_funding_display_index ON public.profile_funding USING btree (display_index);


--
-- Name: profile_funding_orcid_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX profile_funding_orcid_index ON public.profile_funding USING btree (orcid);


--
-- Name: profile_funding_org_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX profile_funding_org_id_idx ON public.profile_funding USING btree (org_id);


--
-- Name: profile_indexing_status_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX profile_indexing_status_idx ON public.profile USING btree (indexing_status);


--
-- Name: profile_keyword_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX profile_keyword_orcid_idx ON public.profile_keyword USING btree (profile_orcid);


--
-- Name: profile_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX profile_orcid_idx ON public.profile USING btree (orcid);


--
-- Name: profile_orcid_type_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX profile_orcid_type_idx ON public.profile USING btree (orcid_type);


--
-- Name: profile_subject_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX profile_subject_orcid_idx ON public.profile_subject USING btree (profile_orcid);


--
-- Name: record_name_credit_name_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX record_name_credit_name_idx ON public.record_name USING btree (credit_name);


--
-- Name: record_name_orcid_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX record_name_orcid_index ON public.record_name USING btree (orcid);


--
-- Name: rejected_grouping_suggestion_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX rejected_grouping_suggestion_orcid_idx ON public.rejected_grouping_suggestion USING btree (orcid);


--
-- Name: research_resource_item_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX research_resource_item_index ON public.research_resource_item USING btree (research_resource_id);


--
-- Name: research_resource_orcid_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX research_resource_orcid_index ON public.research_resource USING btree (orcid);


--
-- Name: researcher_url_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX researcher_url_orcid_idx ON public.researcher_url USING btree (orcid);


--
-- Name: salesforce_connection_account_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX salesforce_connection_account_id_idx ON public.salesforce_connection USING btree (salesforce_account_id);


--
-- Name: spam_orcid_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX spam_orcid_index ON public.spam USING btree (orcid);


--
-- Name: statistic_values_key_id_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX statistic_values_key_id_idx ON public.statistic_values USING btree (key_id);


--
-- Name: token_authentication_key_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX token_authentication_key_index ON public.oauth2_token_detail USING btree (authentication_key);


--
-- Name: token_client_details_id_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX token_client_details_id_index ON public.oauth2_token_detail USING btree (client_details_id);


--
-- Name: token_orcid_index; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX token_orcid_index ON public.oauth2_token_detail USING btree (user_orcid);


--
-- Name: userconnectionrank; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX userconnectionrank ON public.userconnection USING btree (userid, providerid, rank);


--
-- Name: work_doi_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX work_doi_idx ON public.work USING btree (public.extract_doi(external_ids_json));


--
-- Name: work_language_code_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX work_language_code_idx ON public.work USING btree (language_code);


--
-- Name: work_orcid_display_index_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX work_orcid_display_index_idx ON public.work USING btree (orcid, display_index);


--
-- Name: work_orcid_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX work_orcid_idx ON public.work USING btree (orcid);


--
-- Name: work_translated_title_language_code_idx; Type: INDEX; Schema: public; Owner: orcid
--

CREATE INDEX work_translated_title_language_code_idx ON public.work USING btree (translated_title_language_code);


--
-- Name: address address_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: address address_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: address address_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_source_id_fk FOREIGN KEY (source_id) REFERENCES public.profile(orcid);


--
-- Name: biography biography_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.biography
    ADD CONSTRAINT biography_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: client_authorised_grant_type client_details_authorised_grant_type_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_authorised_grant_type
    ADD CONSTRAINT client_details_authorised_grant_type_fk FOREIGN KEY (client_details_id) REFERENCES public.client_details(client_details_id) ON DELETE CASCADE;


--
-- Name: client_granted_authority client_details_client_granted_authority_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_granted_authority
    ADD CONSTRAINT client_details_client_granted_authority_fk FOREIGN KEY (client_details_id) REFERENCES public.client_details(client_details_id) ON DELETE CASCADE;


--
-- Name: client_details client_details_group_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_details
    ADD CONSTRAINT client_details_group_orcid_fk FOREIGN KEY (group_orcid) REFERENCES public.profile(orcid);


--
-- Name: client_redirect_uri client_redirect_uri_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_redirect_uri
    ADD CONSTRAINT client_redirect_uri_fk FOREIGN KEY (client_details_id) REFERENCES public.client_details(client_details_id) ON DELETE CASCADE;


--
-- Name: client_resource_id client_resource_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_resource_id
    ADD CONSTRAINT client_resource_id_fk FOREIGN KEY (client_details_id) REFERENCES public.client_details(client_details_id) ON DELETE CASCADE;


--
-- Name: client_scope client_scope_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_scope
    ADD CONSTRAINT client_scope_fk FOREIGN KEY (client_details_id) REFERENCES public.client_details(client_details_id) ON DELETE CASCADE;


--
-- Name: client_secret client_secret_client_details_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.client_secret
    ADD CONSTRAINT client_secret_client_details_id_fk FOREIGN KEY (client_details_id) REFERENCES public.client_details(client_details_id);


--
-- Name: email email_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.email
    ADD CONSTRAINT email_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: email_frequency email_frequency_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.email_frequency
    ADD CONSTRAINT email_frequency_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: email email_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.email
    ADD CONSTRAINT email_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: email email_source_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.email
    ADD CONSTRAINT email_source_orcid_fk FOREIGN KEY (source_id) REFERENCES public.profile(orcid);


--
-- Name: external_identifier external_identifier_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.external_identifier
    ADD CONSTRAINT external_identifier_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: external_identifier external_identifier_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.external_identifier
    ADD CONSTRAINT external_identifier_source_id_fk FOREIGN KEY (source_id) REFERENCES public.profile(orcid);


--
-- Name: find_my_stuff_history find_my_stuff_history_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.find_my_stuff_history
    ADD CONSTRAINT find_my_stuff_history_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: profile_subject fk1d5ccc962d6b1fe4; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_subject
    ADD CONSTRAINT fk1d5ccc962d6b1fe4 FOREIGN KEY (subjects_name) REFERENCES public.subject(name);


--
-- Name: profile_subject fk1d5ccc9680ddc983; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_subject
    ADD CONSTRAINT fk1d5ccc9680ddc983 FOREIGN KEY (profile_orcid) REFERENCES public.profile(orcid);


--
-- Name: institution fk3529a5b8e84caef; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.institution
    ADD CONSTRAINT fk3529a5b8e84caef FOREIGN KEY (address_id) REFERENCES public.address(id);


--
-- Name: affiliation fk408de65b2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.affiliation
    ADD CONSTRAINT fk408de65b2007f99 FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: affiliation fk408de65cf1a386f; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.affiliation
    ADD CONSTRAINT fk408de65cf1a386f FOREIGN KEY (institution_id) REFERENCES public.institution(id);


--
-- Name: profile_keyword fk5c27955380ddc983; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_keyword
    ADD CONSTRAINT fk5c27955380ddc983 FOREIGN KEY (profile_orcid) REFERENCES public.profile(orcid);


--
-- Name: external_identifier fk641fe19db2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.external_identifier
    ADD CONSTRAINT fk641fe19db2007f99 FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: statistic_values fk9bb60ebf14b94af; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.statistic_values
    ADD CONSTRAINT fk9bb60ebf14b94af FOREIGN KEY (key_id) REFERENCES public.statistic_key(id);


--
-- Name: researcher_url fkd433c438b2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.researcher_url
    ADD CONSTRAINT fkd433c438b2007f99 FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: profile fked8e89a9d6bc0bfe; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile
    ADD CONSTRAINT fked8e89a9d6bc0bfe FOREIGN KEY (source_id) REFERENCES public.profile(orcid);


--
-- Name: other_name fkf5209e5ab2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.other_name
    ADD CONSTRAINT fkf5209e5ab2007f99 FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: funding_external_identifier funding_external_identifiers_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.funding_external_identifier
    ADD CONSTRAINT funding_external_identifiers_fk FOREIGN KEY (profile_funding_id) REFERENCES public.profile_funding(id) ON DELETE CASCADE;


--
-- Name: funding_subtype_to_index funding_subtype_to_index_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.funding_subtype_to_index
    ADD CONSTRAINT funding_subtype_to_index_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: given_permission_to giver_orcid_to_profile_orcid; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.given_permission_to
    ADD CONSTRAINT giver_orcid_to_profile_orcid FOREIGN KEY (giver_orcid) REFERENCES public.profile(orcid);


--
-- Name: identity_provider_name identity_provider_name_identity_provider_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.identity_provider_name
    ADD CONSTRAINT identity_provider_name_identity_provider_id_fk FOREIGN KEY (identity_provider_id) REFERENCES public.identity_provider(id);


--
-- Name: profile_keyword keyword_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_keyword
    ADD CONSTRAINT keyword_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: profile_keyword keyword_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_keyword
    ADD CONSTRAINT keyword_source_id_fk FOREIGN KEY (source_id) REFERENCES public.profile(orcid);


--
-- Name: custom_email member_custom_email_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.custom_email
    ADD CONSTRAINT member_custom_email_fk FOREIGN KEY (client_details_id) REFERENCES public.client_details(client_details_id);


--
-- Name: member_obo_whitelisted_client member_obo_client_details_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.member_obo_whitelisted_client
    ADD CONSTRAINT member_obo_client_details_fk FOREIGN KEY (client_details_id) REFERENCES public.client_details(client_details_id);


--
-- Name: member_obo_whitelisted_client member_obo_whitelisted_client_details_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.member_obo_whitelisted_client
    ADD CONSTRAINT member_obo_whitelisted_client_details_fk FOREIGN KEY (whitelisted_client_details_id) REFERENCES public.client_details(client_details_id);


--
-- Name: notification_item notification_activity_notification_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.notification_item
    ADD CONSTRAINT notification_activity_notification_fk FOREIGN KEY (notification_id) REFERENCES public.notification(id);


--
-- Name: notification notification_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: notification notification_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: notification notification_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_source_id_fk FOREIGN KEY (source_id) REFERENCES public.profile(orcid);


--
-- Name: notification_work notification_work; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.notification_work
    ADD CONSTRAINT notification_work FOREIGN KEY (work_id) REFERENCES public.work(work_id);


--
-- Name: notification_work notification_work_notification_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.notification_work
    ADD CONSTRAINT notification_work_notification_id_fk FOREIGN KEY (notification_id) REFERENCES public.notification(id);


--
-- Name: oauth2_authoriziation_code_detail oauth2_authoriziation_code_detail_client_details_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.oauth2_authoriziation_code_detail
    ADD CONSTRAINT oauth2_authoriziation_code_detail_client_details_fk FOREIGN KEY (client_details_id) REFERENCES public.client_details(client_details_id) ON DELETE CASCADE;


--
-- Name: oauth2_authoriziation_code_detail oauth2_authoriziation_code_detail_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.oauth2_authoriziation_code_detail
    ADD CONSTRAINT oauth2_authoriziation_code_detail_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid) ON DELETE CASCADE;


--
-- Name: oauth2_token_detail oauth2_token_detail_client_details_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.oauth2_token_detail
    ADD CONSTRAINT oauth2_token_detail_client_details_fk FOREIGN KEY (client_details_id) REFERENCES public.client_details(client_details_id);


--
-- Name: oauth2_token_detail oauth2_token_detail_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.oauth2_token_detail
    ADD CONSTRAINT oauth2_token_detail_orcid_fk FOREIGN KEY (user_orcid) REFERENCES public.profile(orcid);


--
-- Name: orcidoauth2authoriziationcodedetail_authorities oauth2authoriziationcodedetail_authorities_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.orcidoauth2authoriziationcodedetail_authorities
    ADD CONSTRAINT oauth2authoriziationcodedetail_authorities_fk FOREIGN KEY (orcidoauth2authoriziationcodedetail_authoriziation_code_value) REFERENCES public.oauth2_authoriziation_code_detail(authoriziation_code_value) ON DELETE CASCADE;


--
-- Name: orcidoauth2authoriziationcodedetail_resourceids oauth2authoriziationcodedetail_resourceids_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.orcidoauth2authoriziationcodedetail_resourceids
    ADD CONSTRAINT oauth2authoriziationcodedetail_resourceids_fk FOREIGN KEY (orcidoauth2authoriziationcodedetail_authoriziation_code_value) REFERENCES public.oauth2_authoriziation_code_detail(authoriziation_code_value) ON DELETE CASCADE;


--
-- Name: orcidoauth2authoriziationcodedetail_scopes oauth2authoriziationcodedetail_scopes_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.orcidoauth2authoriziationcodedetail_scopes
    ADD CONSTRAINT oauth2authoriziationcodedetail_scopes_fk FOREIGN KEY (orcidoauth2authoriziationcodedetail_authoriziation_code_value) REFERENCES public.oauth2_authoriziation_code_detail(authoriziation_code_value) ON DELETE CASCADE;


--
-- Name: oauth2_token_detail obo_client_details_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.oauth2_token_detail
    ADD CONSTRAINT obo_client_details_id_fk FOREIGN KEY (obo_client_details_id) REFERENCES public.client_details(client_details_id);


--
-- Name: orcid_social orcid_social_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.orcid_social
    ADD CONSTRAINT orcid_social_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: org_affiliation_relation org_affiliation_relation_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org_affiliation_relation
    ADD CONSTRAINT org_affiliation_relation_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: org_affiliation_relation org_affiliation_relation_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org_affiliation_relation
    ADD CONSTRAINT org_affiliation_relation_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: org_affiliation_relation org_affiliation_relation_org_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org_affiliation_relation
    ADD CONSTRAINT org_affiliation_relation_org_id_fk FOREIGN KEY (org_id) REFERENCES public.org(id);


--
-- Name: org org_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org
    ADD CONSTRAINT org_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: org_disambiguated_external_identifier org_disambiguated_external_identifier_org_disambiguated_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org_disambiguated_external_identifier
    ADD CONSTRAINT org_disambiguated_external_identifier_org_disambiguated_fk FOREIGN KEY (org_disambiguated_id) REFERENCES public.org_disambiguated(id);


--
-- Name: org org_org_disambiguated_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.org
    ADD CONSTRAINT org_org_disambiguated_fk FOREIGN KEY (org_disambiguated_id) REFERENCES public.org_disambiguated(id);


--
-- Name: other_name other_name_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.other_name
    ADD CONSTRAINT other_name_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: other_name other_name_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.other_name
    ADD CONSTRAINT other_name_source_id_fk FOREIGN KEY (source_id) REFERENCES public.profile(orcid);


--
-- Name: patent_contributor patent_contributor_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.patent_contributor
    ADD CONSTRAINT patent_contributor_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: patent_contributor patent_contributor_patent_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.patent_contributor
    ADD CONSTRAINT patent_contributor_patent_fk FOREIGN KEY (patent_id) REFERENCES public.patent(patent_id);


--
-- Name: patent_source patent_source_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.patent_source
    ADD CONSTRAINT patent_source_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: patent_source patent_source_patent_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.patent_source
    ADD CONSTRAINT patent_source_patent_fk FOREIGN KEY (patent_id) REFERENCES public.patent(patent_id);


--
-- Name: patent_source patent_source_source_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.patent_source
    ADD CONSTRAINT patent_source_source_orcid_fk FOREIGN KEY (source_orcid) REFERENCES public.profile(orcid);


--
-- Name: peer_review peer_review_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.peer_review
    ADD CONSTRAINT peer_review_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: peer_review peer_review_org_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.peer_review
    ADD CONSTRAINT peer_review_org_id_fk FOREIGN KEY (org_id) REFERENCES public.org(id);


--
-- Name: profile profile_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile
    ADD CONSTRAINT profile_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: profile profile_deprecating_admin_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile
    ADD CONSTRAINT profile_deprecating_admin_fk FOREIGN KEY (deprecating_admin) REFERENCES public.profile(orcid);


--
-- Name: profile_email_domain profile_email_domain_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_email_domain
    ADD CONSTRAINT profile_email_domain_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: profile_event profile_event_orcid; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_event
    ADD CONSTRAINT profile_event_orcid FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: profile_funding profile_funding_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_funding
    ADD CONSTRAINT profile_funding_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: profile_funding profile_funding_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_funding
    ADD CONSTRAINT profile_funding_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: profile_funding profile_funding_org_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_funding
    ADD CONSTRAINT profile_funding_org_id_fk FOREIGN KEY (org_id) REFERENCES public.org(id);


--
-- Name: profile_patent profile_patent_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_patent
    ADD CONSTRAINT profile_patent_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: profile_patent profile_patent_patent_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.profile_patent
    ADD CONSTRAINT profile_patent_patent_fk FOREIGN KEY (patent_id) REFERENCES public.patent(patent_id);


--
-- Name: given_permission_to receiver_orcid_to_profile_orcid; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.given_permission_to
    ADD CONSTRAINT receiver_orcid_to_profile_orcid FOREIGN KEY (receiver_orcid) REFERENCES public.profile(orcid);


--
-- Name: record_name record_name_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.record_name
    ADD CONSTRAINT record_name_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: research_resource_item research_resource_item_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.research_resource_item
    ADD CONSTRAINT research_resource_item_fk FOREIGN KEY (research_resource_id) REFERENCES public.research_resource(id);


--
-- Name: research_resource_item_org research_resource_item_org_fk1; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.research_resource_item_org
    ADD CONSTRAINT research_resource_item_org_fk1 FOREIGN KEY (research_resource_item_id) REFERENCES public.research_resource_item(id);


--
-- Name: research_resource_item_org research_resource_item_org_fk2; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.research_resource_item_org
    ADD CONSTRAINT research_resource_item_org_fk2 FOREIGN KEY (org_id) REFERENCES public.org(id);


--
-- Name: research_resource research_resource_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.research_resource
    ADD CONSTRAINT research_resource_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: research_resource_org research_resource_org_fk1; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.research_resource_org
    ADD CONSTRAINT research_resource_org_fk1 FOREIGN KEY (research_resource_id) REFERENCES public.research_resource(id);


--
-- Name: research_resource_org research_resource_org_fk2; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.research_resource_org
    ADD CONSTRAINT research_resource_org_fk2 FOREIGN KEY (org_id) REFERENCES public.org(id);


--
-- Name: researcher_url researcher_url_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.researcher_url
    ADD CONSTRAINT researcher_url_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: researcher_url researcher_url_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.researcher_url
    ADD CONSTRAINT researcher_url_source_id_fk FOREIGN KEY (source_id) REFERENCES public.profile(orcid);


--
-- Name: shibboleth_account shibboleth_account_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.shibboleth_account
    ADD CONSTRAINT shibboleth_account_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: spam spam_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.spam
    ADD CONSTRAINT spam_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: webhook webhook_client_details_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.webhook
    ADD CONSTRAINT webhook_client_details_fk FOREIGN KEY (client_details_id) REFERENCES public.client_details(client_details_id);


--
-- Name: webhook webhook_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.webhook
    ADD CONSTRAINT webhook_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: work work_client_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.work
    ADD CONSTRAINT work_client_source_id_fk FOREIGN KEY (client_source_id) REFERENCES public.client_details(client_details_id);


--
-- Name: work work_orcid_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.work
    ADD CONSTRAINT work_orcid_fk FOREIGN KEY (orcid) REFERENCES public.profile(orcid);


--
-- Name: work work_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY public.work
    ADD CONSTRAINT work_source_id_fk FOREIGN KEY (source_id) REFERENCES public.profile(orcid);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: TABLE address; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.address TO orcidro;


--
-- Name: TABLE affiliation; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.affiliation TO orcidro;


--
-- Name: TABLE org; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.org TO orcidro;


--
-- Name: TABLE org_affiliation_relation; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.org_affiliation_relation TO orcidro;


--
-- Name: TABLE ambiguous_org; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.ambiguous_org TO orcidro;


--
-- Name: TABLE backup_code; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.backup_code TO orcidro;


--
-- Name: TABLE biography; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.biography TO orcidro;


--
-- Name: TABLE client_authorised_grant_type; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.client_authorised_grant_type TO orcidro;


--
-- Name: TABLE client_details; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.client_details TO orcidro;


--
-- Name: TABLE client_granted_authority; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.client_granted_authority TO orcidro;


--
-- Name: TABLE client_redirect_uri; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.client_redirect_uri TO orcidro;


--
-- Name: TABLE client_resource_id; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.client_resource_id TO orcidro;


--
-- Name: TABLE client_scope; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.client_scope TO orcidro;


--
-- Name: TABLE client_secret; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.client_secret TO orcidro;


--
-- Name: TABLE country_reference_data; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.country_reference_data TO orcidro;


--
-- Name: TABLE custom_email; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.custom_email TO orcidro;


--
-- Name: TABLE databasechangelog; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.databasechangelog TO orcidro;


--
-- Name: TABLE databasechangeloglock; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.databasechangeloglock TO orcidro;


--
-- Name: TABLE dw_active_users; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.dw_active_users TO dw_user;
GRANT SELECT ON TABLE public.dw_active_users TO orcidro;


--
-- Name: TABLE dw_address; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_address TO dw_user;
GRANT SELECT ON TABLE public.dw_address TO orcidro;


--
-- Name: TABLE dw_biography; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_biography TO dw_user;
GRANT SELECT ON TABLE public.dw_biography TO orcidro;


--
-- Name: TABLE dw_client_details; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_client_details TO dw_user;
GRANT SELECT ON TABLE public.dw_client_details TO orcidro;


--
-- Name: TABLE dw_client_redirect_uri; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_client_redirect_uri TO dw_user;
GRANT SELECT ON TABLE public.dw_client_redirect_uri TO orcidro;


--
-- Name: TABLE email; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.email TO orcidro;


--
-- Name: TABLE dw_email; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_email TO dw_user;
GRANT SELECT ON TABLE public.dw_email TO orcidro;


--
-- Name: TABLE event_stats; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.event_stats TO orcidro;


--
-- Name: TABLE dw_event_stats; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_event_stats TO dw_user;
GRANT SELECT ON TABLE public.dw_event_stats TO orcidro;


--
-- Name: TABLE external_identifier; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.external_identifier TO orcidro;


--
-- Name: TABLE dw_external_identifier; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_external_identifier TO dw_user;
GRANT SELECT ON TABLE public.dw_external_identifier TO orcidro;


--
-- Name: TABLE given_permission_to; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.given_permission_to TO orcidro;


--
-- Name: TABLE dw_given_permission_to; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_given_permission_to TO dw_user;
GRANT SELECT ON TABLE public.dw_given_permission_to TO orcidro;


--
-- Name: TABLE group_id_record; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.group_id_record TO orcidro;


--
-- Name: TABLE dw_group_id_record; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_group_id_record TO dw_user;
GRANT SELECT ON TABLE public.dw_group_id_record TO orcidro;


--
-- Name: TABLE identifier_type; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.identifier_type TO orcidro;


--
-- Name: TABLE dw_identifier_type; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_identifier_type TO dw_user;
GRANT SELECT ON TABLE public.dw_identifier_type TO orcidro;


--
-- Name: TABLE identity_provider; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.identity_provider TO orcidro;


--
-- Name: TABLE dw_identity_provider; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_identity_provider TO dw_user;
GRANT SELECT ON TABLE public.dw_identity_provider TO orcidro;


--
-- Name: TABLE notification; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.notification TO orcidro;


--
-- Name: TABLE dw_notification; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_notification TO dw_user;
GRANT SELECT ON TABLE public.dw_notification TO orcidro;


--
-- Name: TABLE oauth2_token_detail; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.oauth2_token_detail TO orcidro;


--
-- Name: TABLE dw_oauth2_token_detail; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_oauth2_token_detail TO dw_user;
GRANT SELECT ON TABLE public.dw_oauth2_token_detail TO orcidro;


--
-- Name: TABLE dw_org; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_org TO dw_user;
GRANT SELECT ON TABLE public.dw_org TO orcidro;


--
-- Name: TABLE dw_org_affiliation_relation; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_org_affiliation_relation TO dw_user;
GRANT SELECT ON TABLE public.dw_org_affiliation_relation TO orcidro;


--
-- Name: TABLE org_disambiguated; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.org_disambiguated TO orcidro;


--
-- Name: TABLE dw_org_disambiguated; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_org_disambiguated TO dw_user;
GRANT SELECT ON TABLE public.dw_org_disambiguated TO orcidro;


--
-- Name: TABLE org_disambiguated_external_identifier; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.org_disambiguated_external_identifier TO orcidro;


--
-- Name: TABLE dw_org_disambiguated_external_identifier; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_org_disambiguated_external_identifier TO dw_user;
GRANT SELECT ON TABLE public.dw_org_disambiguated_external_identifier TO orcidro;


--
-- Name: TABLE other_name; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.other_name TO orcidro;


--
-- Name: TABLE dw_other_name; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_other_name TO dw_user;
GRANT SELECT ON TABLE public.dw_other_name TO orcidro;


--
-- Name: TABLE dw_papi_event_stats; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_papi_event_stats TO dw_user;
GRANT SELECT ON TABLE public.dw_papi_event_stats TO orcidro;


--
-- Name: TABLE peer_review; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.peer_review TO orcidro;


--
-- Name: TABLE dw_peer_review; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_peer_review TO dw_user;
GRANT SELECT ON TABLE public.dw_peer_review TO orcidro;


--
-- Name: TABLE profile; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.profile TO orcidro;
GRANT SELECT ON TABLE public.profile TO dw_user;


--
-- Name: TABLE dw_profile; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_profile TO dw_user;
GRANT SELECT ON TABLE public.dw_profile TO orcidro;


--
-- Name: TABLE profile_email_domain; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.profile_email_domain TO orcidro;


--
-- Name: TABLE dw_profile_email_domain; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_profile_email_domain TO dw_user;
GRANT SELECT ON TABLE public.dw_profile_email_domain TO orcidro;


--
-- Name: TABLE profile_funding; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.profile_funding TO orcidro;


--
-- Name: TABLE dw_profile_funding; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_profile_funding TO dw_user;
GRANT SELECT ON TABLE public.dw_profile_funding TO orcidro;


--
-- Name: TABLE profile_history_event; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.profile_history_event TO orcidro;


--
-- Name: TABLE dw_profile_history_event; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_profile_history_event TO dw_user;
GRANT SELECT ON TABLE public.dw_profile_history_event TO orcidro;


--
-- Name: TABLE profile_keyword; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.profile_keyword TO orcidro;


--
-- Name: TABLE dw_profile_keyword; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_profile_keyword TO dw_user;
GRANT SELECT ON TABLE public.dw_profile_keyword TO orcidro;


--
-- Name: TABLE record_name; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.record_name TO orcidro;


--
-- Name: TABLE dw_record_name; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_record_name TO dw_user;
GRANT SELECT ON TABLE public.dw_record_name TO orcidro;


--
-- Name: TABLE research_resource; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.research_resource TO orcidro;


--
-- Name: TABLE dw_research_resource; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_research_resource TO dw_user;
GRANT SELECT ON TABLE public.dw_research_resource TO orcidro;


--
-- Name: TABLE research_resource_item; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.research_resource_item TO orcidro;


--
-- Name: TABLE dw_research_resource_item; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_research_resource_item TO dw_user;
GRANT SELECT ON TABLE public.dw_research_resource_item TO orcidro;


--
-- Name: TABLE research_resource_item_org; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.research_resource_item_org TO orcidro;


--
-- Name: TABLE dw_research_resource_item_org; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_research_resource_item_org TO dw_user;
GRANT SELECT ON TABLE public.dw_research_resource_item_org TO orcidro;


--
-- Name: TABLE research_resource_org; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.research_resource_org TO orcidro;


--
-- Name: TABLE dw_research_resource_org; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_research_resource_org TO dw_user;
GRANT SELECT ON TABLE public.dw_research_resource_org TO orcidro;


--
-- Name: TABLE researcher_url; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.researcher_url TO orcidro;


--
-- Name: TABLE dw_researcher_url; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_researcher_url TO dw_user;
GRANT SELECT ON TABLE public.dw_researcher_url TO orcidro;


--
-- Name: TABLE userconnection; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.userconnection TO orcidro;


--
-- Name: TABLE dw_userconnection; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_userconnection TO dw_user;
GRANT SELECT ON TABLE public.dw_userconnection TO orcidro;


--
-- Name: TABLE validated_public_profile; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.validated_public_profile TO orcidro;


--
-- Name: TABLE dw_validated_public_profile; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_validated_public_profile TO dw_user;
GRANT SELECT ON TABLE public.dw_validated_public_profile TO orcidro;


--
-- Name: TABLE work; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.work TO orcidro;
GRANT SELECT ON TABLE public.work TO dw_user;


--
-- Name: TABLE dw_work; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_work TO dw_user;
GRANT SELECT ON TABLE public.dw_work TO orcidro;


--
-- Name: TABLE dw_work_external_id; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.dw_work_external_id TO dw_user;
GRANT SELECT ON TABLE public.dw_work_external_id TO orcidro;


--
-- Name: TABLE email_domain; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.email_domain TO orcidro;


--
-- Name: TABLE email_event; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.email_event TO orcidro;


--
-- Name: TABLE email_frequency; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.email_frequency TO orcidro;


--
-- Name: TABLE email_schedule; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.email_schedule TO orcidro;


--
-- Name: TABLE event; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.event TO orcidro;


--
-- Name: TABLE find_my_stuff_history; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.find_my_stuff_history TO orcidro;


--
-- Name: TABLE funding_external_identifier; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.funding_external_identifier TO orcidro;


--
-- Name: TABLE funding_subtype_to_index; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.funding_subtype_to_index TO orcidro;


--
-- Name: TABLE granted_authority; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.granted_authority TO orcidro;


--
-- Name: TABLE identity_provider_name; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.identity_provider_name TO orcidro;


--
-- Name: TABLE institution; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.institution TO orcidro;


--
-- Name: TABLE internal_sso; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.internal_sso TO orcidro;


--
-- Name: TABLE invalid_issn_group_id_record; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.invalid_issn_group_id_record TO orcidro;


--
-- Name: TABLE invalid_record_data_changes; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.invalid_record_data_changes TO orcidro;


--
-- Name: TABLE member_chosen_org_disambiguated; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.member_chosen_org_disambiguated TO orcidro;


--
-- Name: TABLE member_obo_whitelisted_client; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.member_obo_whitelisted_client TO orcidro;


--
-- Name: TABLE notification_item; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.notification_item TO orcidro;


--
-- Name: TABLE notification_work; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.notification_work TO orcidro;


--
-- Name: TABLE oauth2_authoriziation_code_detail; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.oauth2_authoriziation_code_detail TO orcidro;


--
-- Name: TABLE orcid_props; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.orcid_props TO orcidro;


--
-- Name: TABLE orcid_social; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.orcid_social TO orcidro;


--
-- Name: TABLE orcidoauth2authoriziationcodedetail_authorities; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.orcidoauth2authoriziationcodedetail_authorities TO orcidro;


--
-- Name: TABLE orcidoauth2authoriziationcodedetail_resourceids; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.orcidoauth2authoriziationcodedetail_resourceids TO orcidro;


--
-- Name: TABLE orcidoauth2authoriziationcodedetail_scopes; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.orcidoauth2authoriziationcodedetail_scopes TO orcidro;


--
-- Name: TABLE org_import_log; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.org_import_log TO orcidro;


--
-- Name: TABLE patent; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.patent TO orcidro;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.patent TO dw_user;


--
-- Name: TABLE patent_contributor; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.patent_contributor TO orcidro;


--
-- Name: TABLE patent_source; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.patent_source TO orcidro;


--
-- Name: TABLE peer_review_subject; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.peer_review_subject TO orcidro;


--
-- Name: TABLE profile_event; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.profile_event TO orcidro;


--
-- Name: TABLE profile_patent; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.profile_patent TO orcidro;


--
-- Name: TABLE profile_subject; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.profile_subject TO orcidro;


--
-- Name: TABLE reference_data; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.reference_data TO orcidro;


--
-- Name: TABLE rejected_grouping_suggestion; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.rejected_grouping_suggestion TO orcidro;


--
-- Name: TABLE salesforce_connection; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.salesforce_connection TO orcidro;


--
-- Name: TABLE shibboleth_account; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.shibboleth_account TO orcidro;


--
-- Name: TABLE spam; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.spam TO orcidro;


--
-- Name: TABLE statistic_key; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.statistic_key TO orcidro;


--
-- Name: TABLE statistic_values; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.statistic_values TO orcidro;


--
-- Name: TABLE subject; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.subject TO orcidro;


--
-- Name: TABLE webhook; Type: ACL; Schema: public; Owner: orcid
--

GRANT SELECT ON TABLE public.webhook TO orcidro;


--
-- PostgreSQL database dump complete
--

