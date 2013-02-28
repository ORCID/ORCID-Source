#
# =============================================================================
#
# ORCID (R) Open Source
# http://orcid.org
#
# Copyright (c) 2012-2013 ORCID, Inc.
# Licensed under an MIT-Style License (MIT)
# http://orcid.org/open-source-license
#
# This copyright and license information (including a link to the full license)
# shall be included in its entirety in all copies or substantial portion of
# the software.
#
# =============================================================================
#


#Usage
# Arg 1: - name of the text file resulting from querying the DB   
# Arg 2: - the name of the file containing the transformed SQL query 

rm -rf output
mkdir output

script/find_single_space_commas.sh > output/$1
script/convert_csv_to_sql.sh  output/$1 output/$2