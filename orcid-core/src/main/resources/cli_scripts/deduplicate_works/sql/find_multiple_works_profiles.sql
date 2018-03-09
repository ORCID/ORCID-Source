select w.orcid from work w
			group by w.orcid
			having count (w.work_id) > 1
		order by (w.orcid) asc