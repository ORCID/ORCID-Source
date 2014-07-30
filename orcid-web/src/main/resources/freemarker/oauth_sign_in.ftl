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
	<#include "/common/browser-checks.ftl" />
	<div class="col-md-6 col-sm-12 margin-top-bottom-box" ng-controller="OauthAuthorizationController">
		<#if RequestParameters['twoSteps']??>	
			<div class="page-header">
			    <h3>${springMacroRequestContext.getMessage("oauth_sign_in.h3signin")}</h3>
			</div>
	        <form id="loginForm" action="<@spring.url '/signin/auth'/>" method="post">	            
	            <@spring.bind "loginForm" />
	            <@spring.showErrors "<br/>" "error" />
				<input type="hidden" name="client_name" value="${client_name}" />
				<input type="hidden" name="client_group_name" value="${client_group_name}" />
	            <div>
	                <label for="userId">${springMacroRequestContext.getMessage("oauth_sign_in.labelemailorID")}</label>
	                <div class="relative">
	                   <input type="text" id="userId" name="userId" value="${userId}" placeholder="Email or iD" class="input-xlarge">
	                </div>
	            </div>
	            <div id="passwordField">
	                <label for="password">${springMacroRequestContext.getMessage("oauth_sign_in.labelpassword")}</label>
	                <div class="relative">
	                   <input type="password" id="password" name="password" value="" placeholder="Password" class="input-xlarge">
	                </div>
	            </div>
	            <div id="buttons">
	                <div class="relative">
	                    <button class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("oauth_sign_in.h3signin")}</button>
	                    <span id="ajax-loader" class="hide"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x2 green"></i></span>
	                </div>
	                <div class="relative margin-top-box">
	                	<a href="<@spring.url '/reset-password'/>">${springMacroRequestContext.getMessage("oauth_sign_in.forgottenpassword")}</a>
	                </div>
	            </div>
	        </form>
		<#else>	
			<div class="app-client-name">
				<h3 ng-click="toggleClientDescription()">${client_name} - ${client_group_name}
					<a ng-show="!showClientDescription" ng-click="toggleClientDescription()" class="glyphicon glyphicon-chevron-down"></a>
					<a ng-show="showClientDescription" ng-click="toggleClientDescription()" class="glyphicon glyphicon-chevron-up"></a>
				</h3>
			</div>
			<div class="app-client-description">
				<p ng-show="showClientDescription">
					<span class="uppercase gray-bold-about"><@orcid.msg 'oauth_sign_in.about'/></span> ${client_description}
				</p>
			</div>
			<div>
				<#list scopes as scope>
					<div><span class="mini-orcid-icon"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/></div>
         		</#list>				
			</div>	
			<div>
				<p><@orcid.msg 'orcid.frontend.web.oauth_is_secure'/>.<a href="${aboutUri}/footer/privacy-policy" target="_blank"><@orcid.msg 'public-layout.privacy_policy'/></a>.</p>
			</div>
			<#assign denyOnClick = " orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Authorize_Deny', 'OAuth " + client_group_name?js_string + " - " + client_name?js_string + "']);"> 
			<!-- LOGIN FORM -->
			<div id="login" ng-show="!showRegisterForm" ng-init="initializeHiddenFields('${scopesString}','${redirect_uri}','${client_id}','${response_type}')">            		
	            <div>
	                <label for="userId">${springMacroRequestContext.getMessage("oauth_sign_in.labelemailorID")}</label>
	                <div class="relative">
	                   <input type="text" id="userId" ng-model="authorizationForm.userName.value" name="userId" value="${userId}" placeholder="Email or iD" class="input-xlarge">
	                </div>
	            </div>
	            <div id="passwordField">
	                <label for="password">${springMacroRequestContext.getMessage("oauth_sign_in.labelpassword")}</label>
	                <div class="relative">
	                   <input type="password" id="password" ng-model="authorizationForm.password.value" name="password" value="" placeholder="Password" class="input-xlarge">
	                </div>
	            </div>
	            <div class="control-group col-md-12 col-sm-12 col-xs-12"> 
			    	<div id="oauth-login-reset" class="col-md-6 col-sm-6 col-xs-12">
				        <a href="<@spring.url '/reset-password'/>"><@orcid.msg 'login.reset'/></a>&nbsp;&nbsp;
				    </div>
				    <div id="oauth-login-register" class="col-md-6 col-sm-6 col-xs-12">
				       	<a class="reg" ng-click="switchForm()"><@orcid.msg 'orcid.frontend.oauth.register'/></a>
			    	</div>
		    	</div>		
	            <div id="login-buttons">                     		            		               					
					<button class="btn btn-primary" name="authorize" value="<@orcid.msg 'confirm-oauth-access.Authorize'/>" ng-click="authorize()">
						<@orcid.msg 'confirm-oauth-access.Authorize' />
					</button>		                 	            
					<button class="btn btn-primary" name="deny" value="<@orcid.msg 'confirm-oauth-access.Deny'/>" ng-click="deny()">
						<@orcid.msg 'confirm-oauth-access.Deny' />
					</button>
	            </div>	                        
        	</div> 
        	<!-- REGISTER FORM -->
        	<div id="register" ng-show="showRegisterForm">
        		<div class="control-group col-md-12 col-sm-12 col-xs-12"> 			    	
					<p><@orcid.msg 'orcid.frontend.oauth.alread_have_account'/>&nbsp;<a class="reg" ng-click="switchForm()"><@orcid.msg 'orcid.frontend.oauth.alread_have_account.link.text'/></a>.</p>			    	
		    	</div>	
        		<div>
			        <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelfirstname")}
			        </label>
			        <div class="relative">			        	
			            <input name="givenNames" type="text" tabindex="1" class="input-xlarge" ng-model="register.givenNames.value" ng-model-onblur ng-change="serverValidate('GivenNames')"/>
			            <span class="required" ng-class="isValidClass(register.givenNames)">*</span>
						<div class="popover-help-container">
			                <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
			                <div id="name-help" class="popover bottom">
						        <div class="arrow"></div>
						        <div class="popover-content">
						            <p><@orcid.msg 'orcid.frontend.register.help.first_name'/></p>			             
									<p><@orcid.msg 'orcid.frontend.register.help.last_name'/></p>
									<p><@orcid.msg 'orcid.frontend.register.help.update_names'/></p>			            
									<a href="<@orcid.msg 'orcid.frontend.register.help.more_info.link.url'/>" target="_blank"><@orcid.msg 'orcid.frontend.register.help.more_info.link.text'/></a>
						        </div>                
						    </div>
			            </div>
						<span class="orcid-error" ng-show="register.givenNames.errors.length > 0">
							<div ng-repeat='error in register.givenNames.errors' ng-bind-html="error"></div>
			   			</span>
			        </div>
			    </div>				
				<div>
			        <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labellastname")}</label>
			        <div class="relative">
			            <input name="familyNames" type="text" tabindex="2" class="input-xlarge"  ng-model="register.familyNames.value" ng-model-onblur/>
			            <span class="orcid-error" ng-show="register.familyNames.errors.length > 0">
							<div ng-repeat='error in register.familyNames.errors' ng-bind-html="error"></div>
			   			</span>
			        </div>
			    </div>
			    <div>
			        <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelemail")}</label>
			        <div class="relative">
			            <input name="email" type="email" tabindex="3" class="input-xlarge" ng-model="register.email.value" ng-model-onblur ng-change="serverValidate('Email')" />
			            <span class="required" ng-class="isValidClass(register.email)">*</span>
			            <span class="orcid-error" ng-show="register.email.errors.length > 0">
							<div ng-repeat='error in register.email.errors' ng-bind-html="error"></div>
			   			</span>
			        </div>
			    </div>				
			    <div>
			        <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelreenteremail")}</label>
			        <div class="relative">
			            <input name="confirmedEmail" type="email" tabindex="4" class="input-xlarge" ng-model="register.emailConfirm.value" ng-model-onblur ng-change="serverValidate('EmailConfirm')" />
			            <span class="required" ng-class="isValidClass(register.emailConfirm)">*</span>
			            <span class="orcid-error" ng-show="register.emailConfirm.errors.length > 0">
							<div ng-repeat='error in register.emailConfirm.errors' ng-bind-html="error"></div>
			   			</span>
			        </div>
			    </div>				
			    <div class="control-group">
			        <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelpassword")}</label>
			        <div class="relative">
			            <input type="password" name="password" tabindex="5" class="input-xlarge" ng-model="register.password.value" ng-change="serverValidate('Password')"/>
			            <span class="required" ng-class="isValidClass(register.password)">*</span>
			   			<@orcid.passwordHelpPopup />
			            <span class="orcid-error" ng-show="register.password.errors.length > 0">
							<div ng-repeat='error in register.password.errors' ng-bind-html="error"></div>
			   			</span>
			        </div>
			    </div>
			    <div>
			        <label class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.labelconfirmpassword")}</label>
			        <div class="relative">
			            <input type="password" name="confirmPassword" tabindex="6" class="input-xlarge" ng-model="register.passwordConfirm.value" ng-change="serverValidate('PasswordConfirm')"/>
			            <span class="required" ng-class="isValidClass(register.passwordConfirm)">*</span>
			            <span class="orcid-error" ng-show="register.passwordConfirm.errors.length > 0">
							<div ng-repeat='error in register.passwordConfirm.errors' ng-bind-html="error"></div>
			   			</span>
			        </div>
			    </div>
				<div style="margin-bottom: 20px; margin-top: 10px;">
			        <label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault")}</label>
			    	<@orcid.privacyToggle 
			    	    angularModel="register.activitiesVisibilityDefault.visibility" 
			    	    questionClick="toggleClickPrivacyHelp('workPrivHelp')"
						clickedClassCheck="{'popover-help-container-show':privacyHelp['workPrivHelp']==true}" 
						publicClick="updateActivitiesVisibilityDefault('PUBLIC', $event)"
						limitedClick="updateActivitiesVisibilityDefault('LIMITED', $event)"
						privateClick="updateActivitiesVisibilityDefault('PRIVATE', $event)" />
			    </div>                    
			    <div style="margin-bottom: 15px;">
			        <div class="relative">
			            <label>${springMacroRequestContext.getMessage("claim.notificationemail")}</label>
			            <label class="checkbox">
			                <input type="checkbox" tabindex="7" name="sendOrcidChangeNotifications" ng-model="register.sendChangeNotifications.value"/>
			                ${springMacroRequestContext.getMessage("register.labelsendmenotifications")}
			            </label>
			            <label class="checkbox">
			                <input type="checkbox" tabindex="8" name="sendOrcidNews" ng-model="register.sendOrcidNews.value"/>
			                ${springMacroRequestContext.getMessage("register.labelsendinformation")}
			            </label>
			         </div>
				</div>
			    <div>
			        <div class="relative"  style="margin-bottom: 15px;">
			            <label>${springMacroRequestContext.getMessage("register.labelTermsofUse")} <span class="required"  ng-class="{'text-error':register.termsOfUse.value == false}">*</span></label>
			            <label class="checkbox" style="width: 100%">
			            <input type="checkbox" tabindex="9" name="acceptTermsAndConditions" ng-model="register.termsOfUse.value" ng-change="serverValidate('TermsOfUse')" />
			            ${springMacroRequestContext.getMessage("register.labelconsent")} <a href="${aboutUri}/footer/privacy-policy" target="_blank">${springMacroRequestContext.getMessage("register.labelprivacypolicy")}</a> ${springMacroRequestContext.getMessage("register.labeland")}  ${springMacroRequestContext.getMessage("common.termsandconditions1")}<a href="${aboutUri}/content/orcid-terms-use" target="_blank">${springMacroRequestContext.getMessage("common.termsandconditions2")}</a> ${springMacroRequestContext.getMessage("common.termsandconditions3")}</p>
			            </label>
			            <span class="orcid-error" ng-show="register.termsOfUse.errors.length > 0">
							<div ng-repeat='error in register.termsOfUse.errors' ng-bind-html="error"></div>
			   			</span>
			        </div>
				</div>   
			    <div id="register-buttons">                     		            		               					
					<button class="btn btn-primary" name="authorize" value="<@orcid.msg 'confirm-oauth-access.Authorize'/>" ng-click="registerAndAuthorize()">
						<@orcid.msg 'confirm-oauth-access.Authorize' />
					</button>		                 	            
					<button class="btn btn-primary" name="deny" value="<@orcid.msg 'confirm-oauth-access.Deny'/>" ng-click="registerAndDeny()">
						<@orcid.msg 'confirm-oauth-access.Deny' />
					</button>
	            </div> 
        	</div> 
		</#if>
	</div>	   
	
<script type="text/ng-template" id="duplicates">
	<div class="lightbox-container" id="duplicates-records">
		<div class="row margin-top-box">			
			<div class="col-md-6 col-sm-6 col-xs-12">
	     		<h4>${springMacroRequestContext.getMessage("duplicate_researcher.wefoundfollowingrecords")}
	     		${springMacroRequestContext.getMessage("duplicate_researcher.to_access.1")}<a href="<@spring.url "/signin" />" target="signin">${springMacroRequestContext.getMessage("duplicate_researcher.to_access.2")}</a>${springMacroRequestContext.getMessage("duplicate_researcher.to_access.3")}
	     		</h4>
     		</div>
     		<div class="col-md-6 col-sm-6 col-xs-12 right margin-top-box">
	     	    <button class="btn btn-primary" ng-click="postRegisterConfirm()">${springMacroRequestContext.getMessage("duplicate_researcher.btncontinuetoregistration")}</button>
			</div>
		</div>				
		<div class="row">
			<div class="col-sm-12">
				<div class="table-container">
					<table class="table">
						<thead>
							<tr>               				
			    				<th>${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
    							<th>${springMacroRequestContext.getMessage("duplicate_researcher.thEmail")}</th>
    							<th>${springMacroRequestContext.getMessage("duplicate_researcher.thgivennames")}</th>
    							<th>${springMacroRequestContext.getMessage("duplicate_researcher.thFamilyName")}</th>
	    						<th>${springMacroRequestContext.getMessage("duplicate_researcher.thInstitution")}</th>                				
							</tr>
						</thead>
						<tbody>
						 	<tr ng-repeat='dup in duplicates'>
					 			<td><a href="<@spring.url '/'/>{{dup.orcid}}" target="_blank">{{dup.orcid}}</a></td>
        						<td>{{dup.email}}</td>
        						<td>{{dup.givenNames}}</td>
        						<td>{{dup.familyNames}}</td>
        						<td>{{dup.institution}}</td>
    						</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>	
		<div class="row margin-top-box">
			<div class="col-md-12 col-sm-12 col-xs-12 right">
		    	<button class="btn btn-primary" ng-click="postRegisterConfirm()">${springMacroRequestContext.getMessage("duplicate_researcher.btncontinuetoregistration")}</button>
			</div>
		</div>
	</div>
</script>      
