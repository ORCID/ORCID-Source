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