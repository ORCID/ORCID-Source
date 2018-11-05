<script type="text/ng-template" id="manage-trusted-organizations-ng2-template"> 
    <div ng-controller="revokeApplicationFormCtrl" class="clearfix">
            <div ng-show="!applicationSummaryList.length > 0" ng-cloak>
                <p><@orcid.msg 'manage.none_added.trustedOrganizations'/></p>
            </div>
            <div ng-show="applicationSummaryList.length > 0" ng-cloak>
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
                        <tr data-ng-repeat="applicationSummary in applicationSummaryList">
                            <td class="revokeApplicationName">{{applicationSummary.name}}<br />
                                <a data-ng-hide="applicationSummary.websiteValue == null" href="{{getApplicationUrlLink(applicationSummary)}}" target="applicationSummary.websiteValue">{{applicationSummary.websiteValue}}</a>
                            </td>
                            <td width="35%">{{applicationSummary.approvalDate}}</td>
                            <td width="5%">
                                <div data-ng-show="applicationSummary.scopePaths">
                                    <span data-ng-repeat="(key, value) in applicationSummary.scopePaths">
                                    {{value}}
                                    </span>
                                </div>
                            </td>
                            <td width="5%" class="tooltip-container">
                                <a id="revokeAppBtn" name="{{applicationSummary.orcidPath}}" ng-click="confirmRevoke(applicationSummary)"
                                    class="glyphicon glyphicon-trash grey"
                                    ng-hide="isPasswordConfirmationRequired">
                                        <div class="popover popover-tooltip top">
                                            <div class="arrow"></div>
                                            <div class="popover-content">
                                                <span><@spring.message "manage.revokeaccess"/></span>
                                            </div>
                                        </div>
                                    </a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
</script>