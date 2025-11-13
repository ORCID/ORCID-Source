<script type="text/ng-template" id="alert-banner-ng2-template">
    <div id="test-warn-div" class="alert alert-banner" *ngIf="showTestSiteNotification">
        <p><strong>${springMacroRequestContext.getMessage("common.js.domain.warn.warning_1")} {{baseDomainRemoveProtocol}} ${springMacroRequestContext.getMessage("common.js.domain.warn.warning_2")}</strong>&nbsp;<a href="https://orcid.org" target="orcid.org">orcid.org</a> ${springMacroRequestContext.getMessage("common.js.domain.warn.is_the_official")} <a href="https://mailinator.com">${springMacroRequestContext.getMessage("common.js.domain.warn.mailinator")}</a> ${springMacroRequestContext.getMessage("common.js.domain.warn.email_addresses")} <a href="<@orcid.msg 'common.kb_uri_default'/>360006972573" target="common.js.domain.warn.more_information">
        ${springMacroRequestContext.getMessage("common.js.domain.warn.more_information")}</a></p>
        <button *ngIf="dismissTestSiteNotificationAllowed" (click)="dismissTestSiteNotification()" id="test-warn-dismiss" class="btn btn-primary">${springMacroRequestContext.getMessage("common.cookies.dismiss")}</button>
    </div>
</script>
