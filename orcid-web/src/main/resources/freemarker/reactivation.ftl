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
<@public>
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
                                    <a href="http://support.orcid.org/knowledgebase/articles/142948-names-in-the-orcid-registry" target="_blank"><@orcid.msg 'orcid.frontend.register.help.more_info.link.text'/></a>
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
                <div>
                    <div class="relative"  style="margin-bottom: 15px;">
                        <label class="dark-label">${springMacroRequestContext.getMessage("register.labelTermsofUse")} <span class="required"  ng-class="{'text-error':register.termsOfUse.value == false}">*</span></label>
                        <div class="checkbox">
                            <label class="checkbox dark-label">            
                                <input type="checkbox" tabindex="9" name="acceptTermsAndConditions" ng-model="register.termsOfUse.value" ng-change="serverValidate('TermsOfUse')" />
                                ${springMacroRequestContext.getMessage("register.labelconsent")} <a href="${aboutUri}/footer/privacy-policy" target="_blank">${springMacroRequestContext.getMessage("register.labelprivacypolicy")}</a> ${springMacroRequestContext.getMessage("register.labeland")}  ${springMacroRequestContext.getMessage("common.termsandconditions1")}<a href="${aboutUri}/content/orcid-terms-use" target="_blank">${springMacroRequestContext.getMessage("common.termsandconditions2")}</a> ${springMacroRequestContext.getMessage("common.termsandconditions3")}
                            </label>
                    </div>
                    <span class="orcid-error" ng-show="register.termsOfUse.errors.length > 0">
                        <div ng-repeat='error in register.termsOfUse.errors' ng-bind-html="error"></div>
                    </span>
                </div>
				<div class="relative">
                    <button type="submit" tabindex="10" class="btn btn-primary" ng-click="postReactivationConfirm(null)">${springMacroRequestContext.getMessage("orcid.frontend.reactivate")}</button>
                </div>
			</fn-form>
		</div>
	</div>
</div>
</@public>


