<script type="text/ng-template" id="alert-banner-ng2-template">
    <div id="test-warn-div" class="alert alert-banner" *ngIf="showTestSiteNotification">
        <p><strong>${springMacroRequestContext.getMessage("common.js.domain.warn.warning_1")} {{baseDomainRemoveProtocol}} ${springMacroRequestContext.getMessage("common.js.domain.warn.warning_2")}</strong>&nbsp;<a href="https://orcid.org" target="orcid.org">orcid.org</a> ${springMacroRequestContext.getMessage("common.js.domain.warn.is_the_official")} <a href="https://mailinator.com">${springMacroRequestContext.getMessage("common.js.domain.warn.mailinator")}</a> ${springMacroRequestContext.getMessage("common.js.domain.warn.email_addresses")} <a href="<@orcid.msg 'common.kb_uri_default'/>360006972573" target="common.js.domain.warn.more_information">
        ${springMacroRequestContext.getMessage("common.js.domain.warn.more_information")}</a></p>
        <button *ngIf="dismissTestSiteNotificationAllowed" (click)="dismissTestSiteNotification()" id="test-warn-dismiss" class="btn btn-primary">${springMacroRequestContext.getMessage("common.cookies.dismiss")}</button>
    </div>
    <div id="cookie-alert" class="alert alert-banner" *ngIf="showCookieNotification">
        <p><span *ngIf="!cookiesEnabled">${springMacroRequestContext.getMessage("common.cookies.required")}</span> ${springMacroRequestContext.getMessage("common.cookies.orcid_uses")} <a href="{{getBaseUri()}}/privacy-policy#TrackingTechnology" target="common.cookies.learn_more">
        ${springMacroRequestContext.getMessage("common.cookies.learn_more")}</a>.</p>
        <button (click)="dismissCookieNotification()" class="btn btn-primary">${springMacroRequestContext.getMessage("common.cookies.dismiss")}</button>
    </div>
</script>