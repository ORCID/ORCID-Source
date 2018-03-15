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
    <div *ngIf="gdprUiFeatureEnabled"> 
        <div class="editTablePadCell35" id="privacy-settings">  
            ${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}
            <br>
            <div class="visibilityDefault">
                <div class="radio">
                  <label><input type="radio" name="defaultVisibility" [ngModel]="prefs['default_visibility']" value="PUBLIC" (change)="updateActivitiesVisibilityDefault(prefs['default_visibility'], 'PUBLIC', $event)"><span class="public"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lipublic'/></b> <@orcid.msg 'register.privacy_everyone_text'/></span></label>
                </div>
                <div class="radio">
                  <label><input type="radio" name="defaultVisibility" [ngModel]="prefs['default_visibility']" value="LIMITED" (change)="updateActivitiesVisibilityDefault(prefs['default_visibility'], 'LIMITED', $event)"><span class="limited"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lilimited'/></b> <@orcid.msg 'register.privacy_limited_text'/></span></label>
                </div>
                <div class="radio">
                  <label><input type="radio" name="defaultVisibility" [ngModel]="prefs['default_visibility']" value="PRIVATE" (change)="updateActivitiesVisibilityDefault(prefs['default_visibility'], 'PRIVATE', $event)"><span class="private"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.liprivate'/></b> <@orcid.msg 'register.privacy_private_text'/></span></label>
                </div>
            </div>
            <div class="visibilityHelp">
                <div class="popover-help-container">
                    <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                    <div id="name-help" class="popover bottom">
                        <div class="arrow"></div>
                        <div class="popover-content">
                            <strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
                            <ul class="privacyHelp">
                                <li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
                                <li class="limited" style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
                                <li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
                            </ul>
                            <a href="${knowledgeBaseUri}/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
                        </div>
                    </div>
                </div>
            </div>
            <span class="orcid-error" *ngIf="errorUpdatingVisibility">
                ${springMacroRequestContext.getMessage("privacy_preferences.error_updating_visibility")}
            </span>
        </div>
    </div>
    <div *ngIf="!gdprUiFeatureEnabled">
        <div class="editTablePadCell35" id="privacy-settings">
            ${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}
            <br>
            <@orcid.privacyToggle3Ng2
            angularModel="this.prefs['default_visibility']"
            publicClick="updateActivitiesVisibilityDefault('PUBLIC', $event)" 
            limitedClick="updateActivitiesVisibilityDefault('LIMITED', $event)" 
            privateClick="updateActivitiesVisibilityDefault('PRIVATE', $event)" 
            elementId="workPrivHelp" /> 
        </div>
    </div>
</script> 