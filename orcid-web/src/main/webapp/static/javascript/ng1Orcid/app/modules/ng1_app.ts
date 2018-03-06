/**
 * This file defines the root module of the Angular 1 of the application.
 */

//Angular and other libraries imports
import * as $ from 'jquery'
import * as angular from 'angular'
import * as ngCookies from 'angular-cookies'
import * as ngSanitize from 'angular-sanitize'
import * as uibootstraptypeahead from 'angular-ui-bootstrap'
import * as vcRecaptcha from 'angular-recaptcha'
import 'angular-route'

//User generated imports
import { AffiliationModule } 
    from './affiliation/affiliation.ts';

import { AffiliationDeleteModule } 
    from './affiliation/affiliationDelete.ts';

import { AffiliationFormModule } 
    from './affiliation/affiliationForm.ts';

import { AlsoKnownAsModule } 
    from './alsoKnownAs/alsoKnownAs.ts';

import { AlsoKnownAsFormModule } 
    from './alsoKnownAsForm/alsoKnownAsForm.ts';

import { BiographyModule } 
    from './biography/biography.ts';

import { CountryModule } 
    from './country/country.ts';

import { CountryFormModule } 
    from './countryForm/countryForm.ts';

import { DeactivateAccountModule }
    from './deactivateAccount/deactivateAccount.ts';

import { DeprecateAccountModule }
    from './deprecateAccount/deprecateAccount.ts';

import { EmailsModule } 
    from './emails/emails.ts';

import { EmailsFormModule } 
    from './emailsForm/emailsForm.ts';

import { EmailUnverifiedWarningModule } 
    from './emailUnverifiedWarning/emailUnverifiedWarning.ts';

import { EmailVerificationSentMesssageModule } 
    from './emailVerificationSentMessage/emailVerificationSentMessage.ts';

import { FundingModule } 
    from './funding/funding.ts';

import { HomeModule } 
    from './home/home.ts';

import { KeywordsFormModule } 
    from './keywordsForm/keywordsForm.ts';

import { KeywordsModule } 
    from './keywords/keywords.ts';

import { HeaderModule } 
    from './header/header.ts';

import { LanguageModule } 
    from './language/language.ts';

import { ModalModule } 
    from './modalNg2/modal-ng.ts';

import { NameModule } 
    from './name/name.ts';

import { PasswordEditModule } 
    from './passwordEdit/passwordEdit.ts';

import { ReactivationModule } 
    from './reactivation/reactivation.ts';

import { SearchModule } 
    from './search/search.ts';

import { SelfServiceModule } 
    from './selfService/selfService.ts';

import { SelfServiceAddContactModule } 
    from './selfServiceAddContact/selfServiceAddContact.ts';

import { SelfServiceExistingSubMemberModule } 
    from './selfServiceExistingSubMember/selfServiceExistingSubMember.ts';

import { SelfServiceRemoveContactModule } 
    from './selfServiceRemoveContact/selfServiceRemoveContact.ts';

import { SelfServiceRemoveSubMemberModule } 
    from './selfServiceRemoveSubMember/selfServiceRemoveSubMember.ts';

import { SocialNetworksModule }
    from './socialNetworks/socialNetworks.ts';

import { ThanksForRegisteringModule } 
    from './thanksForRegistering/thanksForRegistering.ts';

import { ThanksForVerifyingModule } 
    from './thanksForVerifying/thanksForVerifying.ts';

import { TwoFASetupModule } 
    from './2FASetup/twoFASetup.ts';

import { TwoFAStateModule }
    from './2FAState/twoFAState.ts';

import { WebsitesModule } 
    from './websites/websites.ts';

import { WebsitesFormModule } 
    from './websitesForm/websitesForm.ts';

import { WidgetModule } 
    from './widget/widget.ts';

import { WorksModule } 
    from './works/works.ts';

import { WorksPrivacyPreferencesModule } 
    from './worksPrivacyPreferences/worksPrivacyPreferences.ts'

export const orcidApp = angular.module(
    'orcidApp', 
    [
        ngCookies,
        ngSanitize, 
        vcRecaptcha,
        uibootstraptypeahead,
        AffiliationModule.name,
        AffiliationDeleteModule.name,
        AffiliationFormModule.name,
        AlsoKnownAsModule.name,
        AlsoKnownAsFormModule.name,
        BiographyModule.name,
        CountryModule.name,
        CountryFormModule.name,
        DeactivateAccountModule.name,
        DeprecateAccountModule.name,
        EmailsFormModule.name,
        EmailsModule.name,
        EmailUnverifiedWarningModule.name,
        EmailVerificationSentMesssageModule.name,
        //FundingModule.name,
        HomeModule.name,
        KeywordsFormModule.name,
        KeywordsModule.name,
        HeaderModule.name,
        LanguageModule.name,
        ModalModule.name,
        NameModule.name,
        PasswordEditModule.name,
        ReactivationModule.name,
        SearchModule.name,
        SelfServiceModule.name,
        SelfServiceAddContactModule.name,
        SelfServiceExistingSubMemberModule.name,
        SelfServiceRemoveContactModule.name,
        SelfServiceRemoveSubMemberModule.name,
        SocialNetworksModule.name,
        ThanksForRegisteringModule.name,
        ThanksForVerifyingModule.name,
        TwoFASetupModule.name,
        TwoFAStateModule.name,
        WebsitesFormModule.name,
        WebsitesModule.name,
        WidgetModule.name,
        WorksModule.name,
        WorksPrivacyPreferencesModule.name
    ]
);

