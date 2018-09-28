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

<script type="text/ng-template" id="switch-error-modal">
	<div style="padding:20px">
		<h1><@orcid.msg 'admin.switch_user'/></h1>
		<div ng-show="orcidMap">
			{{orcidMap.errorMessg}}
			<div class="controls save-btns pull-right bottom-margin-small">
				<a class="btn btn-primary" href="<@orcid.msg 'admin.switch.click.link'/>{{orcidMap.orcid}}"><@orcid.msg 'admin.switch_anyway.button'/></a>&nbsp;
				<span ng-click="closeModal()" class="btn btn-primary"><@orcid.msg 'freemarker.btnclose'/></span>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="switch-imvalid-modal">
	<div style="padding:20px">
		<h1><@orcid.msg 'admin.switch_user'/></h1>
		<div>
			<@spring.message "orcid.frontend.web.invalid_switch_orcid"/>
			<div class="controls save-btns pull-right bottom-margin-small">
				<span ng-click="closeModal()" class="btn btn-primary"><@orcid.msg 'freemarker.btnclose'/></span>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="email-ids-modal">
	<div style="padding:20px">
		<h1><@orcid.msg 'admin.find_ids.results'/></h1>
		<div ng-show="profileList">
			<table class="table table-bordered table-hover">
				<tr>
					<td><strong><@orcid.msg 'admin.email'/></strong></td>
					<td><strong><@orcid.msg 'admin.orcid'/></strong></td>
					<td><strong><@orcid.msg 'admin.account_status'/></strong></td>
				</tr>
				<tr ng-repeat="profile in profileList">
					<td>{{profile.email}}</td>
					<td><a href="<@orcid.msg 'admin.public_view.click.link'/>{{profile.orcid}}" target="profile.orcid">{{profile.orcid}}</a>&nbsp;(<@orcid.msg 'admin.switch.click.1'/>&nbsp;<a href="<@orcid.msg 'admin.switch.click.link'/>{{profile.orcid}}"><@orcid.msg 'admin.switch.click.here'/></a>&nbsp;<@orcid.msg 'admin.switch.click.2'/>)</td>
					<td>{{profile.status}}</td>
				</tr>
			</table>
			<div class="controls save-btns pull-right bottom-margin-small">
				<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
			</div>
		</div>
		<div ng-show="!profileList">
			<span><@orcid.msg 'admin.find_ids.no_results'/></span>
		</div>
	</div>
</script>

<script type="text/ng-template" id="lookup-email-ids-modal">
	<div style="padding:20px">
		<h1><@orcid.msg 'admin.lookup_id_email.results'/></h1>
		<div ng-show="result">
			<textarea style="height:100px; width: 500px; resize: none;" readonly="readonly">{{result}}</textarea>
			<div class="controls save-btns pull-right bottom-margin-small">
				<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
			</div>
		</div>
		<div ng-show="!result.length">
			<span><@orcid.msg 'admin.lookup_id_email.no_results'/></span>
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

<!-- Admin main Layout -->
<div class="row">
	<!-- Left menu bar -->	
	<div class="col-md-3 col-sm-12 col-xs-12 lhs padding-fix">
		<#include "includes/id_banner.ftl"/>
	</div>
	<!-- Right menu bar -->
	<div class="col-md-9 col-sm-12 col-xs-12 admin-options">	
		
		
		
			
		<!-- Lock Profile -->			
		<a name="lock-profile"></a>
		<div id="lockProfileDiv" ng-controller="profileLockingCtrl" class="workspace-accordion-item" ng-cloak>
			<p>				
				<a ng-show="showLockModal" ng-click="toggleLockModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.lock_profile' /></a>
				<a ng-hide="showLockModal" ng-click="toggleLockModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.lock_profile' /></a>
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="lock_modal" style="display:none;">
				<div class="alert alert-success" ng-show="result.lockSuccessfulList.length || result.notFoundList.length || result.alreadyLockedList.length || result.reviewedList.length" style="overflow-x:auto;">
	    			<div ng-show="result.lockSuccessfulList.length"><@spring.message "admin.profile_lock.lock_success"/>
	    				<br>{{result.lockSuccessfulList}}
	    			</div>
	    			<div ng-show="result.alreadyLockedList.length"><br><@spring.message "admin.profile_lock.already_locked"/>
	    				<br>{{result.alreadyLockedList}}
	    			</div>
	    			<div ng-show="result.reviewedList.length"><br><@spring.message "admin.profile_lock.reviewed"/>
	    				<br>{{result.reviewedList}}
	    			</div>
	    			<div ng-show="result.notFoundList.length"><br><@spring.message "admin.profile_lock.not_found"/>
	    				<br>{{result.notFoundList}}
					</div>
				</div>					    		
		    	<div class="control-group">
					<label for="orcid_to_lock"><@orcid.msg 'admin.lock_profile.orcid_ids_or_emails' /></label>
					<div class="controls">
						<textarea id="orcid_to_lock" ng-model="orcidToLock" class="input-xlarge one-per-line" placeholder="<@orcid.msg 'admin.lock_profile.orcid_ids_or_emails' />" ></textarea>
						<select ng-model="lockReason">
							<option ng-repeat="reason in lockReasons" value="{{reason}}">{{reason}}</option>
        				</select>
        				<textarea id="lock_reason_description" ng-model="description" class="input-xlarge one-per-line" placeholder="<@orcid.msg 'admin.lock_profile.lock_reason_optional_description' />" ></textarea>
					</div>
					<span id="bottom-confirm-lock-profile" ng-click="lockAccount()" class="btn btn-primary"><@orcid.msg 'admin.lock_profile.btn.lock'/></span>		
				</div>
			</div>
		</div>
							
		<!-- Unlock Profile -->			
		<a name="unlock-profile"></a>
		<div id="unlockProfileDiv" ng-controller="profileLockingCtrl" class="workspace-accordion-item" ng-cloak>
			<p>				
				<a ng-show="showUnlockModal" ng-click="toggleUnlockModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.unlock_profile' /></a>
				<a ng-hide="showUnlockModal" ng-click="toggleUnlockModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.unlock_profile' /></a>
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="unlock_modal" style="display:none;">
				<div class="alert alert-success" ng-show="result.unlockSuccessfulList.length || result.notFoundList.length || result.alreadyUnlockedList.length" style="overflow-x:auto;">
	    			<div ng-show="result.unlockSuccessfulList.length"><@spring.message "admin.profile_unlock.unlock_success"/>
	    				<br>{{result.unlockSuccessfulList}}
	    			</div>
	    			<div ng-show="result.alreadyUnlockedList.length"><br><@spring.message "admin.profile_unlock.already_unlocked"/>
	    				<br>{{result.alreadyUnlockedList}}
	    			</div>
	    			<div ng-show="result.notFoundList.length"><br><@spring.message "admin.profile_unlock.not_found"/>
	    				<br>{{result.notFoundList}}
					</div>
				</div>						    		
		    	<div class="form-group">
		    		<p ng-show="message != ''">{{message}}</p>
					<label for="orcid_to_unlock"><@orcid.msg 'admin.lock_profile.orcid_ids_or_emails' /></label>
					<textarea id="orcid_to_unlock" ng-model="orcidToUnlock" class="input-xlarge one-per-line" placeholder="<@orcid.msg 'admin.lock_profile.orcid_ids_or_emails' />" ></textarea>
					<div ng-show="profileDetails.errors.length">
						<span class="orcid-error" ng-repeat="error in profileDetails.errors" ng-bind-html="error"></span><br />
					</div>
				</div>
				<div class="controls save-btns pull-left">
					<span id="bottom-confirm-unlock-profile" ng-click="unlockAccount()" class="btn btn-primary"><@orcid.msg 'admin.unlock_profile.btn.unlock'/></span>		
				</div>
			</div>
		</div>
		
		<!-- Review Profile -->			
		<a name="review-profile"></a>
		<div ng-controller="profileReviewCtrl" class="workspace-accordion-item" ng-cloak>
			<p>				
				<a ng-show="showReviewModal" ng-click="toggleReviewModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.review_profile' /></a>
				<a ng-hide="showReviewModal" ng-click="toggleReviewModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.review_profile' /></a>
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="review_modal" style="display:none;">		
				<div class="alert alert-success" ng-show="result.reviewSuccessfulList.length || result.notFoundList.length || result.alreadyReviewedList.length" style="overflow-x:auto;">
	    			<div ng-show="result.reviewSuccessfulList.length"><@spring.message "admin.profile_review.review_success"/>
	    				<br>{{result.reviewSuccessfulList}}
	    			</div>
	    			<div ng-show="result.alreadyReviewedList.length"><br><@spring.message "admin.profile_review.already_reviewed"/>
	    				<br>{{result.alreadyReviewedList}}
	    			</div>
	    			<div ng-show="result.notFoundList.length"><br><@spring.message "admin.profile_review.not_found"/>
	    				<br>{{result.notFoundList}}
					</div>
				</div>				    		
		    	<div class="form-group">
		    		<p ng-show="message != ''">{{message}}</p>
					<label for="orcid_to_review"><@orcid.msg 'admin.review_profile.orcid_ids_or_emails' /></label>
					<textarea id="orcid_to_review" ng-model="orcidToReview" class="input-xlarge one-per-line" placeholder="<@orcid.msg 'admin.review_profile.orcid_ids_or_emails' />" ></textarea>
					<div ng-show="profileDetails.errors.length">
						<span class="orcid-error" ng-repeat="error in profileDetails.errors" ng-bind-html="error"></span><br />
					</div>
				</div>
				<div class="controls save-btns pull-left">
					<span id="bottom-confirm-review-profile" ng-click="reviewAccount()" class="btn btn-primary"><@orcid.msg 'admin.review_profile.btn.review'/></span>		
				</div>
			</div>
		</div>
							
		<!-- Un Review Profile -->			
		<a name="unreview-profile"></a>
		<div ng-controller="profileReviewCtrl" class="workspace-accordion-item" ng-cloak>
			<p>				
				<a ng-show="showUnreviewModal" ng-click="toggleUnreviewModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.unreview_profile' /></a>
				<a ng-hide="showUnreviewModal" ng-click="toggleUnreviewModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.unreview_profile' /></a>
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="unreview_modal" style="display:none;">	
				<div class="alert alert-success" ng-show="result.unreviewSuccessfulList.length || result.notFoundList.length || result.alreadyUnreviewedList.length" style="overflow-x:auto;">
	    			<div ng-show="result.unreviewSuccessfulList.length"><@spring.message "admin.profile_unreview.unreview_success"/>
	    				<br>{{result.unreviewSuccessfulList}}
	    			</div>
	    			<div ng-show="result.alreadyUnreviewedList.length"><br><@spring.message "admin.profile_unreview.already_unreviewed"/>
	    				<br>{{result.alreadyUnreviewedList}}
	    			</div>
	    			<div ng-show="result.notFoundList.length"><br><@spring.message "admin.profile_unreview.not_found"/>
	    				<br>{{result.notFoundList}}
					</div>
				</div>					    		
		    	<div class="form-group">
		    		<p ng-show="message != ''">{{message}}</p>
					<label for="orcid_to_unreview"><@orcid.msg 'admin.review_profile.orcid_ids_or_emails' /></label>
					<textarea id="orcid_to_unreview" ng-model="orcidToUnreview" class="input-xlarge one-per-line" placeholder="<@orcid.msg 'admin.review_profile.orcid_ids_or_emails' />" ></textarea>
					<div ng-show="profileDetails.errors.length">
						<span class="orcid-error" ng-repeat="error in profileDetails.errors" ng-bind-html="error"></span><br />
					</div>
				</div>
				<div class="controls save-btns pull-left">
					<span id="bottom-confirm-unreview-profile" ng-click="unreviewAccount()" class="btn btn-primary"><@orcid.msg 'admin.unreview_profile.btn.unreview'/></span>		
				</div>
			</div>
		</div>
		
		<!-- Lookup id or email -->
		<a name="lookup-id-email"></a>
		<div ng-controller="lookupIdOrEmailCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a  ng-show="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.lookup_id_email' /></a>
				<a  ng-hide="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.lookup_id_email' /></a>
			</p>			  	
			<div class="collapsible bottom-margin-small admin-modal" id="lookup_ids_section" style="display:none;">
				<div class="form-group">
					<label for="idOrEmails"><@orcid.msg 'admin.lookup_id_email' /></label>
					<input type="text" id="idOrEmails" ng-enter="lookupIdOrEmails()" ng-model="idOrEmails" placeholder="<@orcid.msg 'admin.lookup_id_email.placeholder' />" class="input-xlarge" />
				</div>
				<div class="controls save-btns pull-left">
					<span id="lookup-ids" ng-click="lookupIdOrEmails()" class="btn btn-primary"><@orcid.msg 'admin.lookup_id_email.button'/></span>						
				</div>
			</div>	
		</div>
		
		<!-- Batch resend claim emails -->
		<div ng-controller="ResendClaimCtrl" class="workspace-accordion-item" ng-cloak>
	        <p>
				<a  ng-show="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.resend_claim.title' /></a>
				<a  ng-hide="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.resend_claim.title' /></a>
			</p>			  	
	        
		    <div class="collapsible bottom-margin-small admin-modal" id="batch_resend_section" style="display:none;">
			    <div class="alert alert-success" ng-show="result.claimResendSuccessfulList.length || result.notFoundList.length || result.alreadyClaimedList.length" style="overflow-x:auto;">
	    			<div ng-show="result.claimResendSuccessfulList.length"><@spring.message "admin.resend_claim.sent_success"/>
	    				<br>{{result.claimResendSuccessfulList}}
	    			</div>
	    			<div ng-show="result.alreadyClaimedList.length"><br><@spring.message "admin.resend_claim.already_claimed"/>
	    				<br>{{result.alreadyClaimedList}}
	    			</div>
	    			<div ng-show="result.notFoundList.length"><br><@spring.message "admin.resend_claim.not_found"/>
	    				<br>{{result.notFoundList}}
					</div>
				</div>
				<div class="control-group">
	    			<label for="emailIds" class="control-label">${springMacroRequestContext.getMessage("admin.reset_password.orcid.label")} </label>
	       			<div class="controls">                    	
	       				<input type="text" data-ng-model="emailIds" class="input-xlarge" placeholder="<@orcid.msg 'admin.lookup_id_email.placeholder' />" />
	       			</div>
	       			<span class="btn btn-primary" data-ng-click="resendClaimEmails()"><@spring.message "resend_claim.resend_claim_button_text"/></span>
				</div>
			</div>
	    </div>
	</div>
</div>

<script type="text/ng-template" id="confirm-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<!-- Lock profile -->
				<h3 ng-show="showLockPopover"><@orcid.msg 'admin.lock_profile.confirm_lock.title' /></h3>	
				<p ng-show="showLockPopover"><@orcid.msg 'admin.lock_profile.confirm_lock.text' /></p>	
						
				<!-- Unlock profile -->						
				<h3 ng-show="!showLockPopover"><@orcid.msg 'admin.lock_profile.confirm_unlock.title' /></h3>	
				<p ng-show="!showLockPopover"><@orcid.msg 'admin.lock_profile.confirm_unlock.text' /></p>						
				
				<!-- Profile details-->
				<div class="row">
					<p class="col-md-4 col-sm-6 col-xs-12"><strong><@orcid.msg 'admin.profile_details.given_names'/></strong></p>
					<p class="col-md-8 col-sm-6 col-xs-12">{{profileDetails.givenNames}}</p>
				</div>
				<div class="row">
					<p class="col-md-4 col-sm-6 col-xs-12"><strong><@orcid.msg 'admin.profile_details.family_name'/></strong></p>
					<p class="col-md-8 col-sm-6 col-xs-12">{{profileDetails.familyName}}</p>
				</div>				
				<div class="row">
					<p class="col-md-4 col-sm-6 col-xs-12"><strong><@orcid.msg 'admin.profile_details.email'/></strong></p>
					<p class="col-md-8 col-sm-6 col-xs-12">{{profileDetails.email}}</p>
				</div>
				<div class="row">
					<p class="col-md-4 col-sm-6 col-xs-12"><strong><@orcid.msg 'admin.profile_details.orcid'/></strong></p>
					<p class="col-md-8 col-sm-6 col-xs-12">{{profileDetails.orcid}}</p>
				</div>
				
				<!-- Buttons -->
    			<a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
    			<button class="btn btn-primary" id="btn-lock" ng-click="lockAccount()" ng-show="showLockPopover"><@orcid.msg 'admin.lock_profile.btn.lock'/></button>
    			<button class="btn btn-primary" id="btn-unlock" ng-click="unlockAccount()" ng-show="!showLockPopover"><@orcid.msg 'admin.unlock_profile.btn.unlock'/></button>
			</div>
		</div>
    </div>
</script>

<script type="text/ng-template" id="review-confirm-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<!-- Review profile -->
				<h3 ng-show="showReviewPopover"><@orcid.msg 'admin.review_profile.confirm_review.title' /></h3>	
				<p ng-show="showReviewPopover"><@orcid.msg 'admin.review_profile.confirm_review.text' /></p>	
				
				<!-- Unreview profile -->
				<h3 ng-show="!showReviewPopover"><@orcid.msg 'admin.unreview_profile.confirm_unreview.title' /></h3>	
				<p ng-show="!showReviewPopover"><@orcid.msg 'admin.unreview_profile.confirm_unreview.text' /></p>	
				
				<!-- Profile details-->
				<div class="row">
					<p class="col-md-4 col-sm-6 col-xs-12"><strong><@orcid.msg 'admin.profile_details.given_names'/></strong></p>
					<p class="col-md-8 col-sm-6 col-xs-12">{{profileDetails.givenNames}}</p>
				</div>
				<div class="row">
					<p class="col-md-4 col-sm-6 col-xs-12"><strong><@orcid.msg 'admin.profile_details.family_name'/></strong></p>
					<p class="col-md-8 col-sm-6 col-xs-12">{{profileDetails.familyName}}</p>
				</div>				
				<div class="row">
					<p class="col-md-4 col-sm-6 col-xs-12"><strong><@orcid.msg 'admin.profile_details.email'/></strong></p>
					<p class="col-md-8 col-sm-6 col-xs-12">{{profileDetails.email}}</p>
				</div>
				<div class="row">
					<p class="col-md-4 col-sm-6 col-xs-12"><strong><@orcid.msg 'admin.profile_details.orcid'/></strong></p>
					<p class="col-md-8 col-sm-6 col-xs-12">{{profileDetails.orcid}}</p>
				</div>
				
				<!-- Buttons -->
    			<a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
    			<button class="btn btn-primary" id="btn-review" ng-click="reviewAccount()" ng-show="showReviewPopover"><@orcid.msg 'admin.review_profile.btn.review'/></button>
    			<button class="btn btn-primary" id="btn-unreview" ng-click="unreviewAccount()" ng-show="!showReviewPopover"><@orcid.msg 'admin.unreview_profile.btn.unreview'/></button>
			</div>
		</div>
    </div>    
</script>

</@public >