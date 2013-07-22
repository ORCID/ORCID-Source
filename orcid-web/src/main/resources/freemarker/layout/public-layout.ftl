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
<body data-baseurl="<@spring.url '/'/>" ng-app="orcidApp">
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
                        <input type="search" name="keys" placeholder="<@orcid.msg 'public-layout.search'/>" />
                        <fieldset class="search_options">
                            <input type="radio" name="huh_radio" id="filter_registry" value="registry" checked />
                            <label for="filter_registry"><@orcid.msg 'public-layout.search.choice.registry'/></label>
                            <input type="radio" name="huh_radio" id="filter_website" value="website" />
                            <label for="filter_website"><@orcid.msg 'public-layout.search.choice.website'/></label>
                        </fieldset>
                        <div class="conditions">
                            <p><@orcid.msg 'public-layout.search.terms1'/><a href="${aboutUri}/legal?lang=${locale}"><@orcid.msg 'public-layout.search.terms2'/></a><@orcid.msg 'public-layout.search.terms3'/></p>
                        </div>
                        <button type="submit" class="search-button"><i class="icon-orcid-search"></i></button>
                        <a href="<@spring.url "/orcid-search/search" />" class="settings-button" title="<@orcid.msg 'public-layout.search.advanced'/>"><i class="icon-cog"></i></a>
                    </form>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="span3 override">
           		<aside class="logo">
                    <h1><a href="${aboutUri}?lang=${locale}"><img src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo" /></a></h1>
                    <p><@orcid.msg 'public-layout.logo.tagline'/></p>
                </aside>
            </div>
            <div class="span9">
                <div class="alignment clearfix">
                
                    <nav class="main">
                        <ul class="menu"><li class="first expanded active-trail"><a href="<@spring.url "/" />" title=""><@orcid.msg 'public-layout.for_researchers'/></a><ul class="menu">
                            
                            <@security.authorize ifNotGranted="ROLE_USER">
                                <li class="leaf last"><a ${(nav=="signin")?string('class="active" ', '')} href="<@spring.url "/signin" />"><@orcid.msg 'public-layout.sign_in'/></a></li>
                                <li class="leaf first"><a ${(nav=="register")?string('class="active" ', '')}href="<@spring.url "/register" />"><@orcid.msg 'public-layout.register'/></a></li>
                            </@security.authorize>
                            
                            <#assign isProxy = (profile.orcidBio.delegation.givenPermissionBy)?? && profile.orcidBio.delegation.givenPermissionBy.delegationDetails?size != 0>
                            <@security.authorize ifAnyGranted="ROLE_USER">
                                <li><a ${(nav=="record")?string('class="active" ', '')}href="<@spring.url '/my-orcid'/>"><@orcid.msg 'public-layout.my_orcid_record'/></a></li>
                                <li><a ${(nav=="settings")?string('class="active" ', '')}href="<@spring.url '/account'/>"><@orcid.msg 'public-layout.account_setting'/></a></li>
                                
                                <#--<#if isProxy>
                                    <li><a href="#proxy" class="colorbox-modal"><@orcid.msg 'public-layout.manage_proxy_account'/></a></li>
                                </#if>-->
                                <li><a href="<@spring.url '/signout'/>"><@orcid.msg 'public-layout.sign_out'/></a></li>
                            </@security.authorize>
						</ul></li>
						<li class="expanded"><a href="${aboutUri}/organizations?lang=${locale}"><@orcid.msg 'public-layout.for_organizations'/></a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/organizations/funders?lang=${locale}"><@orcid.msg 'public-layout.funders'/></a></li>
						<li class="leaf"><a href="${aboutUri}/organizations/institutions?lang=${locale}" title=""><@orcid.msg 'public-layout.research_organizations'/></a></li>
						<li class="leaf"><a href="${aboutUri}/organizations/publishers?lang=${locale}"><@orcid.msg 'public-layout.publishers'/></a></li>
						<li class="last leaf"><a href="${aboutUri}/organizations/integrators?lang=${locale}"><@orcid.msg 'public-layout.integrators'/></a></li>
						</ul></li>
						<li class="expanded"><a href="${aboutUri}/about?lang=${locale}"><@orcid.msg 'public-layout.about'/></a><ul class="menu"><li class="first expanded"><a href="${aboutUri}/about/what-is-orcid?lang=${locale}" title=""><@orcid.msg 'public-layout.what_is_orcid'/></a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/what-is-orcid/mission?lang=${locale}" title=""><@orcid.msg 'public-layout.our_mission'/></a></li>
						<li class="last leaf"><a href="${aboutUri}/about/what-is-orcid/our-principles?lang=${locale}" title=""><@orcid.msg 'public-layout.our_principles'/></a></li>
						</ul></li>
						<li class="leaf"><a href="${aboutUri}/about/team?lang=${locale}" title=""><@orcid.msg 'public-layout.the_orcid_team'/></a></li>
						<li class="expanded"><a href="${aboutUri}/about/community?lang=${locale}" title=""><@orcid.msg 'public-layout.the_orcid_community'/></a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/community?lang=${locale}" title=""><@orcid.msg 'public-layout.working_groups'/></a></li>
						<li class="leaf"><a href="${aboutUri}/about/community/participants?lang=${locale}" title=""><@orcid.msg 'public-layout.participants'/></a></li>
						<li class="leaf"><a href="${aboutUri}/about/community/sponsors?lang=${locale}" title=""><@orcid.msg 'public-layout.sponsors'/></a></li>
						<li class="leaf"><a href="${aboutUri}/about/community/members?lang=${locale}" title=""><@orcid.msg 'public-layout.members'/></a></li>
						<li class="last leaf"><a href="${aboutUri}/about/community/launch-partners?lang=${locale}" title=""><@orcid.msg 'public-layout.launch_partners'/></a></li>
						</ul></li>
						<li class="expanded"><a href="${aboutUri}/about/membership?lang=${locale}" title="">Membership</a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/membership?lang=${locale}" title=""><@orcid.msg 'public-layout.membership_and_subscription'/></a></li>
						<li class="leaf"><a href="${aboutUri}/about/membership/standard-member-agreement?lang=${locale}" title=""><@orcid.msg 'public-layout.standard_member_agreement'/></a></li>
						<li class="last leaf"><a href="${aboutUri}/about/community/members?lang=${locale}" title=""><@orcid.msg 'public-layout.our_members'/></a></li>
						</ul></li>
						<li class="leaf"><a href="${aboutUri}/about/news/news?lang=${locale}" title=""><@orcid.msg 'public-layout.news'/></a>
						<li class="last expanded"><a href="${aboutUri}/about/events?lang=${locale}" title=""><@orcid.msg 'public-layout.events'/></a>
						<ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/news/news?lang=${locale}" title=""><@orcid.msg 'public-layout.news'/></a></li>
						<li class="last leaf"><a href="${aboutUri}/about/news/docs?lang=${locale}" title=""><@orcid.msg 'public-layout.documentation'/></a></li>
						</ul></li>
						</ul></li>
						<li class="expanded"><a href="${aboutUri}/help?lang=${locale}"><@orcid.msg 'public-layout.help'/></a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/faq-page?lang=${locale}" title=""><@orcid.msg 'public-layout.faq'/></a></li>
						<li class="leaf"><a href="${aboutUri}/help/contact-us?lang=${locale}" title=""><@orcid.msg 'public-layout.contact_us'/></a></li>
						<li class="leaf"><a href="http://orcid.uservoice.com/forums/175591-general" title=""><@orcid.msg 'public-layout.give_feedback'/></a></li>
						<li class="last leaf"><a href="http://orcid.uservoice.com/knowledgebase" title=""><@orcid.msg 'public-layout.knowledge_base'/>Knowledge Base</a></li>
						</ul></li>
						<li class="last leaf">
						    <@security.authorize ifNotGranted="ROLE_USER"><a href="<@spring.url "/signin" />" title=""><@orcid.msg 'public-layout.sign_in'/></a></@security.authorize>
						    <@security.authorize ifAnyGranted="ROLE_USER"><a href="<@spring.url '/signout'/>"><@orcid.msg 'public-layout.sign_out'/></a></@security.authorize>
						</li>
					</ul>
				</nav>
    <#--<#if isProxy><#include "/common/change_proxy.ftl" /></#if>-->

                    
                </div>
            </div>
			<span ng-controller="statisticCtrl" class="see-more ng-cloak">{{liveIds|number}} <@orcid.msg 'public-layout.amount_ids'/> <a href="<@spring.url "/statistics" />" title=""><@orcid.msg 'public-layout.see_more'/></a></span>
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
                    <li class=""><a href="${aboutUri}/help/contact-us?lang=${locale}"><@orcid.msg 'public-layout.contact_us'/></a></li>
				    <li class=""><a href="${aboutUri}/footer/privacy-policy?lang=${locale}"><@orcid.msg 'public-layout.privacy_policy'/></a></li>
				    <li class=""><a href="${aboutUri}/content/orcid-terms-use?lang=${locale}"><@orcid.msg 'public-layout.terms_of_use'/></a></li>
				    <li class=""><a href="${aboutUri}/open-source-license?lang=${locale}"><@orcid.msg 'footer.openSource'/></a></li>
                </ul>
            </div>
        </div>
    </footer>
<form action="<@spring.url '/'/>">
    <input id="imageUrl" type="hidden" value="${staticCdn}/images">
</form>
</@base>
</#macro>
