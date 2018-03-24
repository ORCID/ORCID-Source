<div class="more-info content" ng-if="moreInfo[group.groupId]">
	<div class="row bottomBuffer">
		<div class="col-md-12"></div>
	</div>
	<span class="dotted-bar"></span>	
	<div class="row">
		<div class="org-ids" ng-if="group.getActive().orgDisambiguatedId.value">
            <div class="col-md-12">   
                <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                <span bind-html-compile='group.getActive().disambiguatedAffiliationSourceId.value | orgIdentifierHtml:group.getActive().disambiguationSource.value:group.getActive().putCode.value:group.getActive().disambiguationSource' class="url-popover"> 
                </span>
            </div>
            <div class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">
                <span ng-if="group.getActive().orgDisambiguatedName">{{group.getActive().orgDisambiguatedName}}</span><span ng-if="group.getActive().orgDisambiguatedCity || group.getActive().orgDisambiguatedRegion || group.getActive().orgDisambiguatedCountry">: </span><span ng-if="group.getActive().orgDisambiguatedCity" ng-cloak>{{group.getActive().orgDisambiguatedCity}}</span><span ng-if="group.getActive().orgDisambiguatedCity && group.getActive().orgDisambiguatedRegion">, </span><span ng-if="group.getActive().orgDisambiguatedRegion" ng-cloak>{{group.getActive().orgDisambiguatedRegion}}</span><span ng-if="group.getActive().orgDisambiguatedCountry && (group.getActive().orgDisambiguatedCity || group.getActive().orgDisambiguatedRegion)">, </span><span ng-if="group.getActive().orgDisambiguatedCountry" ng-cloak>{{group.getActive().orgDisambiguatedCountry}}</span>
                <span ng-if="group.getActive().orgDisambiguatedUrl"><br>
                <a href="{{group.getActive().orgDisambiguatedUrl}}" target="orgDisambiguatedUrl"><span ng-bind="group.getActive().orgDisambiguatedUrl" ng-cloak></span></a>
                </span>
                <!--orgDisambiguatedExternalIdentifiers-->
                <div ng-if="group.getActive().orgDisambiguatedExternalIdentifiers">
                    <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{group.getActive().disambiguationSource.value}}</strong><br>
                    <ul class="reset">
                        <li ng-repeat="orgDisambiguatedExternalIdentifier in group.getActive().orgDisambiguatedExternalIdentifiers | orderBy:orgDisambiguatedExternalIdentifier.identifierType">{{orgDisambiguatedExternalIdentifier.identifierType}}:  <span ng-if="orgDisambiguatedExternalIdentifier.preferred">{{orgDisambiguatedExternalIdentifier.preferred}} <@orcid.msg 'workspace_affiliations.external_ids_preferred'/>, </span> <span ng-if="orgDisambiguatedExternalIdentifier.all"><span ng-repeat="orgDisambiguatedExternalIdentifierAll in orgDisambiguatedExternalIdentifier.all">{{orgDisambiguatedExternalIdentifierAll}}{{$last ? '' : ', '}}</span></span></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="col-md-6" ng-if="group.getActive().url.value" ng-cloak>
        	<div class="bottomBuffer">
				<strong><@orcid.msg 'common.url'/></strong><br> 
				<a href="{{group.getActive().url.value}}" target="affiliation.url.value">{{group.getActive().url.value}}</a>
			</div>
		</div>	
        <div class="col-md-12">
        	<div class="bottomBuffer">
				<strong><@orcid.msg 'groups.common.created'/></strong><br> 
				<span ng-bind="group.getActive().createdDate | ajaxFormDateToISO8601"></span>
			</div>
		</div>	
	</div>
</div>
 