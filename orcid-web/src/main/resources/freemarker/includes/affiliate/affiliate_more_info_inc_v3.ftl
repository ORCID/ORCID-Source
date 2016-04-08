<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<div class="more-info content">
	<div class="row bottomBuffer">
		<div class="col-md-12"></div>
	</div>	
	<div class="row">
		<div class="col-md-6" ng-if="group.getActive().affiliationName.value" ng-cloak>
			
				<div class="bottomBuffer">
					  <strong ng-if="group.getActive().affiliationType.value == 'education'"><@orcid.msg 'manual_affiliation_form_contents.labelinstitution'/></strong>  
					<strong ng-if="group.getActive().affiliationType.value == 'employment'"><@orcid.msg 'manual_affiliation_form_contents.labelinstitutionemployer'/></strong>
					<strong ng-if="group.getActive().affiliationType.value != 'education' && group.getActive().affiliationType.value != 'employment'"><@orcid.msg 'manual_affiliation_form_contents.labelname'/></strong>
					<div ng-bind="group.getActive().affiliationName.value"></div>
				</div>
		</div>	
		
		<div class="col-md-6" ng-if="group.getActive().city.value" ng-cloak>
			<div class="bottomBuffer">
				<strong><@orcid.msg 'manual_affiliation_form_contents.labelcity'/></strong>
				<div ng-bind="group.getActive().city.value"></div>
			</div>
		</div>
		
		<div class="col-md-6" ng-if="group.getActive().region.value" ng-cloak>
			<div class="bottomBuffer">
				<strong><@orcid.msg 'manual_affiliation_form_contents.labelregion'/></strong>
				<div ng-bind="group.getActive().region.value"></div>
			</div>
		</div>
		
		<div class="col-md-6"  ng-if="group.getActive().country.value" ng-cloak>
			<div class="bottomBuffer">
				<strong><@orcid.msg 'manual_affiliation_form_contents.labelcountry'/></strong>
				<div ng-bind="group.getActive().country.value"></div>
			</div>
		</div>
		<div class="col-md-6" ng-if="group.getActive().departmentName.value" ng-cloak>
			<div class="bottomBuffer">
				<strong><@orcid.msg 'manual_affiliation_form_contents.labeldepartment'/></strong>
				<div ng-bind="group.getActive().departmentName.value"></div>
			</div>
		</div>
		
		<div class="col-md-6"  ng-if="group.getActive().roleTitle.value" ng-cloak>
			<div class="bottomBuffer">
				<strong ng-if="group.getActive().affiliationType.value == 'education'"><@orcid.msg 'manual_affiliation_form_contents.labeldegreetitle'/></strong>
				<strong ng-if="group.getActive().affiliationType.value != 'education'"><@orcid.msg 'manual_affiliation_form_contents.labelroletitle'/></strong>
				<div ng-bind="group.getActive().roleTitle.value"></div>
			</div>
		</div>
		
		
		<div class="col-md-6" ng-if="group.getActive().affiliationType.value" ng-cloak>
			<div class="bottomBuffer">
				<strong><@orcid.msg 'manual_affiliation_form_contents.labelaffiliationtype'/></strong>
				<div ng-bind="group.getActive().affiliationType.value"></div>
			</div>
		</div>
		
		<div class="col-md-6" ng-if="group.getActive().startDate.year" ng-cloak>
			<div class="bottomBuffer">				
				<strong><@orcid.msg 'manual_affiliation_form_contents.labelStartDate'/></strong>
				<div>		
					<span ng-if="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span ng-if="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span><span ng-if="group.getActive().startDate.day && group.getActive().startDate.month">-{{group.getActive().startDate.day}}</span>
				</div>				
			</div>
		</div>
		
		<div class="col-md-6" ng-if="group.getActive().endDate.year" ng-cloak>
			<div class="bottomBuffer">				
				<strong><@orcid.msg 'manual_affiliation_form_contents.labelEndDate'/></strong>
				<div>									
					<span ng-if="group.getActive().endDate.year" ng-bind="group.getActive().endDate.year"></span><span ng-if="group.getActive().endDate.month">-</span><span ng-if="group.getActive().endDate.month" ng-bind="group.getActive().endDate.month"></span><span ng-if="group.getActive().endDate.day && group.getActive().endDate.month">-</span><span ng-if="group.getActive().endDate.day && group.getActive().endDate.month" ng-bind="group.getActive().endDate.day"></span>
				</div>				
			</div>
		</div>
				
	</div>		
</div>
 