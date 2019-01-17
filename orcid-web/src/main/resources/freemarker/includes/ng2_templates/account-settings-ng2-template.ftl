<#include "/includes/ng2_templates/emails-form-ng2-template.ftl">
<script type="text/ng-template" id="account-settings-ng2-template">
    <table class="table table-bordered settings-table account-settings" style="margin:0; padding:0;">
        <tbody>
            <!-- Email and notification preferences -->
            <tr>
                <th><a name="editEmail"></a>
                    ${springMacroRequestContext.getMessage("manage.email_notification_preferences")}
                </th>
                <td><a id="account-settings-toggle-email-edit" (click)="toggleSection('editEmail')">{{toggleText['editEmail']}}</a></td>
            </tr>
            <tr>
                <td colspan="2" *ngIf="showSection['editEmail']" >
                    <emails-form-ng2 popUp="false"></emails-form-ng2>
                </td>
            </tr>
            <!--Language display preferences-->
            <tr>
                <th><a name="editLanguage"></a>${springMacroRequestContext.getMessage("manage.language")}</th>
                <td><a id="" (click)="toggleSection('editLanguage')">{{toggleText['editLanguage']}}</a></td>
            </tr>
            <tr *ngIf="showSection['editLanguage']" >
                <td  colspan="2">
                    <p>${springMacroRequestContext.getMessage("manage.language_copy")}</p>
                    <div class="row">
                        <div class="col-md-12">
                            <language-ng2></language-ng2>
                        </div>
                    </div>
                </td>
            </tr>
            <!--Password-->
            <tr>
                <th><a name="editPassword"></a>${springMacroRequestContext.getMessage("manage.password")}</th>
                <td><a (click)="toggleSection('editPassword')">{{toggleText['editPassword']}}</a></td>
            </tr>
            <tr *ngIf="showSection['editPassword']">
                <td colspan="2">
                    <span class="orcid-error" *ngIf="changePasswordPojo?.errors?.length > 0">
                        <div *ngFor='let error of changePasswordPojo.errors' [innerHTML]="error"></div>
                    </span>
                    <div class="form-group">
                        <label for="passwordField">${springMacroRequestContext.getMessage("change_password.oldpassword")}</label>
                        <br />
                        <input id="passwordField" type="password" name="oldPassword" (keyup.enter)="saveChangePassword()" [(ngModel)]="changePasswordPojo.oldPassword" class="input-xlarge" /> <span class="required">*</span>
                    </div>
                    <div class="form-group">
                        <label for="passwordField">${springMacroRequestContext.getMessage("change_password.newpassword")}</label>
                        <br />
                        <input id="password" type="password" name="password" (keyup.enter)="saveChangePassword()" [(ngModel)]="changePasswordPojo.password" class="input-xlarge" 
                        /> <span class="required">*</span>
                    </div>
                    <div class="form-group">
                        <label for="retypedPassword">${springMacroRequestContext.getMessage("change_password.confirmnewpassword")}</label>
                        <br />
                        <input id="retypedPassword" type="password"name="retypedPassword"[(ngModel)]="changePasswordPojo.retypedPassword" (keyup.enter)="saveChangePassword()" class="input-xlarge"/> <span class="required">*</span>
                    </div>
                    <button id="bottom-submit-password-change" class="btn btn-primary" (click)="saveChangePassword()">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</button>                                   
                    <button class="btn btn-white-no-border cancel-option inner-row" (click)="getChangePassword()" id="bottom-clear-password-changes">${springMacroRequestContext.getMessage("freemarker.btncancel")}</button>
                </td>
            </tr>
            <!-- Privacy preferences -->
            <tr>
                <th><a name="editPrivacyPreferences"></a>${springMacroRequestContext.getMessage("manage.privacy_preferences")}</th>
                <td><a (click)="toggleSection('editPrivacy')" id="privacyPreferencesToggle">{{toggleText['editPrivacy']}}</a></td>
            </tr>
            <tr *ngIf="showSection['editPrivacy']" id="privacyPreferencesSection">
                <td colspan="2">          
                    <div> 
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
                                    <i class="glyphicon glyphicon-question-sign"></i>
                                    <div id="name-help" class="popover bottom">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
                                            <ul class="privacyHelp">
                                                <li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
                                                <li class="limited" style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
                                                <li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
                                            </ul>
                                            <a href="<@orcid.msg 'common.kb_uri_default'/>360006897614" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <span class="orcid-error" *ngIf="errorUpdatingVisibility">
                                ${springMacroRequestContext.getMessage("privacy_preferences.error_updating_visibility")}
                            </span>
                        </div>
                    </div> 
                </td>
            </tr>
            <!--Security question-->
            <tr>
                <th><a name="editSecurityQuestion"></a>${springMacroRequestContext.getMessage("manage.security_question")}</th>
                <td><a (click)="toggleSection('editSecurityQuestion')">{{toggleText['editSecurityQuestion']}}</a></td>
            </tr>
            <tr
                *ngIf="showSection['editSecurityQuestion']">
                <td colspan="2">
                    <div class="editTablePadCell35">
                        <span class="orcid-error" *ngIf="securityQuestionPojo?.errors?.length > 0"> <span
                            *ngFor='let error of securityQuestionPojo.errors' [innerHTML]="error"></span>
                        </span>
                        <div class="control-group">
                            <label for="changeSecurityQuestionForm.securityQuestionAnswer"
                                class="">${springMacroRequestContext.getMessage("manage.security_question")}</label>                                    
                            <div class="relative" >
                                {{initSecurityQuestion([<#list securityQuestions?keys as key>'${securityQuestions[key]?js_string}',</#list>''])}}

                                <select id="securityQuestionId" name="securityQuestionId"
                                    class="input-xlarge"
                                    [(ngModel)]="securityQuestionPojo.securityQuestionId">
                                    >

                                        <option *ngFor="let securityOption of securityQuestions; let i = index" value="{{i}}">{{securityOption}}</option>                                    

                                </select>
                            </div>
                        </div>
                        <div class="control-group">
                            <label for="changeSecurityQuestionForm.securityQuestionAnswer"
                                class="">${springMacroRequestContext.getMessage("manage.securityAnswer")}</label>
                            <div class="relative">
                                <input type="text" id="securityQuestionAnswer"
                                    name="securityQuestionAnswer" class="input-xlarge"
                                    [(ngModel)]="securityQuestionPojo.securityAnswer"
                                    (keyup.enter)="securityQuestionCheckCredentials()" />
                            </div>
                        </div>
                        <#if isPasswordConfirmationRequired>
                            <@orcid.msg 'manage.security_question.not_allowed' />
                            <div style="padding: 20px;" *ngIf="showConfirmationWindow">
                                <h2><@orcid.msg 'check_password_modal.confirm_password' /></h2>
                               <label for="check_password_modal.password" class=""><@orcid.msg 'check_password_modal.password' /></label>
                               <input id="check_password_modal.password" type="password" name="check_password_modal.password" [(ngModel)]="securityQuestionPojo.password" class="input-xlarge"/>
                               <br />
                               <button id="bottom-submit" class="btn btn-primary" (click)="saveChangeSecurityQuestion()"><@orcid.msg 'check_password_modal.submit'/></button>
                            </div>
                        <#else>
                            <div class="control-group">
                                <button id="bottom-submit-security-question"
                                    class="btn btn-primary" (click)="securityQuestionCheckCredentials()"><@orcid.msg 'freemarker.btnsavechanges' /></button>
                                <a id="bottom-reset-security-question" class="cancel-option inner-row" (click)="getSecurityQuestion()"><@orcid.msg 'freemarker.btncancel' /></a>                                    
                            </div>
                        </#if>
                    </div>
                </td>
            </tr>
            <!-- Deactivate Account -->
            <tr>
                <th><a name="editDeactivate"></a>${springMacroRequestContext.getMessage("manage.close_account")}</th>
                <td><a (click)="toggleSection('deactivate')">{{toggleText['deactivate']}}</a></td>
            </tr>
            <tr *ngIf="showSection['deactivate']" >
                <td colspan="2">
                    <div class="editTablePadCell35 close-account-container">
                        <p>${springMacroRequestContext.getMessage("deactivate_orcid.gdpr_you_may")}</p>
                        
                        <h4>${springMacroRequestContext.getMessage("deactivate_orcid.whatHappens")}</h4>
                        <p>
                            ${springMacroRequestContext.getMessage("deactivate_orcid.gdpr_once")} <a
                                href="<@orcid.msg 'common.kb_uri_default'/>360006973813"
                                target="deactivate_orcid.close_an">${springMacroRequestContext.getMessage("deactivate_orcid.gdpr_learn_more")}</a>
                        </p>
                        
                        <h4>${springMacroRequestContext.getMessage("deactivate_orcid.anotherAccount")}</h4>
                        <p>
                            ${springMacroRequestContext.getMessage("deactivate_orcid.gdpr_if_you_have")}&nbsp;<strong>${springMacroRequestContext.getMessage("deactivate_orcid.duplicate_orcid.b")}</strong>
                            <a
                                href="<@orcid.msg 'common.kb_uri_default'/>360006896634"
                                target="deprecate_orcid.learn_more_link" class="no-wrap">${springMacroRequestContext.getMessage("deprecate_orcid.learn_more_link")}</a>
                        </p>
                        <h4>${springMacroRequestContext.getMessage("deactivate_orcid.listTitle")}</h4>
                        <ol>
                            <li>${springMacroRequestContext.getMessage("deactivate_orcid.b1")}</li>
                            <li>${springMacroRequestContext.getMessage("deactivate_orcid.b2")}</li>
                            <li>${springMacroRequestContext.getMessage("deactivate_orcid.b3")}</li>
                        </ol>
                        <span class="orcid-error" *ngIf="showSendDeactivateEmailSuccess">
                            ${springMacroRequestContext.getMessage("manage.deactivateSend")} {{primaryEmail}}
                        </span>
                        <button (click)="sendDeactivateEmail()" class="btn btn-primary">${springMacroRequestContext.getMessage("deactivate_orcid.deactivatemyOrcidaccount")}</button>
                    </div>
                </td>
            </tr>
            <!-- Remove duplicate record -->
            <tr>
                <th><a name="editDeprecate"></a>${springMacroRequestContext.getMessage("manage.removeDuplicate")}</th>
                <td><a (click)="toggleSection('deprecate')">{{toggleText['deprecate']}}</a></td>
            </tr>
            <tr *ngIf="showSection['deprecate']" >
                <td colspan="2">
                    <div class="editTablePadCell35 close-account-container">
                        <p>${springMacroRequestContext.getMessage("deprecate_orcid.if_you_have")}</p>
                        <p>${springMacroRequestContext.getMessage("deprecate_orcid.information_in")}</p>
                        <p>${springMacroRequestContext.getMessage("deprecate_orcid.if_you_have_more")}<br />
                            <a href="<@orcid.msg 'common.kb_uri_default'/>360006896634" target="deprecate_orcid.learn_more_link">${springMacroRequestContext.getMessage("deprecate_orcid.learn_more_link")}</a>
                        </p>
                        <div>
                            <label for="emailOrId" class="">${springMacroRequestContext.getMessage("deprecate_orcid.email_or_id")}</label>
                            <div class="relative">
                                <input id="emailOrId" type="text" name="emailOrId" (keyup.enter)="deprecateORCID()" 
                                    [(ngModel)]="deprecateProfilePojo.deprecatingOrcidOrEmail" class="input-xlarge" />
                                <span class="required">*</span>
                            </div>
                        </div>
                        <div>
                            <label for="password" class="">${springMacroRequestContext.getMessage("deprecate_orcid.password")}</label>
                            <div class="relative">
                                <input id="password" type="password"
                                    name="password"
                                    [(ngModel)]="deprecateProfilePojo.deprecatingPassword" (keyup.enter)="deprecateORCID()" 
                                    class="input-xlarge" /> <span class="required">*</span>
                            </div>
                        </div>
                       <span class="orcid-error"
                            *ngIf="deprecateProfilePojo?.errors?.length > 0">
                            <div *ngFor='let error of deprecateProfilePojo.errors'
                                [innerHTML]="error"></div>
                        </span>
                        <button (click)="deprecateORCID()" class="btn btn-primary">${springMacroRequestContext.getMessage("deprecate_orcid.remove_record")}</button>
                    </div>
                    <#include "/includes/ng2_templates/deprecate-account-modal-ng2-template.ftl"> 
                    <#include "/includes/ng2_templates/deprecate-account-success-modal-ng2-template.ftl"> 
                </td>
            </tr>
            <!--Two-factor authentication-->
            <tr>
                <th><a name="edit2FA"></a>${springMacroRequestContext.getMessage("manage.2FA")}</th>
                <td><a (click)="toggleSection('twoFA')">{{toggleText['twoFA']}}</a></td>
            </tr>
            <tr *ngIf="showSection['twoFA']" >
                <td colspan="2">
                    <p>
                        ${springMacroRequestContext.getMessage("2FA.details")}
                        <br />
                        <a href="<@orcid.msg 'common.kb_uri_default'/>360006971673"
                            target="2FA.learn_more_link">${springMacroRequestContext.getMessage("2FA.learn_more_link")}</a>
                    </p>
                    <div *ngIf="showEnabled2FA" >
                        <span class="on">${springMacroRequestContext.getMessage("2FA.state.on.heading")} <span class="glyphicon glyphicon-ok"></span></span>
                        <span class="small bold leftBuffer">${springMacroRequestContext.getMessage("2FA.state.on.description")}</span>
                        <a class="leftBuffer" id="disable2FA" (click)="disable2FA()" href="#">${springMacroRequestContext.getMessage("2FA.disable")}</a>
                    </div>
                    <div *ngIf="showDisabled2FA" >
                        <span class="off">${springMacroRequestContext.getMessage("2FA.state.off.heading")} <span class="glyphicon glyphicon-remove"></span></span>
                        <span class="small bold leftBuffer">${springMacroRequestContext.getMessage("2FA.state.off.description")}</span>
                        <button (click)="enable2FA()" class="btn btn-primary leftBuffer">${springMacroRequestContext.getMessage("2FA.enable")}</button>
                    </div>
                </td>
            </tr>
            <tr>
                <th><a name="getMyData"></a>${springMacroRequestContext.getMessage("manage.get_my_data")}</th>
                <td><a (click)="toggleSection('getMyData')">{{toggleText['getMyData']}}</a></td>
            </tr>
            <!--Download all my data-->
            <tr *ngIf="showSection['getMyData']" id="get-my-data">
                <td colspan="2">
                    <p>
                        <@orcid.msg 'manage.get_my_data.details.p1' />
                    </p>
                    <p>
                        <@orcid.msg 'manage.get_my_data.details.p2' /> <a href="<@orcid.msg 'common.kb_uri_default'/>360006897634"><@orcid.msg 'common.learn_more' /></a>
                    </p>
                    <form id="getMyDataForm" action="${baseUri}/get-my-data" method="POST">
                        <button class="btn btn-primary">
                            Test <@orcid.msg 'manage.get_my_data.button' />
                        </button>
                    </form>
                </td>
            </tr>
        </tbody>
    </table>
</script>