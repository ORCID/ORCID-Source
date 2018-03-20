
#Usage
# Arg 1: - name of the text file resulting from querying the DB   
# Arg 2: - the name of the file containing the transformed SQL query 

rm -rf output
mkdir output

script/find_single_space_commas.sh > output/$1
script/convert_csv_to_sql.sh  output/$1 output/$2