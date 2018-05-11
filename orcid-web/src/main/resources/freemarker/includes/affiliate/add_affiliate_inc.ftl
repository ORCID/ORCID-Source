<script type="text/ng-template" id="add-affiliation-modal">
    <div id="edit-affiliation" class="edit-affiliation colorbox-content">
		<fn-form update-fn="addAffiliation()">
       
		<div class="row">
            <div class="col-md-9 col-sm-8 col-xs-12">
                <h1 ng-show="addAffType == null || addAffType == undefined " class="lightbox-title pull-left">
                    <span ng-show="editAffiliation.putCode.value == null"><@orcid.msg 'manual_affiliation_form_contents.add_affiliation'/></span>
                    <span ng-show="editAffiliation.putCode.value != null"><@orcid.msg 'manual_affiliation_form_contents.edit_affiliation'/></span>
                </h1>
                <h1 ng-show="addAffType == 'education'" class="lightbox-title pull-left">
                    <span ng-show="editAffiliation.putCode.value == null"><@orcid.msg 'manual_affiliation_form_contents.add_education'/></span>
                    <span ng-show="editAffiliation.putCode.value != null"><@orcid.msg 'manual_affiliation_form_contents.edit_education'/></span>
                </h1>
                <h1 ng-show="addAffType == 'employment'" class="lightbox-title pull-left">
                    <span ng-show="editAffiliation.putCode.value == null"><@orcid.msg 'manual_affiliation_form_contents.add_employment'/></span>
                    <span ng-show="editAffiliation.putCode.value != null"><@orcid.msg 'manual_affiliation_form_contents.edit_employment'/></span>
                </h1>
            </div>
        </div>

        <div class="row">
			

			<!-- Left Column -->
            <div class="col-md-6 col-sm-6 col-xs-12">
				<div class="form-group" ng-show="editAffiliation.disambiguatedAffiliationSourceId">
                    <span ng-show="addAffType == 'education'">
                       <label><@orcid.msg 'manual_affiliation_form_contents.labelinstitution'/></label>
                    </span>
                    <span ng-show="addAffType == 'employment'">
                       <label><@orcid.msg 'manual_affiliation_form_contents.labelinstitutionemployer'/></label>
                    </span>
                    <span id="remove-disambiguated" class="pull-right">
                        <a ng-click="removeDisambiguatedAffiliation()">
                            <span class="glyphicon glyphicon-remove-sign"></span><@orcid.msg 'common.remove'/>
                        </a>
                    </span>

                    <div class="relative" style="font-weight: strong;">
                        <span ng-bind="disambiguatedAffiliation.value"></span> <br />
                        <div>
                            <span ng-bind="disambiguatedAffiliation.city"></span><span ng-show="disambiguatedAffiliation.region"> (<span ng-bind="disambiguatedAffiliation.region"></span>)</span><span ng-show="disambiguatedAffiliation.orgType">, <span ng-bind="disambiguatedAffiliation.orgType"></span></span>
                        </div>
                    </div>
                </div>

				<!-- Institution -->
                <div class="form-group">
                    <span ng-show="addAffType == 'education'">
                       <label ng-hide="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labelinstitution'/></label>
                       <label ng-show="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labeldisplayinstitution'/></label>
						<span class="required" ng-class="isValidClass(editAffiliation.affiliationName)">*</span>
                    </span>
                    <span ng-show="addAffType == 'employment'">
                       <label ng-hide="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labelinstitutionemployer'/></label>
                       <label ng-show="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labeldisplayinstitutionemployer'/></label>
					   <span class="required" ng-class="isValidClass(editAffiliation.affiliationName)">*</span>
                    </span>
                    <div>							
                        <input id="affiliationName" class="form-control" name="affiliationName" type="text" ng-model="editAffiliation.affiliationName.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_name'/>" ng-change="serverValidate('affiliations/affiliation/affiliationNameValidate.json')" ng-model-onblur/>
                        <span class="orcid-error" ng-show="editAffiliation.affiliationName.errors.length > 0">
                            <div ng-repeat='error in editAffiliation.affiliationName.errors' ng-bind-html="error"></div>
                        </span>
                    </div>
                </div>

				<!-- City -->
                <div class="form-group">
                    <label ng-hide="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labelcity'/></label>
                    <label ng-show="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labeldisplaycity'/></label>
					<span class="required" ng-class="isValidClass(editAffiliation.city)">*</span>
                    <div>
                        <input id="city" name="city" class="form-control" type="text" ng-model="editAffiliation.city.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_city'/>" ng-change="serverValidate('affiliations/affiliation/cityValidate.json')" ng-model-onblur/>                        
                        <span class="orcid-error" ng-show="editAffiliation.city.errors.length > 0">
                            <div ng-repeat='error in editAffiliation.city.errors' ng-bind-html="error"></div>
                        </span>
                    </div>
                </div>

				<!-- State/Region -->
                <div class="form-group">
                    <label ng-hide="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labelregion'/></label>
                    <label ng-show="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labeldisplayregion'/></label>
                    <div>
                        <input name="region" type="text" class="form-control"  ng-model="editAffiliation.region.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_region'/>" ng-change="serverValidate('affiliations/affiliation/regionValidate.json')" ng-model-onblur/>
                        <span class="orcid-error" ng-show="editAffiliation.region.errors.length > 0">
                            <div ng-repeat='error in editAffiliation.region.errors' ng-bind-html="error"></div>
                        </span>
                    </div>
                </div>

				<!-- Country -->
                <div class="form-group">
                    <label ng-hide="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labelcountry'/></label>
                    <label ng-show="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labeldisplaycountry'/></label>
 					<span class="required" ng-class="isValidClass(editAffiliation.country)">*</span>
                    <div>
                        <select id="country" name="country" ng-model="editAffiliation.country.value" ng-change="serverValidate('affiliations/affiliation/countryValidate.json')" class="form-control">
                            <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
                            <#list isoCountries?keys as key>
                                	<option value="${key}">${isoCountries[key]}</option>
                            </#list>
                        </select>
                       
                        <span class="orcid-error" ng-show="editAffiliation.country.errors.length > 0">
                            <div ng-repeat='error in editAffiliation.country.errors' ng-bind-html="error"></div>
                        </span>
                    </div>
                </div>
            </div>
            

			<!-- Right Column -->
			<div class="col-md-6 col-sm-6 col-xs-12">
				<!-- Department -->
                <div class="form-group">
                    <label><@orcid.msg 'manual_affiliation_form_contents.labeldepartment'/></label>
                    <div>
                        <input id="departmentName" class="form-control" name="departmentName" type="text" ng-model="editAffiliation.departmentName.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_department'/>" ng-change="serverValidate('affiliations/affiliation/departmentValidate.json')" ng-model-onblur/>
                        <span class="orcid-error" ng-show="editAffiliation.departmentName.errors.length > 0">
                            <div ng-repeat='error in editAffiliation.departmentName.errors' ng-bind-html="error"></div>
                        </span>
                    </div>
                </div>
				<!-- Degree/Title -->
                <div class="form-group">
                    <label ng-show="addAffType != 'education'"><@orcid.msg 'manual_affiliation_form_contents.labelroletitle'/></label>
                    <label ng-show="addAffType == 'education'"><@orcid.msg 'manual_affiliation_form_contents.labeldegreetitle'/></label>
                    <div>
                        <input name="roletitle" type="text" class="form-control"  ng-model="editAffiliation.roleTitle.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_title'/>" ng-change="serverValidate('affiliations/affiliation/roleTitleValidate.json')" ng-model-onblur/>
                        <span class="orcid-error" ng-show="editAffiliation.roleTitle.errors.length > 0">
                            <div ng-repeat='error in editAffiliation.roleTitle.errors' ng-bind-html="error"></div>
                        </span>
                    </div>
                </div>
                <!-- URL -->
                <div class="form-group">
                    <label><@orcid.msg 'manual_affiliation_form_contents.url'/></label>
                    <div>
                        <input name="url" type="text" class="form-control"  ng-model="editAffiliation.url.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_url'/>" ng-change="serverValidate('affiliations/affiliation/urlValidate.json')" ng-model-onblur/>
                        <span class="orcid-error" ng-show="editAffiliation.url.errors.length > 0">
                            <div ng-repeat='error in editAffiliation.url.errors' ng-bind-html="error"></div>
                        </span>
                    </div>
                </div>
				<!--  -->
                <div class="form-group">
                    <label class="relative" for="manualAffiliation.startDay"><@orcid.msg 'manual_affiliation_form_contents.labelStartDate'/></label>
					<span class="required" ng-class="isValidStartDate(editAffiliation.startDate)">*</span>
					<div>					
                    	<select id="startYear" name="startMonth" ng-model="editAffiliation.startDate.year" ng-change="serverValidate('affiliations/affiliation/datesValidate.json')">
		                	<#list years?keys as key>
                        		<option value="${key}">${years[key]}</option>
                        	</#list>
                    	</select>					
                    	<select id="startMonth" name="startMonth" ng-model="editAffiliation.startDate.month" ng-change="serverValidate('affiliations/affiliation/datesValidate.json')">
	                        <#list months?keys as key>
                           		<option value="${key}">${months[key]}</option>
                        	</#list>
                    	</select>					
                    	<select id="startDay" name="startDay" ng-model="editAffiliation.startDate.day" ng-change="serverValidate('affiliations/affiliation/datesValidate.json')">
	                        <!--
                            <#list days?keys as key>
                           		<option value="${key}">${days[key]}</option>
                      		</#list>
                            -->
                            <#list days?keys as key>
                                <option disabled="checkAvailableDays(
                                '${days[key]}', editAffiliation.endDate.month, editAffiliation.endDate.year)" value="${key}">${days[key]}</option>
                            </#list>
                    	</select>
					</div>
					<span class="orcid-error" ng-show="editAffiliation.startDate.errors.length > 0">
						<div ng-repeat='error in editAffiliation.startDate.errors' ng-bind-html="error"></div>
					</span>			
                </div>

                <div class="control-group">
                    <label class="relative" for="manualAffiliation.endDay"><@orcid.msg 'manual_affiliation_form_contents.labelEndDateLeave'/></label>
                    <div class="relative">
                        <select id="endYear" name="endMonth" ng-model="editAffiliation.endDate.year" ng-change="serverValidate('affiliations/affiliation/datesValidate.json')">
                            <#list years?keys as key>
                                <option value="${key}">${years[key]}</option>
                            </#list>
                        </select>
                        <select id="endMonth" name="endMonth" ng-model="editAffiliation.endDate.month" ng-change="serverValidate('affiliations/affiliation/datesValidate.json')">
                            <#list months?keys as key>
                                <option value="${key}">${months[key]}</option>
                            </#list>
                        </select>
                        <select id="endDay" name="endDay" ng-model="editAffiliation.endDate.day" ng-change="serverValidate('affiliations/affiliation/datesValidate.json')">
                            <#list days?keys as key>
                                <option disabled="checkAvailableDays(
                                '${days[key]}', editAffiliation.endDate.month, editAffiliation.endDate.year)" value="${key}">${days[key]}</option>
                            </#list>
                        </select>
                    </div>
                    <span class="orcid-error" ng-show="editAffiliation.endDate.errors.length > 0">
                        <div ng-repeat='error in editAffiliation.endDate.errors' ng-bind-html="error"></div>
                    </span>
                </div>                
                <div class="control-group">
					<ul class="inline-list margin-separator pull-left">
						<li>
							<button id="save-affiliation" class="btn btn-primary" ng-click="addAffiliation()" ng-disabled="addingAffiliation" ng-class="{disabled:addingAffiliation}">
                        		<span ng-show="editAffiliation.putCode.value == null"><@orcid.msg 'manual_affiliation_form_contents.btnaddtolist'/></span>
                        		<span ng-show="editAffiliation.putCode.value != null"><@orcid.msg 'manual_affiliation_form_contents.btnedit'/></span>
                    		</button>
						</li>						
						<li>		
							<!-- <button id="" class="btn close-button" ng-click="closeModal()" type="reset"><@orcid.msg 'freemarker.btncancel' /></button>-->					
							<a class="cancel-option" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>					
						</li>
						<li>
							<span ng-if="addingAffiliation">
                        		<i class="glyphicon glyphicon-refresh spin x2 green"></i>
                    		</span>
						</li>
					</ul>
                </div>
				<div class="control-group errors">
                    <span ng-show="editAffiliation.errors.length > 0" class="alert"><@orcid.msg 'common.please_fix_errors' /></span>                    
                </div>
			</fn-form>
        </div>
</script>
