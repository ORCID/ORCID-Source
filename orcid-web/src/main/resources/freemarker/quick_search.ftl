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
<@public classes=['home']>
<div class="row-fluid">
    <div class="row">
        <div class="span12">
            <div ng-controller="QuickSearchCtrl" id="QuickSearchCtrl" data-search-query-url="${searchQueryUrl}">
                <h3 class="ng-cloak search-result-head" ng-show="areResults()">${springMacroRequestContext.getMessage("search_results.h3Searchresults")}</h3>
		        <table class="ng-cloak table table-striped" ng-show="areResults()">
		            <thead>
		            <tr>
		                <th>${springMacroRequestContext.getMessage("search_results.thRelevance")}</th>
		                <th>${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
		                <th>${springMacroRequestContext.getMessage("search_results.thGivenname")}</th>
		                <th>${springMacroRequestContext.getMessage("search_results.thFamilynames")}</th>
		                <th>${springMacroRequestContext.getMessage("search_results.thOthernames")}</th>
		                <th>${springMacroRequestContext.getMessage("search_results.thInstitutions")}</th>
		            </tr>
		            </thead>
		            <tbody>
		                <tr ng-repeat='result in results' class="new-search-result">
		                    <td>{{result['relevancy-score'].value | number:3}}</td>
		                    <td class='search-result-orcid-id'><a href="{{result['orcid-profile']['orcid-id']}}">{{result['orcid-profile'].orcid.value}}</td>
		                    <td>{{result['orcid-profile']['orcid-bio']['personal-details']['given-names'].value}}</td>
		                    <td>{{result['orcid-profile']['orcid-bio']['personal-details']['family-name'].value}}</td>
		                    <td>{{concatPropertyValues(result['orcid-profile']['orcid-bio']['personal-details']['other-names']['other-name'], 'value')}}</td>
		                    <td>{{concatPropertyValues(result['orcid-profile']['orcid-bio']['affiliations'], 'affiliation-name')}}</td>
		                </tr>
		            </tbody>
		        </table>
		        <div id="show-more-button-container">
                    <button id="show-more-button" type="submit" class="ng-cloak btn" ng-click="getMoreResults()" ng-show="areMoreResults">Show more</button>
                    <span id="ajax-loader"><i class="icon-spinner icon-large icon-spin green"></i></span>
                </div>
                <div id="no-results-alert" class="hide alert alert-error"><@spring.message "orcid.frontend.web.no_results"/></div>
            </div>   
        </div>
    </div>
</div>
</@public>