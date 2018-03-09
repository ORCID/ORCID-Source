
#Send the delete script in to postgres
T="$(date)"
psql -d orcid -U orcid -t -h 127.0.0.1 -A < $1
echo "Job started at: ${T}" Finished at: $(date)
