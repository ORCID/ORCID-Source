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
    	<div class="btn btn-danger" ng-click="deleteByPutCode()">
    		${springMacroRequestContext.getMessage("manage.deleteWork.delete")}
    	</div>
    	<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteWork.cancel")}</a>
    <div>
</script>
	
<script type="text/ng-template" id="add-work-modal">
	<div class="edit-work colorbox-content">
		<div class="row">
			<div class="span10">
				<h1 class="lightbox-title pull-left"><@orcid.msg 'manual_work_form_contents.add_work'/></h1>
				<div class="pull-right">
					<div class="control-group span2">
		 				<label class="relative"><@orcid.msg 'privacyToggle.help.who_can_see'/></label>
		 				<@orcid.privacyToggle "editWork.visibility.visibility" "setAddWorkPrivacy('PUBLIC', $event)" 
		                    	  "setAddWorkPrivacy('LIMITED', $event)" "setAddWorkPrivacy('PRIVATE', $event)" />					
		 			</div>				
					<div class="pull-right span1">
						<a class="btn close-button" ng-click="closeModal()">X</a>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="span5">	
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
					<label><@orcid.msg 'manual_work_form_contents.labeltitle'/></label>
				    <div class="relative">
						<input name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.workTitle.title.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_title'/>" ng-change="serverValidate('works/work/workTitle/titleValidate.json')" ng-model-onblur/>						
						<span class="required" ng-class="isValidClass(editWork.workTitle.title)">*</span>						
						<span class="orcid-error" ng-show="editWork.workTitle.title.errors.length > 0">
							<div ng-repeat='error in editWork.workTitle.title.errors' ng-bind-html-unsafe="error"></div>
						</span>
						<div class="translated-title-section">
							<span ng-hide="editTranslatedTitle"><@orcid.msg 'manual_work_form_contents.labelshowtranslatedtitle'/>&nbsp;<a ng-click="toggleTranslatedTitleModal()" class="icon-plus-sign blue"></a></span>
							<span ng-show="editTranslatedTitle"><@orcid.msg 'manual_work_form_contents.labelhidetranslatedtitle'/>&nbsp;<a ng-click="toggleTranslatedTitleModal()" class="icon-minus-sign blue"></a></span>
						</div>
					</div>
				</div>

				<div id="translatedTitle" style="display:none;">
					<span class="orcid-error" ng-show="editWork.workTitle.translatedTitle.errors.length > 0">
						<div ng-repeat='error in editWork.workTitle.translatedTitle.errors' ng-bind-html-unsafe="error"></div>
					</span>
					<div class="control-group">
						<label><@orcid.msg 'manual_work_form_contents.labeltranslatedtitle'/></label>
						<div class="relative">
							<input name="translatedTitle" type="text" class="input-xlarge" ng-model="editWork.workTitle.translatedTitle.content" placeholder="<@orcid.msg 'manual_work_form_contents.add_translated_title'/>" ng-change="serverValidate('works/work/workTitle/translatedTitleValidate.json')" ng-model-onblur/>														
						</div>						
					</div>

					<div class="control-group">
						<label class="relative"><@orcid.msg 'manual_work_form_contents.labeltranslatedtitlelanguage'/></label>						
						<select id="language" name="language" ng-model="editWork.workTitle.translatedTitle.languageCode">			
							<#list languages?keys as key>
								<option value="${languages[key]}">${key}</option>
							</#list>
						</select>						
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
						<input name="journalTitle" type="text" class="input-xlarge"  ng-model="editWork.journalTitle.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_journalTitle'/>"   ng-change="serverValidate('works/work/journalTitleValidate.json')"    ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.journalTitle.errors.length > 0">
							<div ng-repeat='error in editWork.journalTitle.errors' ng-bind-html-unsafe="error"></div>
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

				<div class="span5">&nbsp;</div>

				<div class="control-group">
					<span><strong><@orcid.msg 'manual_work_form_contents.titlecitation'/></strong></span>
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
					<label><@orcid.msg 'manual_work_form_contents.labelcitation'/></label>
				    <div class="relative">
						<textarea name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.citation.citation.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_citation'/>" ng-change="serverValidate('works/work/citationValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.citation.citation.errors.length > 0">
							<div ng-repeat='error in editWork.citation.citation.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>

				<div class="span5">&nbsp;</div>
			
				<div class="control-group">
					<label><@orcid.msg 'manual_work_form_contents.labeldescription'/></label>
				    <div class="relative">
						<textarea name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.shortDescription.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_description'/>" ng-change="serverValidate('works/work/descriptionValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.shortDescription.errors.length > 0">
							<div ng-repeat='error in editWork.shortDescription.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
			</div>

			<div class="span5">
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

		    	<div class="span5">&nbsp;</div>

				<div class="control-group">
					<span><strong><@orcid.msg 'manual_work_form_contents.titlecitationexternalidentifier'/></strong></span>
				</div>
		    	
		   		<div class="control-group" ng-repeat="workExternalIdentifier in editWork.workExternalIdentifiers">
					<label><@orcid.msg 'manual_work_form_contents.labelID'/></label>
				    <div class="relative">
						<input name="currentWorkExternalIds" type="text" class="input-xlarge"  ng-model="workExternalIdentifier.workExternalIdentifierId.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_ID'/>"  ng-change="serverValidate('works/work/workExternalIdentifiersValidate.json')" ng-model-onblur/>
							<span class="orcid-error" ng-show="workExternalIdentifier.workExternalIdentifierId.errors.length > 0">
								<div ng-repeat='error in workExternalIdentifier.workExternalIdentifierId.errors' ng-bind-html-unsafe="error"></div>
							</span>
					</div>
				</div>

				<div class="control-group">
					<label class="relative"><@orcid.msg 'manual_work_form_contents.labelID'/></label>
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
									
				<div class="span5">&nbsp;</div>
				
				<div class="control-group">
		    		<label class="relative"><@orcid.msg 'manual_work_form_contents.labelURL'/></label>
		    		<div class="relative">
						<input name="url" type="text" class="input-xlarge"  ng-model="editWork.url.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL'/>" ng-change="serverValidate('works/work/urlValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.url.errors.length > 0">
							<div ng-repeat='error in editWork.url.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>

				<div class="control-group">
					<label class="relative"><@orcid.msg 'manual_work_form_contents.labelformlanguage'/></label>
					<select id="language" name="language" ng-model="editWork.languageCode.value">
						<option value="${currentLocaleKey}">${currentLocaleValue}</option>
						<#list languages?keys as key>
							<option value="${languages[key]}">${key}</option>
						</#list>
					</select>
				</div>

				<div class="control-group">
                    <label for="country"><@orcid.msg 'manual_work_form_contents.labelcountry'/></label>
                    <div class="relative">
                    	<select id="isoCountryCode" name="isoCountryCode" ng-model="editWork.country.value">
                    		<option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
	                    	<#list isoCountries?keys as key>
								<option value="${key}">${isoCountries[key]}</option>								
							</#list>
						</select>                        
                    </div>
                </div>
				
				<div class="span5">&nbsp;</div>

				<div class="small-row"> 
					<div>
						<button class="btn btn-primary" ng-click="addWork()" ng-disabled="addingWork" ng-class="{disabled:addingWork}"><@orcid.msg 'manual_work_form_contents.btnaddtolist'/></button> 
						<a href="" ng-click="closeModal()"><@orcid.msg 'manage.deleteExternalIdentifier.cancel'/></a>
						&nbsp;
						<span ng-show="addingWork">
							<i class="icon-spinner icon-2x icon-spin  green"></i>
						</span>
						<span ng-show="editWork.errors.length > 0" class="alert" style>Please fix above errors</span>					
					</div>
				</div>
			</div>
		</div>				
	<div>
</script>
	
	 
<ul ng-hide="!works.length" class="workspace-publications workspace-body-list bottom-margin-medium" ng-cloak>        
    <li class="bottom-margin-small" ng-repeat="work in works | orderBy:['-publicationDate.year', '-publicationDate.month', '-publicationDate.day']">            	
    	<#if RequestParameters['worksInfo']??>
    	   <div class="pull-right show-work-info" style="right: 160px; top: 20px; width: 15px;"><a class="icon-plus-sign grey"></a>
    	       <div class="popover bottom popover-work-container"><div class="arrow" style="left: 520px;"></div>
    	       
			    	<div class="row bottomBuffer">
			    	</div>       
			 		<div class="row bottomBuffer" ng-show="work.workTitle.title.value" ng-cloak>
						<div class="span8">
							<strong><@orcid.msg 'manual_work_form_contents.labeltitle'/></strong>
							<div ng-bind="work.workTitle.title.value"></div>
						</div>
					</div>					
					<div class="row bottomBuffer" ng-show="work.workTitle.translatedTitle.content" ng-cloak>
						<div class="span8">
							<strong><@orcid.msg 'manual_work_form_contents.labeltranslatedtitle'/></strong>
							<div ng-bind="renderTranslatedTitleInfo($index)"></div>
						</div>
					</div>					
					<div class="row bottomBuffer" ng-show="work.workTitle.subtitle.value" ng-cloak>
						<div class="span8">
							<strong>
								<@orcid.msg 'manual_work_form_contents.labelsubtitle'/>
							</strong>
							<div ng-bind="work.workTitle.subtitle.value"></div>
						</div>
					</div>
					<div class="row bottomBuffer" ng-show="work.journalTitle.value" ng-cloak>
						<div class="span8">
							<strong>
								<@orcid.msg 'manual_work_form_contents.journalTitle'/>
							</strong>
							<div ng-bind="work.journalTitle.value"></div>
						</div>
					</div>
					<div class="row bottomBuffer" ng-show="work.workType.value" ng-cloak>
						<div class="span8">
							<strong>
								<@orcid.msg 'manual_work_form_contents.labelworktype'/>
							</strong>
							<div ng-bind="work.workType.value"></div>
						</div>
					</div>
					<div class="row bottomBuffer" ng-show="work.citation.citation.value" ng-cloak>
						<div class="span8">
							<strong>
								<@orcid.msg 'manual_work_form_contents.labelcitation'/>
							</strong>
							<span ng-show="showBibtex && work.citation.citationType.value == 'bibtex'"><a ng-click="bibtexShowToggle()">Show in Bibtex</a></span>
							<span ng-show="showBibtex == false && work.citation.citationType.value == 'bibtex'"><a ng-click="bibtexShowToggle()">Show in HTML</a></span>
							<div ng-hide="showBibtex && work.citation.citationType.value == 'bibtex'" ng-bind="work.citation.citation.value"></div>
							<div class="row" ng-show="showBibtex" ng-repeat='bibJSON in bibtexCitations[work.putCode.value]'>
								<div class="row"> 
									<div class="span2" style="margin-left: 0px;">{{bibJSON.entryType}}</div>
									<div class="span6">{{bibJSON.citationKey}}</div>
								</div>
								<div ng-repeat="(entKey,entVal) in bibJSON.entryTags" class="row">
									<div class="span2" style="margin-left: 0px;">{{entKey}}</div>
									<div class="span6">{{entVal}}</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row bottomBuffer" ng-show="work.citation.citationType.value" ng-cloak>
						<div class="span8">
							<strong>
								<@orcid.msg 'manual_work_form_contents.labelcitationtype'/>
							</strong>
							<div ng-bind="work.citation.citationType.value"></div>
						</div>
					</div>
					<div class="row bottomBuffer" ng-show="work.publicationDate.year" ng-cloak>
						<div class="span8">
							<strong>
								<@orcid.msg 'manual_work_form_contents.labelPubDate'/>
							</strong>
							<div><span ng-show="work.publicationDate.day && work.publicationDate.month">{{work.publicationDate.day}}-</span><span ng-show="work.publicationDate.month">{{work.publicationDate.month}}-</span><span ng-show="work.publicationDate.year">{{work.publicationDate.year}}</span></div>
						</div>
					</div>
					<div class="row bottomBuffer" ng-show="work.shortDescription.value" ng-cloak>
						<div class="span8">
							<strong>
								<@orcid.msg 'manual_work_form_contents.labeldescription'/>
							</strong>
							<div ng-bind="work.shortDescription.value" style="white-space: pre-wrap;"></div>
						</div>
					</div>
					<div class="row bottomBuffer" ng-show="work.workExternalIdentifiers.length > 0" ng-cloak>
						<div class="span8">
							<strong>
								<@orcid.msg 'manual_work_form_contents.labelID'/>
							</strong>
							<div>
								<span ng-repeat='ie in work.workExternalIdentifiers'>
			            	    	<span ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
			                    </span>
							</div>
						</div>
					</div>
					<div class="row bottomBuffer" ng-show="work.url.value" ng-cloak>
						<div class="span8">
							<strong>
								<@orcid.msg 'manual_work_form_contents.labelURL'/>
							</strong>
							<div><a href="{{work.url.value | urlWithHttp}}" target="_blank">{{work.url.value}}</a></div>
						</div>
					</div>
					<div class="row bottomBuffer" ng-show="work.contributors.length > 0" ng-cloak>
						<div class="span8">
							<strong>
								Contributor
							</strong>
							<div ng-repeat="contributor in work.contributors">
								{{contributor.creditName.value}} <span ng-bind='contributor | contributorFilter'></span>
							</div>
						</div>
					</div>	
					<div class="row bottomBuffer" ng-show="work.languageCode.value" ng-cloak>
						<div class="span8">
							<strong><@orcid.msg 'manual_work_form_contents.labellanguage'/></strong>
							<div ng-bind="renderLanguageName($index)"></div>
						</div>
					</div>
    	       		<div class="row bottomBuffer" ng-show="work.country.value" ng-cloak>
						<div class="span8">
							<strong><@orcid.msg 'manual_work_form_contents.labelcountry'/></strong>
							<div ng-bind="renderCountryName($index)"></div>
						</div>
					</div>
    	       
    	       </div>
    	   </div>
           <div class="pull-right" style="right: 140px; top: 20px; width: 15px;"><a href ng-click="deleteWork(work.putCode.value)" class="icon-trash orcid-icon-trash grey"></a></div>
    	<#else>
           <div class="pull-right" style="right: 145px; top: 20px; width: 15px;"><a href ng-click="deleteWork(work.putCode.value)" class="icon-trash orcid-icon-trash grey"></a></div>
		</#if>
		<div style="width: 530px;">
        <h3 class="work-title">
        	<strong ng-bind-html="work.workTitle.title.value"></strong><span class="work-subtitle" ng-show="work.workTitle.subtitle.value" ng-bind-html="':&nbsp;'.concat(work.workTitle.subtitle.value)"></span>
        	<span ng-show="work.publicationDate.month">{{work.publicationDate.month}}-</span><span ng-show="work.publicationDate.year">{{work.publicationDate.year}}</span>
        </h3>
        </div>
        <div class="pull-right" style="width: 130px;">
		<@orcid.privacyToggle angularModel="work.visibility.visibility" 
				publicClick="setPrivacy(work.putCode.value, 'PUBLIC', $event)" 
                limitedClick="setPrivacy(work.putCode.value, 'LIMITED', $event)" 
                privateClick="setPrivacy(work.putCode.value, 'PRIVATE', $event)"
                popoverStyle="left: -255px;"
                arrowStyle="left: 261px;" />
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
    