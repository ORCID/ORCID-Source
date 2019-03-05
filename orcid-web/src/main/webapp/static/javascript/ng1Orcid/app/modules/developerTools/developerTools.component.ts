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
    private userInfo: any;

    developerToolsEnabled: boolean;
    client: any;
    showTerms: boolean;
    acceptedTerms: boolean;
    verifyEmailSent: boolean;    
    showForm: boolean;
    hideGoogleUri: boolean;
    hideSwaggerUri: boolean;
    expandDetails: boolean;
    googleUri: string = 'https://developers.google.com/oauthplayground';
    authorizeUrlBase:string = getBaseUri() + '/oauth/authorize';
    tokenURL:string = getBaseUri() + '/oauth/token';    
    selectedRedirectUri: string;
    showResetClientSecret: boolean;
    authorizeURL: String;
    sampleAuthCurl: String;
    sampleOpenId: String;
    swaggerUri: String;
    
    constructor(
            private commonSrvc: CommonService,
            private developerToolsService: DeveloperToolsService,
            private emailService: EmailService,
            private cdr:ChangeDetectorRef
        ) {
        this.client = {clientId: {errors: [], value: ''}, clientSecret: {errors: [], value: ''}, displayName : {errors: [], value: ''}, website: {errors: [], value: ''}, shortDescription: {errors: [], value: ''}};
        this.showTerms = false;
        this.acceptedTerms = false;
        this.developerToolsEnabled = false;
        this.verifyEmailSent = false;
        this.showForm = false;
        this.hideGoogleUri = false;
        this.hideSwaggerUri = false;
        this.selectedRedirectUri = '';
        this.showResetClientSecret = false;
        this.expandDetails = false;
        
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                var pubBaseUri = data.messages['PUB_BASE_URI'];
                this.swaggerUri = pubBaseUri + '/v2.0/';                
            },
            error => {
                console.log('developerTools.component.ts: unable to fetch configInfo', error);                
            } 
        );  

        this.userInfo = this.commonSrvc.userInfo$
          .subscribe(
              data => {
                  this.userInfo = data; 
                  if(data.DEVELOPER_TOOLS_ENABLED=='true'){
                    this.developerToolsEnabled = true;
                  } 
              },
              error => {
                  console.log('developerTools.component.ts: unable to fetch userInfo', error);
                  this.userInfo = {};
              } 
          );
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
                    this.selectedRedirectUri = this.client.redirectUris[0].value.value;
                    this.generateSamples(this.selectedRedirectUri);
                    // Lets hide the test urls if they are already added
                    var rUri;
                    for (var i = 0; i < this.client.redirectUris.length; i++) {
                        rUri = this.client.redirectUris[i].value.value;
                        if(rUri == this.googleUri) {
                            this.hideGoogleUri = true;
                        } else if(rUri == this.swaggerUri) {
                            this.hideSwaggerUri = true;
                        }
                    }                    
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
    
    createOrUpdateCredentials(): void {
        if(this.client.clientId.value.length == 0) {
            this.createCredentials();
        } else {
            this.updateCredentials();
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
                        this.selectedRedirectUri = this.client.redirectUris[0].value.value;
                        this.generateSamples(this.selectedRedirectUri);
                    }
                }                    
            },
            error => {
                console.log("error createCredentials", error);
            } 
        );
    };    
    
    updateCredentials(): void {
        this.developerToolsService.updateCredentials(this.client)
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
                        this.selectedRedirectUri = this.client.redirectUris[0].value.value;
                        this.generateSamples(this.selectedRedirectUri);
                    }
                }                    
            },
            error => {
                console.log("error updateCredentials", error);
            } 
        );
    };
    
    getClientUrl(website): String {
        if(typeof website != undefined && website != null && website.lastIndexOf('http://') === -1 && website.lastIndexOf('https://') === -1) {            
                return '//' + website;            
        }
        return website;
    };
    
    resetClientSecret(): void {
        this.developerToolsService.resetClientSecret(this.client)
        .pipe(    
                takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.showResetClientSecret = false;
                this.getClient();                 
            },
            error => {
                console.log("error resetClientSecret", error);
            } 
        );
    };
    
    generateSamples(url): void {
        this.authorizeURL = getBaseUri() + '/oauth/authorize?client_id=' + this.client.clientId.value + '&response_type=code&scope=/authenticate&redirect_uri=' + url;        
        this.sampleAuthCurl = "curl -i -L -k -H 'Accept: application/json' --data 'client_id=" + this.client.clientId.value + "&client_secret=" + this.client.clientSecret.value + "&grant_type=authorization_code&redirect_uri=" + url + "&code=REPLACE WITH OAUTH CODE' " + getBaseUri() + "/oauth/token";
        this.sampleOpenId = getBaseUri() + '/oauth/authorize?client_id=' + this.client.clientId.value + '&response_type=token&scope=openid&redirect_uri=' + url;
    };        
    
    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };
    
    ngOnInit() {
        this.getClient();
    };
}