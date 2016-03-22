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

<#if emailDoesntMatch?? && emailDoesntMatch>
	<div class="alert  alert-success">
		<@spring.message "orcid.frontend.security.invalid_deactivate_account"/>
	</div>
</#if>    

<div class="row workspace-top public-profile">

	<#-- hidden divs that trigger angular -->
	<#if RequestParameters['recordClaimed']??>
	    <div ng-controller="ClaimThanks" style="display: hidden;"></div>	    
	<#elseif !Session.CHECK_EMAIL_VALIDATED?exists && !inDelegationMode>
    	<div ng-controller="VerifyEmailCtrl" style="display: hidden;" orcid-loading="{{loading}}"></div>
	</#if>

    <div class="col-md-3 lhs left-aside">
    	<div class="workspace-profile">
            <#include "includes/id_banner.ftl"/>
            
            <#if RequestParameters['orcidSocial']??>
            <div class="share-this">
            	<span class='st_facebook_custom st_custom social' st_url="${baseUriHttp}/${(profile.orcidIdentifier.path)!}" st_title="<@orcid.msg 'orcid_social.facebook.message.title'/>" st_image="${staticCdn}/img/orcid-logo.png" st_summary="<@orcid.msg 'orcid_social.facebook.message.summary'/>"></span>
            	<span class='st_googleplus_custom st_custom social' st_url="${baseUriHttp}/${(profile.orcidIdentifier.path)!}" st_title="<@orcid.msg 'orcid_social.google.message.title'/>" st_image="${staticCdn}/img/orcid-logo.png" st_summary="<@orcid.msg 'orcid_social.google.message.summary'/>"></span>
				<span class='st_twitter_custom st_custom social' st_url="${baseUriHttp}/${(profile.orcidIdentifier.path)!}" st_title="<@orcid.msg 'orcid_social.twitter.message.title'/>" st_image="${staticCdn}/img/orcid-logo.png" st_summary="<@orcid.msg 'orcid_social.twitter.message.summary'/>"></span>
				<span class='st_linkedin_custom st_custom social' st_url="${baseUriHttp}/${(profile.orcidIdentifier.path)!}" st_title="<@orcid.msg 'orcid_social.linkedin.message.title'/>" st_image="${staticCdn}/img/orcid-logo.png" st_summary="<@orcid.msg 'orcid_social.linkedin.message.summary'/>"></span>
            </div>
            </#if>
            <!-- QR code -->
            <div>
                <a href="http://qrcode.orcid.org"><@orcid.msg 'workspace.qrcode.link.text'/></a>
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
            <!-- Also Known as -->
	       	<div class="other-names-box">
		       	<div ng-controller="OtherNamesCtrl" class="other-names-controller">
		        	<div class="profile-info-container">
		        	   <!-- Header -->	
		        	   <div class="profile-header">	
			        	   <strong><@orcid.msg 'workspace.Alsoknownas'/></strong>
			        	   <span class="glyphicon glyphicon-pencil edit-other-names edit-option" title="" ng-hide="showEdit == true" ng-click="openEdit()"></span>			        	   
		        	   </div>
		        	   <!-- Current content -->		        	   
		        	   <span ng-hide="showEdit == true" ng-click="openEdit()">		        	   	  
		        	      <span ng-repeat="otherNames in otherNamesForm.otherNames" ng-cloak class="wrap">
		        	         {{ $last?otherNames.value:otherNames.value+ ", "}}
		        	      </span>
		        	   </span>
		        	   <!-- Edit form -->
		        	   <div ng-show="showEdit == true" ng-cloak class="other-names-edit">
		        	      <@orcid.privacyToggle  angularModel="otherNamesForm.visibility.visibility"
				             questionClick="toggleClickPrivacyHelp()"
				             clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				             publicClick="setPrivacy('PUBLIC', $event)" 
	                 	     limitedClick="setPrivacy('LIMITED', $event)" 
	                 	     privateClick="setPrivacy('PRIVATE', $event)" />
		        	   
		        	      <div ng-repeat="otherNames in otherNamesForm.otherNames">
		        	          <input type="text" ng-model="otherNames.value" ng-enter="setOtherNamesForm()">
		        	          <a ng-click="deleteKeyword(otherNames)" class="glyphicon glyphicon-trash grey"></a>
		        	          <br />
		        	          <span class="orcid-error" ng-show="otherNames.url.errors.length > 0">
							     <div ng-repeat='error in otherNames.url.errors' ng-bind-html="error"></div>
						      </span>
		        	          <span class="orcid-error" ng-show="otherNames.name.errors.length > 0">
							     <div ng-repeat='error in otherNames.name.errors' ng-bind-html="error"></div>
						      </span>
		        	      </div>
		        	      <a class="glyphicon glyphicon-plus" ng-click="addNew()"></a><br />				        	      
		        	      <button class="btn btn-primary" ng-click="setOtherNamesForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		        	      <button class="btn" ng-click="close()"><@spring.message "freemarker.btncancel"/></button>
		        	   </div>
		             </div>
		       	</div>
	       	</div>
            
            <!-- Country -->
            <div ng-controller="CountryCtrl" class="country-controller">
            	<!-- Header -->
            	<div class="profile-header">		
		        	<strong><@orcid.msg 'public_profile.labelCountry'/></strong>
		            <span class="glyphicon glyphicon-pencil edit-country edit-option" ng-click="openEdit()" title="" ng-hide="showEdit == true"></span>
	            </div>   
	            <!-- Current content -->
                <span ng-hide="showEdit == true" ng-click="toggleEdit()">                	
	                <span ng-show="countryForm != null && countryForm.countryName != null" ng-bind="countryForm.countryName">
	                </span>
	            </span>
               <!-- Edit form -->
               <div ng-show="showEdit == true" ng-cloak class="country-edit">
               	  <@orcid.privacyToggle  angularModel="countryForm.profileAddressVisibility.visibility"
			         questionClick="toggleClickPrivacyHelp()"
			         clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
			         publicClick="setPrivacy('PUBLIC', $event)" 
                 	     limitedClick="setPrivacy('LIMITED', $event)" 
                 	     privateClick="setPrivacy('PRIVATE', $event)" />
                  
                  <select id="country" name="country" ng-model="countryForm.iso2Country.value">
		    			<option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
						<#list isoCountries?keys as key>
							<option value="${key}">${isoCountries[key]}</option>
						</#list>
				  </select>
	             <button class="btn btn-primary" ng-click="setCountryForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		         <button class="btn" ng-click="close()"><@spring.message "freemarker.btncancel"/></button>	              			  
				</div>				
            </div>
            <!-- Keywords -->
	       	<div class="keyword-box">
		       	<div ng-controller="KeywordsCtrl" class="keywords-controller">
		        	<div class="profile-header">
		        	   <strong><@orcid.msg 'public_profile.labelKeywords'/></strong>
		        	   <span ng-hide="showEdit == true" ng-click="openEdit()">
		        	      <span class="glyphicon glyphicon-pencil edit-keywords edit-option wrap" title=""></span><br />
		        	      <span ng-repeat="keyword in keywordsForm.keywords" ng-cloak class="wrap">
		        	         {{ $last?keyword.value:keyword.value+ ", "}}
		        	      </span>
		        	   </span>
		        	</div>
	        	    <div ng-show="showEdit == true" ng-cloak class="keywords-edit">
	        	      <@orcid.privacyToggle  angularModel="keywordsForm.visibility.visibility"
			             questionClick="toggleClickPrivacyHelp()"
			             clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
			             publicClick="setPrivacy('PUBLIC', $event)" 
                 	     limitedClick="setPrivacy('LIMITED', $event)" 
                 	     privateClick="setPrivacy('PRIVATE', $event)" />
	        	   
	        	      <div ng-repeat="keyword in keywordsForm.keywords">
	        	          <input type="text" ng-model="keyword.value"></input>
	        	          <a ng-click="deleteKeyword(keyword)" class="glyphicon glyphicon-trash grey"></a>
	        	          <br />
	        	          <span class="orcid-error" ng-show="keyword.url.errors.length > 0">
						     <div ng-repeat='error in keyword.url.errors' ng-bind-html="error"></div>
					      </span>
	        	          <span class="orcid-error" ng-show="keyword.name.errors.length > 0">
						     <div ng-repeat='error in keyword.name.errors' ng-bind-html="error"></div>
					      </span>
	        	      </div>
	        	      <a class="glyphicon glyphicon-plus" ng-click="addNew()"></a><br />
	        	      <button class="btn btn-primary" ng-click="setKeywordsForm()"><@spring.message "freemarker.btnsavechanges"/></button>
	        	      <button class="btn" ng-click="close()"><@spring.message "freemarker.btncancel"/></button>		        	    
		            </div>
		       	</div>
	       	</div>
	       	<!-- Websites -->
	       	<div class="websites-box">
		       	<div ng-controller="WebsitesCtrl" class="websites-controller">
		        	<div class="profile-header">
		        	   <strong><@orcid.msg 'public_profile.labelWebsites'/></strong>
		        	   <span ng-hide="showEdit == true">
		        	      <span class="glyphicon glyphicon-pencil edit-websites edit-option" ng-click="openEdit()" title=""></span><br />
		        	      <div ng-repeat="website in websitesForm.websites" ng-cloak class="website-url">
		        	         <a href="{{website.url.value}}" target="_blank" rel="nofollow">{{website.name.value != null? website.name.value : website.url.value}}</a>
		        	      </div>
		        	   </span>
		        	</div>
		        	<div>
		        	   <div ng-show="showEdit == true" ng-cloak class="websites-edit">
		        	      <@orcid.privacyToggle  angularModel="websitesForm.visibility.visibility"
				             questionClick="toggleClickPrivacyHelp()"
				             clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				             publicClick="setPrivacy('PUBLIC', $event)" 
	                 	     limitedClick="setPrivacy('LIMITED', $event)" 
	                 	     privateClick="setPrivacy('PRIVATE', $event)" />
		        	   
		        	      <div ng-repeat="website in websitesForm.websites" class="mobile-box">
		        	          <input type="text" ng-model="website.name.value" placeholder="${springMacroRequestContext.getMessage("manual_work_form_contents.labeldescription")}"></input>
		        	          <input type="text" ng-model="website.url.value" placeholder="${springMacroRequestContext.getMessage("common.url")}"></input>
		        	          <a ng-click="deleteWebsite(website)" class="glyphicon glyphicon-trash grey"></a>
		        	          <br />
		        	          <span class="orcid-error" ng-show="website.url.errors.length > 0">
							     <div ng-repeat='error in website.url.errors' ng-bind-html="error"></div>
						      </span>
		        	          <span class="orcid-error" ng-show="website.name.errors.length > 0">
							     <div ng-repeat='error in website.name.errors' ng-bind-html="error"></div>
						      </span>
		        	      </div>
		        	      <a class="glyphicon glyphicon-plus" ng-click="addNew()"></a><br />
		        	      <button class="btn btn-primary" ng-click="setWebsitesForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		        	      <button class="btn" ng-click="close()"><@spring.message "freemarker.btncancel"/></button>
		        	   </div>
		        	</div>
		       	</div>
	       	</div>
	       	<!-- External Identifiers -->
       		
       		<div ng-controller="ExternalIdentifierCtrl" ng-hide="!externalIdentifiersPojo.externalIdentifiers.length" ng-cloak>	       			
       			<p><strong><@orcid.msg 'public_profile.labelOtherIDs'/></strong></p>
       			<div class="row" id="external-identifiers">
	       			<div ng-repeat='externalIdentifier in externalIdentifiersPojo.externalIdentifiers'>
	       				<div class="col-md-10 col-sm-10 col-xs-10">	       						
			        			<span ng-hide="externalIdentifier.externalIdUrl">{{externalIdentifier.externalIdCommonName.content}} {{externalIdentifier.externalIdReference.content}}</span>
			        			<span ng-show="externalIdentifier.externalIdUrl"><a href="{{externalIdentifier.externalIdUrl.value}}" target="_blank">{{externalIdentifier.externalIdCommonName.content}} {{externalIdentifier.externalIdReference.content}}</a></span>
			        	</div>
			        	<div class="col-md-2 col-sm-2 col-xs-2">
				   			<a ng-click="deleteExternalIdentifier($index)" class="glyphicon glyphicon-trash grey right"></a>
				   		</div>       			
	       			</div>
       			</div>
       			
			</div>
        </div>
    </div>
    <div class="col-md-9 right-aside">
        <div class="workspace-right">
        	<div class="workspace-inner workspace-header" ng-controller="WorkspaceSummaryCtrl">
                <div class="alert alert-info" ng-show="showAddAlert()" ng-cloak><strong><@orcid.msg 'workspace.addinformationaboutyou'/></strong></div>
        		<div class="row">
        			<!-- Works -->
	        		<div class="workspace-overview col-md-3 col-sm-3 col-xs-6" id="works-overview">
	        			<ul>
	        				<li><a href="#workspace-publications" class="overview-count" ng-click="workspaceSrvc.openWorks()"><span ng-bind="worksSrvc.workCount()"></span></a></li>
	        				<li><a href="#workspace-publications" class="overview-title" ng-click="workspaceSrvc.openWorks()"><@orcid.msg 'workspace.Works'/></a></li>
	        			</ul>
	                    <a href="#workspace-publications" class="btn-update no-icon" ng-click="workspaceSrvc.openWorks()"><@orcid.msg 'workspace.view'/></a>	                    
	        		</div>
		            <div class="workspace-overview col-md-3 col-sm-3 col-xs-6" id="educations-overview">
		            	<ul>
		            		<li><a href="#workspace-educations" class="overview-count" ng-click="workspaceSrvc.openEducation()"><span ng-bind="affiliationsSrvc.educations.length"></span></a></li>
		            		<li><a href="#workspace-educations" class="overview-title" ng-click="workspaceSrvc.openEducation()"><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education'/></a></li>
		            	</ul>
		                <a href="#workspace-educations" class="btn-update no-icon" ng-click="workspaceSrvc.openEducation()"><@orcid.msg 'workspace.view'/></a>
		            </div>
		            <div class="workspace-overview col-md-3 col-sm-3 col-xs-6" id="employments-overview">
		            	<ul>
		            		<li><a href="#workspace-employments" class="overview-count" ng-click="workspaceSrvc.openEmployment()"><span ng-bind="affiliationsSrvc.employments.length"></span></a></li>
		            		<li><a href="#workspace-employments" class="overview-title" ng-click="workspaceSrvc.openEmployment()"><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.employment'/></a></li>
		            	</ul>
		                <a href="#workspace-employments" class="btn-update no-icon" ng-click="workspaceSrvc.openEmployment()"><@orcid.msg 'workspace.view'/></a>
		             </div>
					<div class="workspace-overview col-md-3 col-sm-3 col-xs-6">
						<ul>
							<li><a href="#workspace-fundings" class="overview-count" ng-click="workspaceSrvc.openFunding()"><span ng-bind="fundingSrvc.fundingCount()"></span></a></li>
							<li><a href="#workspace-fundings" class="overview-title" ng-click="workspaceSrvc.openFunding()"><@orcid.msg 'workspace.Funding'/></a></li>
						</ul>
        				<a href="#workspace-employments" class="btn-update no-icon" ng-click="workspaceSrvc.openFunding()"><@orcid.msg 'workspace.view'/></a>
        			</div>
	        	</div>
        	</div>
        	<div class="workspace-accordion" id="workspace-accordion">
        		<!-- Personal Information -->
        	   <div id="workspace-personal" class="workspace-accordion-item workspace-accordion-active" ng-controller="PersonalInfoCtrl">
        			<div class="workspace-accordion-header" style="position: relative;">
        			   <ul class="personal-inf-display">        			   		
        			   		<li>
        			   			<a href="" ng-click="toggleDisplayInfo()" class="toggle-text">
	        			   			<i class="glyphicon-chevron-down glyphicon x0" ng-class="{'glyphicon-chevron-right':displayInfo==false}"></i></a>
	        			   		</a>
        			   			<a href="" ng-click="toggleDisplayInfo()" class="toggle-text"><@orcid.msg 'workspace.personal_information'/></a>
        			   		</li>        			   		
        			   </ul>
        			</div>
            		<div class="workspace-accordion-content" ng-show="displayInfo">
            			<#include "workspace_personal.ftl"/>
        			</div>
            	</div>
            	<!-- Affiliations -->
                <#include "workspace_affiliations_body_list.ftl"/>
                <!-- Fundings -->
               	<#include "workspace_fundings_body_list.ftl"/>
               	<#if RequestParameters['peer']??>
               		<!-- Peer Review -->]
               		<#include "workspace_peer_review_body_list.ftl"/>
 				</#if>
		        <!-- Works -->                
                <div id="workspace-publications" class="workspace-accordion-item workspace-accordion-active" ng-controller="WorkCtrl">
                	<div class="workspace-accordion-header">
                		<ul class="personal-inf-display">
                			<li>
		        				<a href="" ng-click="workspaceSrvc.toggleWorks()" class="toggle-text">
		        			       <i class="glyphicon-chevron-down glyphicon x0" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayWorks==false}"></i>
		        			    </a> 
		        				<a href="" ng-click="workspaceSrvc.toggleWorks()" class="toggle-text"><@orcid.msg 'workspace.Works'/></a>
		        			</li>		        			
							<li>
								<a class="label btn-primary" ng-click="showWorkImportWizard()"><@orcid.msg 'workspace.link_works'/></a>
							</li>	
							<li>
								<a href="" class="label btn-primary" ng-click="addWorkModal()"><@orcid.msg 'manual_work_form_contents.add_work_manually'/></a>
							</li>
							<li>
								<a href="" class="label btn-primary" ng-click="openBibTextWizard()"><@orcid.msg 'workspace.bibtexImporter.link_bibtex'/></a>
							</li>
						</ul>					
					</div>
					<!-- Bibtex Importer Results -->
					<div ng-show="showBibtexImportWizard" ng-cloak class="bibtex-box">
						<div class="grey-box bottomBuffer box-border" ng-show="canReadFiles" ng-cloak>
						   <p class="bottomBuffer">
						   		<strong><@orcid.msg 'workspace.bibtexImporter.instructions'/>  <a href="http://support.orcid.org/knowledgebase/articles/390530" target="_blank"><@orcid.msg 'workspace.bibtexImporter.learnMore'/></a></strong>
						   </p> 
					       <div class="label btn-primary upload">
					           <span class="import-label"><@orcid.msg 'workspace.bibtexImporter.fileUpload'/></span>
						       <input type="file" class="upload-button" ng-model="textFiles" accept="*" update-fn="loadBibtexJs()"  app-file-text-reader multiple />
					       </div>
					       <span class="cancel-bibtex">
					        	<a href="" class="label btn-primary" ng-click="openBibTextWizard()"><@orcid.msg 'workspace.bibtexImporter.cancel'/></a>
						   </span>
						</div>						
						<div class="alert alert-block" ng-show="bibtexParsingError">
							<strong><@orcid.msg 'workspace.bibtexImporter.parsingError'/></strong>
						</div>
						<div class="row bottomBuffer" ng-show="bibtexCancelLink">
							<div class="col-md-10"></div>
							<div class="col-md-2">
								<button type="button" ng-click="bibtextCancel()" class="btn close-button pull-right"><@orcid.msg 'workspace.bibtexImporter.cancel'/></button>		
							</div>
						</div>
					   	<div ng-repeat="work in worksFromBibtex" ng-cloak class="grey-box bottomBuffer box-border">
					   		  <div class="row">	  
			        	       	  <div class="col-md-9">
			        	          	{{work.title.value}}
			        	          </div>
			        	          <div class="col-md-3 bibtex-options-menu">
			        	          	<ul>
			        	          		<li><a ng-click="rmWorkFromBibtex(work)" class="ignore glyphicon glyphicon-trash" title="Ignore"></a></li>
			        	          		<li><a ng-click="addWorkFromBibtex(work)" class="save glyphicon glyphicon-floppy-disk" title="Save"></a></li>
			        	          	</ul>
		        	          	 </div>
	        	          	 </div>
		        	  	</div>
					</div>
					
					
      	            <div ng-show="workspaceSrvc.displayWorks" class="workspace-accordion-content">
	            		<#include "includes/work/add_work_modal_inc.ftl"/>
						<#include "includes/work/del_work_modal_inc.ftl"/>
						<#include "includes/work/body_work_inc.ftl"/>
	            	</div>
	            	
            	</div>
            	
            	<#--
        		<div id="workspace-fundings" class="workspace-accordion-item">
        			<div class="workspace-accordion-header"><a href="#"><@orcid.msg 'workspace.Grants'/></a></div>
            	</div>
            	
        		<div id="workspace-patents" class="workspace-accordion-item">
        			<div class="workspace-accordion-header"><a href="#"><@orcid.msg 'workspace.Patents'/></a></div>
            	</div>
            	-->
            	
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
				<span class="btn btn-primary" id="modal-close" ng-click="verifyEmail()"><@orcid.msg 'workspace.send_verification'/></span>
				<span class="btn" id="modal-close" ng-click="closeColorBox()"><@orcid.msg 'freemarker.btncancel'/></span>								
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
				<span class="btn" ng-click="closeColorBox()"><@orcid.msg 'freemarker.btnclose'/></span>
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
				<button class="btn" ng-click="close()"><@spring.message "freemarker.btnclose"/></button>
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
				<button class="btn" ng-click="close()"><@spring.message "orcid.frontend.web.record_claimed.no_thanks" /></button>
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
				<a ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel'/></a>
			<div>
		<div>
	<div>	
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

</@protected>
