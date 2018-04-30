declare var om: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { AdminDelegatesService } 
    from '../../shared/adminDelegates.service.ts'; 

import { FeaturesService }
    from '../../shared/features.service.ts' 

@Component({
    selector: 'edit-table-ng2',
    template:  scriptTmpl("edit-table-ng2-template")
})
export class EditTableComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    
    deactivateToggleText: string;
    deprecateToggleText: string;
    emailPreferencesToggleText: string;
    emailToggleText: string;
    showEditDeactivate: boolean;
    showEditDeprecate: boolean;
    showEditEmail: boolean;
    showEditEmailPreferences: boolean;
    showEditLanguage: boolean;
    languageToggleText: string;
    showEditPassword: boolean;
    passwordToggleText: string;
    showEditPrivacyPreferences: boolean;
    privacyPreferencesToggleText: string;
    showEditSecurityQuestion: boolean;
    securityQuestionToggleText: string;
    showEditSocialSettings: boolean;
    socialNetworksToggleText: string;
    showEdit2FA: boolean;
    twoFAToggleText: string;
    showEditGetMyData: boolean;
    getMyDataToggleText: string;
    
    constructor(
            private featuresService: FeaturesService,
        //private adminDelegatesService: AdminDelegatesService
    ) {
    	this.deactivateToggleText = "";
    	this.deprecateToggleText = "";
    	this.emailPreferencesToggleText = "";
    	this.emailToggleText = "";
    	this.showEditDeactivate = (window.location.hash === "#editDeactivate");
    	this.showEditDeprecate = (window.location.hash === "#editDeprecate");
    	this.showEditEmail = (window.location.hash === "#editEmail");
    	this.showEditEmailPreferences = (window.location.hash === "#editEmailPreferences");
    	this.showEditLanguage = false;
    	this.languageToggleText = "";
    	this.showEditPassword = (window.location.hash === "#editPassword");
    	this.passwordToggleText = "";
    	this.showEditPrivacyPreferences = (window.location.hash === "#editPrivacyPreferences");
    	this.privacyPreferencesToggleText = "";
    	this.showEditSecurityQuestion = (window.location.hash === "#editSecurityQuestion");
    	this.securityQuestionToggleText = "";
    	this.showEditSocialSettings = (window.location.hash === "#editSocialNetworks");
    	this.socialNetworksToggleText = "";
    	this.showEdit2FA = (window.location.hash === "#edit2FA");
    	this.twoFAToggleText = "";
    	this.showEditGetMyData = this.featuresService.isFeatureEnabled('GET_MY_DATA');
    }

	deactivateUpdateToggleText(): void {
        if (this.showEditDeactivate) {
            this.deactivateToggleText = om.get("manage.editTable.hide");
        }
        else {
            this.deactivateToggleText = om.get("manage.editTable.deactivateRecord");
        }
    };

    deprecateUpdateToggleText(): void {
        if (this.showEditDeprecate) {
            this.deprecateToggleText = om.get("manage.editTable.hide");
        }
        else {
            this.deprecateToggleText = om.get("manage.editTable.removeDuplicate");
        } 
    };

    // email preferences edit row
    emailPreferencesUpdateToggleText() {
        if (this.showEditEmailPreferences) {
            this.emailPreferencesToggleText = om.get("manage.editTable.hide");
        }
        else {
            this.emailPreferencesToggleText = om.get("manage.editTable.edit");
        } 
    };

    // email edit row
    emailUpdateToggleText(): void {
        if (this.showEditEmail) {
            this.emailToggleText = om.get("manage.editTable.hide");
        }
        else {
            this.emailToggleText = om.get("manage.editTable.edit");
        } 
    };

    getMyDataUpdateToggleText(): void {
        if (this.showEditGetMyData){
            this.getMyDataToggleText=om.get("manage.editTable.hide");    
        } else {
            this.getMyDataToggleText=om.get("manage.editTable.show"); 
        }       
    };

    languageUpdateToggleText(): void {
        if (this.showEditLanguage) {
            this.languageToggleText = om.get("manage.editTable.hide");
        }
        else {
            this.languageToggleText = om.get("manage.editTable.edit");
        } 
    };

    openEmailEdit(): void {
        this.showEditEmail = true;
        this.emailUpdateToggleText();
        window.location.hash = "#editEmail"
    };

    // password edit row
    passwordUpdateToggleText(): void {
        if (this.showEditPassword) {
            this.passwordToggleText = om.get("manage.editTable.hide");
        }
        else {
            this.passwordToggleText = om.get("manage.editTable.edit");
        } 
    };

    // privacy preferences edit row
    privacyPreferencesUpdateToggleText(): void {
        if (this.showEditPrivacyPreferences) {
            this.privacyPreferencesToggleText = om.get("manage.editTable.hide");
        }
        else {
            this.privacyPreferencesToggleText = om.get("manage.editTable.edit");
        }
    };

    // security question edit row
    securityQuestionUpdateToggleText() {
        if (this.showEditSecurityQuestion) {
            this.securityQuestionToggleText = om.get("manage.editTable.hide");
        }
        else {
            this.securityQuestionToggleText = om.get("manage.editTable.edit");
        } 
    };

    socialNetworksUpdateToggleText(): void {
        if (this.showEditSocialSettings) {
            this.socialNetworksToggleText = om.get("manage.socialNetworks.hide");
        }
        else {
            this.socialNetworksToggleText = om.get("manage.socialNetworks.edit");
        }
    };

    toggle2FAEdit(): void {
        this.showEdit2FA = !this.showEdit2FA;
        this.update2FAToggleText();
    };

    toggleGetMyDataEdit(): void {
        this.showEditGetMyData = !this.showEditGetMyData;        
        this.getMyDataUpdateToggleText();      
    };
    
    toggleDeactivateEdit(): void {
        this.showEditDeactivate = !this.showEditDeactivate;
        this.deactivateUpdateToggleText();
    };

    toggleDeprecateEdit(): void {
        this.showEditDeprecate = !this.showEditDeprecate;
        this.deprecateUpdateToggleText();
    };

    toggleEmailEdit(): void {
        this.showEditEmail = !this.showEditEmail;
        this.emailUpdateToggleText();
    };

    toggleEmailPreferencesEdit(): void {
        this.showEditEmailPreferences = !this.showEditEmailPreferences;
        this.emailPreferencesUpdateToggleText();
    };

    toggleLanguageEdit(): void {
        this.showEditLanguage = !this.showEditLanguage;
        this.languageUpdateToggleText();
    };

    togglePasswordEdit(): void {
        this.showEditPassword = !this.showEditPassword;
        this.passwordUpdateToggleText();
    };

    togglePrivacyPreferencesEdit(): void {
        this.showEditPrivacyPreferences = !this.showEditPrivacyPreferences;
        this.privacyPreferencesUpdateToggleText();
    };

    toggleSecurityQuestionEdit(): void {
        this.showEditSecurityQuestion = !this.showEditSecurityQuestion;
        this.securityQuestionUpdateToggleText();
    };

    toggleSocialNetworksEdit(): void {
        this.showEditSocialSettings = !this.showEditSocialSettings;
        this.socialNetworksUpdateToggleText();
    }; 

    update2FAToggleText(): void {
        if (this.showEdit2FA) {
            this.twoFAToggleText = om.get("manage.editTable.hide");
        }
        else {
            this.twoFAToggleText = om.get("manage.editTable.edit");
        } 
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
    	this.languageUpdateToggleText();
        this.emailUpdateToggleText(); 
        this.getMyDataUpdateToggleText();
        this.passwordUpdateToggleText();
        this.deactivateUpdateToggleText();
        this.deprecateUpdateToggleText();
        this.update2FAToggleText();
        this.privacyPreferencesUpdateToggleText();
        this.emailPreferencesUpdateToggleText();
        this.securityQuestionUpdateToggleText();
        this.socialNetworksUpdateToggleText();
    }; 
}
