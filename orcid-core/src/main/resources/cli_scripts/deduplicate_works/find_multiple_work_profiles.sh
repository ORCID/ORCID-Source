
#Pipe this script to a file of your choice to get the resultant output
#We don't include it as an arg to the script due to the crossover between psqls command line args and the
#interactive options provided to psql
psql -d orcid -U orcid -t -A -h 127.0.0.1 -F\; < sql/find_multiple_works_profiles.sql > $1
