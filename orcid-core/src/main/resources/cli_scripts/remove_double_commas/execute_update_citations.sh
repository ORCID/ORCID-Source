
echo '\encoding UTF8' > /tmp/newfile
cat  $1 >> /tmp/newfile
cp /tmp/newfile $1
T="$(date)"
psql -d orcid -U orcid -t -A < $1
echo "Job started at: ${T}" Finished at: $(date)