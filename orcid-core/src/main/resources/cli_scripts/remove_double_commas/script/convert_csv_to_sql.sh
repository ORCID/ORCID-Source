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


echo "Input File" $1
echo "Output File" $2
#Strip out double commas
sed "s/,\s\?,/,/g" -b $1 > $2
#Alter citation field to become an update statement based on the changed citation field from the step above
sed "s/\@article\s\+{/update work set citation = '\0/g" -b -i $2
#Add the work id as the where clause
sed "s/;\([0-9]\{6,7\}\)/\' where work_id=\0; /g" -b  -i $2
#remove the double colon we've used as temp delimiter
sed "s/where work_id=;/where work_id=/g" -b -i $2
