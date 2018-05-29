<noscript>
    <div class="alert alert-banner">
         ${springMacroRequestContext.getMessage("common.browser-checks.functionalityofthissite")} <a href="http://www.enable-javascript.com/" target="common.browser-checks.instructionhowtoadd">
         ${springMacroRequestContext.getMessage("common.browser-checks.instructionhowtoadd")}</a>.<br>
         ${springMacroRequestContext.getMessage("common.cookies.orcid_uses")} <a href="${baseUri}/privacy-policy#TrackingTechnology" target="common.cookies.learn_more">
         ${springMacroRequestContext.getMessage("common.cookies.learn_more")}</a>.
    </div>
</noscript>
<script type="text/ng-template" id="alert-banner-ng2-template">
    <div id="cookie-alert" class="alert alert-banner" *ngIf="showCookieNotification">
        <p><span *ngIf="!cookiesEnabled">${springMacroRequestContext.getMessage("common.cookies.required")}</span> ${springMacroRequestContext.getMessage("common.cookies.orcid_uses")} <a href="${baseUri}/privacy-policy#TrackingTechnology" target="common.cookies.learn_more">
        ${springMacroRequestContext.getMessage("common.cookies.learn_more")}</a>.</p>
        <button (click)="dismissCookieNotification()" class="btn btn-primary">${springMacroRequestContext.getMessage("common.cookies.dismiss")}</button>
    </div>
</script>