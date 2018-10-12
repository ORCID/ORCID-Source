declare var orcidVar: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, EventEmitter, Output } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
    
import { takeUntil } 
    from 'rxjs/operators';

import { OauthService } 
    from '../../shared/oauth.service.ts'; 

import { CommonService } 
    from '../../shared/common.service.ts';

import { DeveloperToolsService } 
    from '../../shared/developerTools.service.ts'; 
    
import { EmailService } 
    from '../../shared/email.service.ts';

@Component({
    selector: 'developer-tools-ng2',
    template:  scriptTmpl("developerTools-ng2-template")
})
export class DeveloperToolsComponent implements AfterViewInit, OnDestroy, OnInit {    
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    developerToolsEnabled: boolean;
    client: any;
    showTerms: boolean;
    acceptedTerms: boolean;
    verifyEmailSent: boolean;    
    showForm: boolean;
    hideGoogleUri: boolean;
    hideSwaggerUri: boolean;
    googleUri: string = 'https://developers.google.com/oauthplayground';
    swaggerUri: string = orcidVar.pubBaseUri +"/v2.0/";
    
    constructor(
            private commonSrvc: CommonService,
            private developerToolsService: DeveloperToolsService,
            private emailService: EmailService,
            private cdr:ChangeDetectorRef
        ) {
        this.client = {};
        this.showTerms = false;
        this.acceptedTerms = false;
        this.verifyEmailSent = false;
        this.developerToolsEnabled = orcidVar.developerToolsEnabled;
        this.showForm = false;
        this.hideGoogleUri = false;
        this.hideSwaggerUri = false;
    }
    
    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    verifyEmail(primaryEmail): void {        
        this.emailService.verifyEmail(primaryEmail)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.verifyEmailSent = true;
            },
            error => {
                console.log('verifyEmail Error', error);
            } 
        );
    };
    
    getClient(): void {
        this.developerToolsService.getClient()
        .pipe(    
                takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.client = data;                
                if(this.client.clientId.value.length == 0) {
                    this.showForm = true;
                } else {
                    this.showForm = false;
                }
            },
            error => {
                console.log("error ngOnInit", error);
            } 
        );
    };
    
    enableDeveloperTools(): void {
        if(this.acceptedTerms){
            this.developerToolsService.enableDeveloperTools()
            .pipe(    
                    takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    if(data) {
                        this.developerToolsEnabled = true;
                        this.getClient();
                    }                    
                },
                error => {
                    console.log("error ngOnInit", error);
                } 
            );
        }
    };
    
    addRedirectURI(rUri): void {
        rUri = (typeof rUri != undefined && rUri != null) ? rUri : ''; 
        this.client.redirectUris.push({value: {value: rUri}, type: {value: 'sso-authentication'}, errors: []});
    };
    
    addTestRedirectUri(type): void {
        var rUri = null;
        if(type == 'google'){
            rUri = this.googleUri;
            this.hideGoogleUri = true;
        } else if(type == 'swagger'){
            rUri = this.swaggerUri;
            this.hideSwaggerUri = true;
        } 
        
        if(this.client.redirectUris.length == 1 && this.client.redirectUris[0].value.value == '') {
            this.client.redirectUris[0].value.value = rUri;
        } else {
            this.addRedirectURI(rUri);   
        }
    };
    
    deleteRedirectUri(idx): void {
        var removed = this.client.redirectUris[idx].value.value
        this.client.redirectUris.splice(idx, 1);
        if(this.googleUri == removed) {
            this.hideGoogleUri = false;
        } else if (this.swaggerUri == removed){
            this.hideSwaggerUri = false;
        }        
    };
    
    createCredentials(): void {
        this.developerToolsService.createCredentials(this.client)
        .pipe(    
                takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data) {
                    this.client = data;
                    if(this.client.clientId.value.length == 0) {
                        this.showForm = true;
                    } else {
                        this.showForm = false;
                    }
                }                    
            },
            error => {
                console.log("error ngOnInit", error);
            } 
        );
    };    
    
    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };
    
    ngOnInit() {
        this.getClient();
    };
}