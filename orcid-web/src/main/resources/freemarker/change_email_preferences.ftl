<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@base>
<div class="popover-frame">
    <@spring.bind "changeEmailPreferencesForm.*" />
	<form id="email-pref-form" class="form-horizontal" action="<@spring.url '/account/update-email-preferences'/>" method="post" autocomplete="off">
        <div class="control-group">
                <label class="checkbox">
                <@spring.formCheckbox "changeEmailPreferencesForm.sendOrcidChangeNotifcations"/>
                    ${springMacroRequestContext.getMessage("change_email_preferences.sendnotification")}
                </label>
                <label class="checkbox">
                <@spring.formCheckbox "changeEmailPreferencesForm.sendOrcidNews"/>
                    ${springMacroRequestContext.getMessage("change_email_preferences.sendinformation")}
                </label>
                
                <p class="help-block bg-white"><strong>${springMacroRequestContext.getMessage("change_email_preferences.privacy")}</strong>${springMacroRequestContext.getMessage("change_email_preferences.yourregistrationinfo")}</p>
        </div>
        <div class="controls">
            <button id="bottom-submit-changes" class="btn btn-primary">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</button>
            <button id="bottom-clear-changes" class="btn close-parent-popover" type="reset">${springMacroRequestContext.getMessage("freemarker.btncancel")}</button>
        </div>
    </form>
</div>
</@base>
