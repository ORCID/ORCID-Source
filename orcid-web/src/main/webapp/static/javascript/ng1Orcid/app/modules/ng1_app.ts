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

import { AffiliationExtIdPopoverModule } 
    from './affiliationExtIdPopover/affiliationExtIdPopover.ts';

import { AffiliationModule } 
    from './affiliation/affiliation.ts';

import { AffiliationDeleteModule } 
    from './affiliation/affiliationDelete.ts';

import { AffiliationFormModule } 
    from './affiliation/affiliationForm.ts';

import { AlertBannerModule } 
    from './alertBanner/alertBanner.ts';

import { AllConsortiumContactsModule } 
    from './allConsortiumContacts/allConsortiumContacts.ts';

import { AlsoKnownAsModule } 
    from './alsoKnownAs/alsoKnownAs.ts';

import { AlsoKnownAsFormModule } 
    from './alsoKnownAsForm/alsoKnownAsForm.ts';

import { BiographyModule } 
    from './biography/biography.ts';

import { ClaimThanksModule } 
    from './claimThanks/claimThanks.ts';

import { ClientEditModule } 
    from './clientEdit/clientEdit.ts';

import { CountryModule } 
    from './country/country.ts';

import { CountryFormModule } 
    from './countryForm/countryForm.ts';

import { DeactivateAccountModule }
    from './deactivateAccount/deactivateAccount.ts';

import { DeactivateAccountMessageModule }
    from './deactivateAccount/deactivateAccountMessage.ts';

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

import { ExternalIdentifiersModule }
    from './externalIdentifiers/externalIdentifiers.ts';

import { FundingModule } 
    from './funding/funding.ts';

import { FundingFormModule } 
    from './funding/fundingForm.ts';

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

import { LinkAccountModule } 
    from './linkAccount/linkAccount.ts';

import { ModalModule } 
    from './modalNg2/modal-ng.ts';

import { NameModule } 
    from './name/name.ts';

import { NotificationsModule }
    from './notifications/notifications.ts';

import { OauthAuthorizationModule } 
    from './oauthAuthorization/oauthAuthorization.ts';

import { OrgIdentifierPopoverModule } 
    from './orgIdentifierPopover/orgIdentifierPopover.ts';

import { PasswordEditModule } 
    from './passwordEdit/passwordEdit.ts';

import { PersonalInfoModule } 
    from './personalInfo/personalInfo.ts';

import { PublicEduAffiliationModule }
    from './publicEduAffiliation/publicEduAffiliation.ts';

import { ReactivationModule } 
    from './reactivation/reactivation.ts';

import { RegisterDuplicatesModule } 
    from './registerDuplicates/registerDuplicates.ts';

import { RequestPasswordResetModule } 
    from './requestPasswordReset/requestPasswordReset.ts';

import { ResetPasswordModule }
    from './resetPassword/resetPassword.ts';

import { SearchModule } 
    from './search/search.ts';

import { SecurityQuestionEditModule } 
    from './securityQuestionEdit/securityQuestionEdit.ts';

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

import { Social2FAModule }
    from './social2FA/social2FA.ts';

import { SocialNetworksModule }
    from './socialNetworks/socialNetworks.ts';

import { SwitchUserModule }
    from './switchUser/switchUser.ts';

import { ThanksForRegisteringModule } 
    from './thanksForRegistering/thanksForRegistering.ts';

import { ThanksForVerifyingModule } 
    from './thanksForVerifying/thanksForVerifying.ts';

import { TwoFASetupModule } 
    from './2FASetup/twoFASetup.ts';

import { TwoFAStateModule }
    from './2FAState/twoFAState.ts';

import { VerifyEmailModule }
    from './verifyEmail/verifyEmail.ts';

import { WebsitesModule } 
    from './websites/websites.ts';

import { WebsitesFormModule } 
    from './websitesForm/websitesForm.ts';

import { WidgetModule } 
    from './widget/widget.ts';

import { WorksFormModule } 
    from './works/worksForm.ts';

import { WorksModule } 
    from './works/works.ts';

import { WorkSpaceSummaryModule } 
    from './workspaceSummary/workspaceSummary.ts';

import { WorksPrivacyPreferencesModule } 
    from './worksPrivacyPreferences/worksPrivacyPreferences.ts';


export const orcidApp = angular.module(
    'orcidApp', 
    [
        ngCookies,
        ngSanitize, 
        vcRecaptcha,
        uibootstraptypeahead,
        AffiliationExtIdPopoverModule.name,
        AffiliationModule.name,
        AffiliationDeleteModule.name,
        AffiliationFormModule.name,
        AlsoKnownAsModule.name,
        AlertBannerModule.name,
        AllConsortiumContactsModule.name,
        AlsoKnownAsFormModule.name,
        BiographyModule.name,
        ClaimThanksModule.name,
        ClientEditModule.name,
        CountryModule.name,
        CountryFormModule.name,
        DeactivateAccountModule.name,
        DeactivateAccountMessageModule.name,
        DeprecateAccountModule.name,
        EmailsFormModule.name,
        EmailsModule.name,
        EmailUnverifiedWarningModule.name,
        EmailVerificationSentMesssageModule.name,
        ExternalIdentifiersModule.name,
        FundingModule.name,
        FundingFormModule.name,
        HomeModule.name,
        KeywordsFormModule.name,
        KeywordsModule.name,
        HeaderModule.name,
        LanguageModule.name,
        LinkAccountModule.name,
        ModalModule.name,
        NameModule.name,
        OauthAuthorizationModule.name,
        OrgIdentifierPopoverModule.name,
        PasswordEditModule.name,
        PersonalInfoModule.name,
        PublicEduAffiliationModule.name,
        NotificationsModule.name,
        ReactivationModule.name,
        RegisterDuplicatesModule.name,
        RequestPasswordResetModule.name,
        ResetPasswordModule.name,
        SearchModule.name,
        SecurityQuestionEditModule.name,
        SelfServiceModule.name,
        SelfServiceAddContactModule.name,
        SelfServiceExistingSubMemberModule.name,
        SelfServiceRemoveContactModule.name,
        SelfServiceRemoveSubMemberModule.name,
        Social2FAModule.name,
        SocialNetworksModule.name,
        SwitchUserModule.name,
        ThanksForRegisteringModule.name,
        ThanksForVerifyingModule.name,
        TwoFASetupModule.name,
        TwoFAStateModule.name,
        VerifyEmailModule.name,
        WebsitesFormModule.name,
        WebsitesModule.name,
        WidgetModule.name,
        WorksFormModule.name,
        WorksModule.name,
        WorkSpaceSummaryModule.name,
        WorksPrivacyPreferencesModule.name
    ]
);

