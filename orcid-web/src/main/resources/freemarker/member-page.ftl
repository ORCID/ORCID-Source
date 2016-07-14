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
<@public classes=['home']>
    <div class="row" ng-controller="MemberPageController" ng-init="membersListSrvc.getCurrentMemberDetailsBySlug('${memberSlug}')">
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <h1 ng-cloak>{{membersListSrvc.currentMemberDetails.member.name}}</h1>
            <p ng-cloak>{{membersListSrvc.currentMemberDetails.member.researchCommunity}} | {{membersListSrvc.currentMemberDetails.member.country}}</p>
            <p>
                <img class="member-logo" ng-hide="membersListSrvc.currentMemberDetails.member.logoUrl == null" src="{{membersListSrvc.currentMemberDetails.member.logoUrl}}">
                <span ng-bind-html="renderHtml(membersListSrvc.currentMemberDetails.member.description)" ng-if="membersListSrvc.currentMemberDetails.member.description" ng-cloak></span>
            </p>
            <div class="clear-fix">
                <p>
                    <b>Consortium/Parent Organization: </b> 
                    <span ng-show="membersListSrvc.currentMemberDetails.parentOrgName" ng-cloak><a ng-href="{{membersListSrvc.getMemberPageUrl(membersListSrvc.currentMemberDetails.parentOrgSlug)}}">{{membersListSrvc.currentMemberDetails.parentOrgName}}</a></span>
                    <span ng-hide="membersListSrvc.currentMemberDetails.parentOrgName" ng-cloak>None</span>
                </p>
                <hr></hr>
                <h3>Contacts</h3>
                <div ng-if="membersListSrvc.currentMemberDetails.contacts" ng-repeat="contact in membersListSrvc.currentMemberDetails.contacts | orderBy : 'role'" ng-cloak>
                    <p><b>{{contact.role}}</b></p>
                    <p>{{contact.name}}</p>
                    <p><a href="mailto:{{contact.email}}">{{contact.email}}</a></p>
                </div>
                <div ng-hide="membersListSrvc.currentMemberDetails.contacts.length" ng-cloak> 
                    <p>This member does not have contact details.</p>
                </div>
                <hr></hr>
                <h3>Integrations</h3>
                <div ng-if="membersListSrvc.currentMemberDetails.integrations" ng-repeat="integration in membersListSrvc.currentMemberDetails.integrations" ng-cloak>
                    <p><b>{{integration.name}}</b> <em>{{integration.stage}}</em></p>
                    <p>
                        <span ng-bind-html="renderHtml(integration.description)" ng-if="integration.description" ng-cloak>
                            
                        </span>
                   </p>
                   <p>
                        <span ng-if="integration.resourceUrl" >
                            <a href="{{integration.resourceUrl}}" target="_blank">Learn more about this integration</a>
                        </span>
                    </p>
                </div>
                <div ng-hide="membersListSrvc.currentMemberDetails.integrations.length"> 
                    <p>This member has not completed any integrations.</p>
                </div>
                <hr></hr>
                <h3>Consortium Members</h3>
                <div ng-show="membersListSrvc.currentMemberDetails.subMembers" ng-repeat="subMember in membersListSrvc.currentMemberDetails.subMembers | orderBy : 'name'">
                    <p><a ng-href="{{membersListSrvc.getMemberPageUrl(subMember.slug)}}">{{subMember.name}}</a></p>
                </div>
                <div ng-hide="membersListSrvc.currentMemberDetails.subMembers.length"> 
                    <p>This member does not have sub members.</p>
                </div>
            </div>
        </div>
    </div>
</@public>