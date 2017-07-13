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
<div>
    <p ng-cloak ng-if="areResults()">${springMacroRequestContext.getMessage("search_results.showing")} {{resultsShowing}} ${springMacroRequestContext.getMessage("search_results.of")} {{numFound}} <span ng-if="numFound==1">${springMacroRequestContext.getMessage("search_results.result")}</span><span ng-if="numFound>1">${springMacroRequestContext.getMessage("search_results.results")}</span></p>
	<table class="ng-cloak table table-striped" ng-show="areResults()">
		<thead>
		<tr>
			<th>${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
			<th>${springMacroRequestContext.getMessage("search_results.thGivenname")}</th>
			<th>${springMacroRequestContext.getMessage("search_results.thFamilynames")}</th>
			<th>${springMacroRequestContext.getMessage("search_results.thOthernames")}</th>
		</tr>
		</thead>
		<tbody>
			<tr ng-repeat='result in results' class="new-search-result">
				<td class='search-result-orcid-id'><a href="{{result['orcid-profile']['orcid-identifier'].uri}}">{{result['orcid-profile']['orcid-identifier'].path}}</td>
				<td>{{result['orcid-profile']['orcid-bio']['personal-details']['given-names'].value}}</td>
				<td>{{result['orcid-profile']['orcid-bio']['personal-details']['family-name'].value}}</td>
				<td>{{concatPropertyValues(result['orcid-profile']['orcid-bio']['personal-details']['other-names']['other-name'], 'value')}}</td>
			</tr>
		</tbody>
	</table>
	<div id="show-more-button-container">
		<button id="show-more-button" type="submit" class="btn btn-primary" ng-click="getMoreResults()" ng-show="areMoreResults" ng-cloak>Show more</button>
		<span id="ajax-loader-show-more" class="orcid-hide"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
	</div>
	<div id="no-results-alert" class="orcid-hide alert alert-error"><@spring.message "orcid.frontend.web.no_results"/></div>
    <div id="search-error-alert" class="orcid-hide alert alert-error"><@spring.message "orcid.frontend.web.search_error"/></div>
</div>