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
<@public classes=['home'] nav="members-list">
    <div class="row">        
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <h1>ORCID Member Organizations</h1>
            <p>ORCID is a non-profit organization supported by a global community of organizational members, including research organizations, publishers, funders, professional associations, and other stakeholders in the research ecosystem. Interested in becoming a member? <a href="<@orcid.rootPath '/about/membership'/>">Learn more about membership</a></p>
            <div ng-controller="MembersListController">
                <div ng-hide="membersListSrvc.membersList != null" class="text-center">
                    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                    <!--[if lt IE 8]>
                        <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
                    <![endif]-->
                </div>
                <div ng-show="membersListSrvc.membersList">
                    <p>There are {{membersListSrvc.membersList.length}} ORCID members</p>
                    <div class="member" ng-repeat="member in membersListSrvc.membersList | orderBy : 'name'">
                        <h2>{{member.name}}</h2>
                        <p>{{member.researchCommunity}} | {{member.country}}</p>
                        <p>
                        	<img class="member-logo" ng-hide="member.logoUrl == null" src="{{member.logoUrl}}">
                        	<span ng-bind-html="renderHtml(member.description)" ng-show="member.description"></span>
                        </p>
						<div class="clear-fix">
						    <a ng-href="{{getMemberPageUrl(member.slug)}}">Member details <i class="glyphicon x075 glyphicon-chevron-right"></i></a>
						</div>
                        <hr></hr>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@public>