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
	<div class="col-md-6 col-sm-12 oauth-margin-top-bottom-box" ng-controller="OauthAuthorizationController">
		<!-- Freemarker and GA variables -->
		<#assign user_id = "">			
		<#if userId??>
			<#assign user_id = userId>
        </#if>
		<#assign authOnClick = "">			       
	    <#assign denyOnClick = " orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Authorize_Deny', 'OAuth " + client_group_name?js_string + " - " + client_name?js_string + "']);">	    	
		<!-- /Freemarker and GA variables -->

		<div class="app-client-name" ng-init="initGroupAndClientName('${client_group_name}','${client_name}')">
			<h3 ng-click="toggleClientDescription()">${client_name}
				<a class="glyphicon glyphicon-question-sign oauth-question-sign"></a>
			</h3>
		</div>
		<div class="app-client-description">
			<p ng-show="showClientDescription">
				<span class="uppercase gray-bold-about"><@orcid.msg 'oauth_sign_in.about'/></span> ${client_description}
			</p>
		</div>
		<div>
			<p><@orcid.msg 'orcid.frontend.oauth.have_asked'/></p>
		</div>
		<ul class="oauth-scopes">
			<#list scopes as scope>
				<li>
					<#assign authOnClick = authOnClick + " orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'Authorize_" + scope.name()?replace("ORCID_", "") + "', 'OAuth " + client_group_name?js_string + " - " + client_name?js_string + "']);">												
					<#if scope.value()?ends_with("/create")>
						<span class="mini-icon glyphicon glyphicon-cloud-download green"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
					<#elseif scope.value()?ends_with("/update")>
						<span class="mini-icon glyphicon glyphicon-refresh green"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
					<#elseif scope.value()?ends_with("/read-limited")>
						<span class="mini-icon glyphicon glyphicon-eye-open green"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
					<#else>
						<span class="mini-orcid-icon oauth-bullet"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
					</#if>											
				</li>
        		</#list>				
		</ul>	
		<div>
			<p><@orcid.msg 'orcid.frontend.web.oauth_is_secure'/>.&nbsp;<a href="${aboutUri}/footer/privacy-policy" target="_blank"><@orcid.msg 'public-layout.privacy_policy'/></a>.</p>
		</div>
		 
		<!-- LOGIN FORM -->			
		<div id="login" class="oauth-login-form" ng-show="!showRegisterForm" ng-init="loadAndInitLoginForm('${scopesString}','${redirect_uri}','${client_id}','${response_type}', '${user_id}')">            			                        	
			  <div class="row">
				  <div class="form-group has-feedback">
				    <label for="userId" class="col-sm-3 control-label"><@orcid.msg 'oauth_sign_in.labelemailorID'/></label>
				    <div class="col-sm-9">
				      <input type="text" name="userId" id="userId" ng-model="authorizationForm.userName.value" placeholder="Email or iD" class="form-control" >
				      <span class="glyphicon glyphicon-asterisk form-control-feedback-oauth"></span>
				    </div>
				  </div>
			 </div>
			 <div class="row">  
			  <div class="form-group has-feedback">
			    <label for="password" class="col-sm-3 control-label"><@orcid.msg 'oauth_sign_in.labelpassword'/></label>
			    <div class="col-sm-9">
			      <input type="password" id="password" ng-model="authorizationForm.password.value" name="password" placeholder="Password" class="form-control">
			      <span class="glyphicon glyphicon-asterisk form-control-feedback-oauth"></span>
			    </div>
			  </div>
        	</div>
        	<div class="row">	        		
        		<div class="col-sm-offset-3 col-sm-9">
		        	<span class="orcid-error" ng-show="authorizationForm.errors.length > 0">
						<div ng-repeat='error in authorizationForm.errors' ng-bind-html="error"></div>
				   	</span>			   		
			   	</div>
        	</div>
        	
            <div class="row">
	            <div class="control-group"> 
			    	<div id="oauth-login-reset" class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-3 col-xs-12">
				        <a href="<@spring.url '/reset-password'/>"><@orcid.msg 'login.reset'/></a>
				    </div>
				    <div id="oauth-login-register" class="col-md-offset-3 col-md-9 col-sm-6 col-xs-12">
				       	<a class="reg" id="in-login-switch-form" ng-click="switchForm()"><@orcid.msg 'orcid.frontend.oauth.register'/></a>
			    	</div>
		    	</div>
	    	</div>
	    	<div class="row">
                <div class="col-md-12">                		            		               					
					<button class="btn btn-primary pull-right" id="authorize-button" name="authorize" value="<@orcid.msg 'confirm-oauth-access.Authorize'/>" ng-click="loginAndAuthorize()" onclick="${authOnClick} return false;">
						<@orcid.msg 'confirm-oauth-access.Authorize' />
					</button>
					<a class="oauth_deny_link pull-right" name="deny" value="<@orcid.msg 'confirm-oauth-access.Deny'/>" ng-click="loginAndDeny()" onclick="${denyOnClick} return false;">
						<@orcid.msg 'confirm-oauth-access.Deny' />
					</a>		                 	  
				</div>  
            </div>                      
       	</div>         	        	
       	
       	<!-- REGISTER FORM -->
       	<div id="register" class="oauth-registration" ng-show="showRegisterForm" ng-init="loadAndInitRegistrationForm('${scopesString}','${redirect_uri}','${client_id}','${response_type}')">
       		<div class="control-group col-md-12 col-sm-12 col-xs-12"> 			    	
				<p class="pull-right"><@orcid.msg 'orcid.frontend.oauth.alread_have_account'/>&nbsp;<a class="reg" ng-click="switchForm()" id="in-register-switch-form"><@orcid.msg 'orcid.frontend.oauth.alread_have_account.link.text'/></a>.</p>			    	
	    	</div>
	    	<!-- First name -->
       		<div class="form-group">
		        <label for="givelNames" class="col-sm-3 control-label"><@orcid.msg 'oauth_sign_up.labelfirstname'/></label>
		        <div class="col-sm-9 bottomBuffer">			        	
		            <input name="givenNames" type="text" tabindex="1" class="" ng-model="registrationForm.givenNames.value" ng-model-onblur ng-change="serverValidate('GivenNames')"/>									        
		        	<span class="required" ng-class="isValidClass(registrationForm.givenNames)">*</span>						
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
		            <span class="orcid-error" ng-show="registrationForm.givenNames.errors.length > 0">
						<div ng-repeat='error in registrationForm.givenNames.errors' ng-bind-html="error"></div>
		   			</span>
		        </div>
		    </div>
		    <!-- Last name -->
		    <div class="form-group">
				<div>
			        <label class="col-sm-3 control-label"><@orcid.msg 'oauth_sign_up.labellastname'/></label>
			        <div class="col-sm-9 bottomBuffer">
			            <input name="familyNames" type="text" tabindex="2" class=""  ng-model="registrationForm.familyNames.value" ng-model-onblur/>
			            <span class="orcid-error" ng-show="registrationForm.familyNames.errors.length > 0">
							<div ng-repeat='error in registrationForm.familyNames.errors' ng-bind-html="error"></div>
			   			</span>
			        </div>				         
			     </div>
		    </div>
		    			    
		    <div class="form-group">
		        <label class="col-sm-3 control-label"><@orcid.msg 'oauth_sign_up.labelemail'/></label>
		        <div class="col-sm-9 bottomBuffer">
		            <input name="email" type="email" tabindex="3" class="" ng-model="registrationForm.email.value" ng-model-onblur ng-change="serverValidate('Email')" />
		            <span class="required" ng-class="isValidClass(registrationForm.email)">*</span>			            
		            <span class="orcid-error" ng-show="registrationForm.email.errors.length > 0">
						<div ng-repeat='error in registrationForm.email.errors' ng-bind-html="error" compile="html"></div>
		   			</span>
		        </div>			       
		    </div>				
		    
		    <div class="form-group">
		        <label class="col-sm-3 control-label"><@orcid.msg 'oauth_sign_up.labelreenteremail'/></label>
		        <div class="col-sm-9 bottomBuffer">
		            <input name="confirmedEmail" type="email" tabindex="4" class="" ng-model="registrationForm.emailConfirm.value" ng-model-onblur ng-change="serverValidate('EmailConfirm')" />
		            <span class="required" ng-class="isValidClass(registrationForm.emailConfirm)">*</span>			            
		            <span class="orcid-error" ng-show="registrationForm.emailConfirm.errors.length > 0">
						<div ng-repeat='error in registrationForm.emailConfirm.errors' ng-bind-html="error"></div>
		   			</span>
		        </div>			        
		    </div>				
		    
		    <div class="form-group">
		        <label class="col-sm-3 control-label"><@orcid.msg 'oauth_sign_up.labelpassword'/></label>
		        <div class="col-sm-9 bottomBuffer">
		            <input type="password" name="password" tabindex="5" class="" ng-model="registrationForm.password.value" ng-change="serverValidate('Password')"/>
		            <span class="required" ng-class="isValidClass(registrationForm.password)">*</span>
		        	<@orcid.passwordHelpPopup />
		            <span class="orcid-error" ng-show="registrationForm.password.errors.length > 0">
						<div ng-repeat='error in registrationForm.password.errors' ng-bind-html="error"></div>
		   			</span>
		        </div>			        
		    </div>
		    
		    <div class="form-group">
		        <label class="col-sm-3 control-label"><@orcid.msg 'password_one_time_reset.labelconfirmpassword'/></label>
		        <div class="col-sm-9 bottomBuffer">
		            <input type="password" name="confirmPassword" tabindex="6" class="" ng-model="registrationForm.passwordConfirm.value" ng-change="serverValidate('PasswordConfirm')"/>
		            <span class="required" ng-class="isValidClass(registrationForm.passwordConfirm)">*</span>			            
		            <span class="orcid-error" ng-show="registrationForm.passwordConfirm.errors.length > 0">
						<div ng-repeat='error in registrationForm.passwordConfirm.errors' ng-bind-html="error"></div>
		   			</span>
		        </div>			        
		    </div>
			
			<div style="margin-bottom: 20px; margin-top: 10px;">
		        <label class="privacy-toggle-lbl"><@orcid.msg 'privacy_preferences.activitiesVisibilityDefault'/></label>
		    	<@orcid.privacyToggle 
		    	    angularModel="registrationForm.activitiesVisibilityDefault.visibility" 
		    	    questionClick="toggleClickPrivacyHelp('workPrivHelp')"
					clickedClassCheck="{'popover-help-container-show':privacyHelp['workPrivHelp']==true}" 
					publicClick="updateActivitiesVisibilityDefault('PUBLIC', $event)"
					limitedClick="updateActivitiesVisibilityDefault('LIMITED', $event)"
					privateClick="updateActivitiesVisibilityDefault('PRIVATE', $event)" />
		    </div>                    
		   
		    <div style="margin-bottom: 15px;">
		        <div class="relative">
		            <label><@orcid.msg 'claim.notificationemail'/></label>
		            <label class="checkbox">
		                <input type="checkbox" tabindex="7" name="sendOrcidChangeNotifications" ng-model="registrationForm.sendChangeNotifications.value"/>
		                <@orcid.msg 'register.labelsendmenotifications'/>
		            </label>
		            <label class="checkbox">
		                <input type="checkbox" tabindex="8" name="sendOrcidNews" ng-model="registrationForm.sendOrcidNews.value"/>
		                <@orcid.msg 'register.labelsendinformation'/>
		            </label>
		         </div>
			</div>			   
		    
	        <div style="margin-bottom: 15px;">
		        <div class="row">
		            <label for="termsConditions" class="col-sm-12">
		            	<@orcid.msg 'register.labelTermsofUse'/>
		            	<span class="required"  ng-class="{'text-error':register.termsOfUse.value == false}">*</span>
		            </label>
		            <div class="col-sm-12">			            
			            <input type="checkbox" name="termsConditions" tabindex="9" name="acceptTermsAndConditions" ng-model="registrationForm.termsOfUse.value" ng-change="serverValidate('TermsOfUse')" />
			            <@orcid.msg 'register.labelconsent'/> <a href="${aboutUri}/footer/privacy-policy" target="_blank"><@orcid.msg 'register.labelprivacypolicy'/></a>&nbsp;<@orcid.msg 'register.labeland'/>&nbsp;<@orcid.msg 'common.termsandconditions1'/><a href="${aboutUri}/content/orcid-terms-use" target="_blank"><@orcid.msg 'common.termsandconditions2'/></a>&nbsp;<@orcid.msg 'common.termsandconditions3'/></p>			            
			            <span class="orcid-error" ng-show="registrationForm.termsOfUse.errors.length > 0">
							<div ng-repeat='error in registrationForm.termsOfUse.errors' ng-bind-html="error"></div>
			   			</span>
		   			</div>
		   		</div>
	        </div>				   
		   
		    <div id="register-buttons">                     		            		               					
				<button class="btn btn-primary" name="authorize" value="<@orcid.msg 'confirm-oauth-access.Authorize'/>" ng-click="registerAndAuthorize()" onclick="${authOnClick} return false;">
					<@orcid.msg 'confirm-oauth-access.Authorize' />
				</button>		                 	            
				<a class="oauth_deny_link" name="deny" value="<@orcid.msg 'confirm-oauth-access.Deny'/>" ng-click="registerAndDeny()" onclick="${denyOnClick} return false;">
					<@orcid.msg 'confirm-oauth-access.Deny' />
				</a>
            </div> 
       	</div>
	</div>

<script type="text/ng-template" id="duplicates">
	<div class="lightbox-container" id="duplicates-records">
		<div class="row margin-top-box">			
			<div class="col-md-6 col-sm-6 col-xs-12">
	     		<h4><@orcid.msg 'duplicate_researcher.wefoundfollowingrecords'/>
	     		<@orcid.msg 'duplicate_researcher.to_access.1'/><a href="<@spring.url "/signin" />" target="signin"><@orcid.msg 'duplicate_researcher.to_access.2'/></a><@orcid.msg 'duplicate_researcher.to_access.3'/>
	     		</h4>
     		</div>
     		<div class="col-md-6 col-sm-6 col-xs-12 right margin-top-box">
	     	    <button class="btn btn-primary" ng-click="postRegisterConfirm()"><@orcid.msg 'duplicate_researcher.btncontinuetoregistration'/></button>
			</div>
		</div>				
		<div class="row">
			<div class="col-sm-12">
				<div class="table-container">
					<table class="table">
						<thead>
							<tr>               				
			    				<th><@orcid.msg 'search_results.thORCIDID'/></th>
    							<th><@orcid.msg 'duplicate_researcher.thEmail'/></th>
    							<th><@orcid.msg 'duplicate_researcher.thgivennames'/></th>
    							<th><@orcid.msg 'duplicate_researcher.thFamilyName'/></th>
	    						<th><@orcid.msg 'duplicate_researcher.thInstitution'/></th>                				
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
		    	<button class="btn btn-primary" ng-click="postRegisterConfirm()"><@orcid.msg 'duplicate_researcher.btncontinuetoregistration'/></button>
			</div>
		</div>
	</div>
</script>      	