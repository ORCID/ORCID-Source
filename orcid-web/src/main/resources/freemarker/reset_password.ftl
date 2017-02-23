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
<@public classes=['home'] nav="register">
    <div ng-controller="RequestPasswordResetCtrl" id="RequestPasswordResetCtr" ng-init="getResetPasswordForm()" class="row">
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <h2>${springMacroRequestContext.getMessage("reset_password.h2ForgottenPassword")}</h2>
            <p><small>${springMacroRequestContext.getMessage("reset_password.labelenteremailaddress")} <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>.</small></p>      		
        	<form id="password-reset-form" name="emailAddressForm">
        		<fieldset>
        			<span class="orcid-error"
			            ng-show="requestResetPassword.errors.length > 0">
			            <div ng-repeat='error in requestResetPassword.errors'
			                ng-bind-html="error"></div>
			        </span>
			        <div class="alert alert-success" ng-show="requestResetPassword.successMessage != null">
			        	<strong><span ng-bind="requestResetPassword.successMessage" /></strong>
			        </div>
        			<div class="control-group">
            			<label for="email" class="control-label">${springMacroRequestContext.getMessage("manage_bio_settings.emailaddress")} </label>
               			<div class="controls">                    	
               				<input id="email" ng-model="requestResetPassword.email" ng-change="validateRequestPasswordReset()" />
               				<span class="required">*</span>
               			</div>
               			<button class="btn btn-primary" ng-click="postPasswordResetRequest()">${springMacroRequestContext.getMessage("reset_password.labelSendInstructions")}</button>
        			</div>
        		</fieldset>
        	</form>
        </div>       
    </div>
</@public>