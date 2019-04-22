declare var OrcidCookie: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil, shareReplay } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service';

@Component({
    selector: 'alert-banner-ng2',
    template:  scriptTmpl("alert-banner-ng2-template")
})
export class AlertBannerComponent implements OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    
    baseDomainRemoveProtocol: string;
    cookiesEnabled: boolean;
    dismissTestSiteNotificationAllowed: boolean;
    showCookieNotification: boolean;
    showTestSiteNotification: boolean;

    constructor(
        private commonSrvc: CommonService
    ) {
        this.cookiesEnabled = true;
        this.dismissTestSiteNotificationAllowed = false;
        this.showCookieNotification = true;
        this.showTestSiteNotification = false;
    }

    dismissCookieNotification(): void{
        this.showCookieNotification = false;
        if(this.cookiesEnabled){
            OrcidCookie.setCookie("orcidCookiePolicyAlert", "dont show message", 365);
        }
    };

    dismissTestSiteNotification(): void{
        this.showTestSiteNotification = false;
        if(this.cookiesEnabled){
            OrcidCookie.setCookie("testWarningCookie", "dont show message", 365);
        }
    };

    getBaseUri() : String {
        return getBaseUri();
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.baseDomainRemoveProtocol = data.messages['BASE_DOMAIN_RM_PROTOCALL']; 
                if(this.baseDomainRemoveProtocol != "orcid.org"){
                    if(OrcidCookie.getCookie("testWarningCookie")!=undefined){
                        this.showTestSiteNotification = false;
                    } else {
                        this.showTestSiteNotification = true;
                    }
                    if(this.baseDomainRemoveProtocol.indexOf("sandbox") > -1){
                        this.dismissTestSiteNotificationAllowed = false;
                    } else {
                        this.dismissTestSiteNotificationAllowed = true;
                    }
                }              
            },
            error => {
                console.log('alertBanner.component.ts: unable to fetch configInfo', error);                
            } 
        );
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
}
