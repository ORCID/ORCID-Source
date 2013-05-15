<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@public classes=['home'] nav="register">
    <@spring.bind "emailAddressForm.*" />    
    <div class="row">   
        <div class="span3"></div>
        <div class="span9">
        <#if spring.status.error>                                                
    			<div class="alert alert-success">
    				<@spring.showErrors "orcid-error"/>
				</div>	
			</#if>   
			<#if disabledAccount?? && disabledAccount>
				<div class="alert alert-success">
	    			<strong><@spring.message "orcid.frontend.reset.password.disabled_account"/>&nbsp;<a href="${(aboutUri)}/help/contact-us"><@spring.message "orcid.frontend.reset.password.disabled_account.link"/></a></strong>
				</div>
			</#if>                   
    		<#if passwordResetLinkExpired?? && passwordResetLinkExpired>
				<div class="alert alert-success">
					<strong><@spring.message "orcid.frontend.reset.password.resetLinkExpired"/></strong>
				</div>
			</#if>           
    		<#if passwordResetSuccessful?? && passwordResetSuccessful>
				<div class="alert alert-success">
	    			<strong><@spring.message "orcid.frontend.reset.password.successfulReset"/></strong>
				</div>
			</#if>                         
            <h2>${springMacroRequestContext.getMessage("reset_password.h2ForgottenPassword")}</h2>
            <p><small>${springMacroRequestContext.getMessage("reset_password.labelenteremailaddress")} <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>.</small></p>      		
        	<form id="password-reset-form" name="emailAddressForm" action="reset-password" method="post">
        		<fieldset>
        			<div class="control-group">
            			<label for="givenNames" class="control-label">${springMacroRequestContext.getMessage("manage_bio_settings.emailaddress")} </label>
               				<div class="controls">                    	
               				<@spring.formInput "emailAddressForm.userEmailAddress" 'class="input-xlarge" name="userEmail"'/>
               				<span class="required">*</span>
               			</div>
               			<button class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("reset_password.labelSendInstructions")}</button>
        			</div>
        		</fieldset>
        	</form>
        </div>       
    </div>
</@public>