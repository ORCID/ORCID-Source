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

import { SelfServiceNg2Module } 
    from './selfService/selfService.ts';

import { SelfServiceAddContactNg2Module } 
    from './selfServiceAddContact/selfServiceAddContact.ts';

import { SelfServiceExistingSubMemberNg2Module } 
    from './selfServiceExistingSubMember/selfServiceExistingSubMember.ts';

import { SelfServiceRemoveContactNg2Module } 
    from './selfServiceRemoveContact/selfServiceRemoveContact.ts';

import { SelfServiceRemoveSubMemberNg2Module } 
    from './selfServiceRemoveSubMember/selfServiceRemoveSubMember.ts';

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

import { WorksNg2Module } 
    from './works/works.ts';

import { WorksPrivacyPreferencesNg2Module } 
    from './worksPrivacyPreferences/worksPrivacyPreferences.ts';

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
        SelfServiceNg2Module,
        SelfServiceAddContactNg2Module,
        SelfServiceExistingSubMemberNg2Module,
        SelfServiceRemoveContactNg2Module,
        SelfServiceRemoveSubMemberNg2Module,
        ThanksForRegisteringNg2Module,
        ThanksForVerifyingNg2Module,
        WebsitesFormNg2Module,
        WebsitesNg2Module,
        WidgetNg2Module,
        WorksNg2Module,
        WorksPrivacyPreferencesNg2Module
    ],
    providers: [
        { 
            provide: XSRFStrategy, 
            useClass: MetaXSRFStrategy
        }

    ]

})

export class Ng2AppModule {
    constructor( public upgrade: UpgradeModule ){
        console.log('v0.101');
    }
}