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
<html class="no-js oldie ng-app:orcidApp"  ng-app="orcidApp" id="ng-app" lang="en">
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
    <div class="header center">
        <div class="row">
            <div class="span11 offset1">
                <div class="search">
                    <form id="form-search" action="${aboutUri}/search/node" method="POST">
                        <input type="search" name="keys" placeholder="<@orcid.msg 'public-layout.search'/>" />
                        <fieldset class="search_options">
                            <input type="radio" name="huh_radio" id="filter_registry" value="registry" checked />
                            <label for="filter_registry"><@orcid.msg 'public-layout.search.choice.registry'/></label>
                            <input type="radio" name="huh_radio" id="filter_website" value="website" />
                            <label for="filter_website"><@orcid.msg 'public-layout.search.choice.website'/></label>
                        </fieldset>
                        <div class="conditions">
                            <p><@orcid.msg 'public-layout.search.terms1'/><a href="${aboutUri}/legal"><@orcid.msg 'public-layout.search.terms2'/></a><@orcid.msg 'public-layout.search.terms3'/></p>
                        </div>
                        <button type="submit" class="search-button"><i class="icon-orcid-search"></i></button>
                        <a href="<@spring.url "/orcid-search/search" />" class="settings-button" title="<@orcid.msg 'public-layout.search.advanced'/>"><i class="icon-cog"></i></a>
                    </form>
	                <div class="language-selector" ng-controller="languageCtrl" id="languageCtrl">
					    <form id="language-form" action="#">
					    	<select name="language-codes" id="language-codes" ng-model="language" ng-options="language.label for language in languages" ng-change="selectedLanguage()"></select>
					    </form>
					</div>     
                </div>
            </div>
        </div>
        <div class="row">
            <div class="span3 override">
           		<div class="logo">
                    <h1><a href="${aboutUri}"><img src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo" /></a></h1>
                    <p><@orcid.msg 'public-layout.logo.tagline'/></p>
                </div>
            </div>
            <div class="span9">
                <div class="alignment clearfix">                
                    <div class="main">
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
						<li class="expanded"><a href="${aboutUri}/organizations"><@orcid.msg 'public-layout.for_organizations'/></a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/organizations/funders"><@orcid.msg 'public-layout.funders'/></a></li>
						<li class="leaf"><a href="${aboutUri}/organizations/institutions" title=""><@orcid.msg 'public-layout.research_organizations'/></a></li>
						<li class="leaf"><a href="${aboutUri}/organizations/publishers"><@orcid.msg 'public-layout.publishers'/></a></li>
						<li class="last leaf"><a href="${aboutUri}/organizations/integrators"><@orcid.msg 'public-layout.integrators'/></a></li>
						</ul></li>
						<li class="expanded"><a href="${aboutUri}/about"><@orcid.msg 'public-layout.about'/></a><ul class="menu"><li class="first expanded"><a href="${aboutUri}/about/what-is-orcid" title=""><@orcid.msg 'public-layout.what_is_orcid'/></a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/what-is-orcid/mission" title=""><@orcid.msg 'public-layout.our_mission'/></a></li>
						<li class="last leaf"><a href="${aboutUri}/about/what-is-orcid/our-principles" title=""><@orcid.msg 'public-layout.our_principles'/></a></li>
						</ul></li>
						<li class="leaf"><a href="${aboutUri}/about/team" title=""><@orcid.msg 'public-layout.the_orcid_team'/></a></li>
						<li class="expanded"><a href="${aboutUri}/about/community" title=""><@orcid.msg 'public-layout.the_orcid_community'/></a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/community" title=""><@orcid.msg 'public-layout.working_groups'/></a></li>
						<li class="leaf"><a href="${aboutUri}/about/community/participants" title=""><@orcid.msg 'public-layout.participants'/></a></li>
						<li class="leaf"><a href="${aboutUri}/about/community/sponsors" title=""><@orcid.msg 'public-layout.sponsors'/></a></li>
						<li class="leaf"><a href="${aboutUri}/about/community/members" title=""><@orcid.msg 'public-layout.members'/></a></li>
						<li class="last leaf"><a href="${aboutUri}/about/community/launch-partners" title=""><@orcid.msg 'public-layout.launch_partners'/></a></li>
						</ul></li>
						<li class="expanded"><a href="${aboutUri}/about/membership" title=""><@orcid.msg 'public-layout.membership'/></a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/membership" title=""><@orcid.msg 'public-layout.membership_and_subscription'/></a></li>
						<li class="leaf"><a href="${aboutUri}/about/membership/standard-member-agreement" title=""><@orcid.msg 'public-layout.standard_member_agreement'/></a></li>
						<li class="last leaf"><a href="${aboutUri}/about/community/members" title=""><@orcid.msg 'public-layout.our_members'/></a></li>
						</ul></li>
						<li class="leaf"><a href="${aboutUri}/about/news/news" title=""><@orcid.msg 'public-layout.news'/></a>
						<li class="last expanded"><a href="${aboutUri}/about/events" title=""><@orcid.msg 'public-layout.events'/></a>
						<ul class="menu"><li class="first leaf"><a href="${aboutUri}/about/news/news" title=""><@orcid.msg 'public-layout.news'/></a></li>
						<li class="last leaf"><a href="${aboutUri}/about/news/docs" title=""><@orcid.msg 'public-layout.documentation'/></a></li>
						</ul></li>
						</ul></li>
						<li class="expanded"><a href="${aboutUri}/help"><@orcid.msg 'public-layout.help'/></a><ul class="menu"><li class="first leaf"><a href="${aboutUri}/faq-page" title=""><@orcid.msg 'public-layout.faq'/></a></li>
						<li class="leaf"><a href="${aboutUri}/help/contact-us" title=""><@orcid.msg 'public-layout.contact_us'/></a></li>
						<li class="leaf"><a href="http://orcid.uservoice.com/forums/175591-general" title=""><@orcid.msg 'public-layout.give_feedback'/></a></li>
						<li class="last leaf"><a href="http://orcid.uservoice.com/knowledgebase" title=""><@orcid.msg 'public-layout.knowledge_base'/></a></li>
						</ul></li>
						<li class="last leaf">
						    <@security.authorize ifNotGranted="ROLE_USER"><a href="<@spring.url "/signin" />" title=""><@orcid.msg 'public-layout.sign_in'/></a></@security.authorize>
						    <@security.authorize ifAnyGranted="ROLE_USER"><a href="<@spring.url '/signout'/>"><@orcid.msg 'public-layout.sign_out'/></a></@security.authorize>
						</li>
					</ul>
				</div>
    <#--<#if isProxy><#include "/common/change_proxy.ftl" /></#if>-->

                    
                </div>
            </div>
			<span class="see-more">${liveIds} <@orcid.msg 'public-layout.amount_ids'/> <a href="<@spring.url "/statistics" />" title=""><@orcid.msg 'public-layout.see_more'/></a></span>
        </div>
        
        <#include '../common/maintenance_header.ftl'/>
    </div>
    <div id="main" role="main" class="main">
        <#nested>
    <#-- wtf --></div>
    </div>
    <div class="footer">
        <div class="row">
            <div class="span11 offset1">
                <ul class="span11 offset1">
                    <li class=""><a href="${aboutUri}/help/contact-us"><@orcid.msg 'public-layout.contact_us'/></a></li>
				    <li class=""><a href="${aboutUri}/footer/privacy-policy"><@orcid.msg 'public-layout.privacy_policy'/></a></li>
				    <li class=""><a href="${aboutUri}/content/orcid-terms-use"><@orcid.msg 'public-layout.terms_of_use'/></a></li>
				    <li class=""><a href="${aboutUri}/open-source-license"><@orcid.msg 'footer.openSource'/></a></li>
                </ul>
            </div>
        </div>
    </div>
<form action="<@spring.url '/'/>">
    <input id="imageUrl" type="hidden" value="${staticCdn}/images">
</form>
</@base>
</#macro>
