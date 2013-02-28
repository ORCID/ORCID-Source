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
    		<div class="control-group password-group">
    			<label for="passwordField" class="control-label">Please enter a New Password</label>
    			<div class="controls">
        			<input id="passwordField" type="password" name="password" value="${(oneTimeResetPasswordForm.password)!}" class="input-xlarge password-strength"/>
        			<span class="required">*</span>
        			<a class="password-info" href="#"><i class="icon-question-sign"></i></a>
        	 			<div class="popover bottom password-details">
                        	<div class="arrow"></div>
                        	<div class="popover-content">
	                        	<div class="help-block">
                                        <p>Must be 8 or more characters and contain:</p>
                                        <ul>
                                            <li>at least 1 numeral: 0 - 9</li>
                                            <li>at least 1 of the following:</li>
                                            <ul>
                                            	<li>alpha character, case-sensitive a-Z</li>
                                            	<li>any of the following symbols:<br /> ! @ # $ % ^ * ( ) ~ `{ } [ ] | \ &amp; _</li>
                                            </ul>
                                            <li>optionally the space character, i.e ' ' and other punctuation such as . , ;</li>
                                        </ul>                                       
                                        <p>Example: sun% moon2</p>
                                    </div>
                        	</div>
                        </div>    
    			</div>
			</div>
			<div class="control-group password-group">
	    		<label for="retypedPassword" class="control-label">Confirm your new password</label>
    			<div class="controls">
	    			<input id="retypedPassword" type="password" name="retypedPassword" value="${(oneTimeResetPasswordForm.retypedPassword)!}" class="input-xlarge"/>
    	    		<span class="required">*</span>
    			</div>        
			</div>
			<p><small>(optional) Please consider setting a challenge question for additional security.<small></p>			
			<div class="control-group">        		   
	            	<div class="controls">
	            	 	<label for="oneTimeResetPasswordForm.securityQuestionId" class="control-label">Challenge Question</label>
            			<@spring.formSingleSelect "oneTimeResetPasswordForm.securityQuestionId", securityQuestions, 'class="span5"' />                		            		
            			<label for="oneTimeResetPasswordForm.securityQuestionAnswer" class="control-label">Challenge Answer</label>            	
            			<@spring.formInput "oneTimeResetPasswordForm.securityQuestionAnswer", 'class="span5"' />	                	
	                </div>
        		</div>
    			<div class="controls">
        			<button id="bottom-submit-password-change" class="btn btn-primary" type="submit">Save changes</button>      
    			</div>    
		</form>
	</div>
</div>
</@public>


