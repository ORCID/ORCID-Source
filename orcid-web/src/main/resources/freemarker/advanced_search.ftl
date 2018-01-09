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
<@public classes=['home'] >
<script type="text/ng-template" id="search-ng2-template">
	<div class="row">
		<div class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-offset-9 col-xs-12">
			
				<div class="main-search">
					<div class="row">
						<h1>${springMacroRequestContext.getMessage("orcid_bio_search.h1advancedsearch")}</h1>
						<p>${springMacroRequestContext.getMessage("orcid_bio_search.searchpublicly")}</p>
					</div>
			    	<form id="searchForm" class="form-horizontal" (ngSubmit)="getFirstResults(input, results)">
					    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<fieldset>
							<div class="row">
								<div class="control-group col-md-6">
									<!-- Given name -->
									<label for="givenName" class="control-label">${springMacroRequestContext.getMessage("orcid_bio_search.labelfirstname")}</label>
									<div class="controls">
										<input type="text" class="input-xlarge" name="givenNames" id="givenNames" [(ngModel)]="input.givenNames">
									</div>
								</div>
								<div class="control-group col-md-6">
									<!-- Family name -->
									<label for="familyName" class="control-label">${springMacroRequestContext.getMessage("orcid_bio_search.labellastname")}</label>
									<div class="controls">
										<input type="text" class="input-xlarge" name="familyName" id="familyName" [(ngModel)]="input.familyName">
									</div>
								</div>
							</div>
							<div class="row">
								<div class="control-group">
									<div class="checkbox">
										<!-- Other names -->
										<label for="otherNamesSearchable">
										<input type="checkbox" name="otherNamesSearchable" id="otherNamesSearchable" [(ngModel)]="input.searchOtherNames">
										${springMacroRequestContext.getMessage("orcid_bio_search.labelalsosearchothernames")}</label>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="control-group col-md-6">
									<!-- Affiliation organization -->
									<label for="affiliationOrg" class="control-label">${springMacroRequestContext.getMessage("orcid_bio_search.labelaffiliationorg")}</label>
									<div class="popover-help-container">
							            <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
							            <div id="search-help-affiliation" class="popover bottom">
							              <div class="arrow"></div>
							              <div class="popover-content">
							                <p>${springMacroRequestContext.getMessage("orcid_bio_search.popover_help.affiliation1")} <a href="https://www.ringgold.com/" target="orcid_bio_search.popover_help.affiliation2" rel="noopener noreferrer">${springMacroRequestContext.getMessage("orcid_bio_search.popover_help.affiliation2")}</a> ${springMacroRequestContext.getMessage("orcid_bio_search.popover_help.affiliation3")}</p>
							              </div>
							            </div>
							         </div>
									<div class="controls">
										<input type="text" class="input-xlarge" name="affiliationOrg" id="affiliationOrg" [(ngModel)]="input.affiliationOrg">
									</div>
								</div>
								<div class="control-group col-md-6">
									<!-- Keyword -->
									<label for="familyName" class="control-label">${springMacroRequestContext.getMessage("orcid_bio_search.labelkeywords")}</label>
									<div class="popover-help-container">
							            <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
							            <div id="search-help-keyword" class="popover bottom">
							              <div class="arrow"></div>
							              <div class="popover-content">
							                <p>${springMacroRequestContext.getMessage("orcid_bio_search.popover_help.keyword")}</p>
							              </div>
							            </div>
							         </div>
									<div class="controls">
											<input type="text" class="input-xlarge" name="keyword" id="keyword" [(ngModel)]="input.keyword">
									</div>
								</div>
							</div>
						</fieldset>
						<hr>
						<fieldset>
							<div class="row">
								<p>${springMacroRequestContext.getMessage("orcid_bio_search.searchorcid")}</p>
								<div class="control-group">
									<!-- Search by ORCID iD -->
									<label for="orcid" class="control-label">${springMacroRequestContext.getMessage("orcid_bio_search.labelsearchbyorcid")}</label>
									<div class="controls">
										<input type="text" class="input-xlarge" name="orcid" id="orcid" [(ngModel)]="input.text">
										<span id="invalid-orcid" class="orcid-error" *ngIf="!isValidOrcidId()"><@orcid.msg 'admin.profile_deprecation.errors.invalid_regex' /></span>
									</div>
								</div>
							</div>
						</fieldset>
						<hr>
						<div class="row">
							<p *ngIf="hasErrors"><span class="orcid-error">${springMacroRequestContext.getMessage("orcid_bio_search.pyoumustpopulate")}</span></p>
							<div class="control-group">
								<div class="controls">
									<button class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("orcid_bio_search.btnsearch")}</button>
									<span id="ajax-loader-search" class="orcid-hide"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
								</div>
							</div>
						</div>
					</form>
				</div><!--main search-->
			</div><!--row-->
		</div>
		<div class="row search-results">
			<div class="col-md-12">
				<p *ngIf="areResults()">${springMacroRequestContext.getMessage("search_results.showing")} {{resultsShowing}} ${springMacroRequestContext.getMessage("search_results.of")} {{numFound}} <span *ngIf="numFound==1">${springMacroRequestContext.getMessage("search_results.result")}</span><span *ngIf="numFound>1">${springMacroRequestContext.getMessage("search_results.results")}</span></p>
				<table class="table table-striped" *ngIf="areResults()">
					<thead>
						<tr>
							<th>${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thGivenname")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thFamilynames")}</th>
							<th>${springMacroRequestContext.getMessage("search_results.thOthernames")}</th>
							<th>${springMacroRequestContext.getMessage("workspace_bio.Affiliations")}</th>
						</tr>
					</thead>
		            <tbody>
		                <tr *ngFor="let result of allResults" class="new-search-result">
		                    <td class='search-result-orcid-id'><a href="{{result['orcid-identifier'].uri}}">{{result['orcid-identifier'].uri}}</a></td>
    						<td>{{result['given-names']}}</td>
    						<td>{{result['family-name']}}</td>	
    						<td>{{concatPropertyValues(result['other-name'], 'content')}}</td>
    						<td>{{result['affiliations']}}</td>
		                </tr>
		            </tbody>
		        </table>
		        <div id="show-more-button-container">
					<button id="show-more-button" type="submit" class="btn btn-primary" (click)="getMoreResults()" *ngIf="areMoreResults">Show more</button>
					<span id="ajax-loader-show-more" class="orcid-hide"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
				</div>
				<div id="no-results-alert" class="orcid-hide alert alert-error"><@spring.message "orcid.frontend.web.no_results"/></div>
			    <div id="search-error-alert" class="orcid-hide alert alert-error"><@spring.message "orcid.frontend.web.search_error"/></div>
	        </div><!--col 12-->
        </div><!--search results-->
</script> 
<search-ng2></search-ng2>
</@public>