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
    <div class="row member-list" ng-controller="MemberPageController">
    	
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
        	<p><a href="<@orcid.rootPath '/members'/>"><i class="glyphicon x075 glyphicon-chevron-left"></i> All members</a></p>
        	<div ng-hide="membersListSrvc.currentMemberDetails != null" class="text-center" ng-cloak>
	                    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
	                    <!--[if lt IE 8]>
	                        <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
	                    <![endif]-->
	    	</div>
        	<div class="row" ng-show="membersListSrvc.currentMemberDetails">
        		<div class="col-md-12 col-sm-12 col-xs-12">
		            <h1 ng-cloak><a href="{{membersListSrvc.currentMemberDetails.member.websiteUrl}}" target="membersListSrvc.currentMemberDetails.member.publicDisplayName">{{membersListSrvc.currentMemberDetails.member.publicDisplayName}}</a></h1>
                    <p ng-cloak><span ng-if="membersListSrvc.communityTypes[membersListSrvc.currentMemberDetails.member.researchCommunity]">{{membersListSrvc.communityTypes[membersListSrvc.currentMemberDetails.member.researchCommunity]}}</span><span ng-if="membersListSrvc.communityTypes[membersListSrvc.currentMemberDetails.member.researchCommunity]&&membersListSrvc.currentMemberDetails.member.country"> | </span>{{membersListSrvc.currentMemberDetails.member.country}}</p>
		        </div>
		        <div class="col-md-10 col-sm-10 col-xs-12">		       
		       		<p>
                    	<img class="member-logo" src="{{membersListSrvc.currentMemberDetails.member.logoUrl}}"  ng-cloak ng-if="membersListSrvc.currentMemberDetails.member.logoUrl">
                    	<span class="member-decsription" ng-bind-html="renderHtml(membersListSrvc.currentMemberDetails.member.description)" ng-if="membersListSrvc.currentMemberDetails.member.description" ng-cloak></span>
	                </p>	
		        </div>
		        <hr />
		        <div class="col-md-12 col-sm-12 col-xs-12" ng-if="membersListSrvc.currentMemberDetails.parentOrgName">		        	
                    <h3>Consortium/Parent Organization: </h3>
                    <p> 
	                    <span ng-show="membersListSrvc.currentMemberDetails.parentOrgName" ng-cloak><a ng-href="{{membersListSrvc.getMemberPageUrl(membersListSrvc.currentMemberDetails.parentOrgSlug)}}">{{membersListSrvc.currentMemberDetails.parentOrgName}}</a></span>
	                    <span ng-hide="membersListSrvc.currentMemberDetails.parentOrgName" ng-cloak>None</span>
	                </p>
	            <hr />
	            </div>
	            <div class="col-md-12 col-sm-12 col-xs-12">   
	                <h3>Contact Information</h3>
	                <p ng-if="membersListSrvc.currentMemberDetails.member.publicDisplayEmail" ng-cloak>
		                <a href="mailto:{{membersListSrvc.currentMemberDetails.member.publicDisplayEmail}}">{{membersListSrvc.currentMemberDetails.member.publicDisplayEmail}}</a>
	                </p>
	                <p ng-if="!membersListSrvc.currentMemberDetails.member.publicDisplayEmail" ng-cloak> 
	                    This member has not provided contact details.
	                </p>	                
	            </div> 
	            <hr />
	            <div class="col-md-12 col-sm-12 col-xs-12">   
	                <h3>Integrations</h3>
	                <div ng-if="membersListSrvc.currentMemberDetails.integrations" ng-repeat="integration in membersListSrvc.currentMemberDetails.integrations" ng-cloak>
                        <p><b>{{integration.name}}</b> <em>{{integration.stage}}</em></p>
                        <@orcid.checkFeatureStatus 'BADGES'>
                            <div ng-if="integration.badgeAwarded">
                                <div class="cc-badge authenticate popover-help-container" ng-if="integration.level=='Collect'||integration.level=='Display'||integration.level=='Connect'||integration.level=='Sync'">
                                    <a href="javascript:void(0);"><img src="${staticCdn}/img/cc_authenticate.png" height="34" width="34" alt="ORCID Authenticate badge" /></a>
                                    <div id="cc-authenticate-help" class="popover bottom">
                                      <div class="asrrow"></div>
                                      <div class="popover-content">
                                        <p><@orcid.msg 'member_list.details.authenticate_help_text'/></p>
                                      </div>
                                    </div>
                                </div>
                                <div class="cc-badge collect popover-help-container" ng-if="integration.level=='Collect'||integration.level=='Display'||integration.level=='Connect'||integration.level=='Sync'">
                                    <a href="javascript:void(0);"><img src="${staticCdn}/img/cc_collect.png" height="34" width="34" alt="ORCID Collect badge" /></a>
                                    <div id="cc-collect-help" class="popover bottom">
                                      <div class="asrrow"></div>
                                      <div class="popover-content">
                                        <p><@orcid.msg 'member_list.details.collect_help_text'/></p>
                                      </div>
                                    </div>
                                </div>
                                <div class="cc-badge display popover-help-container" ng-if="integration.level=='Display'||integration.level=='Connect'||integration.level=='Sync'">
                                    <a href="javascript:void(0);"><img src="${staticCdn}/img/cc_display.png" height="34" width="34" alt="ORCID Display badge" /></a>
                                    <div id="cc-display-help" class="popover bottom">
                                      <div class="arrow"></div>
                                      <div class="popover-content">
                                        <p><@orcid.msg 'member_list.details.display_help_text'/></p>
                                      </div>
                                    </div>
                                </div>
                                <div class="cc-badge connect popover-help-container" ng-if="integration.level=='Connect'||integration.level=='Sync'">
                                    <a href="javascript:void(0);"><img src="${staticCdn}/img/cc_connect.png" height="34" width="34" alt="ORCID Connect badge" /></a>
                                    <div id="cc-connect-help" class="popover bottom">
                                      <div class="arrow"></div>
                                      <div class="popover-content">
                                        <p><@orcid.msg 'member_list.details.connect_help_text'/></p>
                                      </div>
                                    </div>
                                </div>
                                <div class="cc-badge sync popover-help-container" ng-if="integration.level=='Sync'">
                                    <a href="javascript:void(0);"><img src="${staticCdn}/img/cc_sync.png" height="34" width="34" alt="ORCID Sync badge" /></a>
                                    <div id="cc-sync-help" class="popover bottom">
                                      <div class="arrow"></div>
                                      <div class="popover-content">
                                        <p><@orcid.msg 'member_list.details.sync_help_text'/></p>
                                      </div>
                                    </div>
                                </div>
                            </div>
                        </@orcid.checkFeatureStatus>
	                    <ul class="clearfix">
	                        <li ng-bind-html="renderHtml(integration.description)" ng-if="integration.description" ng-cloak>
	                        </li>
	                        <li ng-if="integration.resourceUrl" >
	                            <a href="{{integration.resourceUrl}}" target="Learn more about this integration">Learn more about this integration</a>
	                        </li>
	                    </ul>
	                </div>
	                <div ng-hide="membersListSrvc.currentMemberDetails.integrations.length"> 
	                    <p>This member has not completed any integrations.</p>
	                </div>
	                <hr />
				</div>
	            <div class="col-md-12 col-sm-12 col-xs-12" ng-if="membersListSrvc.currentMemberDetails.subMembers.length">
	                <h3>Consortium Members</h3>
	                <table ng-show="membersListSrvc.currentMemberDetails.subMembers">
	                	<tr>
	                		<th>Member Name</th>
	                	</tr>
	                	<tr ng-repeat="subMember in membersListSrvc.currentMemberDetails.subMembers | orderBy : 'opportunity.accountName'">
							<td><a ng-href="{{membersListSrvc.getMemberPageUrl(subMember.slug)}}">{{subMember.opportunity.accountName}}</a></td>
	                	</tr>
	                </table>
	                <div ng-hide="membersListSrvc.currentMemberDetails.subMembers.length"> 
						<p>This consortium does not have any members yet.</p>
						<hr />
	                </div>
	                
		        </div>
            </div>
        </div>
    </div>
</@public>