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
	
	
	<div style="padding: 20px;">
		<h3>Add Work</h3>
	
		<div class="control-group">
			<label class="control-label">${springMacroRequestContext.getMessage("manual_work_form_contents.labeltitle")}</label>
		    <div class="relative">
				<input name="familyNames" type="text" class="input-xlarge"  ng-model="newWork.workTitle.title.value" placeholder="Add ${springMacroRequestContext.getMessage("manual_work_form_contents.labeltitle")}" ng-model-onblur/>
				<span class="required" ng-class="isValidClass(newWork.workTitle.title)">*</span>
				<span class="orcid-error" ng-show="newWork.workTitle.title.errors.length > 0">
					<div ng-repeat='error in newWork.workTitle.title.errors' ng-bind-html-unsafe="error"></div>
				</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">${springMacroRequestContext.getMessage("manual_work_form_contents.labelsubtitle")}</label>
		    <div class="relative">
				<input name="familyNames" type="text" class="input-xlarge"  ng-model="newWork.workTitle.subtitle.value" placeholder="Add ${springMacroRequestContext.getMessage("manual_work_form_contents.labelsubtitle")}" ng-model-onblur/>
				<span class="orcid-error" ng-show="newWork.workTitle.subtitle.errors.length > 0">
					<div ng-repeat='error in newWork.workTitlesub.title.errors' ng-bind-html-unsafe="error"></div>
				</span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">${springMacroRequestContext.getMessage("manual_work_form_contents.labelcitation")}</label>
		    <div class="relative">
				<textarea name="familyNames" type="text" class="input-xlarge"  ng-model="newWork.citation.citation.value" placeholder="Add ${springMacroRequestContext.getMessage("manual_work_form_contents.labelcitation")}" ng-model-onblur/>
				<span class="orcid-error" ng-show="newWork.citation.citation.errors.length > 0">
					<div ng-repeat='error in newWork.citation.citation.errors' ng-bind-html-unsafe="error"></div>
				</span>
			</div>
		</div>
		
		<div class="control-group">
    		<label class="relative">${springMacroRequestContext.getMessage("manual_work_form_contents.labelcitationtype")}</label>
    		<div class="relative">
	    		<select id="citationType" name="citationType" class="input-xlarge" ng-model="newWork.citation.citationType.value">
					<#list citationTypes?keys as key>
						<option value="${key}">${citationTypes[key]}</option>
					</#list>
				</select> 
				<span class="orcid-error" ng-show="newWork.citation.citationType.errors.length > 0">
					<div ng-repeat='error in newWork.citation.citationType.errors' ng-bind-html-unsafe="error"></div>
				</span>
			</div>
		</div>
 
		<div class="control-group">
    		<label class="relative">${springMacroRequestContext.getMessage("manual_work_form_contents.labelworktype")}</label>
    		<div class="relative">
	    		<select id="workType" name="workType" class="input-xlarge" ng-model="newWork.workType.value">
					<#list workTypes?keys as key>
						<option value="${key}">${workTypes[key]}</option>
					</#list>
				</select> 
				<span class="required" ng-class="isValidClass(newWork.workType.title)">*</span>
				<span class="orcid-error" ng-show="newWork.workType.errors.length > 0">
					<div ng-repeat='error in newWork.workType.errors' ng-bind-html-unsafe="error"></div>
				</span>
			</div>
		</div>
		
 		<div class="control-group">
    		<label class="relative" for="manualWork.day">Pub Date</label>
    		<div class="relative">
		    <select id="day" name="day" ng-model="newWork.publicationDate.day" class="span1">
				<#list days?keys as key>
					<option value="${key}">${days[key]}</option>
				</#list>
    		</select>

		    <select id="month" name="month" ng-model="newWork.publicationDate.month" class="span1">
				<#list months?keys as key>
					<option value="${key}">${months[key]}</option>
				</#list>
    		</select>

		    <select id="year" name="month" ng-model="newWork.publicationDate.year" class="span1">
				<#list years?keys as key>
					<option value="${key}">${years[key]}</option>
				</#list>
    		</select>
    		</div>
    	</div>
    	
   		<div class="control-group" ng-repeat="workExternalIdentifier in newWork.workExternalIdentifiers">
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
    <label class="relative" for="manualWork.url">URL</label>
    <div class="relative">    <input type="text" id="url" name="url" value="" placeholder="Enter a link to the work" class="input-xlarge">

</div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.currentWorkContributors[0].role">Role</label>
    <div class="relative">    <select id="currentWorkContributors[0].role" name="currentWorkContributors[0].role" class="input-xlarge">
            <option value="" selected="selected">What was your role?</option>
            <option value="assignee">Assignee</option>
            <option value="author">Author</option>
            <option value="chair_or_translator">Chair or translator</option>
            <option value="co_inventor">Co inventor</option>
            <option value="co_investigator">Co investigator</option>
            <option value="editor">Editor</option>
            <option value="graduate_student">Graduate student</option>
            <option value="other_inventor">Other inventor</option>
            <option value="postdoctoral_researcher">Postdoctoral researcher</option>
            <option value="principal_investigator">Principal investigator</option>
            <option value="support_staff">Support staff</option>
    </select>

    </div>
</div>
<div class="control-group">
    <label class="relative">Credited</label>
    <div class="relative">
    <label for="currentWorkContributors[0].sequence0" class="radio radio-inline">First
            <input type="radio" id="currentWorkContributors[0].sequence0" name="currentWorkContributors[0].sequence" value="first" class="inline">

    </label>
    <label for="currentWorkContributors[0].sequence1" class="radio radio-inline">Additional
            <input type="radio" id="currentWorkContributors[0].sequence1" name="currentWorkContributors[0].sequence" value="additional" class="inline">

    </label>
    </div>
</div>
<div class="control-group"><label class="relative" for="manualWork.description">
    Description</label><div class="relative">     <textarea id="description" name="description" placeholder="A brief description" class="input-xlarge"></textarea>
</div>
</div>
<div class="control-group"><div class="control">
		<form action="save-work-manually" method="post" id="save-work-manually" class="">
<div class="msg">
    <span class="help-block">Some of your works may not be included in the search results. You can add them manually here.</span>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.title">Title</label>
    <div class="relative">    <input type="text" id="title" name="title" value="" placeholder="Add the full title here" class="input-xlarge">

<span class="required">*</span>
    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.subtitle">Subtitle</label>
    <div class="relative">    <input type="text" id="subtitle" name="subtitle" value="" placeholder="Add the subtitle here" class="input-xlarge">

</div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.citation">Citation</label>
    <div class="relative">    <textarea id="citation" name="citation" placeholder="Add the citation here" class="input-xlarge"></textarea>

    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.citationType">Citation Type</label>
    <div class="relative">    <select id="citationType" name="citationType" class="input-xlarge">
            <option value="">Pick a citation type</option>
            <option value="bibtex">BIBTEX</option>
            <option value="formatted-apa">APA</option>
            <option value="formatted-chicago">CHICAGO</option>
            <option value="formatted-harvard">HARVARD</option>
            <option value="formatted-ieee">IEEE</option>
            <option value="formatted-mla">MLA</option>
            <option value="formatted-unspecified" selected="selected">UNSPECIFIED</option>
            <option value="formatted-vancouver">VANCOUVER</option>
    </select>
</div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.workType">Work type</label>
    <div class="relative">    <select id="workType" name="workType" class="input-xlarge">
            <option value="" selected="selected">Pick a publication type</option>
            <option value="advertisement">Advertisement</option>
            <option value="audiovisual">Audiovisual</option>
            <option value="book">Book</option>
            <option value="brochure">Brochure</option>
            <option value="cartoon-comic">Cartoon comic</option>
            <option value="chapter-anthology">Chapter anthology</option>
            <option value="conference-proceedings">Conference proceedings</option>
            <option value="congressional-publication">Congressional publication</option>
            <option value="database">Database</option>
            <option value="dictionary-entry">Dictionary entry</option>
            <option value="digital-image">Digital image</option>
            <option value="dissertation">Dissertation</option>
            <option value="dissertation-abstract">Dissertation abstract</option>
            <option value="e-mail">E mail</option>
            <option value="editorial">Editorial</option>
            <option value="electronic-only">Electronic only</option>
            <option value="encyclopedia-article">Encyclopedia article</option>
            <option value="executive-order">Executive order</option>
            <option value="federal-bill">Federal bill</option>
            <option value="federal-report">Federal report</option>
            <option value="federal-rule">Federal rule</option>
            <option value="federal-statute">Federal statute</option>
            <option value="federal-testimony">Federal testimony</option>
            <option value="film-movie">Film movie</option>
            <option value="government-publication">Government publication</option>
            <option value="interview">Interview</option>
            <option value="journal-article">Journal article</option>
            <option value="lecture-speech">Lecture speech</option>
            <option value="legal">Legal</option>
            <option value="letter">Letter</option>
            <option value="live-performance">Live performance</option>
            <option value="magazine-article">Magazine article</option>
            <option value="mailing-list">Mailing list</option>
            <option value="manuscript">Manuscript</option>
            <option value="map-chart">Map chart</option>
            <option value="musical-recording">Musical recording</option>
            <option value="newsgroup">Newsgroup</option>
            <option value="newsletter">Newsletter</option>
            <option value="newspaper-article">Newspaper article</option>
            <option value="other">Other</option>
            <option value="pamphlet">Pamphlet</option>
            <option value="patent">Patent</option>
            <option value="periodicals">Periodicals</option>
            <option value="photograph">Photograph</option>
            <option value="press-release">Press release</option>
            <option value="raw-data">Raw data</option>
            <option value="religious-text">Religious text</option>
            <option value="report">Report</option>
            <option value="reports-working-papers">Reports working papers</option>
            <option value="review">Review</option>
            <option value="scholarly-project">Scholarly project</option>
            <option value="software">Software</option>
            <option value="standards">Standards</option>
            <option value="television-radio">Television radio</option>
            <option value="thesis">Thesis</option>
            <option value="web-site">Web site</option>
    </select>
<span class="required">*</span>
    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.day">Pub Date</label>
    <div class="relative">
    <select id="day" name="day" class="span1">
            <option value="" selected="selected">Day</option>
            <option value="01">01</option>
            <option value="02">02</option>
            <option value="03">03</option>
            <option value="04">04</option>
            <option value="05">05</option>
            <option value="06">06</option>
            <option value="07">07</option>
            <option value="08">08</option>
            <option value="09">09</option>
            <option value="10">10</option>
            <option value="11">11</option>
            <option value="12">12</option>
            <option value="13">13</option>
            <option value="14">14</option>
            <option value="15">15</option>
            <option value="16">16</option>
            <option value="17">17</option>
            <option value="18">18</option>
            <option value="19">19</option>
            <option value="20">20</option>
            <option value="21">21</option>
            <option value="22">22</option>
            <option value="23">23</option>
            <option value="24">24</option>
            <option value="25">25</option>
            <option value="26">26</option>
            <option value="27">27</option>
            <option value="28">28</option>
            <option value="29">29</option>
            <option value="30">30</option>
            <option value="31">31</option>
    </select>
    <select id="month" name="month" class="span1">
            <option value="" selected="selected">Month</option>
            <option value="01">01</option>
            <option value="02">02</option>
            <option value="03">03</option>
            <option value="04">04</option>
            <option value="05">05</option>
            <option value="06">06</option>
            <option value="07">07</option>
            <option value="08">08</option>
            <option value="09">09</option>
            <option value="10">10</option>
            <option value="11">11</option>
            <option value="12">12</option>
    </select>
    <select id="year" name="year" class="span2">
            <option value="" selected="selected">Year</option>
            <option value="2013">2013</option>
            <option value="2012">2012</option>
            <option value="2011">2011</option>
            <option value="2010">2010</option>
            <option value="2009">2009</option>
            <option value="2008">2008</option>
            <option value="2007">2007</option>
            <option value="2006">2006</option>
            <option value="2005">2005</option>
            <option value="2004">2004</option>
            <option value="2003">2003</option>
            <option value="2002">2002</option>
            <option value="2001">2001</option>
            <option value="2000">2000</option>
            <option value="1999">1999</option>
            <option value="1998">1998</option>
            <option value="1997">1997</option>
            <option value="1996">1996</option>
            <option value="1995">1995</option>
            <option value="1994">1994</option>
            <option value="1993">1993</option>
            <option value="1992">1992</option>
            <option value="1991">1991</option>
            <option value="1990">1990</option>
            <option value="1989">1989</option>
            <option value="1988">1988</option>
            <option value="1987">1987</option>
            <option value="1986">1986</option>
            <option value="1985">1985</option>
            <option value="1984">1984</option>
            <option value="1983">1983</option>
            <option value="1982">1982</option>
            <option value="1981">1981</option>
            <option value="1980">1980</option>
            <option value="1979">1979</option>
            <option value="1978">1978</option>
            <option value="1977">1977</option>
            <option value="1976">1976</option>
            <option value="1975">1975</option>
            <option value="1974">1974</option>
            <option value="1973">1973</option>
            <option value="1972">1972</option>
            <option value="1971">1971</option>
            <option value="1970">1970</option>
            <option value="1969">1969</option>
            <option value="1968">1968</option>
            <option value="1967">1967</option>
            <option value="1966">1966</option>
            <option value="1965">1965</option>
            <option value="1964">1964</option>
            <option value="1963">1963</option>
            <option value="1962">1962</option>
            <option value="1961">1961</option>
            <option value="1960">1960</option>
            <option value="1959">1959</option>
            <option value="1958">1958</option>
            <option value="1957">1957</option>
            <option value="1956">1956</option>
            <option value="1955">1955</option>
            <option value="1954">1954</option>
            <option value="1953">1953</option>
            <option value="1952">1952</option>
            <option value="1951">1951</option>
            <option value="1950">1950</option>
            <option value="1949">1949</option>
            <option value="1948">1948</option>
            <option value="1947">1947</option>
            <option value="1946">1946</option>
            <option value="1945">1945</option>
            <option value="1944">1944</option>
            <option value="1943">1943</option>
            <option value="1942">1942</option>
            <option value="1941">1941</option>
            <option value="1940">1940</option>
            <option value="1939">1939</option>
            <option value="1938">1938</option>
            <option value="1937">1937</option>
            <option value="1936">1936</option>
            <option value="1935">1935</option>
            <option value="1934">1934</option>
            <option value="1933">1933</option>
            <option value="1932">1932</option>
            <option value="1931">1931</option>
            <option value="1930">1930</option>
            <option value="1929">1929</option>
            <option value="1928">1928</option>
            <option value="1927">1927</option>
            <option value="1926">1926</option>
            <option value="1925">1925</option>
            <option value="1924">1924</option>
            <option value="1923">1923</option>
            <option value="1922">1922</option>
            <option value="1921">1921</option>
            <option value="1920">1920</option>
            <option value="1919">1919</option>
            <option value="1918">1918</option>
            <option value="1917">1917</option>
            <option value="1916">1916</option>
            <option value="1915">1915</option>
            <option value="1914">1914</option>
            <option value="1913">1913</option>
    </select>
    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.currentWorkExternalIds[0].id">ID</label>
    <div class="relative">    <input type="text" id="currentWorkExternalIds[0].id" name="currentWorkExternalIds[0].id" value="" placeholder="Enter an external ID" class="input-xlarge">


    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.currentWorkExternalIds[0].type">ID type</label>
    <div class="relative">    <select id="currentWorkExternalIds[0].type" name="currentWorkExternalIds[0].type" class="input-xlarge">
            <option value="" selected="selected">What type of external ID?</option>
            <option value="arxiv">ArXiv</option>
            <option value="asin">Amazon Standard Identification Number</option>
            <option value="asin-tld">ASIN top-level domain</option>
            <option value="bibcode">Bibcode</option>
            <option value="doi">Digital object identifier</option>
            <option value="eid">Scopus Identifier</option>
            <option value="isbn">International Standard Book Number</option>
            <option value="issn">International Standard Serial Number</option>
            <option value="jfm">Jahrbuch über die Fortschritte der Mathematik</option>
            <option value="jstor">JSTOR abstract</option>
            <option value="lccn">Library of Congress Control Number</option>
            <option value="mr">Mathematical Reviews</option>
            <option value="oclc">Online Computer Library Center</option>
            <option value="ol">Open Library</option>
            <option value="osti">Office of Scientific and Technical Information</option>
            <option value="other-id">Other identifier type</option>
            <option value="pmc">PubMed Central article number</option>
            <option value="pmid">PubMed Unique Identifier</option>
            <option value="rfc">Request for Comments</option>
            <option value="ssrn">Social Science Research Network</option>
            <option value="zbl">Zentralblatt MATH</option>
    </select>

    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.url">URL</label>
    <div class="relative">    <input type="text" id="url" name="url" value="" placeholder="Enter a link to the work" class="input-xlarge">

</div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.currentWorkContributors[0].role">Role</label>
    <div class="relative">    <select id="currentWorkContributors[0].role" name="currentWorkContributors[0].role" class="input-xlarge">
            <option value="" selected="selected">What was your role?</option>
            <option value="assignee">Assignee</option>
            <option value="author">Author</option>
            <option value="chair_or_translator">Chair or translator</option>
            <option value="co_inventor">Co inventor</option>
            <option value="co_investigator">Co investigator</option>
            <option value="editor">Editor</option>
            <option value="graduate_student">Graduate student</option>
            <option value="other_inventor">Other inventor</option>
            <option value="postdoctoral_researcher">Postdoctoral researcher</option>
            <option value="principal_investigator">Principal investigator</option>
            <option value="support_staff">Support staff</option>
    </select>

    </div>
</div>
<div class="control-group">
    <label class="relative">Credited</label>
    <div class="relative">
    <label for="currentWorkContributors[0].sequence0" class="radio radio-inline">First
            <input type="radio" id="currentWorkContributors[0].sequence0" name="currentWorkContributors[0].sequence" value="first" class="inline">

    </label>
    <label for="currentWorkContributors[0].sequence1" class="radio radio-inline">Additional
            <input type="radio" id="currentWorkContributors[0].sequence1" name="currentWorkContributors[0].sequence" value="additional" class="inline">

    </label>
    </div>
</div>
<div class="control-group"><label class="relative" for="manualWork.description">
    Description</label><div class="relative">     <textarea id="description" name="description" placeholder="A brief description" class="input-xlarge"></textarea>
</div>
</div>

		<button class="btn btn-primary">${springMacroRequestContext.getMessage("manage.deleteExternalIdentifier.delete")}</button> 
		<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteExternalIdentifier.cancel")}</a>
	<div>
</script>

	
</@protected>
