<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
			        <div class="row">        
			        	<!-- Information -->
						<div class="col-md-8 col-sm-8">
						    <div class="affiliation-type" ng-bind-html="affiliation.affiliationTypeForDisplay"></div>
					        <h3 class="affiliation-title">
					        	<strong ng-bind-html="affiliation.affiliationName.value"></strong>
					        	<span class="affiliation-date" ng-show="affiliation.startDate">
					        	    (<span ng-show="affiliation.startDate.month">{{affiliation.startDate.month}}-</span><span ng-show="affiliation.startDate.year">{{affiliation.startDate.year}}</span>
					        	    <@orcid.msg 'workspace_affiliations.dateSeparator'/>
					        	    <span ng-show="affiliation.endDate">
					        	        <span ng-show="affiliation.endDate.month">{{affiliation.endDate.month}}-</span><span ng-show="affiliation.endDate.year">{{affiliation.endDate.year}}</span>)
					        	    </span>
					        	    <span ng-hide="affiliation.endDate">
					        	        <@orcid.msg 'workspace_affiliations.present'/>)
					        	    </span>
					        	</span>
					        </h3>
					        <div class="affiliation-details" ng-show="affiliation.roleTitle">
					            <span ng-bind-html="affiliation.roleTitle.value"></span>
					        </div>
					        <div ng-show="affiliation.sourceName">
				            	<span class="affiliation-source">SOURCE: <span ng-bind-html="affiliation.sourceName"></span></span>
				        	</div>
				        </div>
				        <!-- Privacy Settings -->
				        <div class="col-md-4 col-sm-4 workspace-toolbar">
				        	<#include "affiliate_more_info_inc.ftl"/>
				        	<a href ng-click="deleteAffiliation(affiliation)" class="glyphicon glyphicon-trash grey"></a>
				        	<ul class="workspace-private-toolbar">
									<@orcid.privacyToggle "affiliation.visibility.visibility" "setPrivacy(affiliation.putCode.value, 'PUBLIC', $event)" 
			                    	  "setPrivacy(affiliation.putCode.value, 'LIMITED', $event)" "setPrivacy(affiliation.putCode.value, 'PRIVATE', $event)" />			        
					        </ul>
						</div>
					</div>
