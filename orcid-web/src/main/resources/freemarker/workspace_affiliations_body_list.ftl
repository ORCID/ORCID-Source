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

<script type="text/ng-template" id="delete-affiliation-modal">
	<div style="padding: 20px;">
		<h3 style="margin-bottom: 0px;">${springMacroRequestContext.getMessage("manage.deleteAffiliation.pleaseConfirm")}</h3>
		{{fixedTitle}}<br />
		<br />
    	<div class="btn btn-danger" ng-click="deleteByIndex()">
    		${springMacroRequestContext.getMessage("manage.deleteAffiliation.delete")}
    	</div>
    	<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteAffiliation.cancel")}</a>
    <div>
</script>

<script type="text/ng-template" id="add-affiliation-modal">
	<div class="edit-affiliation colorbox-content">
		<div class="row">
			<div class="span12">
				<h1 class="lightbox-title pull-left"><@orcid.msg 'manual_affiliation_form_contents.add_affiliation'/></h1>
				<a class="btn pull-right close-button" ng-click="closeModal()">X</a>
			</div>
		</div>
		<div class="row">
			<div class="span6">
				<div class="control-group">
					<label><@orcid.msg 'manual_affiliation_form_contents.labelname'/></label>
				    <div class="relative">
						<input id="affiliationName" name="affiliationName" type="text" ng-model="editAffiliation.affiliationName.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_name'/>" ng-change="serverValidate('affiliations/affiliation/affiliationNameValidate.json')" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editAffiliation.affiliationName)">*</span>
						<span class="orcid-error" ng-show="editAffiliation.affiliationName.errors.length > 0">
							<div ng-repeat='error in editAffiliation.affiliationName.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label><@orcid.msg 'manual_affiliation_form_contents.labelcity'/></label>
				    <div class="relative">
						<input name="city" type="text" class="input-xlarge"  ng-model="editAffiliation.city.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_city'/>" ng-change="serverValidate('affiliations/affiliation/cityValidate.json')" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editAffiliation.city)">*</span>
						<span class="orcid-error" ng-show="editAffiliation.city.errors.length > 0">
							<div ng-repeat='error in editAffiliation.city.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label><@orcid.msg 'manual_affiliation_form_contents.labelregion'/></label>
				    <div class="relative">
						<input name="region" type="text" class="input-xlarge"  ng-model="editAffiliation.region.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_region'/>" ng-change="serverValidate('affiliations/affiliation/regionValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editAffiliation.affiliationName.errors.length > 0">
							<div ng-repeat='error in editAffiliation.region.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
                <div class="control-group">
		    		<label class="relative"><@orcid.msg 'manage_bio_settings.labelcountry'/></label>
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
				<div class="control-group">
					<label><@orcid.msg 'manual_affiliation_form_contents.labeldepartment'/></label>
				    <div class="relative">
						<input name="department" type="text" class="input-xlarge"  ng-model="editAffiliation.departmentName.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_department'/>" ng-change="serverValidate('affiliations/affiliation/departmentValidate.json')" ng-model-onblur/>
						<span class="orcid-error" ng-show="editAffiliation.departmentName.errors.length > 0">
							<div ng-repeat='error in editAffiliation.departmentName.errors' ng-bind-html-unsafe="error"></div>
						</span>
					</div>
				</div>
				<div class="control-group">
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
				    <select id="startDay" name="startDay" ng-model="editAffiliation.startDate.day" class="span1">
						<#list days?keys as key>
							<option value="${key}">${days[key]}</option>
						</#list>
		    		</select>
				    <select id="startMonth" name="startMonth" ng-model="editAffiliation.startDate.month" class="span1">
						<#list months?keys as key>
							<option value="${key}">${months[key]}</option>
						</#list>
		    		</select>
				    <select id="startYear" name="startMonth" ng-model="editAffiliation.startDate.year" class="span2">
						<#list years?keys as key>
							<option value="${key}">${years[key]}</option>
						</#list>
		    		</select>
		    		</div>
		    	</div>
		    	<div class="control-group">
		    		<label class="relative" for="manualAffiliation.endDay"><@orcid.msg 'manual_affiliation_form_contents.labelEndDate'/></label>
		    		<div class="relative">
				    <select id="endDay" name="endDay" ng-model="editAffiliation.endDate.day" class="span1">
						<#list days?keys as key>
							<option value="${key}">${days[key]}</option>
						</#list>
		    		</select>
				    <select id="endMonth" name="endMonth" ng-model="editAffiliation.endDate.month" class="span1">
						<#list months?keys as key>
							<option value="${key}">${months[key]}</option>
						</#list>
		    		</select>
				    <select id="endYear" name="endMonth" ng-model="editAffiliation.endDate.year" class="span2">
						<#list years?keys as key>
							<option value="${key}">${years[key]}</option>
						</#list>
		    		</select>
		    		</div>
		    	</div>
		    	<div class="control-group">
		 			<label class="relative"><@orcid.msg 'privacyToggle.help.who_can_see'/></label>
		 				<@orcid.privacyToggle "editAffiliation.visibility.visibility" "setAddAffiliationPrivacy('PUBLIC', $event)" 
		                    	  "setAddAffiliationPrivacy('LIMITED', $event)" "setAddAffiliationPrivacy('PRIVATE', $event)" />
		 		</div>
			</div>
		</div>
		<div class="row">
			<div class="span6">
			   &nbsp;
			</div>
			<div class="span2">
				<button class="btn btn-primary" ng-click="addAffiliation()" ng-disabled="addingAffiliation" ng-class="{disabled:addingAffiliation}"><@orcid.msg 'manual_affiliation_form_contents.btnaddtolist'/></button> 
				<a href="" ng-click="closeModal()"><@orcid.msg 'manage.deleteExternalIdentifier.cancel'/></a>
			</div>
			<div class="span4">
				<span ng-show="addingAffiliation">
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

<ul ng-hide="!affiliations.length" class="workspace-affiliations workspace-body-list bottom-margin-medium" ng-cloak>        
    <li class="bottom-margin-small" ng-repeat="affiliation in affiliations">            	
        <div class="pull-right" style="right: 145px; top: 20px; width: 15px;"><a href ng-click="deleteAffiliation($index)" class="icon-trash orcid-icon-trash grey"></a></div>
		<div style="width: 530px;">
		    <div ng-bind-html="affiliation.affiliationTypeForDisplay"></div>
	        <h3 class="affiliation-title">
	        	<strong ng-bind-html="affiliation.affiliationName.value"></strong>
	        	<span ng-show="affiliation.startDate">
	        	    (<span ng-show="affiliation.startDate.month">{{affiliation.startDate.month}}-</span><span ng-show="affiliation.startDate.year">{{affiliation.startDate.year}}</span>
	        	    <@orcid.msg 'workspace_affiliations.dateSeparator'/>
	        	    <span ng-show="affiliation.endDate">
	        	        <span ng-show="affiliation.endDate.month">{{affiliation.endDate.month}}-</span><span ng-show="affiliation.endDate.year">{{affiliation.endDate.year}}</span>)
	        	    </span>
	        	    <span ng-hide="affiliation.endDate">
	        	        <@orcid.msg 'workspace_affiliations.present'/>)
	        	    </span>
	        	</span>
	        </h3>
	        <div ng-show="affiliation.departmentName">
	            <span ng-bind-html="affiliation.departmentName.value"></span>
	        </div>
	        <div>
	            <span ng-bind-html="affiliation.city.value"></span><span ng-show="affiliation.region">, <span ng-bind-html="affiliation.region.value"></span></span>, <span ng-bind-html="affiliation.countryForDisplay"></span>
	        </div>
        </div>
        <div class="pull-right" style="width: 130px;">
		<@orcid.privacyToggle "affiliation.visibility.visibility" "setPrivacy($index, 'PUBLIC', $event)" 
                    	  "setPrivacy($index, 'LIMITED', $event)" "setPrivacy($index, 'PRIVATE', $event)" />
		</div>
    </li>           
</ul>
<div ng-show="numOfAffiliationsToAdd==null || (numOfAffiliationsToAdd > affiliations.length)" class="text-center">
    <i class="icon-spinner icon-4x icon-spin  green"></i>
</div>
<div ng-show="numOfAffiliationsToAdd==0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noaffilationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyaffiliations")} <a ng-click="addAffiliationModal()">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
</div>
    