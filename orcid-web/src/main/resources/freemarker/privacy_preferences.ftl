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
    <@spring.bind "changeVisibilityPreferencesForm.*"/>
	<form id="privacy-pref-form" class="form-horizontal" action="<@spring.url '/account/update-privacy-preferences'/>" method="post" autocomplete="off">
	    <label class="privacy-toggle-lbl">Default privacy for new Works</label>
        <label class="visibility-lbl">
            <@spring.formSingleSelect "changeVisibilityPreferencesForm.workVisibilityDefault", visibilities />
        </label>
        <@orcid.privacy "" changeVisibilityPreferencesForm.workVisibilityDefault 'btn-group privacy-group'/>
        <div class="relative">
            <button id="privacy-pref-submit-changes" class="btn btn-primary">Save changes</button>
            <button id="privacy-pref-clear-changes" class="btn close-parent-popover" type="reset">Cancel</button>
        </div>
    </form>
</div>
</@base>
