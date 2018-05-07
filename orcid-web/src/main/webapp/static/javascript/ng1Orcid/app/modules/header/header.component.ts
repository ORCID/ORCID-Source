declare var getWindowWidth: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { NotificationsService } 
    from '../../shared/notifications.service.ts'; 


@Component({
    selector: 'header-ng2',
    template:  scriptTmpl("header-ng2-template")
})
export class HeaderComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    
    conditionsActive: boolean;
    filterActive: boolean;
    getUnreadCount: any;
    menuVisible: boolean;
    searchFilterChanged: boolean;
    searchVisible: boolean;
    secondaryMenuVisible: any;
    settingsVisible: boolean;
    tertiaryMenuVisible: any;

    constructor(
        private notificationsSrvc: NotificationsService
    ) {
        this.conditionsActive = false;
        this.filterActive = false;
        this.getUnreadCount = 0;
        this.menuVisible = false;
        this.searchFilterChanged = false;
        this.searchVisible = false;
        this.secondaryMenuVisible = {};
        this.settingsVisible = false;
        this.tertiaryMenuVisible = {};
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

    hideSearchFilter(): void{
        let inputValue = document.getElementById('input1') as HTMLInputElement;
        let searchInputValue = inputValue.value;
        if (searchInputValue === ""){
            setTimeout(function() {
                if ( this.searchFilterChanged === false ) {
                    this.filterActive = false;
                }
            }, 3000);
        }
    };

    isCurrentPage(path): any {
        return window.location.href.startsWith(orcidVar.baseUri + '/' + path);
    };

    onResize(event?): void {
        //console.log("resize", event);
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

    searchBlur(): void {     
        this.hideSearchFilter();
        this.conditionsActive = false;        
    };

    searchFocus(): void {
        this.filterActive = true;
        this.conditionsActive = true;
    };
    
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

        if(!(this.isCurrentPage('my-orcid') || this.isCurrentPage('inbox'))){
            this.notificationsSrvc.retrieveUnreadCount();

            this.notificationsSrvc.retrieveUnreadCount()
            .takeUntil(this.ngUnsubscribe)
            .subscribe(
                data => {
                    console.log('notificationData', data);
                },
                error => {
                    //console.log('verifyEmail', error);
                } 
            );

        }
    }; 
}
