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


#Convert any works ids between 4 and 7 digits to be delete statements
sed "s/\([0-9]\{4,7\}\)/delete from profile_work where work_id=\0;/g" $1 > $2
