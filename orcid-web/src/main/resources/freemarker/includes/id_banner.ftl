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
<#escape x as x?html>
<div class="id-banner <#if inDelegationMode>delegation-mode</#if>">
    <#if inDelegationMode><span class="delegation-mode-warning">${springMacroRequestContext.getMessage("delegate.managing_record")}</span></#if>
	<div ng-controller="NameCtrl" class="name-controller">
		<div ng-show="showEdit == false" ng-click="toggleEdit()">
			<h2 class="full-name">
				<span ng-hide="nameForm != null && nameForm.creditName == null" ng-cloak>
				    {{nameForm.creditName.value}}
				</span>
				<span ng-show="nameForm != null && nameForm.creditName == null" ng-cloak>
				    {{nameForm.givenNames.value}} {{nameForm.familyName.value}}
				</span>
				 <span class="glyphicon glyphicon-pencil edit-name edit-option" title="" ng-hide="showEdit == true"></span> 
			</h2>
		</div>
		<div class="names-edit" ng-show="showEdit == true" ng-cloak>
		   <label for="firstName">${springMacroRequestContext.getMessage("manage_bio_settings.labelfirstname")}</label><br />
		   <input type="text" ng-model="nameForm.givenNames.value"></input><br />
		   <label for="lastName">${springMacroRequestContext.getMessage("manage_bio_settings.labellastname")}</label><br />
		   <input type="text" ng-model="nameForm.familyName.value"></input><br />
		   <label for="creditName">${springMacroRequestContext.getMessage("manage_bio_settings.labelpublishedname")}</label><br/ >
		   <@orcid.privacyToggle  angularModel="nameForm.creditNameVisibility.visibility"
				             questionClick="toggleClickPrivacyHelp()"
				             clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				             publicClick="setCreditNameVisibility('PUBLIC', $event)" 
	                 	     limitedClick="setCreditNameVisibility('LIMITED', $event)" 
	                 	     privateClick="setCreditNameVisibility('PRIVATE', $event)" />
		        	   
		   <input type="text" ng-model="nameForm.creditName.value"></input>
		   <button class="btn btn-primary" ng-click="setNameForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		   <button class="btn" ng-click="close()"><@spring.message "freemarker.btncancel"/></button>
		   
		</div>
		
	</div>
	
	<div class="oid">
		<p class="orcid-id-container">
	    	<span class="mini-orcid-icon"></span>
	    	<a href="${baseUriHttp}/${(profile.orcidIdentifier.path)!}" id="orcid-id" class="orcid-id" title="Click for public view of ORCID iD">${baseUriHttp}/${(profile.orcidIdentifier.path)!}</a>
		</p>
		<#if RequestParameters['delegates']??>
	   <div ng-controller="SwitchUserCtrl" class="dropdown id-banner-container" ng-show="unfilteredLength" ng-cloak>
	       <a ng-click="openMenu($event)" class="id-banner-switch"><@orcid.msg 'public-layout.manage_proxy_account'/><span class="glyphicon glyphicon-chevron-right"></span></a>
	       <ul class="dropdown-menu id-banner-dropdown" ng-show="isDroppedDown" ng-cloak>
	       	   <li>
				   <input id="delegators-search" type="text" ng-model="searchTerm" ng-change="search()" placeholder="<@orcid.msg 'manage_delegators.search.placeholder'/>"></input>
	           </li>
	           <li ng-show="me && !searchTerm">
	               <a href="<@spring.url '/switch-user?j_username='/>{{me.delegateSummary.orcidIdentifier.path}}">
					   <ul>
						   <li><@orcid.msg 'id_banner.switchbacktome'/></li>
						   <li>{{me.delegateSummary.orcidIdentifier.uri}}</li>
					   </ul>
	               </a>
	           </li>
	           <li ng-repeat="delegationDetails in delegators.delegationDetails | orderBy:'delegateSummary.creditName.content' | limitTo:10">
	               <a href="<@spring.url '/switch-user?j_username='/>{{delegationDetails.delegateSummary.orcidIdentifier.path}}">
	               	   <ul>
	               	   	 <li>{{delegationDetails.delegateSummary.creditName.content}}</li>
	               	   	 <li>{{delegationDetails.delegateSummary.orcidIdentifier.uri}}</li>
	               	   </ul>
	               </a>
	           </li>
	           <li><a href="<@spring.url '/delegators?delegates'/>"><@orcid.msg 'id_banner.more'/></a></li>
	       </ul>
	    </div>
	</#if>
	</div>	
</div>
</#escape>