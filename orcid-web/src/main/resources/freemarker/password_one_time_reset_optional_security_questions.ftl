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
<@public>
<div class="row">
	<div class ="span9 offset3">
		<@spring.bind "oneTimeResetPasswordForm.*" />
		<#if spring.status.error>
			<div class="alert alert-error">
	    		<#if spring.status.error>
    				<div class="alert alert-error">        			
	    	    		<#list spring.status.errorMessages?sort as error>${error}<#if error_has_next><br/></#if></#list>        			
    				</div>
    			</#if>    
			</div>
		</#if>
		<form id="reg-form-password" action="<@spring.url '/reset-password-email/${encryptedEmail}'/>" method="post" autocomplete="off">
    		<div class="control-group">
    			<label for="passwordField" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.pleaseenternewpassword")}</label>
    			<div class="controls">
        			<input id="passwordField" type="password" name="password" value="${(oneTimeResetPasswordForm.password)!}" class="input-xlarge password-strength"/>
        			<span class="required">*</span>
        			<@orcid.passwordHelpPopup />   
    			</div>
			</div>
			<div class="control-group">
	    		<label for="retypedPassword" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.confirmyournewpassword")}</label>
    			<div class="controls">
	    			<input id="retypedPassword" type="password" name="retypedPassword" value="${(oneTimeResetPasswordForm.retypedPassword)!}" class="input-xlarge"/>
    	    		<span class="required">*</span>
    			</div>        
			</div>
			<p><small>${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.optionalconsidersetting")}<small></p>			
			<div class="control-group">        		   
	            	<div class="controls">
	            	 	<label for="oneTimeResetPasswordForm.securityQuestionId" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.challengequestion")}</label>
            			<select id="securityQuestionId" name="securityQuestionId" class="span5">
            				<#list securityQuestions?keys as key>
							   <option value="${key}">${securityQuestions[key]}</option>
							</#list>
            			</select>
            			<label for="oneTimeResetPasswordForm.securityQuestionAnswer" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.challengeanswer")}</label>            	
            			<@spring.formInput "oneTimeResetPasswordForm.securityQuestionAnswer", 'class="span5"' />	                	
	                </div>
        		</div>
    			<div class="controls">
        			<button id="bottom-submit-password-change" class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</button>      
    			</div>    
		</form>
	</div>
</div>
</@public>


