<script type="text/ng-template" id="header2-ng2-template">

<div class="new-menu header2" >
    <div  *ngIf="!isOauth && mobileMenu"> 

        <div class="container">
            <div class="logo-search-bar">
                <div class="logo" aria-label="ORCID logo" role="banner" aria-label="orcid logo"> 
                    <a href="{{aboutUri}}"><img *ngIf="assetsPath != null" src="{{assetsPath + '/img/orcid-logo.svg'}}" alt="ORCID logo" /></a>
                    <div class="slogan">${springMacroRequestContext.getMessage("public-layout.logo.tagline")?replace("<br />", " ")?replace("'", "\\'")}</div>
                     
                    <div class="menu-control">
                        <user-menu *ngIf="isMobile && !openMobileMenu"></user-menu>
                        <language-ng2 *ngIf="openMobileMenu"></language-ng2>
                        <span (click)="toggleMenu()" role="navigation"  tabindex="0" aria-label="main menu" [hidden]="!openMobileMenu" style="height: 35px" class="close" alt="close menu"> </span>
                        <img (click)="toggleMenu()" role="navigation"  tabindex="0" aria-label="main menu" [hidden]="openMobileMenu" style="height: 35px" src="{{assetsPath + '/img/glyphicon-menu.svg'}}" alt="open menu"/>
                    </div>
                </div>
                <div class="search" >
                <div class="dropdown-menus-container">
                        <user-menu *ngIf="!isMobile"></user-menu>
                        <language-ng2 *ngIf="!isMobile"></language-ng2>
                </div>
                   
                    <div class="form-group " role="presentation">
                        <div class="search-container" role="search"> 
                        <div class="input-group" role="presentation">
                            <input (keyup.enter)="searchSubmit()" [(ngModel)]="headerSearch.searchInput" aria-label="search"  class="form-control" name="search" type="text" placeholder="${springMacroRequestContext.getMessage("public-layout.search")?replace("<br />", " ")?replace("'", "\\'")}"/>
                            <div tabindex="0" role="button" (keyup.enter)="searchSubmit()" (click)="searchSubmit()" aria-label="${springMacroRequestContext.getMessage("orcid_bio_search.btnsearch")?replace("<br />", " ")?replace("'", "\\'")}" aria-label="${springMacroRequestContext.getMessage("orcid_bio_search.btnsearch")?replace("<br />", " ")?replace("'", "\\'")}" class="input-group-addon" role="presentation">
                                <span class="glyphicon glyphicon-search" ></span> 
                            </div>
                        </div>
                    </div>        
                </div>
                </div>
            </div>
        </div>

        <div [ngClass]="{'mobile': isMobile}"  class="menu-bar"  [hidden]="!openMobileMenu && isMobile"  (mouseleave)="mouseLeave()" role="navigation" aria-label="main menu">
                <!--  Desktop / Tablet menu -->             
                <div class="container container-menu"> 
                <ul class="menu" resize  role="presentation" role="menu">

                    <!-- ABOUT -->
                    <li role="presentation" class="expanded"  >
                        <a href="{{aboutUri}}/what-is-orcid" role="menuitem" >{{'${springMacroRequestContext.getMessage("wp-infosite-header.about")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }}</a>
                    </li>
    
                     <!-- FOR RESEARCHERS -->
                    <li role="presentation" class="expanded">
                        <a href="{{aboutUri}}/help-getting-started-with-your-orcid/" role="menuitem" >{{'${springMacroRequestContext.getMessage("wp-infosite-header.forResearchers")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }} </a>
                    </li>

                    <!-- Membership -->
                    <li role="presentation" class="expanded">
                        <a href="{{aboutUri}}/about-membership" role="menuitem" >{{'${springMacroRequestContext.getMessage("wp-infosite-header.membership")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }} </a>
                    </li>

                    <!-- DOCUMENTATION -->
                    <li role="presentation" class="expanded">
                        <a href="{{aboutUri}}/documentation/" role="menuitem" >{{'${springMacroRequestContext.getMessage("wp-infosite-header.documentation")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }} </a>
                    </li>

                    <!-- RESOURCES -->
                    <li role="presentation" class="expanded">
                        <a href="{{aboutUri}}/resources/" role="menuitem" >{{'${springMacroRequestContext.getMessage("wp-infosite-header.resources")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }} </a>
                    </li>

                    <!-- NEWS & EVENTS -->
                    <li role="presentation" class="expanded">
                        <a href="{{aboutUri}}" role="menuitem" >{{'${springMacroRequestContext.getMessage("wp-infosite-header.newsAndEvents")?replace("<br />", " ")?replace("'", "\\'")} '| uppercase }} </a>
                    </li>
                    <li role="presentation" class="last leaf ${(nav=="signin")?then('open', '')} " [ngClass]="{'hover': mobileMenu.SIGNIN}" (mouseenter)="menuHandler('SIGNIN', $event)" (focus)="menuHandler('SIGNIN', $event)" (click)="menuHandler('SIGNIN', $event)">                    
                        <div class="mobile-menu-sign-in">
                            <a  class="mobile-menu-sign-in-url" *ngIf="!userInfo['REAL_USER_ORCID']" href="{{getBaseUri()}}/signin" role="menuitem">{{'${springMacroRequestContext.getMessage("public-layout.sign_in")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase  }}/{{'${springMacroRequestContext.getMessage("header.register")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}</a>                    
                            <a class="mobile-menu-sign-in-url" *ngIf="userInfo['REAL_USER_ORCID']" href="{{getBaseUri()}}/signout" role="menuitem">{{'${springMacroRequestContext.getMessage("public-layout.sign_out")?replace("<br />", " ")?replace("'", "\\'")}  '| uppercase }}</a>
                        </div>
                    </li>
                </ul>  
                </div>
        </div>
    </div>   
</div> 

</script>