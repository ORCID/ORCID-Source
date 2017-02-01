<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@public classes=['home'] nav="record-corrections">	>
<#escape x as x?html>
	<div class="row">	    
	    <div class="col-md-9 col-md-offset-3">
	    	<h1><@orcid.msg 'record_corrections.heading'/></h1>
	    	<p><@orcid.msg 'record_corrections.a_core'/>&nbsp;<a href="<@orcid.rootPath '/about/trust/home'/>"><@orcid.msg 'record_corrections.orcid_trust'/></a>&nbsp;<@orcid.msg 'record_corrections.principle_is'/></p>
	    </div>
    </div>
</#escape>
</@public>