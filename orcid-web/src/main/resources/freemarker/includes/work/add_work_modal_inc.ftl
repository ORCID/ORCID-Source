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
<script type="text/ng-template" id="add-work-modal">
	<div class="add-work colorbox-content">
		<div class="lightbox-container-ie7">		
		<!-- Title -->
		<div class="row">			
			<div class="col-md-9 col-sm-8 col-xs-9">
				<h1 class="lightbox-title pull-left"><@orcid.msg 'manual_work_form_contents.add_work'/></h1>
			</div>			
			<div class="col-md-3 col-sm-3 pull-left">
				<div class="control-group privacy-control">
			 		<label class="relative">
						<@orcid.msg 'privacyToggle.help.who_can_see'/>
					</label>
		 			<@orcid.privacyToggle "editWork.visibility" "setAddWorkPrivacy('PUBLIC', $event)" 
					"setAddWorkPrivacy('LIMITED', $event)" "setAddWorkPrivacy('PRIVATE', $event)" />					
		 		</div>
			</div>			
		</div>

		<!-- Main content -->		
		<div class="row">
			<!-- Left Column -->			
			<div class="col-md-6 col-sm-6 col-xs-12">	
				<div class="control-group">
		    		<label class="relative"><@orcid.msg 'manual_work_form_contents.labelworkcategory'/></label>
		    		<div class="relative">
			    		<select id="workCategory" name="workCategory" class="input-xlarge" ng-model="editWork.workCategory.value" ng-change="loadWorkTypes()">
			    			<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.WorkCategory.empty' /></option>
							<#list workCategories?keys as key>
								<option value="${key}">${workCategories[key]}</option>
							</#list>
						</select> 
						<span class="required" ng-class="isValidClass(editWork.workCategory)">*</span>
						<span class="orcid-error" ng-show="editWork.workCategory.errors.length > 0">
							<div ng-repeat='error in editWork.workCategory.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>

				<div class="control-group">
		    		<label class="relative"><@orcid.msg 'manual_work_form_contents.labelworktype'/></label>
					<select id="workType" name="workType" class="input-xlarge" ng-model="editWork.workType.value" ng-options="key as value for (key , value) in types">						
					</select>
					<span class="required" ng-class="isValidClass(editWork.workType)">*</span>
					<span class="orcid-error" ng-show="editWork.workType.errors.length > 0">
						<div ng-repeat='error in editWork.workType.errors' ng-bind-html="error"></div>
					</span>
				</div>

				<div class="control-group">
					<label><@orcid.msg 'manual_work_form_contents.labeltitle'/></label>
				    <div class="relative">
						<input name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.workTitle.title.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_title'/>" ng-change="serverValidate('works/work/workTitle/titleValidate.json')" ng-model-onblur/>						
						<span class="required" ng-class="isValidClass(editWork.workTitle.title)">*</span>						
						<span class="orcid-error" ng-show="editWork.workTitle.title.errors.length > 0">
							<div ng-repeat='error in editWork.workTitle.title.errors' ng-bind-html="error"></div>
						</span>
						<div class="add-item-link">
							<span ng-hide="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-plus-sign blue"></i> <@orcid.msg 'manual_work_form_contents.labelshowtranslatedtitle'/></a></span>
							<span ng-show="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-minus-sign blue"></i> <@orcid.msg 'manual_work_form_contents.labelhidetranslatedtitle'/></a></span>
						</div>
					</div>
				</div>

				<div id="translatedTitle">
					<span class="orcid-error" ng-show="editWork.workTitle.translatedTitle.errors.length > 0">
						<div ng-repeat='error in editWork.workTitle.translatedTitle.errors' ng-bind-html="error"></div>
					</span>
					<div class="control-group">
						<label><@orcid.msg 'manual_work_form_contents.labeltranslatedtitle'/></label>
						<div class="relative">
							<input name="translatedTitle" type="text" class="input-xlarge" ng-model="editWork.workTitle.translatedTitle.content" placeholder="<@orcid.msg 'manual_work_form_contents.add_translated_title'/>" ng-change="serverValidate('works/work/workTitle/translatedTitleValidate.json')" ng-model-onblur/>														
						</div>						
					</div>

					<div class="control-group">
						<label class="relative"><@orcid.msg 'manual_work_form_contents.labeltranslatedtitlelanguage'/></label>
						<div class="relative">						
							<select id="language" name="language" ng-model="editWork.workTitle.translatedTitle.languageCode" ng-change="serverValidate('works/work/workTitle/translatedTitleValidate.json')">			
								<#list languages?keys as key>
									<option value="${languages[key]}">${key}</option>
								</#list>
							</select>				
						</div>
					</div>					
				</div>

				<div class="control-group">
					<label><@orcid.msg 'manual_work_form_contents.labelsubtitle'/></label>
				    <div class="relative">
						<input name="familyNames" type="text" class="input-xlarge"  ng-model="editWork.workTitle.subtitle.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_subtitle'/>" ng-change="serverValidate('works/work/workTitle/subtitleValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.workTitle.subtitle.errors.length > 0">
							<div ng-repeat='error in editWork.workTitle.subtitle.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>

				<div class="control-group">
					<label><@orcid.msg 'manual_work_form_contents.journalTitle'/></label>
				    <div class="relative">
						<input name="journalTitle" type="text" class="input-xlarge"  ng-model="editWork.journalTitle.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_journalTitle'/>"   ng-change="serverValidate('works/work/journalTitleValidate.json')"    ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.journalTitle.errors.length > 0">
							<div ng-repeat='error in editWork.journalTitle.errors' ng-bind-html="error"></div>
						</span>						
					</div>
				</div>									 				 														

		 		<div class="control-group">
		    		<label class="relative" for="manualWork.day"><@orcid.msg 'manual_work_form_contents.labelPubDate'/></label>
		    		<div class="relative">					    
						<select id="year" name="month" ng-model="editWork.publicationDate.year" class="col-md-4">
							<#list years?keys as key>
								<option value="${key}">${years[key]}</option>
							</#list>
			    		</select>
					    <select id="month" name="month" ng-model="editWork.publicationDate.month" class="col-md-3">
							<#list months?keys as key>
								<option value="${key}">${months[key]}</option>
							</#list>
			    		</select>
						<select id="day" name="day" ng-model="editWork.publicationDate.day" class="col-md-3">
							<#list days?keys as key>
								<option value="${key}">${days[key]}</option>
							</#list>
			    		</select>								    
		    		</div>
		    	</div>
		    	
				<div class="control-group">
					<span class="citation-title">
						<strong><@orcid.msg 'manual_work_form_contents.titlecitation'/></strong>
					</span>
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
							<div ng-repeat='error in editWork.citation.citationType.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>
				
				<div class="control-group">
					<label><@orcid.msg 'manual_work_form_contents.labelcitation'/></label>
				    <div class="relative">
						<textarea name="citation" type="text" class="input-xlarge"  ng-model="editWork.citation.citation.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_citation'/>" ng-change="serverValidate('works/work/citationValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.citation.citation.errors.length > 0">
							<div ng-repeat='error in editWork.citation.citation.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>
			
				<div class="control-group">
					<label><@orcid.msg 'manual_work_form_contents.labeldescription'/></label>
				    <div class="relative">
						<textarea name="discription" type="text" class="input-xlarge"  ng-model="editWork.shortDescription.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_description'/>" ng-change="serverValidate('works/work/descriptionValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.shortDescription.errors.length > 0">
							<div ng-repeat='error in editWork.shortDescription.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>
				
			</div>


			<!-- Right column -->
			<div class="col-md-6 col-sm-6 col-xs-12">				
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
								<div ng-repeat='error in contributor.contributorRole.errors' ng-bind-html="error"></div>
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
								<div ng-repeat='error in contributor.contributorSequence.errors' ng-bind-html="error"></div>
						</span>
				    </div>
				</div>

				<div class="control-group">
					<span><strong><@orcid.msg 'manual_work_form_contents.titlecitationexternalidentifier'/></strong></span>
				</div>
		    	
				<div ng-repeat="workExternalIdentifier in editWork.workExternalIdentifiers"> 
					<div class="control-group">
						<label class="relative"><@orcid.msg 'manual_work_form_contents.labelIDtype'/></label>
						<div class="relative">
		    				<select id="idType" name="idType" class="input-xlarge" ng-model="workExternalIdentifier.workExternalIdentifierType.value" ng-change="serverValidate('works/work/workExternalIdentifiersValidate.json')">																						 
								<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.WorkExternalIdentifierType.empty' /></option>
								<#list idTypes?keys as key>
									<option value="${idTypes[key]}">${key}</option>
								</#list>
							</select> 
							<span class="orcid-error" ng-show="workExternalIdentifier.workExternalIdentifierType.errors.length > 0">
								<div ng-repeat='error in workExternalIdentifier.workExternalIdentifierType.errors' ng-bind-html="error"></div>
							</span>
						</div>	
					</div>
			   		<div class="control-group">
						<label><@orcid.msg 'manual_work_form_contents.labelID'/></label>
					    <div class="relative">
							<input name="currentWorkExternalIds" type="text" class="input-xlarge"  ng-model="workExternalIdentifier.workExternalIdentifierId.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_ID'/>"  ng-change="serverValidate('works/work/workExternalIdentifiersValidate.json')" ng-model-onblur/>
							<span class="orcid-error" ng-show="workExternalIdentifier.workExternalIdentifierId.errors.length > 0">
								<div ng-repeat='error in workExternalIdentifier.workExternalIdentifierId.errors' ng-bind-html="error"></div>
							</span>
						</div>
					</div>	
					<div ng-show="$last" class="add-item-link">			
						<span><a href ng-click="addExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign blue"></i> <@orcid.msg 'manual_work_form_contents.add_external_identifier' /></a></span>
					</div>			
				</div>
			
				<div class="control-group">
		    		<label class="relative"><@orcid.msg 'manual_work_form_contents.labelURL'/></label>
		    		<div class="relative">
						<input name="url" type="text" class="input-xlarge"  ng-model="editWork.url.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL'/>" ng-change="serverValidate('works/work/urlValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editWork.url.errors.length > 0">
							<div ng-repeat='error in editWork.url.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>

				<div class="control-group">
					<label class="relative"><@orcid.msg 'manual_work_form_contents.labelformlanguage'/></label>
					<div class="relative">	
						<select id="language" name="language" ng-model="editWork.languageCode.value">
							<option value="${currentLocaleKey}">${currentLocaleValue}</option>
							<#list languages?keys as key>
								<option value="${languages[key]}">${key}</option>
							</#list>
						</select>
					</div>
				</div>

				<div class="control-group">
                    <label for="country"><@orcid.msg 'manual_work_form_contents.labelcountry'/></label>
                    <div class="relative">
                    	<select id="isoCountryCode" name="isoCountryCode" ng-model="editWork.countryCode.value">
                    		<option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
	                    	<#list isoCountries?keys as key>
								<option value="${key}">${isoCountries[key]}</option>								
							</#list>
						</select>                        
                    </div>
                </div>
				<div class="control-group">
					<div class="controls save-btns">
						<button class="btn btn-primary" ng-click="addWork()" ng-disabled="addingWork" ng-class="{disabled:addingWork}"><@orcid.msg 'manual_work_form_contents.btnaddtolist'/></button>
						<button id="" class="btn close-button" type="reset"  ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteExternalIdentifier.cancel")}</button>
					</div>
					<span ng-show="addingWork">
						<i class="glyphicon glyphicon-refresh spin x2 green"></i>
					</span>
					<span ng-show="editWork.errors.length > 0" class="alert" style><@orcid.msg 'common.please_fix_errors' /></span>
				</div>
			</div>			
		</div>		
</script>