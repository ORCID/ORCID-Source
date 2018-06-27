<script type="text/ng-template" id="delegators-ng2-template">
    <div>        
        <table class="table table-bordered settings-table normal-width" *ngIf="delegators?.length > 0" >
            <thead>
                <tr>
                    <th width="35%" (click)="changeSorting('receiverName.value')"><@orcid.msg 'manage.thproxy' /></th>
                    <th width="35%" (click)="changeSorting('receiverOrcid.path')"><@orcid.msg 'search_results.thORCIDID' /></th>
                    <th width="15%" (click)="changeSorting('approvalDate')"><@orcid.msg 'manage_delegators.delegates_table.access_granted' /></th>
                    <th width="15%" (click)="changeSorting('lastModifiedDate')"><@orcid.msg 'manage_delegators.delegates_table.last_modified' /></th>
                </tr>
            </thead>
            <tbody>
                <tr *ngFor="let delegationDetails of delegators | orderBy:sort.column:sort.descending">
                    <td width="35%"><a href="<@orcid.rootPath '/switch-user?username='/>{{delegationDetails.giverOrcid.path}}">{{delegationDetails.giverName.value}}</a></td>
                    <td width="35%"><a href="{{delegationDetails.giverOrcid.uri}}" target="{{delegationDetails.giverOrcid.path}}">{{delegationDetails.giverOrcid.path}}</a></td>                        
                    <td width="15%">{{delegationDetails.approvalDate|date:'yyyy-MM-dd'}}</td>
                    <td width="15%">{{delegationDetails.lastModifiedDate|date:'yyyy-MM-dd'}}</td>
                </tr>
            </tbody>
        </table>
    </div>
</script>