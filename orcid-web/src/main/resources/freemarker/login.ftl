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
	
	<#include "sandbox_warning.ftl"/>
	<#include "/common/browser-checks.ftl" />
	<@spring.bind "loginForm" />
	<@spring.showErrors "<br/>" "error" />
	
	
	
	
	
	<#if (RequestParameters['shibboleth'])?? || (RequestParameters['social'])??>
		<div class="login">
			<div class="row">
				<!-- ORCID login form -->
					<div class="col-md-offset-1 col-md-5 login-left-column col-xs-12">
					<p class="title">Sign in with your ORCID account</p>
					<form class="form-sign-in" id="loginForm" action="<@orcid.rootPath '/signin/auth'/>" method="post">
				        <div class="control-group">
				            <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>
				            <div>               
				                <input type="text" id="userId" name="userId" value="" placeholder="${springMacroRequestContext.getMessage("login.username")}">
				            </div>
				        </div>
				        
				        <div class="control-group password">
				            <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>
				            <div>
				                <input type="password" id="password" name="password" value="" placeholder="${springMacroRequestContext.getMessage("login.password")}">
				            </div>
				            <div id="login-reset">
				                <a href="<@orcid.rootPath '/reset-password'/>">${springMacroRequestContext.getMessage("login.reset")}</a>
				            </div>
				        </div>
				        
				        <div class="control-group submit-login">				            
	                        <button id='form-sign-in-button' class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("login.signin")}</button>
	                        <span id="ajax-loader" class="no-visible"><i id="ajax-loader-icon" class="glyphicon glyphicon-refresh spin x2 green"></i></span>
				            
				            <#if (RequestParameters['alreadyClaimed'])??>
				            	<div class="alert"><@spring.message "orcid.frontend.security.already_claimed"/></div>
				            </#if>   
				            <#if (RequestParameters['invalidClaimUrl'])??>
				            	<div class="alert"><@spring.message "orcid.frontend.security.invalid_claim_url"/></div>
				            </#if>              
				        </div>			    
					</form>
					<#if (RequestParameters['shibboleth'])?? || (RequestParameters['social'])??>
						<span class="or">OR</span>
					</#if>	
				</div>			
				<!-- Shibboleth and Social Login -->			
				<div class="col-md-offset-1 col-md-5 login-right-column">
					<#if (RequestParameters['shibboleth'])??>
						<p class="title">Sign in via your institution <a href="${springMacroRequestContext.getMessage('common.support_url')}" target="_blank" class="shibboleth-help"><i class="glyphicon glyphicon-question-sign"></i></a></p>
						<div id="idpSelectContainer">				   
						    <div id="idpSelectInner">
						        <!-- Where the widget is going to be injected -->
						        <div id="idpSelect"></div>
						    </div>				    
						</div>
					</#if>
					<#if (RequestParameters['social'])??>
					    <div class="social-login">
					    	<p class="title">Sign in with a social account <a href="${springMacroRequestContext.getMessage('common.support_url')}" target="_blank" class="shibboleth-help"><i class="glyphicon glyphicon-question-sign"></i></a></p>
					    	<ul class="social-icons">
					    		<li>
					    			<form action="<@orcid.rootPath '/signin/facebook'/>" method="POST">
				                        <button type="submit" class="btn btn-social-icon btn-facebook"></button>
				                        <input type="hidden" name="scope" value="email" />
				                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				                    </form>
					    		</li>
					    		<!-- 
					    		<li>
					    			<form action="<@orcid.rootPath '/signin/twitter'/>" method="POST">
				                        <button type="submit" class="btn btn-social-icon btn-twitter"></button>
				                        <input type="hidden" name="scope" value="email" />
				                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				                    </form>
					    		</li>
					    		 -->
					    		 <li>
					    			<form action="<@orcid.rootPath '/signin/google'/>" method="POST">
				                        <button type="submit" class="btn btn-social-icon btn-google"></button>
				                        <input type="hidden" name="scope" value="email" />
				                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				                    </form>
					    		</li>
					    	</ul>				        
					    </div>
					</#if>
						
				</div>		
			</div>		
			<div class="row">			
				<div class="login-register">
					Do you have an ORCID iD yet? <a class="reg" href="<@orcid.rootPath '/register'/>">${springMacroRequestContext.getMessage("login.register")}</a>
				</div>
			</div>
		<#else>
			<div id="old-login">
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
						       	<a class="reg" href="<@orcid.rootPath '/register'/>">${springMacroRequestContext.getMessage("public-layout.register")}</a>
						    </div>
					    </div>		    
				    </div>
				</form>
			</div>
		</#if>  
	</div>
</@public>