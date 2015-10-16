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
<@public classes=['home'] nav="signin">
<#include "sandbox_warning.ftl"/>
    <form class="form-sign-in shibboleth" id="loginForm" ng-enter-submit action="<@orcid.rootPath '/signin/auth'/>" method="post">
        <div class="row">
	        <div class="col-md-offset-3 col-md-offset-9 col-sm-offset-3 col-sm-9 col-xs-12">
	        	<div class="congrat">
        			<div class="grey-box">
        				<h4>Congratulations!</h4>
			            <p>
				            You have linked your ${providerId} account ${emailId}<br />
				            to your ORCID account ${effectiveUserOrcid}.<br />
				            You will now be able to signin to ORCID using ${providerId}.<br />
				            You can now <a href="<@orcid.rootPath '/my-orcid'/>">continue to your ORCID record</a>.
		            	</p>
		            </div>
	            </div>
           </div>
        </div>
    </form>
</@public>