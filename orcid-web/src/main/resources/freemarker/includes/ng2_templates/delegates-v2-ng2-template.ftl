<script type="text/ng-template" 
    <@orcid.checkFeatureStatus featureName='HTTPS_IDS'> 
    id="delegates-v2-ng2-template"
    </@orcid.checkFeatureStatus>
    <@orcid.checkFeatureStatus featureName='HTTPS_IDS'> 
    id="delegates-ng2-template"
    </@orcid.checkFeatureStatus>

>
    <div 
        class="clearfix" 
        id="DelegatesCtrl" 
        data-search-query-url="${searchBaseUrl}"
    > 
        <div *ngIf="delegation?.length > 0" >
            <div class="ng-hide" *ngIf="showInitLoader == true;">

            </div>
            <table class="table table-bordered settings-table normal-width" *ngIf="delegation" >
                    <thead>
                        <tr>
                            <th class="width-30" (click)="changeSorting('receiverName.value')">${springMacroRequestContext.getMessage("manage.trustindividual")}</th>
                            <th (click)="changeSorting('receiverOrcid.value')">${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
                            <th class="width-15" (click)="changeSorting('approvalDate')"><@orcid.msg 'manage_delegators.delegates_table.access_granted' /></th>
                            <th class="width-10" ></th>
                        </tr>
                    </thead>
                    <tbody *ngIf="!delegation?.length > 0" >
                        <tr>
                            <td>No trusted individuals added yet</td>
                        </tr>
                    </tbody>
                    <tbody *ngIf="delegation?.length > 0" >
                        <tr *ngFor="let delegationDetails of delegation | orderBy:sort.column:sort.descending">
                            <td><a href="{{delegationDetails.receiverOrcid.uri}}" target="delegationDetails.receiverName.value">{{delegationDetails.receiverName.value}}</a></td>
                            <td><a href="{{delegationDetails.receiverOrcid.uri}}" target="delegationDetails.receiverOrcid.value">{{delegationDetails.receiverOrcid.uri}}</a></td>
                            <td>{{delegationDetails.approvalDate|date:'yyyy-MM-dd'}}</td>
                            <td class="tooltip-container">
                                <a
                                *ngIf="!(realUserOrcid === delegationDetails.receiverOrcid.value || isPasswordConfirmationRequired)"
                                (click)="confirmRevoke(delegationDetails.receiverName.value, delegationDetails.receiverOrcid.path)"
                                class="glyphicon glyphicon-trash grey">
                                    <div class="popover popover-tooltip top">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <span><@spring.message "manage.revokeaccess"/></span>
                                        </div>
                                    </div>                            
                                </a>
                                <span *ngIf="realUserOrcid === delegationDetails.receiverOrcid.path">${springMacroRequestContext.getMessage("manage_delegation.you")}</span>
                            </td>
                        </tr>
                    </tbody>
            </table>
        </div>
        <#if isPasswordConfirmationRequired>
        ${springMacroRequestContext.getMessage("manage_delegation.notallowed")}
        <#else>
        <p>${springMacroRequestContext.getMessage("manage_delegation.searchfortrustedindividuals")}</p>

        <div>
            <form (ngSubmit)="search()">
                <input type="text" placeholder="${springMacroRequestContext.getMessage("manage_delegation.searchplaceholder")}" class="input-xlarge inline-input" [(ngModel)]="input.text" />
                <input type="submit" class="btn btn-primary" value="<@orcid.msg 'search_for_delegates.btnSearch'/>" />
            </form>
        </div>
        <div>
            <table class=" table" *ngIf="areResults()">
                <thead>
                    <tr>
                        <th width="20%">${springMacroRequestContext.getMessage("manage_bio_settings.thname")}</th>
                        <th width="25%">${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
                        <th width="10%"></th>
                    </tr>
                </thead>
                <tbody>
                    <@orcid.checkFeatureStatus featureName="HTTPS_IDS">
                        <tr *ngFor='let result of results track by $index' class="new-search-result">
                            <td width="20%">{{getDisplayName(result)}}</td>
                            <td width="25%" class='search-result-orcid-id'><a href="{{result['orcid-identifier'].uri}}" target="{{result['orcid-identifier'].path}}">{{result['orcid-identifier'].uri}}</a></td>
                            <td width="10%">
                                <span *ngIf="effectiveUserOrcid !== result['orcid-identifier'].path">
                                    <span *ngIf="!delegatesByOrcid[result['orcid-identifier'].path]"
                                        (click)="confirmAddDelegate(result['given-names'] + ' ' + result['family-name'], result['orcid-identifier'].path, $index)"
                                        class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>
                                    <a *ngIf="delegatesByOrcid[result['orcid-identifier'].path]"
                                        (click)="confirmRevoke(result['given-names'] + ' ' + result['family-name'], result['orcid-identifier'].path, $index)"
                                        class="glyphicon glyphicon-trash grey"
                                        title="${springMacroRequestContext.getMessage("manage.revokeaccess")}"></a>
                                </span>
                                <span *ngIf="effectiveUserOrcid === result['orcid-identifier'].path">${springMacroRequestContext.getMessage("manage_delegation.you")}</span>
                            </td>
                        </tr>
                    </@orcid.checkFeatureStatus>
                    <@orcid.checkFeatureStatus featureName="HTTPS_IDS" enabled=false>
                        <tr *ngFor='let result of results' class="new-search-result">
                            <td width="20%"><a href="{{result['orcid-profile']['orcid-identifier'].uri}}" target="DisplayName">{{getDisplayName(result)}}</a></td>
                            <td width="25%" class='search-result-orcid-id'><a href="{{result['orcid-profile']['orcid-identifier'].uri}}" target="orcid-identifier">{{result['orcid-profile']['orcid-identifier'].path}}</td>
                            <td width="10%">
                                <span *ngIf="effectiveUserOrcid !== result['orcid-profile']['orcid-identifier'].path">
                                    <span *ngIf="!delegatesByOrcid[result['orcid-profile']['orcid-identifier'].path]"
                                        (click)="confirmAddDelegate(result['orcid-profile']['orcid-bio']['personal-details']['given-names'].value + ' ' + result['orcid-profile']['orcid-bio']['personal-details']['family-name'].value, result['orcid-profile']['orcid-identifier'].path, $index)"
                                        class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>
                                    <a *ngIf="delegatesByOrcid[result['orcid-profile']['orcid-identifier'].path]"
                                        (click)="confirmRevoke(result['orcid-profile']['orcid-bio']['personal-details']['given-names'].value + ' ' + result['orcid-profile']['orcid-bio']['personal-details']['family-name'].value, result['orcid-profile']['orcid-identifier'].path, $index)"
                                        class="glyphicon glyphicon-trash grey"
                                        title="${springMacroRequestContext.getMessage("manage.revokeaccess")}"></a>
                                </span>
                                <span *ngIf="effectiveUserOrcid === result['orcid-profile']['orcid-identifier'].path">${springMacroRequestContext.getMessage("manage_delegation.you")}</span>
                            </td>
                        </tr>
                    </@orcid.checkFeatureStatus>
                    </tbody>
                </table>
                <div id="show-more-button-container">
                    <button id="show-more-button" type="submit" class=" btn" (click)="getMoreResults()" *ngIf="areMoreResults">${springMacroRequestContext.getMessage("notifications.show_more")}</button>
                    <span id="ajax-loader" class="" *ngIf="showLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
                </div>
        </div>
        <div id="no-results-alert" class="orcid-hide alert alert-error no-delegate-matches"><@spring.message "orcid.frontend.web.no_results"/></div>
        </#if>
    </div>
</script>