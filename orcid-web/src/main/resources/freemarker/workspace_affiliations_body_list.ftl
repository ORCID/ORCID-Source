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
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3>${springMacroRequestContext.getMessage("manage.deleteAffiliation.pleaseConfirm")}</h3>
				<p>{{fixedTitle}}</p>		
    			<div class="btn btn-danger" ng-click="deleteByPutCode()">
    				${springMacroRequestContext.getMessage("manage.deleteAffiliation.delete")}
    			</div>
    			<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteAffiliation.cancel")}</a>
			</div>
		</div>
    </div>
</script>

<script type="text/ng-template" id="add-affiliation-modal">
	<div id="edit-affiliation" class="edit-affiliation colorbox-content">
		<div class="row">
			<div class="col-md-11 col-sm-10 col-xs-10">
				<h1 class="lightbox-title pull-left"><@orcid.msg 'manual_affiliation_form_contents.add_affiliation'/></h1>				
			</div>
			<div class="col-md-1 col-sm-2 col-xs-2">
				<a class="btn pull-right close-button" ng-click="closeModal()">X</a>
			</div>
		</div>

		<div class="row">
			<div class="col-md-6 col-sm-6 col-xs-12">
				<div class="control-group" ng-show="editAffiliation.disambiguatedAffiliationIdentifier">
					<div style="position: absolute; left: 300px"><label><a class="icon-remove-sign grey" ng-click="removeDisambiguatedAffiliation()">&nbsp;<@orcid.msg 'common.remove'/></a></label></div>
					<label>Affiliation</label>
				    <div class="relative"  style="font-weight: strong;">
						<span ng-bind="disambiguatedAffiliation.value"></span> <br />
						<div>
						    <span ng-bind="disambiguatedAffiliation.city"></span><span ng-bind="disambiguatedAffiliation.region"></span>,<span ng-bind="disambiguatedAffiliation.orgType"></span>
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
					<i class="icon-spinner icon-2x icon-spin  green"></i>
				</span>				
			</div>			
		</div>		
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
				<span ng-show="editWork.errors.length > 0" class="alert">Please fix above errors</span>
			<div>
		<div>
	<div>
</script>

<ul ng-hide="!affiliations.length" class="workspace-affiliations workspace-body-list bottom-margin-medium" ng-cloak>        
    <li class="bottom-margin-small" ng-repeat="affiliation in affiliations | orderBy:['-startDate.year', '-startDate.month', '-startDate.day', '-endDate.year', '-endDate.month', '-endDate.day', 'affiliationName.value']">            	
        <div class="row">        
        	<!-- Information -->
			<div class="col-md-8 col-sm-8">
			    <div class="affiliation-type" ng-bind-html="affiliation.affiliationTypeForDisplay"></div>
		        <h3 class="affiliation-title">
		        	<strong ng-bind-html="affiliation.affiliationName.value"></strong>
		        	<span class="affiliation-date" ng-show="affiliation.startDate">
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
		        <div class="affiliation-details" ng-show="affiliation.roleTitle">
		            <span ng-bind-html="affiliation.roleTitle.value"></span>
		        </div>
		        <div ng-show="affiliation.sourceName">
	            	<span class="affiliation-source">SOURCE: <span ng-bind-html="affiliation.sourceName"></span></span>
	        	</div>
	        </div>
	        <!-- Privacy Settings -->
	        <div class="col-md-4 col-sm-4 workspace-toolbar">
	        	<ul class="workspace-private-toolbar">
	        		<li>	
			        	<a href ng-click="deleteAffiliation(affiliation.putCode.value)" class="glyphicon glyphicon-trash grey"></a>
			        </li>
			        <li>
						<@orcid.privacyToggle "affiliation.visibility.visibility" "setPrivacy(affiliation.putCode.value, 'PUBLIC', $event)" 
                    	  "setPrivacy(affiliation.putCode.value, 'LIMITED', $event)" "setPrivacy(affiliation.putCode.value, 'PRIVATE', $event)" />
			        </li>
		        </ul>
			</div>
		</div>
    </li>           
</ul>
<div ng-show="numOfAffiliationsToAdd==null || (numOfAffiliationsToAdd > affiliations.length)" class="text-center">
    <i class="icon-spinner icon-4x icon-spin  green"></i>
</div>
<div ng-show="numOfAffiliationsToAdd==0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noaffilationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyaffiliations")} <a ng-click="addAffiliationModal()">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
</div>
    