\encoding UTF8
select replace(citation,'''',''''''), work_id from work where citation_type ='BIBTEX' and citation similar to ('%,\s?,%')
--select replace(citation,'''',''''''), work_id from work where work_id=481535 -test for a single work
