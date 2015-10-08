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
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" />
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-social/4.10.0/bootstrap-social.min.css" />
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
	            		<td>
	            			<button id='form-sign-in-button' class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("login.signin")}</button>
	            			<span id="ajax-loader" class="no-visible"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x2 green"></i></span>
	            		</td>
	            	</tr>
	            </table>	            	            
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
				<a href="<@orcid.rootPath '/shibboleth/link'/>">${springMacroRequestContext.getMessage("login.shibboleth")}</a>    
           </div>
        </div>
    </#if>
    <#if (RequestParameters['social'])??>
		<div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12">
			<h5>Or sign in with :</h5>
			<table>
				<tr>
					<td>
						<form action="<@orcid.rootPath '/signin/facebook'/>" method="POST">
						    <button type="submit" class="btn btn-social-icon btn-facebook"><i class="fa fa-facebook"></i></button>
						    <input type="hidden" name="scope" value="email" />
						</form>
					</td>
					<td style="padding-left: 20px;">
						<form action="<@orcid.rootPath '/signin/google'/>" method="POST">
						    <button type="submit" class="btn btn-social-icon btn-google"><i class="fa fa-google"></i></button>
						    <input type="hidden" name="scope" value="email" />
						</form>
					</td>
				</tr>
			</table>
		</div>
	</#if>
</@public>