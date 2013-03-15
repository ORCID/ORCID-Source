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
            <div class="alert alert-success">
                <@spring.showErrors "orcid-error"/>
		    </div>
		    <#if claimResendSuccessful!false>
				<div class="alert alert-success">
	    			<strong><@spring.message "resend_claim.successful_resend"/></strong>
				</div>
			</#if>       
            <h2><@spring.message "resend_claim.title"/></h2>
            <p><small><@spring.message "resend_claim.resend_help"/> <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>.</small></p>      		
        	<form id="resend-claim-form" name="emailAddressForm" action="resend-claim" method="post">
        		<fieldset>
        			<div class="control-group">
            			<label for="givenNames" class="control-label">${springMacroRequestContext.getMessage("resend_claim.labelEmailAddress")} </label>
               				<div class="controls">                    	
               				<@spring.formInput "emailAddressForm.userEmailAddress" 'class="input-xlarge" name="userEmail"'/>
               				<span class="required">*</span>
               			</div>
               			<button class="btn btn-primary" type="submit"><@spring.message "resend_claim.resend_claim_button_text"/></button>
        			</div>
        		</fieldset>
        	</form>
        </div>
    </div>
</@public>