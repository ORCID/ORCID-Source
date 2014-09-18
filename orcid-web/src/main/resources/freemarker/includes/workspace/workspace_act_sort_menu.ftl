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
	<div class="sort-menu-container">			       					 
 		<ul class="action-option manage-button toggle-menu">
 			<li>
				<span class="glyphicon glyphicon-sort"></span>							
				<@orcid.msg 'manual_orcid_record_contents.sort'/>
				<ul class="sort-menu-options">
					<li ng-class="{'checked':sortPredicateKey=='date'}">											
						<a ng-click="sort('date');">
							<@orcid.msg 'manual_orcid_record_contents.sort_date'/>
							
							<span ng-show="sortReverseKey['date']" ng-class="{'glyphicon glyphicon-sort-by-order-alt':sortPredicateKey=='date'}"></span>
							<span ng-show="sortReverseKey['date'] == false" ng-class="{'glyphicon glyphicon-sort-by-order':sortPredicateKey=='date'}"></span>
																					
						</a>																					
					</li>
				    <li ng-class="{'checked':sortPredicateKey=='title'}">									    	
				    	<a ng-click="sort('title');">
				    		<@orcid.msg 'manual_orcid_record_contents.sort_title'/>
				    		
				    		<span ng-show="sortReverseKey['title']" ng-class="{'glyphicon glyphicon-sort-by-alphabet-alt':sortPredicateKey=='title'}" ></span>
				    		<span ng-show="sortReverseKey['title'] == false" ng-class="{'glyphicon glyphicon-sort-by-alphabet':sortPredicateKey=='title'}" ></span>
				    		
				    	</a>									    	
				    </li>
					<li ng-show="sortGroupType != 'affiliation'" ng-class="{'checked':sortPredicateKey=='type'}">											
						<a ng-click="sort('type');">
							<@orcid.msg 'manual_orcid_record_contents.sort_type'/>
							
							<span ng-show="sortReverseKey['type']" ng-class="{'glyphicon glyphicon-sort-by-alphabet-alt':sortPredicateKey=='type'}"></span>
							<span ng-show="sortReverseKey['type'] == false" ng-class="{'glyphicon glyphicon-sort-by-alphabet':sortPredicateKey=='type'}"></span>
							
						</a>																						
					</li>
			    </ul>											
			</li>
		</ul>									
	</div>
</#escape>