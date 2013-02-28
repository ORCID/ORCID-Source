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
    <@spring.bind "changeSecurityQuestionForm.*" />    
    <div class="row">   
        <div class="span3"></div>
        <div class="span9">
        	<#if securityQuestionIncorrect ?? && securityQuestionIncorrect>
				<div class="alert alert-error">
    				<@spring.message "orcid.frontend.reset.password.securityQuestionIncorrect"/>
				</div>
			</#if>
      		<#if spring.status.error>
    			<div class="alert alert-error">        			
            			<#list spring.status.errorMessages?sort as error>${error}<#if error_has_next><br/></#if></#list>        			
    			</div>
    		</#if>
    		<p><small>Please provide the answer to your security question to reset your password.</small></p>    		
        	<form id="answer-security--question-form" name="changeSecurityQuestionForm" action="<@spring.url '/answer-security-question/${encryptedEmail}'/>" method="post" autocomplete="off">
        		<fieldset>
        			<div class="control-group">            			
        				<div class="controls">
        				    <label for="changeSecurityQuestionForm.securityQuestionAnswer" class="control-label">${securityQuestionText}</label>
            				<@spring.formInput "changeSecurityQuestionForm.securityQuestionAnswer", 'class="span5"' />
                			<span class="required">*</span>
            			</div>
        			</div>        
         			<div class="controls">
            			<button id="bottom-submit-security-question" class="btn btn-primary" type="submit">Submit</button>            
        			</div>
        		</fieldset>
        		<input type="hidden" name="securityQuestionId" value="${changeSecurityQuestionForm.securityQuestionId}"/>
        	</form>
        </div>       
    </div>
</@public>