declare var orcidGA: any;
declare var orcidVar: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import 'rxjs/add/operator/takeUntil';

import { OauthService } 
    from '../../shared/oauth.service.ts'; 

import { CommonService } 
    from '../../shared/common.service.ts';

import { FeaturesService } 
    from '../../shared/features.service.ts'; 

import { ReactivationService } 
    from '../../shared/reactivation.service.ts'; 

@Component({
    selector: 'reactivation-ng2',
    template:  scriptTmpl("reactivation-ng2-template")
})
export class ReactivationComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    privacyHelp: any;
    registrationForm: any;
    gdprUiFeatureEnabled: boolean = this.featuresService.isFeatureEnabled('GDPR_UI');

    constructor(
        private oauthService: OauthService,
        private commonSrvc: CommonService,
        private featuresService: FeaturesService,
        private reactivationService: ReactivationService,
        private cdr:ChangeDetectorRef
    ) {
        this.privacyHelp = {};
        this.registrationForm = {}; 
    }

    getReactivation(resetParams, linkFlag): void{
        this.oauthService.oauth2ScreensLoadRegistrationForm( )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.registrationForm = data;
                this.registrationForm.resetParams = resetParams;
                if (orcidVar.features['GDPR_UI'] === true){
                    this.registrationForm.activitiesVisibilityDefault.visibility = null;
                }
                this.cdr.detectChanges();              
            },
            error => {
                // something bad is happening!
                console.log("error fetching register.json");
            } 
        );
    };

    isValidClass(cur) : string{
        return this.commonSrvc.isValidClass(cur);
    };

    postReactivationConfirm(): void {
        this.registrationForm.valNumClient = this.registrationForm.valNumServer / 2;
        var baseUri = getBaseUri();                
        if(this.registrationForm.linkType === 'shibboleth'){
            baseUri += '/shibboleth';
        }
        this.reactivationService.postReactivationConfirm(this.registrationForm)
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data.errors.length == 0){
                    window.location.href = data.url;
                }
                else{
                    this.registrationForm = data;
                    this.cdr.detectChanges();
                } 
            },
            error => {
                // something bad is happening!
                console.log("ReactivationCtrl.postReactivationConfirm() error");
            } 
        );
    };

    serverValidate(field): void {
        if (field === undefined) {
            field = '';
        }
        this.reactivationService.serverValidate(this.registrationForm, field)
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.commonSrvc.copyErrorsLeft(this.registrationForm, data);
            },
            error => {
                // something bad is happening!
                console.log("serverValidate() error");
            } 
        );
    };

    trimAjaxFormText(pojoMember): void {
        this.commonSrvc.trimAjaxFormText(pojoMember);
    }

    updateActivitiesVisibilityDefault(priv, $event): void {
        this.registrationForm.activitiesVisibilityDefault.visibility = priv;
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
        this.getReactivation(orcidVar.resetParams, '');
    };


}