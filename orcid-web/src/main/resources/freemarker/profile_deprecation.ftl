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
<@public >
	<script type="text/ng-template" id="success-modal">
	  <div style="padding:20px">
	    <a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
	    <h1><@orcid.msg 'admin.profile_deprecation.deprecate_account.success'/></h1>
	    <p id="success-message"><@orcid.msg 'admin.profile_deprecation.deprecate_account.success_message'/></p>	        
	  </div>
	</script>


	<script type="text/ng-template" id="deprecate-account-modal">
	  <div style="padding:20px">
	    <a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
	    <h1><@orcid.msg 'admin.profile_deprecation.deprecate_account.title'/></h1>
		<div>
			<input type="text" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.account_to_deprecate' />" class="input-xlarge" ng-model="deprecatedAccount.orcid" ng-change="findAccountDetails('deprecated')">
			<span class="orcid-error" ng-show="deprecatedAccount.errors.length > 0">
				<span ng-repeat='error in deprecatedAccount.errors' ng-bind-html-unsafe="error"></span><br />
			</span>	
			<div ng-show="deprecatedAccount.givenNames.length">
				<span><@orcid.msg 'admin.profile_deprecation.given_names'/>:{{deprecatedAccount.givenNames}}</span>
				<span><@orcid.msg 'admin.profile_deprecation.family_name'/>:{{deprecatedAccount.familyName}}</span>
			</div>
		</div>
		<div>
			<input type="text" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.primary_account' />" class="input-xlarge" ng-model="primaryAccount.orcid" ng-change="findAccountDetails('primary')">
			<span class="orcid-error" ng-show="primaryAccount.errors.length > 0">
				<span ng-repeat='error in primaryAccount.errors' ng-bind-html-unsafe="error"></span><br />
			</span>
			<div ng-show="primaryAccount.givenNames.length">
				<span><@orcid.msg 'admin.profile_deprecation.given_names'/>:{{primaryAccount.givenNames}}</span>
				<span><@orcid.msg 'admin.profile_deprecation.family_name'/>:{{primaryAccount.familyName}}</span>
			</div>
		</div>
		<div class="controls save-btns pull-right bottom-margin-small">
        	<span id="bottom-deprecate-profile" ng-click="deprecateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deprecation.deprecate_account'/></span>
		</div>	    	        
	  </div>
	</script>

	<div ng-controller="profileDeprecationCtrl">
		<div class="controls save-btns pull-right bottom-margin-small">
        	<span id="bottom-deprecate-profile" ng-click="showDeprecateAccountModal()" class="btn btn-primary"><@orcid.msg 'admin.profile_deprecation.deprecate_account'/></span>
		</div> 
	</div>

</@public >