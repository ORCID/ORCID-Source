<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@public classes=['home'] nav="signin">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css" />
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" />
<style type="text/css">
    .ml { width: 120px; color: white; text-decoration: none }
    .ml:hover, a:focus { color: white; text-decoration: none }
    .btn-social{position:relative;padding-left:44px;text-align:left;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.btn-social :first-child{position:absolute;left:0;top:0;bottom:0;width:32px;line-height:34px;font-size:1.6em;text-align:center;border-right:1px solid rgba(0,0,0,0.2)}
	.btn-social.btn-md{padding-left:40px}.btn-social.btn-md :first-child{line-height:35px;width:35px;font-size:1.6em}
	.btn-social-icon{position:relative;padding-left:44px;text-align:left;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;height:34px;width:34px;padding:0}.btn-social-icon :first-child{position:absolute;left:0;top:0;bottom:0;width:32px;line-height:34px;font-size:1.6em;text-align:center;border-right:1px solid rgba(0,0,0,0.2)}
	.btn-social-icon.btn-md{padding-left:45px}.btn-social-icon.btn-md :first-child{line-height:35px;width:35px;font-size:1.6em}
	.btn-social-icon :first-child{border:none;text-align:center;width:100% !important}
	.btn-social-icon.btn-lg{height:45px;width:45px;padding-left:0;padding-right:0}
	.btn-social-icon.btn-sm{height:30px;width:30px;padding-left:0;padding-right:0}
	.btn-social-icon.btn-xs{height:22px;width:22px;padding-left:0;padding-right:0}
	.btn-facebook{color:#fff;background-color:#3b5998;margin-left:10px;border-color:rgba(0,0,0,0.2)}.btn-facebook:hover,.btn-facebook:focus,.btn-facebook:active,.btn-facebook.active,.open>.dropdown-toggle.btn-facebook{color:#fff;background-color:#2d4373;border-color:rgba(0,0,0,0.2)}
	.btn-facebook:active,.btn-facebook.active,.open>.dropdown-toggle.btn-facebook{background-image:none}
	.btn-facebook.disabled,.btn-facebook[disabled],fieldset[disabled] .btn-facebook,.btn-facebook.disabled:hover,.btn-facebook[disabled]:hover,fieldset[disabled] .btn-facebook:hover,.btn-facebook.disabled:focus,.btn-facebook[disabled]:focus,fieldset[disabled] .btn-facebook:focus,.btn-facebook.disabled:active,.btn-facebook[disabled]:active,fieldset[disabled] .btn-facebook:active,.btn-facebook.disabled.active,.btn-facebook[disabled].active,fieldset[disabled] .btn-facebook.active{background-color:#3b5998;border-color:rgba(0,0,0,0.2)}
	.btn-facebook .badge{color:#3b5998;background-color:#fff}
	.btn-google-plus{color:#fff;background-color:#dd4b39;border-color:rgba(0,0,0,0.2)}.btn-google-plus:hover,.btn-google-plus:focus,.btn-google-plus:active,.btn-google-plus.active,.open>.dropdown-toggle.btn-google-plus{color:#fff;background-color:#c23321;border-color:rgba(0,0,0,0.2)}
	.btn-google-plus:active,.btn-google-plus.active,.open>.dropdown-toggle.btn-google-plus{background-image:none}
	.btn-google-plus.disabled,.btn-google-plus[disabled],fieldset[disabled] .btn-google-plus,.btn-google-plus.disabled:hover,.btn-google-plus[disabled]:hover,fieldset[disabled] .btn-google-plus:hover,.btn-google-plus.disabled:focus,.btn-google-plus[disabled]:focus,fieldset[disabled] .btn-google-plus:focus,.btn-google-plus.disabled:active,.btn-google-plus[disabled]:active,fieldset[disabled] .btn-google-plus:active,.btn-google-plus.disabled.active,.btn-google-plus[disabled].active,fieldset[disabled] .btn-google-plus.active{background-color:#dd4b39;border-color:rgba(0,0,0,0.2)}
	.btn-google-plus .badge{color:#dd4b39;background-color:#fff}
</style>
<#include "sandbox_warning.ftl"/>
	<form class="form-sign-in" id="loginForm" action="<@orcid.rootPath '/signin/auth'/>" method="post">
		<div class="row">
		    <@spring.bind "loginForm" />		     
		    <@spring.showErrors "<br/>" "error" />		     
		    <#include "/common/browser-checks.ftl" />
		    <div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12">
		        <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>
		        <div>		        
		        	<input type="text" id="userId" name="userId" value="" placeholder="${springMacroRequestContext.getMessage("login.username")}">
		        </div>			        
		    </div>
		    <div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12 password">
		        <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>
		        <div>
		            <input type="password" id="password" name="password" value="" placeholder="${springMacroRequestContext.getMessage("login.password")}">
		        </div>
		    </div>
		    <div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12 submit-login">		        		        	
	            <table>
	            	<tr>
	            		<td><button id='form-sign-in-button' class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("login.signin")}</button></td>
	            		<td>
							<button onclick="loginFacebook()" class="ml btn btn-block btn-social btn-md btn-facebook"><i class="fa fa-facebook"></i>Facebook</button>
	            		</td>
	            	</tr>
	            </table>
	            <span id="ajax-loader" class="no-visible"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x2 green"></i></span>	            
	            <#if (RequestParameters['alreadyClaimed'])??>
			        <div class="alert"><@spring.message "orcid.frontend.security.already_claimed"/></div>
			    </#if>   
			    <#if (RequestParameters['invalidClaimUrl'])??>
			        <div class="alert"><@spring.message "orcid.frontend.security.invalid_claim_url"/></div>
			    </#if>		        
		    </div>	    		    
		</div>
		<div class="row">
			<div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12"> 
			    <div id="login-reset">
			        <a href="<@orcid.rootPath '/reset-password'/>">${springMacroRequestContext.getMessage("login.reset")}</a>&nbsp;&nbsp;
			    </div>
			    <div id="login-register">
			       	<a class="reg" href="<@orcid.rootPath '/register'/>">${springMacroRequestContext.getMessage("login.register")}</a>
			    </div>
		    </div>		    
	    </div>
	</form>
	<#if (RequestParameters['shibboleth'])??>
        <div class="row">
            <div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12">
                <form class="form-shibboleth-sign-in" id="shibbolethSignInForm" action="<@orcid.rootPath '/shibboleth/link'/>" method="get">
                    <button id='shibboleth-sign-in-button' class="btn btn-primary" type="submit">Sign in with Shibboleth</button>
                </form>
            </div>
        </div>
    </#if>
</@public>