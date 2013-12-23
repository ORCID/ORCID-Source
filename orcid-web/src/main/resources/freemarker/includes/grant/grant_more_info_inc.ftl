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
<div class="more-info" ng-mouseleave="closeMoreInfo(grant.putCode.value)" ng-class="{'more-info-show':moreInfo[grant.putCode.value]==true}">
	<a class="glyphicon glyphicon-plus-sign grey" ng-mouseenter="moreInfoMouseEnter(grant.putCode.value,$event);" ng-click="toggleClickMoreInfo(grant.putCode.value)"></a>	
	<div class="popover bottom more-info-container">
		<div class="arrow"></div>	
		<div class="lightbox-container">
			<div class="ie7fix">
			<div class="row bottomBuffer"></div>
			<div class="row bottomBuffer" ng-show="grant.grantName.value"
				ng-cloak>
				<div class="col-md-8">					
					<strong><@orcid.msg 'manual_grant_form_contents.label_institution_organization'/></strong>
					<div ng-bind="grant.grantName.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.grantType.value"
				ng-cloak>
				<div class="col-md-8">					
					<strong><@orcid.msg 'manual_grant_form_contents.grant_type'/></strong>
					<div ng-bind="grant.grantTypeForDisplay"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.city.value"
				ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_grant_form_contents.labelcity'/></strong>
					<div ng-bind="grant.city.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.region.value"
				ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_grant_form_contents.labelregion'/></strong>
					<div ng-bind="grant.region.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.country.value"
				ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_grant_form_contents.labelcountry'/></strong>
					<div ng-bind="grant.countryForDisplay"></div>
				</div>
			</div>			
			<div class="row bottomBuffer" ng-show="grant.title.value"
				ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_grant_form_contents.label_title'/></strong>
					<div ng-bind="grant.title.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.description.value"
				ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_grant_form_contents.label_description'/></strong>
					<div ng-bind="grant.description.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.amount.value"
				ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_grant_form_contents.label_amount'/></strong>
					<div>{{grant.currencyCode.value}} {{grant.amount.value}}</div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.url.value"
				ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_grant_form_contents.label_url'/></strong>
					<div ng-bind="grant.url.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.startDate.year" ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_grant_form_contents.labelStartDate'/></strong>
					<div>
						<span
							ng-show="grant.startDate.day && grant.startDate.month">{{grant.startDate.day}}-</span><span
							ng-show="grant.startDate.month">{{grant.startDate.month}}-</span><span
							ng-show="grant.startDate.year">{{grant.startDate.year}}</span>
					</div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.endDate.year" ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_grant_form_contents.labelEndDate'/></strong>
					<div>
						<span
							ng-show="grant.endDate.day && grant.endDate.month">{{grant.endDate.day}}-</span><span
							ng-show="grant.endDate.month">{{grant.endDate.month}}-</span><span
							ng-show="grant.endDate.year">{{grant.endDate.year}}</span>
					</div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.externalIdentifiers.length > 0" ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_grant_form_contents.title_external_identifier'/></strong>
					<div>
						<span ng-repeat='ei in grant.externalIdentifiers'> <span
							ng-bind-html='ei | externalIdentifierHtml:$first:$last:grant.externalIdentifiers.length'></span>
						</span>
					</div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.contributors.length > 0"
				ng-cloak>
				<div class="col-md-12">
					<strong><@orcid.msg 'manual_grant_form_contents.label_contributors'/></strong>
					<div ng-repeat="contributor in grant.contributors">
						{{contributor.creditName.value}} <span
							ng-bind='contributor | contributorFilter'></span>
					</div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="grant.sourceName"
				ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_grant_form_contents.label_source'/></strong>
					<div ng-bind="grant.sourceName"></div>
				</div>
			</div>
			</div>
		</div>
	</div>
</div>
