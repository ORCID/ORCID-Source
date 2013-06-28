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
<#assign verDateTime = startupDate?datetime>
<#macro base ver="${verDateTime?iso_utc}">
<!DOCTYPE html>
<!--[if lt IE 9]><html class="no-js oldie" lang="en"><![endif]-->
<!--[if gt IE 9]><!--><html class="no-js" lang="en"><!--<![endif]-->
<#include "/common/html-head.ftl" />
<body data-baseurl="<@spring.url '/'/>">
<#nested />
<#include "/common/scripts.ftl" />
</body>
</html>
</#macro>
<#macro nav></#macro>
<#macro footer></#macro>
<#macro public css=[] js=[] classes=[] other=[] nav="" >
<@base>
<#if devSandboxUrl != ''>
    <div class="dev-watermark"></div>
</#if>
<div class="container">
    <header class="header center">
        <div class="row">
            <div class="span11 offset1">
                <div class="search">
                    <form id="form-search" action="${aboutUri}/search/node?lang=${locale}" method="POST">
                        <input type="search" name="keys" placeholder="${springMacroRequestContext.getMessage("public-layout.search", [], "", false)}" />
                        <fieldset class="search_options">
                            <input type="radio" name="huh_radio" id="filter_registry" value="registry" checked />
                            <label for="filter_registry">${springMacroRequestContext.getMessage("public-layout.search.choice.registry", [], "", false)}</label>
                            <input type="radio" name="huh_radio" id="filter_website" value="website" />
                            <label for="filter_website">${springMacroRequestContext.getMessage("public-layout.search.choice.website", [], "", false)}</label>
                        </fieldset>
                        <div class="conditions">
                            <p>${springMacroRequestContext.getMessage("public-layout.search.terms1", [], "", false)}<a href="${aboutUri}/legal?lang=${locale}">${springMacroRequestContext.getMessage("public-layout.search.terms2", [], "", false)}</a>${springMacroRequestContext.getMessage("public-layout.search.terms3", [], "", false)}</p>
                        </div>
                        <button type="submit" class="search-button"><i class="icon-orcid-search"></i></button>
                        <a href="<@spring.url "/orcid-search/search" />" class="settings-button" title="${springMacroRequestContext.getMessage("public-layout.search.advanced", [], "", false)}"><i class="icon-cog"></i></a>
                    </form>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="span3 override">
                <aside class="logo">
                    <h1><a href="${aboutUri}?lang=${locale}"><img src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo" /></a></h1>
                    <p>${springMacroRequestContext.getMessage("public-layout.logo.tagline", [], "", false)}</p>
                </aside>
            </div>
            <div class="span9">
                <div class="alignment clearfix">
                
                    <nav class="main">
                        <ul class="menu"><li class="first expanded active-trail"><a href="<@spring.url "/" />" title="">${springMacroRequestContext.getMessage("public-layout.for_researchers", [], "", false)}</a><ul class="menu">
                            <@security.authorize ifNotGranted="ROLE_USER">
                                <li class="leaf last"><a ${(nav=="signin")?string('class="active" ', '')} href="<@spring.url "/signin" />">${springMacroRequestContext.getMessage("public-layout.sign_in", [], "", false)}</a></li>
                                <li class="leaf first"><a ${(nav=="register")?string('class="active" ', '')}href="<@spring.url "/register" />">${springMacroRequestContext.getMessage("public-layout.register", [], "", false)}</a></li>
                            </@security.authorize>
                            <#assign isProxy = (profile.orcidBio.delegation.givenPermissionBy)?? && profile.orcidBio.delegation.givenPermissionBy.delegationDetails?size != 0>
                            <@security.authorize ifAnyGranted="ROLE_USER">
                                <li><a ${(nav=="record")?string('class="active" ', '')}href="<@spring.url '/my-orcid'/>">${springMacroRequestContext.getMessage("public-layout.my_orcid_record", [], "", false)}</a></li>
                                <li><a ${(nav=="settings")?string('class="active" ', '')}href="<@spring.url '/account'/>">${springMacroRequestContext.getMessage("public-layout.account_setting", [], "", false)}</a></li>
                                
                                <#--<#if isProxy>
                                    <li><a href="#proxy" class="colorbox-modal">${springMacroRequestContext.getMessage("public-layout.manage_proxy_account", [], "", false)}</a></li>
                                </#if>-->
                                <li><a href="<@spring.url '/signout'/>">${springMacroRequestContext.getMessage("public-layout.sign_out", [], "", false)}</a></li>
                            </@security.authorize>
</ul></li>
<li class="expanded"><a href="${aboutUri}/organizations?lang=${locale}">${springMacroRequestContext.getMessage("public-layout.for_organizations", [], "", false)}</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/organizations/funders?lang=${locale}">${springMacroRequestContext.getMessage("public-layout.funders", [], "", false)}</a></li>
<li class="leaf"><a href="${aboutUri}/organizations/institutions?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.research_organizations", [], "", false)}</a></li>
<li class="leaf"><a href="${aboutUri}/organizations/publishers?lang=${locale}">${springMacroRequestContext.getMessage("public-layout.publishers", [], "", false)}</a></li>
<li class="last leaf"><a href="${aboutUri}/organizations/integrators?lang=${locale}">${springMacroRequestContext.getMessage("public-layout.integrators", [], "", false)}</a></li>
</ul></li>
<li class="expanded"><a href="${aboutUri}/about?lang=${locale}">${springMacroRequestContext.getMessage("public-layout.about", [], "", false)}</a><ul class="menu"><li class="first expanded"><a href="${aboutUri}/about/what-is-orcid?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.what_is_orcid", [], "", false)}</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/what-is-orcid/mission?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.our_mission", [], "", false)}</a></li>
<li class="last leaf"><a href="${aboutUri}/about/what-is-orcid/our-principles?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.our_principles", [], "", false)}</a></li>
</ul></li>
<li class="leaf"><a href="${aboutUri}/about/team?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.the_orcid_team", [], "", false)}</a></li>
<li class="expanded"><a href="${aboutUri}/about/community?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.the_orcid_community", [], "", false)}</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/community?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.working_groups", [], "", false)}</a></li>
<li class="leaf"><a href="${aboutUri}/about/community/participants?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.participants", [], "", false)}</a></li>
<li class="leaf"><a href="${aboutUri}/about/community/sponsors?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.sponsors", [], "", false)}</a></li>
<li class="leaf"><a href="${aboutUri}/about/community/members?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.members", [], "", false)}</a></li>
<li class="last leaf"><a href="${aboutUri}/about/community/launch-partners?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.launch_partners", [], "", false)}</a></li>
</ul></li>
<li class="expanded"><a href="${aboutUri}/about/membership?lang=${locale}" title="">Membership</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/membership?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.membership_and_subscription", [], "", false)}</a></li>
<li class="leaf"><a href="${aboutUri}/about/membership/standard-member-agreement?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.standard_member_agreement", [], "", false)}</a></li>
<li class="last leaf"><a href="${aboutUri}/about/community/members?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.our_members", [], "", false)}</a></li>
</ul></li>
<li class="leaf"><a href="${aboutUri}/about/news/news?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.news", [], "", false)}</a>
<li class="last expanded"><a href="${aboutUri}/about/events?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.events", [], "", false)}</a>
<ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/news/news?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.news", [], "", false)}</a></li>
<li class="last leaf"><a href="${aboutUri}/about/news/docs?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.documentation", [], "", false)}</a></li>
</ul></li>
</ul></li>
<li class="expanded"><a href="${aboutUri}/help?lang=${locale}">${springMacroRequestContext.getMessage("public-layout.help", [], "", false)}</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/faq-page?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.faq", [], "", false)}</a></li>
<li class="leaf"><a href="${aboutUri}/help/contact-us?lang=${locale}" title="">${springMacroRequestContext.getMessage("public-layout.contact_us", [], "", false)}</a></li>
<li class="leaf"><a href="http://orcid.uservoice.com/forums/175591-general" title="">${springMacroRequestContext.getMessage("public-layout.give_feedback", [], "", false)}</a></li>
<li class="last leaf"><a href="http://orcid.uservoice.com/knowledgebase" title="">${springMacroRequestContext.getMessage("public-layout.knowledge_base", [], "", false)}Knowledge Base</a></li>
</ul></li>
<li class="last leaf">
    <@security.authorize ifNotGranted="ROLE_USER"><a href="<@spring.url "/signin" />" title="">${springMacroRequestContext.getMessage("public-layout.sign_in", [], "", false)}</a></@security.authorize>
    <@security.authorize ifAnyGranted="ROLE_USER"><a href="<@spring.url '/signout'/>">${springMacroRequestContext.getMessage("public-layout.sign_out", [], "", false)}</a></@security.authorize>
</li>
</ul>                    </nav>
    <#--<#if isProxy><#include "/common/change_proxy.ftl" /></#if>-->

                    
                </div>
            </div>
        </div>
        <#include '../common/maintenance_header.ftl'/>
    </header>
    <div id="main" role="main" class="main">
        <#nested>
    <#-- wtf --></div>
    </div>
    <footer class="footer">
        <div class="row">
            <div class="span11 offset1">
                <ul class="span11 offset1">
                    <li class=""><a href="${aboutUri}/help/contact-us?lang=${locale}">${springMacroRequestContext.getMessage("public-layout.contact_us", [], "", false)}</a></li>
				    <li class=""><a href="${aboutUri}/footer/privacy-policy?lang=${locale}">${springMacroRequestContext.getMessage("public-layout.privacy_policy", [], "", false)}</a></li>
				    <li class=""><a href="${aboutUri}/content/orcid-terms-use?lang=${locale}">${springMacroRequestContext.getMessage("public-layout.terms_of_use", [], "", false)}</a></li>
				    <li class=""><a href="${aboutUri}/open-source-license?lang=${locale}">${springMacroRequestContext.getMessage("footer.openSource", [], "", false)}</a></li>
                </ul>
            </div>
        </div>
    </footer>
<form action="<@spring.url '/'/>">
    <input id="imageUrl" type="hidden" value="${staticCdn}/images">
</form>
</@base>
</#macro>
