<@public classes=['home'] >
<script type="text/ng-template" id="search-ng2-template">
    <div class="container">
        <div class="row">
            <div class="col-md-9 col-xs-12 main-search">
                <div class="row">
                    <h1>${springMacroRequestContext.getMessage("orcid_bio_search.h1advancedsearch")}</h1>
                    <p>${springMacroRequestContext.getMessage("orcid_bio_search.searchpublicly")}</p>
                </div>
                <form id="searchForm" class="form-horizontal" (ngSubmit)="getFirstResults(input, results)">                    
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
                                <div class="controls">
                                    <input placeholder="${springMacroRequestContext.getMessage('orcid_bio_search.placeholder.affiliation')}" type="text" class="input-xlarge" name="affiliationOrg" id="affiliationOrg" [(ngModel)]="input.affiliationOrg">
                                </div>
                            </div>
                            <div class="control-group col-md-6">
                                <!-- Keyword -->
                                <label for="familyName" class="control-label">${springMacroRequestContext.getMessage("orcid_bio_search.labelkeywords")}</label>
                                <div class="controls">
                                        <input type="text" class="input-xlarge" name="keyword" id="keyword" [(ngModel)]="input.keyword">
                                </div>
                            </div>
                        </div>
                    </fieldset>
                    <hr class="row">
                    <fieldset>
                        <div class="row">
                            <p>${springMacroRequestContext.getMessage("orcid_bio_search.searchorcid")}</p>
                            <div class="control-group">
                                <!-- Search by ORCID iD -->
                                <label for="orcid" class="control-label">${springMacroRequestContext.getMessage("orcid_bio_search.labelsearchbyorcid")}</label>
                                <div class="controls">
                                    <input  placeholder="XXXX-XXXX-XXXX-XXXX" type="text" class="input-xlarge" name="orcid" id="orcid" [(ngModel)]="input.text">
                                    <span id="invalid-orcid" class="orcid-error" *ngIf="!isValidOrcidId()"><@orcid.msg 'admin.profile_deprecation.errors.invalid_regex' /></span>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                    <hr class="row">
                    <div class="row">
                        <p *ngIf="hasErrors"><span class="orcid-error">${springMacroRequestContext.getMessage("orcid_bio_search.pyoumustpopulate")}</span></p>
                        <div class="control-group">
                            <div class="controls">
                                <button class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("orcid_bio_search.btnsearch")}</button>
                                <span *ngIf="searchResultsLoading"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="container search-results">
        <#include "includes/search/search_results_ng2.ftl"/>
    </div><!--search results-->
</script>
<search-ng2></search-ng2>
</@public>