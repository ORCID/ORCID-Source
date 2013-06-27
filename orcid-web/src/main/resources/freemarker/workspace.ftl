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

<div id="ng-app" ng-app="orcidApp" class="row workspace-top public-profile">

	<#-- hidden divs that trigger angular -->
	<#if RequestParameters['recordClaimed']??>
	    <div ng-controller="ClaimThanks" style="display: hidden;"></div>	    
	<#elseif !Session.CHECK_EMAIL_VALIDATED?exists>
    	<div ng-controller="VerifyEmailCtrl" style="display: hidden;"></div>
	</#if>

    <div class="span3 lhs">
    	<div class="workspace-left workspace-profile">
            <h2 class="full-name">
                <#if (profile.orcidBio.personalDetails.creditName.content)??>
                    ${(profile.orcidBio.personalDetails.creditName.content)!}
                <#else>
                    ${(profile.orcidBio.personalDetails.givenNames.content)!} ${(profile.orcidBio.personalDetails.familyName.content)!}
                </#if>
            </h2>
            <p><small id="orcid-id" class="orcid-id">${(profile.orcid.value)!}</small></p>
	        <p class="hoover-white-fonts"><a href="<@spring.url "/" + (profile.orcid.value)!"my-orcid/public" />" class="label btn-primary">${springMacroRequestContext.getMessage("workspace.ViewPublicORCIDRecord")}</a></p>
	        <p><a href="<@spring.url '/account/manage-bio-settings'/>" class="btn-update">${springMacroRequestContext.getMessage("workspace.Update")}</a></p>
	        <#if ((profile.orcidBio.personalDetails.otherNames.otherName)?size != 0)>
	        	<p><strong>${springMacroRequestContext.getMessage("workspace.Alsoknownas")}</strong><br />
		       		<#list profile.orcidBio.personalDetails.otherNames.otherName as otherName>
		       			${otherName.content}<#if otherName_has_next><br /></#if>
		       		</#list></p>
	       	</#if>
            <#if (profile.orcidBio.contactDetails.address.country.content)??>
                <p><strong>${springMacroRequestContext.getMessage("public_profile.labelCountry")}</strong>
                ${(profile.orcidBio.contactDetails.address.country.content)!}
                </p>
            </#if>
	       	<#if (profile.orcidBio.keywords)?? && (profile.orcidBio.keywords.keyword?size != 0)>
	        	<p><strong>${springMacroRequestContext.getMessage("public_profile.labelKeywords")}</strong> 
		       		<#list profile.orcidBio.keywords.keyword as keyword>
		       			${keyword.content}<#if keyword_has_next>,</#if>
		       		</#list></p>
	       	</#if>
	       	<#if (profile.orcidBio.researcherUrls)?? && (profile.orcidBio.researcherUrls.researcherUrl?size != 0)>
	        	<p><strong>${springMacroRequestContext.getMessage("public_profile.labelWebsites")}</strong> <br/>
		       		<#list profile.orcidBio.researcherUrls.researcherUrl as url>		       		
		       		   <a href="<@orcid.absUrl url.url/>"><#if (url.urlName.content)! != "">${url.urlName.content}<#else>${url.url.value}</#if></a><#if url_has_next><br/></#if>
		       		</#list></p>
	       	</#if>
       		<div ng-controller="ExternalIdentifierCtrl" ng-hide="!externalIdentifiersPojo.externalIdentifiers.length" ng-cloak>	       			
       			<p><strong>${springMacroRequestContext.getMessage("public_profile.labelOtherIDs")}</strong> </p>
		        <table id="externalIdentifierTable">
		        	<tr style="vertical-align:bottom;" ng-repeat='externalIdentifier in externalIdentifiersPojo.externalIdentifiers'>
		        		<td class="padRgt">
		        			<p ng-hide="externalIdentifier.externalIdUrl">{{externalIdentifier.externalIdCommonName.content}} {{externalIdentifier.externalIdReference.content}}</p>
		        			<p ng-show="externalIdentifier.externalIdUrl"><a ng-href="{{externalIdentifier.externalIdUrl.value}}">{{externalIdentifier.externalIdCommonName.content}} {{externalIdentifier.externalIdReference.content}}</a></p>
		     			</td>
			   			<td class="padRgt">
			   				<p><a href ng-click="deleteExternalIdentifier($index)" class="icon-trash grey"></a></p>
			   			</td>		        		
		        	</tr>
		        </table>
			</div>
		    <#if ((thirdPartiesForImport)?? && (thirdPartiesForImport)?size &gt; 0)>
    	        <ul class="workspace-help">
    	        	<li><a href="#third-parties" class="colorbox-modal">${springMacroRequestContext.getMessage("workspace.ImportResearchActivities")}</a></li>
    	        </ul>
    	        <div class="inline-modal" id="third-parties">					
					<div class="span9">
	           			<h1 class="lightbox-title pull-left">${springMacroRequestContext.getMessage("workspace.ImportResearchActivities")?upper_case}</h1>
	           			<a class="btn pull-right close-button">X</a>
	           		</div>
	           		<br />          		
    	           	<div class="justify">${springMacroRequestContext.getMessage("workspace.ImportResearchActivities.description")}</div>
    	           	<br />    	           	
    	           	<#list thirdPartiesForImport as thirdPartyDetails>
                        <#assign redirect = (thirdPartyDetails.redirectUris.redirectUri[0].value) >
                        <#assign predefScopes = (thirdPartyDetails.redirectUris.redirectUri[0].scopeAsSingleString) >
                        <strong><a class="third-party-colorbox" href="<@spring.url '/oauth/authorize?client_id=${thirdPartyDetails.clientId}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>">${thirdPartyDetails.displayName}</a></strong><br />
                        <div class="justify">${(thirdPartyDetails.shortDescription)!}</div>
                        <#if (thirdPartyDetails_has_next)><hr /></#if>
                    </#list>
                    <br />
                    <div class="footer">
	                    <#noescape>
	                    	<strong>${springMacroRequestContext.getMessage("workspace.ImportResearchActivities.footer.title")}</strong>
	                    </#noescape>
	                    <br />
	                    ${springMacroRequestContext.getMessage("workspace.ImportResearchActivities.footer.description1")} <a href="${springMacroRequestContext.getMessage("workspace.ImportResearchActivities.footer.description.url")}">${springMacroRequestContext.getMessage("workspace.ImportResearchActivities.footer.description.link")}</a> ${springMacroRequestContext.getMessage("workspace.ImportResearchActivities.footer.description2")}
                    </div>
    	        </div>
	        </#if>
        </div>
    </div>
    <div class="span9">
        <div class="workspace-right">
        	<div class="workspace-inner workspace-header">
                <div class="alert alert-info"><strong>${springMacroRequestContext.getMessage("workspace.addinformationaboutyou")}</strong></div>
                <div class="workspace-overview">
                    <a href="#workspace-affiliations" class="overview-count">${(profile.orcidBio.affiliations?size)!0}</a>
                    <a href="#workspace-affiliations" class="overview-title">${springMacroRequestContext.getMessage("workspace_bio.Affiliations")}</a>
                    <div><a target="_blank" href="http://support.orcid.org/forums/179657-coming-soon" class="btn-update no-icon">${springMacroRequestContext.getMessage("workspace.ComingSoon")}</a></div>
                </div>
        		<div class="workspace-overview">
        			<a href="#workspace-publications" class="overview-count">${(profile.orcidActivities.orcidWorks.orcidWork?size)!0}</a>
        			<a href="#workspace-publications" class="overview-title">${springMacroRequestContext.getMessage("workspace.Works")}</a>
                    <div><a href="<@spring.url '/works-update'/>" class="btn-update">${springMacroRequestContext.getMessage("workspace.Update")}</a></div>
        		</div>
        		<div class="workspace-overview">
        			<a href="#workspace-grants" class="overview-count">${(profile.orcidActivities.orcidGrants.orcidGrant?size)!0}</a>
        			<a href="#workspace-grants" class="overview-title">${springMacroRequestContext.getMessage("workspace.Grants")}</a>
        			<br />
        			<a target="_blank" href="http://support.orcid.org/forums/179657-coming-soon" class="btn-update no-icon">${springMacroRequestContext.getMessage("workspace.ComingSoon")}</a>
        		</div>
        		<div class="workspace-overview">
        			<a href="#workspace-patents" class="overview-count">${(profile.orcidActivities.orcidPatents.orcidPatent?size)!0}</a>
        			<a href="#workspace-patents" class="overview-title">${springMacroRequestContext.getMessage("workspace.Patents")}</a>
        			<br />
        			<a target="_blank" href="http://support.orcid.org/forums/179657-coming-soon" class="btn-update no-icon">${springMacroRequestContext.getMessage("workspace.ComingSoon")}</a>
        		</div>
        	</div>
        	<div class="workspace-accordion" id="workspace-accordion">
        	
        	   <div id="workspace-personal" class="workspace-accordion-item workspace-accordion-active">
        			<h3 class="workspace-accordion-header"><a href="#">${springMacroRequestContext.getMessage("workspace.personal_information")}</a> <a href="<@spring.url '/account/manage-bio-settings'/>" class="btn-update">${springMacroRequestContext.getMessage("workspace.Update")}</a></h3>
            		<div class="workspace-accordion-content">
            			<#include "workspace_personal.ftl"/>
        			</div>
            	</div>
            	
        		<div id="workspace-affiliations" class="workspace-accordion-item${(!(profile.orcidBio.affiliations)?? || (profile.orcidBio.affiliations?size = 0))?string(" workspace-accordion-active", "")}">
                    <h3 class="workspace-accordion-header"><a href="#">${springMacroRequestContext.getMessage("workspace_bio.Affiliations")}</a></h3>
                </div>
                
                <div id="workspace-publications" class="workspace-accordion-item workspace-accordion-active" ng-controller="WorkCtrl">
                	<#if RequestParameters['addWorks']??>
        				<h3 class="workspace-accordion-header"><a href="#">${springMacroRequestContext.getMessage("workspace.Works")}</a></h3>
            		<#else>
        				<h3 class="workspace-accordion-header"><a href="#">${springMacroRequestContext.getMessage("workspace.Works")}</a> <a href="<@spring.url '/works-update'/>" class="btn-update">${springMacroRequestContext.getMessage("workspace.Update")}</a></h3>
					</#if>            		
					<div style="margin-left: 20px;">
						<a href="#third-parties" class="colorbox-modal label btn-primary">${springMacroRequestContext.getMessage("workspace.ImportResearchActivities")}</a>
						<a href="#" class="label btn-primary" ng-click="addWorkModal()">Add Research Activities</a>
					</div>
            		<div class="workspace-accordion-content">
            			<#include "workspace_works_body_list.ftl"/>
            		</div>
            	</div>
            	
        		<div id="workspace-grants" class="workspace-accordion-item<#--${(!(profile.orcidActivities.orcidGrants)??)?string(" workspace-accordion-active", "")}-->">
        			<h3 class="workspace-accordion-header"><a href="#">${springMacroRequestContext.getMessage("workspace.Grants")}</a></h3>
            	</div>
            	
        		<div id="workspace-patents" class="workspace-accordion-item<#--${(!(profile.orcidActivities.orcidPatents)??)?string(" workspace-accordion-active", "")}-->">
        			<h3 class="workspace-accordion-header"><a href="#">${springMacroRequestContext.getMessage("workspace.Patents")}</a></h3>
            	</div>
            	
            </div>
        </div>
    </div>    
</div>
</#escape>

<script type="text/ng-template" id="verify-email-modal">
	<div style="padding: 20px">
			<h4>${springMacroRequestContext.getMessage("workspace.your_primary_email")}</h4>
			${springMacroRequestContext.getMessage("workspace.ensure_future_access")}<br />
			<br />
			<span class="btn btn-primary" id="modal-close" ng-click="verifyEmail()">${springMacroRequestContext.getMessage("workspace.send_verification")}</span>
			<span class="btn" id="modal-close" ng-click="closeColorBox()">${springMacroRequestContext.getMessage("freemarker.btncancel")}</span>
		</div>
</script>

<script type="text/ng-template" id="verify-email-modal-sent">
	<div style="padding: 20px; width: 400px;">
		<h4>${springMacroRequestContext.getMessage("workspace.sent")}</h4>
		${springMacroRequestContext.getMessage("workspace.check_your_email")}<br />
		<br />
		<span class="btn" ng-click="closeColorBox()">${springMacroRequestContext.getMessage("freemarker.btnclose")}</span>
	</div>
</script>

<script type="text/ng-template" id="claimed-record-thanks">
	<div style="padding: 20px;">
		<strong><@spring.message "orcid.frontend.web.record_claimed"/></strong><br />
		<br />
		<button class="btn" ng-click="close()"><@spring.message "freemarker.btnclose"/></button>
	</div>
</script>
	
<script type="text/ng-template" id="claimed-record-thanks-source-grand-read">
	<div style="padding: 20px;">
		<strong><@spring.message "orcid.frontend.web.record_claimed"/></strong><br />
		<br />
		<strong ng-bind="sourceGrantReadWizard.displayName"></strong> <@spring.message "orcid.frontend.web.record_claimed.would_like"/><br />
		<br />
		<button class="btn btn-primary" ng-click="yes()"><@spring.message "orcid.frontend.web.record_claimed.yes_go_to" /></button>
		<button class="btn" ng-click="close()"><@spring.message "orcid.frontend.web.record_claimed.no_thanks" /></button>
	</div>
</script>

<script type="text/ng-template" id="delete-external-id-modal">
	<div style="padding: 20px;">
		<h3>${springMacroRequestContext.getMessage("manage.deleteExternalIdentifier.pleaseConfirm")} {{removeExternalModalText}} </h3>
		<button class="btn btn-danger" ng-click="removeExternalIdentifier()">${springMacroRequestContext.getMessage("manage.deleteExternalIdentifier.delete")}</button> 
		<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteExternalIdentifier.cancel")}</a>
	<div>
</script>

<script type="text/ng-template" id="add-work-modal">
	<div class="row">
		
		<div class="span12">
			<h3>Add Work</h3>
		</div>
		
		<div class="span6">	
			<div class="control-group">
				<label class="control-label">${springMacroRequestContext.getMessage("manual_work_form_contents.labeltitle")}</label>
			    <div class="relative">
					<input name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.workTitle.title.value" placeholder="Add ${springMacroRequestContext.getMessage("manual_work_form_contents.labeltitle")}" ng-change="serverValidate('my-orcid/work/workTitle/titleValidate.json')" ng-model-onblur/>
					<span class="required" ng-class="isValidClass(editWork.workTitle.title)">*</span>
					<span class="orcid-error" ng-show="editWork.workTitle.title.errors.length > 0">
						<div ng-repeat='error in editWork.workTitle.title.errors' ng-bind-html-unsafe="error"></div>
					</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">${springMacroRequestContext.getMessage("manual_work_form_contents.labelsubtitle")}</label>
			    <div class="relative">
					<input name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.workTitle.subtitle.value" placeholder="Add ${springMacroRequestContext.getMessage("manual_work_form_contents.labelsubtitle")}" ng-model-onblur/>
					<span class="orcid-error" ng-show="editWork.workTitle.subtitle.errors.length > 0">
						<div ng-repeat='error in editWork.workTitlesub.title.errors' ng-bind-html-unsafe="error"></div>
					</span>
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label">${springMacroRequestContext.getMessage("manual_work_form_contents.labelcitation")}</label>
			    <div class="relative">
					<textarea name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.citation.citation.value" placeholder="Add ${springMacroRequestContext.getMessage("manual_work_form_contents.labelcitation")}" ng-change="serverValidate('my-orcid/work/citationValidate.json')" ng-model-onblur/>
					<span class="orcid-error" ng-show="editWork.citation.citation.errors.length > 0">
						<div ng-repeat='error in editWork.citation.citation.errors' ng-bind-html-unsafe="error"></div>
					</span>
				</div>
			</div>
			
			<div class="control-group">
	    		<label class="relative">${springMacroRequestContext.getMessage("manual_work_form_contents.labelcitationtype")}</label>
	    		<div class="relative">
		    		<select id="citationType" name="citationType" class="input-xlarge" ng-model="editWork.citation.citationType.value" ng-change="serverValidate('my-orcid/work/citationValidate.json')">
						<#list citationTypes?keys as key>
							<option value="${key}">${citationTypes[key]}</option>
						</#list>
					</select> 
					<span class="orcid-error" ng-show="editWork.citation.citationType.errors.length > 0">
						<div ng-repeat='error in editWork.citation.citationType.errors' ng-bind-html-unsafe="error"></div>
					</span>
				</div>
			</div>
	 
			<div class="control-group">
	    		<label class="relative">${springMacroRequestContext.getMessage("manual_work_form_contents.labelworktype")}</label>
	    		<div class="relative">
		    		<select id="workType" name="workType" class="input-xlarge" ng-model="editWork.workType.value" ng-change="serverValidate('my-orcid/work/workTypeValidate.json')">
						<#list workTypes?keys as key>
							<option value="${key}">${workTypes[key]}</option>
						</#list>
					</select> 
					<span class="required" ng-class="isValidClass(editWork.workType)">*</span>
					<span class="orcid-error" ng-show="editWork.workType.errors.length > 0">
						<div ng-repeat='error in editWork.workType.errors' ng-bind-html-unsafe="error"></div>
					</span>
				</div>
			</div>
		</div>
		
		<div class="span6">
	 		<div class="control-group">
	    		<label class="relative" for="manualWork.day">Pub Date</label>
	    		<div class="relative">
			    <select id="day" name="day" ng-model="editWork.publicationDate.day" class="span1">
					<#list days?keys as key>
						<option value="${key}">${days[key]}</option>
					</#list>
	    		</select>
	
			    <select id="month" name="month" ng-model="editWork.publicationDate.month" class="span1">
					<#list months?keys as key>
						<option value="${key}">${months[key]}</option>
					</#list>
	    		</select>
	
			    <select id="year" name="month" ng-model="editWork.publicationDate.year" class="span1">
					<#list years?keys as key>
						<option value="${key}">${years[key]}</option>
					</#list>
	    		</select>
	    		</div>
	    	</div>
	    	
	   		<div class="control-group" ng-repeat="workExternalIdentifier in editWork.workExternalIdentifiers">
				<label class="control-label">${springMacroRequestContext.getMessage("manual_work_form_contents.labelID")}</label>
			    <div class="relative">
					<input name="currentWorkExternalIds" type="text" class="input-xlarge"  ng-model="workExternalIdentifier.workExternalIdentifierId.value" placeholder="Add ${springMacroRequestContext.getMessage("manual_work_form_contents.labelID")}" ng-model-onblur/>
						<span class="orcid-error" ng-show="workExternalIdentifier.workExternalIdentifierId.errors.length > 0">
							<div ng-repeat='error in workExternalIdentifier.workExternalIdentifierId.errors' ng-bind-html-unsafe="error"></div>
						</span>
				</div>
				<label class="relative">ID type</label>
				<div class="relative">
		    		<select id="workType" name="workType" class="input-xlarge" ng-model="workExternalIdentifier.workExternalIdentifierType.value">
						<#list idTypes?keys as key>
							<option value="${key}">${idTypes[key]}</option>
						</#list>
					</select> 
					<span class="orcid-error" ng-show="workExternalIdentifier.workExternalIdentifierType.errors.length > 0">
						<div ng-repeat='error in workExternalIdentifier.workExternalIdentifierType.errors' ng-bind-html-unsafe="error"></div>
					</span>
				</div>	
			</div>
			<div class="control-group">
	    		<label class="relative">${springMacroRequestContext.getMessage("manual_work_form_contents.labelURL")}</label>
	    		<div class="relative">
					<input name="url" type="text" class="input-xlarge"  ng-model="editWork.url.value" placeholder="Add ${springMacroRequestContext.getMessage("manual_work_form_contents.labelURL")}" ng-model-onblur/>
					<span class="orcid-error" ng-show="editWork.url.errors.length > 0">
						<div ng-repeat='error in editWork.url.errors' ng-bind-html-unsafe="error"></div>
					</span>
				</div>
			</div>
	
			<div class="control-group" ng-repeat="contributor in editWork.contributors">
			    <label class="relative">${springMacroRequestContext.getMessage("manual_work_form_contents.labelRole")}</label>
			    <div class="relative">    
					<select id="role" name="role" ng-model="contributor.contributorRole.value">
						<#list roles?keys as key>
							<option value="${key}">${roles[key]}</option>
						</#list>
		    		</select>
					<span class="orcid-error" ng-show="contributor.contributorRole.errors.length > 0">
							<div ng-repeat='error in contributor.contributorRole.errors' ng-bind-html-unsafe="error"></div>
					</span>
			    </div>
			</div>
	
			<div class="control-group" ng-repeat="contributor in editWork.contributors">
			    <label class="relative">${springMacroRequestContext.getMessage("manual_work_form_contents.labelcredited")}</label>
			    <div class="relative">    
					<select id="role" name="role" ng-model="contributor.contributorSequence.value">
						<#list sequences?keys as key>
							<option value="${key}">${sequences[key]}</option>
						</#list>
		    		</select>
					<span class="orcid-error" ng-show="contributor.contributorSequence.errors.length > 0">
							<div ng-repeat='error in contributor.contributorSequence.errors' ng-bind-html-unsafe="error"></div>
					</span>
			    </div>
			</div>
			
			<div class="control-group">
				<label class="control-label">${springMacroRequestContext.getMessage("manual_work_form_contents.labeldescription")}</label>
			    <div class="relative">
					<textarea name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.shortDescription.value" placeholder="Add ${springMacroRequestContext.getMessage("manual_work_form_contents.labeldescription")}" ng-model-onblur/>
					<span class="orcid-error" ng-show="editWork.shortDescription.errors.length > 0">
						<div ng-repeat='error in editWork.shortDescription.errors' ng-bind-html-unsafe="error"></div>
					</span>
				</div>
			</div>
		</div>
		
		<div class="span12">
		   &nbsp;
		</div>		
		<div class="span6">
		   &nbsp;
		</div>
		<div class="span6">
			<button class="btn btn-primary">${springMacroRequestContext.getMessage("manual_work_form_contents.btnaddtolist")}</button> 
			<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteExternalIdentifier.cancel")}</a>
		</div>
		<div class="span12">
		   &nbsp;
		</div>
	<div>
</script>

	
</@protected>
