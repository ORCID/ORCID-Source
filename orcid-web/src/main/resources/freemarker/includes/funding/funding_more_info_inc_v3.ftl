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
<div class="more-info" ng-show="moreInfo[group.getActive().putCode.value]">
	<div class="row"> 
		
		<div class="col-md-6" ng-show="group.getActive().fundingType.value" ng-cloak>			
			<div class="bottomBuffer">					
				<strong><@orcid.msg 'manual_funding_form_contents.grant_type'/></strong>
				<div ng-bind="group.getActive().fundingTypeForDisplay"></div>
			</div>
		</div>

		<div class="col-md-6" ng-show="group.getActive().organizationDefinedFundingSubType.subtype.value" ng-cloak>
			<div class="bottomBuffer">					
				<strong><@orcid.msg 'manual_funding_form_contents.organization_defined_type.label'/></strong>
				<div ng-bind="group.getActive().organizationDefinedFundingSubType.subtype.value"></div>
			</div>		
		</div>
		
		<div class="col-md-6" ng-show="group.getActive().fundingTitle.title.value" ng-cloak>
			<div class="bottomBuffer">
				<strong><@orcid.msg 'manual_funding_form_contents.label_title'/></strong>
				<div ng-bind="group.getActive().fundingTitle.title.value"></div>
			</div>
		</div>
		
		<div class="col-md-6" ng-show="group.getActive().fundingTitle.translatedTitle.content" ng-cloak>
			<div class="bottomBuffer">				
				<strong><@orcid.msg
					'manual_funding_form_contents.label_translated_title'/></strong>
				<div ng-bind="renderTranslatedTitleInfo(funding)"></div>					
			</div>		
		</div>
		
		<div class="col-md-6" ng-show="group.getActive().amount.value" ng-cloak>
			<div class="bottomBuffer">				
				<strong><@orcid.msg 'manual_funding_form_contents.label_amount'/></strong>
				<div>{{group.getActive().currencyCode.value}} {{group.getActive().amount.value}}</div>				
			</div>
		</div>
		<div class="col-md-6" ng-show="group.getActive().startDate.year" ng-cloak>
			<div class="bottomBuffer">				
				<strong><@orcid.msg 'manual_funding_form_contents.labelStartDate'/></strong>
				<div>
					<span ng-show="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span ng-show="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span><span ng-show="group.getActive().startDate.day && group.getActive().startDate.month">-{{group.getActive().startDate.day}}</span>						
				</div>				
			</div>
		</div>
		<div class="col-md-6" ng-show="group.getActive().endDate.year" ng-cloak>
			<div class="bottomBuffer">				
				<strong><@orcid.msg 'manual_funding_form_contents.labelEndDate'/></strong>
				<div>
					<span ng-show="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span ng-show="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span><span ng-show="group.getActive().endDate.day && group.getActive().endDate.month">-{{group.getActive().endDate.day}}</span>						
				</div>				
			</div>
		</div>
		<div class="col-md-6" ng-show="group.getActive().contributors.length > 0" ng-cloak>
			<div class="bottomBuffer">
				<strong><@orcid.msg 'manual_funding_form_contents.label_contributors'/></strong>
				<div ng-repeat="contributor in group.getActive().contributors">
					{{contributor.creditName.value}} <span
						ng-bind='contributor | contributorFilter'></span>
				</div>		
			</div>
		</div>
		<div class="col-md-6" ng-show="group.getActive().description.value" ng-cloak>
			<div class="bottomBuffer">				
				<strong><@orcid.msg 'manual_funding_form_contents.label_description'/></strong>
				<div ng-bind="group.getActive().description.value"></div>				
			</div>
		</div>
		<div class="col-md-6"  ng-show="group.getActive().fundingName.value" ng-cloak>
			<div class="bottomBuffer">									
				<strong><@orcid.msg 'manual_funding_form_contents.label_funding_agency_name'/></strong>
				<div ng-bind="group.getActive().fundingName.value"></div>				
			</div>
		</div>
		<div class="col-md-6" ng-show="group.getActive().city.value" ng-cloak>
			<div class="bottomBuffer">				
				<strong><@orcid.msg 'manual_funding_form_contents.label_city'/></strong>
				<div ng-bind="group.getActive().city.value"></div>
			</div>
		</div>
		<div class="col-md-6" ng-show="group.getActive().region.value" ng-cloak>
			<div class="bottomBuffer">				
				<strong><@orcid.msg 'manual_funding_form_contents.label_region'/></strong>
				<div ng-bind="group.getActive().region.value"></div>				
			</div>
		</div>	
		<div class="col-md-6"  ng-show="group.getActive().country.value" ng-cloak>
			<div class="bottomBuffer">				
				<strong><@orcid.msg 'manual_funding_form_contents.label_country'/></strong>
				<div ng-bind="group.getActive().countryForDisplay"></div>				
			</div>
		</div>
		
		<div class="col-md-6" ng-show="group.getActive().url.value" ng-cloak>
			<div class="bottomBuffer">				
				<strong><@orcid.msg 'manual_funding_form_contents.label_url'/></strong>
				<div ng-bind="group.getActive().url.value"></div>				
			</div>
		</div>
	</div>
	<div class="row">									
		<div class="col-md-12">
			<div class="bottomBuffer" ng-show="group.getActive().sourceName" ng-cloak>				
				<strong><@orcid.msg 'manual_funding_form_contents.label_source'/></strong>
				<div ng-bind="group.getActive().sourceName"></div>						
			</div>
		</div>
	</div>
</div>
