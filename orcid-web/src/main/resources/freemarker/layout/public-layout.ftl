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
                    <form id="form-search" action="${aboutUri}/search/node" method="POST">
                        <input type="search" name="keys" placeholder="${springMacroRequestContext.getMessage("public-layout.search")}" />
                        <fieldset class="search_options">
                            <input type="radio" name="huh_radio" id="filter_registry" value="${springMacroRequestContext.getMessage('layout.public-layout.registry')}" checked />
                            <label for="filter_registry">${springMacroRequestContext.getMessage("public-layout.search.choice.registry")}</label>
                            <input type="radio" name="huh_radio" id="filter_website" value="${springMacroRequestContext.getMessage('layout.public-layout.website')}" />
                            <label for="filter_website">${springMacroRequestContext.getMessage("public-layout.search.choice.website")}</label>
                        </fieldset>
                        <div class="conditions">
                            <p>${springMacroRequestContext.getMessage("public-layout.search.terms1")}<a href="${aboutUri}/legal">${springMacroRequestContext.getMessage("public-layout.search.terms2")}</a>${springMacroRequestContext.getMessage("public-layout.search.terms3")}</p>
                        </div>
                        <button type="submit" href="#" class="search-button"><i class="icon-orcid-search"></i></button>
                        <a href="<@spring.url "/orcid-search/search" />" class="settings-button" title="${springMacroRequestContext.getMessage("public-layout.search.advanced")}"><i class="icon-cog"></i></a>
                    </form>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="span3 override">
                <aside class="logo">
                    <h1><a href="${aboutUri}"><img src="<@spring.url '/static/img/orcid-logo.png'/>" alt="ORCID logo" /></a></h1>
                    <p>${springMacroRequestContext.getMessage("public-layout.logo.tagline")}</p>
                </aside>
            </div>
            <div class="span9">
                <div class="alignment clearfix">
                
                    <nav class="main">
                        <ul class="menu"><li class="first expanded active-trail"><a href="<@spring.url "/" />" title="">${springMacroRequestContext.getMessage("public-layout.for_researchers")}</a><ul class="menu">
                            <@security.authorize ifNotGranted="ROLE_USER">
                                <li class="leaf last"><a ${(nav=="signin")?string('class="active" ', '')} href="<@spring.url "/signin" />">${springMacroRequestContext.getMessage("public-layout.sign_in")}</a></li>
                                <li class="leaf first"><a ${(nav=="register")?string('class="active" ', '')}href="<@spring.url "/register" />">${springMacroRequestContext.getMessage("public-layout.register")}</a></li>
                            </@security.authorize>
                            <#assign isProxy = (profile.orcidBio.delegation.givenPermissionBy)?? && profile.orcidBio.delegation.givenPermissionBy.delegationDetails?size != 0>
                            <@security.authorize ifAnyGranted="ROLE_USER">
                                <li><a ${(nav=="record")?string('class="active" ', '')}href="<@spring.url '/my-orcid'/>">${springMacroRequestContext.getMessage("public-layout.my_orcid_record")}</a></li>
                                <li><a ${(nav=="settings")?string('class="active" ', '')}href="<@spring.url '/account'/>">${springMacroRequestContext.getMessage("public-layout.account_setting")}</a></li>
                                
                                <#--<#if isProxy>
                                    <li><a href="#proxy" class="colorbox-modal">${springMacroRequestContext.getMessage("public-layout.manage_proxy_account")}</a></li>
                                </#if>-->
                                <li><a href="<@spring.url '/signout'/>">${springMacroRequestContext.getMessage("public-layout.sign_out")}</a></li>
                            </@security.authorize>
</ul></li>
<li class="expanded"><a href="${aboutUri}/organizations">${springMacroRequestContext.getMessage("public-layout.for_organizations")}</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/organizations/funders">${springMacroRequestContext.getMessage("public-layout.funders")}</a></li>
<li class="leaf"><a href="${aboutUri}/organizations/institutions" title="">${springMacroRequestContext.getMessage("public-layout.research_organizations")}</a></li>
<li class="leaf"><a href="${aboutUri}/organizations/publishers">${springMacroRequestContext.getMessage("public-layout.publishers")}</a></li>
<li class="last leaf"><a href="${aboutUri}/organizations/integrators">${springMacroRequestContext.getMessage("public-layout.integrators")}</a></li>
</ul></li>
<li class="expanded"><a href="${aboutUri}/about">${springMacroRequestContext.getMessage("public-layout.about")}</a><ul class="menu"><li class="first expanded"><a href="${aboutUri}/about/what-is-orcid" title="">${springMacroRequestContext.getMessage("public-layout.what_is_orcid")}</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/what-is-orcid/mission" title="">${springMacroRequestContext.getMessage("public-layout.our_mission")}</a></li>
<li class="last leaf"><a href="${aboutUri}/about/what-is-orcid/our-principles" title="">${springMacroRequestContext.getMessage("public-layout.our_principles")}</a></li>
</ul></li>
<li class="leaf"><a href="${aboutUri}/about/team" title="">${springMacroRequestContext.getMessage("public-layout.the_orcid_team")}</a></li>
<li class="expanded"><a href="${aboutUri}/about/community" title="">${springMacroRequestContext.getMessage("public-layout.the_orcid_community")}</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/community" title="">${springMacroRequestContext.getMessage("public-layout.working_groups")}</a></li>
<li class="leaf"><a href="${aboutUri}/about/community/participants" title="">${springMacroRequestContext.getMessage("public-layout.participants")}</a></li>
<li class="leaf"><a href="${aboutUri}/about/community/sponsors" title="">${springMacroRequestContext.getMessage("public-layout.sponsors")}</a></li>
<li class="leaf"><a href="${aboutUri}/about/community/members" title="">${springMacroRequestContext.getMessage("public-layout.members")}</a></li>
<li class="last leaf"><a href="${aboutUri}/about/community/launch-partners" title="">${springMacroRequestContext.getMessage("public-layout.launch_partners")}</a></li>
</ul></li>
<li class="expanded"><a href="${aboutUri}/about/membership" title="">Membership</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/membership" title="">${springMacroRequestContext.getMessage("public-layout.membership_and_subscription")}</a></li>
<li class="leaf"><a href="${aboutUri}/about/membership/standard-member-agreement" title="">${springMacroRequestContext.getMessage("public-layout.standard_member_agreement")}</a></li>
<li class="last leaf"><a href="${aboutUri}/about/community/members" title="">${springMacroRequestContext.getMessage("public-layout.our_members")}</a></li>
</ul></li>
<li class="last expanded"><a href="${aboutUri}/about/news/news" title="">${springMacroRequestContext.getMessage("public-layout.news")}</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/news/news" title="">${springMacroRequestContext.getMessage("public-layout.news")}</a></li>
<li class="last leaf"><a href="${aboutUri}/about/news/docs" title="">${springMacroRequestContext.getMessage("public-layout.documentation")}</a></li>
</ul></li>
</ul></li>
<li class="expanded"><a href="${aboutUri}/help">${springMacroRequestContext.getMessage("public-layout.help")}</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/faq-page" title="">${springMacroRequestContext.getMessage("public-layout.faq")}</a></li>
<li class="leaf"><a href="${aboutUri}/help/contact-us" title="">${springMacroRequestContext.getMessage("public-layout.contact_us")}</a></li>
<li class="leaf"><a href="http://orcid.uservoice.com/forums/175591-general" title="">${springMacroRequestContext.getMessage("public-layout.give_feedback")}</a></li>
<li class="last leaf"><a href="http://orcid.uservoice.com/knowledgebase" title="">${springMacroRequestContext.getMessage("public-layout.knowledge_base")}Knowledge Base</a></li>
</ul></li>
<li class="last leaf">
    <@security.authorize ifNotGranted="ROLE_USER"><a href="<@spring.url "/signin" />" title="">${springMacroRequestContext.getMessage("public-layout.sign_in")}</a></@security.authorize>
    <@security.authorize ifAnyGranted="ROLE_USER"><a href="<@spring.url '/signout'/>">${springMacroRequestContext.getMessage("public-layout.sign_out")}</a></@security.authorize>
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
                    <li class=""><a href="${aboutUri}/help/contact-us">${springMacroRequestContext.getMessage("public-layout.contact_us")}</a></li>
				    <li class=""><a href="${aboutUri}/footer/privacy-policy">${springMacroRequestContext.getMessage("public-layout.privacy_policy")}</a></li>
				    <li class=""><a href="${aboutUri}/content/orcid-terms-use">${springMacroRequestContext.getMessage("public-layout.terms_of_use")}</a></li>
                </ul>
            </div>
        </div>
    </footer>
<form action="<@spring.url '/'/>">
    <input id="imageUrl" type="hidden" value="<@spring.url '/static/images'/>">
</form>
</@base>
</#macro>
