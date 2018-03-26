<div class="more-info" ng-mouseleave="closeMoreInfo(group.getActive().putCode.value)" ng-class="{'more-info-show':moreInfo[group.getActive().putCode.value]==true}">
	<a class="glyphicon glyphicon-plus-sign grey" ng-mouseenter="moreInfoMouseEnter(group.getActive().putCode.value,$event);" ng-click="toggleClickMoreInfo(group.getActive().putCode.value)"></a>	
	<div class="popover bottom more-info-container">
		<div class="arrow"></div>	
		<div class="lightbox-container">
			<div class="ie7fix">
				<div class="row bottomBuffer"></div>
				<div class="row bottomBuffer" ng-show="group.getActive().affiliationName.value"
					ng-cloak>
					<div class="col-md-8">
						<strong ng-show="group.getActive().affiliationType.value == 'education'"><@orcid.msg 'manual_affiliation_form_contents.labelinstitution'/></strong>
						<strong ng-show="group.getActive().affiliationType.value == 'employment'"><@orcid.msg 'manual_affiliation_form_contents.labelinstitutionemployer'/></strong>
						<strong ng-show="group.getActive().affiliationType.value != 'education' && group.getActive().affiliationType.value != 'employment'"><@orcid.msg 'manual_affiliation_form_contents.labelname'/></strong>
						<div ng-bind="group.getActive().affiliationName.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="group.getActive().city.value"
					ng-cloak>
					<div class="col-md-8">
						<strong><@orcid.msg 'manual_affiliation_form_contents.labelcity'/></strong>
						<div ng-bind="group.getActive().city.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="group.getActive().region.value"
					ng-cloak>
					<div class="col-md-8">
						<strong><@orcid.msg 'manual_affiliation_form_contents.labelregion'/></strong>
						<div ng-bind="group.getActive().region.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="group.getActive().country.value"
					ng-cloak>
					<div class="col-md-8">
						<strong><@orcid.msg 'manual_affiliation_form_contents.labelcountry'/></strong>
						<div ng-bind="group.getActive().country.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="group.getActive().departmentName.value"
					ng-cloak>
					<div class="col-md-8">
						<strong><@orcid.msg 'manual_affiliation_form_contents.labeldepartment'/></strong>
						<div ng-bind="group.getActive().departmentName.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="group.getActive().roleTitle.value"
					ng-cloak>
					<div class="col-md-8">
						<strong ng-show="group.getActive().affiliationType.value == 'education'"><@orcid.msg 'manual_affiliation_form_contents.labeldegreetitle'/></strong>
						<strong ng-show="group.getActive().affiliationType.value != 'education'"><@orcid.msg 'manual_affiliation_form_contents.labelroletitle'/></strong>
						<div ng-bind="group.getActive().roleTitle.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="group.getActive().affiliationType.value"
					ng-cloak>
					<div class="col-md-8">
						<strong><@orcid.msg 'manual_affiliation_form_contents.labelaffiliationtype'/></strong>
						<div ng-bind="group.getActive().affiliationType.value"></div>
					</div>
				</div>			
				<div class="row bottomBuffer" ng-show="group.getActive().startDate.year" ng-cloak>
					<div class="col-md-8">
						<strong><@orcid.msg 'manual_affiliation_form_contents.labelStartDate'/></strong>
						<div>
							<span ng-show="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span ng-show="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span><span ng-show="group.getActive().startDate.day && group.getActive().startDate.month">-{{group.getActive().startDate.day}}</span>
						</div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="group.getActive().endDate.year" ng-cloak>
					<div class="col-md-8">
						<strong><@orcid.msg 'manual_affiliation_form_contents.labelEndDate'/></strong>
						<div>
							<span ng-show="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span ng-show="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span><span ng-show="group.getActive().endDate.day && group.getActive().endDate.month">-{{group.getActive().endDate.day}}</span>						
						</div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="group.getActive().sourceName"
					ng-cloak>
					<div class="col-md-8">
						<strong><@orcid.msg 'manual_affiliation_form_contents.labelsource'/></strong>
						<div ng-bind="group.getActive().sourceName"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
