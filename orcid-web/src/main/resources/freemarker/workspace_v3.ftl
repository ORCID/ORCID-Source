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
<#-- @ftlvariable name="profile" type="org.orcid.jaxb.model.message.OrcidProfile" -->
<@protected nav="record">
<#escape x as x?html>
<#if emailVerified?? && emailVerified>
    <div class="alert alert-success">
        <strong><@spring.message "orcid.frontend.web.email_verified"/></strong>
    </div>
</#if>

<#if invalidVerifyUrl?? && invalidVerifyUrl>
    <div class="alert alert-success">
        <strong><@spring.message "orcid.frontend.web.invalid_verify_link"/></strong>
    </div>
</#if>


<#if invalidOrcid?? && invalidOrcid>
    <div class="alert alert-success">
        <strong><@spring.message "orcid.frontend.web.invalid_switch_orcid"/></strong>
    </div>
</#if>

<script type="text/javascript">
	orcidVar.notificationsEnabled = ${profile.orcidInternal.preferences.notificationsEnabled?string};
</script>

<div class="row workspace-top public-profile">

	<#-- hidden divs that trigger angular -->
	<#if RequestParameters['recordClaimed']??>
	    <div ng-controller="ClaimThanks" style="display: hidden;"></div>	    
	<#elseif !Session.CHECK_EMAIL_VALIDATED?exists && !inDelegationMode>
    	<div ng-controller="VerifyEmailCtrl" style="display: hidden;" orcid-loading="{{loading}}"></div>
	</#if>
	<!-- ID Banner and other account information -->
    <div class="col-md-3 lhs left-aside">
    	<div class="workspace-profile">
            
            <#include "includes/id_banner.ftl"/>
            
            <#include "includes/orcid_public_record_widget.ftl"/>
            
            <#include "includes/print_record.ftl"/>
            
            <div class="qrcode-container">
                <a href="http://qrcode.orcid.org" target="_blank"><span class="glyphicons qrcode orcid-qr"></span><@orcid.msg 'workspace.qrcode.link.text'/></a>
                <div class="popover-help-container">
                    <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                    <div id="qrcode-help" class="popover bottom">
                        <div class="arrow"></div>
                        <div class="popover-content">
                            <p><@orcid.msg 'workspace.qrcode.help'/></p>
                        </div>
                    </div>
                </div>
            </div>            	       	
	       	
	       	<!-- Other Names -->
	       	<#if RequestParameters['v2modal']??>
	       		<div ng-controller="OtherNamesCtrl" class="workspace-section other-names" id="other-names-section">        	   
	        	   <div class="workspace-section-header">
	        	   	   <span class="workspace-section-title"><@orcid.msg 'workspace.Alsoknownas'/></span>
		        	   <span ng-hide="showEdit == true" ng-click="openEditModal()">		        	   	  
		        	      <span class="glyphicon glyphicon-pencil edit-other-names edit-option pull-right" title="" id="open-edit-other-names"></span>
		        	      <span ng-repeat="otherName in otherNamesForm.otherNames | orderBy: 'displayIndex'" ng-cloak>
		        	         {{ $last?otherName.content:otherName.content + ", "}}
		        	      </span>
		        	   </span>		        	   
	        	   </div>
	        	</div>
	       	<#else>	       	
		       	<div ng-controller="OtherNamesCtrl" class="workspace-section other-names" id="other-names-section">        	   
	        	   <div class="workspace-section-header">
	        	   	   <span class="workspace-section-title"><@orcid.msg 'workspace.Alsoknownas'/></span>
		        	   <span ng-hide="showEdit == true" ng-click="openEdit()">		        	   	  
		        	      <span class="glyphicon glyphicon-pencil edit-other-names edit-option pull-right" title="" id="open-edit-other-names"></span>
		        	      <span ng-repeat="otherName in otherNamesForm.otherNames" ng-cloak>
		        	         {{ $last?otherName.content:otherName.content + ", "}}
		        	      </span>
		        	   </span>
		        	   <span class="pull-right" ng-show="showEdit == true" id="other-names-visibility" ng-cloak>
			        	   <@orcid.privacyToggle3  angularModel="defaultVisibility"
				             questionClick="toggleClickPrivacyHelp($index)"
				             clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				             publicClick="setPrivacy('PUBLIC', $event)" 
		                	     limitedClick="setPrivacy('LIMITED', $event)" 
		                	     privateClick="setPrivacy('PRIVATE', $event)"
		                	     elementId="$index" publicId="other-names-public-id"
		                	     limitedId="other-names-limited-id" privateId="other-names-private-id" />
	                   </span>
	        	   </div>
	        	   
	        	   <!-- Edit -->
	        	   <div ng-show="showEdit == true" ng-cloak>
	        	      <div ng-repeat="otherName in otherNamesForm.otherNames" class="icon-inside-input">
	        	          <input name="other-name" type="text" ng-model="otherName.content" ng-enter="setOtherNamesForm()">
	        	          <a ng-click="deleteOtherName(otherName)" class="glyphicon glyphicon-trash grey icon-inside"></a>
	        	          <span class="orcid-error" ng-show="otherName.errors.length > 0">
						     <div ng-repeat='error in otherName.errors' ng-bind-html="error"></div>
					      </span>
	        	      </div>
	        	      <ul class="workspace-section-toolbar">
	        	      	<li>
	        	      		<a ng-click="addNew()"><span class="glyphicon glyphicon-plus"></span></a>
	        	      	</li>
	        	      	<li class="pull-right">
	        	      		<button id="save-other-names" class="btn btn-primary" ng-click="setOtherNamesForm()"><@spring.message "freemarker.btnsavechanges"/></button>
	        	      	</li>
	        	      	<li class="pull-right">
	        	      		<a class="cancel-option" ng-click="close()"><@spring.message "freemarker.btncancel"/></a>
	        	      	</li>
	        	      </ul>
	        	   </div>
		       	</div>
            </#if>
            
            <!-- Country -->
            <#if RequestParameters['v2modal']??>
            	<div ng-controller="CountryCtrl" class="workspace-section country">
	            	<div class="workspace-section-header">
			        	<span class="workspace-section-title"><@orcid.msg 'public_profile.labelCountry'/></span>
			            <span class="glyphicon glyphicon-pencil edit-country edit-option pull-right" ng-click="openEditModal()" title=""></span>
			            
			            <span ng-repeat="country in countryForm.addresses | orderBy: 'displayIndex'">			            				            	
			            	<span ng-show="country != null && country.countryName != null" ng-bind="country.countryName"></span>
			            </span>			            
		            </div>
		        </div>
            <#else>            
            	<div ng-controller="CountryCtrl" class="workspace-section country"> 
	            	<div class="workspace-section-header">
			        	<span class="workspace-section-title"><@orcid.msg 'public_profile.labelCountry'/></span>
			            <span class="glyphicon glyphicon-pencil edit-country edit-option pull-right" ng-click="openEdit()" title="" ng-hide="showEdit == true" id="open-edit-country"></span>
			            <span ng-hide="showEdit == true" ng-click="toggleEdit()">
			            	<span ng-repeat="country in countryForm.addresses">			            				            	
			            		<span ng-show="showEdit == false && country != null && country.countryName != null && country.primary == true" ng-bind="country.countryName" ></span>			            		
			            	</span>			            	
			            </span>
			            <span class="pull-right" ng-hide="showEdit == false" ng-cloak>
			            	<@orcid.privacyToggle3 angularModel="defaultVisibility"
				         		questionClick="toggleClickPrivacyHelp($index)"
				         		clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				         		publicClick="setPrivacy('PUBLIC', $event)" 
	                 	     	limitedClick="setPrivacy('LIMITED', $event)" 
	                 	     	privateClick="setPrivacy('PRIVATE', $event)"
	                 	      	elementId="$index" publicId="country-public-id"
		                	    limitedId="country-limited-id" privateId="country-private-id" />
			            </span>
		            </div>
	                <!-- Edit -->
	                <div ng-show="showEdit == true" ng-cloak>	                		                  	 
	                  	 <div ng-repeat="country in countryForm.addresses">
			                 <select id="country" ng-model="country.iso2Country.value" ng-show="country.primary == true">
				    			 <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
								 <#list isoCountries?keys as key>
								     <option value="${key}">${isoCountries[key]}</option>
							 	 </#list>
							 </select>							 
						 </div>
						 <ul class="workspace-section-toolbar">
	        	      		<li class="pull-right">
			             		<button id="save-country" class="btn btn-primary" ng-click="setCountryForm()"><@spring.message "freemarker.btnsavechanges"/></button>
			             	</li>
			             	<li class="pull-right">
				         		<a class="cancel-option" ng-click="close()"><@spring.message "freemarker.btncancel"/></a>
				         	</li>
				         </ul>
					</div>
	            </div>
            </#if>
            
            
              
	       	<!-- Keywords -->
	       	<#if RequestParameters['v2modal']??>
	       		<div ng-controller="KeywordsCtrl" class="workspace-section keywords">
		        	<div class="workspace-section-header">
	        	   		<span class="workspace-section-title"><@orcid.msg 'public_profile.labelKeywords'/></span>
		        	   	<span>
		        	   	  	<span class="glyphicon glyphicon-pencil edit-keywords edit-option pull-right" ng-click="openEditModal()" title=""></span>	
		        	      	<span ng-repeat="keyword in keywordsForm.keywords | orderBy: 'displayIndex'" ng-cloak>
		        	         	{{ $last?keyword.content:keyword.content+ ", "}}
		        	      	</span>
		        	   	</span>
	        	   	</div>
	       		</div>
	       	<#else>
	       		<div ng-controller="KeywordsCtrl" class="workspace-section keywords">
		        	<div class="workspace-section-header">
		        	   	<span class="workspace-section-title"><@orcid.msg 'public_profile.labelKeywords'/></span>			        	   
		        	   	<span ng-hide="showEdit == true">
		        	   	  	<span class="glyphicon glyphicon-pencil edit-keywords edit-option pull-right" ng-click="openEdit()" title="" id="open-edit-keywords"></span>	
		        	      	<span ng-repeat="keyword in keywordsForm.keywords" ng-cloak>
		        	         	{{ $last?keyword.content:keyword.content+ ", "}}
		        	      	</span>
		        	   	</span>
		        	   	<span class="pull-right" ng-show="showEdit == true" ng-cloak>
		        	   			<@orcid.privacyToggle3  angularModel="defaultVisibility"
			             	  	questionClick="toggleClickPrivacyHelp($index)"
			             	  	clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
			             	  	publicClick="setPrivacy('PUBLIC', $event)" 
	                	      	limitedClick="setPrivacy('LIMITED', $event)" 
	                	      	privateClick="setPrivacy('PRIVATE', $event)"
	                	      	elementId="$index" publicId="keywords-public-id"
		                	    limitedId="keywords-limited-id" privateId="keywords-private-id" />
		        	   	</span>
	        	 	</div>
	        	   
        	   		<div ng-show="showEdit == true" ng-cloak>
        	      		<div ng-repeat="keyword in keywordsForm.keywords">
        	      	  		<div class="icon-inside-input">
	        	          		<input type="text" ng-model="keyword.content" ng-enter="setKeywordsForm()" name="keyword"></input>
		        	          	<a ng-click="deleteKeyword(keyword)" class="glyphicon glyphicon-trash grey icon-inside"></a>
	        	          	</div>
	        	          	<span class="orcid-error" ng-show="keyword.errors.length > 0">
						     	<div ng-repeat='error in keyword.errors' ng-bind-html="error"></div>
					      	</span>					      
	        	      	</div>
	        	      	<ul class="workspace-section-toolbar">
	        	      		<li>
	        	      			<a ng-click="addNew()"><span class="glyphicon glyphicon-plus"></span></a>
	        	      		</li>
	        	      		<li class="pull-right">
	        	      			<button id="save-keywords" class="btn btn-primary" ng-click="setKeywordsForm()"><@spring.message "freemarker.btnsavechanges"/></button>
	        	      		</li>
	        	      		<li class="pull-right">
		        	      		<a class="cancel-option" ng-click="close()"><@spring.message "freemarker.btncancel"/></a>
		        	      	</li>
	        	   		</ul>
        	   		</div>
	       		</div>
	        </#if>
	       	
	      	<!-- Websites  -->
	      	<#if RequestParameters['v2modal']??>
	      		<div ng-controller="WebsitesCtrl" class="workspace-section websites">
		        	<div class="workspace-section-header">
		        	   <span class="workspace-section-title"><@orcid.msg 'public_profile.labelWebsites'/></span>
		        	   <span>
		        	      <span class="glyphicon glyphicon-pencil edit-websites edit-option pull-right" ng-click="openEditModal()" title=""></span><br />
		        	      <div ng-repeat="website in websitesForm.websites | orderBy: 'displayIndex'" ng-cloak class="wrap">
		        	         <a href="{{website.url}}" target="_blank" rel="me nofollow">{{website.urlName != null? website.urlName : website.url}}</a>
		        	      </div>
		        	   </span>
		        	</div>	
		       	</div>
	        <#else>
		       	<div ng-controller="WebsitesCtrl" class="workspace-section websites">
		        	<div class="workspace-section-header">
		        	   <span class="workspace-section-title"><@orcid.msg 'public_profile.labelWebsites'/></span>
		        	   <span ng-hide="showEdit == true">
		        	      <span class="glyphicon glyphicon-pencil edit-websites edit-option pull-right" ng-click="openEdit()" title="" id="open-edit-websites"></span><br />
		        	      <div ng-repeat="website in websitesForm.websites" ng-cloak class="wrap">
		        	         <a href="{{website.url}}" target="_blank" rel="me nofollow">{{website.urlName != null? website.urlName : website.url}}</a>
		        	      </div>
		        	   </span>	
		        	   <span class="pull-right" ng-show="showEdit == true" ng-cloak>
		        	   		<@orcid.privacyToggle3 
			        	   		angularModel="defaultVisibility"
				            	questionClick="toggleClickPrivacyHelp($index)"
				             	clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				             	publicClick="setPrivacy('PUBLIC', $event)" 
		                	    limitedClick="setPrivacy('LIMITED', $event)" 
		                	    privateClick="setPrivacy('PRIVATE', $event)" 
		                	    elementId="$index" publicId="websites-public-id"
		                	    limitedId="websites-limited-id" privateId="websites-private-id" />
		        	   </span>
		        	</div>
	
	        	   <div ng-show="showEdit == true" ng-cloak>
	        	      <div ng-repeat="website in websitesForm.websites" class="mobile-box">
	        	          <input name="website-name" type="text" ng-model="website.urlName" ng-enter="setWebsitesForm()" placeholder="${springMacroRequestContext.getMessage('manual_work_form_contents.labeldescription')}"></input>        	          
	        	          <input name="website-url" type="text" ng-model="website.url" ng-enter="setWebsitesForm()" placeholder="${springMacroRequestContext.getMessage('common.url')}" style="padding-right: 5px;"></input>
		        	      <a name="delete-website" ng-click="deleteWebsite(website)" class="glyphicon glyphicon-trash grey icon-inside pull-right"></a>	        	              	          
	        	          <span class="orcid-error" ng-show="website.errors.length > 0">
						     <div ng-repeat='error in website.errors' ng-bind-html="error"></div>
					      </span>        	          
					      <span class="dotted-bar left"></span>
	        	      </div>
	        	      <ul class="workspace-section-toolbar">
	        	      	<li>
	        	      		<a ng-click="addNew()">
	        	      			<span class="glyphicon glyphicon-plus"></span>
	        	      		</a>
	        	      	</li>
	        	      	<li class="pull-right">
	        	      		<button id="save-websites" class="btn btn-primary" ng-click="setWebsitesForm()"><@spring.message "freemarker.btnsavechanges"/></button>		
	        	      	</li>
	        	      	<li class="pull-right">
	        	      		<a class="cancel-option" ng-click="close()"><@spring.message "freemarker.btncancel"/></a>
	        	      	</li>
	        	      </ul>
	        	   </div>
		       	</div>
	       	</#if>
	       	<!-- Emails  -->
	       	<#if RequestParameters['v2modal']??>
	       		<div ng-controller="EmailsCtrl" class="workspace-section">
		        	<div class="workspace-section-header">
		        	   <span class="workspace-section-title"><@orcid.msg 'manage.emails'/></span>		        	   
		        	   <span class="glyphicon glyphicon-pencil edit-websites edit-option pull-right" ng-click="openEditModal()"></span>
		        	</div> 
		        	<div ng-repeat="email in emailSrvc.emails.emails" class="mobile-box emails-box">
		        	   <span ng-bind="email.value"></span>
		        	</div>
		       	</div>
	       	<#else>
		       	<div ng-controller="EmailsCtrl" class="workspace-section">
		        	<div class="workspace-section-header">
		        	   <span class="workspace-section-title"><@orcid.msg 'manage.emails'/></span>
		        	   <span ng-hide="showEdit == true">
		        	      	<span class="glyphicon glyphicon-pencil edit-websites edit-option pull-right" ng-click="openEdit()" id="open-edit-emails"></span>
		        	   </span>
		        	   <div id="emailSectionId" ng-repeat="email in emailSrvc.emails.emails" class="mobile-box emails-box">
		        	   		<div ng-bind="email.value"></div>
		        	   		<div ng-show="showEdit == true" ng-cloak>
			        	   		<@orcid.privacyToggle3
		                            angularModel="email.visibility"
									questionClick="toggleClickPrivacyHelp($index)"
									clickedClassCheck="{'popover-help-container-show':privacyHelp[email.value]==true}" 
									publicClick="emailSrvc.setPrivacy(email, 'PUBLIC', $event)" 
				                  	limitedClick="emailSrvc.setPrivacy(email, 'LIMITED', $event)" 
				                  	privateClick="emailSrvc.setPrivacy(email, 'PRIVATE', $event)" 
				                  	elementId="$index" publicId="email-{{email.value}}-public-id"
		                	    	limitedId="email-{{email.value}}-limited-id" privateId="email-{{email.value}}-private-id" />
		        	   		</div>
		        	   </div>
		        	   <div ng-show="showEdit == true" ng-cloak>
		        	   		<a href="account"><@orcid.msg 'workspace.EditMoreEmailSettings'/></a>
		        	   </div>
		        	   <div ng-show="showEdit == true" ng-cloak>
		        	   		<a class="cancel-option pull-right" ng-click="close()"><@spring.message "freemarker.btncancel"/></a>
		        	   </div>	        	   
		        	</div>	        	
		       	</div>
	       	</#if>
			<!--  External Identifiers -->
	       	<#if RequestParameters['v2modal']??>
	       		<div ng-controller="ExternalIdentifierCtrl" ng-hide="!externalIdentifiersForm.externalIdentifiers.length" ng-cloak  class="workspace-section">
	       			<div class="workspace-section-header">
	       				<span class="workspace-section-title"><@orcid.msg 'public_profile.labelOtherIDs'/></span>
	       				<span class="glyphicon glyphicon-pencil edit-websites edit-option pull-right" ng-click="openEditModal()"></span>
	       			</div>
	       			<div ng-repeat="externalIdentifier in externalIdentifiersForm.externalIdentifiers | orderBy:'displayIndex'">	       				
			        	<span ng-hide="externalIdentifier.url">{{externalIdentifier.commonName}} {{externalIdentifier.reference}}</span>
			        	<span ng-show="externalIdentifier.url"><a href="{{externalIdentifier.url}}" target="_blank">{{externalIdentifier.commonName}} {{externalIdentifier.reference}}</a></span>				   		
	       			</div>
				</div>
			<#else>
				<div ng-controller="ExternalIdentifierCtrl" ng-hide="!externalIdentifiersForm.externalIdentifiers.length" ng-cloak  class="workspace-section">
	       			<div class="workspace-section-header">
	       				<span class="workspace-section-title"><@orcid.msg 'public_profile.labelOtherIDs'/></span>
	       			</div>
	       			<div ng-repeat='externalIdentifier in externalIdentifiersForm.externalIdentifiers'>
			        	<span ng-hide="externalIdentifier.url">{{externalIdentifier.commonName}} {{externalIdentifier.reference}}</span>
			        	<span ng-show="externalIdentifier.url"><a href="{{externalIdentifier.url}}" target="_blank">{{externalIdentifier.commonName}} {{externalIdentifier.reference}}</a></span>
				   		<a ng-click="deleteExternalIdentifierConfirmation($index)" class="glyphicon glyphicon-trash grey"></a>
	       			</div>
				</div>
			</#if>		
		</div>
    </div>
    
    <div class="col-md-9 right-aside">
        <div class="workspace-right">        
        	<!-- Locked error message -->
        	<#if (locked)?? && locked>
	        	<div class="workspace-inner workspace-header">
	                <div class="alert alert-error readme" ng-cloak>
	                	<strong><@orcid.msg 'workspace.locked.header'/></strong>
	                	<p><@orcid.msg 'workspace.locked.message'/></p>
	              	</div>                
	        	</div>                
        	</#if>
        	<div class="workspace-inner workspace-header" ng-controller="WorkspaceSummaryCtrl">
                <div class="grey-box" ng-show="showAddAlert()" ng-cloak>
                	<strong><@orcid.msg 'workspace.addinformationaboutyou'/></strong>
              	</div>                
        	</div>
        	<div class="workspace-accordion" id="workspace-accordion">        		
        		<!-- Personal Information -->
				<div id="workspace-personal" class="workspace-accordion-item workspace-accordion-active" ng-controller="PersonalInfoCtrl">        			
            		<div class="workspace-accordion-content" ng-show="displayInfo">
            			<#include "workspace_personal_v3.ftl"/>
        			</div>
            	</div>
            	<!-- Affiliations / Education / Employment -->
                <#include "workspace_affiliations_body_list_v3.ftl"/>
                <!-- Fundings -->
               	<#include "workspace_fundings_body_list_v3.ftl"/>
               
		        <!-- Works -->                
                <div id="workspace-publications" class="workspace-accordion-item workspace-accordion-active" ng-controller="WorkCtrl" orcid-loaded="{{worksSrvc.worksToAddIds != null && worksSrvc.loading != true}}">
                    <#include "includes/work/work_section_header_inc_v3.ftl"/>
                    <!-- Work Import Wizard -->
					<div ng-show="workImportWizard == true" class="work-import-wizard" ng-cloak>
						<div class="ie7fix-inner">
							<div class="row">	
								<div class="col-md-12 col-sm-12 col-xs-12">
					           		<h1 class="lightbox-title wizard-header"><@orcid.msg 'workspace.link_works'/></h1>
					           		<span ng-click="showWorkImportWizard()" class="close-wizard"><@orcid.msg 'workspace.LinkResearchActivities.hide_link_works'/></span>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12 col-sm-12 col-xs-12">
									<p class="wizard-content">
						           		<@orcid.msg 'workspace.LinkResearchActivities.description'/> <@orcid.msg 'workspace.LinkResearchActivities.description.more_info'/>
						           	</p>								
					           	</div>
							</div>
							<#if RequestParameters['import_works_wizard']??>
								<div id="workFilters" class="col-md-12 col-sm-12 col-xs-12">
									<@orcid.msg 'workspace.link_works.filter.worktype'/>&nbsp;&nbsp;<select ng-options="wt as wt for wt in workType" ng-model="selectedWorkType" ng-change="processWorkImportWizardList()"></select>
									<@orcid.msg 'workspace.link_works.filter.geographicalarea'/>&nbsp;&nbsp;<select ng-options="ga as ga for ga in geoArea" ng-model="selectedGeoArea" ng-change="processWorkImportWizardList()"></select>
								</div>
							</#if>
							<br>
							<div class="row wizards">
								<div class="col-md-12 col-sm-12 col-xs-12">
				    		    	<div ng-repeat="wtw in workImportWizards">
				                   		<strong><a ng-click="openImportWizardUrlFilter('<@orcid.rootPath '/oauth/authorize'/>', wtw)">{{wtw.displayName}}</a></strong><br />					                   							                   		                		
				                 		<div class="justify">												
											<p class="wizard-description" ng-class="{'ellipsis-on' : wizardDescExpanded[wtw.clientId] == false || wizardDescExpanded[wtw.clientId] == null}">
												{{wtw.shortDescription}}													
												<a ng-click="toggleWizardDesc(wtw.clientId)" ng-show="wizardDescExpanded[wtw.clientId] == true"><span class="glyphicon glyphicon-chevron-right wizard-chevron"></span></a>
											</p>												
											<a ng-click="toggleWizardDesc(wtw.clientId)" ng-show="wizardDescExpanded[wtw.clientId] == false || wizardDescExpanded[wtw.clientId] == null" class="toggle-wizard-desc"><span class="glyphicon glyphicon-chevron-down wizard-chevron"></span></a>
										</div>
					                    <hr/>
				                	</div>
								</div>
							</div>
						</div>						
					</div>
					<!-- Bulk Edit -->					
					<div ng-show="bulkEditShow && workspaceSrvc.displayWorks" ng-cloak>						
						<div class="bulk-edit">
							<div class="row">
								<div class="col-md-7 col-sm-7 col-xs-6">
									<h4><@orcid.msg 'workspace.bulkedit.title'/></h4><span class="hide-bulk" ng-click="toggleBulkEdit()"><@orcid.msg 'workspace.bulkedit.hide'/></span>
									<ol>
										<li><@orcid.msg 'workspace.bulkedit.selectWorks'/></li>
										<li><@orcid.msg 'workspace.bulkedit.selectAction'/></li>
									</ol>
								</div>
								<div class="col-md-5 col-sm-5 col-xs-6">
									<ul class="bulk-edit-toolbar">
																			
										<li class="bulk-edit-toolbar-item work-multiple-selector"><!-- Select all -->
											<label><@orcid.msg 'workspace.bulkedit.select'/></label>
											<div id="custom-control-x">
												<div class="custom-control-x" >	
													<div class="dropdown-custom-menu" id="dropdown-custom-menu" ng-click="toggleSelectMenu()">										
														<span class="custom-checkbox-parent">
															<div class="custom-checkbox" id="custom-checkbox" ng-click="swapbulkChangeAll();$event.stopPropagation();" ng-class="{'custom-checkbox-active':bulkChecked == true}"></div>
														</span>										
														<div class="custom-control-arrow" ng-click="toggleSelectMenu(); $event.stopPropagation();"></div>
													</div>
													<div>
														<ul class="dropdown-menu" role="menu" id="special-menu" ng-class="{'block': bulkDisplayToggle == true}">
												          <li><a href="" ng-click="bulkChangeAll(true); bulkDisplayToggle = false;"><@orcid.msg 'workspace.bulkedit.selected.all'/></a></li>
												          <li><a href="" ng-click="bulkChangeAll(false); bulkDisplayToggle = false;"><@orcid.msg 'workspace.bulkedit.selected.none'/></a></li>							          							          
												        </ul>			
													</div>
												</div>
											</div>
										</li>
										<li class="bulk-edit-toolbar-item"><!-- Privacy control -->
											<label><@orcid.msg 'workspace.bulkedit.edit'/></label>
											<div class="bulk-edit-privacy-control">
												<@orcid.privacyToggle2 angularModel="groupPrivacy()" 
													    questionClick=""
													    clickedClassCheck=""
														publicClick="setBulkGroupPrivacy('PUBLIC', $event)" 
									                	limitedClick="setBulkGroupPrivacy('LIMITED', $event)" 
									                	privateClick="setBulkGroupPrivacy('PRIVATE', $event)"/>
								 			</div>
								 			<div class="bulk-edit-delete pull-right">
											    <div class="centered">
													<a ng-click="deleteBulkConfirm()" class="ignore toolbar-button edit-item-button" ng-mouseenter="showTooltip('Bulk-Edit')" ng-mouseleave="hideTooltip('Bulk-Edit')">
														<span class="edit-option-toolbar glyphicon glyphicon-trash"></span>
													</a>
													<div class="popover popover-tooltip top bulk-edit-popover" ng-show="showElement['Bulk-Edit'] == true">
		                                             <div class="arrow"></div>
			                                            <div class="popover-content">
			                                                <span><@orcid.msg 'workspace.bulkedit.delete'/></span>
			                                            </div>
			                                        </div>
												</div>
											</div>
										</li>
									</ul>
								</div>							
							</div>						  
					   </div>
					</div>
					<#if RequestParameters['bibtexExport']??>
						<!-- BibTeX Export Layout -->					
						<div ng-show="showBibtexExport && workspaceSrvc.displayWorks" ng-cloak class="bibtex-box">
							<div class=box-border" ng-show="canReadFiles" ng-cloak>
							   <h4>Export BibTeX</h4><span ng-click="toggleBibtexExport()" class="hide-importer">Hide export BibTeX</span>
							   <div class="row full-height-row">
							   	   <div class="col-md-9 col-sm-9 col-xs-8">
									   <p>
									   		Export your works to a BibTeX file. For more information see <a href="">exporting works</a>.
									   </p> 
								   </div>
								   <div class="col-md-3 col-sm-3 col-xs-4">
								   		<span class="bibtext-options">							   									   		
										    <a class="bibtex-cancel" ng-click="toggleBibtexExport()"><@orcid.msg 'workspace.bibtexImporter.cancel'/></a>			       
										    <span ng-hide="worksFromBibtex.length > 0" class="import-label" ng-click="openBibtexExportDialog()">Export</span>										
										</span>								    
								   </div>
							   </div>
							</div>
							<div ng-show="loadingScripts == true" class="text-center ng-hide" ng-cloak>
							    <i id="" class="glyphicon glyphicon-refresh spin x2 green"></i>
							</div>
							<span class="dotted-bar" ng-show="scriptsLoaded"></span>
							<div class="bottomBuffer" ng-show="scriptsLoaded && !bibtexGenerated && !bibtexExportError" ng-cloak>
								<ul class="inline-list">
									<li>
										Generating BibTeX, please wait...
									</li>
									<li>
										&nbsp;<span><i id="" class="glyphicon glyphicon-refresh spin x1 green"></i></span>		
									</li>
								</ul>
								 
							</div>
							<div class="alert alert-block" ng-show="bibtexExportError">
								<strong>Something went wrong, please try again...</strong>
							</div>
							<div ng-show="bibtexGenerated && !bibtexExportError" class="bottomBuffer">							
									<a download="orcid.bib" href="{{bibtexURL}}" id="downloadlink">Click to Download</a>
							</div>
						</div>		
					</#if>
					<!-- Bibtex Importer Wizard -->
					<div ng-show="showBibtexImportWizard && workspaceSrvc.displayWorks" ng-cloak class="bibtex-box">
						<div class=box-border" ng-show="canReadFiles" ng-cloak>
						   <h4>Link BibTeX</h4><span ng-click="openBibTextWizard()" class="hide-importer">Hide link BibTeX</span>
						   <div class="row full-height-row">
						   	   <div class="col-md-9 col-sm-9 col-xs-8">
								   <p>
								   		<@orcid.msg 'workspace.bibtexImporter.instructions'/>  <a href="http://support.orcid.org/knowledgebase/articles/390530" target="_blank"><@orcid.msg 'workspace.bibtexImporter.learnMore'/></a>.
								   </p> 
							   </div>
							   <div class="col-md-3 col-sm-3 col-xs-4">
							   		<span class="bibtext-options">							   									   		
									    <a class="bibtex-cancel" ng-click="openBibTextWizard()"><@orcid.msg 'workspace.bibtexImporter.cancel'/></a>			       
									    <span ng-hide="worksFromBibtex.length > 0" class="import-label" ng-click="openFileDialog()"><@orcid.msg 'workspace.bibtexImporter.fileUpload'/></span>
									    <span ng-show="worksFromBibtex.length > 0" class="import-label" ng-click="saveAllFromBibtex()">Save all</span>									    							           
										<input id="inputBibtex" type="file" class="upload-button" ng-model="textFiles" accept="*" update-fn="loadBibtexJs()"  app-file-text-reader multiple />
									</span>								    
							   </div>
						   </div>
						</div>						
						<div class="alert alert-block" ng-show="bibtexParsingError">
							<strong><@orcid.msg 'workspace.bibtexImporter.parsingError'/></strong>
						</div>
						<span class="dotted-bar" ng-show="worksFromBibtex.length > 0"></span>
					   	
					   	
					   	<!-- Bibtex Import Results List -->
					   	<div ng-repeat="work in worksFromBibtex" ng-cloak class="bottomBuffer">					   	
					   		  <div class="row full-height-row">	  
			        	       	  <div class="col-md-9 col-sm-9 col-xs-9">
			        	          	<h3 ng-show="{{work.title.value != null}}" class="workspace-title bibtex-work-title">{{work.title.value}}</h3>
			        	          	<h3 ng-show="{{work.title.value == null}}" class="workspace-title bibtex-work-title bibtex-content-missing">&lt;<@orcid.msg 'workspace.bibtexImporter.work.title_missing' />&gt;</h3>
			        	          	
			        	          	
			        	          	<!-- Work Category --> 
			        	          	<span class="info-detail" ng-show="{{work.workCategory.value.length > 0}}">{{work.workCategory.value | formatBibtexOutput}}</span>
			        	          	<span class="bibtex-content-missing small-missing-info" ng-show="{{work.workCategory.value.length == 0}}">&lt;<@orcid.msg 'workspace.bibtexImporter.work.category_missing' />&gt;</span>
			        	          	
			        	          	<!-- Work Type -->
			        	          	<span class="info-detail" ng-show="{{work.workType.value.length > 0}}">{{work.workType.value | formatBibtexOutput}}</span>
			        	          	<span class="bibtex-content-missing small-missing-info" ng-show="{{work.workType.value.length == 0}}">&lt;<@orcid.msg 'workspace.bibtexImporter.work.type_missing' />&gt;</span>
			        	          	
			        	          	<!-- External identifiers -->
			        	          	<span class="info-detail" ng-show="{{work.workExternalIdentifiers[0].workExternalIdentifierType.value.length > 0}}">
			        	          		<span ng-repeat='ie in work.workExternalIdentifiers'><span
		                                     ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
		                                </span>
			        	          	</span>
			        	          	<!-- 
			        	          		<span class="info-detail bibtex-content-missing" ng-show="{{work.workExternalIdentifiers[0].workExternalIdentifierType.value.length == 0}}"">&lt;<@orcid.msg 'workspace.bibtexImporter.work.external_id_missing' />&gt;</span>
			        	          	 -->
			        	          
			        	          </div>			        	          
			        	          <div class="col-md-3 col-sm-3 col-xs-3 bibtex-options-menu">			        	          	
			        	          	<ul>
                                        <li><a ng-click="rmWorkFromBibtex(work)" class="ignore glyphicon glyphicon-trash bibtex-button" title="Ignore"></a></li>
                                        <li><a ng-show="{{work.errors.length == 0}}" ng-click="addWorkFromBibtex(work)" class="save glyphicon glyphicon-floppy-disk bibtex-button" title="Save"></a></li>
                                        <li><a ng-show="{{work.errors.length > 0}}" ng-click="editWorkFromBibtex(work)" class="save glyphicon glyphicon-pencil bibtex-button" title="Edit"></a></li>
                                        <li><span ng-show="{{work.errors.length > 0}}"><a ng-click="editWorkFromBibtex(work)"><i class="glyphicon glyphicon-exclamation-sign"></i><@orcid.msg 'workspace.bibtexImporter.work.warning' /></a></span></li>
			        	          	</ul>
		        	          	 </div>
	        	          	 </div>
		        	  	</div>
		        	  	
		        	  	
					</div>
      	            <div ng-show="workspaceSrvc.displayWorks" class="workspace-accordion-content">
	            		<#include "includes/work/add_work_modal_inc.ftl"/>
						<#include "includes/work/del_work_modal_inc.ftl"/>
						<#include "includes/work/body_work_inc_v3.ftl"/>						
	            	</div>
            	</div>
            	<div ng-controller="PeerReviewCtrl">
	            	<div ng-show="peerReviewSrvc.groups.length > 0" ng-cloak>
	            		<#include "workspace_peer_review_body_list.ftl"/>
	            	</div>
	            </div>
            </div>
        </div>
    </div>    
</div>
</#escape>

<script type="text/ng-template" id="verify-email-modal">	
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h4><@orcid.msg 'workspace.your_primary_email'/></h4>
				<@orcid.msg 'workspace.ensure_future_access'/>
				<br />
				<br />						
				<button class="btn btn-primary" id="modal-close" ng-click="verifyEmail()"><@orcid.msg 'workspace.send_verification'/></button>
				<button class="btn" id="modal-close" ng-click="closeColorBox()"><@orcid.msg 'freemarker.btncancel'/></button>								
			</div>
		</div>		
	</div>		
</script>

<script type="text/ng-template" id="combine-work-template">
	<div class="lightbox-container">
		<div class="row combine-work">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3>Selected work "{{combineWork.title.value}}"				
					<span ng-show="hasCombineableEIs(combineWork)">
						(<span ng-repeat='ie in combineWork.workExternalIdentifiers'>
							<span ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:combineWork.workExternalIdentifiers.length'></span>
					 	</span>)
					</span>				
				</h3>
				<p>Combine with (select one):</p>
				<ul class="list-group">
  					<li class="list-group-item" ng-repeat="group in worksSrvc.groups | orderBy:sortState.predicate:sortState.reverse" ng-show="combineWork.putCode.value != group.getDefault().putCode.value && validCombineSel(combineWork,group.getDefault())">
						<strong>{{group.getDefault().title.value}}</strong>
						<a ng-click="combined(combineWork,group.getDefault())" class="btn btn-primary pull-right bottomBuffer">Combine</a>

					</li>  					
				</ul>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<button class="btn close-button pull-right" id="modal-close" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel'/></button>
			</div>
		</div>
	</div>
	
</script>






<script type="text/ng-template" id="verify-email-modal-sent">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<h4><@orcid.msg 'workspace.sent'/></h4>
				<@orcid.msg 'workspace.check_your_email'/><br />
				<br />
				<button class="btn" ng-click="closeColorBox()"><@orcid.msg 'freemarker.btnclose'/></button>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="claimed-record-thanks">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<strong><@spring.message "orcid.frontend.web.record_claimed"/></strong><br />
				<br />
				<button class="btn btn-primary" ng-click="close()"><@spring.message "freemarker.btnclose"/></button>
			</div>
		</div>
	</div>
</script>
	
<script type="text/ng-template" id="claimed-record-thanks-source-grand-read">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<strong><@spring.message "orcid.frontend.web.record_claimed"/></strong><br />
				<br />
				<strong ng-bind="sourceGrantReadWizard.displayName"></strong> <@spring.message "orcid.frontend.web.record_claimed.would_like"/><br />
				<br />
				<button class="btn btn-primary" ng-click="yes()"><@spring.message "orcid.frontend.web.record_claimed.yes_go_to" /></button>
				<button class="btn btn-primary" ng-click="close()"><@spring.message "orcid.frontend.web.record_claimed.no_thanks" /></button>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="delete-external-id-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<h3><@orcid.msg 'manage.deleteExternalIdentifier.pleaseConfirm'/> {{removeExternalModalText}} </h3>
				<button class="btn btn-danger" ng-click="removeExternalIdentifier()"><@orcid.msg 'freemarker.btnDelete'/></button> 
				<a ng-click="closeEditModal()"><@orcid.msg 'freemarker.btncancel'/></a>
			<div>
		<div>
	<div>	
</script>

<script type="text/ng-template" id="bulk-delete-modal">
	<div class="lightbox-container bulk-delete-modal">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<h3><@orcid.msg 'groups.bulk_delete.confirm.header'/></h3>
				<div class="orcid-error">
					<p>
						<@orcid.msg 'groups.bulk_delete.confirm.line_1'/>
					</p>
					<p>
						<@orcid.msg 'groups.bulk_delete.confirm.line_2'/>
					</p>
					<p ng-class="{'red-error':bulkDeleteSubmit == true}">
    	            	<@orcid.msg 'groups.bulk_delete.confirm.line_3'/> <input ng-class="{'red-border-error':bulkDeleteSubmit == true}" type="text" size="3" ng-init="delCountVerify=0" ng-model="delCountVerify"/>
					</p>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
				<div class="right">			
					<button class="btn btn-danger" ng-click="bulkDeleteFunction()"><@orcid.msg 'freemarker.btnDelete'/></button>&nbsp;&nbsp;
					<a ng-click="closeModal()">
						<@orcid.msg 'freemarker.btncancel'/>
					</a>
				</div>				
			</div>
		</div>		
	</div>	
</script>


<script type="text/ng-template" id="import-wizard-modal">
    <#if ((workImportWizards)??)>		
    	<div id="third-parties">
			<div class="ie7fix-inner">
			<div class="row">	
				<div class="col-md-12 col-sm-12 col-xs-12">					
					<a class="btn pull-right close-button" ng-click="closeModal()">X</a>
	           		<h1 class="lightbox-title" style="text-transform: uppercase;"><@orcid.msg 'workspace.link_works'/></h1>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
	    	    	<div class="justify">
						<p><@orcid.msg 'workspace.LinkResearchActivities.description'/></p>
					</div>            	    	           	
    		    	<#list workImportWizards?sort_by("displayName") as thirdPartyDetails>
	        	       	<#assign redirect = (thirdPartyDetails.redirectUris.redirectUri[0].value) >
            	   		<#assign predefScopes = (thirdPartyDetails.redirectUris.redirectUri[0].scopeAsSingleString) >
                   		<strong><a ng-click="openImportWizardUrl('<@orcid.rootPath '/oauth/authorize?client_id=${thirdPartyDetails.clientId}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>')">${thirdPartyDetails.displayName}</a></strong><br />
                 		<div class="justify">
							<p>
								${(thirdPartyDetails.shortDescription)!}
							</p>
						</div>
                   		<#if (thirdPartyDetails_has_next)>
	                      	<hr/>
						</#if>
                		</#list>
				</div>
			</div>                 
            <div class="row footer">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<p>
				   		<strong><@orcid.msg 'workspace.LinkResearchActivities.footer.title'/></strong>	    
	        			<@orcid.msg 'workspace.LinkResearchActivities.footer.description1'/> <a href="<@orcid.msg 'workspace.LinkResearchActivities.footer.description.url'/>"><@orcid.msg 'workspace.LinkResearchActivities.footer.description.link'/></a> <@orcid.msg 'workspace.LinkResearchActivities.footer.description2'/>
			    	</p>
				</div>
	        </div>
		</div>
		</div>
	</#if>
</script>

<script type="text/ng-template" id="import-funding-modal">
    <#if ((fundingImportWizards)??)>		
    	<div id="third-parties">
			<div class="ie7fix-inner">
			<div class="row">	
				<div class="col-md-12 col-sm-12 col-xs-12">					
					<a class="btn pull-right close-button" ng-click="closeModal()">X</a>
	           		<h1 class="lightbox-title" style="text-transform: uppercase;"><@orcid.msg 'workspace.link_funding'/></h1>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
	    	    	<div class="justify">
						<p><@orcid.msg 'workspace.LinkResearchActivities.description'/></p>
					</div>            	    	           	
    		    	<#list fundingImportWizards?sort_by("displayName") as thirdPartyDetails>
	        	       	<#assign redirect = (thirdPartyDetails.redirectUris.redirectUri[0].value) >
            	   		<#assign predefScopes = (thirdPartyDetails.redirectUris.redirectUri[0].scopeAsSingleString) >
                   		<strong><a ng-click="openImportWizardUrl('<@orcid.rootPath '/oauth/authorize?client_id=${thirdPartyDetails.clientId}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>')">${thirdPartyDetails.displayName}</a></strong><br />
                 		<div class="justify">
							<p>
								${(thirdPartyDetails.shortDescription)!}
							</p>
						</div>
                   		<#if (thirdPartyDetails_has_next)>
	                      	<hr/>
						</#if>
                		</#list>
				</div>
			</div>                 
            <div class="row footer">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<p>
				   		<strong><@orcid.msg 'workspace.LinkResearchActivities.footer.title'/></strong>	    
	        			<@orcid.msg 'workspace.LinkResearchActivities.footer.description1'/> <a href="<@orcid.msg 'workspace.LinkResearchActivities.footer.description.url'/>"><@orcid.msg 'workspace.LinkResearchActivities.footer.description.link'/></a> <@orcid.msg 'workspace.LinkResearchActivities.footer.description2'/>
			    	</p>
				</div>
	        </div>
		</div>
		</div>
	</#if>
</script>

<#include "/includes/record/record_modals.ftl">
<#include "/includes/record/email_settings.ftl">

</@protected>  