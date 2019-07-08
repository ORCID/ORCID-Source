declare var getWindowWidth: any;

//Import all the angular components


import { AfterViewInit, Component, OnDestroy, OnInit, ChangeDetectorRef, HostListener } 
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
export class Header2Component  {
    getUnreadCount: any;
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
    userMenu: String;
    searchDropdownOpen = false; 
    mobileMenu: {} = null
    openMobileMenu = false
    isMobile = false

    constructor(
        private notificationsSrvc: NotificationsService,
        private featuresService: FeaturesService,
        private commonSrvc: CommonService, 
        private ref: ChangeDetectorRef
    ) {
        this.getUnreadCount = 0;
        this.headerSearch = {};
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
                this.userMenu = data.messages['ENABLE_USER_MENU']     
                
                
                this.mobileMenu = {
                    HELP: false,
                    ABOUT: false, 
                    ORGANIZATIONS: false,
                    RESEARCHERS: false,
                    SIGNIN: location.href.indexOf('signin') >= 0
                }

            },
            error => {
                console.log('header.component.ts: unable to fetch configInfo', error);                
            } 
        );

    }
    
    filterChange(): void {
        this.searchFilterChanged = true;
    };


    retrieveUnreadCount(): any {
        if( this.notificationsSrvc.retrieveCountCalled == false ) {
            this.notificationsSrvc.retrieveUnreadCount()
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
    
    getBaseUri(): String {
        return getBaseUri();
    };

    clickDropdown (value) {
        this.searchDropdownOpen = !this.searchDropdownOpen;
        if (value) {
            this.headerSearch.searchOption = value
        }
    }

    closeDropdown () {
        this.searchDropdownOpen = false;
    }

    menuHandler (value, $event) {

        // Ignore first click on mobile if not is SIGNIN 
        if (this.isMobile && value !== "SIGNIN") {
                $event.preventDefault()
            
        }

        // If is mobile ignore no-click events
        if ($event.type === "click" || !this.isMobile) {
            if (!this.mobileMenu[value]){
                Object.keys(this.mobileMenu).forEach ( item => {
                    this.mobileMenu[item] = item === value
                })
                this.ref.detectChanges();
            } else {
                Object.keys(this.mobileMenu).forEach ( item => {
                    this.mobileMenu[item] = false
                })
                this.ref.detectChanges();
            }
        }
        
    }

    mouseLeave( ){
        if (!this.isMobile) {
            Object.keys(this.mobileMenu).forEach ( item => {
                this.mobileMenu[item] = item === "SIGNIN" && location.href.indexOf('signin') != -1
            })
        }
    }

    toggleMenu() {
        this.openMobileMenu = !this.openMobileMenu; 
    }

    toggleSecondaryMenu(submenu): void {
        
        if (!this.secondaryMenuVisible[submenu]) {
            this.secondaryMenuVisible = {}
            this.secondaryMenuVisible[submenu] = !this.secondaryMenuVisible[submenu];
        } else { 
            this.secondaryMenuVisible = {}
        }
    };

    handleMobileMenuOption( $event ): void{
        let w = getWindowWidth();           
        
        $event.preventDefault();
        
        if( w > 840) {               
            window.location.href = $event.target.getAttribute('href');
        }
    };

    toggleTertiaryMenu(submenu): void {
        
        if (!this.tertiaryMenuVisible[submenu]) {
            this.tertiaryMenuVisible = {}
            this.tertiaryMenuVisible[submenu] = !this.tertiaryMenuVisible[submenu];
        } else { 
            this.tertiaryMenuVisible = {}
        }
    };


    ngOnInit() {
        this.isMobile = window.innerWidth < 840.999;
        this.headerSearch.searchOption='registry'
        this.headerSearch.searchInput = ''
    }

    @HostListener('window:resize', ['$event'])
        onResize(event) {
        this.isMobile = window.innerWidth < 840.999;
    }
}
