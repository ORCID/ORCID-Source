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
<#escape x as x?html>
<div class="id-banner <#if inDelegationMode>delegation-mode</#if>">	
	
    <#if inDelegationMode><span class="delegation-mode-warning">${springMacroRequestContext.getMessage("delegate.managing_record")}</span></#if>
    
    <!-- Name -->    
	<div ng-controller="NameCtrl" class="workspace-section" id="names-section">
		<div ng-show="showEdit == false" ng-click="toggleEdit()">
			<div class="row">
				<div class="col-md-9 col-sm-9 col-xs-10">
					<h2 class="full-name">
						<span ng-hide="nameForm != null 
						    && (nameForm.creditName == null || nameForm.namesVisibility.visibility != 'PUBLIC')" ng-bind="nameForm.creditName.value" ng-cloak>
						</span>
						<span ng-show="nameForm != null 
						    && (nameForm.creditName == null || nameForm.creditName.value == null || nameForm.namesVisibility.visibility != 'PUBLIC')" ng-cloak>
						    {{nameForm.givenNames.value}} {{nameForm.familyName.value}}
						</span>						 
					</h2>
				</div>
				<div class="col-md-3 col-sm-3 col-xs-2">
					<span class="glyphicon glyphicon-pencil edit-name edit-option right" title="" ng-hide="showEdit == true" id="open-edit-names"></span>
				</div>
			</div>
		</div>
		<!-- Edit Mode -->
		<div class="names-edit" ng-show="showEdit == true" ng-cloak>
		   <label for="firstName">${springMacroRequestContext.getMessage("manage_bio_settings.labelfirstname")}</label>
		   <input type="text" ng-model="nameForm.givenNames.value" ng-enter="setNameForm()" class="full-width-input"></input>
		   <span class="orcid-error" ng-show="nameForm.givenNames.errors.length > 0">
			   <div ng-repeat='error in nameForm.givenNames.errors' ng-bind-html="error"></div>
		   </span>
		   <label for="lastName">${springMacroRequestContext.getMessage("manage_bio_settings.labellastname")}</label>
		   <input type="text" ng-model="nameForm.familyName.value" ng-enter="setNameForm()" class="full-width-input"></input>
		   <label for="creditName">${springMacroRequestContext.getMessage("manage_bio_settings.labelpublishedname")}</label>		   		        	   
		   <input type="text" ng-model="nameForm.creditName.value" ng-enter="setNameForm()" class="full-width-input"></input>		   
		   <@orcid.privacyToggle  angularModel="nameForm.namesVisibility.visibility"
				             questionClick="toggleClickPrivacyHelp()"
				             clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				             publicClick="setNamesVisibility('PUBLIC', $event)" 
	                 	     limitedClick="setNamesVisibility('LIMITED', $event)" 
	                 	     privateClick="setNamesVisibility('PRIVATE', $event)" />
		   <ul class="workspace-section-toolbar">
 				<li class="pull-right">
		   			<button class="btn btn-primary" ng-click="setNameForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		   		</li>
		   		<li class="pull-right">
		   			<a class="cancel-option" ng-click="close()"><@spring.message "freemarker.btncancel"/></a>
		   		</li>
		   	</ul>
		</div>
	</div>
	<div class="oid">
		<div class="id-banner-header">
			<span><@orcid.msg 'common.orcid_id' /></span>
		</div>
		<div class="orcid-id-container">
			<div class="orcid-id-info">
		    	<span class="mini-orcid-icon"></span>
		    	<!-- Reference: orcid.js:removeProtocolString() -->
	       		<span id="orcid-id" class="orcid-id shortURI">${baseDomainRmProtocall}/${(profile.orcidIdentifier.path)!}</span>
			</div>
			<div class="orcid-id-options">
				<a href="${baseUriHttp}/${(profile.orcidIdentifier.path)!}" class="gray-button" target="_blank"><@orcid.msg 'id_banner.viewpublicprofile'/>
			</div>
		</div>
	</div>
	<#if (locked)?? && !locked>
		<div ng-controller="SwitchUserCtrl" class="dropdown id-banner-container" ng-show="unfilteredLength" ng-cloak>
	       <a ng-click="openMenu($event)" class="id-banner-switch"><@orcid.msg 'public-layout.manage_proxy_account'/><span class="glyphicon glyphicon-chevron-right"></span></a>
	       <ul class="dropdown-menu id-banner-dropdown" ng-show="isDroppedDown" ng-cloak>
	       	   <li>
				   <input id="delegators-search" type="text" ng-model="searchTerm" ng-change="search()" placeholder="<@orcid.msg 'manage_delegators.search.placeholder'/>"></input>
	           </li>
	           <li ng-show="me && !searchTerm">
	               <a href="<@orcid.rootPath '/switch-user?j_username='/>{{me.delegateSummary.orcidIdentifier.path}}">
					   <ul>
						   <li><@orcid.msg 'id_banner.switchbacktome'/></li>
						   <li>{{me.delegateSummary.orcidIdentifier.uri}}</li>
					   </ul>
	               </a>
	           </li>
	           <li ng-repeat="delegationDetails in delegators.delegationDetails | orderBy:'delegateSummary.creditName.content' | limitTo:10">
	               <a href="<@orcid.rootPath '/switch-user?j_username='/>{{delegationDetails.delegateSummary.orcidIdentifier.path}}">
	               	   <ul>
	               	   	 <li>{{delegationDetails.delegateSummary.creditName.content}}</li>
	               	   	 <li>{{delegationDetails.delegateSummary.orcidIdentifier.uri}}</li>
	               	   </ul>
	               </a>
	           </li>
	           <li><a href="<@orcid.rootPath '/delegators?delegates'/>"><@orcid.msg 'id_banner.more'/></a></li>
	       </ul>
	    </div>	
	</#if>
	    
	
	
	
</div>
</#escape>
