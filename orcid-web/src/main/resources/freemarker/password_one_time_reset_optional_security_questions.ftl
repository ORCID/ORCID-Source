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
	<div class ="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12">
		<@spring.bind "oneTimeResetPasswordForm.*" />
		<#if spring.status.error>
			<div class="alert alert-error">
	    		<#if spring.status.error>
    				<div class="alert alert-error">        			
	    	    		<#list spring.status.errorMessages?sort as error><@orcid.msg '${error}' /><#if error_has_next><br/></#if></#list>        			
    				</div>
    			</#if>    
			</div>
		</#if>
		<div ng-controller="ResetPasswordCtrl">
			<form id="reg-form-password" action="<@spring.url '/reset-password-email/${encryptedEmail}'/>" method="post" autocomplete="off">
	    		<div class="control-group">
	    			<label for="passwordField" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.pleaseenternewpassword")}</label>
	    			<div class="controls">
	        			<input id="passwordField" type="password" name="password" value="${(oneTimeResetPasswordForm.password)!}" class="input-xlarge" ng-model="resetPasswordForm.password.value" ng-change="serverValidate()"/>
	        			<span class="required">*</span>
	        			<@orcid.passwordHelpPopup /> 
	        			<span class="orcid-error" ng-show="resetPasswordForm.password.errors.length > 0">
							<div ng-repeat='error in resetPasswordForm.password.errors' ng-bind-html="error"></div>
						</span>   
	    			</div>
				</div>
				<div class="control-group">
		    		<label for="retypedPassword" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.confirmyournewpassword")}</label>
	    			<div class="controls">
		    			<input id="retypedPassword" type="password" name="retypedPassword" value="${(oneTimeResetPasswordForm.retypedPassword)!}" class="input-xlarge" ng-model="resetPasswordForm.retypedPassword.value" ng-change="serverValidate()"/>
	    	    		<span class="required">*</span>
	    	    		<span class="orcid-error" ng-show="resetPasswordForm.retypedPassword.errors.length > 0">
							<div ng-repeat='error in resetPasswordForm.retypedPassword.errors' ng-bind-html="error"></div>
						</span>   
	    			</div>        
				</div>
				<p><small>${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.optionalconsidersetting")}<small></p>			
				<div class="control-group">
						<label for="oneTimeResetPasswordForm.securityQuestionId" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.challengequestion")}</label>        		   
		            	<div class="controls">		            	 	
	            			<select id="securityQuestionId" name="securityQuestionId" class="input-xlarge">
	            				<#list securityQuestions?keys as key>
								   <option value="${key}">${securityQuestions[key]}</option>
								</#list>
	            			</select>	                	
		                </div>
		                <label for="oneTimeResetPasswordForm.securityQuestionAnswer" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.challengeanswer")}</label>
		                <div class="controls">			                            	
		            		<@spring.formInput "oneTimeResetPasswordForm.securityQuestionAnswer",'class="input-xlarge"'/>
			            </div>
	        		</div>
	    			<div class="controls">
	        			<button id="bottom-submit-password-change" class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</button>      
	    			</div>    
			</form>
		</div>
	</div>
</div>
</@public>


