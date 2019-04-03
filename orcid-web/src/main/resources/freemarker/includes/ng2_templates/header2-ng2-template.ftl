<script type="text/ng-template" id="header2-ng2-template">

<div class="header2">
    <#if oauthError??>            
    <ng-container *ngIf="isOauth"> 
        <div class="row top-header">
            <div class="col-md-6 col-md-offset-3 centered logo topBuffer">
                <a href="https://orcid.org" alt="ORCID logo">
                    <img src="${staticCdn}/img/orcid-logo-208-64.png" width="208px" height="64px" alt="ORCID logo">
                </a>
            </div>       
        </div>
    </ng-container>
    </#if>

    <div *ngIf="!isOauth"> 


        <div class="logo-search-bar container">
            <div class="logo"> 
                <a href="{{aboutUri}}"><img *ngIf="assetsPath != null" src="{{assetsPath + '/img/orcid-logo.png'}}" alt="ORCID logo" /></a>
                <div class="slogan"><@orcid.msg 'public-layout.logo.tagline'/></div>
            </div>
            <div class="search">
                        <!-- Search Form  -->               
            <form id="form-search" (ngSubmit)="searchSubmit()" *ngIf="searchVisible == true">
                <div id="search-box">
                    <input type="search" id="searchInput" [(ngModel)]="headerSearch.searchInput" name="searchInput"
                        (focus)="searchFocus()" (blur)="searchBlur()"
                        placeholder="<@orcid.msg 'public-layout.search'/>" />
                </div>

                <div class="bar">
                    <fieldset class="search_options" *ngIf="filterActive == true"
                        >
                        <input type="radio" name="searchOption" id="filter_registry" [(ngModel)]="headerSearch.searchOption" name="searchOption" (click)="filterChange()"
                            value="registry" checked /> 
                        <label for="filter_registry"><@orcid.msg 'public-layout.search.choice.registry'/></label> 
                        <input type="radio" name="searchOption" (click)="filterChange()"id="filter_website" [(ngModel)]="headerSearch.searchOption" value="website" />
            		    <label for="filter_website"><@orcid.msg 'public-layout.search.choice.website'/></label>
                    </fieldset>
                </div>

                <div class="conditions" *ngIf="conditionsActive == true" >
                    <p>                         
                        <@orcid.msg 'public-layout.search.terms1'/><a
                            href="{{aboutUri}}/legal"><@orcid.msg
                            'public-layout.search.terms2'/></a><@orcid.msg
                        'public-layout.search.terms3'/>
                    </p>
                </div>

                <div class="top-buttons">
                    <button type="submit" class="search-button">
                        <i class="icon-orcid-search"></i>
                    </button>
                    <a href="{{getBaseUri()}}/orcid-search/search"
                    class="settings-button" title="<@orcid.msg
                    'public-layout.search.advanced'/>"><i class="glyphicon glyphicon-cog"></i></a>
                </div>
            </form>
            </div>
        </div>

        <div class="menu-bar">
                <!--  Desktop / Tablet menu -->             
                <ul class="menu container" *ngIf="menuVisible == true"  resize>
                    <!-- FOR RESEARCHERS -->
                    <li class="first expanded active-trail">
                        <a href="{{aboutUri}}/about/what-is-orcid/mission" (click)="handleMobileMenuOption($event)" title=""><@orcid.msg 'public-layout.for_researchers'/></a>
                        <ul class="menu lang-fixes" *ngIf="!userInfo['REAL_USER_ORCID']">
                            <!-- Mobile view Only -->
                            <li class="leaf hidden-md hidden-lg hidden-sm visible-xs"><a href="{{getBaseUri()}}" title=""><@orcid.msg 'public-layout.for_researchers'/></a></li>
                    
                            <!-- Menu -->
                            <li class="leaf last"><a ${(nav=="signin")?then('class="active" ', '')} href="{{getBaseUri()}}/signin"><@orcid.msg 'public-layout.sign_in'/></a></li>                                   
                            <li class="leaf last"><a ${(nav=="register")?then('class="active" ', '')} href="{{getBaseUri()}}/register"><@orcid.msg 'public-layout.register'/></a></li>                                                                                                                          
                            <li class="leaf last"><a href="{{getBaseUri()}}/content/initiative"><@orcid.msg 'manage_delegators.learn_more.link.text' /></a></li>
                        </ul>
                        <ul class="menu lang-fixes" *ngIf="userInfo['REAL_USER_ORCID']">
                            <li>
                                <a ${(nav=="record")?then('class="active" ', '')}href="{{getBaseUri()}}/my-orcid">
                                    <div *ngIf="userInfo['IN_DELEGATION_MODE'] == 'true'">
                                        <@orcid.msg 'public-layout.my_orcid'/>
                                    </div>
                                    <div *ngIf="userInfo['IN_DELEGATION_MODE'] == 'false'">
                                        <@orcid.msg 'public-layout.my_orcid_record'/>
                                    </div>
                                </a>
                            </li>
                            <li>
                                {{retrieveUnreadCount()}}
                                <a ${(nav=="notifications")?then('class="active" ', '')} href="{{getBaseUri()}}/inbox">${springMacroRequestContext.getMessage("workspace.notifications")} <span *ngIf="getUnreadCount > 0">({{getUnreadCount}})</span></a>
                            </li>
                            <li>
                                <a ${(nav=="settings")?then('class="active" ', '')} href="{{getBaseUri()}}/account" id="accountSettingMenuLink"><@orcid.msg 'public-layout.account_setting'/></a>
                            </li>
                            
                            <!-- Developer tools -->
                            <li *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']=='true'"><a ${(nav=="developer-tools")?then('class="active" ', '')}href="{{getBaseUri()}}/group/developer-tools">${springMacroRequestContext.getMessage("workspace.developer_tools")}</a></li>
                            <li *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']!='true'"><a ${(nav=="developer-tools")?then('class="active" ', '')}href="{{getBaseUri()}}/developer-tools">${springMacroRequestContext.getMessage("workspace.developer_tools")}</a></li>
                            
                            <!-- Admin menu -->
                            <li *ngIf="userInfo['ADMIN_MENU']"><a ${(nav=="members")?then('class="active" ', '')}href="{{getBaseUri()}}/manage-members"><@orcid.msg 'admin.members.workspace_link' /></a></li>
                            <li *ngIf="userInfo['ADMIN_MENU']"><a ${(nav=="admin")?then('class="active" ', '')}href="{{getBaseUri()}}/admin-actions"><@orcid.msg 'admin.workspace_link' /></a></li>
                                
                            <!-- Self service menu -->
                            <li *ngIf="userInfo['SELF_SERVICE_MENU']"><a ${(nav=="self-service")?then('class="active" ', '')}href="{{getBaseUri()}}/self-service"><@orcid.msg 'workspace.self_service' /></a></li>
                                    
                            <li class="leaf last"><a href="{{getBaseUri()}}/content/initiative"><@orcid.msg 'manage_delegators.learn_more.link.text' /></a></li>
                        </ul>
                    </li>

                    <!-- DRUPAL WEBSITE MENUS -->
                    <!-- FOR ORGANIZATIONS -->
                    <li class="expanded">
                        <a href="{{aboutUri}}/organizations" (click)="handleMobileMenuOption($event)"><@orcid.msg 'public-layout.for_organizations'/></a>
                        <ul class="menu lang-fixes">
                            <!-- Mobile view Only -->
                            <li class="first leaf hidden-md hidden-lg hidden-sm visible-xs">
                                <a href="{{aboutUri}}/organizations"><@orcid.msg 'public-layout.for_organizations'/></a>
                            </li>

                            <li class="first leaf">
                                <a href="{{aboutUri}}/organizations/funders" class="russian-fix" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('funders')"><@orcid.msg 'public-layout.funders'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['funders'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['funders'] == true">
                                    <li class="hidden-sm hidden-md hidden-lg">
                                        <a href="{{aboutUri}}/organizations/funders"><@orcid.msg 'public-layout.funders'/></a>
                                    </li>
                                    <li class="first leaf">
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
                            <li class="leaf">
                                <a href="{{aboutUri}}/organizations/institutions" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('institutions')"><@orcid.msg 'public-layout.research_organizations'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['institutions'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['institutions'] == true">
                                    <li class="hidden-sm hidden-md hidden-lg">
                                        <a href="{{aboutUri}}/organizations/institutions"><@orcid.msg 'public-layout.research_organizations'/></a>
                                    </li>
                                    <li class="first leaf">
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
                            <li class="leaf">
                                <a href="{{aboutUri}}/organizations/publishers" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('publishers')"> <@orcid.msg 'public-layout.publishers'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['publishers'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['publishers'] == true">
                                    <li class="hidden-sm hidden-md hidden-lg">
                                        <a href="{{aboutUri}}/organizations/publishers"> <@orcid.msg 'public-layout.publishers'/></a>
                                    </li>
                                    <li class="first leaf">
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
                            <li class="leaf">
                                <a href="{{aboutUri}}/organizations/associations" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('associations')"><@orcid.msg 'public-layout.associations'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['associations'] == true}"></span></a> <!-- Updated according Drupal website structure -->
                                <ul class="menu" *ngIf="secondaryMenuVisible['associations'] == true">
                                    <li class="hidden-sm hidden-md hidden-lg">
                                        <a href="{{aboutUri}}/organizations/associations"><@orcid.msg 'public-layout.associations'/></a>
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
                                <a href="{{aboutUri}}/organizations/integrators" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('integrators')"><@orcid.msg 'public-layout.integrators'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['integrators‰'] == true}"></span></a> <!-- Updated according Drupal website structure -->
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
                    <li class="expanded"><a href="{{aboutUri}}/about" (click)="handleMobileMenuOption($event)"><@orcid.msg
                            'public-layout.about'/></a>

                        <ul class="menu lang-fixes">
                            <!-- Mobile view Only -->
                            <li><a href="{{aboutUri}}/about"
                                class="first leaf hidden-md hidden-lg hidden-sm visible-xs"><@orcid.msg
                                    'public-layout.about'/></a></li>
                            <!-- What is ORCID? -->
                            <li class="first expanded">
                                <a href="{{aboutUri}}/about/what-is-orcid" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('whatIsOrcid')"><@orcid.msg 'public-layout.what_is_orcid'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['whatIsOrcid'] == true}"></span></a>
                                <ul class="menu" *ngIf="secondaryMenuVisible['whatIsOrcid'] == true">
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
                                        <a href="{{aboutUri}}/about/what-is-orcid/policies" (click)="handleMobileMenuOption($event); toggleTertiaryMenu('policies')">Our Policies</a>
                                        <ul class="menu" *ngIf="tertiaryMenuVisible['policies'] == true">
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
                            <li class="leaf"><a href="{{aboutUri}}/about/team" title=""><@orcid.msg
                                    'public-layout.the_orcid_team'/></a></li>
                            <!-- The ORCID Comunity -->
                            <li class="expanded">
                                <a href="{{aboutUri}}/about/community" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('community')"><@orcid.msg 'public-layout.the_orcid_community'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['community'] == true}"></span></a>
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
                                        <a href="{{aboutUri}}/about/community/launch-partners" title="">Launch Partners</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/about/community/orcid-technical-community">Open Source</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/content/partners">Partners</a>
                                    </li>
                                    <li class="leaf">
                                        <a href="{{aboutUri}}/content/adoption-and-integration-program">Adoption &amp; Integration Program</a>
                                    </li>
                                    <li class="expanded">
                                        <a href="{{aboutUri}}/content/orcid-ambassadors">Ambassadors</a>
                                        <ul class="menu">
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
                            <li class="expanded">
                                <a href="{{aboutUri}}/about/membership" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('membership')"><@orcid.msg 'public-layout.membership'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['membership'] == true}"></span></a>
                                <ul class="menu" *ngIf="secondaryMenuVisible['membership'] == true">
                                    <li class="hidden-md hidden-lg hidden-sm visible-xs">
                                        <a href="{{aboutUri}}/about/membership"><@orcid.msg 'public-layout.membership'/></a>
                                    </li>
                                    <li class="first expanded">
                                        <a href="{{aboutUri}}/about/membership" title=""><@orcid.msg 'public-layout.membership_and_subscription'/></a>
                                        <ul class="menu">
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
                            <li class="leaf">
                                <a href="{{aboutUri}}/about/news/news" (click)="handleMobileMenuOption($event); toggleSecondaryMenu('news')"><@orcid.msg 'public-layout.news'/><span class="more" [ngClass]="{'less':secondaryMenuVisible['news'] == true}"></span></a>
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
                            <li class="last expanded">
                                <a href="{{aboutUri}}/about/events" title=""><@orcid.msg 'public-layout.events'/></a>
                            </li>
                        </ul>
                    </li>
                    <!-- HELP -->
                    <li class="expanded">
                        <a href="{{aboutUri}}/help" (click)="handleMobileMenuOption($event)"><@orcid.msg 'public-layout.help'/></a>
                        <ul class="menu lang-fixes">
                            <!-- Mobile view Only -->
                            <li class="first leaf hidden-md hidden-lg hidden-sm visible-xs">
                                <a href="{{aboutUri}}/help"><@orcid.msg 'public-layout.help'/></a>
                            </li>
                            <li class="first leaf">
                                <a href="{{aboutUri}}/faq-page" title=""><@orcid.msg 'public-layout.faq'/></a>
                            </li>
                            <li class="leaf">
                                <a href="{{aboutUri}}/help/contact-us" title=""><@orcid.msg 'public-layout.contact_us'/></a>
                            </li>
                            <li class="leaf">
                                <a href="https://support.orcid.org/hc/en-us/community/topics" title=""><@orcid.msg 'public-layout.give_feedback'/></a>
                            </li>
                            <li class="last leaf">
                                <a href="<@orcid.msg 'common.kb_uri_help_center_home'/>" title=""><@orcid.msg 'public-layout.knowledge_base'/></a>
                            </li>
                        </ul>
                    </li>
                    
                    <!-- SIGN IN/OUT -->
                    <li class="last leaf">                    
                        <a *ngIf="!userInfo['REAL_USER_ORCID']" href="{{getBaseUri()}}/signin"><@orcid.msg 'public-layout.sign_in'/></a>                    
                        <a *ngIf="userInfo['REAL_USER_ORCID']" href="{{getBaseUri()}}/signout"><@orcid.msg 'public-layout.sign_out'/></a>
                    </li>                    

                </ul>  
            <div class="header-secondary-bar-row">
            </div>
        </div>

    </div>   
</div> 
</script>