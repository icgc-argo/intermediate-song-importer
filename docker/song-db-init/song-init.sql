--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.15
-- Dumped by pg_dump version 9.6.15

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
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


--
-- Name: access_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.access_type AS ENUM (
    'controlled',
    'open'
);


ALTER TYPE public.access_type OWNER TO postgres;

--
-- Name: analysis_state; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.analysis_state AS ENUM (
    'PUBLISHED',
    'UNPUBLISHED',
    'SUPPRESSED'
);


ALTER TYPE public.analysis_state OWNER TO postgres;

--
-- Name: analysis_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.analysis_type AS ENUM (
    'sequencingRead',
    'variantCall',
    'MAF'
);


ALTER TYPE public.analysis_type OWNER TO postgres;

--
-- Name: file_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.file_type AS ENUM (
    'FASTA',
    'FAI',
    'FASTQ',
    'BAM',
    'BAI',
    'VCF',
    'TBI',
    'IDX',
    'XML',
    'TGZ',
    'CRAM',
    'CRAI'
);


ALTER TYPE public.file_type OWNER TO postgres;

--
-- Name: gender; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.gender AS ENUM (
    'Male',
    'Female',
    'Other'
);


ALTER TYPE public.gender OWNER TO postgres;

--
-- Name: id_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.id_type AS ENUM (
    'Study',
    'Donor',
    'Specimen',
    'Sample',
    'File',
    'Analysis',
    'SequencingRead',
    'VariantCall'
);


ALTER TYPE public.id_type OWNER TO postgres;

--
-- Name: legacy_sample_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.legacy_sample_type AS ENUM (
    'DNA',
    'FFPE DNA',
    'Amplified DNA',
    'RNA',
    'Total RNA',
    'FFPE RNA'
);


ALTER TYPE public.legacy_sample_type OWNER TO postgres;

--
-- Name: legacy_specimen_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.legacy_specimen_type AS ENUM (
    'Normal - solid tissue',
    'Normal - blood derived',
    'Normal - bone marrow',
    'Normal - tissue adjacent to primary',
    'Normal - buccal cell',
    'Normal - EBV immortalized',
    'Normal - lymph node',
    'Normal - other',
    'Primary tumour',
    'Primary tumour - solid tissue',
    'Primary tumour - blood derived (peripheral blood)',
    'Primary tumour - blood derived (bone marrow)',
    'Primary tumour - additional new primary',
    'Primary tumour - other',
    'Recurrent tumour - solid tissue',
    'Recurrent tumour - blood derived (peripheral blood)',
    'Recurrent tumour - blood derived (bone marrow)',
    'Recurrent tumour - other',
    'Metastatic tumour - NOS',
    'Metastatic tumour - lymph node',
    'Metastatic tumour - metastasis local to lymph node',
    'Metastatic tumour - metastasis to distant location',
    'Metastatic tumour - additional metastatic',
    'Xenograft - derived from primary tumour',
    'Xenograft - derived from tumour cell line',
    'Cell line - derived from tumour',
    'Primary tumour - lymph node',
    'Metastatic tumour - other',
    'Cell line - derived from xenograft tumour'
);


ALTER TYPE public.legacy_specimen_type OWNER TO postgres;

--
-- Name: library_strategy; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.library_strategy AS ENUM (
    'WGS',
    'WXS',
    'RNA-Seq',
    'ChIP-Seq',
    'miRNA-Seq',
    'Bisulfite-Seq',
    'Validation',
    'Amplicon',
    'Other'
);


ALTER TYPE public.library_strategy OWNER TO postgres;

--
-- Name: sample_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.sample_type AS ENUM (
    'Total DNA',
    'Amplified DNA',
    'ctDNA',
    'Other DNA enrichments',
    'Total RNA',
    'Ribo-Zero RNA',
    'polyA+ RNA',
    'Other RNA fractions'
);


ALTER TYPE public.sample_type OWNER TO postgres;

--
-- Name: specimen_class; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.specimen_class AS ENUM (
    'Normal',
    'Tumour',
    'Adjacent normal'
);


ALTER TYPE public.specimen_class OWNER TO postgres;

--
-- Name: specimen_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.specimen_type AS ENUM (
    'Normal',
    'Normal - tissue adjacent to primary tumour',
    'Primary tumour',
    'Primary tumour - adjacent to normal',
    'Primary tumour - additional new primary',
    'Recurrent tumour',
    'Metastatic tumour',
    'Metastatic tumour - metastasis local to lymph node',
    'Metastatic tumour - metastasis to distant location',
    'Metastatic tumour - additional metastatic',
    'Xenograft - derived from primary tumour',
    'Xenograft - derived from tumour cell line',
    'Cell line - derived from xenograft tumour',
    'Cell line - derived from tumour',
    'Cell line - derived from normal'
);


ALTER TYPE public.specimen_type OWNER TO postgres;

--
-- Name: tissue_source_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.tissue_source_type AS ENUM (
    'Blood derived',
    'Blood derived - bone marrow',
    'Blood derived - peripheral blood',
    'Bone marrow',
    'Buccal cell',
    'Lymph node',
    'Solid tissue',
    'Plasma',
    'Serum',
    'Urine',
    'Cerebrospinal fluid',
    'Sputum',
    'Other',
    'Pleural effusion',
    'Mononuclear cells from bone marrow',
    'Saliva',
    'Skin',
    'Intestine',
    'Buffy coat',
    'Stomach',
    'Esophagus',
    'Tonsil',
    'Spleen',
    'Bone',
    'Cerebellum',
    'Endometrium'
);


ALTER TYPE public.tissue_source_type OWNER TO postgres;

--
-- Name: tumour_normal_designation_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.tumour_normal_designation_type AS ENUM (
    'Normal',
    'Tumour'
);


ALTER TYPE public.tumour_normal_designation_type OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: analysis; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.analysis (
    id character varying(36) NOT NULL,
    study_id character varying(36),
    type public.analysis_type,
    state public.analysis_state,
    analysis_schema_id integer,
    analysis_data_id integer
);


ALTER TABLE public.analysis OWNER TO postgres;

--
-- Name: analysis_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.analysis_data (
    id bigint NOT NULL,
    data jsonb NOT NULL
);


ALTER TABLE public.analysis_data OWNER TO postgres;

--
-- Name: analysis_data_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.analysis_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.analysis_data_id_seq OWNER TO postgres;

--
-- Name: analysis_data_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.analysis_data_id_seq OWNED BY public.analysis_data.id;


--
-- Name: analysis_schema; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.analysis_schema (
    id bigint NOT NULL,
    version integer,
    name character varying(225) NOT NULL,
    schema jsonb NOT NULL
);


ALTER TABLE public.analysis_schema OWNER TO postgres;

--
-- Name: analysis_schema_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.analysis_schema_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.analysis_schema_id_seq OWNER TO postgres;

--
-- Name: analysis_schema_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.analysis_schema_id_seq OWNED BY public.analysis_schema.id;


--
-- Name: donor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.donor (
    id character varying(36) NOT NULL,
    study_id character varying(36),
    submitter_id text,
    gender public.gender NOT NULL
);


ALTER TABLE public.donor OWNER TO postgres;

--
-- Name: sample; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sample (
    id character varying(36) NOT NULL,
    specimen_id character varying(36),
    submitter_id text,
    legacy_type public.legacy_sample_type,
    type public.sample_type,
    matched_normal_submitter_sample_id character varying(255)
);


ALTER TABLE public.sample OWNER TO postgres;

--
-- Name: specimen; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.specimen (
    id character varying(36) NOT NULL,
    donor_id character varying(36),
    submitter_id text,
    class public.specimen_class,
    legacy_type public.legacy_specimen_type,
    type public.specimen_type,
    tissue_source public.tissue_source_type,
    tumour_normal_designation public.tumour_normal_designation_type NOT NULL
);


ALTER TABLE public.specimen OWNER TO postgres;

--
-- Name: study; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.study (
    id character varying(36) NOT NULL,
    name text,
    description text,
    organization text
);


ALTER TABLE public.study OWNER TO postgres;

--
-- Name: businesskeyview; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.businesskeyview AS
 SELECT s.id AS study_id,
    sp.id AS specimen_id,
    sp.submitter_id AS specimen_submitter_id,
    sa.id AS sample_id,
    sa.submitter_id AS sample_submitter_id
   FROM (((public.study s
     JOIN public.donor d ON (((s.id)::text = (d.study_id)::text)))
     JOIN public.specimen sp ON (((d.id)::text = (sp.donor_id)::text)))
     JOIN public.sample sa ON (((sp.id)::text = (sa.specimen_id)::text)));


ALTER TABLE public.businesskeyview OWNER TO postgres;

--
-- Name: file; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.file (
    id character varying(36) NOT NULL,
    analysis_id character varying(36),
    study_id character varying(36),
    name text,
    size bigint,
    md5 character(32),
    access public.access_type,
    type public.file_type,
    data_type character varying(255)
);


ALTER TABLE public.file OWNER TO postgres;

--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO postgres;

--
-- Name: sampleset; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sampleset (
    analysis_id character varying(36),
    sample_id character varying(36)
);


ALTER TABLE public.sampleset OWNER TO postgres;

--
-- Name: idview; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.idview AS
 SELECT DISTINCT a.id AS analysis_id,
    ans.id AS analysis_schema_id,
    ans.name AS analysis_schema_name,
    a.state AS analysis_state,
    a.study_id,
    d.id AS donor_id,
    sp.id AS specimen_id,
    sa.id AS sample_id,
    f.id AS object_id
   FROM ((((((public.donor d
     JOIN public.specimen sp ON (((d.id)::text = (sp.donor_id)::text)))
     JOIN public.sample sa ON (((sp.id)::text = (sa.specimen_id)::text)))
     JOIN public.sampleset sas ON (((sa.id)::text = (sas.sample_id)::text)))
     JOIN public.file f ON (((sas.analysis_id)::text = (f.analysis_id)::text)))
     JOIN public.analysis a ON (((sas.analysis_id)::text = (a.id)::text)))
     JOIN public.analysis_schema ans ON ((a.analysis_schema_id = ans.id)));


ALTER TABLE public.idview OWNER TO postgres;

--
-- Name: info; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.info (
    id character varying(36),
    id_type public.id_type,
    info json
);


ALTER TABLE public.info OWNER TO postgres;

--
-- Name: infoview; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.infoview AS
 SELECT a.id AS analysis_id,
    i_study.info AS study_info,
    i_donor.info AS donor_info,
    i_sp.info AS specimen_info,
    i_sa.info AS sample_info,
    i_a.info AS analysis_info,
    i_f.info AS file_info
   FROM ((((((((((((public.study s
     JOIN public.info i_study ON ((((i_study.id)::text = (s.id)::text) AND (i_study.id_type = 'Study'::public.id_type))))
     JOIN public.donor d ON (((s.id)::text = (d.study_id)::text)))
     JOIN public.info i_donor ON ((((i_donor.id)::text = (d.id)::text) AND (i_donor.id_type = 'Donor'::public.id_type))))
     JOIN public.specimen sp ON (((d.id)::text = (sp.donor_id)::text)))
     JOIN public.info i_sp ON ((((i_sp.id)::text = (sp.id)::text) AND (i_sp.id_type = 'Specimen'::public.id_type))))
     JOIN public.sample sa ON (((sp.id)::text = (sa.specimen_id)::text)))
     JOIN public.info i_sa ON ((((i_sa.id)::text = (sa.id)::text) AND (i_sa.id_type = 'Sample'::public.id_type))))
     JOIN public.sampleset ss ON (((sa.id)::text = (ss.sample_id)::text)))
     JOIN public.analysis a ON (((ss.analysis_id)::text = (a.id)::text)))
     JOIN public.info i_a ON ((((i_a.id)::text = (a.id)::text) AND (i_a.id_type = 'Analysis'::public.id_type))))
     JOIN public.file f ON (((a.id)::text = (f.analysis_id)::text)))
     JOIN public.info i_f ON ((((i_f.id)::text = (f.id)::text) AND (i_f.id_type = 'File'::public.id_type))));


ALTER TABLE public.infoview OWNER TO postgres;

--
-- Name: upload; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.upload (
    id character varying(40) NOT NULL,
    study_id character varying(36),
    analysis_id text,
    state character varying(50),
    errors text,
    payload text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.upload OWNER TO postgres;

--
-- Name: analysis_data id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis_data ALTER COLUMN id SET DEFAULT nextval('public.analysis_data_id_seq'::regclass);


--
-- Name: analysis_schema id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis_schema ALTER COLUMN id SET DEFAULT nextval('public.analysis_schema_id_seq'::regclass);


--
-- Data for Name: analysis; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.analysis (id, study_id, type, state, analysis_schema_id, analysis_data_id) FROM stdin;
fda11e16-2139-4ba6-a11e-162139cba69e	ABC123-CA	\N	PUBLISHED	1	2
60610159-4f9b-4402-a101-594f9b740217	ABC123-CA	\N	PUBLISHED	1	3
fdd69e1e-0f80-45b7-969e-1e0f8095b743	ABC123-CA	\N	PUBLISHED	1	4
de11647c-b797-450d-9164-7cb797050d56	TEST-CA	\N	PUBLISHED	1	5
3f49f7be-9d78-4be4-89f7-be9d780be494	TEST-CA	\N	PUBLISHED	1	6
5b64d69d-59b4-4fe0-a4d6-9d59b4bfe027	TEST-CA	\N	PUBLISHED	1	7
\.


--
-- Data for Name: analysis_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.analysis_data (id, data) FROM stdin;
2	{"info": {"description": "This is extra info in a JSON format"}, "experiment": {"variantCallingTool": "silver bullet", "matchedNormalSampleSubmitterId": "sample-x24-11a"}}
3	{"info": {"description": "This is extra info in a JSON format"}, "experiment": {"variantCallingTool": "silver bullet", "matchedNormalSampleSubmitterId": "sample-x24-11a"}}
4	{"info": {"description": "This is extra info in a JSON format"}, "experiment": {"variantCallingTool": "silver bullet", "matchedNormalSampleSubmitterId": "sample-x24-11a"}}
5	{"info": {"description": "This is extra info in a JSON format"}, "experiment": {"variantCallingTool": "silver bullet", "matchedNormalSampleSubmitterId": "sample-x24-11a"}}
6	{"info": {"description": "This is extra info in a JSON format"}, "experiment": {"variantCallingTool": "silver bullet", "matchedNormalSampleSubmitterId": "sample-x24-11a"}}
7	{"info": {"description": "This is extra info in a JSON format"}, "experiment": {"variantCallingTool": "silver bullet", "matchedNormalSampleSubmitterId": "sample-x24-11a"}}
\.


--
-- Name: analysis_data_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.analysis_data_id_seq', 7, true);


--
-- Data for Name: analysis_schema; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.analysis_schema (id, version, name, schema) FROM stdin;
1	1	variantCall	{"type": "object", "required": ["experiment"], "properties": {"experiment": {"type": "object", "required": ["matchedNormalSampleSubmitterId", "variantCallingTool"], "properties": {"variantCallingTool": {"type": "string"}, "matchedNormalSampleSubmitterId": {"type": "string"}}}}}
2	1	sequencingRead	{"type": "object", "required": ["experiment"], "properties": {"experiment": {"type": "object", "required": ["libraryStrategy"], "properties": {"aligned": {"type": ["boolean", "null"]}, "pairedEnd": {"type": ["boolean", "null"]}, "insertSize": {"type": ["integer", "null"]}, "alignmentTool": {"type": ["string", "null"]}, "libraryStrategy": {"enum": ["WGS", "WXS", "RNA-Seq", "ChIP-Seq", "miRNA-Seq", "Bisulfite-Seq", "Validation", "Amplicon", "Other"], "type": "string"}, "referenceGenome": {"type": ["string", "null"]}}}}}
\.


--
-- Name: analysis_schema_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.analysis_schema_id_seq', 2, true);


--
-- Data for Name: donor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.donor (id, study_id, submitter_id, gender) FROM stdin;
f0a69282-9241-564e-912d-cf3b3ec48599	ABC123-CA	internal_donor_123456789-00	Female
9a83523b-d59a-5856-ad08-f7db8d43d7df	TEST-CA	internal_donor_123456789-00	Female
\.


--
-- Data for Name: file; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.file (id, analysis_id, study_id, name, size, md5, access, type, data_type) FROM stdin;
de54fff3-70a8-50c3-aa51-99607bc0d871	fda11e16-2139-4ba6-a11e-162139cba69e	ABC123-CA	example4.vcf.gz	52	9a793e90d0d1e11301ea8da996446e59	open	VCF	SOME_DATA_TYPE
08a26f1c-3ed6-5361-aeff-e210f4c62e51	fda11e16-2139-4ba6-a11e-162139cba69e	ABC123-CA	example4.vcf.gz.idx	25	c03274816eb4907a92b8e5632cd6eb81	open	IDX	SOME_DATA_TYPE
569da244-e2f4-5afb-8b8d-602a5cf0a5c1	60610159-4f9b-4402-a101-594f9b740217	ABC123-CA	example5.vcf.gz	52	9a793e90d0d1e11301ea8da996446e59	open	VCF	SOME_DATA_TYPE
ab99bcea-6360-5664-9967-122b0d8671c8	60610159-4f9b-4402-a101-594f9b740217	ABC123-CA	example5.vcf.gz.idx	25	c03274816eb4907a92b8e5632cd6eb81	open	IDX	SOME_DATA_TYPE
555c425f-b256-5328-9b66-8338c62f5170	fdd69e1e-0f80-45b7-969e-1e0f8095b743	ABC123-CA	example6.vcf.gz	52	9a793e90d0d1e11301ea8da996446e59	open	VCF	SOME_DATA_TYPE
bcf73b26-293a-54e5-8934-699e5697c191	fdd69e1e-0f80-45b7-969e-1e0f8095b743	ABC123-CA	example6.vcf.gz.idx	25	c03274816eb4907a92b8e5632cd6eb81	open	IDX	SOME_DATA_TYPE
8e6b8b22-d70e-5618-b572-f02928638739	de11647c-b797-450d-9164-7cb797050d56	TEST-CA	example1.vcf.gz	52	9a793e90d0d1e11301ea8da996446e59	open	VCF	SOME_DATA_TYPE
f4d13c0a-4297-57d1-91dd-f78b37ebb958	de11647c-b797-450d-9164-7cb797050d56	TEST-CA	example1.vcf.gz.idx	25	c03274816eb4907a92b8e5632cd6eb81	open	IDX	SOME_DATA_TYPE
2fa72e42-2688-5073-8aaf-047f14f8e6c3	3f49f7be-9d78-4be4-89f7-be9d780be494	TEST-CA	example2.vcf.gz	52	9a793e90d0d1e11301ea8da996446e59	open	VCF	SOME_DATA_TYPE
49c7bea3-8c17-5b43-b584-af7b07a26e81	3f49f7be-9d78-4be4-89f7-be9d780be494	TEST-CA	example2.vcf.gz.idx	25	c03274816eb4907a92b8e5632cd6eb81	open	IDX	SOME_DATA_TYPE
d4781fee-8300-52bd-ad36-3ee9dd453eef	5b64d69d-59b4-4fe0-a4d6-9d59b4bfe027	TEST-CA	example3.vcf.gz	52	9a793e90d0d1e11301ea8da996446e59	open	VCF	SOME_DATA_TYPE
a6417e81-2d64-5b36-ba1f-fbee89c4a489	5b64d69d-59b4-4fe0-a4d6-9d59b4bfe027	TEST-CA	example3.vcf.gz.idx	25	c03274816eb4907a92b8e5632cd6eb81	open	IDX	SOME_DATA_TYPE
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	Base version	SQL	V1__Base_version.sql	-1608472095	postgres	2019-10-22 19:30:10.105505	493	t
2	1.1	added schema	SQL	V1_1__added_schema.sql	675033696	postgres	2019-10-22 19:30:10.625976	30	t
3	1.2	dynamic schema integration	SPRING_JDBC	db.migration.V1_2__dynamic_schema_integration	\N	postgres	2019-10-22 19:30:10.679764	141	t
4	1.3	post schema integration	SQL	V1_3__post_schema_integration.sql	1429883245	postgres	2019-10-22 19:30:10.885393	13	t
5	1.4	file enum update	SQL	V1_4__file_enum_update.sql	1895896985	postgres	2020-01-15 14:22:23.079332	23	t
6	1.5	donor enum update	SQL	V1_5__donor_enum_update.sql	-452882819	postgres	2020-01-15 14:22:23.133487	17	t
7	1.6	base schema changes	SQL	V1_6__base_schema_changes.sql	1007233558	postgres	2020-01-15 14:22:23.171658	23	t
\.


--
-- Data for Name: info; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.info (id, id_type, info) FROM stdin;
ABC123-CA	Study	{}
f0a69282-9241-564e-912d-cf3b3ec48599	Donor	{}
3c049863-5bae-56d4-b313-dcae28a4ae90	Specimen	{}
1f7eb743-f0c6-55ef-afc3-c3ca1707c301	Sample	{}
de54fff3-70a8-50c3-aa51-99607bc0d871	File	{}
08a26f1c-3ed6-5361-aeff-e210f4c62e51	File	{}
569da244-e2f4-5afb-8b8d-602a5cf0a5c1	File	{}
ab99bcea-6360-5664-9967-122b0d8671c8	File	{}
555c425f-b256-5328-9b66-8338c62f5170	File	{}
bcf73b26-293a-54e5-8934-699e5697c191	File	{}
TEST-CA	Study	{}
9a83523b-d59a-5856-ad08-f7db8d43d7df	Donor	{}
71a9fe8f-f807-5579-aafa-d783eec91cda	Specimen	{}
c8a5303f-f58f-5c9f-ad2b-6b0b275ab96d	Sample	{}
8e6b8b22-d70e-5618-b572-f02928638739	File	{}
f4d13c0a-4297-57d1-91dd-f78b37ebb958	File	{}
2fa72e42-2688-5073-8aaf-047f14f8e6c3	File	{}
49c7bea3-8c17-5b43-b584-af7b07a26e81	File	{}
d4781fee-8300-52bd-ad36-3ee9dd453eef	File	{}
a6417e81-2d64-5b36-ba1f-fbee89c4a489	File	{}
\.


--
-- Data for Name: sample; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sample (id, specimen_id, submitter_id, legacy_type, type, matched_normal_submitter_sample_id) FROM stdin;
1f7eb743-f0c6-55ef-afc3-c3ca1707c301	3c049863-5bae-56d4-b313-dcae28a4ae90	internal_sample_98024759826836	\N	Total RNA	sample-x24-11a
c8a5303f-f58f-5c9f-ad2b-6b0b275ab96d	71a9fe8f-f807-5579-aafa-d783eec91cda	internal_sample_98024759826836	\N	Total RNA	sample-x24-11a
\.


--
-- Data for Name: sampleset; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sampleset (analysis_id, sample_id) FROM stdin;
fda11e16-2139-4ba6-a11e-162139cba69e	1f7eb743-f0c6-55ef-afc3-c3ca1707c301
60610159-4f9b-4402-a101-594f9b740217	1f7eb743-f0c6-55ef-afc3-c3ca1707c301
fdd69e1e-0f80-45b7-969e-1e0f8095b743	1f7eb743-f0c6-55ef-afc3-c3ca1707c301
de11647c-b797-450d-9164-7cb797050d56	c8a5303f-f58f-5c9f-ad2b-6b0b275ab96d
3f49f7be-9d78-4be4-89f7-be9d780be494	c8a5303f-f58f-5c9f-ad2b-6b0b275ab96d
5b64d69d-59b4-4fe0-a4d6-9d59b4bfe027	c8a5303f-f58f-5c9f-ad2b-6b0b275ab96d
\.


--
-- Data for Name: specimen; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.specimen (id, donor_id, submitter_id, class, legacy_type, type, tissue_source, tumour_normal_designation) FROM stdin;
3c049863-5bae-56d4-b313-dcae28a4ae90	f0a69282-9241-564e-912d-cf3b3ec48599	internal_specimen_9b73gk8s02dk	\N	\N	Primary tumour	Solid tissue	Tumour
71a9fe8f-f807-5579-aafa-d783eec91cda	9a83523b-d59a-5856-ad08-f7db8d43d7df	internal_specimen_9b73gk8s02dk	\N	\N	Primary tumour	Solid tissue	Tumour
\.


--
-- Data for Name: study; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.study (id, name, description, organization) FROM stdin;
ABC123	\N	\N	\N
ABC123-CA	\N	\N	\N
TEST-CA	\N	\N	\N
\.


--
-- Data for Name: upload; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.upload (id, study_id, analysis_id, state, errors, payload, created_at, updated_at) FROM stdin;
\.


--
-- Name: analysis_data analysis_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis_data
    ADD CONSTRAINT analysis_data_pkey PRIMARY KEY (id);


--
-- Name: analysis analysis_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis
    ADD CONSTRAINT analysis_pkey PRIMARY KEY (id);


--
-- Name: analysis_schema analysis_schema_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis_schema
    ADD CONSTRAINT analysis_schema_pkey PRIMARY KEY (id);


--
-- Name: donor donor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.donor
    ADD CONSTRAINT donor_pkey PRIMARY KEY (id);


--
-- Name: file file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.file
    ADD CONSTRAINT file_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: sample sample_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sample
    ADD CONSTRAINT sample_pkey PRIMARY KEY (id);


--
-- Name: specimen specimen_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.specimen
    ADD CONSTRAINT specimen_pkey PRIMARY KEY (id);


--
-- Name: study study_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.study
    ADD CONSTRAINT study_pkey PRIMARY KEY (id);


--
-- Name: upload upload_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.upload
    ADD CONSTRAINT upload_pkey PRIMARY KEY (id);


--
-- Name: analysis_id_study_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX analysis_id_study_id_uindex ON public.analysis USING btree (id, study_id);


--
-- Name: analysis_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX analysis_id_uindex ON public.analysis USING btree (id);


--
-- Name: analysis_schema_name_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX analysis_schema_name_index ON public.analysis_schema USING btree (name);


--
-- Name: analysis_schema_name_version_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX analysis_schema_name_version_index ON public.analysis_schema USING btree (name, version);


--
-- Name: analysis_schema_version_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX analysis_schema_version_index ON public.analysis_schema USING btree (version);


--
-- Name: analysis_study_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX analysis_study_id_uindex ON public.analysis USING btree (study_id);


--
-- Name: donor_id_study_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX donor_id_study_id_uindex ON public.donor USING btree (id, study_id);


--
-- Name: donor_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX donor_id_uindex ON public.donor USING btree (id);


--
-- Name: donor_study_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX donor_study_id_uindex ON public.donor USING btree (study_id);


--
-- Name: donor_submitter_id_study_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX donor_submitter_id_study_id_uindex ON public.donor USING btree (submitter_id, study_id);


--
-- Name: donor_submitter_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX donor_submitter_id_uindex ON public.donor USING btree (submitter_id);


--
-- Name: file_analysis_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX file_analysis_id_uindex ON public.file USING btree (analysis_id);


--
-- Name: file_id_analysis_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX file_id_analysis_id_uindex ON public.file USING btree (id, analysis_id);


--
-- Name: file_id_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX file_id_index ON public.file USING btree (id);


--
-- Name: file_name_analysis_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX file_name_analysis_id_uindex ON public.file USING btree (name, analysis_id);


--
-- Name: file_study_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX file_study_id_uindex ON public.file USING btree (study_id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: info_id_id_type_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX info_id_id_type_uindex ON public.info USING btree (id, id_type);


--
-- Name: info_id_type_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX info_id_type_uindex ON public.info USING btree (id_type);


--
-- Name: info_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX info_id_uindex ON public.info USING btree (id);


--
-- Name: sample_id_specimen_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX sample_id_specimen_id_uindex ON public.sample USING btree (id, specimen_id);


--
-- Name: sample_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX sample_id_uindex ON public.sample USING btree (id);


--
-- Name: sample_specimen_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX sample_specimen_id_uindex ON public.sample USING btree (specimen_id);


--
-- Name: sample_submitter_id_specimen_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX sample_submitter_id_specimen_id_uindex ON public.sample USING btree (submitter_id, specimen_id);


--
-- Name: sample_submitter_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX sample_submitter_id_uindex ON public.sample USING btree (submitter_id);


--
-- Name: sampleset_analysis_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX sampleset_analysis_id_uindex ON public.sampleset USING btree (analysis_id);


--
-- Name: sampleset_sample_id_analysis_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX sampleset_sample_id_analysis_id_uindex ON public.sampleset USING btree (sample_id, analysis_id);


--
-- Name: sampleset_sample_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX sampleset_sample_id_uindex ON public.sampleset USING btree (sample_id);


--
-- Name: specimen_donor_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX specimen_donor_id_uindex ON public.specimen USING btree (donor_id);


--
-- Name: specimen_id_donor_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX specimen_id_donor_id_uindex ON public.specimen USING btree (id, donor_id);


--
-- Name: specimen_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX specimen_id_uindex ON public.specimen USING btree (id);


--
-- Name: specimen_submitter_id_donor_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX specimen_submitter_id_donor_id_uindex ON public.specimen USING btree (submitter_id, donor_id);


--
-- Name: specimen_submitter_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX specimen_submitter_id_uindex ON public.specimen USING btree (submitter_id);


--
-- Name: study_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX study_id_uindex ON public.study USING btree (id);


--
-- Name: upload_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX upload_id_uindex ON public.upload USING btree (id);


--
-- Name: upload_study_id_analysis_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX upload_study_id_analysis_id_uindex ON public.upload USING btree (study_id, analysis_id);


--
-- Name: analysis analysis_data_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis
    ADD CONSTRAINT analysis_data_id_fk FOREIGN KEY (analysis_data_id) REFERENCES public.analysis_data(id);


--
-- Name: analysis analysis_schema_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis
    ADD CONSTRAINT analysis_schema_id_fk FOREIGN KEY (analysis_schema_id) REFERENCES public.analysis_schema(id);


--
-- Name: analysis analysis_study_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis
    ADD CONSTRAINT analysis_study_id_fkey FOREIGN KEY (study_id) REFERENCES public.study(id);


--
-- Name: donor donor_study_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.donor
    ADD CONSTRAINT donor_study_id_fkey FOREIGN KEY (study_id) REFERENCES public.study(id);


--
-- Name: file file_analysis_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.file
    ADD CONSTRAINT file_analysis_id_fkey FOREIGN KEY (analysis_id) REFERENCES public.analysis(id);


--
-- Name: file file_study_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.file
    ADD CONSTRAINT file_study_id_fkey FOREIGN KEY (study_id) REFERENCES public.study(id);


--
-- Name: sample sample_specimen_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sample
    ADD CONSTRAINT sample_specimen_id_fkey FOREIGN KEY (specimen_id) REFERENCES public.specimen(id);


--
-- Name: sampleset sampleset_analysis_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sampleset
    ADD CONSTRAINT sampleset_analysis_id_fkey FOREIGN KEY (analysis_id) REFERENCES public.analysis(id);


--
-- Name: sampleset sampleset_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sampleset
    ADD CONSTRAINT sampleset_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES public.sample(id);


--
-- Name: specimen specimen_donor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.specimen
    ADD CONSTRAINT specimen_donor_id_fkey FOREIGN KEY (donor_id) REFERENCES public.donor(id);


--
-- Name: upload upload_study_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.upload
    ADD CONSTRAINT upload_study_id_fkey FOREIGN KEY (study_id) REFERENCES public.study(id);


--
-- PostgreSQL database dump complete
--

