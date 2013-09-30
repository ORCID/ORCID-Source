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

<script type="text/ng-template" id="delete-work-modal">
	<div style="padding: 20px;">
		<h3 style="margin-bottom: 0px;">${springMacroRequestContext.getMessage("manage.deleteWork.pleaseConfirm")}</h3>
		{{fixedTitle}}<br />
		<br />
    	<div class="btn btn-danger" ng-click="deleteByIndex()">
    		${springMacroRequestContext.getMessage("manage.deleteWork.delete")}
    	</div>
    	<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteWork.cancel")}</a>
    <div>
</script>

<script type="text/ng-template" id="detail-work-modal">
	<div class="edit-work colorbox-content">
		<div class="row">
			<div class="span12">
				<h1 class="lightbox-title pull-left">Work Details</h1>
				<a class="btn pull-right close-button" ng-click="closeModal()">X</a>
			</div>
		</div>
		<div class="row" ng-show="detailWork.workTitle.title.value" ng-cloak>
			<div class="span2">
				<div class="label">
					<@orcid.msg 'manual_work_form_contents.labeltitle'/>
				</div>
			</div>
			<div class="span10">
				<div ng-bind="detailWork.workTitle.title.value"></div>
			</div>
		</div>
		<div class="row" ng-show="detailWork.workTitle.subtitle.value" ng-cloak>
			<div class="span2">
				<div class="label">
					<@orcid.msg 'manual_work_form_contents.labelsubtitle'/>
				</div>
			</div>
			<div class="span10">
				<div ng-bind="detailWork.workTitle.subtitle.value"></div>
			</div>
		</div>
		<div class="row" ng-show="detailWork.workType.value" ng-cloak>
			<div class="span2">
				<div class="label">
					<@orcid.msg 'manual_work_form_contents.labelworktype'/>
				</div>
			</div>
			<div class="span10">
				<div ng-bind="detailWork.workType.value"></div>
			</div>
		</div>
		<div class="row" ng-show="detailWork.citation.citation.value" ng-cloak>
			<div class="span2">
				<div class="label">
					<@orcid.msg 'manual_work_form_contents.labelcitation'/>
				</div>
				<div ng-show="showBibtex && detailWork.citation.citationType.value == 'bibtex'"><a ng-click="bibtexShowToggle()">show raw</a></div>
				<div ng-show="showBibtex == false && detailWork.citation.citationType.value == 'bibtex'"><a ng-click="bibtexShowToggle()">hide raw</a></div>
			</div>
			<div class="span10" ng-hide="showBibtex" ng-cloak>
				<div ng-bind="detailWork.citation.citation.value"></div>
			</div>
			<div class="span10" ng-show="showBibtex" ng-cloak>
				<div ng-repeat='bibJSON in detailWork.bibtexCitation'>
					<div class="row"> 
						<div class="span10">
							{{bibJSON.entryType}} : {{bibJSON.citationKey}}
						</div>
					</div>
					<div ng-repeat="(entKey,entVal) in bibJSON.entryTags" class="row">
						<div class="span1"></div>
						<div class="span3">{{entKey}}</div>
						<div class="span6">{{entVal}}</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row" ng-show="detailWork.citation.citationType.value" ng-cloak>
			<div class="span2">
				<div class="label">
					<@orcid.msg 'manual_work_form_contents.labelcitationtype'/>
				</div>
			</div>
			<div class="span10">
				<div ng-bind="detailWork.citation.citationType.value"></div>
			</div>
		</div>
		<div class="row" ng-show="detailWork.publicationDate.year" ng-cloak>
			<div class="span2">
				<div class="label">
					<@orcid.msg 'manual_work_form_contents.labelPubDate'/>
				</div>
			</div>
			<div class="span10">
				<div><span ng-show="detailWork.publicationDate.day && detailWork.publicationDate.month">{{detailWork.publicationDate.day}}-</span><span ng-show="detailWork.publicationDate.month">{{detailWork.publicationDate.month}}-</span><span ng-show="detailWork.publicationDate.year">{{detailWork.publicationDate.year}}</span></div>
			</div>
		</div>
		<div class="row" ng-show="detailWork.shortDescription.value" ng-cloak>
			<div class="span2">
				<div class="label">
					<@orcid.msg 'manual_work_form_contents.labeldescription'/>
				</div>
			</div>
			<div class="span10">
				<div ng-bind="detailWork.shortDescription.value" style="white-space: pre-wrap;"></div>
			</div>
		</div>
		<div class="row" ng-show="detailWork.workExternalIdentifiers.length > 0" ng-cloak>
			<div class="span2">
				<div class="label">
					<@orcid.msg 'manual_work_form_contents.labelID'/>
				</div>
			</div>
			<div class="span10">
				<div>
					<span ng-repeat='ie in detailWork.workExternalIdentifiers'>
            	    	<span ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
                    </span>
				</div>
			</div>
		</div>
		<div class="row" ng-show="detailWork.url.value" ng-cloak>
			<div class="span2">
				<div class="label">
					<@orcid.msg 'manual_work_form_contents.labelURL'/>
				</div>
			</div>
			<div class="span10">
				<div ng-bind="detailWork.url.value"></div>
			</div>
		</div>
		<div class="row" ng-show="detailWork.contributors.length > 0" ng-cloak>
			<div class="span2">
				<div class="label">
					Contributor
				</div>
			</div>
			<div class="span10">
				<div ng-repeat="contributor in detailWork.contributors">
					<div class="row" ng-show="contributor.contributorRole.value">
						<div class="span2">
							Role:
						</div>
						<div class="span8">
							{{contributor.contributorRole.value}}
						</div>
					</div>
					<div class="row" ng-show="contributor.contributorSequence.value">
						<div class="span2">
							Credited:
						</div>
						<div class="span8">
							{{contributor.contributorSequence.value}}
						</div>
					</div>
					<div class="row" ng-show="contributor.email.value">
						<div class="span2">
							Email:
						</div>
						<div class="span8">
							{{contributor.email.value}}
						</div>
					</div>
					<div class="row" ng-show="contributor.orcid.value">
						<div class="span2">
							ORCID:
						</div>
						<div class="span8">
							{{contributor.orcid.value}}
						</div>
					</div>
					<div class="row" ng-show="contributor.email.value">
						<div class="span2">
							Credited:
						</div>
						<div class="span8">
							{{contributor.email.value}}
						</div>
					</div>
					<div class="row" ng-show="contributor.creditName.value">
						<div class="span2">
							Credit Name:
						</div>
						<div class="span8">
							{{contributor.creditName.value}}
						</div>
					</div>
				</div>
				    
				
			</div>
		</div>		
	</div>
</script>
	
<script type="text/ng-template" id="add-work-modal">
	<div class="edit-work colorbox-content">
		<div class="row">
			<div class="span12">
				<h1 class="lightbox-title pull-left"><@orcid.msg 'manual_work_form_contents.add_work'/></h1>
				<a class="btn pull-right close-button" ng-click="closeModal()">X</a>
			</div>
		</div>
		<div class="row">
			<div class="span6">	
				<div class="control-group">
					<label><@orcid.msg 'manual_work_form_contents.labeltitle'/></label>
				    <div class="relative">
						<input name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.workTitle.title.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_title'/>" ng-change="serverValidate('works/work/workTitle/titleValidate.json')" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editWork.workTitle.title)">*</span>
						<span class="orcid-error" ng-show="editWork.workTitle.title.errors.length > 0">
							<div ng-repeat='error in editWork.workTitle.title.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label><@orcid.msg 'manual_work_form_contents.labelsubtitle'/></label>
				    <div class="relative">
						<input name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.workTitle.subtitle.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_subtitle'/>" ng-change="serverValidate('works/work/workTitle/subtitleValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.workTitle.subtitle.errors.length > 0">
							<div ng-repeat='error in editWork.workTitle.subtitle.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>

				<div class="control-group">
					<label><@orcid.msg 'manual_work_form_contents.journalTitle'/></label>
				    <div class="relative">
						<input name="journalTitle" type="text" class="input-xlarge"  ng-model="editWork.journalTitle.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_journalTitle'/>" ng-model-onblur/>						
					</div>
				</div>

				<div class="control-group">
		    		<label class="relative"><@orcid.msg 'manual_work_form_contents.labelworktype'/></label>
		    		<div class="relative">
			    		<select id="workType" name="workType" class="input-xlarge" ng-model="editWork.workType.value" ng-change="serverValidate('works/work/workTypeValidate.json')">
			    			<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.WorkType.empty' /></option>
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
				
		 		<div class="control-group">
		 			<label class="relative"><@orcid.msg 'privacyToggle.help.who_can_see'/></label>
		 				<@orcid.privacyToggle "editWork.visibility.visibility" "setAddWorkPrivacy('PUBLIC', $event)" 
		                    	  "setAddWorkPrivacy('LIMITED', $event)" "setAddWorkPrivacy('PRIVATE', $event)" />
		 		</div>
		 		
				<div class="control-group">
					<label><@orcid.msg 'manual_work_form_contents.labelcitation'/></label>
				    <div class="relative">
						<textarea name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.citation.citation.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_citation'/>" ng-change="serverValidate('works/work/citationValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.citation.citation.errors.length > 0">
							<div ng-repeat='error in editWork.citation.citation.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				
				<div class="control-group">
		    		<label class="relative"><@orcid.msg 'manual_work_form_contents.labelcitationtype'/></label>
		    		<div class="relative">
			    		<select id="citationType" name="citationType" class="input-xlarge" ng-model="editWork.citation.citationType.value" ng-change="serverValidate('works/work/citationValidate.json')">
							<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.CitationType.empty' /></option>
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
		    		<label class="relative" for="manualWork.day"><@orcid.msg 'manual_work_form_contents.labelPubDate'/></label>
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
		
				    <select id="year" name="month" ng-model="editWork.publicationDate.year" class="span2">
						<#list years?keys as key>
							<option value="${key}">${years[key]}</option>
						</#list>
		    		</select>
		    		</div>
		    	</div>
		 		
			</div>
			
			<div class="span6">
		    	
		    	<div class="control-group">
					<label><@orcid.msg 'manual_work_form_contents.labeldescription'/></label>
				    <div class="relative">
						<textarea name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.shortDescription.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_description'/>" ng-change="serverValidate('works/work/descriptionValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.shortDescription.errors.length > 0">
							<div ng-repeat='error in editWork.shortDescription.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
		    	
		   		<div class="control-group" ng-repeat="workExternalIdentifier in editWork.workExternalIdentifiers">
					<label><@orcid.msg 'manual_work_form_contents.labelID'/></label>
				    <div class="relative">
						<input name="currentWorkExternalIds" type="text" class="input-xlarge"  ng-model="workExternalIdentifier.workExternalIdentifierId.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_ID'/>"  ng-change="serverValidate('works/work/workExternalIdentifiersValidate.json')" ng-model-onblur/>
							<span class="orcid-error" ng-show="workExternalIdentifier.workExternalIdentifierId.errors.length > 0">
								<div ng-repeat='error in workExternalIdentifier.workExternalIdentifierId.errors' ng-bind-html-unsafe="error"></div>
							</span>
					</div>
					<label class="relative">ID type</label>
					<div class="relative">
			    		<select id="idType" name="idType" class="input-xlarge" ng-model="workExternalIdentifier.workExternalIdentifierType.value" ng-change="serverValidate('works/work/workExternalIdentifiersValidate.json')">
							<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.WorkExternalIdentifierType.empty' /></option>
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
		    		<label class="relative"><@orcid.msg 'manual_work_form_contents.labelURL'/></label>
		    		<div class="relative">
						<input name="url" type="text" class="input-xlarge"  ng-model="editWork.url.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL'/>" ng-change="serverValidate('works/work/urlValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.url.errors.length > 0">
							<div ng-repeat='error in editWork.url.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
		
				<div class="control-group" ng-repeat="contributor in editWork.contributors">
				    <label class="relative"><@orcid.msg 'manual_work_form_contents.labelRole'/></label>
				    <div class="relative">    
						<select id="role" name="role" ng-model="contributor.contributorRole.value">
							<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.ContributorRole.empty' /></option>
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
				    <label class="relative"><@orcid.msg 'manual_work_form_contents.labelcredited'/></label>
				    <div class="relative">    
						<select id="sequence" name="sequence" ng-model="contributor.contributorSequence.value">
							<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.SequenceType.empty'/></option>
							<#list sequences?keys as key>
								<option value="${key}">${sequences[key]}</option>
							</#list>
			    		</select>
						<span class="orcid-error" ng-show="contributor.contributorSequence.errors.length > 0">
								<div ng-repeat='error in contributor.contributorSequence.errors' ng-bind-html-unsafe="error"></div>
						</span>
				    </div>
				</div>
				
			</div>
		</div>
		<div class="row">	
			<div class="span12">
			   &nbsp;
			</div>
		</div>
		<div class="row">
			<div class="span6">
			   &nbsp;
			</div>
			<div class="span2">
				<button class="btn btn-primary" ng-click="addWork()" ng-disabled="addingWork" ng-class="{disabled:addingWork}"><@orcid.msg 'manual_work_form_contents.btnaddtolist'/></button> 
				<a href="" ng-click="closeModal()"><@orcid.msg 'manage.deleteExternalIdentifier.cancel'/></a>
			</div>
			<div class="span4">
				<span ng-show="addingWork">
					<i class="icon-spinner icon-2x icon-spin  green"></i>
				</span>
				<span ng-show="editWork.errors.length > 0" class="alert" style>Please fix above errors</span>
			</div>
		</div>
		<div class="row">
			<div class="span12">
			   &nbsp;
			</div>
		</div>
	<div>
</script>
	
	 
<ul ng-hide="!works.length" class="workspace-publications workspace-body-list bottom-margin-medium" ng-cloak>        
    <li class="bottom-margin-small" ng-repeat="work in works | orderBy:['-publicationDate.year', '-publicationDate.month', '-publicationDate.day']">            	
    	<#if RequestParameters['worksInfo']??>
    	   <div class="pull-right" style="right: 160px; top: 20px; width: 15px;"><a href ng-click="showDetailModal($index)" class="icon-resize-full grey"></a></div>
           <div class="pull-right" style="right: 140px; top: 20px; width: 15px;"><a href ng-click="deleteWork($index)" class="icon-trash orcid-icon-trash grey"></a></div>
    	<#else>
           <div class="pull-right" style="right: 145px; top: 20px; width: 15px;"><a href ng-click="deleteWork($index)" class="icon-trash orcid-icon-trash grey"></a></div>
		</#if>
		<div style="width: 530px;">
        <h3 class="work-title">
        	<strong ng-bind-html="work.workTitle.title.value"></strong><span class="work-subtitle" ng-show="work.workTitle.subtitle.value" ng-bind-html="':&nbsp;'.concat(work.workTitle.subtitle.value)"></span>
        	<span ng-show="work.publicationDate.month">{{work.publicationDate.month}}-</span><span ng-show="work.publicationDate.year">{{work.publicationDate.year}}</span>
        </h3>
        </div>
        <div class="pull-right" style="width: 130px;">
		<@orcid.privacyToggle "work.visibility.visibility" "setPrivacy($index, 'PUBLIC', $event)" 
                    	  "setPrivacy($index, 'LIMITED', $event)" "setPrivacy($index, 'PRIVATE', $event)" />
		</div>
		<div  style="width: 680px;" class="work-metadata">
            <span ng-repeat='ie in work.workExternalIdentifiers'>
            	<span ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
            </span>
            <span ng-show="work.url.value" style=" display: inline-block;">URL: <a href="{{work.url.value | urlWithHttp}}" target="_blank">{{work.url.value}}</a></span>
        </div>
        
        <div ng-show="work.shortDescription" ng-bind-html="work.shortDescription.value" style="width: 680px; white-space: pre-wrap;"></div>
        <div ng-show="work.citationForDisplay" class="citation {{work.workCitation.workCitationType.toLowerCase()}}" ng-bind-html="work.citationForDisplay" style="width: 680px;"></div>
    </li>           
</ul>
<div ng-show="numOfWorksToAdd==null || (numOfWorksToAdd > works.length)" class="text-center">
    <i class="icon-spinner icon-4x icon-spin  green"></i>
</div>
<div ng-show="numOfWorksToAdd==0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a ng-click="addWorkModal()">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
</div>
    