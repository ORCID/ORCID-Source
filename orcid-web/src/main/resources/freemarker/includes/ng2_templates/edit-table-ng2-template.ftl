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

<script type="text/ng-template" id="edit-table-ng2-template">
    <table class="table table-bordered settings-table account-settings"
        style="margin:0; padding:0;">
        <tbody>
            <tr>
                <!-- Email header -->
                <th><a name="editEmail"></a>${springMacroRequestContext.getMessage("manage.thEmail")}</th>
                <td><a href="" id="account-settings-toggle-email-edit" (click)="toggleEmailEdit()">{{emailToggleText}}</a></td>
            </tr>
            
            <tr>
                <td colspan="2" ng-show="showEditEmail" ng-cloak>
                    <emails-form-ng2 popUp="false"></emails-form-ng2>                                            
                </td>
            </tr>
            
            <tr>
                <th><a name="editLanguage"></a>${springMacroRequestContext.getMessage("manage.language")}</th>
                <td><a href="" id="" (click)="toggleLanguageEdit()">{{languageToggleText}}</a></td>
            </tr>
            

            <tr *ngIf="showEditLanguage">
                <td colspan="2">
                    <language-ng2></language-ng2>
                </td>
            </tr>
            
            <!-- Notifications -->
            <tr>
                <th><a name="editEmailPreferences"></a>${springMacroRequestContext.getMessage("manage.notification_preferences")}</th>
                <td><a href="" (click)="toggleEmailPreferencesEdit()">{{emailPreferencesToggleText}}</a></td>
            </tr>
            <tr
                *ngIf="showEditEmailPreferences">
                <td colspan="2">
                    <notification-preference-ng2></notification-preference-ng2>
                </td>
            </tr>
            <tr>
                <th><a name="editPassword"></a>${springMacroRequestContext.getMessage("manage.password")}</th>
                <td><a href="" (click)="togglePasswordEdit()">{{passwordToggleText}}</a></td>
            </tr>
            <tr *ngIf="showEditPassword">
                <td colspan="2" class="reset" id="password-edit">
                    <password-edit-ng2></password-edit-ng2>                        
                </td>
            </tr>
            
            
            <!-- Privacy preferences -->
            <tr>
                <th><a name="editPrivacyPreferences"></a>${springMacroRequestContext.getMessage("manage.privacy_preferences")}</th>
                <td><a href="" (click)="togglePrivacyPreferencesEdit()" id="privacyPreferencesToggle">{{privacyPreferencesToggleText}}</a></td>
            </tr>

            <tr *ngIf="showEditPrivacyPreferences" id="privacyPreferencesSection">
                <td colspan="2">
                    <works-privacy-preferences-ng2></works-privacy-preferences-ng2>
                </td>
            </tr>
            <tr>
                <th><a name="editSecurityQuestion"></a>${springMacroRequestContext.getMessage("manage.security_question")}</th>
                <td><a href="" (click)="toggleSecurityQuestionEdit()">{{securityQuestionToggleText}}</a></td>
            </tr>
            <tr *ngIf="showEditSecurityQuestion" >
                <td colspan="2">
                    <security-question-edit-ng2></security-question-edit-ng2>
                </td>
            </tr>
            <!-- Deactivate Account -->
            <tr>
                <th><a name="editDeactivate"></a>${springMacroRequestContext.getMessage("manage.close_account")}</th>>{{deactivateToggleText}}</a></td>
            </tr>
            <tr *ngIf="showEditDeactivate" >
                <td colspan="2">
                    <deactivate-account-ng2></deactivate-account-ng2>
                </td>
            </tr>
            <!-- / Deactivate Account -->
            <!-- Deprecate duplicate account -->
            <tr>
                <th><a name="editDeprecate"></a>${springMacroRequestContext.getMessage("manage.removeDuplicate")}</th>
                <td><a href="" (click)="toggleDeprecateEdit()">{{deprecateToggleText}}</a></td>
            </tr>
            <tr *ngIf="showEditDeprecate" >
                <td colspan="2">
                    <deprecate-account-ng2></deprecate-account-ng2>
                </td>
            </tr>
            <@orcid.checkFeatureStatus 'TWO_FACTOR_AUTHENTICATION'>
                <tr>
                    <th><a name="edit2FA"></a>${springMacroRequestContext.getMessage("manage.2FA")}</th>
                    <td><a href="" (click)="toggle2FAEdit()">{{twoFAToggleText}}</a></td>
                </tr>
                <tr *ngIf="showEdit2FA" >
                    <td colspan="2">
                        <two-fa-state-ng2></two-fa-state-ng2>
                    </td>
                </tr>
            </@orcid.checkFeatureStatus>
            <@orcid.checkFeatureStatus 'GET_MY_DATA'>
                <tr>
                    <th><a name="getMyData"></a>${springMacroRequestContext.getMessage("manage.get_my_data")}</th>
                    <td><a href="" (click)="toggleGetMyDataEdit()">{{twoFAToggleText}}</a></td>
                </tr>
                <tr *ngIf="showEditGetMyData" >
                    <td colspan="2">
                        <p>
                            <@orcid.msg 'manage.get_my_data.details.p1' />
                        </p>
                        <p>
                            <@orcid.msg 'manage.get_my_data.details.p2' /> <a href="https://support.orcid.org/knowledgebase/articles/117225"><@orcid.msg 'common.learn_more' /></a>
                        </p>
                        <p>
                            <form action="${baseUri}/get-my-data" method="POST">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button class="btn btn-primary">
                                    <@orcid.msg 'manage.get_my_data.button' />
                                </button>
                            </form>
                        </p>
                    </td>
                </tr>
            </td>
                </tr>
            </@orcid.checkFeatureStatus>
            <#if RequestParameters['OrcidSocial']??>
                <tr>
                    <th><a name="editSocialNetworks"></a>${springMacroRequestContext.getMessage("manage.social_networks")}</th>
                    <td><a href="" (click)="toggleSocialNetworksEdit()">{{socialNetworksToggleText}}</a></td>
                </tr>
                <tr *ngIf="showEditSocialSettings"  id="social-networks">
                    <td colspan="2">
                        <social-networks-ng2></social-networks-ng2>
                    </td>
                </tr>
            </#if>
        </tbody>
    </table>
</script>