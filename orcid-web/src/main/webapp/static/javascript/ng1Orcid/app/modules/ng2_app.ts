//Angular imports
import 'reflect-metadata';

import { CommonModule } 
    from '@angular/common'; 

import { Component, NgModule } 
    from '@angular/core';

import { FormsModule } 
    from '@angular/forms';

import { HttpModule, JsonpModule, Request, XSRFStrategy } 
    from '@angular/http';

import { BrowserModule } 
    from "@angular/platform-browser";

import { platformBrowserDynamic } 
    from '@angular/platform-browser-dynamic';

import { RouterModule, UrlHandlingStrategy } 
    from '@angular/router';

import { UpgradeModule } 
    from '@angular/upgrade/static';

//User generated modules imports
import { AffiliationNg2Module } 
    from './affiliation/affiliation.ts';
import { BiographyNg2Module } 
    from './biography/biography.ts';
import { CountryNg2Module } 
    from './country/country.ts';
import { CountryFormNg2Module } 
    from './countryForm/countryForm.ts';
import { EmailUnverifiedWarningNg2Module } 
    from './emailUnverifiedWarning/emailUnverifiedWarning.ts';
import { EmailVerificationSentMesssageNg2Module } 
    from './emailVerificationSentMessage/emailVerificationSentMessage.ts';
import { ModalNg2Module }
    from './modalNg2/modal-ng.ts';
import { NameNg2Module } 
    from './name/name.ts';
import { ThanksForRegisteringNg2Module } 
    from './thanksForRegistering/thanksForRegistering.ts';
import { ThanksForVerifyingNg2Module } 
    from './thanksForVerifying/thanksForVerifying.ts';
import { WidgetNg2Module } 
    from './widget/widget.ts';
import { WorksPrivacyPreferencesNg2Module } 
    from './worksPrivacyPreferences/worksPrivacyPreferences.ts';

//User generated filters
//import { OrderByPipe }
//    from '../pipes/orderByNg2.ts'; 

//User generated services
import { AffiliationService } 
    from '../shared/affiliationService.ts';

import { BiographyService } 
    from '../shared/biographyService.ts';

import { CountryService } 
    from '../shared/countryService.ts'; 

import { CommonService }
    from '../shared/commonService.ts'

import { EmailService } 
    from '../shared/emailService.ts'; 

import { GroupedActivitiesUtilService } 
    from '../shared/groupedActivitiesService.ts';

import { ModalService } 
    from '../shared/modalService.ts';

import { NameService } 
    from '../shared/nameService.ts'; 

import { WorkspaceService } 
    from '../shared/workspaceService.ts'; 

export class MetaXSRFStrategy implements XSRFStrategy {
    constructor() {
    }

    configureRequest(req: Request): any {
        let token = document.querySelector("meta[name='_csrf']").getAttribute("content");
        let header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
        if (token && header) {
            req.headers.set(header, token);
        }
    }
}

@Component(
    {
        selector: 'root-cmp',
        template: '<div class="ng-view"></div>'
    }
) 
export class RootCmp {
}

@NgModule({
    bootstrap: [
        RootCmp
    ],
    declarations: [
        RootCmp,
        //OrderByPipe
    ],
    imports: [
        /* Ng Modules */
        BrowserModule,
        CommonModule, 
        FormsModule,
        HttpModule,
        JsonpModule,
        UpgradeModule,
        /* User Generated Modules */
        AffiliationNg2Module,
        BiographyNg2Module,
        CountryNg2Module,
        CountryFormNg2Module,
        EmailUnverifiedWarningNg2Module,
        EmailVerificationSentMesssageNg2Module,
        ModalNg2Module,
        NameNg2Module,
        ThanksForRegisteringNg2Module,
        ThanksForVerifyingNg2Module,
        WidgetNg2Module,
        WorksPrivacyPreferencesNg2Module
    ],
    providers: [
        { 
            provide: XSRFStrategy, 
            useClass: MetaXSRFStrategy
        },
        AffiliationService,
        BiographyService,
        CommonService,
        CountryService,
        EmailService,
        //GroupedActivitiesUtilService,
        ModalService,
        NameService,
        WorkspaceService
    ]

})

export class Ng2AppModule {
    constructor( public upgrade: UpgradeModule ){
        console.log('v0.99');
    }
}