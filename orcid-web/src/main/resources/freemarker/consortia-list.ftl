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
<@public classes=['home'] nav="consortia-list">
    <div class="member-list row">        
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <h1>ORCID Consortia Members</h1>
            <p>Consortia are groups of 5 or more non-profit and/or governmental organizations organizations taking a coordinated approach to ORCID implementation.
			Interested in joining ORCID as a consortium? <a href="<@orcid.rootPath '/about/membership'/>">Learn more about membership</a></p>
            <div ng-controller="ConsortiaListController">
                <div ng-hide="membersListSrvc.consortiaList != null" class="text-center">
                    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                    <!--[if lt IE 8]>
                        <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
                    <![endif]-->
                </div>
                <div ng-show="membersListSrvc.consortiaList">
                    <p>There are currently {{membersListSrvc.consortiaList.length}} ORCID consortia members.</p>
                    <p>
	            		<a href="<@orcid.rootPath '/members'/>">All members</a> | <a class="selected" href="<@orcid.rootPath '/consortia'/>">Consortia members</a>
	            	<p>
						<select ng-model="by_country" ng-options="member.country as member.country for member in membersListSrvc.consortiaList | unique:'country' | orderBy : 'country'">
            					<option value="">Country</option>
        				</select>
        				<select ng-model="by_researchCommunity" ng-options="member.researchCommunity as member.researchCommunity for member in membersListSrvc.consortiaList | unique:'researchCommunity' | orderBy : 'researchCommunity'">
            					<option value="">Research community</option>
        				</select>
        				<button class="btn btn-primary" ng-click="clearFilters()">Reset</button>
        			</p>
        				
    				<hr class="no-margin-top no-margin-bottom" />
        			<ul class="filter">
						<li ng-click="activateLetter('')" ng-class="{'active':activeLetter==''}"><a>ALL</a></li>
						<li ng-repeat="letter in alphabet track by $index " ng-click="activateLetter(letter)" ng-class="{'active':letter==activeLetter}"><a>{{letter}}</a></li>

					</ul>
                    <div class="member" ng-repeat="member in membersListSrvc.consortiaList | filter:{ country: by_country} | filter: {researchCommunity: by_researchCommunity} | startsWithLetter : activeLetter | orderBy : 'name' ">
                        <hr class="no-margin-top" />
	                    	<div class="col-md-12 col-sm-12 col-xs-12">
	                        	<h2 ng-cloak><a href="{{member.websiteUrl}}" target="_blank">{{member.name}}</a></h2>	                        
	                        	<p ng-cloak>{{member.researchCommunity}} | {{member.country}}</p>
	                        </div>
	                        <div class="col-md-10 col-sm-10 col-xs-12">
	                        	<p>
		                        	<img class="member-logo" src="{{member.logoUrl}}"  ng-cloak ng-if="member.logoUrl">
		                        	<span class="member-decsription" ng-bind-html="renderHtml(member.description)" ng-if="member.description" ng-cloak></span>
	                        	</p>
	                        	<p class="clear-fix">
	                        		<a ng-href="{{membersListSrvc.getMemberPageUrl(member.slug)}}" ng-cloak>Member details <i class="glyphicon x075 glyphicon-chevron-right"></i></a>
	                        	</p>
	                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@public>