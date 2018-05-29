<#escape x as x?html>
	<!-- Sort -->
	<div class="menu-container">			       					 
 		<ul class="toggle-menu">
 			<li>
				<span class="glyphicon glyphicon-sort"></span>							
				<@orcid.msg 'manual_orcid_record_contents.sort'/>
				<ul class="menu-options sort">
					<li ng-class="{'checked':sortState.predicateKey=='endDate'}" ng-show="sortState.type == 'affiliation'">											
						<a ng-click="sort('endDate');" class="action-option manage-button">
							<@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
							<span ng-show="sortState.reverseKey['endDate']" ng-class="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='endDate'}"></span>
							<span ng-show="sortState.reverseKey['endDate'] == false" ng-class="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='endDate'}"></span>
						</a>																					
					</li>
				    <li ng-class="{'checked':sortState.predicateKey=='startDate'}" ng-show="sortState.type == 'affiliation'">											
						<a ng-click="sort('startDate');" class="action-option manage-button">
							<@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
							<span ng-show="sortState.reverseKey['startDate']" ng-class="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='startDate'}"></span>
							<span ng-show="sortState.reverseKey['startDate'] == false" ng-class="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='startDate'}"></span>
						</a>																					
					</li>
					<li ng-class="{'checked':sortState.predicateKey=='date'}" ng-hide="sortHideOption || sortState.type == 'affiliation'">											
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
					<li ng-class="{'checked':sortState.predicateKey=='type'}" ng-hide="sortHideOption || sortState.type == 'affiliation'">											
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