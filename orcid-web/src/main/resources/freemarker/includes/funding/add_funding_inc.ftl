<script type="text/ng-template" id="add-funding-modal">
    <div id="add-funding" class="add-funding colorbox-content">
		<fn-form update-fn="putFunding()">
        <!-- Title -->
        <div class="row">
            <div class="col-md-9 col-sm-8 col-xs-9">
                <h1 class="lightbox-title pull-left">
                    <div ng-show="editFunding.putCode.value == null">
                        <@orcid.msg 'manual_funding_form_contents.add_grant'/>
                    </div>
                    <div ng-show="editFunding.putCode.value != null">
                        <@orcid.msg 'manual_funding_form_contents.edit_grant'/>
                    </div>
                </h1>
            </div>
        </div>

        <div class="row">
            <div class="col-md-6 col-sm-6 col-xs-12">
                <!-- Funding type -->
                <div class="form-group">
                    <label><@orcid.msg 'manual_funding_form_contents.grant_type'/></label>
                    <span class="required text-error" ng-class="isValidClass(editFunding.fundingType)">*</span>                    
                    <select id="fundingType" class="form-control" name="fundingType" ng-model="editFunding.fundingType.value" ng-change="serverValidate('fundings/funding/typeValidate.json'); typeChanged()">
                    	<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.FundingType.empty' /></option>
                        <#list fundingTypes?keys as key>
                            <option value="${key}">${fundingTypes[key]}</option>
                        </#list>
                    </select>
                    <span class="orcid-error" ng-show="editFunding.fundingType.errors.length > 0">
                        <div ng-repeat='error in editFunding.fundingType.errors' ng-bind-html="error"></div>
                    </span>
                </div>
                <!-- Funding subtype -->
                <div class="form-group">
                    <label><@orcid.msg 'manual_funding_form_contents.organization_defined_type.label'/></label>                    
                    <input id="organizationDefinedType" class="form-control" name="organizationDefinedTitle" type="text" ng-model="editFunding.organizationDefinedFundingSubType.subtype.value" placeholder="<@orcid.msg 'manual_funding_form_contents.organization_defined_type.placeholder'/>" ng-change="serverValidate('fundings/funding/organizationDefinedTypeValidate.json'); setSubTypeAsNotIndexed()" ng-model-onblur/>
                    <span class="orcid-error" ng-show="editFunding.organizationDefinedFundingSubType.subtype.errors.length > 0">
                    	<div ng-repeat='error in editFunding.organizationDefinedFundingSubType.subtype.errors' ng-bind-html="error"></div>
                    </span>                    
                </div>
				<!-- Title of funded project -->
                <div class="form-group">
                    <label><@orcid.msg 'manual_funding_form_contents.label_title'/></label>
					<span class="required" ng-class="isValidClass(editFunding.fundingTitle.title)">*</span>                    
                    <input id="fundingTitle" class="form-control" name="fundingTitle" type="text" ng-model="editFunding.fundingTitle.title.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_title'/>" ng-change="serverValidate('fundings/funding/titleValidate.json')" ng-model-onblur/>
                    <span class="orcid-error" ng-show="editFunding.fundingTitle.title.errors.length > 0">
                        <div ng-repeat='error in editFunding.fundingTitle.title.errors' ng-bind-html="error"></div>
                    </span>
                    <div class="add-item-link">
                        <span ng-hide="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_funding_form_contents.labelshowtranslatedtitle'/></a></span>
                        <span ng-show="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-minus-sign"></i> <@orcid.msg 'manual_funding_form_contents.labelhidetranslatedtitle'/></a></span>
                    </div>                    
                </div>

				<!-- Translated title -->
                <div id="translatedTitle">
                    <span class="orcid-error" ng-show="editFunding.fundingTitle.translatedTitle.errors.length > 0">
                        <div ng-repeat='error in editFunding.fundingTitle.translatedTitle.errors' ng-bind-html="error"></div>
                    </span>
                    <div class="form-group">
                        <label><@orcid.msg 'manual_funding_form_contents.label_translated_title'/></label>
                        <div class="relative">
                            <input name="translatedTitle" type="text" class="form-control" ng-model="editFunding.fundingTitle.translatedTitle.content" placeholder="<@orcid.msg 'manual_funding_form_contents.add_translated_title'/>" ng-change="serverValidate('fundings/funding/translatedTitleValidate.json')" ng-model-onblur/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="relative"><@orcid.msg 'manual_funding_form_contents.label_translated_title_language'/></label>
                        <div class="relative">
                            <select id="language" name="language" class="form-control" ng-model="editFunding.fundingTitle.translatedTitle.languageCode" ng-change="serverValidate('fundings/funding/translatedTitleValidate.json')">
                                <#list languages?keys as key>
                                    <option value="${languages[key]}">${key}</option>
                                </#list>
                            </select>
                        </div>
                    </div>
                </div>


                <div class="form-group">
                    <span>
                       <label><@orcid.msg 'manual_funding_form_contents.label_description'/></label>
                    </span>
                    <div class="relative">
                        <textarea id="fundingDescription" class="form-control" name="fundingDescription" type="text" ng-model="editFunding.description.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_description'/>" ng-change="serverValidate('fundings/funding/descriptionValidate.json')" ng-model-onblur/>
                        <span class="orcid-error" ng-show="editFunding.description.errors.length > 0">
                            <div ng-repeat='error in editFunding.description.errors' ng-bind-html="error"></div>
                        </span>
                    </div>
                </div>


                <div class="form-group">
                    <span>
                        <label><@orcid.msg 'manual_funding_form_contents.label_amount'/></label>
                    </span>
                    
                    <div class="funding-info">
						<select id="currencyCode" name="currencyCode" ng-model="editFunding.currencyCode.value" ng-change="serverValidate('fundings/funding/currencyValidate.json')">
                             <#list currencyCodeTypes?keys as key>
                                  <option value="${currencyCodeTypes[key]}">${key}</option>
                             </#list>
                        </select>
                        <input id="fundingAmount" name="fundingAmount" type="text" ng-model="editFunding.amount.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_amount'/>" ng-change="serverValidate('fundings/funding/amountValidate.json')" ng-class="form-control" ng-model-onblur/>
                    </div>

                    <span class="orcid-error" ng-show="editFunding.currencyCode.errors.length > 0">
                         <div ng-repeat='error in editFunding.currencyCode.errors' ng-bind-html="error"></div>
                    </span>
                    <span class="orcid-error" ng-show="editFunding.amount.errors.length > 0">
                         <div ng-repeat='error in editFunding.amount.errors' ng-bind-html="error"></div>
                    </span>	                    
                </div>

                <div class="form-group">
                    <label class="start-year"><@orcid.msg 'manual_funding_form_contents.labelStartDate'/></label>
					<div>                    
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
					</div>                    
                    <span class="orcid-error" ng-show="editFunding.startDate.errors.length > 0">
                        <div ng-repeat='error in editFunding.startDate.errors' ng-bind-html="error"></div>
                    </span>
                </div>
                
				<div class="form-group">
                    <label><@orcid.msg 'manual_funding_form_contents.labelEndDateLeave'/></label>
					<div>                    
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
					</div>                    
                    <span class="orcid-error" ng-show="editFunding.endDate.errors.length > 0">
                        <div ng-repeat='error in editFunding.endDate.errors' ng-bind-html="error"></div>
                    </span>
                </div>

                <div class="form-group" ng-repeat="contributor in editFunding.contributors">
                    <label><@orcid.msg 'manual_funding_form_contents.label_role'/></label>                    
                    <select id="role" name="role" ng-model="contributor.contributorRole.value" class="form-control">
                        <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.ContributorRole.empty' /></option>
                        <#list fundingRoles?keys as key>
                            <option value="${key}">${fundingRoles[key]}</option>
                        </#list>
                    </select>
                    <span class="orcid-error" ng-show="contributor.contributorRole.errors.length > 0">
                        <div ng-repeat='error in contributor.contributorRole.errors' ng-bind-html="error"></div>
                    </span>                    
                </div>
            </div>


            <div class="col-md-6 col-sm-6 col-xs-12">
                <div class="control-group no-margin-bottom">
                    <strong><@orcid.msg 'manual_funding_form_contents.title_funding_agency'/></strong>
                </div>
                <div class="control-group" ng-show="editFunding.disambiguatedFundingSourceId">
                    <label><@orcid.msg 'manual_funding_form_contents.label_funding_agency'/></label>
                    <span id="remove-disambiguated" class="pull-right">
                        <a ng-click="removeDisambiguatedFunding()">
                            <span class="glyphicon glyphicon-remove-sign"></span><@orcid.msg 'common.remove'/>
                        </a>
                    </span>
                    <div>
                        <span ng-bind="disambiguatedFunding.value"></span></strong>
                    </div>
                </div>
                <div class="form-group">
                    <span ng-hide="disambiguatedFunding">
                    	<label><@orcid.msg 'manual_funding_form_contents.label_funding_agency_name'/></label>						
                    </span>
                    <span ng-show="disambiguatedFunding">
                        <label><@orcid.msg 'manual_funding_form_contents.label_funding_agency_display_name'/></label>					
                    </span>
					<span class="required" ng-class="isValidClass(editFunding.fundingName)">*</span>                    
                    <input id="fundingName" class="form-control" name="fundingName" type="text" ng-model="editFunding.fundingName.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_name'/>" ng-change="serverValidate('fundings/funding/orgNameValidate.json')" ng-model-onblur/>
                    <span class="orcid-error" ng-show="editFunding.fundingName.errors.length > 0">
                        <div ng-repeat='error in editFunding.fundingName.errors' ng-bind-html="error"></div>
                    </span>                    
                </div>

                <div class="form-group">
                    <label ng-hide="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_city'/></label>
                    <label ng-show="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_display_city'/></label>
					<span class="required" ng-class="isValidClass(editFunding.city)">*</span>                    
                    <input id="city" name="city" type="text" class="form-control"  ng-model="editFunding.city.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_city'/>" ng-change="serverValidate('fundings/funding/cityValidate.json')" ng-model-onblur/>                        
                    <span class="orcid-error" ng-show="editFunding.city.errors.length > 0">
                        <div ng-repeat='error in editFunding.city.errors' ng-bind-html="error"></div>
                    </span>                    
                </div>

                <div class="form-group">
                    <label ng-hide="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_region'/></label>
                    <label ng-show="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_display_region'/></label>
                    <input name="region" type="text" class="form-control"  ng-model="editFunding.region.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_region'/>" ng-change="serverValidate('fundings/funding/regionValidate.json')" ng-model-onblur/>
                    <span class="orcid-error" ng-show="editFunding.region.errors.length > 0">
                         <div ng-repeat='error in editFunding.region.errors' ng-bind-html="error"></div>
                    </span>                    
                </div>

                <div class="form-group">
                    <label ng-hide="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_country'/></label>
                    <label ng-show="disambiguatedFunding"><@orcid.msg 'manual_funding_form_contents.label_display_country'/></label>
					<span class="required" ng-class="isValidClass(editFunding.country)">*</span>                    
                    <select id="country" class="form-control" name="country" ng-model="editFunding.country.value" ng-change="serverValidate('fundings/funding/countryValidate.json')">
                        <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
                        <#list isoCountries?keys as key>
                    	    <option value="${key}">${isoCountries[key]}</option>
                        </#list>
                    </select>                        
                    <span class="orcid-error" ng-show="editFunding.country.errors.length > 0">
                        <div ng-repeat='error in editFunding.country.errors' ng-bind-html="error"></div>
                    </span>                    
                </div>
                <div class="control-group no-margin-bottom">
                    <strong id="funding-ext-ids-title"><@orcid.msg 'manual_funding_form_contents.title_external_identifier'/></strong>
                </div>
                
				<div class="control-group" ng-repeat="externalIdentifier in editFunding.externalIdentifiers">
                    <!-- Value -->
                    <div class="form-group">
                        <label id="funding-ext-ids-value-label"><@orcid.msg 'manual_funding_form_contents.external_identifier.label_value'/></label>                        
                        <input name="currentFundingExternalIdentifierValue" id="funding-ext-ids-value-input" type="text" class="form-control" ng-model="externalIdentifier.externalIdentifierId.value" placeholder="<@orcid.msg 'manual_funding_form_contents.external_identifier.value'/>" ng-model-onblur/>
                        <span class="orcid-error" ng-show="externalIdentifier.externalIdentifierId.errors.length > 0">
                            <div ng-repeat='error in externalIdentifier.externalIdentifierId.errors' ng-bind-html="error"></div>
                        </span>
                    </div>
                    <!-- URL -->
					<div class="form-group">
						<label id="funding-ext-ids-url-label"><@orcid.msg 'manual_funding_form_contents.external_identifier.label_url'/></label>                        	
	                    <input name="currentFundingExternalIdentifierUrl" id="funding-ext-ids-url-input" type="text" class="form-control action-icon-inside" ng-model="externalIdentifier.url.value" placeholder="<@orcid.msg 'manual_funding_form_contents.external_identifier.url'/>" ng-model-onblur/>                        
						<span class="orcid-error" ng-show="externalIdentifier.url.errors.length > 0">
	                    	<div ng-repeat='error in externalIdentifier.url.errors' ng-bind-html="error"></div>
						</span>                        
					</div>
					<!-- Relationship -->
                    <div class="bottomBuffer">
                    	<label><@orcid.msg 'common.ext_id.relationship'/></label>
						<div class="relative">							
  							<label class="checkbox-inline">
    							<input type="radio" name="relationship{{$index}}" ng-model="externalIdentifier.relationship.value" value="self">
    							<@orcid.msg "common.self" />
  							</label>
																					
  							<label class="checkbox-inline">
    							<input type="radio" name="relationship{{$index}}" ng-model="externalIdentifier.relationship.value" value="part-of">
    							<@orcid.msg "common.part_of" />
  							</label>							
							<a href ng-click="deleteFundingExternalIdentifier(externalIdentifier)" class="glyphicon glyphicon-trash grey action-icon-align-right" ng-hide="$first"></a>							
						</div>
						<div ng-show="$last" class="add-item-link">
	                    	<span><a href ng-click="addFundingExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_funding_form_contents.external_identifier.add_another' /></a></span>
                        </div>
                    </div>
                </div>

                <div class="form-group">                    
                    <label><@orcid.msg 'manual_funding_form_contents.label_url'/></label>                                        
                    <input id="fundingUrl" class="form-control" name="fundingUrl" type="text" ng-model="editFunding.url.value" placeholder="<@orcid.msg 'manual_funding_form_contents.add_url'/>" ng-change="serverValidate('fundings/funding/urlValidate.json')" ng-model-onblur/>
                    <span class="orcid-error" ng-show="editFunding.url.errors.length > 0">
                        <div ng-repeat='error in editFunding.url.errors' ng-bind-html="error"></div>
                    </span>                    
                </div>
                
				<div class="control-group">
					

					<div class="control-group" ng-if="editFunding.putCode.value != null">
                    	<ul class="inline-list margin-separator pull-left">
							<li>
								<button class="btn btn-primary" ng-click="putFunding()" ng-disabled="addingFunding" ng-class="{disabled:addingFunding}">
                            		<@orcid.msg 'freemarker.btnsave'/>
                        		</button>
							</li>
							<li>								
								<a class="cancel-option" ng-click="closeModal()" ><@orcid.msg 'freemarker.btncancel'/></a>
							</li>
							<li>
								<span ng-show="addingFunding">
                        			<i class="glyphicon glyphicon-refresh spin x2 green"></i>
                    			</span>
							</li>
						</ul>
					</div>

                    <div class="control-group" ng-if="editFunding.putCode.value == null">	
						<ul class="inline-list margin-separator pull-left">
							<li>
								<button id="save-funding" class="btn btn-primary" ng-click="putFunding()" ng-disabled="addingFunding" ng-class="{disabled:addingFunding}">
	                            	<@orcid.msg 'manual_funding_form_contents.btnaddtolist'/>
                        		</button>
							</li>
							<li>
								<a class="cancel-option" ng-click="closeModal()" ><@orcid.msg 'freemarker.btncancel'/></a>
							</li>
							<li>
								<span ng-show="addingFunding">
                        			<i class="glyphicon glyphicon-refresh spin x2 green"></i>
                    			</span>
							</li>
						</ul>
                    </div>

					<div class="control-group errors">
                    	<span ng-show="editFunding.errors.length > 0" class="alert"><@orcid.msg 'common.please_fix_errors' /></span>
                	</div>

                </div>
            </div>
        </div>
		</fn-form>
    </div>
</script>
