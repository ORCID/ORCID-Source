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
<@public classes=['home']>

<div class="row" ng-controller="SearchCtrl" id="SearchCtrl" data-search-query="${searchQuery?html}">
	<div class="col-md-12">
		<#if noResultsFound??>
			<!-- no results -->
		<#else>
			<#include "includes/search/search_results.ftl"/>
		</#if>
	</div>
</div>

</@public>