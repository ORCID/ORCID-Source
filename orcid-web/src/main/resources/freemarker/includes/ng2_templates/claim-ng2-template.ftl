<script type="text/ng-template" id="claim-ng2-template"> 
    <div>
        <div>
            <h4>${springMacroRequestContext.getMessage("claim.almostthere")}</h4>
            <p>${springMacroRequestContext.getMessage("claim.completefields")}</p>
                            
            <div class="control-group">
                <label class="control-label">${springMacroRequestContext.getMessage("claim.password")}</label>
                <div class="relative">
                    <input type="password" name="password" class="input-xlarge" [(ngModel)]="register.password.value" (ngModelChange)="serverValidate('Password')"/>
                    <span class="required" [ngClass]="isValidClass(register.password)">*</span>
                    <div class="popover-help-container" style="display: inline;float: none;">
                        <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                        <div id="name-help" class="popover bottom">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <p>${springMacroRequestContext.getMessage("password_one_time_reset.labelmust8more")}</p>
                                <ul>
                                    <li>${springMacroRequestContext.getMessage("password_one_time_reset.labelatleast09")}</li>
                                    <li>${springMacroRequestContext.getMessage("password_one_time_reset.labelatleast1following")}
                                        <ul>
                                            <li>${springMacroRequestContext.getMessage("password_one_time_reset.labelalphacharacter")}</li>
                                            <li>${springMacroRequestContext.getMessage("password_one_time_reset.labelanyoffollow")}<br /> ! @ # $ % ^ * ( ) ~ `{ } [ ] | \ &amp; _</li>
                                        </ul>
                                    </li>
                                    <li>
                                    ${springMacroRequestContext.getMessage("password_one_time_reset.labeloptionallyspace_1")}<br/>
                                    ${springMacroRequestContext.getMessage("password_one_time_reset.labeloptionallyspace_2")}
                                    </li>
                                </ul>                         
                                <p>${springMacroRequestContext.getMessage("password_one_time_reset.commonpasswords")}<a href="https://github.com/danielmiessler/SecLists/blob/master/Passwords/10_million_password_list_top_1000.txt" target="password_one_time_reset.commonpasswordslink">${springMacroRequestContext.getMessage("password_one_time_reset.commonpasswordslink")}</a></p>
                                <p><strong>${springMacroRequestContext.getMessage("password_one_time_reset.examplesunmoon")}</strong></p>
                            </div>                
                        </div>
                    </div>
                    <span class="orcid-error" *ngIf="register?.password?.errors?.length > 0">
                        <div *nfFor='let error of register.password.errors' [innerHTML]="error"></div>
                    </span>
                </div>
            </div>
            <div>
                <label class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.labelconfirmpassword")}</label>
                <div class="relative">
                    <input type="password" name="confirmPassword" class="input-xlarge" [(ngModel)]="register.passwordConfirm.value" (ngModelChange)="serverValidate('PasswordConfirm')"/>
                    <span class="required" [ngClass]="isValidClass(register.passwordConfirm)">*</span>
                    <span class="orcid-error" *ngIf="register?.passwordConfirm?.errors?.length > 0">
                        <div *ngFor='let error of register.passwordConfirm.errors' [innerHTML]="error"></div>
                    </span>
                </div>
            </div>
            <div class="margin-top-box privacy">
                <label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault")}</label> 
                <label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}</label>
                <@orcid.privacyToggle "register.activitiesVisibilityDefault.visibility" "updateActivitiesVisibilityDefault('PUBLIC', $event)"
                    "updateActivitiesVisibilityDefault('LIMITED', $event)" "updateActivitiesVisibilityDefault('PRIVATE', $event)" /> 
            </div>                    
            <div class="margin-top-box">
                <div class="relative">
                    <label></strong>${springMacroRequestContext.getMessage("claim.notificationemail")}</label>
                    <label class="checkbox">
                        <input type="checkbox" name="sendOrcidChangeNotifications" [(ngModel)]="register.sendChangeNotifications.value"/>
                        ${springMacroRequestContext.getMessage("register.labelsendmenotifications")}
                    </label>                                
                 </div>
            </div>
            <div class="margin-top-box">
                <div class="relative">
                    <label>${springMacroRequestContext.getMessage("register.labelTermsofUse")} <span class="required"  [ngClass]="{'text-error':register.termsOfUse.value == false}">*</span></label>
                    <label class="checkbox">
                        <input type="checkbox" name="acceptTermsAndConditions" [(ngModel)]="register.termsOfUse.value" (ngModelChange)="serverValidate('TermsOfUse')"/>
                        ${springMacroRequestContext.getMessage("register.labelconsent")} <a href="${aboutUri}/footer/privacy-policy" target="register.labelprivacypolicy">${springMacroRequestContext.getMessage("register.labelprivacypolicy")}</a> ${springMacroRequestContext.getMessage("register.labeland")} ${springMacroRequestContext.getMessage("common.termsandconditions1")}<a href="${aboutUri}/content/orcid-terms-use" target="common.termsandconditions2">${springMacroRequestContext.getMessage("common.termsandconditions2")}</a> ${springMacroRequestContext.getMessage("common.termsandconditions3")}</p>
                    </label>
                    <span class="orcid-error" *ngIf="register?.termsOfUse?.errors?.length > 0">
                        <div *ngFor='let error of register.termsOfUse.errors' [innerHTML]="error"></div>
                    </span>
                </div>
            </div>   
            <div class="relative centered-mobile">
                  <button type="submit" class="btn btn-primary" (click)="postClaim()">${springMacroRequestContext.getMessage("claim.btnClaim")}</button>
                  <span *ngIf="postingClaim" >
                    <i class="glyphicon glyphicon-refresh spin x2 green"></i>
                  </span>
            </div>  
        </div> 
    </div>
</script>