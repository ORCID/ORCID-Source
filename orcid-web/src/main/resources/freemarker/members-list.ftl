<@public classes=['home'] nav="members-list">	
    <div class="member-list row">        
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
        	<div class="row">
        		<div class="col-md-12 col-sm-12 col-xs-12">
		            <h1><@orcid.msg 'member_list.orcid_member_organizations'/></h1>
		            <p><@orcid.msg 'member_list.orcid_is_a'/> <a href="<@orcid.rootPath '/about/membership'/>"><@orcid.msg 'developer_tools.member_api.description.1'/></a></p>
	            	
	            </div>            
	            <div ng-controller="MembersListController">
	                <div ng-hide="membersListSrvc.membersList != null" class="text-center" ng-cloak>
	                    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
	                    <!--[if lt IE 8]>
	                        <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
	                    <![endif]-->
	                </div>
	                
	                <div ng-show="membersListSrvc.membersList">
	                	<div class="col-md-12 col-sm-12 col-xs-12">
	                    	<p ng-cloak><@orcid.msg 'member_list.there_are_currently'/> {{membersListSrvc.membersList.length}} <@orcid.msg 'member_list.orcid_member_organizations'/></p>
	                    	<p>
	            				<a class="selected" href="<@orcid.rootPath '/members'/>"><@orcid.msg 'member_details.all_members'/></a> | <a href="<@orcid.rootPath '/consortia'/>"><@orcid.msg 'member_list.consortia_members'/></a>
	            			</p>
	            			
	            			<p>
							<select ng-model="by_country" ng-options="member.country as member.country for member in membersListSrvc.membersList | unique:'country' | orderBy : 'country'">
                					<option value=""><@orcid.msg 'macros.orcid.Country'/></option>
            				</select>
            				<select ng-model="by_researchCommunity" ng-options="member.researchCommunity as membersListSrvc.communityTypes[member.researchCommunity] for member in membersListSrvc.membersList | unique:'researchCommunity' | orderBy : 'researchCommunity'">
                					<option value=""><@orcid.msg 'member_list.research_community'/></option>
            				</select>
            				<button class="btn btn-primary" ng-click="clearFilters()">Reset</button>
            				</p>
            				
            				<hr class="no-margin-top no-margin-bottom" />
	            			<ul class="filter">
      							<li ng-click="activateLetter('')" ng-class="{'active':activeLetter==''}"><a>ALL</a></li>
      							<li ng-repeat="letter in alphabet track by $index " ng-click="activateLetter(letter)" ng-class="{'active':letter==activeLetter}"><a>{{letter}}</a></li>
    						</ul>
	                    </div>
	                    <div class="member" ng-repeat="member in membersListSrvc.membersList | filter:{ country: by_country} | filter: {researchCommunity: by_researchCommunity} | startsWithLetter : activeLetter:'publicDisplayName' | orderBy : 'publicDisplayName' ">
	                    	<hr class="no-margin-top" />
	                    	<div class="col-md-12 col-sm-12 col-xs-12">
	                        	<h2 ng-cloak><a ng-href="{{membersListSrvc.getMemberPageUrl(member.slug)}}" target="member.publicDisplayName">{{member.publicDisplayName}}</a></h2>
	                        	<p ng-cloak><span ng-if="membersListSrvc.communityTypes[member.researchCommunity]">{{membersListSrvc.communityTypes[member.researchCommunity]}}</span><span ng-if="membersListSrvc.communityTypes[member.researchCommunity]&&member.country"> | </span>{{member.country}}</p>
	                        </div>
	                        <div class="col-md-10 col-sm-10 col-xs-12">
	                        	<p>
		                        	<img class="member-logo" src="{{member.logoUrl}}"  ng-cloak ng-if="member.logoUrl">
		                        	<span class="member-decsription" ng-bind-html="renderHtml(member.description)" ng-if="member.description" ng-cloak></span>
	                        	</p>
	                        </div>
	                    </div>
	                </div>
	            </div>
	    	</div>
        </div>	    
    </div>
</@public>