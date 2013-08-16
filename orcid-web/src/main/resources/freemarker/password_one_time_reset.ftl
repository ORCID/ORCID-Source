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
        <div class ="span9 offset3">
        	<p>${springMacroRequestContext.getMessage("password_one_time_reset.ptypenewpassword")}</p>
        	<#if spring.status.error>
    			<div class="alert alert-error">        			
        			<#list spring.status.errorMessages?sort as error><@orcid.msg '${error}'/><#if error_has_next><br/></#if></#list>        			
    			</div>
    		</#if>               
            <form id="reset-password-one-time" name="oneTimeResetForm" action="<@spring.url '/one-time-password/${encryptedEmail}'/>" method="post" autocomplete="off">
                    <div class="control-group">
                        <label for="passwordTypeAndConfirmForm.password" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.labelpassword")}</label>
                        <div class="controls">
                            <input id="passwordField" type="password" name="password" value="${(passwordTypeAndConfirmForm.password)!}" class="input-xlarge password-strength" data-validate="{required:true}"/>                            
                            <span class="required">*</span>
                            <@orcid.passwordHelpPopup />                            
                        </div>
                        <label for="passwordTypeAndConfirmForm.retypedPassword" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.labelconfirmpassword")}</label>
                         <div class="controls">                         	
                         	<input id="passwordField" type="password" name="retypedPassword" value="${(passwordTypeAndConfirmForm.retypedPassword)!}" class="input-xlarge" data-validate="{required:true}"/>                            
                            <span class="required">*</span>                           
                        </div>    
                        <div class="controls">
        					<button id="bottom-submit-password-change" class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("password_one_time_reset.btnsavechanges")}</button>    
        				</div>
    				</div>	
	         </form>
        </div>
    </div>                    
</@public>