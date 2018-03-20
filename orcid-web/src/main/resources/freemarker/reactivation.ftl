<@public>
<@orcid.checkFeatureStatus featureName='ANGULAR2_QA'> 
    <#include "/includes/ng2_templates/reactivation-ng2-template.ftl">
    <div class="row">
        <reactivation-ng2></reactivation-ng2>
    </div>
</@orcid.checkFeatureStatus>
<@orcid.checkFeatureStatus featureName='ANGULAR1_LEGACY' enabled=false>
    <div class="row">
    	<div class ="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12">
    	    <p>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation.thank_you")}</p>
    	    <p>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation.please_complete")}</p>
    		<div ng-controller="ReactivationCtrl" ng-init="getReactivation('${resetParams}', '')">
    			<fn-form update-fn="postRegister()">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div>
                        <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelfirstname")}
                        </label>
                        <div class="relative">
                            <input name="givenNames234" type="text" tabindex="1" class="input-xlarge" ng-model="register.givenNames.value" ng-model-onblur ng-change="serverValidate('GivenNames')"/>
                            <span class="required" ng-class="isValidClass(register.givenNames)">*</span>
                            <div class="popover-help-container">
                                <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                                <div id="name-help" class="popover bottom">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <p><@orcid.msg ''/></p>
                                        <p><@orcid.msg 'orcid.frontend.register.help.last_name'/></p>
                                        <p><@orcid.msg 'orcid.frontend.register.help.update_names'/></p>
                                        <a href="${knowledgeBaseUri}/articles/142948-names-in-the-orcid-registry" target="orcid.frontend.register.help.more_info.link.text"><@orcid.msg 'orcid.frontend.register.help.more_info.link.text'/></a>
                                    </div>
                                </div>
                            </div>
                            <span class="orcid-error" ng-show="register.givenNames.errors.length > 0">
                                <div ng-repeat='error in register.givenNames.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <div>
                        <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labellastname")}</label>
                        <div class="relative">
                            <input name="familyNames234" type="text" tabindex="2" class="input-xlarge"  ng-model="register.familyNames.value" ng-model-onblur/>
                            <span class="orcid-error" ng-show="register.familyNames.errors.length > 0">
                                <div ng-repeat='error in register.familyNames.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
    	    		<div class="control-group">
                        <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelpassword")}</label>
                        <div class="relative">
                            <input type="password" name="password" tabindex="5" class="input-xlarge" ng-model="register.password.value" ng-change="serverValidate('Password')"/>
                            <span class="required" ng-class="isValidClass(register.password)">*</span>
                            <@orcid.passwordHelpPopup />
                            <span class="orcid-error" ng-show="register.password.errors.length > 0">
                                <div ng-repeat='error in register.password.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <div>
                        <label class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.labelconfirmpassword")}</label>
                        <div class="relative">
                            <input type="password" name="confirmPassword" tabindex="6" class="input-xlarge" ng-model="register.passwordConfirm.value" ng-change="serverValidate('PasswordConfirm')"/>
                            <span class="required" ng-class="isValidClass(register.passwordConfirm)">*</span>
                            <span class="orcid-error" ng-show="register.passwordConfirm.errors.length > 0">
                                <div ng-repeat='error in register.passwordConfirm.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <@orcid.checkFeatureStatus featureName='GDPR_UI'> 
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
                                    <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                                    <div id="name-help" class="popover bottom">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
                                            <ul class="privacyHelp">
                                                <li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
                                                <li class="limited"style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
                                                <li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
                                            </ul>
                                            <a href="${knowledgeBaseUri}/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <span class="orcid-error" ng-show="register.activitiesVisibilityDefault.errors.length > 0">
                            <div ng-repeat='error in register.activitiesVisibilityDefault.errors' ng-bind-html="error"></div>
                        </span>
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
                    </@orcid.checkFeatureStatus>
                    <@orcid.checkFeatureStatus featureName='GDPR_UI' enabled=false>  
                    <div style="margin-bottom: 20px; margin-top: 10px;">
                        <label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault")}</label>
                        <label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}</label>
                        <@orcid.privacyToggle
                            angularModel="register.activitiesVisibilityDefault.visibility"
                            questionClick="toggleClickPrivacyHelp('workPrivHelp')"
                            clickedClassCheck="{'popover-help-container-show':privacyHelp['workPrivHelp']==true}"
                            publicClick="updateActivitiesVisibilityDefault('PUBLIC', $event)"
                            limitedClick="updateActivitiesVisibilityDefault('LIMITED', $event)"
                            privateClick="updateActivitiesVisibilityDefault('PRIVATE', $event)" />
                    </div>
                    <div>
                        <div class="relative"  style="margin-bottom: 15px;">
                            <label class="dark-label">${springMacroRequestContext.getMessage("register.labelTermsofUse")} <span class="required"  ng-class="{'text-error':register.termsOfUse.value == false}">*</span></label>
                            <div class="checkbox">
                                <label class="checkbox dark-label">            
                                    <input type="checkbox" tabindex="9" name="acceptTermsAndConditions" ng-model="register.termsOfUse.value" ng-change="serverValidate('TermsOfUse')" />
                                    ${springMacroRequestContext.getMessage("register.labelconsent")} <a href="${aboutUri}/footer/privacy-policy" target="register.labelprivacypolicy">${springMacroRequestContext.getMessage("register.labelprivacypolicy")}</a> ${springMacroRequestContext.getMessage("register.labeland")}  ${springMacroRequestContext.getMessage("common.termsandconditions1")}<a href="${aboutUri}/content/orcid-terms-use" target="common.termsandconditions2">${springMacroRequestContext.getMessage("common.termsandconditions2")}</a> ${springMacroRequestContext.getMessage("common.termsandconditions3")}
                                </label>
                        </div>
                        <span class="orcid-error" ng-show="register.termsOfUse.errors.length > 0">
                            <div ng-repeat='error in register.termsOfUse.errors' ng-bind-html="error"></div>
                        </span>
                    </div>
                    </@orcid.checkFeatureStatus>
    				<div class="relative">
                        <button type="submit" tabindex="10" class="btn btn-primary" ng-click="postReactivationConfirm(null)">${springMacroRequestContext.getMessage("orcid.frontend.reactivate")}</button>
                    </div>
    			</fn-form>
    		</div>
    	</div>
    </div>
</@orcid.checkFeatureStatus>
</@public>


