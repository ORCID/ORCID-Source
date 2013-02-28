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
            <h2>Forgotten Password</h2>
            <p><small>Please enter the email address that you are using for your ORCID iD. An email will be sent to this
             address with further instructions. If you no longer have access to this address please contact 
             support at <a href="mailto:support@orcid.org">support@orcid.org</a>.</small></p>      		
        	<form id="password-reset-form" name="emailAddressForm" action="reset-password" method="post">
        		<fieldset>
        			<div class="control-group">
            			<label for="givenNames" class="control-label">Email Address </label>
               				<div class="controls">                    	
               				<@spring.formInput "emailAddressForm.userEmailAddress" 'class="input-xlarge" name="userEmail"'/>
               				<span class="required">*</span>
               			</div>
               			<button class="btn btn-primary" type="submit">Send Instructions</button>
        			</div>
        		</fieldset>
        	</form>
        </div>       
    </div>
</@public>