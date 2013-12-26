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
 <script type="text/ng-template" id="add-grant-modal">
	<div id="edit-grant" class="edit-grant colorbox-content">		 
		<div class="row">
			<div class="col-md-8 col-sm-6 col-xs-9">
				<h1 class="lightbox-title pull-left"><@orcid.msg 'manual_grant_form_contents.add_grant'/></h1>
			</div>
			
			<div class="col-xs-3 visible-xs hidden-sm hidden-md hidden-lg">
				<a class="btn close-button" ng-click="closeModal()">X</a>
			</div>
			
			<div class="col-md-3 col-sm-2">
				<div class="control-group privacy-control pull-right">
		 			<label class="relative">
						<@orcid.msg 'privacyToggle.help.who_can_see'/>
					</label>
		 			<@orcid.privacyToggle "editGrant.visibility.visibility" "setAddGrantPrivacy('PUBLIC', $event)" 
		                   	  "setAddGrantPrivacy('LIMITED', $event)" "setAddGrantPrivacy('PRIVATE', $event)" />		
		 		</div>
			</div>

			<div class="col-md-1 col-sm-1 hidden-xs">
				<a class="btn close-button" ng-click="closeModal()">X</a>
			</div>
		</div>

		<div class="row">
			<div class="col-md-6 col-sm-6 col-xs-12">
				<div class="control-group" ng-show="editGrant.disambiguatedGrantSourceId">					
					<span>
					   <label><@orcid.msg 'manual_grant_form_contents.label_institution_organization'/></label>
				    </span>
					<span id="remove-disambiguated" class="pull-right">
						<a ng-click="removeDisambiguatedGrant()">
							<span class="glyphicon glyphicon-remove-sign"></span><@orcid.msg 'common.remove'/>
						</a>
					</span>

				    <div class="relative" style="font-weight: strong;">
						<span ng-bind="disambiguatedGrant.value"></span>						
					</div>
				</div>
				<div class="control-group">
					<span>
					   <label ng-hide="disambiguatedGrant"><@orcid.msg 'manual_grant_form_contents.labelinstitution'/></label>
					   <label ng-show="disambiguatedGrant"><@orcid.msg 'manual_grant_form_contents.labeldisplayinstitution'/></label>
					</span>
					<div class="relative">
						<input id="grantName" class="input-xlarge" name="grantName" type="text" ng-model="editGrant.grantName.value" placeholder="<@orcid.msg 'manual_grant_form_contents.add_name'/>" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editGrant.grantName)">*</span>
						<span class="orcid-error" ng-show="editGrant.grantName.errors.length > 0">
							<div ng-repeat='error in editGrant.grantName.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label ng-hide="disambiguatedGrant"><@orcid.msg 'manual_grant_form_contents.labelcity'/></label>
					<label ng-show="disambiguatedGrant"><@orcid.msg 'manual_grant_form_contents.labeldisplaycity'/></label>
				    <div class="relative">
						<input name="city" type="text" class="input-xlarge"  ng-model="editGrant.city.value" placeholder="<@orcid.msg 'manual_grant_form_contents.add_city'/>" ng-change="serverValidate('grants/grant/cityValidate.json')" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editGrant.city)">*</span>
						<span class="orcid-error" ng-show="editGrant.city.errors.length > 0">
							<div ng-repeat='error in editGrant.city.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label ng-hide="disambiguatedGrant"><@orcid.msg 'manual_grant_form_contents.labelregion'/></label>
					<label ng-show="disambiguatedGrant"><@orcid.msg 'manual_grant_form_contents.labeldisplayregion'/></label>
				    <div class="relative">
						<input name="region" type="text" class="input-xlarge"  ng-model="editGrant.region.value" placeholder="<@orcid.msg 'manual_grant_form_contents.add_region'/>" ng-change="serverValidate('grants/grant/regionValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editGrant.region.errors.length > 0">
							<div ng-repeat='error in editGrant.region.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
                <div class="control-group">
		    		<label ng-hide="disambiguatedGrant"><@orcid.msg 'manual_grant_form_contents.labelcountry'/></label>
		    		<label ng-show="disambiguatedGrant"><@orcid.msg 'manual_grant_form_contents.labeldisplaycountry'/></label>
		    		<div class="relative">
			    		<select id="country" name="country" ng-model="editGrant.country.value" ng-change="serverValidate('grants/grant/countryValidate.json')">
			    			<option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
							<#list isoCountries?keys as key>
								    <option value="${key}">${isoCountries[key]}</option>
							</#list>
						</select> 
						<span class="required" ng-class="isValidClass(editGrant.country)">*</span>
						<span class="orcid-error" ng-show="editGrant.country.errors.length > 0">
							<div ng-repeat='error in editGrant.country.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<span>
					   <label><@orcid.msg 'manual_grant_form_contents.label_title'/></label>					   
					</span>
					<div class="relative">
						<input id="grantTitle" class="input-xlarge" name="grantTitle" type="text" ng-model="editGrant.title.value" placeholder="<@orcid.msg 'manual_grant_form_contents.add_title'/>" ng-change="serverValidate('grants/grant/titleValidate.json')" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editGrant.title)">*</span>
						<span class="orcid-error" ng-show="editGrant.title.errors.length > 0">
							<div ng-repeat='error in editGrant.title.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<span>
					   <label><@orcid.msg 'manual_grant_form_contents.label_description'/></label>					   
					</span>
					<div class="relative">
						<input id="grantDescription" class="input-xlarge" name="grantDescription" type="text" ng-model="editGrant.description.value" placeholder="<@orcid.msg 'manual_grant_form_contents.add_description'/>" ng-change="serverValidate('grants/grant/descriptionValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editGrant.description.errors.length > 0">
							<div ng-repeat='error in editGrant.description.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<span>
					   <label><@orcid.msg 'manual_grant_form_contents.label_url'/></label>					   
					</span>
					<div class="relative">
						<input id="grantUrl" class="input-xlarge" name="grantUrl" type="text" ng-model="editGrant.url.value" placeholder="<@orcid.msg 'manual_grant_form_contents.add_url'/>" ng-change="serverValidate('grants/grant/urlValidate.json')" ng-model-onblur/>						
						<span class="orcid-error" ng-show="editGrant.url.errors.length > 0">
							<div ng-repeat='error in editGrant.url.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<span>
						<label><@orcid.msg 'manual_grant_form_contents.grant_type'/></label>
					</span>
					<div class="relative">						
						<select id="grantType" name="grantType" ng-model="editGrant.grantType.value" ng-change="serverValidate('grants/grant/typeValidate.json')">			
							<#list grantTypes?keys as key>
								<option value="${key}">${grantTypes[key]}</option>
							</#list>
						</select>
						<span class="required" ng-class="isValidClass(editGrant.grantType)">*</span>
						<span class="orcid-error" ng-show="editGrant.grantType.errors.length > 0">
							<div ng-repeat='error in editGrant.grantType.errors' ng-bind-html-unsafe="error"></div>
						</span>				
					</div>
				</div>	
				<div class="control-group">
					<span>
						<label><@orcid.msg 'manual_grant_form_contents.label_amount'/></label>
					</span>
					<div class="relative">						
						<div class="relative">
							<select id="currencyCode" name="currencyCode" ng-model="editGrant.currencyCode.value" ng-change="serverValidate('grants/grant/currencyValidate.json')">			
								<#list currencyCodeTypes?keys as key>
									<option value="${currencyCodeTypes[key]}">${key}</option>
								</#list>
							</select>	
							<input id="grantAmount" class="input-xlarge" name="grantAmount" type="text" ng-model="editGrant.amount.value" placeholder="<@orcid.msg 'manual_grant_form_contents.add_amount'/>" ng-change="serverValidate('grants/grant/amountValidate.json')" ng-model-onblur/>
							<span class="required" ng-class="isValidClass(editGrant.amount)">*</span>
						</div>
						<span class="orcid-error" ng-show="editGrant.currencyCode.errors.length > 0">
							<div ng-repeat='error in editGrant.currencyCode.errors' ng-bind-html-unsafe="error"></div>
						</span>
						<span class="orcid-error" ng-show="editGrant.amount.errors.length > 0">
							<div ng-repeat='error in editGrant.amount.errors' ng-bind-html-unsafe="error"></div>
						</span>			
					</div>
				</div>							
			</div>
			
								
			<div class="col-md-6 col-sm-6 col-xs-12">
				<div class="control-group">
		    		<label class="relative"><@orcid.msg 'manual_grant_form_contents.labelStartDate'/></label>
		    		<div class="relative">
				    <select id="startDay" name="startDay" ng-model="editGrant.startDate.day">
						<#list days?keys as key>
							<option value="${key}">${days[key]}</option>
						</#list>
		    		</select>
				    <select id="startMonth" name="startMonth" ng-model="editGrant.startDate.month">
						<#list months?keys as key>
							<option value="${key}">${months[key]}</option>
						</#list>
		    		</select>
				    <select id="startYear" name="startMonth" ng-model="editGrant.startDate.year">
						<#list years?keys as key>
							<option value="${key}">${years[key]}</option>
						</#list>
		    		</select>
		    		</div>
		    	</div>
		    	<div class="control-group">
		    		<label class="relative"><@orcid.msg 'manual_grant_form_contents.labelEndDateLeave'/></label>
		    		<div class="relative">
				    <select id="endDay" name="endDay" ng-model="editGrant.endDate.day">
						<#list days?keys as key>
							<option value="${key}">${days[key]}</option>
						</#list>
		    		</select>
				    <select id="endMonth" name="endMonth" ng-model="editGrant.endDate.month">
						<#list months?keys as key>
							<option value="${key}">${months[key]}</option>
						</#list>
		    		</select>
				    <select id="endYear" name="endMonth" ng-model="editGrant.endDate.year">
						<#list years?keys as key>
							<option value="${key}">${years[key]}</option>
						</#list>
		    		</select>
		    		</div>
		    		<span class="orcid-error" ng-show="editGrant.endDate.errors.length > 0">
						<div ng-repeat='error in editGrant.endDate.errors' ng-bind-html-unsafe="error"></div>
					</span>
		    	</div>	
			
				<div class="control-group" ng-repeat="contributor in editGrant.contributors">
				    <label class="relative"><@orcid.msg 'manual_grant_form_contents.label_role'/></label>
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
				<div class="control-group" ng-repeat="contributor in editGrant.contributors">
				    <label class="relative"><@orcid.msg 'manual_grant_form_contents.labelcredited'/></label>
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
		    	<div class="control-group">
					<span><strong><@orcid.msg 'manual_grant_form_contents.title_external_identifier'/></strong></span>
				</div>
		    	<div ng-repeat="externalIdentifier in editGrant.externalIdentifiers"> 
					<!-- Type -->
					<div class="control-group">
						<label class="relative"><@orcid.msg 'manual_grant_form_contents.external_identifier.labelType'/></label>
						<div class="relative">
		    				<input name="currentGrantExternalIdentifierType" type="text" class="input-xlarge" ng-model="externalIdentifier.type.value" placeholder="<@orcid.msg 'manual_grant_form_contents.external_identifier.type'/>" ng-model-onblur/>
							<span class="orcid-error" ng-show="externalIdentifier.type.errors.length > 0">
								<div ng-repeat='error in externalIdentifier.type.errors' ng-bind-html-unsafe="error"></div>
							</span>
						</div>	
					</div>
					<!-- Value -->
					<div class="control-group">
						<label class="relative"><@orcid.msg 'manual_grant_form_contents.external_identifier.labelValue'/></label>
						<div class="relative">
		    				<input name="currentGrantExternalIdentifierValue" type="text" class="input-xlarge" ng-model="externalIdentifier.value.value" placeholder="<@orcid.msg 'manual_grant_form_contents.external_identifier.value'/>" ng-model-onblur/>
							<span class="orcid-error" ng-show="externalIdentifier.value.errors.length > 0">
								<div ng-repeat='error in externalIdentifier.value.errors' ng-bind-html-unsafe="error"></div>
							</span>
						</div>	
					</div>
					<!-- URL -->
					<div class="control-group">
						<label class="relative"><@orcid.msg 'manual_grant_form_contents.external_identifier.labelUrl'/></label>
						<div class="relative">
		    				<input name="currentGrantExternalIdentifierUrl" type="text" class="input-xlarge" ng-model="externalIdentifier.url.value" placeholder="<@orcid.msg 'manual_grant_form_contents.external_identifier.url'/>" ng-model-onblur/>
							<span class="orcid-error" ng-show="externalIdentifier.url.errors.length > 0">
								<div ng-repeat='error in externalIdentifier.url.errors' ng-bind-html-unsafe="error"></div>
							</span>
						</div>	
					</div>
					<hr />
			   		
					<div ng-show="$last" class="add-item-link">			
						<span><a href ng-click="addExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign blue"></i> <@orcid.msg 'manual_grant_form_contents.add_external_identifier' /></a></span>
					</div>			
				</div>
				
		    	<div class="small-row">
					<button class="btn btn-primary" ng-click="addGrant()" ng-disabled="addingGrant" ng-class="{disabled:addingGrant}"><@orcid.msg 'manual_grant_form_contents.btnaddtolist'/></button> 
					<a href="" ng-click="closeModal()"><@orcid.msg 'manage.deleteExternalIdentifier.cancel'/></a>
					<span ng-show="addingGrant">
						<i class="glyphicon glyphicon-refresh spin x2 green"></i>
					</span>					
					<span ng-show="editGrant.errors.length > 0" class="alert"><@orcid.msg 'manual_grant_form_contents.fix_errors'/></span>
				</div>
			</div>
	</div>
</script>
