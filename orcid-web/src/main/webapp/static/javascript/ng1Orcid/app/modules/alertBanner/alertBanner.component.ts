declare var OrcidCookie: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

@Component({
    selector: 'alert-banner-ng2',
    template:  scriptTmpl("alert-banner-ng2-template")
})
export class AlertBannerComponent implements OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    
    cookiesEnabled: boolean;
    showCookieNotification: boolean;

    constructor(
    ) {
        this.cookiesEnabled = true;
        this.showCookieNotification = true;
    }

    dismissCookieNotification(): void{
        console.log("dismiss");
        this.showCookieNotification = false;
        if(this.cookiesEnabled){
            OrcidCookie.setCookie("orcidCookiePolicyAlert", "dont show message", 365);
        }
    };

    ngOnInit() {
        if(OrcidCookie.getCookie("orcidCookiePolicyAlert")!=undefined){
            this.cookiesEnabled = true; 
            this.showCookieNotification = false;
        } else if(!OrcidCookie.checkIfCookiesEnabled()){
            this.cookiesEnabled = false; 
            this.showCookieNotification = true;  
        } else {
            this.cookiesEnabled = true; 
            this.showCookieNotification = true;
        } 
    }; 
    
    getBaseUri() : String {
        return getBaseUri();
    };
}
