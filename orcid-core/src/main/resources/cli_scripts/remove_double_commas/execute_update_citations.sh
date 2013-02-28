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


echo '\encoding UTF8' > /tmp/newfile
cat  $1 >> /tmp/newfile
cp /tmp/newfile $1
T="$(date)"
psql -d orcid -U orcid -t -A < $1
echo "Job started at: ${T}" Finished at: $(date)