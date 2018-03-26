<@public classes=['home'] nav="register">
    <div ng-controller="RequestResendClaimCtrl" id="RequestResendClaimCtr" ng-init="getRequestResendClaim()" class="row">
        <div class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-3 col-xs-12">
            <h2><@spring.message "resend_claim.title"/></h2>
            <p><small><@spring.message "resend_claim.resend_help"/> <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>.</small></p>      		
        	<form id="resend-claim-form" name="emailAddressForm">
        		<fieldset>
        			<span class="orcid-error"
			            ng-show="requestResendClaim.errors.length > 0">
			            <div ng-repeat='error in requestResendClaim.errors'
			                ng-bind-html="error"></div>
			        </span>
			        <div class="alert alert-success" ng-show="requestResendClaim.successMessage != null">
			        	<strong><span ng-bind="requestResendClaim.successMessage" /></strong>
			        </div>
        			<div class="control-group">
            			<label for="givenNames" class="control-label">${springMacroRequestContext.getMessage("resend_claim.labelEmailAddress")} </label>
               				<div class="controls">                    	
               				<input id="email" type="text" ng-model="requestResendClaim.email" />
               				<span class="required">*</span>
               			</div>
               			<button class="btn btn-primary topBuffer" ng-click="postResendClaimRequest()"><@spring.message "resend_claim.resend_claim_button_text"/></button>
        			</div>
        		</fieldset>
        	</form>
        </div>
    </div>
</@public>