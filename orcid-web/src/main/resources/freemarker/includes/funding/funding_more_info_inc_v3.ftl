<div class="more-info" ng-show="moreInfo[group.groupId] && group.activePutCode == funding.putCode.value">
	<span class="dotted-bar"></span>	
    <div class="row">        
		<!-- Funding subtype -->
        <div class="col-md-6" ng-show="group.getActive().organizationDefinedFundingSubType.subtype.value" ng-cloak>
            <div class="bottomBuffer">                    
                <strong><@orcid.msg 'manual_funding_form_contents.organization_defined_type.label'/></strong>
                <div ng-bind="group.getActive().organizationDefinedFundingSubType.subtype.value"></div>
            </div>        
        </div> 
        
        <!-- Funding translated title -->
        <div class="col-md-6" ng-show="group.getActive().fundingTitle.translatedTitle.content" ng-cloak>
            <div class="bottomBuffer">                
                <strong><@orcid.msg
                    'manual_funding_form_contents.label_translated_title'/></strong>
                <div ng-bind="renderTranslatedTitleInfo(funding)"></div>                    
            </div>        
        </div>
        
        <!-- Funding Amount -->
        <div class="col-md-6" ng-show="group.getActive().amount.value" ng-cloak>
            <div class="bottomBuffer">                
                <strong><@orcid.msg 'manual_funding_form_contents.label_amount'/></strong>
                <div>{{group.getActive().currencyCode.value}} {{group.getActive().amount.value}}</div>                
            </div>
        </div>
        
        <!-- Contribuitors -->
        <div class="col-md-6" ng-show="group.getActive().contributors.length > 0" ng-cloak>
            <div class="bottomBuffer">
                <strong><@orcid.msg 'manual_funding_form_contents.label_contributors'/></strong>
                <div ng-repeat="contributor in group.getActive().contributors">
                    {{contributor.creditName.value}} <span
                        ng-bind='contributor | contributorFilter'></span>
                </div>        
            </div>
        </div>
        
        <!-- Description -->
        <div class="col-md-6" ng-show="group.getActive().description.value" ng-cloak>
            <div class="bottomBuffer">                
                <strong><@orcid.msg 'manual_funding_form_contents.label_description'/></strong>
                <div ng-bind="group.getActive().description.value"></div>                
            </div>
        </div>
        
        <!-- Created Date -->
        <div class="col-md-6">
        	<strong><@orcid.msg 'groups.common.created'/></strong>
        	<div ng-bind="group.getActive().createdDate | ajaxFormDateToISO8601"></div>
        </div>
    </div>
</div>
