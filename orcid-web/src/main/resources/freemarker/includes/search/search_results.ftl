<div>
    <p ng-cloak ng-if="areResults()">${springMacroRequestContext.getMessage("search_results.showing")} {{resultsShowing}} ${springMacroRequestContext.getMessage("search_results.of")} {{numFound}} <span ng-if="numFound==1">${springMacroRequestContext.getMessage("search_results.result")}</span><span ng-if="numFound>1">${springMacroRequestContext.getMessage("search_results.results")}</span></p>
	<table class="ng-cloak table table-striped" ng-show="areResults()">
		<thead>
		<tr>
			<th>${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
			<th>${springMacroRequestContext.getMessage("search_results.thGivenname")}</th>
			<th>${springMacroRequestContext.getMessage("search_results.thFamilynames")}</th>
			<th>${springMacroRequestContext.getMessage("search_results.thOthernames")}</th>
            <@orcid.checkFeatureStatus featureName='SEARCH_RESULTS_AFFILIATIONS'>
                <th>${springMacroRequestContext.getMessage("workspace_bio.Affiliations")}</th>
            </@orcid.checkFeatureStatus>
		</tr>
		</thead>
		<tbody>
			<tr ng-repeat='result in results' class="new-search-result">
				<td class='search-result-orcid-id'><a href="{{result['orcid-identifier'].uri}}">{{result['orcid-identifier'].uri}}</td>
				<td ng-bind="getNames(result)">{{result['given-names']}}</td>
				<td>{{result['family-name']}}</td>
				<td>{{concatPropertyValues(result['other-name'], 'content')}}</td>
                <@orcid.checkFeatureStatus featureName='SEARCH_RESULTS_AFFILIATIONS'>
                    <td ng-bind="getAffiliations(result)">{{result['affiliations']}}</td>
                </@orcid.checkFeatureStatus>
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