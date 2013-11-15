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
<script type="text/ng-template" id="add-affiliation-modal">
	<div id="edit-affiliation" class="edit-affiliation colorbox-content">
		<div class="lightbox-container"> 
		<div class="row">
			<div class="col-md-11 col-sm-10 col-xs-10">
				<h1 ng-show="addAffType == null || addAffType == undefined " class="lightbox-title pull-left"><@orcid.msg 'manual_affiliation_form_contents.add_affiliation'/></h1>
				<h1 ng-show="addAffType == 'education'" class="lightbox-title pull-left"><@orcid.msg 'manual_affiliation_form_contents.add_education'/></h1>
				<h1 ng-show="addAffType == 'employment'" class="lightbox-title pull-left"><@orcid.msg 'manual_affiliation_form_contents.add_employment'/></h1>				
			</div>
			<div class="col-md-1 col-sm-2 col-xs-2">
				<a class="btn pull-right close-button" ng-click="closeModal()">X</a>
			</div>
		</div>

		<div class="row">
			<div class="col-md-6 col-sm-6 col-xs-12">
				<div class="control-group" ng-show="editAffiliation.disambiguatedAffiliationSourceId">
					<div style="position: absolute; left: 300px"><label><a class="icon-remove-sign grey" ng-click="removeDisambiguatedAffiliation()">&nbsp;<@orcid.msg 'common.remove'/></a></label></div>
					<label>Affiliation</label>
				    <div class="relative"  style="font-weight: strong;">
						<span ng-bind="disambiguatedAffiliation.value"></span> <br />
						<div>
						    <span ng-bind="disambiguatedAffiliation.city"></span><span ng-show="disambiguatedAffiliation.region"> (<span ng-bind="disambiguatedAffiliation.region"></span>)</span>, <span ng-bind="disambiguatedAffiliation.orgType"></span>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label ng-hide="editAffiliation.disambiguatedAffiliationIdentifier"><@orcid.msg 'manual_affiliation_form_contents.labelname'/></label>
					<label ng-show="editAffiliation.disambiguatedAffiliationIdentifier"><@orcid.msg 'manual_affiliation_form_contents.labeldisplayname'/></label>
				    <div class="relative">
						<input id="affiliationName" class="input-xlarge" name="affiliationName" type="text" ng-model="editAffiliation.affiliationName.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_name'/>" ng-change="serverValidate('affiliations/affiliation/affiliationNameValidate.json')" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editAffiliation.affiliationName)">*</span>
						<span class="orcid-error" ng-show="editAffiliation.affiliationName.errors.length > 0">
							<div ng-repeat='error in editAffiliation.affiliationName.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label ng-hide="editAffiliation.disambiguatedAffiliationIdentifier"><@orcid.msg 'manual_affiliation_form_contents.labelcity'/></label>
					<label ng-show="editAffiliation.disambiguatedAffiliationIdentifier"><@orcid.msg 'manual_affiliation_form_contents.labeldisplaycity'/></label>
				    <div class="relative">
						<input name="city" type="text" class="input-xlarge"  ng-model="editAffiliation.city.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_city'/>" ng-change="serverValidate('affiliations/affiliation/cityValidate.json')" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editAffiliation.city)">*</span>
						<span class="orcid-error" ng-show="editAffiliation.city.errors.length > 0">
							<div ng-repeat='error in editAffiliation.city.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label ng-hide="editAffiliation.disambiguatedAffiliationIdentifier"><@orcid.msg 'manual_affiliation_form_contents.labelregion'/></label>
					<label ng-show="editAffiliation.disambiguatedAffiliationIdentifier"><@orcid.msg 'manual_affiliation_form_contents.labeldisplayregion'/></label>
				    <div class="relative">
						<input name="region" type="text" class="input-xlarge"  ng-model="editAffiliation.region.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_region'/>" ng-change="serverValidate('affiliations/affiliation/regionValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editAffiliation.region.errors.length > 0">
							<div ng-repeat='error in editAffiliation.region.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
                <div class="control-group">
		    		<label ng-hide="editAffiliation.disambiguatedAffiliationIdentifier"><@orcid.msg 'manual_affiliation_form_contents.labelcountry'/></label>
		    		<label ng-show="editAffiliation.disambiguatedAffiliationIdentifier"><@orcid.msg 'manual_affiliation_form_contents.labeldisplaycountry'/></label>
		    		<div class="relative">
			    		<select id="country" name="country" ng-model="editAffiliation.country.value" ng-change="serverValidate('affiliations/affiliation/countryValidate.json')">
			    			<option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
							<#list isoCountries?keys as key>
								    <option value="${key}">${isoCountries[key]}</option>
							</#list>
						</select> 
						<span class="required" ng-class="isValidClass(editAffiliation.country)">*</span>
						<span class="orcid-error" ng-show="editAffiliation.country.errors.length > 0">
							<div ng-repeat='error in editAffiliation.country.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
			</div>
			<div class="col-md-6 col-sm-6 col-xs-12">
				<div class="control-group">
					<label><@orcid.msg 'manual_affiliation_form_contents.labeldepartment'/></label>
				    <div class="relative">
						<input id="departmentName" class="input-xlarge" name="departmentName" type="text" ng-model="editAffiliation.departmentName.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_department'/>" ng-change="serverValidate('affiliations/affiliation/departmentValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editAffiliation.departmentName.errors.length > 0">
							<div ng-repeat='error in editAffiliation.departmentName.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label><@orcid.msg 'manual_affiliation_form_contents.labelroletitle'/></label>
				    <div class="relative">
						<input name="roletitle" type="text" class="input-xlarge"  ng-model="editAffiliation.roleTitle.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_title'/>" ng-change="serverValidate('affiliations/affiliation/roleTitleValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editAffiliation.roleTitle.errors.length > 0">
							<div ng-repeat='error in editAffiliation.roleTitle.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>

				<div ng-show="addAffType != 'education' && addAffType != 'employment'" class="control-group">
		    		<label class="relative"><@orcid.msg 'manual_affiliation_form_contents.labelaffiliationtype'/></label>
		    		<div class="relative">
			    		<select id="affiliationType" name="affiliationType" class="input-xlarge" ng-model="editAffiliation.affiliationType.value">
			    			<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.empty' /></option>
							<#list affiliationTypes?keys as key>
								<option value="${key}">${affiliationLongDescriptionTypes[key]}</option>
							</#list>
						</select> 
					</div>
				</div>
				<div class="control-group">
		    		<label class="relative" for="manualAffiliation.startDay"><@orcid.msg 'manual_affiliation_form_contents.labelStartDate'/></label>
		    		<div class="relative">
				    <select id="startDay" name="startDay" ng-model="editAffiliation.startDate.day">
						<#list days?keys as key>
							<option value="${key}">${days[key]}</option>
						</#list>
		    		</select>
				    <select id="startMonth" name="startMonth" ng-model="editAffiliation.startDate.month">
						<#list months?keys as key>
							<option value="${key}">${months[key]}</option>
						</#list>
		    		</select>
				    <select id="startYear" name="startMonth" ng-model="editAffiliation.startDate.year">
						<#list years?keys as key>
							<option value="${key}">${years[key]}</option>
						</#list>
		    		</select>
		    		</div>
		    	</div>
		    	<div class="control-group">
		    		<label class="relative" for="manualAffiliation.endDay"><@orcid.msg 'manual_affiliation_form_contents.labelEndDate'/></label>
		    		<div class="relative">
				    <select id="endDay" name="endDay" ng-model="editAffiliation.endDate.day">
						<#list days?keys as key>
							<option value="${key}">${days[key]}</option>
						</#list>
		    		</select>
				    <select id="endMonth" name="endMonth" ng-model="editAffiliation.endDate.month">
						<#list months?keys as key>
							<option value="${key}">${months[key]}</option>
						</#list>
		    		</select>
				    <select id="endYear" name="endMonth" ng-model="editAffiliation.endDate.year">
						<#list years?keys as key>
							<option value="${key}">${years[key]}</option>
						</#list>
		    		</select>
		    		</div>
		    		<span class="orcid-error" ng-show="editAffiliation.endDate.errors.length > 0">
						<div ng-repeat='error in editAffiliation.endDate.errors' ng-bind-html-unsafe="error"></div>
					</span>
		    	</div>
		    	<div class="control-group">
		 			<label class="relative"><@orcid.msg 'privacyToggle.help.who_can_see'/></label>
		 				<@orcid.privacyToggle "editAffiliation.visibility.visibility" "setAddAffiliationPrivacy('PUBLIC', $event)" 
		                    	  "setAddAffiliationPrivacy('LIMITED', $event)" "setAddAffiliationPrivacy('PRIVATE', $event)" />
		 		</div>
			</div>
		</div>
		<div class="row">			
			<div class="col-md-offset-6 col-md-4 col-offset-sm-6 col-sm-4 col-xs-12">
				<button class="btn btn-primary" ng-click="addAffiliation()" ng-disabled="addingAffiliation" ng-class="{disabled:addingAffiliation}"><@orcid.msg 'manual_affiliation_form_contents.btnaddtolist'/></button> 
				<a href="" ng-click="closeModal()"><@orcid.msg 'manage.deleteExternalIdentifier.cancel'/></a>
			</div>
			<div class="col-md-2 col-sm-6 col-xs-6">
				<span ng-show="addingAffiliation">
					<i class="glyphicon glyphicon-refresh spin x2 green"></i>
				</span>				
			</div>			
		</div>		
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
				<span ng-show="editWork.errors.length > 0" class="alert">Please fix above errors</span>
			</div>
		</div>
		</div>
	</div>
</script>
