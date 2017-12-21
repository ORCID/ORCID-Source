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

import { AlsoKnownAsNg2Module } 
    from './alsoKnownAs/alsoKnownAs.ts';

import { AlsoKnownAsFormNg2Module } 
    from './alsoKnownAsForm/alsoKnownAsForm.ts';

import { BiographyNg2Module } 
    from './biography/biography.ts';

import { CountryNg2Module } 
    from './country/country.ts';

import { CountryFormNg2Module } 
    from './countryForm/countryForm.ts';

import { EmailsNg2Module } 
    from './emails/emails.ts';

import { EmailsFormNg2Module } 
    from './emailsForm/emailsForm.ts';

import { EmailUnverifiedWarningNg2Module } 
    from './emailUnverifiedWarning/emailUnverifiedWarning.ts';

import { EmailVerificationSentMesssageNg2Module } 
    from './emailVerificationSentMessage/emailVerificationSentMessage.ts';

import { FundingNg2Module } 
    from './funding/funding.ts';

import { KeywordsNg2Module } 
    from './keywords/keywords.ts';

import { KeywordsFormNg2Module } 
    from './keywordsForm/keywordsForm.ts';

import { ModalNg2Module }
    from './modalNg2/modal-ng.ts';

import { NameNg2Module } 
    from './name/name.ts';

import { ThanksForRegisteringNg2Module } 
    from './thanksForRegistering/thanksForRegistering.ts';

import { ThanksForVerifyingNg2Module } 
    from './thanksForVerifying/thanksForVerifying.ts';

import { WebsitesNg2Module } 
    from './websites/websites.ts';

import { WebsitesFormNg2Module } 
    from './websitesForm/websitesForm.ts';

import { WidgetNg2Module } 
    from './widget/widget.ts';

import { WorksPrivacyPreferencesNg2Module } 
    from './worksPrivacyPreferences/worksPrivacyPreferences.ts';

//User generated services
import { AffiliationService } 
    from '../shared/affiliation.service.ts';

import { AlsoKnownAsService } 
    from '../shared/alsoKnownAs.service.ts';

import { BiographyService } 
    from '../shared/biography.service.ts';

import { CommonService }
    from '../shared/common.service.ts'

import { EmailService } 
    from '../shared/email.service.ts';

import { FundingService } 
    from '../shared/funding.service.ts';

//import { GroupedActivitiesUtilService } 
//    from '../shared/groupedActivities.service.ts';

import { KeywordsService } 
    from '../shared/keywords.service.ts';

import { ModalService } 
    from '../shared/modal.service.ts';

import { NameService } 
    from '../shared/name.service.ts'; 

import { WorkspaceService } 
    from '../shared/workspace.service.ts'; 

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
        RootCmp
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
        AlsoKnownAsFormNg2Module,
        AlsoKnownAsNg2Module,
        BiographyNg2Module,
        CountryFormNg2Module,
        CountryNg2Module,
        EmailsFormNg2Module,
        EmailsNg2Module,
        EmailUnverifiedWarningNg2Module,
        EmailVerificationSentMesssageNg2Module,
        //FundingNg2Module,
        KeywordsFormNg2Module,
        KeywordsNg2Module,
        ModalNg2Module,
        NameNg2Module,
        ThanksForRegisteringNg2Module,
        ThanksForVerifyingNg2Module,
        WebsitesFormNg2Module,
        WebsitesNg2Module,
        WidgetNg2Module,
        WorksPrivacyPreferencesNg2Module
    ],
    providers: [
        { 
            provide: XSRFStrategy, 
            useClass: MetaXSRFStrategy
        },
        AffiliationService,
        AlsoKnownAsService,
        BiographyService,
        CommonService,
        EmailService,
        FundingService,
        //GroupedActivitiesUtilService,
        KeywordsService,
        ModalService,
        NameService,
        WorkspaceService
    ]

})

export class Ng2AppModule {
    constructor( public upgrade: UpgradeModule ){
        console.log('v0.101');
    }
}