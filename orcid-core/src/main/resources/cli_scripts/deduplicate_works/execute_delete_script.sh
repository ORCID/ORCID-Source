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


#Send the delete script in to postgres
T="$(date)"
psql -d orcid -U orcid -t -h 127.0.0.1 -A < $1
echo "Job started at: ${T}" Finished at: $(date)
