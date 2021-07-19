<script type="text/ng-template" id="delegators-ng2-template">
    <ng-container *ngIf="userInfo.IN_DELEGATION_MODE=='false' || (userInfo.IN_DELEGATION_MODE=='true' && userInfo.DELEGATED_BY_ADMIN=='true')">
        <div class="section-heading" *ngIf="delegators?.length > 0">
            <h1>
                <@orcid.msg 'manage_delegators.title' />
            </h1>            
        </div>
        <div class="clearfix" *ngIf="delegators?.length > 0">
            <#if springMacroRequestContext.requestUri?contains("/delegators")>
                <p>
                <@orcid.msg 'manage_delegators.description' />
                </p>
                <p>
                    <a href="<@orcid.msg 'manage_delegators.learn_more.link.url' />" target="manage_delegators.learn_more.link.text"><@orcid.msg 'manage_delegators.learn_more.link.text' /></a>&nbsp;<@orcid.msg 'manage_delegators.learn_more.text' />
                </p>
                <p><@orcid.msg 'manage_delegators.search'/></p>
                <div class="form-group">
                    <input id="delegatorsSearch" name="delegatorsSearch" type="text" placeholder="<@orcid.msg 'manage_delegators.search.placeholder' />" class="form-control input-xlarge inline-input" (selectItem)="selectDelegator($event.item)" [ngbTypeahead]="searchDelegators" [inputFormatter]="formatSearchDelegatorsInput" [resultFormatter]="formatSearchDelegatorsResult" [focusFirst]=true [editable]=false [showHint]=true />
                </div>
            </#if>
                <tr> 
                <td colspan="2">
                    <div>        
                        <table class="table table-bordered settings-table normal-width">
                            <thead>
                                <tr>
                                    <th class="width-30" (click)="changeSorting('receiverName.value')"><@orcid.msg 'manage.thproxy' /></th>
                                    <th class="width-30" (click)="changeSorting('receiverOrcid.path')"><@orcid.msg 'search_results.thORCIDID' /></th>
                                    <th class="width-15" (click)="changeSorting('approvalDate')"><@orcid.msg 'manage_delegators.delegates_table.access_granted' /></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr *ngFor="let delegationDetails of delegators | orderBy:sort.column:sort.descending">
                                    <td width="35%"><a (click)="switchUser(delegationDetails.giverOrcid.path)">{{delegationDetails.giverName.value}}</a></td>
                                    <td width="35%"><a href="{{delegationDetails.giverOrcid.uri}}" target="{{delegationDetails.giverOrcid.path}}">{{delegationDetails.giverOrcid.path}}</a></td>                        
                                    <td width="15%">{{delegationDetails.approvalDate|date:'yyyy-MM-dd'}}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </div> 
    </ng-container> 
</script>