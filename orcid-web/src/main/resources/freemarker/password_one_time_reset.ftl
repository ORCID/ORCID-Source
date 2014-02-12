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
<@spring.bind "passwordTypeAndConfirmForm.*" />			
	<div class="row">
        <div class ="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12">
        	<p>${springMacroRequestContext.getMessage("password_one_time_reset.ptypenewpassword")}</p>
        	<#if spring.status.error>
    			<div class="alert alert-error">        			
        			<#list spring.status.errorMessages?sort as error><@orcid.msg '${error}'/><#if error_has_next><br/></#if></#list>        			
    			</div>
    		</#if>               
    		<div ng-controller="ResetPasswordCtrl">
	            <form id="reset-password-one-time" name="oneTimeResetForm" action="<@spring.url '/one-time-password/${encryptedEmail}'/>" method="post" autocomplete="off">
	                    <div class="control-group">
	                        <label for="passwordTypeAndConfirmForm.password.value" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.labelpassword")}</label>
	                        <div class="controls">
	                            <input id="passwordField" type="password" name="password" value="${(passwordTypeAndConfirmForm.password.value)!}" class="input-xlarge" ng-model="resetPasswordForm.password.value" ng-change="serverValidate()"/>                            
	                            <span class="required">*</span>
	                            <@orcid.passwordHelpPopup />  
	                            <span class="orcid-error" ng-show="resetPasswordForm.password.errors.length > 0">
									<div ng-repeat='error in resetPasswordForm.password.errors' ng-bind-html="error"></div>
						   		</span>                          
	                        </div>
	                        <label for="passwordTypeAndConfirmForm.retypedPassword.value" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.labelconfirmpassword")}</label>
	                         <div class="controls">                         	
	                         	<input id="passwordField" type="password" name="retypedPassword" value="${(passwordTypeAndConfirmForm.retypedPassword.value)!}" class="input-xlarge" ng-model="resetPasswordForm.retypedPassword.value" ng-change="serverValidate()"/>                            
	                            <span class="required">*</span>
	                            <span class="orcid-error" ng-show="resetPasswordForm.retypedPassword.errors.length > 0">
									<div ng-repeat='error in resetPasswordForm.retypedPassword.errors' ng-bind-html="error"></div>
						   		</span>                           
	                        </div>    
	                        <div class="controls">
	        					<button id="bottom-submit-password-change" class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("password_one_time_reset.btnsavechanges")}</button>    
	        				</div>
	    				</div>	
		         </form>
			</div>
        </div>
    </div>                    
</@public>