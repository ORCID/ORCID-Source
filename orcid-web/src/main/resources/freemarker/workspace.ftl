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

<div class="row workspace-top public-profile">

	<#-- hidden divs that trigger angular -->
	<#if RequestParameters['recordClaimed']??>
	    <div ng-controller="ClaimThanks" style="display: hidden;"></div>	    
	<#elseif !Session.CHECK_EMAIL_VALIDATED?exists>
    	<div ng-controller="VerifyEmailCtrl" style="display: hidden;"></div>
	</#if>

    <div class="col-md-3 lhs">
    	<div class="workspace-profile">
            <h2 class="full-name">
                <#if (profile.orcidBio.personalDetails.creditName.content)??>
                    ${(profile.orcidBio.personalDetails.creditName.content)!}
                <#else>
                    ${(profile.orcidBio.personalDetails.givenNames.content)!} ${(profile.orcidBio.personalDetails.familyName.content)!}
                </#if>
            </h2>
            <p><small id="orcid-id" class="orcid-id">${baseUriHttp}/${(profile.orcid.value)!}</small></p>
	        <p class="hoover-white-fonts"><a href="${baseUriHttp}/${(profile.orcid.value)!}" class="label btn-primary"><@orcid.msg 'workspace.ViewPublicORCIDRecord'/></a></p>
	        <#if ((profile.orcidBio.personalDetails.otherNames.otherName)?size != 0)>
	        	<p><strong><@orcid.msg 'workspace.Alsoknownas'/></strong><br />
		       		<#list profile.orcidBio.personalDetails.otherNames.otherName as otherName>
		       			${otherName.content}<#if otherName_has_next><br /></#if>
		       		</#list></p>
	       	</#if>
            <#if (profile.orcidBio.contactDetails.address.country.content)??>
                <p><strong><@orcid.msg 'public_profile.labelCountry'/></strong>
                ${(profile.orcidBio.contactDetails.address.country.content)!}
                </p>
            </#if>
	       	<#if (profile.orcidBio.keywords)?? && (profile.orcidBio.keywords.keyword?size != 0)>
	        	<p><strong><@orcid.msg 'public_profile.labelKeywords'/></strong> 
		       		<#list profile.orcidBio.keywords.keyword as keyword>
		       			${keyword.content}<#if keyword_has_next>,</#if>
		       		</#list></p>
	       	</#if>
	       	<#if (profile.orcidBio.researcherUrls)?? && (profile.orcidBio.researcherUrls.researcherUrl?size != 0)>
	        	<p><strong><@orcid.msg 'public_profile.labelWebsites'/></strong> <br/>
		       		<#list profile.orcidBio.researcherUrls.researcherUrl as url>
		       		   <a href="<@orcid.absUrl url.url/>" target="_blank"><#if (url.urlName.content)! != "">${url.urlName.content}<#else>${url.url.value}</#if></a><#if url_has_next><br/></#if>
		       		</#list></p>
	       	</#if>
       		<div ng-controller="ExternalIdentifierCtrl" ng-hide="!externalIdentifiersPojo.externalIdentifiers.length" ng-cloak>	       			
       			<p><strong><@orcid.msg 'public_profile.labelOtherIDs'/></strong></p>
       			<div ng-repeat='externalIdentifier in externalIdentifiersPojo.externalIdentifiers'>
		        			<span ng-hide="externalIdentifier.externalIdUrl">{{externalIdentifier.externalIdCommonName.content}} {{externalIdentifier.externalIdReference.content}}</span>
		        			<span ng-show="externalIdentifier.externalIdUrl"><a href="{{externalIdentifier.externalIdUrl.value}}" target="_blank">{{externalIdentifier.externalIdCommonName.content}} {{externalIdentifier.externalIdReference.content}}</a></span>
			   				<a ng-click="deleteExternalIdentifier($index)" class="glyphicon glyphicon-trash grey"></a>       			
       			</div>
			</div>							    
	        <@security.authorize ifAnyGranted="ROLE_ADMIN, ROLE_GROUP, ROLE_BASIC, ROLE_BASIC_INSTITUTION, ROLE_PREMIUM, ROLE_PREMIUM_INSTITUTION">
	        	 <p><a href="<@spring.url "/manage-clients" />">${springMacroRequestContext.getMessage("workspace.ManageClientCredentials")}</a></p>	        	 
	        </@security.authorize>
			<@security.authorize ifAnyGranted="ROLE_ADMIN">
				<p><a href="<@spring.url "/admin-actions" />"><@orcid.msg 'admin.workspace_link' /></a></p>
			</@security.authorize>
        </div>
    </div>
    <div class="col-md-9">
        <div class="workspace-right">
        	<div class="workspace-inner workspace-header" ng-controller="WorkspaceSummaryCtrl">
                <div class="alert alert-info" ng-show="showAddAlert()" ng-cloak><strong><@orcid.msg 'workspace.addinformationaboutyou'/></strong></div>
        		<div class="row">
        			<!-- Works -->
	        		<div class="workspace-overview col-md-3 col-sm-3 col-xs-3" id="works-overview">
	        			<a href="#workspace-publications" class="overview-count"><span ng-bind="worksSrvc.works.length"></span></a>
	        			<a href="#workspace-publications" class="overview-title"><@orcid.msg 'workspace.Works'/></a>
	                    <br />	                    	
	                    <a href="#workspace-publications" class="btn-update no-icon"><@orcid.msg 'workspace.view'/></a>	                    
	        		</div>
	        		<!-- Afilliations -->
					<#if RequestParameters['affiliations']??>
		                <div class="workspace-overview col-md-3 col-sm-3 col-xs-3" id="educations-overview">
		                    <a href="#workspace-educations" class="overview-count"><span ng-bind="affiliationsSrvc.educations.length"></span></a>
		                    <a href="#workspace-educations" class="overview-title"><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education'/></a>
		                    <br />
		                    <a href="#workspace-educations" class="btn-update no-icon"><@orcid.msg 'workspace.view'/></a>
		                </div>
		                <div class="workspace-overview col-md-3 col-sm-3 col-xs-3" id="educations-overview">
		                    <a href="#workspace-employments" class="overview-count"><span ng-bind="affiliationsSrvc.employments.length"></span></a>
		                    <a href="#workspace-employments" class="overview-title"><@orcid.msg 'workspace.view'/></a>
		                    <br />
		                    <a href="#workspace-employments" class="btn-update no-icon"><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.employment'/></a>
		                </div>
		             <#else>
		                <div class="workspace-overview col-md-3 col-sm-3 col-xs-3" id="affiliations-overview">
		                    <a href="#workspace-affiliations" class="overview-count"><span ng-bind="affiliationsSrvc.affiliations.length"></span></a>
		                    <a href="#workspace-affiliations" class="overview-title"><@orcid.msg 'workspace_bio.Affiliations'/></a>
		                    <br />
		                    <#if RequestParameters['affiliations']??>
		                        <a href="#workspace-affiliations" class="btn-update no-icon"><@orcid.msg 'workspace.view'/></a>
		                    <#else>
		                        <a target="_blank" href="http://support.orcid.org/forums/179657-coming-soon" class="btn-update no-icon"><@orcid.msg 'workspace.ComingSoon'/></a>
		                    </#if>
		                </div>
		             </#if>	                
	                <!-- Grants -->     
	        		<div class="workspace-overview  col-md-3 col-sm-3 col-xs-3">
	        			<a href="#workspace-grants" class="overview-count">${(profile.orcidActivities.orcidGrants.orcidGrant?size)!0}</a>
	        			<a href="#workspace-grants" class="overview-title"><@orcid.msg 'workspace.Grants'/></a>
	        			<br />
	        			<a target="_blank" href="http://support.orcid.org/forums/179657-coming-soon" class="btn-update no-icon"><@orcid.msg 'workspace.ComingSoon'/></a>
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
	        			   			<i class="icon-caret-down" ng-class="{'icon-caret-right':displayInfo==false}"></i></a>
	        			   		</a>
        			   			<a href="" ng-click="toggleDisplayInfo()" class="toggle-text"><@orcid.msg 'workspace.personal_information'/></a></li>
        			   		<li><a href="<@spring.url '/account/manage-bio-settings'/>" id="update-personal-modal-link" class="label btn-primary"><@orcid.msg 'workspace.Update'/></a></li>        			   		
        			   </ul>
        			</div>
            		<div class="workspace-accordion-content" ng-show="displayInfo">
            			<#include "workspace_personal.ftl"/>
        			</div>
            	</div>
            	<!-- Affiliations -->
                <#if RequestParameters['affiliations']??>
                	<#include "workspace_affiliations_body_list.ftl"/>
                </#if>
		        <!-- Works -->                
                <div id="workspace-publications" class="workspace-accordion-item workspace-accordion-active" ng-controller="WorkCtrl">
                	<div class="workspace-accordion-header">
                		<ul class="personal-inf-display">
                			<li>
		        				<a href="" ng-click="toggleDisplayWorks()" class="toggle-text">
		        			       <i class="icon-caret-down icon" ng-class="{'icon-caret-right':displayWorks==false}"></i></a>
		        			    </a> 
		        				<a href="" ng-click="toggleDisplayWorks()" class="toggle-text"><@orcid.msg 'workspace.Works'/></a>
		        			</li>		        			
							<li>
								<a class="label btn-primary" ng-click="showWorkImportWizard()"><@orcid.msg 'workspace.import_works'/></a>
							</li>	
							<li>
								<a href="" class="label btn-primary" ng-click="addWorkModal()"><@orcid.msg 'manual_work_form_contents.add_work_manually'/></a>
							</li>	
						</ul>					
					</div>
					
      	            <div ng-show="displayWorks" class="workspace-accordion-content">
	            		<#include "includes/work/add_work_modal_inc.ftl"/>
						<#include "includes/work/del_work_modal_inc.ftl"/>
						<#include "includes/work/body_work_inc.ftl"/>
	            	</div>
	            	
            	</div>
            	
            	<#--
        		<div id="workspace-grants" class="workspace-accordion-item">
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
				<@orcid.msg 'workspace.ensure_future_access'/><br />
				<br />
				<span class="btn btn-primary" id="modal-close" ng-click="verifyEmail()"><@orcid.msg 'workspace.send_verification'/></span>
				<span class="btn" id="modal-close" ng-click="closeColorBox()"><@orcid.msg 'freemarker.btncancel'/></span>
				</div>
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
				<button class="btn btn-danger" ng-click="removeExternalIdentifier()"><@orcid.msg 'manage.deleteExternalIdentifier.delete'/></button> 
				<a ng-click="closeModal()"><@orcid.msg 'manage.deleteExternalIdentifier.cancel'/></a>
			<div>
		<div>
	<div>	
</script>

<script type="text/ng-template" id="import-wizard-modal">
    <#if ((thirdPartiesForImport)??)>		
    	<div id="third-parties">
			<div class="ie7fix-inner">
			<div class="row">	
				<div class="col-md-12 col-sm-12 col-xs-12">					
					<a class="btn pull-right close-button" ng-click="closeModal()">X</a>
	           		<h1 class="lightbox-title" style="text-transform: uppercase;"><@orcid.msg 'workspace.import_works'/></h1>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
	    	    	<div class="justify">
						<p><@orcid.msg 'workspace.ImportResearchActivities.description'/></p>
					</div>            	    	           	
    		    	<#list thirdPartiesForImport?sort_by("displayName") as thirdPartyDetails>
	        	       	<#assign redirect = (thirdPartyDetails.redirectUris.redirectUri[0].value) >
            	   		<#assign predefScopes = (thirdPartyDetails.redirectUris.redirectUri[0].scopeAsSingleString) >
                   		<strong><a ng-click="openImportWizardUrl('<@spring.url '/oauth/authorize?client_id=${thirdPartyDetails.clientId}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>')">${thirdPartyDetails.displayName}</a></strong><br />
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
				   		<strong><@orcid.msg 'workspace.ImportResearchActivities.footer.title'/></strong>	    
	        			<@orcid.msg 'workspace.ImportResearchActivities.footer.description1'/> <a href="<@orcid.msg 'workspace.ImportResearchActivities.footer.description.url'/>"><@orcid.msg 'workspace.ImportResearchActivities.footer.description.link'/></a> <@orcid.msg 'workspace.ImportResearchActivities.footer.description2'/>
			    	</p>
				</div>
	        </div>
		</div>
		</div>
	</#if>
</script>
	
</@protected>
