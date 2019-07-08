<script type="text/ng-template" id="header2-ng2-template">

<div class="header2" >
    <div  *ngIf="!isOauth"> 

        <div class="container" role="banner">
            <div class="logo-search-bar">
                <div class="logo" aria-label="ORCID logo"> 
                    <a href="{{aboutUri}}"><img *ngIf="assetsPath != null" src="{{assetsPath + '/img/orcid-logo.svg'}}" alt="ORCID logo" /></a>
                    <div class="slogan">${springMacroRequestContext.getMessage("public-layout.logo.tagline")?replace("<br />", " ")?replace("'", "\\'")}</div>
                     
                    <div class="menu-control">
                        <@orcid.checkFeatureStatus 'ENABLE_USER_MENU'>
                        <user-menu *ngIf="isMobile && !openMobileMenu"></user-menu>
                        </@orcid.checkFeatureStatus>
                        <language-ng2 *ngIf="openMobileMenu"></language-ng2>
                        <span [hidden]="!openMobileMenu" style="height: 35px" class="close" (click)="toggleMenu()" alt="close menu"> </span>
                        <img [hidden]="openMobileMenu" (click)="toggleMenu()" style="height: 35px" src="{{assetsPath + '/img/glyphicon-menu.svg'}}" alt="open menu"/>
                    </div>
                </div>
                <div class="search" >
                <div class="dropdown-menus-container">
                        <@orcid.checkFeatureStatus 'ENABLE_USER_MENU'> 
                        <user-menu *ngIf="!isMobile"></user-menu>
                        </@orcid.checkFeatureStatus>
                        <language-ng2 *ngIf="!isMobile"></language-ng2>
                </div>
                   
                    <div class="form-group " role="presentation">
                        <div class="search-container" role="search"> 
                        <div class="input-group" role="presentation">
                            <div class="input-group-addon" role="presentation">
                                <div role="presentation" class="search-dropdown" [ngClass]="{'hover': searchDropdownOpen}"  (mouseleave)="closeDropdown()">
                                    <div  aria-label="{{'${springMacroRequestContext.getMessage("aria.search-location")?replace("<br />", " ")?replace("'", "\\'")}" role="menu" class="search-dropbtn"   (click)="clickDropdown()"> {{ (headerSearch.searchOption === 'website'? '${springMacroRequestContext.getMessage("layout.public-layout.website")?replace("<br />", " ")?replace("'", "\\'")}':'${springMacroRequestContext.getMessage("layout.public-layout.registry")?replace("<br />", " ")?replace("'", "\\'")}') | titlecase }} <span [ngClass]="{'dropdown-arrow': !searchDropdownOpen, 'dropdown-arrow-up': searchDropdownOpen}"></span> </div>
                                    <ul class="dropdown-content" role="presentation">
                                        <div (click)="clickDropdown('registry')"> {{'${springMacroRequestContext.getMessage("layout.public-layout.registry")?replace("<br />", " ")?replace("'", "\\'")}'| titlecase }} </div>
                                        <div (click)="clickDropdown('website')"> {{'${springMacroRequestContext.getMessage("layout.public-layout.website")?replace("<br />", " ")?replace("'", "\\'")}'| titlecase  }} </div>
                                    </ul>
                                </div>  
                            </div>
                            <input (keyup.enter)="searchSubmit()" [(ngModel)]="headerSearch.searchInput" class="form-control" name="search" type="text" placeholder="${springMacroRequestContext.getMessage("public-layout.search")?replace("<br />", " ")?replace("'", "\\'")}"/>
                            <div aria-label="${springMacroRequestContext.getMessage("orcid_bio_search.btnsearch")?replace("<br />", " ")?replace("'", "\\'")}" class="input-group-addon" role="presentation">
                                <span role="button" aria-label="${springMacroRequestContext.getMessage("orcid_bio_search.btnsearch")?replace("<br />", " ")?replace("'", "\\'")}" class="glyphicon glyphicon-search" (click)="searchSubmit()"></span> 
                            </div>
                        </div>
                    </div>        
                </div>
                         <#--  
                        <div class="conditions" >
                            <p> ${springMacroRequestContext.getMessage("public-layout.search.terms1")?replace("<br />", " ")?replace("'", "\\'")}<a
                                    href="{{aboutUri}}/legal">${springMacroRequestContext.getMessage("public-layout.search.terms2")?replace("<br />", " ")?replace("'", "\\'")}</a>${springMacroRequestContext.getMessage("public-layout.search.terms3")?replace("<br />", " ")?replace("'", "\\'")}
                            </p>
                        </div>  
                        -->
                </div>
            </div>
        </div>

        <div class="menu-bar"  [hidden]="!openMobileMenu && isMobile"  (mouseleave)="mouseLeave()" role="menu">
                <!--  Desktop / Tablet menu -->             
                <div class="container container-menu"  role="presentation"> 
                <ul class="menu" resize  role="presentation">
                    <!-- FOR RESEARCHERS -->
                    <li class="first expanded open" role="presentation" [ngClass]="{'hover': mobileMenu.RESEARCHERS}"  >
                        <a href="{{aboutUri}}/about/what-is-orcid/mission" title="" role="menu-item" (mouseenter)="menuHandler('RESEARCHERS', $event)"  (click)="menuHandler('RESEARCHERS', $event)">{{'${springMacroRequestContext.getMessage("public-layout.for_researchers")?replace("<br />", " ")?replace("'", "\\'")}' | uppercase }} <span class="more" [ngClass]="{'less':mobileMenu.RESEARCHERS == true}"></span></a>
                        <ul class="menu lang-fixes" *ngIf="!userInfo['REAL_USER_ORCID']" aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li class="leaf    " [hidden]="!isMobile"><a href="{{getBaseUri()}}" title="" role="menu-item">{{'${springMacroRequestContext.getMessage("public-layout.for_researchers")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a></li>
                    
                            <!-- Menu -->
                            <li class="leaf last"><a ${(nav=="signin")?then('class="active" ', '')} href="{{getBaseUri()}}/signin" role="menu-item">{{'${springMacroRequestContext.getMessage("public-layout.sign_in")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }}</a></li>                                   
                            <li class="leaf last"><a ${(nav=="register")?then('class="active" ', '')} href="{{getBaseUri()}}/register" role="menu-item">{{'${springMacroRequestContext.getMessage("public-layout.register")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }}</a></li>                                                                                                                          
                            <li class="leaf last"><a href="{{getBaseUri()}}/content/initiative" role="menu-item">{{'${springMacroRequestContext.getMessage("manage_delegators.learn_more.link.text")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }}</a></li>
                        </ul>
                        <ul class="menu lang-fixes" *ngIf="userInfo['REAL_USER_ORCID']"  aria-label="submenu">
                            <li role="presentation" >
                                <a ${(nav=="record")?then('class="active" ', '')}href="{{getBaseUri()}}/my-orcid" role="menu-item" aria-labelby="my-orcid-menu-item">
                                    <div *ngIf="userInfo['IN_DELEGATION_MODE'] == 'true'" role="menu-item" id="my-orcid-menu-item">
                                        {{'${springMacroRequestContext.getMessage("public-layout.my_orcid")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }}
                                    </div >
                                    <div *ngIf="userInfo['IN_DELEGATION_MODE'] == 'false'" id="my-orcid-menu-item">
                                        {{'${springMacroRequestContext.getMessage("public-layout.my_orcid_record")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }}
                                    </div >
                                </a>
                            </li>
                            <li role="presentation">
                                {{retrieveUnreadCount()}}
                                <a role="menu-item" ${(nav=="notifications")?then('class="active" ', '')} href="{{getBaseUri()}}/inbox">{{'${springMacroRequestContext.getMessage("workspace.notifications")}  '| uppercase }} <span *ngIf="getUnreadCount > 0">({{getUnreadCount}})</span></a>
                            </li>
                            <li role="presentation">
                                <a ${(nav=="settings")?then('class="active" ', '')} href="{{getBaseUri()}}/account" id="accountSettingMenuLink">{{'${springMacroRequestContext.getMessage("public-layout.account_setting")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a>
                            </li>
                            
                            <!-- Developer tools -->
                            <li role="presentation" *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']=='true'"><a role="menu-item" ${(nav=="developer-tools")?then('class="active" ', '')}href="{{getBaseUri()}}/group/developer-tools">{{'${springMacroRequestContext.getMessage("workspace.developer_tools")}'| uppercase }}</a></li>
                            <li *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']!='true'"><a role="menu-item" ${(nav=="developer-tools")?then('class="active" ', '')}href="{{getBaseUri()}}/developer-tools">{{'${springMacroRequestContext.getMessage("workspace.developer_tools")}'| uppercase }}</a></li>
                            
                            <!-- Admin menu -->
                            <li  role="presentation" role="presentation" *ngIf="userInfo['ADMIN_MENU']"><a role="menu-item" ${(nav=="members")?then('class="active" ', '')}href="{{getBaseUri()}}/manage-members">{{'${springMacroRequestContext.getMessage("admin.members.workspace_link")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a></li>
                            <li  role="presentation" *ngIf="userInfo['ADMIN_MENU']"><a  role="menu-item" ${(nav=="admin")?then('class="active" ', '')}href="{{getBaseUri()}}/admin-actions">{{'${springMacroRequestContext.getMessage("admin.workspace_link")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a></li>
                                
                            <!-- Self service menu -->
                            <li  role="presentation" *ngIf="userInfo['SELF_SERVICE_MENU']"><a role="menu-item" ${(nav=="self-service")?then('class="active" ', '')}href="{{getBaseUri()}}/self-service">{{'${springMacroRequestContext.getMessage("workspace.self_service")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a></li>
                                    
                            <li  role="presentation" class="leaf last"><a role="menu-item" href="{{getBaseUri()}}/content/initiative">{{'${springMacroRequestContext.getMessage("manage_delegators.learn_more.link.text")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a></li>
                        </ul>
                    </li>

                    <!-- DRUPAL WEBSITE MENUS -->
                    <!-- FOR ORGANIZATIONS -->
                    <li role="presentation" class="expanded" [ngClass]="{'hover': mobileMenu.ORGANIZATIONS}" >
                        <a href="{{aboutUri}}/organizations" role="menu-item" (mouseenter)="menuHandler('ORGANIZATIONS', $event)" (click)="menuHandler('ORGANIZATIONS', $event)">{{'${springMacroRequestContext.getMessage("public-layout.for_organizations")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}<span class="more" [ngClass]="{'less':mobileMenu.ORGANIZATIONS == true}"></span></a>
                        <ul class="menu lang-fixes"  aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li role="presentation" class="first leaf" [hidden]="!isMobile" >
                                <a role="menu-item" href="{{aboutUri}}/organizations" >{{'${springMacroRequestContext.getMessage("public-layout.for_organizations")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a>
                            </li>

                            <li role="presentation" class="first leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/funders" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('funders')" class="russian-fix" >{{'${springMacroRequestContext.getMessage("public-layout.funders")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}<span class="more" [ngClass]="{'less':secondaryMenuVisible['funders'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['funders'] == true">
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/organizations/funders">{{'${springMacroRequestContext.getMessage("public-layout.funders")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/funders/learnmore">{{'Learn more '| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/funders/outreachresources">{{'Outreach Resources '| uppercase }}</a>
                                    </li>
                                    <li class="last leaf">
                                        <a href="{{aboutUri}}/about/membership" title="">{{'Membership '| uppercase }}</a>
                                    </li>
                                </ul>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/institutions" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('institutions')">{{'${springMacroRequestContext.getMessage("public-layout.research_organizations")?replace("<br />", " ")?replace("'", "\\'")}' | uppercase }} <span class="more" [ngClass]="{'less':secondaryMenuVisible['institutions'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                    <ul class="menu" *ngIf="secondaryMenuVisible['institutions'] == true">
                                        <li class="first leaf">
                                            <a href="{{aboutUri}}/organizations/institutions">{{'${springMacroRequestContext.getMessage("public-layout.research_organizations")?replace("<br />", " ")?replace("'", "\\'")}' | uppercase }}</a>
                                        </li>
                                        <li class="leaf">
                                            <a href="{{aboutUri}}/organizations/institutions/learnmore">{{'Learn more'| uppercase }}</a>
                                        </li>
                                        <li class="leaf">
                                            <a href="{{aboutUri}}/organizations/institutions/outreachresources">{{'Outreach Resources'| uppercase }}</a>
                                        </li>
                                            <li class="leaf"><a href="{{aboutUri}}/about/membership" title="">{{'Membership'| uppercase }}</a>
                                        </li>
                                        <li class="last leaf">
                                            <a href="{{aboutUri}}/organizations/institutions/usecases">{{'Use cases'| uppercase }}</a>
                                        </li>
                                    </ul>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/publishers" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('publishers')">{{'${springMacroRequestContext.getMessage("public-layout.publishers")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }}<span class="more" [ngClass]="{'less':secondaryMenuVisible['publishers'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['publishers'] == true">
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/organizations/publishers"> {{'${springMacroRequestContext.getMessage("public-layout.publishers")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/publishers/learnmore">{{'Learn more'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/publishers/outreachresources">{{'Outreach Resources'| uppercase }}</a>
                                    </li>
                                    <li class="last leaf">
                                        <a href="{{aboutUri}}/about/membership" title="">{{'Membership'| uppercase }}</a>
                                    </li>
                                </ul>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/associations"  (click)="handleMobileMenuOption($event); toggleSecondaryMenu('associations')">{{'${springMacroRequestContext.getMessage("public-layout.associations")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }}<span class="more" [ngClass]="{'less':secondaryMenuVisible['associations'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['associations'] == true">
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/organizations/associations">{{'${springMacroRequestContext.getMessage("public-layout.associations")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="/organizations/associations/learnmore">{{'Learn more'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="/organizations/associations/outreachresources">{{'Outreach resources'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="http://orcid.org/about/membership">{{'Membership'| uppercase }}</a>
                                    </li>
                                    <li class="last leaf">
                                        <a href="/organizations/associations/usecases">{{'Use cases'| uppercase }}</a>
                                    </li>
                                </ul>
                            </li>
                            <li role="presentation" class="last leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/integrators" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('integrators')">{{'${springMacroRequestContext.getMessage("public-layout.integrators")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }} <span class="more" [ngClass]="{'less':secondaryMenuVisible['integrators'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['integrators'] == true">
                                    <li class="first leaf hidden-sm hidden-md hidden-lg">
                                        <a href="{{aboutUri}}/organizations/integrators">{{'${springMacroRequestContext.getMessage("public-layout.integrators")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                    </li>
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/organizations/integrators/API">{{'The ORCID API'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/content/register-client-application-0">{{'Register a Client Application'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/integrators/current">{{'Current Integrations'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/integrators/integration-chart">{{'Integration Chart'| uppercase }}</a>
                                    </li>
                                    <li class="last leaf">
                                        <a href="{{aboutUri}}/content/beta-tester-request">{{'Beta Testers'| uppercase }}</a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                    <!-- ABOUT -->
                    <li role="presentation" class="expanded" [ngClass]="{'hover': mobileMenu.ABOUT}"  >
                        <a href="{{aboutUri}}/about" role="menu-item" (mouseenter)="menuHandler('ABOUT', $event)"  (click)="menuHandler('ABOUT', $event)">{{'${springMacroRequestContext.getMessage("public-layout.about")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }} <span class="more" [ngClass]="{'less':mobileMenu.ABOUT == true}"></span></a>

                        <ul class="menu lang-fixes"  aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li  role="presentation" [hidden]="!isMobile" ><a role="menu-item" href="{{aboutUri}}/about"
                                class="first leaf    ">{{'${springMacroRequestContext.getMessage("public-layout.about")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a></li>
                            <!-- What is ORCID? -->
                            <li role="presentation" class="first expanded">
                                <a role="menu-item" href="{{aboutUri}}/about/what-is-orcid" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('whatIsOrcid')">{{'${springMacroRequestContext.getMessage("public-layout.what_is_orcid")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }} <span class="more" [ngClass]="{'less':secondaryMenuVisible['whatIsOrcid'] == true}"></span></a>
                                <ul class="menu" *ngIf="secondaryMenuVisible['whatIsOrcid'] == true" >
                                        <li class="hidden-md hidden-lg hidden-sm visible-xs">
                                            <a href="{{aboutUri}}/about/what-is-orcid">{{'${springMacroRequestContext.getMessage("public-layout.what_is_orcid")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                        </li>
                                        <li class="first leaf">
                                            <a href="{{aboutUri}}/about/what-is-orcid/mission-statement" title="">{{'${springMacroRequestContext.getMessage("public-layout.our_mission")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                        </li>
                                        <li class="leaf">
                                            <a href="{{aboutUri}}/about/what-is-orcid/our-principles" title="">{{'${springMacroRequestContext.getMessage("public-layout.our_principles")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                        </li>
                                        <li class="leaf">
                                            <a href="{{aboutUri}}/content/our-governance">{{'Our Governance'| uppercase }}</a>
                                        </li>
                                        <li class="last expanded">
                                            <a href="{{aboutUri}}/about/what-is-orcid/policies" (click)="handleMobileMenuOption($event); toggleTertiaryMenu('policies')">{{'Our Policies'| uppercase }} <span class="more dark" [ngClass]="{'less':tertiaryMenuVisible['policies'] == true}"></span></a>
                                            <ul class="menu" *ngIf="tertiaryMenuVisible['policies'] == true" >
                                                <li class="first leaf"><a
                                                    href="{{aboutUri}}/orcid-dispute-procedures">{{'Dispute
                                                        Procedures'| uppercase }}</a></li>
                                                <li class="leaf"><a
                                                    href="{{aboutUri}}/footer/privacy-policy" title="">{{'Privacy
                                                        Policy'| uppercase }}</a></li>
                                                <li class="leaf"><a
                                                    href="{{aboutUri}}/content/orcid-public-client-terms-service">{{'Public
                                                        Client Terms of Service'| uppercase }}</a></li>
                                                <li class="leaf"><a
                                                    href="{{aboutUri}}/content/orcid-public-data-file-use-policy">{{'Public
                                                        Data File Use Policy'| uppercase }}</a></li>
                                                <li class="leaf"><a href="{{aboutUri}}/legal">{{'Terms
                                                        and Conditions of Use'| uppercase }}</a></li>
                                                <li class="last leaf"><a
                                                    href="{{aboutUri}}/trademark-and-id-display-guidelines">{{'Trademark
                                                        and iD Display Guidelines'| uppercase }}</a></li>
                                            </ul>
                                        </li>
                                    </ul>
                            </li>
                            <!-- The ORCID Team -->
                            <li role="presentation" class="leaf"><a role="menu-item" href="{{aboutUri}}/about/team" title="">{{'${springMacroRequestContext.getMessage("public-layout.the_orcid_team")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a></li>
                            <!-- The ORCID Comunity -->
                            <li role="presentation" class="expanded" >
                                <a role="menu-item" href="{{aboutUri}}/about/community" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('community')">{{'${springMacroRequestContext.getMessage("public-layout.the_orcid_community")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }} <span class="more" [ngClass]="{'less':secondaryMenuVisible['community'] == true}"></span></a>
                                <ul class="menu" *ngIf="secondaryMenuVisible['community'] == true">
                                    <li class="hidden-md hidden-lg hidden-sm visible-xs">
                                        <a href="{{aboutUri}}/about/community">{{'${springMacroRequestContext.getMessage("public-layout.the_orcid_community")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                    </li>
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/about/community" title="">{{'${springMacroRequestContext.getMessage("public-layout.working_groups")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/about/community/sponsors" title="">{{'${springMacroRequestContext.getMessage("public-layout.sponsors")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/about/community/members" title="">{{'${springMacroRequestContext.getMessage("public-layout.members")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                    </li>
                                    <li class="last">
                                        <a href="{{aboutUri}}/about/community/launch-partners" title="">{{'${springMacroRequestContext.getMessage("public-layout.launch_partners")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a></li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/about/community/orcid-technical-community">{{'Open Source'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/content/partners">{{'Partners'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/content/adoption-and-integration-program">{{'Adoption &amp; Integration Program'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/content/orcid-ambassadors" (click)="handleMobileMenuOption($event); toggleTertiaryMenu('ambassadors')">{{'Ambassadors '| uppercase }}<span class="more dark" [ngClass]="{'less':tertiaryMenuVisible['ambassadors'] == true}"></span></a>
                                        <ul class="menu" *ngIf="tertiaryMenuVisible['ambassadors']">
                                            <li class="first last leaf">
                                                <a href="{{aboutUri}}/content/orcid-ambassadors-1/outreachresources">{{'Outreach Resources '| uppercase }}</a>
                                            </li>
                                        </ul>
                                    </li>
                                    <li class="last leaf">
                                        <a href="http://www.cafepress.com/orcid" title="">{{'ORCID Gear '| uppercase }}</a>
                                    </li>
                                </ul> 
                            </li>
                            <!-- Membership -->
                            <li role="presentation" class="expanded" >
                                <a role="menu-item" href="{{aboutUri}}/about/membership" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('membership')"    >{{'${springMacroRequestContext.getMessage("public-layout.membership")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }} <span class="more" [ngClass]="{'less':secondaryMenuVisible['membership'] == true}"></span></a>
                                <ul class="menu" *ngIf="secondaryMenuVisible['membership'] == true">
                                    <li class="hidden-md hidden-lg hidden-sm visible-xs">
                                        <a href="{{aboutUri}}/about/membership">{{'${springMacroRequestContext.getMessage("public-layout.membership")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                    </li>
                                    <li class="first expanded">
                                        <a href="{{aboutUri}}/about/membership" title="" (click)="handleMobileMenuOption($event); toggleTertiaryMenu('membership')"  >{{'${springMacroRequestContext.getMessage("public-layout.membership_and_subscription")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}<span class="more dark" [ngClass]="{'less': tertiaryMenuVisible['membership'] == true}"></span></a>
                                        <ul class="menu" *ngIf="tertiaryMenuVisible['membership']">
                                            <li class="first last leaf">
                                                <a href="{{aboutUri}}/content/membership-comparison">{{'Membership Comparison'| uppercase }}</a>
                                            </li>
                                        </ul>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/about/membership/standard-member-agreement" title="">{{'${springMacroRequestContext.getMessage("public-layout.standard_member_agreement")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/document/standard-creator-membership-agreement">{{'Standard Creator Member Agreement'| uppercase }}</a>
                                    </li>
                                    <li class="last leaf">
                                        <a href="{{aboutUri}}/about/community/members" title="">{{'${springMacroRequestContext.getMessage("public-layout.our_members")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                    </li>
                                </ul>
                            </li>
                            <!-- News -->
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/about/news/news" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('news')">{{'${springMacroRequestContext.getMessage("public-layout.news")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}<span class="more" [ngClass]="{'less':secondaryMenuVisible['news'] == true}"></span></a>
                                <ul class="menu" *ngIf="secondaryMenuVisible['news'] == true">
                                    <li class="hidden-md hidden-lg hidden-sm visible-xs">
                                        <a href="{{aboutUri}}/about/news/news">{{'${springMacroRequestContext.getMessage("public-layout.news")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>
                                    </li>
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/category/newsletter/blog" title="">{{'Blog'| uppercase }}</a>
                                    </li>
                                    <li class="last leaf">
                                        <a href="{{aboutUri}}/newsletter/subscriptions" title="">{{'Subscribe!'| uppercase }}</a>
                                    </li>
                                </ul>
                            </li>
                            <!-- Events -->
                            <li role="presentation" class="last expanded">
                                <a role="menu-item" href="{{aboutUri}}/about/events" title="">{{'${springMacroRequestContext.getMessage("public-layout.events")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a>
                            </li>
                        </ul>
                    </li>
                    <!-- HELP -->
                    <li role="presentation" class="expanded" [ngClass]="{'hover': mobileMenu.HELP}"  >
                        <a role="menu-item" href="{{aboutUri}}/help" (mouseenter)="menuHandler('HELP', $event)" (click)="menuHandler('HELP', $event)">{{'${springMacroRequestContext.getMessage("public-layout.help")?replace("<br />", " ")?replace("'", "\\'")}   '| uppercase }}<span class="more" [ngClass]="{'less':mobileMenu.HELP == true}"></span></a>
                        <ul  [hidden]="!isMobile" class="menu lang-fixes"  aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li role="presentation" class="first leaf    "  [hidden]="!isMobile">
                                <a role="menu-item" href="{{aboutUri}}/help">{{'${springMacroRequestContext.getMessage("public-layout.help")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a>
                            </li>
                            <li role="presentation" class="first leaf">
                                <a role="menu-item" href="{{aboutUri}}/faq-page" title="">{{'${springMacroRequestContext.getMessage("public-layout.faq")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/help/contact-us" title="">{{'${springMacroRequestContext.getMessage("public-layout.contact_us")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="https://support.orcid.org/hc/en-us/community/topics" title="">{{'${springMacroRequestContext.getMessage("public-layout.give_feedback")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a>
                            </li>
                            <li role="presentation" class="last leaf">
                                <a role="menu-item" href="{{'${springMacroRequestContext.getMessage("common.kb_uri_help_center_home")?replace("<br />", " ")?replace("'", "\\'")}" title="">{{'${springMacroRequestContext.getMessage("public-layout.knowledge_base")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a>
                            </li>
                        </ul>
                    </li>
                    
                    <!-- SIGN IN/OUT -->
                    <li role="presentation" class="last leaf" [ngClass]="{'hover': mobileMenu.SIGNIN}" (mouseenter)="menuHandler('SIGNIN', $event)" (click)="menuHandler('SIGNIN', $event)">                    
                        <a *ngIf="!userInfo['REAL_USER_ORCID']" href="{{getBaseUri()}}/signin" role="menu-item">{{'${springMacroRequestContext.getMessage("public-layout.sign_in")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a>                    
                        <a *ngIf="userInfo['REAL_USER_ORCID']" href="{{getBaseUri()}}/signout" role="menu-item">{{'${springMacroRequestContext.getMessage("public-layout.sign_out")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a>
                    </li>                    

                </ul>  
                </div>
        </div>

        <p class="header2-see-more container" role="Complementary" aria-label="<@orcid.msg 'aria.orcid-statistics'/>">{{liveIds}} <@orcid.msg
             'public-layout.amount_ids'/> <a href="{{getBaseUri()}}/statistics"
             title=""><@orcid.msg 'public-layout.see_more'/></a>
        </p>

    </div>   
</div> 
</script>