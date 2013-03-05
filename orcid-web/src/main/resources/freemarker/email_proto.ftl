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
<@base>
<div style="padding: 100px;">
			<table class="table table-bordered settings-table" ng-app="orcidApp" ng-controller="EditTableCtrl">
				<tbody>
					<tr>
						<th>Email</th>
						<td>
							<a href="" ng-click="toggleEmail()">{{toggleText}}</a>
						</td>
					</tr>
					<tr ng-controller="EmailEdit" ng-show="showEditEmail">
						<td colspan="2">
	   						<div ng-repeat='email in emailsPojo.emails' style="height: 35px;">
	   							<!-- we should never see errors here, but just to be safe -->
	   							<span class="orcid-error" ng-show="emailsPojo.errors.length > 0">
		   							<span ng-repeat='error in emailsPojo.errors' ng-bind-html-unsafe="error"></span>
		   						</span>
	   							<div style="width: 400px; display:inline-block;">{{email.value}}</div>
	   							<div style="width: 100px; display:inline-block;"><a href="" ng-click="setPrimary(email)" ng-class="{muted: email.primary==false}" ng-model="email.primary" primary-display="email.primary"></a>
	   							</div> <div ng-click="toggleCurrent(email)" ng-bind="email.current | emailCurrentFtr" style="width: 100px; display:inline-block;"></div> 
	   							<span ng-bind="email.verified | emailVerifiedFtr" ng-click="verifyEmail(email)"></span>
	   							<span class="orcid-error" ng-show="email.errors.length > 0">
	   								error!
	   							   <span ng-repeat='error in email.errors'>{{error}}</span>
	   							</span>
	   							<div style="display:inline-block; width 30px">
	   								<span ng-show="email.primary == false" ng-click="deleteEmail($index)" class="btn btn-danger">X</span>
	   							</div>
	   							<div class="privacy-tool" style="display:inline-block;">
							        <div class="btn-group privacy-group abs-left-top">
							            <button class="btn {{email.visibility | emailVisibilityBtnClassFtr}} dropdown-toggle privacy-toggle" ng-bind="email.visibility | emailVisibilityFtr" ng-click="toggleVisibility(email)"></button>
							        </div>
								</div>
	   						</div>
	   						<div>
	   							<input type="text" placeholder="Add Another Email" class="input-xlarge" ng-model="inputEmail.value" style="margin: 0px;"/> <span ng-click="add()" class="btn">Add</span>
	   							<span class="orcid-error" ng-show="inputEmail.errors.length > 0">
		   							<span ng-repeat='error in inputEmail.errors' ng-bind-html-unsafe="error"></span>
		   						</span>
		   					</div>	
						</td>
					</tr>
				</tbody>
			</table>
</div>
</@base>
