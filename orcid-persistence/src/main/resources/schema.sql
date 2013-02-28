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

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: accession_num; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE accession_num (
    id character varying(350) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    type character varying(10),
    work_id bigint
);


ALTER TABLE public.accession_num OWNER TO orcid;

--
-- Name: address; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE address (
    id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    address_line_1 character varying(350),
    address_line_2 character varying(350),
    city character varying(150),
    country character varying(100),
    postal_code character varying(15),
    state_or_province character varying(150)
);


ALTER TABLE public.address OWNER TO orcid;

--
-- Name: address_id_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE address_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.address_id_seq OWNER TO orcid;

--
-- Name: address_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: orcid
--

ALTER SEQUENCE address_id_seq OWNED BY address.id;


--
-- Name: affiliate_profile_institution; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE affiliate_profile_institution (
    institution_id bigint NOT NULL,
    orcid character varying(255) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    end_date timestamp without time zone,
    role character varying(255),
    start_date timestamp without time zone
);


ALTER TABLE public.affiliate_profile_institution OWNER TO orcid;

--
-- Name: author; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE author (
    id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    family_name character varying(150),
    given_names character varying(150),
    vocative_name character varying(450),
    orcid character varying(19)
);


ALTER TABLE public.author OWNER TO orcid;

--
-- Name: author_id_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE author_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.author_id_seq OWNER TO orcid;

--
-- Name: author_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: orcid
--

ALTER SEQUENCE author_id_seq OWNED BY author.id;


--
-- Name: author_other_name; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE author_other_name (
    author_other_name_id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    display_name character varying(255),
    author_id bigint NOT NULL
);


ALTER TABLE public.author_other_name OWNER TO orcid;

--
-- Name: author_other_name_author_other_name_id_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE author_other_name_author_other_name_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.author_other_name_author_other_name_id_seq OWNER TO orcid;

--
-- Name: author_other_name_author_other_name_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: orcid
--

ALTER SEQUENCE author_other_name_author_other_name_id_seq OWNED BY author_other_name.author_other_name_id;


--
-- Name: author_work; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE author_work (
    author_id bigint NOT NULL,
    work_id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone
);


ALTER TABLE public.author_work OWNER TO orcid;

--
-- Name: electronic_resource_num; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE electronic_resource_num (
    id character varying(30) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    electronic_resource_num character varying(10),
    electronic_resource_num_id bigint
);


ALTER TABLE public.electronic_resource_num OWNER TO orcid;

--
-- Name: external_identifier; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE external_identifier (
    external_identifier character varying(400) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    external_identifier_name character varying(250),
    orcid character varying(19) NOT NULL
);


ALTER TABLE public.external_identifier OWNER TO orcid;

--
-- Name: granted_authority; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE granted_authority (
    authority character varying(255) NOT NULL,
    user_id character varying(19)
);


ALTER TABLE public.granted_authority OWNER TO orcid;

--
-- Name: hear_about; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE hear_about (
    id integer NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    name character varying(255)
);


ALTER TABLE public.hear_about OWNER TO orcid;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO orcid;

--
-- Name: institution; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE institution (
    id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    institution_name character varying(350),
    address_id bigint,
    institution_department_id bigint
);


ALTER TABLE public.institution OWNER TO orcid;

--
-- Name: institution_department; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE institution_department (
    id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    name character varying(250)
);


ALTER TABLE public.institution_department OWNER TO orcid;

--
-- Name: institution_department_id_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE institution_department_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.institution_department_id_seq OWNER TO orcid;

--
-- Name: institution_department_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: orcid
--

ALTER SEQUENCE institution_department_id_seq OWNED BY institution_department.id;


--
-- Name: institution_id_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE institution_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.institution_id_seq OWNER TO orcid;

--
-- Name: institution_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: orcid
--

ALTER SEQUENCE institution_id_seq OWNED BY institution.id;


--
-- Name: institution_other_name; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE institution_other_name (
    institution_other_name_id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    display_name character varying(255),
    institution_id bigint NOT NULL
);


ALTER TABLE public.institution_other_name OWNER TO orcid;

--
-- Name: institution_other_name_institution_other_name_id_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE institution_other_name_institution_other_name_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.institution_other_name_institution_other_name_id_seq OWNER TO orcid;

--
-- Name: institution_other_name_institution_other_name_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: orcid
--

ALTER SEQUENCE institution_other_name_institution_other_name_id_seq OWNED BY institution_other_name.institution_other_name_id;


--
-- Name: other_name; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE other_name (
    other_name_id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    display_name character varying(255),
    orcid character varying(19) NOT NULL
);


ALTER TABLE public.other_name OWNER TO orcid;

--
-- Name: other_name_other_name_id_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE other_name_other_name_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.other_name_other_name_id_seq OWNER TO orcid;

--
-- Name: other_name_other_name_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: orcid
--

ALTER SEQUENCE other_name_other_name_id_seq OWNED BY other_name.other_name_id;


--
-- Name: past_profile_institution; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE past_profile_institution (
    institution_id bigint NOT NULL,
    orcid character varying(255) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    end_date timestamp without time zone,
    role character varying(255),
    start_date timestamp without time zone
);


ALTER TABLE public.past_profile_institution OWNER TO orcid;

--
-- Name: primary_profile_institution; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE primary_profile_institution (
    institution_id bigint NOT NULL,
    orcid character varying(255) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    role character varying(255),
    start_date timestamp without time zone
);


ALTER TABLE public.primary_profile_institution OWNER TO orcid;

--
-- Name: profile; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE profile (
    orcid character varying(19) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    account_expiry timestamp without time zone,
    non_locked boolean,
    address_line_1 character varying(350),
    address_line_2 character varying(350),
    city character varying(150),
    completed_date timestamp without time zone,
    confirmed boolean,
    country character varying(100),
    creation_method character varying(10),
    credentials_expiry timestamp without time zone,
    credit_name character varying(150),
    email character varying(150),
    enabled boolean,
    family_name character varying(150),
    given_name character varying(150),
    is_selectable_sponsor boolean,
    password character varying(255),
    postal_code character varying(15),
    state_or_province character varying(150),
    submission_date timestamp without time zone,
    tel_num character varying(19),
    vocative_name character varying(450),
    sponsor_id character varying(19)
);


ALTER TABLE public.profile OWNER TO orcid;

--
-- Name: profile_work; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE profile_work (
    orcid character varying(255) NOT NULL,
    work_id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone
);


ALTER TABLE public.profile_work OWNER TO orcid;

--
-- Name: registration; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE registration (
    id integer NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    email character varying(255),
    email_send_status character varying(255),
    ip_address character varying(255),
    hearabout_id integer
);


ALTER TABLE public.registration OWNER TO orcid;

--
-- Name: registration_role; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE registration_role (
    id integer NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    role character varying(255)
);


ALTER TABLE public.registration_role OWNER TO orcid;

--
-- Name: related_url; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE related_url (
    id character varying(350) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    work_id bigint
);


ALTER TABLE public.related_url OWNER TO orcid;

--
-- Name: researcher_url; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE researcher_url (
    id character varying(350) NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    orcid character varying(19) NOT NULL
);


ALTER TABLE public.researcher_url OWNER TO orcid;

--
-- Name: secondary_author_work; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE secondary_author_work (
    secondary_author_id bigint NOT NULL,
    work_id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone
);


ALTER TABLE public.secondary_author_work OWNER TO orcid;

--
-- Name: security_question; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE security_question (
    id integer NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    question character varying(255)
);


ALTER TABLE public.security_question OWNER TO orcid;

--
-- Name: work; Type: TABLE; Schema: public; Owner: orcid; Tablespace: 
--

CREATE TABLE work (
    id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    cover_date character varying(30),
    isbn character varying(13),
    issn character varying(8),
    number character varying(10),
    page_end integer,
    page_start integer,
    publication_date timestamp without time zone,
    publisher character varying(600),
    ref_type character varying(255),
    secondary_title character varying(1000),
    title character varying(1000),
    volume character varying(10)
);


ALTER TABLE public.work OWNER TO orcid;

--
-- Name: work_id_seq; Type: SEQUENCE; Schema: public; Owner: orcid
--

CREATE SEQUENCE work_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.work_id_seq OWNER TO orcid;

--
-- Name: work_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: orcid
--

ALTER SEQUENCE work_id_seq OWNED BY work.id;

CREATE TABLE reference_data (
    id bigint NOT NULL,
    date_created timestamp without time zone,
    last_modified timestamp without time zone,
    ref_data_key character varying(255),
    ref_data_value character varying(255)
);

CREATE SEQUENCE ref_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.reference_data OWNER TO orcid;
ALTER TABLE public.ref_data_id_seq OWNER TO orcid;
ALTER SEQUENCE ref_data_id_seq OWNED BY reference_data.id;

    
--
-- Name: id; Type: DEFAULT; Schema: public; Owner: orcid
--

ALTER TABLE address ALTER COLUMN id SET DEFAULT nextval('address_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: orcid
--

ALTER TABLE author ALTER COLUMN id SET DEFAULT nextval('author_id_seq'::regclass);


--
-- Name: author_other_name_id; Type: DEFAULT; Schema: public; Owner: orcid
--

ALTER TABLE author_other_name ALTER COLUMN author_other_name_id SET DEFAULT nextval('author_other_name_author_other_name_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: orcid
--

ALTER TABLE institution ALTER COLUMN id SET DEFAULT nextval('institution_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: orcid
--

ALTER TABLE institution_department ALTER COLUMN id SET DEFAULT nextval('institution_department_id_seq'::regclass);


--
-- Name: institution_other_name_id; Type: DEFAULT; Schema: public; Owner: orcid
--

ALTER TABLE institution_other_name ALTER COLUMN institution_other_name_id SET DEFAULT nextval('institution_other_name_institution_other_name_id_seq'::regclass);


--
-- Name: other_name_id; Type: DEFAULT; Schema: public; Owner: orcid
--

ALTER TABLE other_name ALTER COLUMN other_name_id SET DEFAULT nextval('other_name_other_name_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: orcid
--

ALTER TABLE work ALTER COLUMN id SET DEFAULT nextval('work_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: orcid
--
ALTER TABLE reference_data ALTER COLUMN id SET DEFAULT nextval('ref_data_id_seq'::regclass);

--
-- Name: accession_num_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY accession_num
    ADD CONSTRAINT accession_num_pkey PRIMARY KEY (id);


--
-- Name: address_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- Name: affiliate_profile_institution_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY affiliate_profile_institution
    ADD CONSTRAINT affiliate_profile_institution_pkey PRIMARY KEY (institution_id, orcid);


--
-- Name: author_other_name_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY author_other_name
    ADD CONSTRAINT author_other_name_pkey PRIMARY KEY (author_other_name_id);


--
-- Name: author_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY author
    ADD CONSTRAINT author_pkey PRIMARY KEY (id);


--
-- Name: author_work_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY author_work
    ADD CONSTRAINT author_work_pkey PRIMARY KEY (author_id, work_id);


--
-- Name: electronic_resource_num_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY electronic_resource_num
    ADD CONSTRAINT electronic_resource_num_pkey PRIMARY KEY (id);


--
-- Name: external_identifier_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY external_identifier
    ADD CONSTRAINT external_identifier_pkey PRIMARY KEY (external_identifier);


--
-- Name: granted_authority_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY granted_authority
    ADD CONSTRAINT granted_authority_pkey PRIMARY KEY (authority);


--
-- Name: hear_about_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY hear_about
    ADD CONSTRAINT hear_about_pkey PRIMARY KEY (id);


--
-- Name: institution_department_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY institution_department
    ADD CONSTRAINT institution_department_pkey PRIMARY KEY (id);


--
-- Name: institution_other_name_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY institution_other_name
    ADD CONSTRAINT institution_other_name_pkey PRIMARY KEY (institution_other_name_id);


--
-- Name: institution_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY institution
    ADD CONSTRAINT institution_pkey PRIMARY KEY (id);


--
-- Name: other_name_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY other_name
    ADD CONSTRAINT other_name_pkey PRIMARY KEY (other_name_id);


--
-- Name: past_profile_institution_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY past_profile_institution
    ADD CONSTRAINT past_profile_institution_pkey PRIMARY KEY (institution_id, orcid);


--
-- Name: primary_profile_institution_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY primary_profile_institution
    ADD CONSTRAINT primary_profile_institution_pkey PRIMARY KEY (institution_id, orcid);


--
-- Name: profile_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY profile
    ADD CONSTRAINT profile_pkey PRIMARY KEY (orcid);


--
-- Name: profile_work_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY profile_work
    ADD CONSTRAINT profile_work_pkey PRIMARY KEY (orcid, work_id);


--
-- Name: registration_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY registration
    ADD CONSTRAINT registration_pkey PRIMARY KEY (id);


--
-- Name: registration_role_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY registration_role
    ADD CONSTRAINT registration_role_pkey PRIMARY KEY (id);


--
-- Name: related_url_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY related_url
    ADD CONSTRAINT related_url_pkey PRIMARY KEY (id);


--
-- Name: researcher_url_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY researcher_url
    ADD CONSTRAINT researcher_url_pkey PRIMARY KEY (id);


--
-- Name: secondary_author_work_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY secondary_author_work
    ADD CONSTRAINT secondary_author_work_pkey PRIMARY KEY (secondary_author_id, work_id);


--
-- Name: security_question_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY security_question
    ADD CONSTRAINT security_question_pkey PRIMARY KEY (id);


--
-- Name: work_pkey; Type: CONSTRAINT; Schema: public; Owner: orcid; Tablespace: 
--

ALTER TABLE ONLY work
    ADD CONSTRAINT work_pkey PRIMARY KEY (id);


ALTER TABLE ONLY reference_data
    ADD CONSTRAINT reference_data_pkey PRIMARY KEY (id);
    
--
-- Name: fk21888c75b2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY past_profile_institution
    ADD CONSTRAINT fk21888c75b2007f99 FOREIGN KEY (orcid) REFERENCES profile(orcid);


--
-- Name: fk21888c75cf1a386f; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY past_profile_institution
    ADD CONSTRAINT fk21888c75cf1a386f FOREIGN KEY (institution_id) REFERENCES institution(id);


--
-- Name: fk2a839d4572299aab; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY author_work
    ADD CONSTRAINT fk2a839d4572299aab FOREIGN KEY (author_id) REFERENCES author(id);


--
-- Name: fk2a839d45f14b93eb; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY author_work
    ADD CONSTRAINT fk2a839d45f14b93eb FOREIGN KEY (work_id) REFERENCES work(id);


--
-- Name: fk2f77fd8e72299aab; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY author_other_name
    ADD CONSTRAINT fk2f77fd8e72299aab FOREIGN KEY (author_id) REFERENCES author(id);


--
-- Name: fk3529a5b89a1617c0; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY institution
    ADD CONSTRAINT fk3529a5b89a1617c0 FOREIGN KEY (institution_department_id) REFERENCES institution_department(id);


--
-- Name: fk3529a5b8e84caef; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY institution
    ADD CONSTRAINT fk3529a5b8e84caef FOREIGN KEY (address_id) REFERENCES address(id);


--
-- Name: fk408de65b2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY primary_profile_institution
    ADD CONSTRAINT fk408de65b2007f99 FOREIGN KEY (orcid) REFERENCES profile(orcid);


--
-- Name: fk408de65cf1a386f; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY primary_profile_institution
    ADD CONSTRAINT fk408de65cf1a386f FOREIGN KEY (institution_id) REFERENCES institution(id);


--
-- Name: fk425c835bf14b93eb; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY related_url
    ADD CONSTRAINT fk425c835bf14b93eb FOREIGN KEY (work_id) REFERENCES work(id);


--
-- Name: fk45bd0abfa2e9e9ad; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY granted_authority
    ADD CONSTRAINT fk45bd0abfa2e9e9ad FOREIGN KEY (user_id) REFERENCES profile(orcid);


--
-- Name: fk48f497e7b2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY profile_work
    ADD CONSTRAINT fk48f497e7b2007f99 FOREIGN KEY (orcid) REFERENCES profile(orcid);


--
-- Name: fk48f497e7f14b93eb; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY profile_work
    ADD CONSTRAINT fk48f497e7f14b93eb FOREIGN KEY (work_id) REFERENCES work(id);


--
-- Name: fk641fe19db2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY external_identifier
    ADD CONSTRAINT fk641fe19db2007f99 FOREIGN KEY (orcid) REFERENCES profile(orcid);


--
-- Name: fk647b0b41cf1a386f; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY institution_other_name
    ADD CONSTRAINT fk647b0b41cf1a386f FOREIGN KEY (institution_id) REFERENCES institution(id);


--
-- Name: fk9bb60ebf14b93eb; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY accession_num
    ADD CONSTRAINT fk9bb60ebf14b93eb FOREIGN KEY (work_id) REFERENCES work(id);


--
-- Name: fkac2d218bb2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY author
    ADD CONSTRAINT fkac2d218bb2007f99 FOREIGN KEY (orcid) REFERENCES profile(orcid);


--
-- Name: fkaf83e8b9bee4362f; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY registration
    ADD CONSTRAINT fkaf83e8b9bee4362f FOREIGN KEY (hearabout_id) REFERENCES hear_about(id);


--
-- Name: fkd1e968b83f763ce4; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY electronic_resource_num
    ADD CONSTRAINT fkd1e968b83f763ce4 FOREIGN KEY (electronic_resource_num_id) REFERENCES work(id);


--
-- Name: fkd433c438b2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY researcher_url
    ADD CONSTRAINT fkd433c438b2007f99 FOREIGN KEY (orcid) REFERENCES profile(orcid);


--
-- Name: fked8e89a9d6bc0bfe; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY profile
    ADD CONSTRAINT fked8e89a9d6bc0bfe FOREIGN KEY (sponsor_id) REFERENCES profile(orcid);


--
-- Name: fkf5209e5ab2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY other_name
    ADD CONSTRAINT fkf5209e5ab2007f99 FOREIGN KEY (orcid) REFERENCES profile(orcid);


--
-- Name: fkf7f1defae36c5a0; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY secondary_author_work
    ADD CONSTRAINT fkf7f1defae36c5a0 FOREIGN KEY (secondary_author_id) REFERENCES author(id);


--
-- Name: fkf7f1defaf14b93eb; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY secondary_author_work
    ADD CONSTRAINT fkf7f1defaf14b93eb FOREIGN KEY (work_id) REFERENCES work(id);


--
-- Name: fkfe6bc230b2007f99; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY affiliate_profile_institution
    ADD CONSTRAINT fkfe6bc230b2007f99 FOREIGN KEY (orcid) REFERENCES profile(orcid);


--
-- Name: fkfe6bc230cf1a386f; Type: FK CONSTRAINT; Schema: public; Owner: orcid
--

ALTER TABLE ONLY affiliate_profile_institution
    ADD CONSTRAINT fkfe6bc230cf1a386f FOREIGN KEY (institution_id) REFERENCES institution(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--
