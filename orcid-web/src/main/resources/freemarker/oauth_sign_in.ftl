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
	<#include "/common/browser-checks.ftl" />
	<div class="col-md-6 col-sm-12 oauth-margin-top-bottom-box" ng-controller="OauthAuthorizationController">
		<div class="page-header">
		    <h3><@orcid.msg 'oauth_sign_in.h3signin'/></h3>
		</div>
        <form id="loginForm" action="<@orcid.rootPath '/signin/auth'/>" method="post">	            
            <@spring.bind "loginForm" />
            <@spring.showErrors "<br/>" "error" />
            
			<#assign js_group_name = client_group_name?replace('"', '&quot;')?js_string>
			<#assign js_client_name = client_name?replace('"', '&quot;')?js_string>            
			<input type="hidden" name="client_name" value="${js_client_name}" />
			<input type="hidden" name="client_group_name" value="${js_group_name}" /> 
            <div>
                <label for="userId"><@orcid.msg 'oauth_sign_in.labelemailorID'/></label>
                <div class="relative">
                   <input type="text" id="userId" name="userId" value="${userId}" placeholder="<@orcid.msg 'login.username'/>" class="input-xlarge">
                </div>
            </div>
            <div id="passwordField">
                <label for="password"><@orcid.msg 'login.password'/></label>
                <div class="relative">
                   <input type="password" id="password" name="password" value="" placeholder="<@orcid.msg 'login.password'/>" class="input-xlarge">
                </div>
            </div>
            <div id="buttons">
                <div class="relative">
                    <button class="btn btn-primary" type="submit"><@orcid.msg 'oauth_sign_in.h3signin'/></button>
                    <span id="ajax-loader" class="hide"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x2 green"></i></span>
                </div>
                <div class="relative margin-top-box">
                	<a href="<@orcid.rootPath '/reset-password'/>"><@orcid.msg 'oauth_sign_in.forgottenpassword'/></a>
                </div>
            </div>
        </form>	
	</div>	   
	
<script type="text/ng-template" id="duplicates">
	<div class="lightbox-container" id="duplicates-records">
		<div class="row margin-top-box">			
			<div class="col-md-6 col-sm-6 col-xs-12">
	     		<h4><@orcid.msg 'duplicate_researcher.wefoundfollowingrecords'/>
	     		<@orcid.msg 'duplicate_researcher.to_access.1'/><a href="<@orcid.rootPath "/signin" />" target="signin"><@orcid.msg 'duplicate_researcher.to_access.2'/></a><@orcid.msg 'duplicate_researcher.to_access.3'/>
	     		</h4>
     		</div>
     		<div class="col-md-6 col-sm-6 col-xs-12 right margin-top-box">
	     	    <button class="btn btn-primary" ng-click="postRegisterConfirm()"><@orcid.msg 'duplicate_researcher.btncontinuetoregistration'/></button>
			</div>
		</div>				
		<div class="row">
			<div class="col-sm-12">
				<div class="table-container">
					<table class="table">
						<thead>
							<tr>               				
			    				<th><@orcid.msg 'search_results.thORCIDID'/></th>
    							<th><@orcid.msg 'duplicate_researcher.thEmail'/></th>
    							<th><@orcid.msg 'duplicate_researcher.thgivennames'/></th>
    							<th><@orcid.msg 'duplicate_researcher.thFamilyName'/></th>
	    						<th><@orcid.msg 'duplicate_researcher.thInstitution'/></th>                				
							</tr>
						</thead>
						<tbody>
						 	<tr ng-repeat='dup in duplicates'>
					 			<td><a href="<@orcid.rootPath '/'/>{{dup.orcid}}" target="dup.orcid">{{dup.orcid}}</a></td>
        						<td>{{dup.email}}</td>
        						<td>{{dup.givenNames}}</td>
        						<td>{{dup.familyNames}}</td>
        						<td>{{dup.institution}}</td>
    						</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>	
		<div class="row margin-top-box">
			<div class="col-md-12 col-sm-12 col-xs-12 right">
		    	<button class="btn btn-primary" ng-click="postRegisterConfirm()"><@orcid.msg 'duplicate_researcher.btncontinuetoregistration'/></button>
			</div>
		</div>
	</div>
</script>      
