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
    <#include "/common/browser-checks.ftl" />
    <div class="span6">
    <div class="page-header">
	    <h3>Sign in</h3>
	</div>
        <form id="loginForm" action="<@spring.url '/signin/auth'/>" method="post">
            
            <@spring.bind "loginForm" />
            <@spring.showErrors "<br/>" "error" />

            <div>
                <label for="userId">Email or iD</label>
                <div class="relative">
                   <@spring.formInput "loginForm.userId" 'placeholder="Email or ORCID" class="input-xlarge"' />
                </div>
            </div>
            <div id="passwordField">
                <label for="password">Password</label>
                <div class="relative">
                   <@spring.formPasswordInput "loginForm.password" 'placeholder="Password" class="input-xlarge"' />
                </div>
            </div>
            <div id="buttons">
                <div class="relative">
                    <button class="btn-large btn-primary" type="submit">Sign in</button>
                    <img id="ajax-loader" class="hide" src="<@spring.url '/static/img/ajax-loader.gif'/>" alt="Progress animation"></img>
                </div>
                <div class="relative">
                	<a href="<@spring.url '/reset-password'/>">Forgotten Password?</a>
                </div>
            </div>
        </form>
    </div>
