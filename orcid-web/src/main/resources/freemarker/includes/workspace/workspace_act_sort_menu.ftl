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
<#escape x as x?html>
	<!-- Sort -->
	<div class="menu-container">			       					 
 		<ul class="toggle-menu">
 			<li>
				<span class="glyphicon glyphicon-sort"></span>							
				<@orcid.msg 'manual_orcid_record_contents.sort'/>
				<ul class="menu-options sort">
					<li ng-class="{'checked':sortState.predicateKey=='date'}" ng-hide="sortHideOption">											
						<a ng-click="sort('date');" class="action-option manage-button">
							<@orcid.msg 'manual_orcid_record_contents.sort_date'/>
							<span ng-show="sortState.reverseKey['date']" ng-class="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='date'}"></span>
							<span ng-show="sortState.reverseKey['date'] == false" ng-class="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='date'}"></span>
						</a>																					
					</li>
				    <li ng-class="{'checked':sortState.predicateKey=='groupName'}" ng-hide="sortHideOption == null">
				    	<a ng-click="sort('groupName');" class="action-option manage-button">
				    		<@orcid.msg 'manual_orcid_record_contents.sort_title'/>
				    		<span ng-show="sortState.reverseKey['groupName']" ng-class="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='groupName'}" ></span>
				    		<span ng-show="sortState.reverseKey['groupName'] == false" ng-class="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='groupName'}" ></span>
				    	</a>									    	
				    </li>
				    <li ng-class="{'checked':sortState.predicateKey=='title'}" ng-hide="sortHideOption">									    	
				    	<a ng-click="sort('title');" class="action-option manage-button">
				    		<@orcid.msg 'manual_orcid_record_contents.sort_title'/>
				    		<span ng-show="sortState.reverseKey['title']" ng-class="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='title'}" ></span>
				    		<span ng-show="sortState.reverseKey['title'] == false" ng-class="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='title'}" ></span>
				    	</a>									    	
				    </li>
					<li ng-show="sortState.type != 'affiliation'" ng-class="{'checked':sortState.predicateKey=='type'}" ng-hide="sortHideOption">											
						<a ng-click="sort('type');" class="action-option manage-button">
							<@orcid.msg 'manual_orcid_record_contents.sort_type'/>
							<span ng-show="sortState.reverseKey['type']" ng-class="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='type'}"></span>
							<span ng-show="sortState.reverseKey['type'] == false" ng-class="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='type'}"></span>
						</a>																						
					</li>
			    </ul>											
			</li>
		</ul>									
	</div>
</#escape>