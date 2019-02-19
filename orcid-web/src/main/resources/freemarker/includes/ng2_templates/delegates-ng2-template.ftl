<script type="text/ng-template" id="delegates-ng2-template">
    <div class="clearfix" id="DelegatesCtrl"> 
        <div *ngIf="delegation?.length > 0" >
            <table class="table table-bordered settings-table normal-width" *ngIf="delegation">
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
                <input type="text" name="search" placeholder="${springMacroRequestContext.getMessage("manage_delegation.searchplaceholder")}" class="input-xlarge inline-input" [(ngModel)]="input.text" />
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
                    <tr *ngFor='let result of allResults; let $index=index' class="new-search-result">
                        <td width="20%">
                            <span *ngIf="result['credit-name']">{{result['credit-name']}}</span>
                            <span *ngIf="!result['credit-name']">{{result['given-names']}} {{result['family-name']}}</span>
                        </td>
                        <td width="25%" class='search-result-orcid-id'><a href="{{result['orcid-identifier'].uri}}" target="{{result['orcid-identifier'].path}}">{{result['orcid-identifier'].uri}}</a></td>
                        <td width="10%">
                            <span *ngIf="effectiveUserOrcid !== result['orcid-identifier'].path">
                                <span *ngIf="!delegatesByOrcid[result['orcid-identifier'].path]"
                                    (click)="confirmAddDelegate(result['credit-name'], result['given-names'], result['family-name'], result['orcid-identifier'].path, $index)"
                                    class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>
                                <a *ngIf="delegatesByOrcid[result['orcid-identifier'].path]"
                                    (click)="confirmRevoke(result['given-names'] + ' ' + result['family-name'], result['orcid-identifier'].path, $index)"
                                    class="glyphicon glyphicon-trash grey"
                                    title="${springMacroRequestContext.getMessage("manage.revokeaccess")}"></a>
                            </span>
                            <span *ngIf="effectiveUserOrcid === result['orcid-identifier'].path">${springMacroRequestContext.getMessage("manage_delegation.you")}</span>
                        </td>
                    </tr>
                </tbody>
            </table>
            <div id="show-more-button-container">
                <button id="show-more-button" type="submit" class=" btn" (click)="getMoreResults()" *ngIf="areMoreResults">${springMacroRequestContext.getMessage("notifications.show_more")}</button>
                <span id="ajax-loader" class="" *ngIf="showLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
            </div>
        </div>
        <div *ngIf="noResults" id="no-results-alert" class="alert alert-error no-delegate-matches"><@spring.message "orcid.frontend.web.no_results"/></div>
        </#if>
    </div>
</script>