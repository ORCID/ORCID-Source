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
<@public nav="admin">

<script type="text/ng-template" id="success-modal">
  <div style="padding:20px">
    <a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    <h1><@orcid.msg 'admin.success'/></h1>
    <p id="success-message">{{successMessage}}</p>  
    <div class="control-group">
    	<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
    </div>  
  </div>
</script>

<script type="text/ng-template" id="email-ids-modal">
	<div style="padding:20px">
		<h1><@orcid.msg 'admin.find_ids.results'/></h1>
		<div ng-show="emailIdsMap">
			<table class="table table-bordered table-hover">
				<tr>
					<td><strong><@orcid.msg 'admin.email'/></strong></td>
					<td><strong><@orcid.msg 'admin.orcid'/></strong></td>
				</tr>
				<tr ng-repeat="(email, orcid) in emailIdsMap">
					<td>{{email}}</td>
					<td>{{orcid}}&nbsp;(<@orcid.msg 'admin.switch.click.1'/>&nbsp;<a href="<@orcid.msg 'admin.switch.click.link'/>{{orcid}}"><@orcid.msg 'admin.switch.click.here'/></a>&nbsp;<@orcid.msg 'admin.switch.click.2'/>)</td>
				</tr>
			</table>
			<div class="controls save-btns pull-right bottom-margin-small">
				<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
			</div>
		</div>
		<div ng-show="!emailIdsMap">
			<span><@orcid.msg 'admin.find_ids.no_results'/></span>
		</div>
	</div>
</script>

<script type="text/ng-template" id="confirm-deprecation-modal">
  <div style="padding:20px">
    <a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    <h1><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm'/></h1>
    
    <div ng-show="errors.length">
		<span class="orcid-error" ng-repeat='error in errors' ng-bind-html="error"></span><br />
	</div>	    	    
    <div class="bottom-margin-small">
	    <p><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm.message.1'/></p>
	    <table border="0">
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.orcid'/></strong></td>
		    	<td>{{deprecatedAccount.orcid}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.name'/></strong></td>
		    	<td>{{deprecatedAccount.givenNames}}&nbsp;{{deprecatedAccount.familyName}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.email'/></strong></td>
		    	<td>{{deprecatedAccount.primaryEmail}}</td>
		    </tr>		    
	    </table>
	</div>
	<div class="bottom-margin-small">	    
	    <p><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm.message.2'/></p>
	    <table>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.orcid'/></strong></td>
		    	<td>{{primaryAccount.orcid}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.name'/></strong></td>
		    	<td>{{primaryAccount.givenNames}}&nbsp;{{primaryAccount.familyName}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.email'/></strong></td>
		    	<td>{{primaryAccount.primaryEmail}}</td>
		    </tr>	    
	    </table>
	</div>		   	
	<div class="control-group">
		<button class="btn btn-primary" id="bottom-deprecate-profile" ng-click="deprecateAccount()"><@orcid.msg 'admin.profile_deprecation.deprecate_account'/></button>
		<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel'/></a>
	</div>
  </div>
</script>

<script type="text/ng-template" id="confirm-deactivation-modal">
	<div style="padding:20px">
    	<a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    	<h1><@orcid.msg 'admin.profile_deactivation.confirm'/></h1>
		<div class="bottom-margin-small">
	    	<p><@orcid.msg 'admin.profile_deactivation.confirm.message'/></p>
	    	<table border="0">
		    	<tr>
			    	<td><strong><@orcid.msg 'admin.profile_deprecation.orcid'/></strong></td>
			    	<td>{{deactivatedAccount.orcid}}</td>
			    </tr>
		    	<tr>
			    	<td><strong><@orcid.msg 'admin.profile_deprecation.name'/></strong></td>
			    	<td>{{deactivatedAccount.givenNames}}&nbsp;{{deactivatedAccount.familyName}}</td>
			    </tr>
		    	<tr>
			    	<td><strong><@orcid.msg 'admin.profile_deprecation.email'/></strong></td>
			    	<td>{{deactivatedAccount.email}}</td>
			    </tr>		    
	    	</table>
		</div>
		<div class="control-group">			
			<button class="btn btn-primary" id="bottom-deactivate-profile" ng-click="deactivateAccount()"><@orcid.msg 'admin.profile_deactivation.deactivate_account'/></button>
			<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel'/></a>
		</div>
	</div>
</script>

<script type="text/ng-template" id="confirm-reactivation-modal">
	<div style="padding:20px">
    	<a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    	<h1><@orcid.msg 'admin.profile_reactivation.confirm'/></h1>
		<div class="bottom-margin-small">
	    	<span><@orcid.msg 'admin.profile_reactivation.confirm.message'/></span>
			<br />
			<span>{{orcidToReactivate}}</span>				    	
		</div>
		<div class="control-group">			
			<button class="btn btn-primary" id="bottom-deactivate-profile" ng-click="reactivateAccount()"><@orcid.msg 'admin.profile_reactivation.reactivate_account'/></button>
			<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel'/></a>
		</div>
	</div>
</script>

<!-- Confirm reset password -->
<script type="text/ng-template" id="confirm-reset-password">
	<div class="colorbox-content">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
    			<h1><@orcid.msg 'admin.reset_password.confirm_title'/></h1>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">					
				<div class="control-group">
    				<label class="relative"><@orcid.msg 'admin.reset_password.confirm.message'/> {{params.orcidOrEmail}}?</label>    				
  				</div>
				<div class="control-group">
					<button class="btn btn-primary" ng-click="resetPassword()"><@orcid.msg 'admin.reset_password.confirm.button'/></button>
					<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel'/></a>
				</div>
			</div>				
		</div>
	</div>
</script>

<!-- Confirm remove security question -->
<script type="text/ng-template" id="confirm-remove-security-question">
	<div class="colorbox-content">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
    			<h1><@orcid.msg 'admin.remove_security_question.confirm_title'/></h1>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">					
				<div class="control-group">
    				<label class="relative"><@orcid.msg 'admin.remove_security_question.confirm.message'/> {{orcidOrEmail}}?</label>    				
  				</div>
				<div class="control-group">
					<button class="btn btn-primary" ng-click="removeSecurityQuestion()"><@orcid.msg 'admin.remove_security_question.confirm.button'/></button>
					<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel'/></a>
				</div>
			</div>				
		</div>
	</div>
</script>



<#if invalidOrcid?? && invalidOrcid>
    <div class="alert alert-success">
        <strong><@spring.message "orcid.frontend.web.invalid_switch_orcid"/></strong>
    </div>
</#if>
<!-- Admin main Layout -->
<div class="row">
	<!-- Left menu bar -->	
	<div class="col-md-3 col-sm-12 col-xs-12 lhs padding-fix">
		<#include "includes/id_banner.ftl"/>
	</div>
	<!-- Right menu bar -->
	<div class="col-md-9 col-sm-12 col-xs-12 admin-options">	
		<!-- Switch user -->
		<a name="switch-user"></a>
		<div ng-controller="switchUserCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a  ng-show="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.switch_user' /></a>
				<a  ng-hide="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.switch_user' /></a>
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="switch_user_section" style="display:none;">
				<form action="./admin-actions/admin-switch-user" method="post">
					<div class="form-group">
						<label for="orcidOrEmail"><@orcid.msg 'admin.switch_user.orcid.label' /></label>
						<input type="text" id="orcidOrEmail" name="orcidOrEmail" placeholder="<@orcid.msg 'admin.switch_user.orcid.placeholder' />" class="input-xlarge" />
					</div>
					<div class="controls save-btns pull-left">
						<input type="submit" class="btn btn-primary" value="<@orcid.msg 'admin.switch_user.button'/>" />											
					</div>
				</form>
			</div>	
		</div>

		<!-- Find Ids by email -->
		<a name="find-ids"></a>
		<div ng-controller="findIdsCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a  ng-show="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.find_ids' /></a>
				<a  ng-hide="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.find_ids' /></a>
			</p>			  	
			<div class="collapsible bottom-margin-small admin-modal" id="find_ids_section" style="display:none;">
				<div class="form-group">
					<label for="emails"><@orcid.msg 'admin.find_ids.label' /></label>
					<input type="text" id="emails" ng-model="emails" placeholder="<@orcid.msg 'admin.find_ids.placeholder' />" class="input-xlarge" />
				</div>
				<div class="controls save-btns pull-left">
					<span id="find-ids" ng-click="findIds()" class="btn btn-primary"><@orcid.msg 'admin.find_ids.button'/></span>						
				</div>
			</div>	
		</div>
		
		<!-- Reset password -->
		<a name="reset-password"></a>
		<div ng-controller="resetPasswordCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a  ng-show="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.reset_password' /></a>
				<a  ng-hide="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.reset_password' /></a>
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="reset_password_section" style="display:none;">			
				<div class="form-group">
					<label for="orcid"><@orcid.msg 'admin.reset_password.orcid.label' /></label>
					<input type="text" id="orcid" ng-model="params.orcidOrEmail" placeholder="<@orcid.msg 'admin.reset_password.orcid.placeholder' />" class="input-xlarge" />
					<label for="password"><@orcid.msg 'admin.reset_password.password.label' /></label>
					<input type="text" id="password" ng-model="params.password" placeholder="<@orcid.msg 'admin.reset_password.password.placeholder' />" class="input-xlarge" />
					<a href ng-click="randomString()" class="glyphicon glyphicon-random blue"></a>									
					<div ng-show="result != ''">
						<span class="orcid-error" ng-bind-html="result"></span><br />
					</div>
				</div>
				<div class="controls save-btns pull-left">
					<span id="find-ids" ng-click="confirmResetPassword()" class="btn btn-primary"><@orcid.msg 'admin.reset_password.button'/></span>						
				</div>
			</div>
		</div>
		
		<!-- Verify email -->
		<a name="verify-email"></a>
		<div ng-controller="adminVerifyEmailCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a  ng-show="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.verify_email' /></a>
				<a  ng-hide="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.verify_email' /></a>
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="verify_email_section" style="display:none;">
				<div class="form-group">				
					<div ng-show="result">
						<span class="orcid-error" ng-bind-html="result"></span><br />
					</div>
					<label for="email"><@orcid.msg 'admin.verify_email.title' /></label>
					<input type="text" id="name" ng-model="email" placeholder="<@orcid.msg 'admin.verify_email.placeholder' />" class="input-xlarge" />																					
				</div>
				<div class="controls save-btns pull-left">
					<span id="verify-email" ng-click="verifyEmail()" class="btn btn-primary"><@orcid.msg 'admin.verify_email.btn'/></span>						
				</div>
			</div>
		</div>
		
		<!-- Admin delegates -->
		<a name="admin-delegates"></a>
		<div ng-controller="adminDelegatesCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a  ng-show="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.delegate' /></a>
				<a  ng-hide="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.delegate' /></a>
			</p>
			
			<div class="collapsible bottom-margin-small admin-modal" id="delegates_section" style="display:none;">
				<div ng-show="success">
					<span class="orcid-error" ng-bind-html="request.successMessage"></span>
				</div>
				<div ng-show="request.errors.length > 0">
					<span class="orcid-error" ng-repeat='error in request.errors' ng-bind-html="error"></span><br />
				</div>
				<!-- Managed -->
				<div class="form-group">
					<label for="managed"><@orcid.msg 'admin.delegate.managed.label' /></label>
					<input type="text" id="managed" placeholder="<@orcid.msg 'admin.delegate.managed.placeholder' />" class="input-xlarge" ng-model="request.managed.value" ng-change="checkClaimedStatus('managed')">				
					<a href class="glyphicon glyphicon-ok green" ng-show="managed_verified"></a>					
					<div id="invalid-managed" ng-show="request.managed.errors.length > 0" ng-cloak>
						<span class="orcid-error" ng-repeat='error in request.managed.errors' ng-bind-html="error"></span><br />
					</div>							
				</div>				
				<!-- Trusted -->
				<div class="form-group">
					<label for="trusted"><@orcid.msg 'admin.delegate.trusted.label' /></label>
					<input type="text" id="trusted" placeholder="<@orcid.msg 'admin.delegate.trusted.placeholder' />" class="input-xlarge" ng-model="request.trusted.value" ng-change="checkClaimedStatus('trusted')">				
					<a href class="glyphicon glyphicon-ok green" ng-show="trusted_verified"></a>					
					<div id="invalid-trusted" ng-show="request.trusted.errors.length > 0" ng-cloak>
						<span class="orcid-error" ng-repeat='error in request.trusted.errors' ng-bind-html="error"></span><br />
					</div>							
				</div>
				<!-- Buttons -->
				<div class="controls save-btns pull-left">
		    		<span id="bottom-confirm-delegate-profile" ng-click="confirmDelegatesProcess()" class="btn btn-primary"><@orcid.msg 'admin.delegate.button'/></span>
				</div>
			</div>
		</div>
		
		<!-- Remove security question -->
		<a name="remove-security-question"></a>
		<div ng-controller="removeSecQuestionCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a  ng-show="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.remove_security_question' /></a>
				<a  ng-hide="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.remove_security_question' /></a>
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="remove_security_question_section" style="display:none;">
				<div class="form-group">
					<label for="orcid"><@orcid.msg 'admin.remove_security_question.orcid.label' /></label>
					<input type="text" id="orcid" ng-model="orcidOrEmail" placeholder="<@orcid.msg 'admin.remove_security_question.orcid.placeholder' />" class="input-xlarge" />					
					<div ng-show="result != ''">
						<span class="orcid-error" ng-bind-html="result"></span><br />
					</div>
				</div>
				<div class="controls save-btns pull-left">
					<span id="find-ids" ng-click="confirmRemoveSecurityQuestion()" class="btn btn-primary"><@orcid.msg 'admin.remove_security_question.button'/></span>						
				</div>
			</div>
		</div>							
		
		<!-- Deprecate Profile -->
		<a name="deprecate-profile"></a>
		<div ng-controller="profileDeprecationCtrl" class="workspace-accordion-item" ng-cloak>
			<p>				
				<a ng-hide="showModal" ng-click="toggleDeprecationModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.profile_deprecation' /></a>
				<a ng-show="showModal" ng-click="toggleDeprecationModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.profile_deprecation' /></a>
			</p>		
			<div class="collapsible bottom-margin-small admin-modal" id="deprecation_modal" style="display:none;">		    	
				<div class="form-group">
					<label for="deprecated_orcid"><@orcid.msg 'admin.profile_deprecation.to_deprecate' /></label>
					<input type="text" id="deprecated_orcid" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.account_to_deprecate' />" class="form-control" ng-model="deprecatedAccount.orcid" ng-change="findAccountDetails('deprecated')">
					<a href class="glyphicon glyphicon-ok green" ng-show="deprecated_verified"></a>					
					<div id="invalid-regex-deprecated" ng-show="invalid_regex_deprecated" ng-cloak>
						<span class="orcid-error"><@orcid.msg 'admin.profile_deprecation.errors.invalid_regex' /></span>
					</div>
					<div ng-show="deprecatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in deprecatedAccount.errors' ng-bind-html="error"></span>	
					</div>
				</div>
				<div class="form-group">
					<label for="deprecated_orcid"><@orcid.msg 'admin.profile_deprecation.primary' /></label>
					<input type="text" id="primary_orcid" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.primary_account' />" class="form-control" ng-model="primaryAccount.orcid" ng-change="findAccountDetails('primary')">				
					<a href class="glyphicon glyphicon-ok green" ng-show="primary_verified"></a>					
					<div id="invalid-regex-primary" ng-show="invalid_regex_primary" ng-cloak>
						<span class="orcid-error"><@orcid.msg 'admin.profile_deprecation.errors.invalid_regex' /></span><br />
					</div>
					<div ng-show="primaryAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in primaryAccount.errors' ng-bind-html="error"></span><br />
					</div>			
				</div>				
				<div class="controls save-btns pull-left">
		    		<span id="bottom-confirm-deprecate-profile" ng-click="confirmDeprecateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deprecation.deprecate_account'/></span>
				</div>
			</div>
		</div>
				
		<!-- Deactivate Profile -->
		<a name="deactivate-profile"></a>			  
		<div ng-controller="profileDeactivationAndReactivationCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a  ng-show="showDeactivateModal" ng-click="toggleDeactivationModal()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.profile_deactivation' /></a>
				<a  ng-hide="showDeactivateModal" ng-click="toggleDeactivationModal()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.profile_deactivation' /></a>
			</p>			  	
			<div class="collapsible bottom-margin-small admin-modal" id="deactivation_modal" style="display:none;">					    		
	    		<div class="form-group">
					<label for="orcid_to_deactivate"><@orcid.msg 'admin.profile_deactivation.to_deactivate' /></label>
					<input type="text" id="orcid_to_deactivate" ng-model="orcidToDeactivate" placeholder="<@orcid.msg 'admin.profile_deactivation.placeholder.to_deactivate' />" class="form-control" />					
					<div ng-show="deactivatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in deactivatedAccount.errors' ng-bind-html="error"></span><br />
					</div>		
				</div>
				<div class="controls save-btns pull-left">
					<span id="bottom-confirm-deactivate-profile" ng-click="confirmDeactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deactivation.deactivate_account'/></span>
				</div>
			</div>
		</div>
		
		<!-- Reactivate Profile -->
		<a name="reactivate-profile"></a>
		<div ng-controller="profileDeactivationAndReactivationCtrl" class="workspace-accordion-item" ng-cloak>
			<p>				
				<a ng-show="showReactivateModal" ng-click="toggleReactivationModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.profile_reactivation' /></a>
				<a ng-hide="showReactivateModal" ng-click="toggleReactivationModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.profile_reactivation' /></a>
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="reactivation_modal" style="display:none;">					    		
	    		<div class="form-group">
					<label for="orcid_to_reactivate"><@orcid.msg 'admin.profile_reactivation.to_reactivate' /></label>
					<input type="text" id="orcid_to_reactivate" ng-model="orcidToReactivate" placeholder="<@orcid.msg 'admin.profile_reactivation.placeholder.to_reactivate' />" class="form-control" />
					<div ng-show="reactivatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in reactivatedAccount.errors' ng-bind-html="error"></span><br />
					</div>
				</div>
				<div class="controls save-btns pull-left">
					<span id="bottom-confirm-reactivate-profile" ng-click="confirmReactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_reactivation.reactivate_account'/></span>		
				</div>
			</div>
		</div>
						
	</div>
</div>

<script type="text/ng-template" id="confirm-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3><@orcid.msg 'admin.edit_client.confirm_update.title' /></h3>	
				<p><@orcid.msg 'admin.edit_client.confirm_update.text' /></p>			
				<p><strong>{{client.displayName.value}}</strong></p>						
    			<div class="btn btn-danger" ng-click="updateClient()">
    				<@orcid.msg 'admin.edit_client.btn.update' />
    			</div>
    			<a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
			</div>
		</div>
    </div>
</script>

</@public >