<@public classes=['home']>
<script type="text/ng-template" id="search-ng2-template">
    <div class="row" id="SearchCtrl" data-search-query="${searchQuery?html}">
    <div class="centered">
        <span id="ajax-loader-search" class="orcid-hide"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
    </div>
    <#include "includes/search/search_results_ng2.ftl"/>
</script>
<@orcid.checkFeatureStatus 'ANGULAR2_QA'> 
    <search-ng2></search-ng2>
</@orcid.checkFeatureStatus>
<@orcid.checkFeatureStatus featureName='ANGULAR1_LEGACY' enabled=false>
    <div class="row" ng-controller="SearchCtrlV2" id="SearchCtrl" data-search-query="${searchQuery?html}">
        <div class="centered">
            <span id="ajax-loader-search" class="orcid-hide"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
        </div>
    	<div class="col-md-12">
    		<#if noResultsFound??>
    			<!-- no results -->
    			<div id="no-results-alert" class="orcid-hide alert alert-error"><@spring.message "orcid.frontend.web.no_results"/></div>
    		<#else>
    			<#include "includes/search/search_results.ftl"/>
    		</#if>
    	</div>
    </div>
</@orcid.checkFeatureStatus>
</@public>