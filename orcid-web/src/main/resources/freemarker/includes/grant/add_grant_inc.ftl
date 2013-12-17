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
	<div id="edit-affiliation" class="edit-grant colorbox-content">		 
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
					   <label ng-hide="disambiguatedGrant"><@orcid.msg 'manual_affiliation_form_contents.labelinstitution'/></label>
					   <label ng-show="disambiguatedGrant"><@orcid.msg 'manual_affiliation_form_contents.labeldisplayinstitution'/></label>
					</span>
					<div class="relative">
						<input id="grantName" class="input-xlarge" name="grantName" type="text" ng-model="editGrant.grantName.value" placeholder="<@orcid.msg 'manual_grant_form_contents.add_name'/>" ng-change="serverValidate('grants/grant/nameValidate.json')" ng-model-onblur/>
						<span class="required" ng-class="isValidClass(editGrant.grantName)">*</span>
						<span class="orcid-error" ng-show="editGrant.grantName.errors.length > 0">
							<div ng-repeat='error in editGrant.grantName.errors' ng-bind-html-unsafe="error"></div>
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
						<label><@orcid.msg 'manual_grant_form_contents.grant_type'/></label>
					</span>
					<div class="relative">						
						<select id="grantType" name="grantType" ng-model="editGrant.grantType.value" ng-change="serverValidate('grants/grant/typeValidate.json')">			
							<#list grantTypes?keys as key>
								<option value="${grantTypes[key]}">${key}</option>
							</#list>
						</select>				
					</div>
				</div>	
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
			</div>




			<div class="col-md-6 col-sm-6 col-xs-12">
				<div class="control-group">
					<span>
						<label><@orcid.msg 'manual_grant_form_contents.amount'/></label>
					</span>
					<div class="relative">						
						<div>
							<select id="currencyCode" name="currencyCode" ng-model="editGrant.currencyCode.value" ng-change="serverValidate('grants/grant/currencyValidate.json')">			
								<#list currencyCodeTypes?keys as key>
									<option value="${currencyCodeTypes[key]}">${key}</option>
								</#list>
							</select>	
							<input id="grantAmount" class="input-xlarge" name="grantAmount" type="text" ng-model="editGrant.amount.value" placeholder="<@orcid.msg 'manual_grant_form_contents.add_amount'/>" ng-change="serverValidate('grants/grant/amountValidate.json')" ng-model-onblur/>
						</div>
						<span class="orcid-error" ng-show="editGrant.currencyCode.errors.length > 0">
							<div ng-repeat='error in editGrant.currencyCode.errors' ng-bind-html-unsafe="error"></div>
						</span>
						<span class="orcid-error" ng-show="editGrant.amount.errors.length > 0">
							<div ng-repeat='error in editGrant.amount.errors' ng-bind-html-unsafe="error"></div>
						</span>			
					</div>
				</div>	
				
				
				
				
				
				
		    	<div class="control-group">
					<button class="btn btn-primary" ng-click="addAffiliation()" ng-disabled="addingAffiliation" ng-class="{disabled:addingAffiliation}"><@orcid.msg 'manual_affiliation_form_contents.btnaddtolist'/></button> 
					<a href="" ng-click="closeModal()"><@orcid.msg 'manage.deleteExternalIdentifier.cancel'/></a>
					<span ng-show="addingAffiliation">
						<i class="glyphicon glyphicon-refresh spin x2 green"></i>
					</span>
					<span ng-show="editWork.errors.length > 0" class="alert">Please fix above errors</span>
				</div>
			</div>
	</div>
</script>
