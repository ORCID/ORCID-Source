<@public nav="admin_actions">
    <div class="row">
        <div class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-9 col-xs-12">
            <h2><@orcid.msg 'claim.claimyourrecord' /></h2>
            <h4><@orcid.msg 'claim.almostthere' /></h4>
            <p><@orcid.msg 'claim.completefields' /></p>                           
            <script type="text/ng-template" id="claim-ng2-template">
                <div id="claim" class="oauth-registration">
                    <!--Password-->
                    <div class="form-group clear-fix">
                        <label class="control-label"><@orcid.msg 'claim.password' /></label>
                        <div class="bottomBuffer">
                            <input type="password" name="password" class="input-xlarge" [(ngModel)]="claim.password.value" (ngModelChange)="serverValidate('Password')"/>
                            <span class="required" [ngClass]="isValidClass(claim.password)">*</span>
                            <@orcid.passwordHelpPopup />
                            <span class="orcid-error" *ngIf="claim.password?.errors?.length > 0">
                                <div *ngFor="let error of claim.password.errors" [innerHTML]="error"></div>
                            </span>   
                        </div>
                    </div>
                    <!--Confirm password-->
                    <div class="form-group clear-fix">
                        <label class="control-label"><@orcid.msg 'password_one_time_reset.labelconfirmpassword'/></label>
                        <div class="bottomBuffer">
                            <input type="password" name="confirmPassword" class="input-xlarge" [(ngModel)]="claim.passwordConfirm.value" (ngModelChange)="serverValidate('PasswordConfirm')"/>
                            <span class="required" [ngClass]="isValidClass(claim.passwordConfirm)">*</span>
                            <span class="orcid-error" *ngIf="claim.passwordConfirm.errors.length > 0">
                                <div *ngFor="let error of claim.passwordConfirm.errors" [innerHTML]="error"></div>
                            </span>
                        </div>
                    </div>

                    <!--Visibility default-->
                    <div class="form-group clear-fix popover-registry">  
                        <h4>${springMacroRequestContext.getMessage("register.privacy_settings")}</h4>         
                        <p>${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault")}</p> 
                        <p><b>${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}</b></p>
                        <div class="visibilityDefault">
                            <div class="radio">
                              <label><input type="radio" name="defaultVisibility" [(ngModel)]="claim.activitiesVisibilityDefault.visibility" value="PUBLIC"><span class="public"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lipublic'/></b> <@orcid.msg 'register.privacy_everyone_text'/></span></label>
                            </div>
                            <div class="radio">
                              <label><input type="radio" name="defaultVisibility" [(ngModel)]="claim.activitiesVisibilityDefault.visibility" value="LIMITED"><span class="limited"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lilimited'/></b> <@orcid.msg 'register.privacy_limited_text'/></span></label>
                            </div>
                            <div class="radio">
                              <label><input type="radio" name="defaultVisibility" [(ngModel)]="claim.activitiesVisibilityDefault.visibility" value="PRIVATE"><span class="private"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.liprivate'/></b> <@orcid.msg 'register.privacy_private_text'/></span></label>
                            </div>
                        </div>
                        <div class="visibilityHelp">
                            <span class="required" [ngClass]="isValidClass(registrationForm.activitiesDefaultVisibility)">*</span>
                            <div class="popover-help-container">
                                <i class="glyphicon glyphicon-question-sign"></i>
                                <div id="name-help" class="popover bottom">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
                                        <ul class="privacyHelp">
                                            <li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
                                            <li class="limited"style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
                                            <li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
                                        </ul>
                                        <a href="<@orcid.msg 'common.kb_uri_default'/>360006897614" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <span class="orcid-error" *ngIf="claim.activitiesVisibilityDefault.errors.length > 0">
                            <div *ngFor="let error of claim.activitiesVisibilityDefault.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                    <!--Notifications settings -->
                    <div id="notificationSettings" class="form-group clear-fix">  
                        <h4 class="dark-label"><@orcid.msg 'register.label.notification_settings' /></h4>                
                        <p><@orcid.msg 'register.paragraph.1' /></p>
                        <p><@orcid.msg 'register.paragraph.2' /></p>
                        <div class="control-group">
                            <input id="send-orcid-news" type="checkbox" name="sendOrcidNews" tabindex="9" [(ngModel)]="claim.sendOrcidNews.value" />
                            <label for="send-orcid-news"><@orcid.msg 'manage.email.email_frequency.notifications.news.checkbox.label' /></label>
                            <p class="italic"><@orcid.msg 'register.paragraph.3' /></p>
                        </div>
                        <p><@orcid.msg 'register.paragraph.4' /></p>
                    </div>
                    <!--Terms and conditions-->
                    <div class="clear-fix bottomBuffer">
                        <h4><@orcid.msg 'register.labelTermsofUse'/>
                            <span class="required" [ngClass]="{'text-error':registrationForm.termsOfUse.value == false}"></span></h4>  
                        <p>
                            <input id="register-form-term-box" type="checkbox" name="termsConditions" [(ngModel)]="claim.termsOfUse.value" tabindex="9" name="acceptTermsAndConditions" (ngModelChange)="serverValidate('TermsOfUse')" />
                            <@orcid.msg 'register.labelconsent'/> <a href="${aboutUri}/footer/privacy-policy" target="register.labelprivacypolicy"><@orcid.msg 'register.labelprivacypolicy'/></a>&nbsp;<@orcid.msg 'register.labeland'/>&nbsp;<@orcid.msg 'common.termsandconditions1'/><a href="${aboutUri}/content/orcid-terms-use" target="common.termsandconditions2"><@orcid.msg 'common.termsandconditions2'/></a>&nbsp;<@orcid.msg 'common.termsandconditions3'/>
                        </p>
                        <span class="orcid-error" *ngIf="claim.termsOfUse.errors.length > 0">
                            <div *ngFor="let error of claim.termsOfUse.errors" [innerHTML]="error"></div>
                        </span>
                    </div>

                    <!-- Buttons  -->
                    <div class="bottomBuffer col-xs-12 col-sm-3">
                        <button type="submit" class="btn btn-primary" (click)="postClaim()"><@orcid.msg 'claim.btnClaim' /></button>
                        <span *ngIf="postingClaim">
                            <i class="glyphicon glyphicon-refresh spin x2 green"></i>
                        </span>
                    </div>
                </div>                        
            </script>
            <claim-ng2></claim-ng2>
        </div>
    </div>
</@public>