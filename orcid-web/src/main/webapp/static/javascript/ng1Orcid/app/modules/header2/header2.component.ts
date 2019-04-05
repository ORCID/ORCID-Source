declare var getWindowWidth: any;

//Import all the angular components


import { AfterViewInit, Component, OnDestroy, OnInit, ChangeDetectorRef } 
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
    searchDropdownOpen = false; 
    mobileMenu = {
        HELP: false,
        ABOUT: false, 
        ORGANIZATIONS: false,
        RESEARCHERS: true
    }

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

    clickDropdown (value, $event) {
        this.searchDropdownOpen = !this.searchDropdownOpen;
        if (value) {
            this.headerSearch.searchOption = value
        }
        $event.preventDefault();
    }

    menuHandler (value, $event) {

        // Ignore first click on mobile
        if ($( window ).width() < 600) {
            if (this.mobileMenu[value] == false) {
                $event.preventDefault()
            }
        }

        // If is mobile ignore no-click events
        if ($event.type === "click" || $( window ).width() >= 600) {
            Object.keys(this.mobileMenu).forEach ( item => {
                this.mobileMenu[item] = item === value
            })
            this.ref.detectChanges();
        }
        
    }

    mouseLeave( ){
        //If is not mobile
        if ($( window ).width() > 600) {
            Object.keys(this.mobileMenu).forEach ( item => {
                this.mobileMenu[item] = item === "RESEARCHERS"
            })
        }
    }
}
