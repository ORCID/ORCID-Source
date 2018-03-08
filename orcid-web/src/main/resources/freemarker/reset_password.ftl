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
    <@orcid.checkFeatureStatus 'ANGULAR2_QA'>
    <!-- ****
    <request-password-reset-ng2></request-password-reset-ng2>
    -->
    </@orcid.checkFeatureStatus>
    <@orcid.checkFeatureStatus featureName='ANGULAR1_LEGACY' enabled=false> 
    <div ng-controller="RequestPasswordResetCtrl" id="RequestPasswordResetCtr" ng-init="getResetPasswordForm()" class="row">
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <h2>${springMacroRequestContext.getMessage("reset_password.h2ForgottenPassword")}</h2>
            <#if (tokenExpired)>
		        <span class="orcid-error">${springMacroRequestContext.getMessage("orcid.frontend.reset.password.resetAgain")}</span>
		    </#if>
            <p>
                <small>
                ${springMacroRequestContext.getMessage("reset_password.enterEmail_1")}<br />
                ${springMacroRequestContext.getMessage("reset_password.enterEmail_2")}
                <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>
                </small>
            </p>      		
        	<form id="password-reset-form" name="emailAddressForm">
        	    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
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
            			<label for="email" class="control-label">${springMacroRequestContext.getMessage("manage_bio_settings.h3email")} </label>
               			<div class="controls">                    	
               				<input id="email" type="text" ng-model="requestResetPassword.email" />
               			</div>
               			<button class="btn btn-primary topBuffer" ng-click="postPasswordResetRequest()">${springMacroRequestContext.getMessage("reset_password.sendResetLink")}</button>
        			</div>
        		</fieldset>
        	</form>
        </div>       
    </div>
    </@orcid.checkFeatureStatus>
</@public>