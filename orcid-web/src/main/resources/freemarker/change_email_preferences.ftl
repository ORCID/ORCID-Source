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
                    Send me notifications about changes to my ORCID Record.
                </label>
                <label class="checkbox">
                <@spring.formCheckbox "changeEmailPreferencesForm.sendOrcidNews"/>
                    Send me information about events ORCID is sponsoring and ORCID news.
                </label>
                
                <p class="help-block bg-white"><strong>Privacy:</strong> Your registration information and the information that
                    you submit will be processed by ORCID in accordance with Terms of Use and Privacy Policy. If you do
                    not wish for certain of your registration information to be viewable on your public ORCID Record page on
                    the ORCID website please indicate your preferences by using the applicable checkboxes.</p>
        </div>
        <div class="controls">
            <button id="bottom-submit-changes" class="btn btn-primary">Save changes</button>
            <button id="bottom-clear-changes" class="btn close-parent-popover" type="reset">Cancel</button>
        </div>
    </form>
</div>
</@base>
