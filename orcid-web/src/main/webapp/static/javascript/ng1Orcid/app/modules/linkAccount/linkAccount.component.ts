declare var $: any
declare var orcidGA: any;
declare var orcidVar: any

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, NgZone } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { DiscoService } 
    from '../../shared/disco.service'; 

import { OauthService } 
    from '../../shared/oauth.service'; 

import { WidgetService } 
    from '../../shared/widget.service'; 

import { CommonService } 
    from '../../shared/common.service.ts';  
    
@Component({
    selector: 'link-account-ng2',
    template:  scriptTmpl("link-account-ng2-template")
})
export class LinkAccountComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    authorizationForm: any;
    entityId: any;
    feed: any;
    gaString: string;
    loadedFeed: boolean;
    idpName: string;
    oauthRequest: boolean;
    requestInfoForm: any;
    showDeactivatedError: any;
    showReactivationSent: any;
    initReactivationRequest: any;
    assetsPath: String;
    registration: boolean;
    socialSignin: boolean;
    shibbolethSignin: boolean;    
    signinData: any;
   
    constructor(
        private zone:NgZone,
        private discoService: DiscoService,
        private oauthService: OauthService,
        private widgetService: WidgetService,
        private commonSrvc: CommonService,
        private cdr:ChangeDetectorRef
    ) {
        window['angularComponentReference'] = {
                zone: this.zone,
                showDeactivationError: () => this.showDeactivationError(),
                component: this,
            };
        this.authorizationForm = {};
        this.gaString = "";
        this.loadedFeed = false;
        this.idpName = "";
        this.oauthRequest = false;
        this.requestInfoForm = {};
        this.showDeactivatedError = false;
        this.showReactivationSent = false;
        this.initReactivationRequest = { "email":null, "error":null, "success":false };
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.assetsPath = data.messages['STATIC_PATH'];
            },
            error => {
                console.log('linkAccount.component.ts: unable to fetch userInfo', error);                
            } 
        );
    }

    loadDiscoFeed = function() {
        this.discoService.getDiscoFeed()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.feed = data;
                this.idpName = this.discoService.getIdpName(this.entityId, this.feed, this.widgetService.getLocale());
                this.loadedFeed = true;
            },
            error => {
                console.log('Error getting disco feed');
                this.feed = [];
                this.idpName = this.discoService.getIdpName(this.entityId, this.feed, this.widgetService.getLocale());
                this.loadedFeed = true;
            } 
        );
    };

    loadRequestInfoForm = function() {
        this.oauthService.loadRequestInfoForm()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data){
                    this.oauthRequest = true;                     
                    this.requestInfoForm = data;              
                    this.gaString = orcidGA.buildClientString(this.requestInfoForm.memberName, this.requestInfoForm.clientName);
                } else {
                    this.oauthRequest = false;
                }
            },
            error => {
                console.log('Error loading request info form');
            } 
        );
    };
    
    loadSocialSigninData = function() {
        this.oauthService.loadSocialSigninData()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data){                     
                    this.signinData = data;     
                    this.entityId = data.providerId;  
                    if(this.entityId === "facebook" || this.entityId === "google"){
                        this.idpName = this.entityId.charAt(0).toUpperCase() + this.entityId.slice(1);
                        this.loadedFeed = true;
                        this.cdr.detectChanges();
                    }
                }
            },
            error => {
                console.log('Error loading social signin data ' + JSON.stringify(error));
            } 
        );
    };
    
    loadShibbolethSigninData = function() {
        this.oauthService.loadShibbolethSigninData()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data){                     
                    this.signinData = data;    
                    this.entityId = data.providerId;          
                    this.loadDiscoFeed();  
                }
            },
            error => {
                console.log('Error loading shibboleth signin data');
            } 
        );
    };

    showDeactivationError(): void {
        this.showDeactivatedError = true;
        this.showReactivationSent = false;        
        if(this.authorizationForm.userName.value != null && this.authorizationForm.userName.value.includes('@')) {
            this.initReactivationRequest.email = this.authorizationForm.userName.value;            
        } else {
            this.initReactivationRequest.email = '';
        }
    };
    
    sendReactivationEmail(): void {
        this.oauthService.sendReactivationEmail(this.initReactivationRequest.email)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.initReactivationRequest = data;
                if(this.initReactivationRequest.error == null || this.initReactivationRequest.error == '') {
                    this.showDeactivatedError = false;
                    this.showReactivationSent = true;                    
                } else {
                    this.showDeactivatedError = true;
                    this.showReactivationSent = false;                    
                }
            },
            error => {
                console.log("error sending reactivation email");
            } 
        );
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
        var urlParts = window.location.href.split('/');
        if (urlParts.indexOf("register") >= 0) {
            this.registration = true;
        } else if (urlParts.indexOf("social") >= 0) {
            this.socialSignin = true;
            this.loadSocialSigninData();
        } else if (urlParts.indexOf("shibboleth") >= 0) {
            this.shibbolethSignin = true;
            this.loadShibbolethSigninData();
        }
        
        this.loadRequestInfoForm();
        this.authorizationForm = {
            userName:  {value: ""}
        } 
    }; 
    
    getBaseUri(): String {
        return getBaseUri();
    };
}