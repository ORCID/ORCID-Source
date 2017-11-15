<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->

<script type="text/ng-template" id="works-privacy-preferences-ng2-template">
    <div class="editTablePadCell35" id="privacy-settings">
        ${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}
        <br>
        <@orcid.privacyToggle3Ng2
        angularModel="default_visibility"
        publicClick="updateActivitiesVisibilityDefault('PUBLIC', $event)" 
        limitedClick="updateActivitiesVisibilityDefault('LIMITED', $event)" 
        privateClick="updateActivitiesVisibilityDefault('PRIVATE', $event)" 
        elementId="workPrivHelp" /> 
    </div>
</script> 