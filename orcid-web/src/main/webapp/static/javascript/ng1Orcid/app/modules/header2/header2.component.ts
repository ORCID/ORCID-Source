declare var getWindowWidth: any;

//Import all the angular components


import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Subject } 
    from 'rxjs';
    
import { takeUntil } 
    from 'rxjs/operators';
    
import { NotificationsService } 
    from '../../shared/notifications.service'; 

import { CommonService } 
    from '../../shared/common.service';
    
import { FeaturesService }
    from '../../shared/features.service';

@Component({
    selector: 'header2-ng2',
    template: scriptTmpl("header2-ng2-template")
})
export class Header2Component implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    getUnreadCount: any;
    menuVisible: boolean;
    headerSearch: any;
    searchFilterChanged: boolean;
    searchVisible: boolean;
    secondaryMenuVisible: any;
    settingsVisible: boolean;
    tertiaryMenuVisible: any;
    userInfo: any;
    isOauth: boolean = false;
    isPublicPage: boolean = false;
    profileOrcid: string = null;
    showSurvey = this.featuresService.isFeatureEnabled('SURVEY');
    assetsPath: String;
    aboutUri: String;
    liveIds: String;    
    searchDropdownOpen = false; 

    constructor(
        private notificationsSrvc: NotificationsService,
        private featuresService: FeaturesService,
        private commonSrvc: CommonService
    ) {
        this.getUnreadCount = 0;
        this.headerSearch = {};
        this.menuVisible = false;
        this.searchFilterChanged = false;
        this.searchVisible = false;
        this.secondaryMenuVisible = {};
        this.settingsVisible = false;
        this.tertiaryMenuVisible = {};
        const urlParams = new URLSearchParams(window.location.search);
        this.isOauth = (urlParams.has('client_id') && urlParams.has('redirect_uri'));
        this.isPublicPage = this.commonSrvc.isPublicPage;
        if(this.isPublicPage) {                        
            this.userInfo = this.commonSrvc.publicUserInfo$
            .subscribe(
                data => {
                    this.userInfo = data;                
                },
                error => {
                    console.log('header.component.ts: unable to fetch publicUserInfo', error);
                    this.userInfo = {};
                } 
            );
        } else {
            this.userInfo = this.commonSrvc.userInfo$
            .subscribe(
                data => {
                    this.userInfo = data;                
                },
                error => {
                    console.log('header.component.ts: unable to fetch userInfo', error);
                    this.userInfo = {};
                } 
            );
        }  
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.assetsPath = data.messages['STATIC_PATH'];
                this.aboutUri = data.messages['ABOUT_URI'];
                this.liveIds = data.messages['LIVE_IDS'];                
            },
            error => {
                console.log('header.component.ts: unable to fetch configInfo', error);                
            } 
        );
    }
    
    filterChange(): void {
        this.searchFilterChanged = true;
    };

    handleMobileMenuOption( $event ): void{
        let w = getWindowWidth();           
        
        $event.preventDefault();
        
        if( w > 767) {               
            window.location.href = $event.target.getAttribute('href');
        }
    };

    isCurrentPage(path): any {
        return window.location.href.startsWith(getBaseUri() + '/' + path);
    };

    onResize(event?): void {
        let windowWidth = getWindowWidth();
        if(windowWidth > 767){ /* Desktop view */
            this.menuVisible = true;
            this.searchVisible = true;
            this.settingsVisible = true;
        }else{
            this.menuVisible = false;
            this.searchVisible = false;
            this.settingsVisible = false;
        }
    };

    retrieveUnreadCount(): any {
        if( this.notificationsSrvc.retrieveCountCalled == false ) {
            this.notificationsSrvc.retrieveUnreadCount()
            .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
            .subscribe(
                data => {
                    this.getUnreadCount = data;
                },
                error => {
                    //console.log('verifyEmail', error);
                } 
            );
        }
    };

    searchSubmit(): void {
        if (this.headerSearch.searchOption=='website'){
            window.location.assign(getBaseUri() + '/search/node/' + encodeURIComponent(this.headerSearch.searchInput));
        }
        if(this.headerSearch.searchOption=='registry'){
            window.location.assign(getBaseUri()
                    + "/orcid-search/quick-search/?searchQuery="
                    + encodeURIComponent(this.headerSearch.searchInput));
        }
    }
  
    toggleMenu(): void {
        this.menuVisible = !this.menuVisible;
        this.searchVisible = false;
        this.settingsVisible = false;     
    };
    
    toggleSearch(): void {
        this.searchVisible = !this.searchVisible;
        this.menuVisible = false;     
        this.settingsVisible = false;
    };

    toggleSecondaryMenu(submenu): void {
        this.secondaryMenuVisible[submenu] = !this.secondaryMenuVisible[submenu];
    };

    toggleSettings(): void {
        this.settingsVisible = !this.settingsVisible;
        this.menuVisible = false;
        this.searchVisible = false;
    };
    
    toggleTertiaryMenu(submenu): void {
        this.tertiaryMenuVisible[submenu] = !this.tertiaryMenuVisible[submenu];
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.onResize(); 
        this.headerSearch.searchOption = 'registry';         
    }; 
    
    getBaseUri(): String {
        return getBaseUri();
    };

    clickDropdown (value) {
        this.searchDropdownOpen = !this.searchDropdownOpen;
        if (value) {
            this.headerSearch.searchOption = value
        }
    }
}
