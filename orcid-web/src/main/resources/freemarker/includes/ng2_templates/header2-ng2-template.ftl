<script type="text/ng-template" id="header2-ng2-template">

<div class="header2" >
    <div  *ngIf="!isOauth"> 

        <div class="container" role="banner">
            <div class="logo-search-bar">
                <div class="logo" aria-label="ORCID logo"> 
                    <a href="{{aboutUri}}"><img *ngIf="assetsPath != null" src="{{assetsPath + '/img/orcid-logo.svg'}}" alt="ORCID logo" /></a>
                    <div class="slogan"><@orcid.msg 'public-layout.logo.tagline'/></div>
                    <div class="menu-control"> 
                        <span [hidden]="!openMobileMenu" style="height: 35px" class="close" (click)="toggleMenu()" alt="close menu"> </span>
                        <img [hidden]="openMobileMenu" (click)="toggleMenu()" style="height: 35px" src="{{assetsPath + '/img/glyphicon-menu.svg'}}" alt="open menu"/>
                    </div>
                </div>
                <div class="search" >

                
                <div class="form-group " role="presentation">
                    <div class="search-container" role="search"> 
                        <div class="advance-search-link" role="presentation">
                            <a href="{{getBaseUri()}}/orcid-search/search" class="settings-button" title="<@orcid.msg 'public-layout.search.advanced'/>"><@orcid.msg 'public-layout.search.advanced'/></a>
                        </div>
                        <div class="input-group" role="presentation">
                            <div class="input-group-addon" role="presentation">
                                <div role="presentation" class="search-dropdown" [ngClass]="{'open': searchDropdownOpen}"  (mouseleave)="closeDropdown()">
                                    <div  aria-label="<@orcid.msg 'aria.search-location'/>" role="menu" class="search-dropbtn"   (click)="clickDropdown()"> {{ (headerSearch.searchOption === 'website'? '<@orcid.msg 'layout.public-layout.website'/>':'<@orcid.msg 'layout.public-layout.registry'/>') | titlecase }} <span [ngClass]="{'dropdown-arrow': !searchDropdownOpen, 'dropdown-arrow-up': searchDropdownOpen}"></span> </div>
                                    <ul class="dropdown-content" role="presentation">
                                        <div (click)="clickDropdown('registry')"> {{'<@orcid.msg 'layout.public-layout.registry'/>'| titlecase }} </div>
                                        <div (click)="clickDropdown('website')"> {{'<@orcid.msg 'layout.public-layout.website'/>'| titlecase  }} </div>
                                    </ul>
                                </div>  
                            </div>
                            <input (keyup.enter)="searchSubmit()" [(ngModel)]="headerSearch.searchInput" class="form-control" name="search" type="text" placeholder="<@orcid.msg 'public-layout.search'/>"/>
                            <div aria-label="<@orcid.msg 'orcid_bio_search.btnsearch'/>" class="input-group-addon" role="presentation">
                                <span role="button" aria-label="<@orcid.msg 'orcid_bio_search.btnsearch'/>" class="glyphicon glyphicon-search" (click)="searchSubmit()"></span> 
                            </div>
                        </div>
                    </div>
                    <language-ng2></language-ng2>
                </div>
                         <#--  
                        <div class="conditions" >
                            <p>                         
                                <@orcid.msg 'public-layout.search.terms1'/><a
                                    href="{{aboutUri}}/legal"><@orcid.msg
                                    'public-layout.search.terms2'/></a><@orcid.msg
                                'public-layout.search.terms3'/>
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
                    <li class="first expanded" role="presentation" [ngClass]="{'open': mobileMenu.RESEARCHERS}"  >
                        <a href="{{aboutUri}}/about/what-is-orcid/mission" title="" role="menu-item" (mouseenter)="menuHandler('RESEARCHERS', $event)"  (click)="menuHandler('RESEARCHERS', $event)"><@orcid.msg 'public-layout.for_researchers'/> <span class="more" [ngClass]="{'less dark':mobileMenu.RESEARCHERS == true}"></span></a>
                        <ul class="menu lang-fixes" *ngIf="!userInfo['REAL_USER_ORCID']" aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li class="leaf    " [hidden]="!isMobile"><a href="{{getBaseUri()}}" title="" role="menu-item"><@orcid.msg 'public-layout.for_researchers'/></a></li>
                    
                            <!-- Menu -->
                            <li class="leaf last"><a ${(nav=="signin")?then('class="active" ', '')} href="{{getBaseUri()}}/signin" role="menu-item"><@orcid.msg 'public-layout.sign_in'/></a></li>                                   
                            <li class="leaf last"><a ${(nav=="register")?then('class="active" ', '')} href="{{getBaseUri()}}/register" role="menu-item"><@orcid.msg 'public-layout.register'/></a></li>                                                                                                                          
                            <li class="leaf last"><a href="{{getBaseUri()}}/content/initiative" role="menu-item"><@orcid.msg 'manage_delegators.learn_more.link.text' /></a></li>
                        </ul>
                        <ul class="menu lang-fixes" *ngIf="userInfo['REAL_USER_ORCID']"  aria-label="submenu">
                            <li role="presentation" >
                                <a ${(nav=="record")?then('class="active" ', '')}href="{{getBaseUri()}}/my-orcid" role="menu-item" aria-labelby="my-orcid-menu-item">
                                    <div *ngIf="userInfo['IN_DELEGATION_MODE'] == 'true'" role="menu-item" id="my-orcid-menu-item">
                                        <@orcid.msg 'public-layout.my_orcid'/>
                                    </div >
                                    <div *ngIf="userInfo['IN_DELEGATION_MODE'] == 'false'" id="my-orcid-menu-item">
                                        <@orcid.msg 'public-layout.my_orcid_record'/>
                                    </div >
                                </a>
                            </li>
                            <li role="presentation">
                                {{retrieveUnreadCount()}}
                                <a role="menu-item" ${(nav=="notifications")?then('class="active" ', '')} href="{{getBaseUri()}}/inbox">${springMacroRequestContext.getMessage("workspace.notifications")} <span *ngIf="getUnreadCount > 0">({{getUnreadCount}})</span></a>
                            </li>
                            <li role="presentation">
                                <a ${(nav=="settings")?then('class="active" ', '')} href="{{getBaseUri()}}/account" id="accountSettingMenuLink"><@orcid.msg 'public-layout.account_setting'/></a>
                            </li>
                            
                            <!-- Developer tools -->
                            <li role="presentation" *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']=='true'"><a role="menu-item" ${(nav=="developer-tools")?then('class="active" ', '')}href="{{getBaseUri()}}/group/developer-tools">${springMacroRequestContext.getMessage("workspace.developer_tools")}</a></li>
                            <li *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']!='true'"><a role="menu-item" ${(nav=="developer-tools")?then('class="active" ', '')}href="{{getBaseUri()}}/developer-tools">${springMacroRequestContext.getMessage("workspace.developer_tools")}</a></li>
                            
                            <!-- Admin menu -->
                            <li  role="presentation" role="presentation" *ngIf="userInfo['ADMIN_MENU']"><a role="menu-item" ${(nav=="members")?then('class="active" ', '')}href="{{getBaseUri()}}/manage-members"><@orcid.msg 'admin.members.workspace_link' /></a></li>
                            <li  role="presentation" *ngIf="userInfo['ADMIN_MENU']"><a  role="menu-item" ${(nav=="admin")?then('class="active" ', '')}href="{{getBaseUri()}}/admin-actions"><@orcid.msg 'admin.workspace_link' /></a></li>
                                
                            <!-- Self service menu -->
                            <li  role="presentation" *ngIf="userInfo['SELF_SERVICE_MENU']"><a role="menu-item" ${(nav=="self-service")?then('class="active" ', '')}href="{{getBaseUri()}}/self-service"><@orcid.msg 'workspace.self_service' /></a></li>
                                    
                            <li  role="presentation" class="leaf last"><a role="menu-item" href="{{getBaseUri()}}/content/initiative"><@orcid.msg 'manage_delegators.learn_more.link.text' /></a></li>
                        </ul>
                    </li>

                    <!-- DRUPAL WEBSITE MENUS -->
                    <!-- FOR ORGANIZATIONS -->
                    <li role="presentation" class="expanded" [ngClass]="{'open': mobileMenu.ORGANIZATIONS}" >
                        <a href="{{aboutUri}}/organizations" role="menu-item" (mouseenter)="menuHandler('ORGANIZATIONS', $event)" (click)="menuHandler('ORGANIZATIONS', $event)"><@orcid.msg 'public-layout.for_organizations'/> <span class="more" [ngClass]="{'less dark':mobileMenu.ORGANIZATIONS == true}"></span></a>
                        <ul class="menu lang-fixes"  aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li role="presentation" class="first leaf" [hidden]="!isMobile" >
                                <a role="menu-item" href="{{aboutUri}}/organizations" ><@orcid.msg 'public-layout.for_organizations'/></a>
                            </li>

                            <li role="presentation" class="first leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/funders" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('funders')" class="russian-fix" ><@orcid.msg 'public-layout.funders'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['funders'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['funders'] == true">
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/organizations/funders"><@orcid.msg 'public-layout.funders'/> </a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/funders/learnmore">Learn more</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/funders/outreachresources">Outreach Resources</a>
                                    </li>
                                    <li class="last leaf">
                                        <a href="{{aboutUri}}/about/membership" title="">Membership</a>
                                    </li>
                                </ul>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/institutions" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('institutions')"><@orcid.msg 'public-layout.research_organizations'/> <span class="more" [ngClass]="{'less':secondaryMenuVisible['institutions'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                    <ul class="menu" *ngIf="secondaryMenuVisible['institutions'] == true">
                                        <li class="first leaf">
                                            <a href="{{aboutUri}}/organizations/institutions"><@orcid.msg 'public-layout.research_organizations'/></a>
                                        </li>
                                        <li class="leaf">
                                            <a href="{{aboutUri}}/organizations/institutions/learnmore">Learn more</a>
                                        </li>
                                        <li class="leaf">
                                            <a href="{{aboutUri}}/organizations/institutions/outreachresources">Outreach Resources</a>
                                        </li>
                                            <li class="leaf"><a href="{{aboutUri}}/about/membership" title="">Membership</a>
                                        </li>
                                        <li class="last leaf">
                                            <a href="{{aboutUri}}/organizations/institutions/usecases">Use cases</a>
                                        </li>
                                    </ul>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/publishers" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('publishers')"> <@orcid.msg 'public-layout.publishers'/> <span class="more" [ngClass]="{'less':secondaryMenuVisible['publishers'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['publishers'] == true">
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/organizations/publishers"> <@orcid.msg 'public-layout.publishers'/></a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/publishers/learnmore">Learn more</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/publishers/outreachresources">Outreach Resources</a>
                                    </li>
                                    <li class="last leaf">
                                        <a href="{{aboutUri}}/about/membership" title="">Membership</a>
                                    </li>
                                </ul>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/associations"  (click)="handleMobileMenuOption($event); toggleSecondaryMenu('associations')"><@orcid.msg 'public-layout.associations'/> <span class="more" [ngClass]="{'less':secondaryMenuVisible['associations'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['associations'] == true">
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/organizations/associations"><@orcid.msg 'public-layout.associations'/></a>
                                    </li>
                                    <li class="leaf">
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
                            <li role="presentation" class="last leaf">
                                <a role="menu-item" href="{{aboutUri}}/organizations/integrators" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('integrators')"><@orcid.msg 'public-layout.integrators'/> <span class="more" [ngClass]="{'less':secondaryMenuVisible['integrators'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['integrators'] == true">
                                    <li class="first leaf hidden-sm hidden-md hidden-lg">
                                        <a href="{{aboutUri}}/organizations/integrators"><@orcid.msg 'public-layout.integrators'/></a>
                                    </li>
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/organizations/integrators/API">The ORCID API</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/content/register-client-application-0">Register a Client Application</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/integrators/current">Current Integrations</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/organizations/integrators/integration-chart">Integration Chart</a>
                                    </li>
                                    <li class="last leaf">
                                        <a href="{{aboutUri}}/content/beta-tester-request">Beta Testers</a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                    <!-- ABOUT -->
                    <li role="presentation" class="expanded" [ngClass]="{'open': mobileMenu.ABOUT}"  >
                        <a href="{{aboutUri}}/about" role="menu-item" (mouseenter)="menuHandler('ABOUT', $event)"  (click)="menuHandler('ABOUT', $event)"><@orcid.msg 'public-layout.about'/> <span class="more" [ngClass]="{'less dark':mobileMenu.ABOUT == true}"></span></a>

                        <ul class="menu lang-fixes"  aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li  role="presentation" [hidden]="!isMobile" ><a role="menu-item" href="{{aboutUri}}/about"
                                class="first leaf    "><@orcid.msg
                                    'public-layout.about'/></a></li>
                            <!-- What is ORCID? -->
                            <li role="presentation" class="first expanded">
                                <a role="menu-item" href="{{aboutUri}}/about/what-is-orcid" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('whatIsOrcid')"><@orcid.msg 'public-layout.what_is_orcid'/> <span class="more" [ngClass]="{'less':secondaryMenuVisible['whatIsOrcid'] == true}"></span></a>
                                <ul class="menu" *ngIf="secondaryMenuVisible['whatIsOrcid'] == true" >
                                        <li class="hidden-md hidden-lg hidden-sm visible-xs">
                                            <a href="{{aboutUri}}/about/what-is-orcid"><@orcid.msg 'public-layout.what_is_orcid'/></a>
                                        </li>
                                        <li class="first leaf">
                                            <a href="{{aboutUri}}/about/what-is-orcid/mission-statement" title=""><@orcid.msg 'public-layout.our_mission'/></a>
                                        </li>
                                        <li class="leaf">
                                            <a href="{{aboutUri}}/about/what-is-orcid/our-principles" title=""><@orcid.msg 'public-layout.our_principles'/></a>
                                        </li>
                                        <li class="leaf">
                                            <a href="{{aboutUri}}/content/our-governance">Our Governance</a>
                                        </li>
                                        <li class="last expanded">
                                            <a href="{{aboutUri}}/about/what-is-orcid/policies" (click)="handleMobileMenuOption($event); toggleTertiaryMenu('policies')">Our Policies <span class="more dark" [ngClass]="{'less':tertiaryMenuVisible['policies'] == true}"></span></a>
                                            <ul class="menu" *ngIf="tertiaryMenuVisible['policies'] == true" >
                                                <li class="first leaf"><a
                                                    href="{{aboutUri}}/orcid-dispute-procedures">Dispute
                                                        Procedures</a></li>
                                                <li class="leaf"><a
                                                    href="{{aboutUri}}/footer/privacy-policy" title="">Privacy
                                                        Policy</a></li>
                                                <li class="leaf"><a
                                                    href="{{aboutUri}}/content/orcid-public-client-terms-service">Public
                                                        Client Terms of Service</a></li>
                                                <li class="leaf"><a
                                                    href="{{aboutUri}}/content/orcid-public-data-file-use-policy">Public
                                                        Data File Use Policy</a></li>
                                                <li class="leaf"><a href="{{aboutUri}}/legal">Terms
                                                        and Conditions of Use</a></li>
                                                <li class="last leaf"><a
                                                    href="{{aboutUri}}/trademark-and-id-display-guidelines">Trademark
                                                        and iD Display Guidelines</a></li>
                                            </ul>
                                        </li>
                                    </ul>
                            </li>
                            <!-- The ORCID Team -->
                            <li role="presentation" class="leaf"><a role="menu-item" href="{{aboutUri}}/about/team" title=""><@orcid.msg
                                    'public-layout.the_orcid_team'/></a></li>
                            <!-- The ORCID Comunity -->
                            <li role="presentation" class="expanded" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('community')">
                                <a role="menu-item" href="{{aboutUri}}/about/community"><@orcid.msg 'public-layout.the_orcid_community'/> <span class="more" [ngClass]="{'less':secondaryMenuVisible['community'] == true}"></span></a>
                                <ul class="menu" *ngIf="secondaryMenuVisible['community'] == true">
                                    <li class="hidden-md hidden-lg hidden-sm visible-xs">
                                        <a href="{{aboutUri}}/about/community"><@orcid.msg 'public-layout.the_orcid_community'/></a>
                                    </li>
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/about/community" title=""><@orcid.msg 'public-layout.working_groups'/></a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/about/community/sponsors" title=""><@orcid.msg 'public-layout.sponsors'/></a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/about/community/members" title=""><@orcid.msg 'public-layout.members'/></a>
                                    </li>
                                    <li class="last">
                                        <a href="{{aboutUri}}/about/community/launch-partners" title=""><@orcid.msg 'public-layout.launch_partners'/></a></li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/about/community/orcid-technical-community">Open Source</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/content/partners">Partners</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/content/adoption-and-integration-program">Adoption &amp; Integration Program</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/content/orcid-ambassadors" (click)="handleMobileMenuOption($event); toggleTertiaryMenu('ambassadors')">Ambassadors <span class="more dark" [ngClass]="{'less':tertiaryMenuVisible['ambassadors'] == true}"></span></a>
                                        <ul class="menu" *ngIf="tertiaryMenuVisible['ambassadors']">
                                            <li class="first last leaf">
                                                <a href="{{aboutUri}}/content/orcid-ambassadors-1/outreachresources">Outreach Resources</a>
                                            </li>
                                        </ul>
                                    </li>
                                    <li class="last leaf">
                                        <a href="http://www.cafepress.com/orcid" title="">ORCID Gear</a>
                                    </li>
                                </ul> 
                            </li>
                            <!-- Membership -->
                            <li role="presentation" class="expanded" >
                                <a role="menu-item" href="{{aboutUri}}/about/membership" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('membership')"    ><@orcid.msg 'public-layout.membership'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['membership'] == true}"></span></a>
                                <ul class="menu" *ngIf="secondaryMenuVisible['membership'] == true">
                                    <li class="hidden-md hidden-lg hidden-sm visible-xs">
                                        <a href="{{aboutUri}}/about/membership"><@orcid.msg 'public-layout.membership'/></a>
                                    </li>
                                    <li class="first expanded">
                                        <a href="{{aboutUri}}/about/membership" title="" (click)="handleMobileMenuOption($event); toggleTertiaryMenu('membership')"  ><@orcid.msg 'public-layout.membership_and_subscription'/><span class="more dark" [ngClass]="{'less': tertiaryMenuVisible['membership'] == true}"></span></a>
                                        <ul class="menu" *ngIf="tertiaryMenuVisible['membership']">
                                            <li class="first last leaf">
                                                <a href="{{aboutUri}}/content/membership-comparison">Membership Comparison</a>
                                            </li>
                                        </ul>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/about/membership/standard-member-agreement" title=""><@orcid.msg 'public-layout.standard_member_agreement'/></a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/document/standard-creator-membership-agreement">Standard Creator Member Agreement</a>
                                    </li>
                                    <li class="last leaf">
                                        <a href="{{aboutUri}}/about/community/members" title=""><@orcid.msg 'public-layout.our_members'/></a>
                                    </li>
                                </ul>
                            </li>
                            <!-- News -->
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/about/news/news" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('news')"><@orcid.msg 'public-layout.news'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['news'] == true}"></span></a>
                                <ul class="menu" *ngIf="secondaryMenuVisible['news'] == true">
                                    <li class="hidden-md hidden-lg hidden-sm visible-xs">
                                        <a href="{{aboutUri}}/about/news/news"><@orcid.msg 'public-layout.news'/></a>
                                    </li>
                                    <li class="first leaf">
                                        <a href="{{aboutUri}}/category/newsletter/blog" title="">Blog</a>
                                    </li>
                                    <li class="last leaf">
                                        <a href="{{aboutUri}}/newsletter/subscriptions" title="">Subscribe!</a>
                                    </li>
                                </ul>
                            </li>
                            <!-- Events -->
                            <li role="presentation" class="last expanded">
                                <a role="menu-item" href="{{aboutUri}}/about/events" title=""><@orcid.msg 'public-layout.events'/></a>
                            </li>
                        </ul>
                    </li>
                    <!-- HELP -->
                    <li role="presentation" class="expanded" [ngClass]="{'open': mobileMenu.HELP}"  >
                        <a role="menu-item" href="{{aboutUri}}/help" (mouseenter)="menuHandler('HELP', $event)" (click)="menuHandler('HELP', $event)"><@orcid.msg 'public-layout.help' /> <span class="more" [ngClass]="{'less dark':mobileMenu.HELP == true}"></span></a>
                        <ul  [hidden]="!isMobile" class="menu lang-fixes"  aria-label="submenu">
                            <!-- Mobile view Only -->
                            <li role="presentation" class="first leaf    "  [hidden]="!isMobile">
                                <a role="menu-item" href="{{aboutUri}}/help"><@orcid.msg 'public-layout.help'/></a>
                            </li>
                            <li role="presentation" class="first leaf">
                                <a role="menu-item" href="{{aboutUri}}/faq-page" title=""><@orcid.msg 'public-layout.faq'/></a>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="{{aboutUri}}/help/contact-us" title=""><@orcid.msg 'public-layout.contact_us'/></a>
                            </li>
                            <li role="presentation" class="leaf">
                                <a role="menu-item" href="https://support.orcid.org/hc/en-us/community/topics" title=""><@orcid.msg 'public-layout.give_feedback'/></a>
                            </li>
                            <li role="presentation" class="last leaf">
                                <a role="menu-item" href="<@orcid.msg 'common.kb_uri_help_center_home'/>" title=""><@orcid.msg 'public-layout.knowledge_base'/></a>
                            </li>
                        </ul>
                    </li>
                    
                    <!-- SIGN IN/OUT -->
                    <li role="presentation" class="last leaf" [ngClass]="{'open': mobileMenu.SIGNIN}" (mouseenter)="menuHandler('SIGNIN', $event)" (click)="menuHandler('SIGNIN', $event)">                    
                        <a *ngIf="!userInfo['REAL_USER_ORCID']" href="{{getBaseUri()}}/signin" role="menu-item"><@orcid.msg 'public-layout.sign_in'/></a>                    
                        <a *ngIf="userInfo['REAL_USER_ORCID']" href="{{getBaseUri()}}/signout" role="menu-item"><@orcid.msg 'public-layout.sign_out'/></a>
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