<@protected>
<div id="claim">
   <div class="row">        
        <div class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-9 col-xs-12"> 
        <h2>${springMacroRequestContext.getMessage("claim.claimyourrecord")}</h2>
                <div ng-controller="ClaimCtrl">
                    <div>
                        <!-- span class="orcid-error" ng-show="register.errors.length > 0">
                            <div ng-repeat='error in register.errors' ng-bind-html="error"></div>
                        </span -->
                        <h4>${springMacroRequestContext.getMessage("claim.almostthere")}</h4>
                        <p>${springMacroRequestContext.getMessage("claim.completefields")}</p>
                                        
                        <div class="control-group">
                            <label class="control-label">${springMacroRequestContext.getMessage("claim.password")}</label>
                            <div class="relative">
                                <input type="password" name="password" class="input-xlarge" ng-model="register.password.value" ng-change="serverValidate('Password')"/>
                                <span class="required" ng-class="isValidClass(register.password)">*</span>
                                <div class="popover-help-container" style="display: inline;float: none;">
                                    <i class="glyphicon glyphicon-question-sign"></i>
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
                                            <p>${springMacroRequestContext.getMessage("password_one_time_reset.commonpasswords")}<a href="https://github.com/danielmiessler/SecLists/blob/master/Passwords/Common-Credentials/10-million-password-list-top-1000.txt" target="password_one_time_reset.commonpasswordslink">${springMacroRequestContext.getMessage("password_one_time_reset.commonpasswordslink")}</a></p>
                                            <p><strong>${springMacroRequestContext.getMessage("password_one_time_reset.examplesunmoon")}</strong></p>
                                        </div>                
                                    </div>
                                </div>
                                <span class="orcid-error" ng-show="register.password.errors.length > 0">
                                    <div ng-repeat='error in register.password.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>
                        <div>
                            <label class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.labelconfirmpassword")}</label>
                            <div class="relative">
                                <input type="password" name="confirmPassword" class="input-xlarge" ng-model="register.passwordConfirm.value" ng-change="serverValidate('PasswordConfirm')"/>
                                <span class="required" ng-class="isValidClass(register.passwordConfirm)">*</span>
                                <span class="orcid-error" ng-show="register.passwordConfirm.errors.length > 0">
                                    <div ng-repeat='error in register.passwordConfirm.errors' ng-bind-html="error"></div>
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
                                  <label><input type="radio" name="defaultVisibility" ng-model="register.activitiesVisibilityDefault.visibility" value="PUBLIC" ng-change="serverValidate('ActivitiesVisibilityDefault')"><span class="public"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lipublic'/></b> <@orcid.msg 'register.privacy_everyone_text'/></span></label>
                                </div>
                                <div class="radio">
                                  <label><input type="radio" name="defaultVisibility" ng-model="register.activitiesVisibilityDefault.visibility" value="LIMITED" ng-change="serverValidate('ActivitiesVisibilityDefault')"><span class="limited"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lilimited'/></b> <@orcid.msg 'register.privacy_limited_text'/></span></label>
                                </div>
                                <div class="radio">
                                  <label><input type="radio" name="defaultVisibility" ng-model="register.activitiesVisibilityDefault.visibility" value="PRIVATE" ng-change="serverValidate('ActivitiesVisibilityDefault')"><span class="private"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.liprivate'/></b> <@orcid.msg 'register.privacy_private_text'/></span></label>
                                </div>
                            </div>
                            <div class="visibilityHelp">
                                <span class="required" ng-class="isValidClass(register.activitiesDefaultVisibility)">*</span>
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
                            <span class="orcid-error" ng-show="register.activitiesVisibilityDefault.errors.length > 0">
                            <div ng-repeat='error in register.activitiesVisibilityDefault.errors' ng-bind-html="error"></div>
                        </span>
                        </div>
                         <div>
                            <!--Notifications settings -->
                            <div id="notificationSettings" class="form-group clear-fix">  
                                <h4 class="dark-label"><@orcid.msg 'register.label.notification_settings' /></h4>                
                                <p><@orcid.msg 'register.paragraph.1' /></p>
                                <p><@orcid.msg 'register.paragraph.2' /></p>
                                <div class="control-group">
                                    <input id="send-orcid-news" type="checkbox" name="sendOrcidNews" tabindex="9" ng-model="register.sendOrcidNews.value" />
                                    <label for="send-orcid-news"><@orcid.msg 'manage.email.email_frequency.notifications.news.checkbox.label' /></label>
                                </div>
                                <p><@orcid.msg 'register.paragraph.3' /></p>
                            </div>
                        </div>
                        
                        <!--Terms and conditions-->
                        <div class="form-group clear-fix bottomBuffer">
                            <h4><@orcid.msg 'register.labelTermsofUse'/>
                                <span class="required"  ng-class="{'text-error':register.termsOfUse.value == false}"></span></h4>  
                            <p>
                                <input id="register-form-term-box" type="checkbox" name="termsConditions" tabindex="9" name="acceptTermsAndConditions" ng-model="register.termsOfUse.value" ng-change="serverValidate('TermsOfUse')" />
                                <@orcid.msg 'register.labelconsent'/> <a href="${aboutUri}/footer/privacy-policy" target="register.labelprivacypolicy"><@orcid.msg 'register.labelprivacypolicy'/></a>&nbsp;<@orcid.msg 'register.labeland'/>&nbsp;<@orcid.msg 'common.termsandconditions1'/><a href="${aboutUri}/content/orcid-terms-use" target="common.termsandconditions2"><@orcid.msg 'common.termsandconditions2'/></a>&nbsp;<@orcid.msg 'common.termsandconditions3'/>
                            </p>
                            <span class="orcid-error" ng-show="register.termsOfUse.errors.length > 0">
                                <div ng-repeat='error in register.termsOfUse.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                        <div class="relative centered-mobile">
                              <button type="submit" class="btn btn-primary" ng-click="postClaim()">${springMacroRequestContext.getMessage("claim.btnClaim")}</button>
                              <span ng-show="postingClaim" ng-cloak>
                                <i class="glyphicon glyphicon-refresh spin x2 green"></i>
                              </span>
                        </div>  
                    </div> 
                </div>
        </div>
    </div>
</div>
</@protected>
