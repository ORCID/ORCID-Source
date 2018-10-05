declare var om: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { AccountService } 
    from '../../shared/account.service.ts'; 

import { PreferencesService } 
    from '../../shared/preferences.service.ts';

@Component({
    selector: 'edit-table-ng2',
    template:  scriptTmpl("edit-table-ng2-template")
})
export class EditTableComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    
    changePasswordPojo: any;
    errorUpdatingVisibility: any;
    prefs: any;
    showSection: any;
    toggleText: any;
    
    constructor(
        private cdr:ChangeDetectorRef,
        private accountService: AccountService,
        private preferencesService: PreferencesService,
    ) {
        this.changePasswordPojo = {};
        this.errorUpdatingVisibility = false;
        this.prefs = {};
        this.showSection = {
            'deactivate': (window.location.hash === "#editDeactivate"),
            'deprecate': (window.location.hash === "#editDeprecate"),
            'editEmail': (window.location.hash === "#editEmail"),
            'editLanguage': false,
            'editPassword': (window.location.hash === "#editPassword"),
            'editPrivacy': (window.location.hash === "#editPrivacyPreferences"),
            'editSecurityQuestion': (window.location.hash === "#editSecurityQuestion"),
            'twoFA': (window.location.hash === "#edit2FA"),
            'getMyData': false
        };
        this.toggleText = {};
    }

    getChangePassword(): void {
        this.accountService.getChangePassword()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data) {
                    this.changePasswordPojo = data;
                }
            },
            error => {
                //console.log('getformDataError', error);
            } 
        );
 
    };

    getPreferences(): void {
        this.preferencesService.getPrivacyPreferences()
            .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            preferences => {
                this.prefs = preferences;
                this.cdr.detectChanges();
            },
            error => {
                // something bad is happening!
                console.log("error getting preferences");
            } 
        );
    };

    saveChangePassword(): void {
        this.accountService.saveChangePassword( this.changePasswordPojo )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.changePasswordPojo = data;

            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    }

    toggleSection(sectionName): void {
        this.showSection[sectionName] = !this.showSection[sectionName];
        this.updateToggleText(sectionName);
    };

    updateActivitiesVisibilityDefault(oldPriv, newPriv, $event: any): void {
        this.errorUpdatingVisibility = false;
        this.preferencesService.updateDefaultVisibility(newPriv)
            .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
                .subscribe(
                    response => {
                        this.prefs['default_visibility'] = newPriv;
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.prefs['default_visibility'] = oldPriv; 
                        this.errorUpdatingVisibility = true;
                        this.cdr.detectChanges();
                        // something bad is happening!
                        console.log("error updating preferences");
                    } 
                );    
    };

    updateToggleText(sectionName){
        switch(sectionName) { 
            case 'deactivate': { 
                if(this.showSection[sectionName]==true){
                    this.toggleText[sectionName] = om.get("manage.editTable.hide");
                } else {
                    this.toggleText[sectionName] = om.get("manage.editTable.deactivateRecord");
                }
                break; 
            } 
            case 'deprecate': { 
                if(this.showSection[sectionName]==true){
                    this.toggleText[sectionName] = om.get("manage.editTable.hide");
                } else {
                    this.toggleText[sectionName] = om.get("manage.editTable.removeDuplicate");
                }
                break; 
            } 
            case 'getMyData': {
                if(this.showSection[sectionName]==true){
                    this.toggleText[sectionName] = om.get("manage.editTable.hide");
                } else {
                    this.toggleText[sectionName] = om.get("manage.editTable.show");
                }
                break;
            }
            default: { 
                if(this.showSection[sectionName]==true){
                    this.toggleText[sectionName] = om.get("manage.editTable.hide");
                } else {
                    this.toggleText[sectionName] = om.get("manage.editTable.edit");
                }
                break; 
            } 
        } 

    }
   

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        for(var key in this.showSection){
            this.updateToggleText(key);
        }
        this.getChangePassword();
        this.getPreferences();
    }; 
}
