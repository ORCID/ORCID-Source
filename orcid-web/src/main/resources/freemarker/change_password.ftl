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
<@base>
<div class="popover-frame">
	<@spring.bind "changePasswordForm.*" />
	<#if passwordOptionsSaved?? && passwordOptionsSaved>
		<div class="alert alert-success">
		    <strong><@spring.message "orcid.frontend.web.passwordoptions_changed"/></strong>
		</div>
	</#if>
	<#if spring.status.error>
		<div class="alert alert-error">
		    <ul class="validationerrors">
		        <#list spring.status.errorMessages?sort as error> <li>${error}</li> </#list>
		    </ul>
		</div>
	</#if>
	<form id="reg-form-password" class="popover-form" action="<@spring.url '/account/password'/>" method="post" autocomplete="off">
	    <div class="">
	    	<label for="passwordField" class="">${springMacroRequestContext.getMessage("change_password.oldpassword")}</label>
	    	<div class="relative">
	        	<input id="passwordField" type="password" name="oldPassword" value="${(changePasswordForm.oldPassword)!}" class="input-xlarge"/>
	        	<span class="required">*</span>            
	    	</div>
		</div>
		<div class="">
	    	<label for="passwordField" class="">${springMacroRequestContext.getMessage("change_password.newpassword")}</label>
	    	<div class="relative">
	        	<input id="passwordField" type="password" name="password" value="${(changePasswordForm.password)!}" class="password-strength input-xlarge"/>
	        	<span class="required">*</span>
	        	<a class="password-info" href="#"><i class="icon-question-sign"></i></a>    
	    	</div>
		</div>
		<div class="">
	    	<label for="retypedPassword" class="">${springMacroRequestContext.getMessage("change_password.confirmnewpassword")}</label>
	    	<div class="relative">
	    		<input id="retypedPassword" type="password" name="retypedPassword" value="${(changePasswordForm.retypedPassword)!}" class="input-xlarge"/>
	        	<span class="required">*</span>
	    	</div>        
		</div><br />
	    <div class="">
	        <button id="bottom-submit-password-change" class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</button>
	        <button id="bottom-clear-password-changes" class="btn close-parent-popover" type="reset">${springMacroRequestContext.getMessage("freemarker.btncancel")}</button>
	    </div>
	</form>
</div>
</@base>
