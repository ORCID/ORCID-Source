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
<div ng-controller="RequestPasswordResetCtrl" id="RequestPasswordResetCtr" ng-init="getRequestResetPassword()" class="reset-password">
	<a name="resetPassword"></a>
	<a href="" id="reset-password-toggle-text" ng-click="toggleResetPassword()" ng-bind="resetPasswordToggleText"></a>
	<div ng-show="showResetPassword" ng-cloak>
	 	<p>
	 	     <small>
	 	         ${springMacroRequestContext.getMessage("reset_password.enterEmail_1")}
             </small>
        </p>
        <p>
            <small>
                ${springMacroRequestContext.getMessage("reset_password.enterEmail_3")}
            </small>
        </p>
        <p>
            <small>
                 ${springMacroRequestContext.getMessage("reset_password.enterEmail_2")}
                 <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>
            </small>
	 	</p>
		<form id="password-reset-form" name="emailAddressForm">
		    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<div class="control-group">
				<label for="email" class="control-label">${springMacroRequestContext.getMessage("manage_bio_settings.h3email")} </label>			           
				<div class="controls"> 
					<input id="email" type="text" class="form-control" ng-model="requestResetPassword.email" />
				</div>
                <!--Success msg-->
                <div class="alert alert-success" ng-show="requestResetPassword.successMessage != null">
                    <strong><span ng-bind="requestResetPassword.successMessage"></span></strong>
                </div>
                <!--Validation error-->
                <span class="orcid-error" ng-show="requestResetPassword.errors.length > 0">
                    <div ng-repeat='error in requestResetPassword.errors' ng-bind-html="error"></div>
                </span>
                <!--General error-->
                <div style="margin-bottom: 15px;" ng-show="showSendResetLinkError">
                    <span class="orcid-error">${springMacroRequestContext.getMessage("Email.resetPasswordForm.error")}</span>
                </div>  
				<button class="btn btn-primary" ng-click="postPasswordResetRequest()">${springMacroRequestContext.getMessage("reset_password.sendResetLink")}</button>
			</div>
		</form>
  	</div>
</div>