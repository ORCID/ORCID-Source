<@public nav="admin">

<!-- Admin main Layout -->
<div class="row">
	<!-- Left menu bar -->	
	<div class="col-md-3 col-sm-12 col-xs-12 lhs padding-fix">
		<#include "includes/id_banner.ftl"/>
	</div>
	<!-- Right menu bar -->
	<div class="col-md-9 col-sm-12 col-xs-12 admin-options">	
		
						
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