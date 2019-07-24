<script type="text/ng-template" id="trusted-organizations-ng2-template"> 
    <div class="section-heading">
        <h1 id="manage-permissions">
            ${springMacroRequestContext.getMessage("manage.trusted_organisations")}
        </h1>
        <div class="popover-help-container">
            <i class="glyphicon glyphicon-question-sign"></i>
            <div id="trusted-organizations-help" class="popover bottom">
                <div class="arrow"></div>
                <div class="popover-content">
                    <p><@orcid.msg 'manage.help_popover.trustedOrganizations'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006973893" target="manage.help_popover.trustedOrganizations"><@orcid.msg 'common.learn_more'/></a></p>
                </div>
            </div>
        </div>
    </div>
    <div class="clearfix">
            <div *ngIf="!applicationSummaryList?.length > 0">
                <p><@orcid.msg 'manage.none_added.trustedOrganizations'/></p>
            </div>
            <div *ngIf="applicationSummaryList?.length > 0">
                <table class="table table-bordered settings-table normal-width">
                    <thead>
                        <tr>
                            <th width="35%">${springMacroRequestContext.getMessage("manage.trusted_organization")}</th>
                            <th width="5%">${springMacroRequestContext.getMessage("manage.thapprovaldate")}</th>
                            <th width="35%">${springMacroRequestContext.getMessage("manage.thaccesstype")}</th>
                            <td width="5%"></td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr *ngFor="let applicationSummary of applicationSummaryList">
                            <td class="revokeApplicationName">{{applicationSummary.name}}<br />
                                <a *ngIf="applicationSummary.websiteValue" href="{{getApplicationUrlLink(applicationSummary)}}" target="applicationSummary.websiteValue">{{applicationSummary.websiteValue}}</a>
                            </td>
                            <td width="35%">{{applicationSummary.approvalDate | date:'yyyy-MM-dd'}}</td>
                            <td width="5%">
                                <div *ngIf="applicationSummary.scopePaths">
                                    <span *ngFor="let scope of applicationSummary.scopePaths | keys">
                                    {{scope.value}}<br>
                                    </span>
                                </div>
                            </td>
                            <td width="5%" class="tooltip-container">
                                <a id="revokeAppBtn" name="{{applicationSummary.orcidPath}}" (click)="confirmRevoke(applicationSummary)" class="glyphicon glyphicon-trash grey" *ngIf="!isPasswordConfirmationRequired">
                                </a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
</script>