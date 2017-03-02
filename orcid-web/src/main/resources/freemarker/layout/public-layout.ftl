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
<#assign verDateTime = startupDate?datetime>
<#macro base ver="${verDateTime?iso_utc}">
<!DOCTYPE html>
<html class="no-js oldie" lang="en">
<#include "/common/html-head.ftl" />
<body data-baseurl="<@orcid.rootPath '/'/>">
<#nested />
<#include "/common/scripts.ftl" />
<#if !hideUserVoiceScript??> 
	<#include "/common/user_voice_script.ftl" />
</#if>
</body>
</html>
</#macro>
<#macro nav></#macro>
<#macro footer></#macro>
<#macro public css=[] js=[] classes=[] other=[] nav="" >
<@base>
<!--<#if devSandboxUrl != ''>
	<div class="dev-watermark"></div>
</#if>-->
<div class="container">
	<!--<div id="pi-banner" style="position: absolute;">
				<svg height="250" width="100">
					<polygon points="0,0 0,200 50,250 100,200 100,0" style="fill:#338caf;" />
					Sorry, your browser does not support inline SVG.
				</svg>
			</div>-->
	<div class="header center" ng-controller="headerCtrl">
		
		<div class="row">
			
			
			<div class="search col-md-11 col-md-offset-1 col-sm-12 col-xs-12"
				id="search" ng-show="searchVisible == true || settingsVisible == true" ng-cloak>
				
				
				<!-- Search Form  -->				
				<form id="form-search" action='<@orcid.rootPath "/search/node" />' method="POST" ng-show="searchVisible == true" ng-cloak>
					<div id="search-box">
						<input type="search" id="search-input" name="keys"
							ng-focus="searchFocus()" ng-blur="searchBlur()"
							placeholder="<@orcid.msg 'public-layout.search'/>" />
					</div>

					<div class="bar">
						<fieldset class="search_options" ng-show="filterActive == true"
							ng-cloak>
							<input type="radio" name="huh_radio" id="filter_registry"
								value="registry" ng-click="focusActive()" checked /> <label
								for="filter_registry"><@orcid.msg
								'public-layout.search.choice.registry'/></label> <input type="radio"
								name="huh_radio" id="filter_website" value="website"
								ng-click="focusActive()" /> <label for="filter_website"><@orcid.msg
								'public-layout.search.choice.website'/></label>
						</fieldset>
					</div>


					<div class="conditions" ng-show="conditionsActive == true" ng-cloak>
						<p>							
							<@orcid.msg 'public-layout.search.terms1'/><a
								href="${aboutUri}/legal"><@orcid.msg
								'public-layout.search.terms2'/></a><@orcid.msg
							'public-layout.search.terms3'/>
						</p>
					</div>

					<div class="top-buttons">
						<button type="submit" class="search-button">
							<i class="icon-orcid-search"></i>
						</button>
						<a href="<@orcid.rootPath "/orcid-search/search" />"
						class="settings-button" title="<@orcid.msg
						'public-layout.search.advanced'/>"><i class="glyphicon glyphicon-cog"></i></a>
					</div>
				</form>				
				
				<!-- Language -->
				<div class="language-selector" ng-show = "settingsVisible == true">
					<!-- Shared component -->
					<div ng-include="'edit-language'"></div>
					
					<div class="account-settings-mobile-menu">
						<span class="account-settings-mobile"> 
							<a ${(nav=="settings")?string('class="active"', '')}href="<@orcid.rootPath '/account'/>">
								<@orcid.msg 'public-layout.account_setting'/>
							</a>
						</span>
					</div>
				</div>
			</div>


		</div>
		<!-- .row -->
		<div class="row">			
			<#if ((isPublicProfile)?? && isPublicProfile == true | (locked)?? && locked | (deprecated)?? && deprecated) && (!(RequestParameters['publicRecordMenu']??) | RequestParameters['publicRecordMenu'] != 'false')>	
				<div class="col-md-9 col-sm-9 col-sm-push-3 col-md-push-3 navigation public">
					
					<!-- Mobile View -->
					<a id="mobile-menu-icon" class="mobile-button mobile-menu-icon hidden-md hidden-lg hidden-sm visible-xs"
					   ng-click="toggleMenu()" ng-class="{'mobile-menu-active': menuVisible == true}">
						<span class="glyphicon glyphicon-align-justify"></span>
					</a>								
					<a href="${aboutUri}" id="logo-mini" class="pull-left"></a>
					<a id="mobile-settings" class="mobile-button mobile-settings" ng-click="toggleSettings()" ng-class="{'mobile-menu-active': settingsVisible == true}">
						<span class="glyphicon glyphicon-cog"></span>
					</a>
					<a id="mobile-search" class="mobile-button mobile-search" ng-click="toggleSearch()" ng-class="{'mobile-menu-active': searchVisible == true}">
						<span class="glyphicon glyphicon-search"></span>
					</a>
					<a href="<@orcid.rootPath " signin" />" id="mobile-sign-in" class="mobile-button mobile-sign-in">
						<span class="glyphicon glyphicon-user"></span>
					</a>
					
					
					<!-- Desktop / Tablet View -->
					<ul class="menu public" ng-show="menuVisible == true" resize>
						<li class="active-trail"><a href="<@orcid.rootPath "/my-orcid" />">Edit your record</a></li>
						<li><a href="<@orcid.rootPath "/about" />">About ORCID</a></li>
						<li><a href="<@orcid.rootPath "/contact-us" />">Contact us</a></li>
						<li><a href="<@orcid.rootPath "/help" />">Help</a></li>
					</ul>
				
			<#else>
				<div class="col-md-9 col-sm-9 col-sm-push-3 col-md-push-3 navigation">
					<!--  Mobile menu -->				
					<a id="mobile-menu-icon" class="mobile-button mobile-menu-icon hidden-md hidden-lg hidden-sm visible-xs"
					   ng-click="toggleMenu()" ng-class="{'mobile-menu-active': menuVisible == true}">
						<span class="glyphicon glyphicon-align-justify"></span>
					</a>								
					<a href="${aboutUri}" id="logo-mini" class="pull-left"></a>
					<a id="mobile-settings" class="mobile-button mobile-settings" ng-click="toggleSettings()" ng-class="{'mobile-menu-active': settingsVisible == true}">
						<span class="glyphicon glyphicon-cog"></span>
					</a>
					<a id="mobile-search" class="mobile-button mobile-search" ng-click="toggleSearch()" ng-class="{'mobile-menu-active': searchVisible == true}">
						<span class="glyphicon glyphicon-search"></span>
					</a>
					<a href="<@orcid.rootPath " signin" />" id="mobile-sign-in" class="mobile-button mobile-sign-in">
						<span class="glyphicon glyphicon-user"></span>
					</a>				
					
					<!--  Desktop / Tablet menu -->				
					<ul class="menu" ng-show="menuVisible == true" ng-cloak resize>
						<!-- FOR RESEARCHERS -->
						<li class="first expanded active-trail">
							<a href="${aboutUri}/about/what-is-orcid/mission" ng-click="handleMobileMenuOption($event)" title=""><@orcid.msg
							'public-layout.for_researchers'/></a>
							<ul class="menu lang-fixes">
							<!-- Mobile view Only -->
							<li class="leaf hidden-md hidden-lg hidden-sm visible-xs"><a href="<@orcid.rootPath "/" />" title=""><@orcid.msg 'public-layout.for_researchers'/></a></li>
							
							<@security.authorize ifNotGranted="ROLE_USER, ROLE_ADMIN, ROLE_BASIC, ROLE_PREMIUM, ROLE_BASIC_INSTITUTION, ROLE_PREMIUM_INSTITUTION">								
								<li class="leaf last"><a ${(nav=="signin")?string('class="active" ', '')} href="<@orcid.rootPath "/signin" />"><@orcid.msg 'public-layout.sign_in'/></a></li>									
								<li class="leaf last"><a ${(nav=="register")?string('class="active" ', '')} href="<@orcid.rootPath "/register" />"><@orcid.msg 'public-layout.register'/></a></li>
							</@security.authorize>
							<@security.authorize ifAnyGranted="ROLE_USER, ROLE_ADMIN, ROLE_BASIC, ROLE_PREMIUM, ROLE_BASIC_INSTITUTION, ROLE_PREMIUM_INSTITUTION">								
								<li><a ${(nav=="record")?string('class="active" ', '')}href="<@orcid.rootPath '/my-orcid'/>">
									<#if inDelegationMode><@orcid.msg 'public-layout.my_orcid'/><#else><@orcid.msg 'public-layout.my_orcid_record'/></#if>
								</a></li>
							<@security.authorize ifAnyGranted="ROLE_USER, ROLE_ADMIN, ROLE_BASIC, ROLE_PREMIUM, ROLE_BASIC_INSTITUTION, ROLE_PREMIUM_INSTITUTION">
							<li ng-controller="NotificationsCountCtrl">
								<a ${(nav=="notifications")?string('class="active" ', '')}href="<@orcid.rootPath "/inbox" />">${springMacroRequestContext.getMessage("workspace.notifications")} <span ng-cloak ng-hide="getUnreadCount() === 0">({{getUnreadCount()}})</span></a>
							</li>
							</@security.authorize>
							
							<li><a ${(nav=="settings")?string('class="active" ', '')}href="<@orcid.rootPath '/account'/>" id="accountSettingMenuLink"><@orcid.msg 'public-layout.account_setting'/></a></li>
							
							<#if !inDelegationMode || isDelegatedByAdmin>
								<@security.authorize ifAnyGranted="ROLE_GROUP, ROLE_BASIC, ROLE_BASIC_INSTITUTION, ROLE_PREMIUM, ROLE_PREMIUM_INSTITUTION">
				 					<li><a ${(nav=="developer-tools")?string('class="active" ', '')}href="<@orcid.rootPath "/group/developer-tools" />">${springMacroRequestContext.getMessage("workspace.developer_tools")}</a></li>
								</@security.authorize>
								<@security.authorize ifAnyGranted="ROLE_USER">
									<li><a ${(nav=="developer-tools")?string('class="active" ', '')}href="<@orcid.rootPath "/developer-tools" />">${springMacroRequestContext.getMessage("workspace.developer_tools")}</a></li>
								</@security.authorize>
							</#if>
							
							<@security.authorize ifAnyGranted="ROLE_ADMIN">
								<li><a ${(nav=="members")?string('class="active" ', '')}href="<@orcid.rootPath "/manage-members" />"><@orcid.msg 'admin.members.workspace_link' /></a></li>
								<li><a ${(nav=="admin")?string('class="active" ', '')}href="<@orcid.rootPath "/admin-actions" />"><@orcid.msg 'admin.workspace_link' /></a></li>
							</@security.authorize>
																	
							</@security.authorize>
								<li class="leaf last"><a href="<@orcid.rootPath "/content/initiative" />"><@orcid.msg 'manage_delegators.learn_more.link.text' /></a></li>
							</ul>
						</li>
	
						<!-- DRUPAL WEBSITE MENUS -->
						<!-- FOR ORGANIZATIONS -->
						<li class="expanded">
							<a href="${aboutUri}/organizations" ng-click="handleMobileMenuOption($event)"><@orcid.msg 'public-layout.for_organizations'/></a>
							<ul class="menu lang-fixes">
								<!-- Mobile view Only -->
								<li class="first leaf hidden-md hidden-lg hidden-sm visible-xs">
									<a href="${aboutUri}/organizations"><@orcid.msg 'public-layout.for_organizations'/></a>
								</li>
	
								<li class="first leaf">
									<a href="${aboutUri}/organizations/funders" class="russian-fix" ng-click="handleMobileMenuOption($event); toggleSecondaryMenu('funders')"><@orcid.msg 'public-layout.funders'/><span class="more" ng-class="{'less':secondaryMenuVisible['funders'] == true}"></span></a> <!-- Updated according Drupal website structure -->
									<ul class="menu" ng-show="secondaryMenuVisible['funders'] == true">
										<li class="hidden-sm hidden-md hidden-lg">
											<a href="${aboutUri}/organizations/funders"><@orcid.msg 'public-layout.funders'/></a>
										</li>
										<li class="first leaf">
											<a href="${aboutUri}/organizations/funders/learnmore">Learn more</a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/organizations/funders/outreachresources">Outreach Resources</a>
										</li>
										<li class="last leaf">
											<a href="${aboutUri}/about/membership" title="">Membership</a>
										</li>
									</ul>
								</li>
								<li class="leaf">
									<a href="${aboutUri}/organizations/institutions" ng-click="handleMobileMenuOption($event); toggleSecondaryMenu('institutions')"><@orcid.msg 'public-layout.research_organizations'/><span class="more" ng-class="{'less':secondaryMenuVisible['institutions'] == true}"></span></a> <!-- Updated according Drupal website structure -->
									<ul class="menu" ng-show="secondaryMenuVisible['institutions'] == true">
										<li class="hidden-sm hidden-md hidden-lg">
											<a href="${aboutUri}/organizations/institutions"><@orcid.msg 'public-layout.research_organizations'/></a>
										</li>
										<li class="first leaf">
											<a href="${aboutUri}/organizations/institutions/learnmore">Learn more</a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/organizations/institutions/outreachresources">Outreach Resources</a>
										</li>
											<li class="leaf"><a href="${aboutUri}/about/membership" title="">Membership</a>
										</li>
										<li class="last leaf">
											<a href="${aboutUri}/organizations/institutions/usecases">Use cases</a>
										</li>
									</ul>
								</li>
								<li class="leaf">
									<a href="${aboutUri}/organizations/publishers" ng-click="handleMobileMenuOption($event); toggleSecondaryMenu('publishers')"> <@orcid.msg 'public-layout.publishers'/><span class="more" ng-class="{'less':secondaryMenuVisible['publishers'] == true}"></span></a> <!-- Updated according Drupal website structure -->
									<ul class="menu" ng-show="secondaryMenuVisible['publishers'] == true">
										<li class="hidden-sm hidden-md hidden-lg">
											<a href="${aboutUri}/organizations/publishers"> <@orcid.msg 'public-layout.publishers'/></a>
										</li>
										<li class="first leaf">
											<a href="${aboutUri}/organizations/publishers/learnmore">Learn more</a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/organizations/publishers/outreachresources">Outreach Resources</a>
										</li>
										<li class="last leaf">
											<a href="${aboutUri}/about/membership" title="">Membership</a>
										</li>
									</ul>
								</li>
								<li class="leaf">
									<a href="${aboutUri}/organizations/associations" ng-click="handleMobileMenuOption($event); toggleSecondaryMenu('associations')"><@orcid.msg 'public-layout.associations'/><span class="more" ng-class="{'less':secondaryMenuVisible['associations'] == true}"></span></a> <!-- Updated according Drupal website structure -->
									<ul class="menu" ng-show="secondaryMenuVisible['associations'] == true">
										<li class="hidden-sm hidden-md hidden-lg">
											<a href="${aboutUri}/organizations/associations"><@orcid.msg 'public-layout.associations'/></a>
										</li>
										<li class="first leaf">
											<a href="/organizations/associations/learnmore">Learn more</a>
										</li>
										<li class="leaf">
											<a href="/organizations/associations/outreachresources">Outreach resources</a>
										</li>
										<li class="leaf">
											<a href="http://orcid.org/about/membership">Membership</a>
										</li>
										<li class="last leaf">
											<a href="/organizations/associations/usecases">Use cases</a>
										</li>
									</ul>
								</li>
								<li class="last leaf">
									<a href="${aboutUri}/organizations/integrators" ng-click="handleMobileMenuOption($event); toggleSecondaryMenu('integrators')"><@orcid.msg 'public-layout.integrators'/><span class="more" ng-class="{'less':secondaryMenuVisible['integrators‰'] == true}"></span></a> <!-- Updated according Drupal website structure -->
									<ul class="menu" ng-show="secondaryMenuVisible['integrators'] == true">
										<li class="first leaf hidden-sm hidden-md hidden-lg">
											<a href="${aboutUri}/organizations/integrators"><@orcid.msg 'public-layout.integrators'/></a>
										</li>
										<li class="first leaf">
											<a href="${aboutUri}/organizations/integrators/API">The ORCID API</a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/content/register-client-application-0">Register a Client Application</a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/organizations/integrators/current">Current Integrations</a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/organizations/integrators/integration-chart">Integration Chart</a>
										</li>
										<li class="last leaf">
											<a href="${aboutUri}/content/beta-tester-request">Beta Testers</a>
										</li>
									</ul>
								</li>
							</ul>
						</li>
						<!-- ABOUT -->
						<li class="expanded"><a href="${aboutUri}/about" ng-click="handleMobileMenuOption($event)"><@orcid.msg
								'public-layout.about'/></a>
	
							<ul class="menu lang-fixes">
								<!-- Mobile view Only -->
								<li><a href="${aboutUri}/about"
									class="first leaf hidden-md hidden-lg hidden-sm visible-xs"><@orcid.msg
										'public-layout.about'/></a></li>
								<!-- What is ORCID? -->
								<li class="first expanded">
									<a href="${aboutUri}/about/what-is-orcid" ng-click="handleMobileMenuOption($event); toggleSecondaryMenu('whatIsOrcid')"><@orcid.msg 'public-layout.what_is_orcid'/><span class="more" ng-class="{'less':secondaryMenuVisible['whatIsOrcid'] == true}"></span></a>
									<ul class="menu" ng-show="secondaryMenuVisible['whatIsOrcid'] == true">
										<li class="hidden-md hidden-lg hidden-sm visible-xs">
											<a href="${aboutUri}/about/what-is-orcid"><@orcid.msg 'public-layout.what_is_orcid'/></a>
										</li>
										<li class="first leaf">
											<a href="${aboutUri}/about/what-is-orcid/mission-statement" title=""><@orcid.msg 'public-layout.our_mission'/></a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/about/what-is-orcid/our-principles" title=""><@orcid.msg 'public-layout.our_principles'/></a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/content/our-governance">Our Governance</a>
										</li>
										<li class="last expanded">
											<a href="${aboutUri}/about/what-is-orcid/policies" ng-click="handleMobileMenuOption($event); toggleTertiaryMenu('policies')">Our Policies</a>
											<ul class="menu" ng-show="tertiaryMenuVisible['policies'] == true">
												<li class="first leaf"><a
													href="${aboutUri}/orcid-dispute-procedures">Dispute
														Procedures</a></li>
												<li class="leaf"><a
													href="${aboutUri}/footer/privacy-policy" title="">Privacy
														Policy</a></li>
												<li class="leaf"><a
													href="${aboutUri}/content/orcid-public-client-terms-service">Public
														Client Terms of Service</a></li>
												<li class="leaf"><a
													href="${aboutUri}/content/orcid-public-data-file-use-policy">Public
														Data File Use Policy</a></li>
												<li class="leaf"><a href="${aboutUri}/legal">Terms
														and Conditions of Use</a></li>
												<li class="last leaf"><a
													href="${aboutUri}/trademark-and-id-display-guidelines">Trademark
														and iD Display Guidelines</a></li>
											</ul>
										</li>
									</ul>
								</li>
								<!-- The ORCID Team -->
								<li class="leaf"><a href="${aboutUri}/about/team" title=""><@orcid.msg
										'public-layout.the_orcid_team'/></a></li>
								<!-- The ORCID Comunity -->
								<li class="expanded">
									<a href="${aboutUri}/about/community" ng-click="handleMobileMenuOption($event); toggleSecondaryMenu('community')"><@orcid.msg 'public-layout.the_orcid_community'/><span class="more" ng-class="{'less':secondaryMenuVisible['community'] == true}"></span></a>
									<ul class="menu" ng-show="secondaryMenuVisible['community'] == true">
										<li class="hidden-md hidden-lg hidden-sm visible-xs">
											<a href="${aboutUri}/about/community"><@orcid.msg 'public-layout.the_orcid_community'/></a>
										</li>
										<li class="first leaf">
											<a href="${aboutUri}/about/community" title=""><@orcid.msg 'public-layout.working_groups'/></a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/about/community/sponsors" title=""><@orcid.msg 'public-layout.sponsors'/></a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/about/community/members" title=""><@orcid.msg 'public-layout.members'/></a>
										</li>
										<li class="last">
											<a href="${aboutUri}/about/community/launch-partners" title=""><@orcid.msg 'public-layout.launch_partners'/></a></li>
										<li class="leaf">
											<a href="${aboutUri}/about/community/launch-partners" title="">Launch Partners</a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/about/community/orcid-technical-community">Open Source</a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/content/partners">Partners</a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/content/adoption-and-integration-program">Adoption &amp; Integration Program</a>
										</li>
										<li class="expanded">
											<a href="${aboutUri}/content/orcid-ambassadors">Ambassadors</a>
											<ul class="menu">
												<li class="first last leaf">
													<a href="${aboutUri}/content/orcid-ambassadors-1/outreachresources">Outreach Resources</a>
												</li>
											</ul>
										</li>
										<li class="last leaf">
											<a href="http://www.cafepress.com/orcid" title="">ORCID Gear</a>
										</li>
									</ul>
								</li>
								<!-- Membership -->
								<li class="expanded">
									<a href="${aboutUri}/about/membership" ng-click="handleMobileMenuOption($event); toggleSecondaryMenu('membership')"><@orcid.msg 'public-layout.membership'/><span class="more" ng-class="{'less':secondaryMenuVisible['membership'] == true}"></span></a>
									<ul class="menu" ng-show="secondaryMenuVisible['membership'] == true">
										<li class="hidden-md hidden-lg hidden-sm visible-xs">
											<a href="${aboutUri}/about/membership"><@orcid.msg 'public-layout.membership'/></a>
										</li>
										<li class="first expanded">
											<a href="${aboutUri}/about/membership" title=""><@orcid.msg 'public-layout.membership_and_subscription'/></a>
											<ul class="menu">
												<li class="first last leaf">
													<a href="${aboutUri}/content/membership-comparison">Membership Comparison</a>
												</li>
											</ul>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/about/membership/standard-member-agreement" title=""><@orcid.msg 'public-layout.standard_member_agreement'/></a>
										</li>
										<li class="leaf">
											<a href="${aboutUri}/document/standard-creator-membership-agreement">Standard Creator Member Agreement</a>
										</li>
										<li class="last leaf">
											<a href="${aboutUri}/about/community/members" title=""><@orcid.msg 'public-layout.our_members'/></a>
										</li>
									</ul>
								</li>
								<!-- News -->
								<li class="leaf">
									<a href="${aboutUri}/about/news/news" ng-click="handleMobileMenuOption($event); toggleSecondaryMenu('news')"><@orcid.msg 'public-layout.news'/><span class="more" ng-class="{'less':secondaryMenuVisible['news'] == true}"></span></a>
									<ul class="menu" ng-show="secondaryMenuVisible['news'] == true">
										<li class="hidden-md hidden-lg hidden-sm visible-xs">
											<a href="${aboutUri}/about/news/news"><@orcid.msg 'public-layout.news'/></a>
										</li>
										<li class="first leaf">
											<a href="${aboutUri}/category/newsletter/blog" title="">Blog</a>
										</li>
										<li class="last leaf">
											<a href="${aboutUri}/newsletter/subscriptions" title="">Subscribe!</a>
										</li>
									</ul>
								</li>
								<!-- Events -->
								<li class="last expanded">
									<a href="${aboutUri}/about/events" title=""><@orcid.msg 'public-layout.events'/></a>
								</li>
							</ul>
						</li>
						<!-- HELP -->
						<li class="expanded">
							<a href="${aboutUri}/help" ng-click="handleMobileMenuOption($event)"><@orcid.msg 'public-layout.help'/></a>
							<ul class="menu lang-fixes">
								<!-- Mobile view Only -->
								<li class="first leaf hidden-md hidden-lg hidden-sm visible-xs">
									<a href="${aboutUri}/help"><@orcid.msg 'public-layout.help'/></a>
								</li>
								<li class="first leaf">
									<a href="${aboutUri}/faq-page" title=""><@orcid.msg 'public-layout.faq'/></a>
								</li>
								<li class="leaf">
									<a href="${aboutUri}/help/contact-us" title=""><@orcid.msg 'public-layout.contact_us'/></a>
								</li>
								<li class="leaf">
									<a href="http://orcid.uservoice.com/forums/175591-general" title=""><@orcid.msg 'public-layout.give_feedback'/></a>
								</li>
								<li class="last leaf">
									<a href="http://orcid.uservoice.com/knowledgebase" title=""><@orcid.msg 'public-layout.knowledge_base'/></a>
								</li>
							</ul>
						</li>
						
						<!-- SIGN IN/OUT -->
						<li class="last leaf"><@security.authorize
							ifNotGranted="ROLE_USER, ROLE_ADMIN, ROLE_BASIC, ROLE_PREMIUM,
							ROLE_BASIC_INSTITUTION, ROLE_PREMIUM_INSTITUTION">
							<a href="<@orcid.rootPath "/signin" />" title=""><@orcid.msg 'public-layout.sign_in'/></a>
						</@security.authorize>
						 
						<@security.authorize ifAnyGranted="ROLE_USER, ROLE_ADMIN, ROLE_BASIC, ROLE_PREMIUM, ROLE_BASIC_INSTITUTION, ROLE_PREMIUM_INSTITUTION">
							<a href="<@orcid.rootPath "/signout" />"><@orcid.msg 'public-layout.sign_out'/></a>
						</@security.authorize>
						
					</li>
	
					</ul>
					<#--<#if isProxy><#include "/common/change_proxy.ftl" /></#if>-->
				</#if>
			</div>
			<div class="col-md-3 col-sm-3 col-sm-pull-9 col-md-pull-9 reset logo">
			<!--Pi Day banner-->
			<#if RequestParameters['piDay']??>
				<a href="${aboutUri}/blog/2017/02/21/orcid-pi-day-coming">
					<div id="pi-banner">
						<![if gte IE 9]>
						<svg height="250" width="100">
							<polygon points="0,0 0,154 50,180 100,154 100,0" style="fill:#338caf;" />
						</svg>
						<div id="pi-text">
							Time to party irrationally!
							<img src="${staticCdn}/img/pi-day-icon.png" alt="ORCID Pi day icon" />
							<span id="pi-number">3,141,593</span><br><small>ORCID iDs</small>
						</div>
						<![endif]-->
						<!--[if lt IE 9]>
            				<img src="${staticCdn}/img/pi-day-banner.png" alt="ORCID Pi day banner" />
        				<![endif]-->
					</div>
				</a>
			</#if>
				<h1>
					<a href="${aboutUri}"><img
						src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo" /></a>
				</h1>
				<p><@orcid.msg 'public-layout.logo.tagline'/></p>
			</div>
			<span class="see-more">${liveIds} <@orcid.msg
				'public-layout.amount_ids'/> <a href="<@orcid.rootPath " statistics" />"
				title=""><@orcid.msg 'public-layout.see_more'/></a>
			</span>
			<!--
			<#if inDelegationMode>
				<div class="delegation-label">
					<span class="label label-danger"><@orcid.msg 'delegate.global_status_label'/></span>
				</div>
			</#if>
			 -->	
		</div><!-- .row -->
	</div><!-- .header -->
	<div id="main" role="main" class="main"><#include
		'../common/maintenance_header.ftl'/> <#-- wtf --> <#nested>
	</div>
</div>
<!-- .container -->
<div class="footer clear-fix">
	<div class="container">
		<div class="row">
			<div class="col-md-11 col-md-offset-1">
				<ul class="col-md-11 col-md-offset-1">
					<li class=""><a href="${aboutUri}/help/contact-us"><@orcid.msg 'public-layout.contact_us'/></a></li>
					<li class=""><a href="${aboutUri}/footer/privacy-policy"><@orcid.msg 'public-layout.privacy_policy'/></a></li>
					<li class=""><a href="${aboutUri}/content/orcid-terms-use"><@orcid.msg 'public-layout.terms_of_use'/></a></li>
					<li class=""><a href="${aboutUri}/open-source-license"><@orcid.msg 'footer.openSource'/></a></li>
				</ul>
			</div>
		</div>
	</div>
</div>
<form action="<@orcid.rootPath '/'/>">
	<input id="imageUrl" type="hidden" value="${staticCdn}/images">
</form>

<#include "../includes/language_selector.ftl">

</@base>
</#macro>

