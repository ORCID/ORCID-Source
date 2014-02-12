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
<script type="text/ng-template" id="add-funding-modal">
	<div id="edit-funding" class="edit-funding colorbox-content">		 
		<div class="row">
			
			<div class="col-md-8 col-sm-8 col-xs-9">
				<h1 class="lightbox-title pull-left"><@orcid.msg 'manual_funding_form_contents.add_grant'/></h1>
			</div>
			
			<div class="col-xs-3 visible-xs hidden-sm hidden-md hidden-lg">
				<a class="btn close-button pull-right" ng-click="closeModal()">X</a>
			</div>
			
			<div class="col-md-3 col-sm-3 pull-left">
				<div class="control-group privacy-control">
		 			<label class="relative">
						<@orcid.msg 'privacyToggle.help.who_can_see'/>
					</label>
		 			<@orcid.privacyToggle "editFunding.visibility.visibility" "setAddFundingPrivacy('PUBLIC', $event)" 
		                   	  "setAddFundingPrivacy('LIMITED', $event)" "setAddFundingPrivacy('PRIVATE', $event)" />		
		 		</div>
			</div>

			<div class="col-md-1 col-sm-1 hidden-xs">
				<a class="btn close-button" ng-click="closeModal()">X</a>
			</div>
			
		</div>	
		
		<div class="row">
			<div class="col-md-6 col-sm-6 col-xs-12">
				<div class="control-group">
					<span>
						<label><@orcid.msg 'manual_funding_form_contents.grant_type'/></label>
					</span>
					<div class="relative">						
						<select id="fundingType" class="input-xlarge" name="fundingType" ng-model="editFunding.fundingType.value" ng-change="serverValidate('fundings/funding/typeValidate.json')">
							<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.FundingType.empty' /></option>
							<#list fundingTypes?keys as key>
								<option value="${key}">${fundingTypes[key]}</option>
							</#list>
						</select>
						<span class="required text-error" ng-class="isValidClass(editFunding.fundingType)">*</span>
						<span class="orcid-error" ng-show="editFunding.fundingType.errors.length > 0">
							<div ng-repeat='error in editFunding.fundingType.errors' ng-bind-html="error"></div>
						</span>				
					</div>
				</div>
				<div class="control-group">
					<span>
					   <label><@orcid.msg 'manual_funding_form_contents.label_title'/></label>					   
					</span>
					<div class="relative">
						<input id="fundingTitle" class="input-xlarge" name="fundingTitle" type="text" ng-model="editFunding.fundingTitle.title.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_title'/>" ng-change="serverValidate('fundings/funding/titleValidate.json')" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editFunding.fundingTitle.title)">*</span>
						<span class="orcid-error" ng-show="editFunding.fundingTitle.title.errors.length > 0">
							<div ng-repeat='error in editFunding.fundingTitle.title.errors' ng-bind-html="error"></div>
						</span>						
						<div class="add-item-link">
							<span ng-hide="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-plus-sign blue"></i> <@orcid.msg 'manual_funding_form_contents.labelshowtranslatedtitle'/></a></span>
							<span ng-show="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-minus-sign blue"></i> <@orcid.msg 'manual_funding_form_contents.labelhidetranslatedtitle'/></a></span>
						</div>
					</div>
				</div>
				<div id="translatedTitle">
					<span class="orcid-error" ng-show="editFunding.fundingTitle.translatedTitle.errors.length > 0">
						<div ng-repeat='error in editFunding.fundingTitle.translatedTitle.errors' ng-bind-html="error"></div>
					</span>
					<div class="control-group">
						<label><@orcid.msg 'manual_funding_form_contents.label_translated_title'/></label>
						<div class="relative">
							<input name="translatedTitle" type="text" class="input-xlarge" ng-model="editFunding.fundingTitle.translatedTitle.content" placeholder="<@orcid.msg 'manual_funding_form_contents.add_translated_title'/>" ng-change="serverValidate('fundings/funding/translatedTitleValidate.json')" ng-model-onblur/>														
						</div>						
					</div>

					<div class="control-group">
						<label class="relative"><@orcid.msg 'manual_funding_form_contents.label_translated_title_language'/></label>
						<div class="relative">						
							<select id="language" name="language" ng-model="editFunding.fundingTitle.translatedTitle.languageCode" ng-change="serverValidate('fundings/funding/translatedTitleValidate.json')">			
								<#list languages?keys as key>
									<option value="${languages[key]}">${key}</option>
								</#list>
							</select>				
						</div>
					</div>					
				</div>
				<div class="control-group">
					<span>
						<label><@orcid.msg 'manual_funding_form_contents.label_amount'/></label>
					</span>
					<div class="relative">						
						<div class="relative">
							<select id="currencyCode" name="currencyCode" ng-model="editFunding.currencyCode.value" ng-change="serverValidate('fundings/funding/currencyValidate.json')">			
								<#list currencyCodeTypes?keys as key>
									<option value="${currencyCodeTypes[key]}">${key}</option>
								</#list>
							</select>	
							<input id="fundingAmount" name="fundingAmount" type="text" ng-model="editFunding.amount.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_amount'/>" ng-change="serverValidate('fundings/funding/amountValidate.json')" ng-model-onblur/>
							<span class="required" ng-class="isValidClass(editFunding.amount)">*</span>
						</div>
						<span class="orcid-error" ng-show="editFunding.currencyCode.errors.length > 0">
							<div ng-repeat='error in editFunding.currencyCode.errors' ng-bind-html="error"></div>
						</span>
						<span class="orcid-error" ng-show="editFunding.amount.errors.length > 0">
							<div ng-repeat='error in editFunding.amount.errors' ng-bind-html="error"></div>
						</span>			
					</div>
				</div>		
				<div class="control-group">
		    		<label class="relative"><@orcid.msg 'manual_funding_form_contents.labelStartDate'/></label>
		    		<div class="relative">
					    <select id="startYear" name="startMonth" ng-model="editFunding.startDate.year">
							<#list years?keys as key>
								<option value="${key}">${years[key]}</option>
							</#list>
			    		</select>				    
					    <select id="startMonth" name="startMonth" ng-model="editFunding.startDate.month">
							<#list months?keys as key>
								<option value="${key}">${months[key]}</option>
							</#list>
			    		</select>
					    <select id="startDay" name="startDay" ng-model="editFunding.startDate.day">
							<#list days?keys as key>
								<option value="${key}">${days[key]}</option>
							</#list>
			    		</select>
		    		</div>
		    	</div>
		    	<div class="control-group">
		    		<label class="relative"><@orcid.msg 'manual_funding_form_contents.labelEndDateLeave'/></label>
		    		<div class="relative">					    
			    		<select id="endYear" name="endMonth" ng-model="editFunding.endDate.year">
							<#list fundingYears?keys as key>
								<option value="${key}">${fundingYears[key]}</option>
							</#list>
			    		</select>
					    <select id="endMonth" name="endMonth" ng-model="editFunding.endDate.month">
							<#list months?keys as key>
								<option value="${key}">${months[key]}</option>
							</#list>
			    		</select>			
			    		<select id="endDay" name="endDay" ng-model="editFunding.endDate.day">
							<#list days?keys as key>
								<option value="${key}">${days[key]}</option>
							</#list>
			    		</select>		    
		    		</div>
		    		<span class="orcid-error" ng-show="editFunding.endDate.errors.length > 0">
						<div ng-repeat='error in editFunding.endDate.errors' ng-bind-html="error"></div>
					</span>
		    	</div>
				<div class="control-group" ng-repeat="contributor in editFunding.contributors">
				    <label class="relative"><@orcid.msg 'manual_funding_form_contents.label_role'/></label>
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
				<div class="control-group" ng-repeat="contributor in editFunding.contributors">
				    <label class="relative"><@orcid.msg 'manual_funding_form_contents.labelcredited'/></label>
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
					<span>
					   <label><@orcid.msg 'manual_funding_form_contents.label_description'/></label>					   
					</span>
					<div class="relative">
						<input id="fundingDescription" class="input-xlarge" name="fundingDescription" type="text" ng-model="editFunding.description.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_description'/>" ng-change="serverValidate('fundings/funding/descriptionValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editFunding.description.errors.length > 0">
							<div ng-repeat='error in editFunding.description.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>
			</div>
			<div class="col-md-6 col-sm-6 col-xs-12">
				<div class="control-group">
					<span><strong><@orcid.msg 'manual_funding_form_contents.title_funding_agency'/></strong></span>
				</div>
				<div class="control-group" ng-show="editFunding.disambiguatedFundingSourceId">					
					<span>
					   <label><@orcid.msg 'manual_funding_form_contents.label_funding_agency'/></label>
				    </span>
					<span id="remove-disambiguated" class="pull-right">
						<a ng-click="removeDisambiguatedFunding()">
							<span class="glyphicon glyphicon-remove-sign"></span><@orcid.msg 'common.remove'/>
						</a>
					</span>

				    <div class="relative" style="font-weight: strong;">
						<span ng-bind="disambiguatedFunding.value"></span>						
					</div>
				</div>
				<div class="control-group">
					<span ng-hide="disambiguatedFunding">												
					   	<label><@orcid.msg 'manual_funding_form_contents.label_funding_agency_name'/></label>
					   	<div id="fundingOnlyDiv" class="right show-funding-only">
							<input type="checkbox" id="fundersOnly" />
							<label for="fundersOnly"><@orcid.msg 'manual_funding_form_contents.show_only_funders'/></label>
						</div>					   
					</span>
					<span ng-show="disambiguatedFunding">
						<label><@orcid.msg 'manual_funding_form_contents.label_funding_agency_display_name'/></label>
					</span>	
					<div class="relative">
						<input id="fundingName" class="input-xlarge" name="fundingName" type="text" ng-model="editFunding.fundingName.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_name'/>" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editFunding.fundingName)">*</span>
						<span class="orcid-error" ng-show="editFunding.fundingName.errors.length > 0">
							<div ng-repeat='error in editFunding.fundingName.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label ng-hide="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_city'/></label>
					<label ng-show="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_display_city'/></label>
				    <div class="relative">
						<input name="city" type="text" class="input-xlarge"  ng-model="editFunding.city.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_city'/>" ng-change="serverValidate('fundings/funding/cityValidate.json')" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editFunding.city)">*</span>
						<span class="orcid-error" ng-show="editFunding.city.errors.length > 0">
							<div ng-repeat='error in editFunding.city.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label ng-hide="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_region'/></label>
					<label ng-show="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_display_region'/></label>
				    <div class="relative">
						<input name="region" type="text" class="input-xlarge"  ng-model="editFunding.region.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_region'/>" ng-change="serverValidate('fundings/funding/regionValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editFunding.region.errors.length > 0">
							<div ng-repeat='error in editFunding.region.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>
                <div class="control-group">
		    		<label ng-hide="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_country'/></label>
		    		<label ng-show="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_display_country'/></label>
		    		<div class="relative">
			    		<select id="country" class="input-xlarge" name="country" ng-model="editFunding.country.value" ng-change="serverValidate('fundings/funding/countryValidate.json')">
			    			<option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
							<#list isoCountries?keys as key>
								    <option value="${key}">${isoCountries[key]}</option>
							</#list>
						</select> 
						<span class="required" ng-class="isValidClass(editFunding.country)">*</span>
						<span class="orcid-error" ng-show="editFunding.country.errors.length > 0">
							<div ng-repeat='error in editFunding.country.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>					
		    	<div class="control-group">
					<span><strong><@orcid.msg 'manual_funding_form_contents.title_external_identifier'/></strong></span>
				</div>
		    	<div ng-repeat="externalIdentifier in editFunding.externalIdentifiers"> 
					<!-- Type -->
					<div class="control-group">
						<label class="relative"><@orcid.msg 'manual_funding_form_contents.external_identifier.label_type'/></label>
						<div class="relative">
		    				<label name="currentFundingExternalIdentifierType" class="input-xlarge type-preset"><@orcid.msg 'manual_funding_form_contents.external_identifier.type_default'/></label>							
						</div>	
					</div>
					<!-- Value -->
					<div class="control-group">
						<label class="relative"><@orcid.msg 'manual_funding_form_contents.external_identifier.label_value'/></label>
						<div class="relative">
		    				<input name="currentFundingExternalIdentifierValue" type="text" class="input-xlarge" ng-model="externalIdentifier.value.value" placeholder="<@orcid.msg 'manual_funding_form_contents.external_identifier.value'/>" ng-model-onblur/>
							<span class="orcid-error" ng-show="externalIdentifier.value.errors.length > 0">
								<div ng-repeat='error in externalIdentifier.value.errors' ng-bind-html="error"></div>
							</span>
						</div>	
					</div>
					<!-- URL -->
					<div class="control-group">
						<label class="relative"><@orcid.msg 'manual_funding_form_contents.external_identifier.label_url'/></label>
						<div class="relative">
		    				<input name="currentFundingExternalIdentifierUrl" type="text" class="input-xlarge" ng-model="externalIdentifier.url.value" placeholder="<@orcid.msg 'manual_funding_form_contents.external_identifier.url'/>" ng-model-onblur/>
							<span class="orcid-error" ng-show="externalIdentifier.url.errors.length > 0">
								<div ng-repeat='error in externalIdentifier.url.errors' ng-bind-html="error"></div>
							</span>
						</div>	
					</div>
					<hr />			   		
					<div ng-show="$last" class="add-item-link">			
						<span><a href ng-click="addExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign blue"></i> <@orcid.msg 'manual_funding_form_contents.add_external_identifier' /></a></span>
					</div>			
				</div>
				<div class="control-group">
					<span>
					   <label><@orcid.msg 'manual_funding_form_contents.label_url'/></label>					   
					</span>
					<div class="relative">
						<input id="fundingUrl" class="input-xlarge" name="fundingUrl" type="text" ng-model="editFunding.url.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_url'/>" ng-change="serverValidate('fundings/funding/urlValidate.json')" ng-model-onblur/>						
						<span class="orcid-error" ng-show="editFunding.url.errors.length > 0">
							<div ng-repeat='error in editFunding.url.errors' ng-bind-html="error"></div>
						</span>
					</div>
				</div>		
		    	<div class="row">
					<div class="col-md-3 col-sm-3 col-xs-12">	
						<button class="btn btn-primary" ng-click="addFunding()" ng-disabled="addingFunding" ng-class="{disabled:addingFunding}"><@orcid.msg 'manual_funding_form_contents.btnaddtolist'/></button> 
						<a href="" ng-click="closeModal()" class="cancel-action"><@orcid.msg 'manage.deleteExternalIdentifier.cancel'/></a>
						<span ng-show="addingFunding">
							<i class="glyphicon glyphicon-refresh spin x2 green"></i>
						</span>					
						<span ng-show="editFunding.errors.length > 0" class="alert"><@orcid.msg 'common.please_fix_errors' /></span>
					</div>
				</div>
			</div>
	</div>
</script>
