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
<span class="dotted-green-bar"></span>
<div class="row" ng-cloak>
	<div class="col-md-12 col-sm-12 col-xs-12">
		<ul class="oauth-icons">			
			<li ng-show="showBulletIcon"><span class="mini-orcid-icon oauth-bullet"></span></li>
			<li ng-show="showLimitedIcon"><span class="mini-icon glyphicon glyphicon-eye-open green"></span></li>
			<li ng-show="showCreateIcon"><span class="mini-icon glyphicon glyphicon-cloud-upload green"></span></li>
			<li ng-show="showUpdateIcon"><span class="mini-icon glyphicon glyphicon-repeat green"></span></li>							
		</ul>
	</div>
	<div class="col-md-12 col-sm-12 col-xs-12">
		<ul class="oauth-scopes" id="scopes-ul">
			<li ng-repeat="theScope in requestInfoForm.scopes">
				<span ng-show="theScope.name != 'EMAIL_READ_PRIVATE'" ng-mouseenter="toggleLongDescription(theScope.name)" ng-mouseleave="toggleLongDescription(theScope.name)">{{theScope.description}}</span>
				<div ng-show="theScope.name != 'EMAIL_READ_PRIVATE'" class="popover bottom scopeLongDesc" ng-class="{'popover bottom inline':showLongDescription[theScope.name] == true}">
					<div class="arrow"></div>
					<div class="lightbox-container">{{theScope.longDescription}}</div>
				</div>
			</li>		   				
		</ul>
	</div>
	<div ng-show="emailRequested">
		<div class="col-md-12 col-sm-12 col-xs-12">
			<span class="mini-icon glyphicon glyphicon-envelope green"></span>
			<h4 class="dark-label">EMAIL</h4>
		</div>
		<div class="col-md-1 col-sm-1 col-xs-1">
			<input type="checkbox" name="allowEmailAccess" id="allowEmailAccess" ng-model="allowEmailAccess"/>
		</div>
		<div class="col-md-11 col-sm-11 col-xs-11">
			{{requestInfoForm.clientName}}&nbsp;<@orcid.msg 'oauth.email_read_private_description'/><br>
			<p class="persistent-token-note"><@orcid.msg 'oauth.email_read_private_reason_prefix'/>&nbsp;{{requestInfoForm.clientEmailRequestReason}}</p>
		</div>	
	</div>
</div>
<span class="dotted-green-bar"></span>
<@orcid.checkFeatureStatus featureName='GDPR_UI' enabled=false> 
    <div class="row" ng-show="requestInfoForm.clientHavePersistentTokens">
    	<div class="col-md-1 col-sm-1 col-xs-1">
    		<input type="checkbox" name="enablePersistentToken" id="enablePersistentToken" ng-model="enablePersistentToken"/>
    	</div>
    	<div class="col-md-11 col-sm-11 col-xs-11">
    		<@orcid.msg 'oauth.persistent_token_description'/><br>
    		<p class="persistent-token-note"><@orcid.msg 'oauth.persistent_token_description.note'/></p>
    	</div> 	
    </div>
</@orcid.checkFeatureStatus>

