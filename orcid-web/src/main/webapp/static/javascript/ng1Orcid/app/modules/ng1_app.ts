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

import { AlertBannerModule } 
    from './alertBanner/alertBanner.ts';

import { AllConsortiumContactsModule } 
    from './allConsortiumContacts/allConsortiumContacts.ts';

import { BiographyModule } 
    from './biography/biography.ts';

import { ClientEditModule } 
    from './clientEdit/clientEdit.ts';

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

import { ExtIdPopoverModule } 
    from './extIdPopover/extIdPopover.ts';

import { FundingDeleteModule } 
    from './funding/fundingDelete.ts';

import { FundingModule } 
    from './funding/funding.ts';

import { FundingFormModule } 
    from './funding/fundingForm.ts';

import { HomeModule } 
    from './home/home.ts';

import { HeaderModule } 
    from './header/header.ts';

import { LanguageModule } 
    from './language/language.ts';

import { LinkAccountModule } 
    from './linkAccount/linkAccount.ts';

import { MembersListModule } 
    from './membersList/membersList.ts';

import { ModalModule } 
    from './modalNg2/modal-ng.ts';

import { MyOrcidAlertsModule } 
    from './myOrcidAlerts/myOrcidAlerts.ts';

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

import { PeerReviewModule } 
    from './peerReview/peerReview.ts';

import { PeerReviewDeleteModule } 
    from './peerReview/peerReviewDelete.ts';

import { PersonModule } 
    from './person/person.ts';

import { PrintRecordModule } 
    from './printRecord/printRecord.ts';

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

import { ResearchResourceModule } 
    from './researchResource/researchResource.ts';

import { ResearchResourceDeleteModule } 
    from './researchResource/researchResourceDelete.ts';

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
    
import { SwitchUserModule }
    from './switchUser/switchUser.ts';

import { TwoFaSetupModule } 
    from './2FASetup/twoFASetup.ts';

import { TwoFaStateModule }
    from './2FAState/twoFAState.ts';

import { WidgetModule } 
    from './widget/widget.ts';

import { WorksBulkDeleteModule } 
    from './works/worksBulkDelete.ts';

import { WorksDeleteModule } 
    from './works/worksDelete.ts';

import { WorksFormModule } 
    from './works/worksForm.ts';

import { WorksModule } 
    from './works/works.ts';

import { WorksPrivacyPreferencesModule } 
    from './worksPrivacyPreferences/worksPrivacyPreferences.ts';

import { UnsubscribeModule }
    from './unsubscribe/unsubscribe.ts'

import { DelegatorsModule } 
    from './delegators/delegators.ts';
    
import { ResendClaimModule } 
    from './resendClaim/resendClaim.ts';
    
import { WorksMergeChoosePreferredVersionModule } 
    from './works/worksMergeChoosePreferredVersion.ts';
    
import { WorksMergeSuggestionsModule } 
    from './works/worksMergeSuggestions.ts';

import { AdminActionsModule } 
    from './adminActions/adminActions.ts';       
    
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
        AlertBannerModule.name,
        AllConsortiumContactsModule.name,
        BiographyModule.name,
        ClientEditModule.name,
        DeactivateAccountModule.name,
        DeactivateAccountMessageModule.name,
        DeprecateAccountModule.name,
        EmailsFormModule.name,
        EmailsModule.name,
        EmailUnverifiedWarningModule.name,
        EmailVerificationSentMesssageModule.name,
        ExtIdPopoverModule.name,
        FundingDeleteModule.name,
        FundingFormModule.name,
        FundingModule.name,
        HomeModule.name,
        HeaderModule.name,
        LanguageModule.name,
        LinkAccountModule.name,
        MembersListModule.name,
        ModalModule.name,
        MyOrcidAlertsModule.name,
        NameModule.name,
        OauthAuthorizationModule.name,
        OrgIdentifierPopoverModule.name,
        PasswordEditModule.name,
        PeerReviewModule.name,
        PeerReviewDeleteModule.name,
        PersonModule.name,
        PrintRecordModule.name,
        PublicEduAffiliationModule.name,
        NotificationsModule.name,
        ReactivationModule.name,
        RegisterDuplicatesModule.name,
        RequestPasswordResetModule.name,
        ResetPasswordModule.name,
        ResearchResourceModule.name,
        ResearchResourceDeleteModule.name,
        SearchModule.name,
        SecurityQuestionEditModule.name,
        SelfServiceModule.name,
        SelfServiceAddContactModule.name,
        SelfServiceExistingSubMemberModule.name,
        SelfServiceRemoveContactModule.name,
        SelfServiceRemoveSubMemberModule.name,
        Social2FAModule.name,
        SwitchUserModule.name,
        TwoFaStateModule.name,
        TwoFaSetupModule.name,
        WidgetModule.name,
        WorksBulkDeleteModule.name,
        WorksDeleteModule.name,
        WorksFormModule.name,
        WorksModule.name,
        WorksPrivacyPreferencesModule.name,
        UnsubscribeModule.name,
        DelegatorsModule.name,
        ResendClaimModule.name,
        WorksMergeChoosePreferredVersionModule.name,
        WorksMergeSuggestionsModule.name,
        AdminActionsModule.name
    ]
);

