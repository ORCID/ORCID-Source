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

import { AffiliationDeleteNg2Module } 
    from './affiliation/affiliationDelete.ts';

import { AffiliationFormNg2Module } 
    from './affiliation/affiliationForm.ts';

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

import { DeactivateAccountNg2Module }
    from './deactivateAccount/deactivateAccount.ts';

import { DeprecateAccountNg2Module }
    from './deprecateAccount/deprecateAccount.ts';

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

import { HomeNg2Module } 
    from './home/home.ts';

import { KeywordsNg2Module } 
    from './keywords/keywords.ts';

import { KeywordsFormNg2Module } 
    from './keywordsForm/keywordsForm.ts';

import { HeaderNg2Module } 
    from './header/header.ts';

import { LanguageNg2Module }
    from './language/language.ts';

import { ModalNg2Module }
    from './modalNg2/modal-ng.ts';

import { NameNg2Module } 
    from './name/name.ts';

import { PasswordEditNg2Module } 
    from './passwordEdit/passwordEdit.ts';

import { SearchNg2Module } 
    from './search/search.ts';

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

import { SocialNetworksNg2Module }
    from './socialNetworks/socialNetworks.ts';

import { ThanksForRegisteringNg2Module } 
    from './thanksForRegistering/thanksForRegistering.ts';

import { ThanksForVerifyingNg2Module } 
    from './thanksForVerifying/thanksForVerifying.ts';

import { TwoFASetupNg2Module }
    from './2FASetup/twoFASetup.ts';

import { TwoFAStateNg2Module }
    from './2FAState/twoFAState.ts';

import { WebsitesNg2Module } 
    from './websites/websites.ts';

import { WebsitesFormNg2Module } 
    from './websitesForm/websitesForm.ts';

import { WidgetNg2Module } 
    from './widget/widget.ts';

import { WorksNg2Module } 
    from './works/works.ts';

import { SearchService } 
    from '../shared/search.service.ts'; 

import { WorkspaceService } 
    from '../shared/workspace.service.ts'; 

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
        AffiliationNg2Module,//Aproved
        AffiliationDeleteNg2Module,//Aproved
        AffiliationFormNg2Module,//Aproved
        AlsoKnownAsFormNg2Module,
        AlsoKnownAsNg2Module,
        BiographyNg2Module, //Approved
        CountryFormNg2Module,//Approved
        CountryNg2Module,//Approved
        DeactivateAccountNg2Module,
        DeprecateAccountNg2Module,
        EmailsFormNg2Module,//Aproved
        EmailsNg2Module,//Aproved
        EmailUnverifiedWarningNg2Module,//Aproved
        EmailVerificationSentMesssageNg2Module,//Aproved
        HeaderNg2Module,
        //FundingNg2Module,
        HomeNg2Module,
        KeywordsFormNg2Module,//Approved
        KeywordsNg2Module,//Approved
        LanguageNg2Module,
        ModalNg2Module, //Approved
        NameNg2Module, //Approved
        PasswordEditNg2Module,
        SearchNg2Module, //Approved
        SelfServiceNg2Module, //Approved
        SelfServiceAddContactNg2Module, //Approved
        SelfServiceExistingSubMemberNg2Module, //Approved
        SelfServiceRemoveContactNg2Module, //Approved
        SelfServiceRemoveSubMemberNg2Module, //Approved
        SocialNetworksNg2Module,
        ThanksForRegisteringNg2Module,
        ThanksForVerifyingNg2Module,
        TwoFAStateNg2Module,
        TwoFASetupNg2Module,
        WebsitesFormNg2Module, //Approved
        WebsitesNg2Module, //Approved
        WidgetNg2Module, //Approved
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
        //console.log('v0.102');
    }
}