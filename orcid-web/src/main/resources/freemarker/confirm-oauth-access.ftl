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
<#assign displayName = "">
<#if client_name??>
	<#assign displayName = client_name>
</#if>
<!-- colorbox-content -->
<div class="container top-green-border">
<div class="row">
    <div class="col-md-6 col-sm-12">
        <@security.authorize ifAnyGranted="ROLE_USER">
        <#if justRegistered?? && justRegistered>
            <div class="alert alert-success">
                <strong><@spring.message "orcid.frontend.web.just_registered"/></strong>
            </div>
        </#if>
        
        <div class="logo">
            <h1><a href="${aboutUri}"><img src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo" /></a></h1>
            <p>${springMacroRequestContext.getMessage("confirm-oauth-access.connectingresearchandresearchers")}</p>
        </div>
        
        <br />
        <h2 class="oauth-title">${springMacroRequestContext.getMessage("confirm-oauth-access.connecting")}<br /><span>${displayName?html}</span><br />${springMacroRequestContext.getMessage("confirm-oauth-access.withyourrecord")}</h2>        
        <hr />
        
    </div>
    </div>
   <div class="row">
   	
   	<div class="col-md-6 col-md-push-6 col-sm-12 margin-top-box">
         <h5>
         	<#if (client_website)??>
         		<a href="${(client_website)!}" target="_blank">
         	</#if>
         	${displayName?html}
        	<#if (client_website)??>
        		</a>
        	</#if>
        	</h5>
			${client_description!?html}
    </div>
    
    <div class="col-md-6 col-md-pull-6 col-sm-12 margin-bottom-box">
         <h3>
         	${displayName?html}
         </h3>
         <p>${springMacroRequestContext.getMessage("confirm-oauth-access.hasaskedforthefollowing")}</p>
         <#list scopes as scope>
             <div class="alert">
                 <@spring.message "${scope.declaringClass.name}.${scope.name()}"/>
             </div>
         </#list>
         <p><@spring.message "orcid.frontend.web.oauth_is_secure"/><a href="${aboutUri}/footer/privacy-policy" target="_blank">. <@orcid.msg 'public-layout.privacy_policy'/></a>.</p>
         <div class="row">
	        <#assign authOnClick = "">
	        <#list scopes as scope>
	           <#assign authOnClick = authOnClick + " orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'Authorize_" + scope.name()?replace("ORCID_", "") + "', 'OAuth " + client_group_name?js_string + " - " + client_name?js_string + "']);">     
	        </#list>
	    	<#assign denyOnClick = " orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Authorize_Deny', 'OAuth " + client_group_name?js_string + " - " + client_name?js_string + "']);">	    	
	    	<div class="col-md-3 col-sm-2">     
	            <span class="span">
	                <form id="denialForm" class="form-inline" name="denialForm" action="<@spring.url '/oauth/authorize'/>" onsubmit="${denyOnClick} orcidGA.gaFormSumbitDelay(this); return false;" method="post">
	                    <input name="user_oauth_approval" value="false" type="hidden"/>
	                    <button class="btn btn-primary" name="deny" value="${springMacroRequestContext.getMessage('confirm-oauth-access.Deny')}" type="submit">Deny
	                    </button>	
	                </form>        
	            </span>
            </div>
            <div class="col-md-6 col-sm-2">
	            <span class="span">
	                <form id="confirmationForm" class="form-inline" name="confirmationForm" action="<@spring.url '/oauth/authorize'/>" onsubmit="${authOnClick} orcidGA.gaFormSumbitDelay(this); return false;" method="post">
	                    <input name="user_oauth_approval" value="true" type="hidden"/>
	                    <button class="btn btn-primary" name="authorize" value="${springMacroRequestContext.getMessage('confirm-oauth-access.Authorize')}" type="submit">
	                    	Authorize
	                    </button>
	                </form>
	            </span>	            
            </div>
        </div>        
    </div>
    
</div>
</@security.authorize>
</div>
</@base>