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

\encoding UTF8
select replace(citation,'''',''''''), work_id from work where citation_type ='BIBTEX' and citation similar to ('%,\s?,%')
--select replace(citation,'''',''''''), work_id from work where work_id=481535 -test for a single work
