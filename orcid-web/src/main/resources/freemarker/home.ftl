<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@public classes=['home'] >

<#include "sandbox_warning.ftl"/>
<div class="page-header">
    <h1>Identify. Communicate. Collaborate</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        <p>ORCID is a global, multi-disciplinary scholarly research community. With a unique identifier assigned to each author in ORCID, you can eliminate author misidentification and view an author’s citation metrics instantly. Search the registry to find collaborators, review publication lists and explore how research is used around the world.</p>
        <#if reducedFunctionalityMode>
            <p><strong><span>IMPORTANT UPDATE</span></strong><br>
            In preparation for the upcoming <a href="http://dev.orcid.org/launch" target="_blank">launch</a> we have made some changes to the Developers Sandbox site. This site has been updated to use the new <span>API</span>/XML, a description of which can be found on the <a href="http://dev.orcid.org/resources" target="_blank">Developers Portal</a>.</p>
            <p>In addition to the update to the <span>API</span>/XML, we also will be improving our user experience for using the site. While we are working on this new user experience, this Developers Sandbox user interface will not be updated (though will be at launch). As a result, while optimized for use with the <span>API</span>, this Sandbox will not have a fully functioning user interface. Strategies for interfacing with the database directly can be found on the <a href="http://dev.orcid.org/resources" target="_blank">Resources page</a> of the Developers Portal.</p>
            <p>Thanks for your interest in <span>ORCID</span>. If you have any questions, please contact us at <a hre="mailto:devsupport@orcid.org">devsupport@orcid.org</a>.</p>   
        <#else>
	        <form class="well form-horizontal" action="<@spring.url '/orcid-search/search-for-orcid'/>" method="get">
	            <h3>Search ORCID</h3>
	            <div class="control-group">
	                <label for="search-field-given-name" class="control-label">Given Name</label>
	                <div class="controls">
	                   <input id="search-field-given-name" type="text" placeholder="Given name" class="span3" name="givenName"/>
	                </div>
	            </div>
	            <div class="control-group">
	                <label for="search-field-family-name" class="control-label">Family Name</label>
	                <div class="controls">
	                   <input id="search-field-family-name" type="text" placeholder="Family name" class="span3" name="familyName"/>
	                </div>
	            </div>
	            <div class="control-group">
	                <div class="controls">
	                   <button class="btn" type="submit">Search</button>
	                </div>
	            </div>
	        </form>
        </#if>
    </div>
    <#if !reducedFunctionalityMode>
	    <div id="top-keywords" class="span4 well">
	        <ul class="nav nav-list">
	            <li class="nav-header">Top 20 keywords</li>
	        </ul>
	    </div>
    </#if>
</div>
</@public>