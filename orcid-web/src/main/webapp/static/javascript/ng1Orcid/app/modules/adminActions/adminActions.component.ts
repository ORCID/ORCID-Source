//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { AdminActionsService } 
    from '../../shared/adminActions.service.ts';    

import { CommonService } 
    from '../../shared/common.service.ts';
    
@Component({
    selector: 'admin-actions-ng2',
    template:  scriptTmpl("admin-actions-ng2-template"),
    providers: [CommonService]
})
export class AdminActionsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();    
   
    // Switch user
    switchId: string;
    showSwitchUser: boolean;
    switchUserError: boolean;
    
    // Find ids
    csvIdsOrEmails: string;
    profileList: any;
    showFindIds: boolean;
    showIds: boolean;
    
    // Reset password
    showResetPassword: boolean;
    resetPasswordParams: any;
    showResetPasswordConfirm: boolean;
    resetPasswordSuccess: boolean;
    
    // Verify email
    showVerifyEmail: boolean;
    emailToVerify: string;
    verifyEmailMessage: string;
    
    // Add delegates
    showAddDelegates: boolean;
    trustedVerified: boolean;
    managedVerified: boolean;
    addDelegateParams: any;
    
    // Remove security question
    showRemoveSecurityQuestion: boolean;
    orcidOrEmail: string;
    removeSecurityQuestionResult: string;
    showRemoveSecurityQuestionConfirm: boolean;
    
    // Deprecate record
    showDeprecateRecord: boolean;
    showDeprecateRecordConfirm: boolean;
    deprecateRecordParams: any;
    
    // Deactivate record
    showDeactivateRecord: boolean;
    deactivateResults: any;
    
    // Reactivate record
    showReactivateRecord: boolean;
    showReactivateRecordConfirm: boolean;
    elementToReactivate: any;
    
    // Lock record
    lockReasons: any;
    showLockRecord: boolean;
    lockRecordsParams: any;
    lockResults: any;
    
    // Unlock record
    showUnlockRecord: boolean;
    unlockResults: any;

    // Review record
    showReviewRecord: boolean;
    reviewResults: any;
    
    // Unreview record
    showUnreviewRecord: boolean;
    unreviewResults: any;
    
    // Lookup ids or emails
    showLookupIdOrEmail: boolean;
    idsString: string;
    
    // Resend claim email
    showResendClaimEmail: boolean;
    resendClaimResults: any;
    
    // General
    ids: string;
    
    constructor(
        private adminActionsService: AdminActionsService,
        private commonSrvc: CommonService
    ) {
        this.showSwitchUser = false;
        this.switchUserError = false;
        
        this.csvIdsOrEmails = '';
        this.showFindIds = false;
        this.showIds = false;
        this.profileList = {};
    
        this.showResetPassword = false;
        this.resetPasswordParams = {};
        this.showResetPasswordConfirm = false;
        this.resetPasswordSuccess = false;
    
        this.showVerifyEmail = false;
        this.verifyEmailMessage = null;
        
        this.showAddDelegates = false;
        this.trustedVerified = false;
        this.managedVerified = false;
        this.addDelegateParams = {trusted : {errors: [], value: ''}, managed : {errors: [], value: ''}};
        
        this.showRemoveSecurityQuestion = false;
        this.showRemoveSecurityQuestionConfirm = false;
        
        this.showDeprecateRecord = false;
        this.showDeprecateRecordConfirm = false;
        this.deprecateRecordParams = {deprecatedAccount : {errors: [], orcid:''}, primaryAccount : {errors: [], orcid:''}, errors: []};
    
        this.showDeactivateRecord = false;
        this.deactivateResults = {};
        
        this.showReactivateRecord =  false;
        this.showReactivateRecordConfirm = false;
        this.elementToReactivate = {errors: [], orcid:'', email:''};
    
        this.showLockRecord = false;
        this.lockResults = {};
        this.lockRecordsParams = {orcidsToLock:'', lockReason:'', description:''};
        
        this.showUnlockRecord = false;        
        this.unlockResults = {};
        
        this.showReviewRecord = false;
        this.reviewResults = {};
        
        this.showUnreviewRecord = false;
        this.unreviewResults = {};
        
        this.showLookupIdOrEmail = false;
        this.idsString = '';
        
        this.showResendClaimEmail = false;
        this.resendClaimResults = {};
        
        // General
        this.ids = '';
        
        this.getLockReasons();
    }    

    getLockReasons(): void {
        this.adminActionsService.getLockReasons( )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.lockReasons = data;
                this.lockRecordsParams.lockReason = data[0];                
            },
            error => {
                console.log('admin: getLockReasons error', error);
            } 
        );
    }
    
    switchUser(id): void {
        this.adminActionsService.adminSwitchUserValidate(id)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {                
                if(data != null && data.errorMessg == null) {
                    this.switchUserError = false;
                    window.location.replace(getBaseUri() + '/switch-user?username=' + data.id);                    
                } else {
                    this.switchUserError = true;
                }
            },
            error => {
                console.log('admin: switchUser error', error);
                this.switchUserError = true;
            } 
        );
        
    };
    
    findIds(): void {
        this.adminActionsService.findIds( this.csvIdsOrEmails )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.showIds = true;
                if(data) {
                    this.profileList = data;
                } else {
                    this.profileList = {};
                }                
            },
            error => {
                console.log('admin: findIds error', error);
            } 
        );
    };
    
    randomString(): void {
        this.commonSrvc.randomString()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.resetPasswordParams.password = data;
            },
            error => {
                console.log('admin: randomString', error);
            } 
        );
    };
    
    confirmResetPassword(): void {
        if(this.resetPasswordParams != null && this.resetPasswordParams.orcidOrEmail != null && this.resetPasswordParams.password != null) {
            this.adminActionsService.resetPasswordValidate( this.resetPasswordParams ) 
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.resetPasswordParams = data;
                    if(this.resetPasswordParams.error == undefined || this.resetPasswordParams.error == '') {
                        this.showResetPasswordConfirm = true;
                    }
                },
                error => {
                    console.log('admin: confirmResetPassword error', error);
                } 
            );                                                   
        }        
    };
    
    resetPassword(): void {        
        this.adminActionsService.resetPassword( this.resetPasswordParams )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.showResetPasswordConfirm = false;                
                this.resetPasswordParams = data;      
                if(this.resetPasswordParams.error == undefined || this.resetPasswordParams.error == '') {
                    this.resetPasswordParams.orcidOrEmail = '';
                    this.resetPasswordParams.password = '';
                    this.showResetPasswordConfirm = false;
                    this.resetPasswordSuccess = true;
                }
            },
            error => {
                console.log('admin: resetPassword error', error);
            } 
        );        
    };
    
    verifyEmail(): void {        
        this.adminActionsService.verifyEmail( this.emailToVerify )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emailToVerify = '';
                this.verifyEmailMessage = data;
            },
            error => {
                console.log('admin: verifyEmail error', error);
            } 
        );      
    };
    
    addDelegate(): void {        
        this.adminActionsService.addDelegate( this.addDelegateParams )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {                
                this.addDelegateParams = data;
            },
            error => {
                console.log('admin: verifyEmail error', error);
            } 
        );   
    };
    
    confirmRemoveSecurityQuestion(): void {
        if(this.orcidOrEmail != null) {
            this.showRemoveSecurityQuestionConfirm = true;
        }
    };
    
    removeSecurityQuestion(): void {
        this.adminActionsService.removeSecurityQuestion( this.orcidOrEmail )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {                
                this.removeSecurityQuestionResult = data;
                this.showRemoveSecurityQuestionConfirm = false;
            },
            error => {
                console.log('admin: verifyEmail error', error);
            } 
        );          
    };
    
    confirmDeprecate(): void {        
        this.adminActionsService.validateDeprecateRequest( this.deprecateRecordParams )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {                
                this.deprecateRecordParams = data;
                if(this.deprecateRecordParams.errors.length == 0) {
                    this.showDeprecateRecordConfirm = true;
                }
            },
            error => {
                console.log('admin: confirmDeprecate error', error);
            } 
        ); 
    };
    
    deprecateRecord(): void {
        this.adminActionsService.deprecateRecord( this.deprecateRecordParams )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.deprecateRecordParams = data;
                if(this.deprecateRecordParams.errors.length == 0) {
                    this.showDeprecateRecordConfirm = false;                    
                }
            },
            error => {
                console.log('admin: confirmDeprecate error', error);
            } 
        ); 
    };
    
    deprecateRecordReset(): void {
        this.showDeprecateRecord = false;
        this.showDeprecateRecordConfirm = false;
        this.deprecateRecordParams = {deprecatedAccount : {errors: [], orcid:''}, primaryAccount : {errors: [], orcid:''}, errors: []};
    };
    
    deactivateRecord(): void {
        this.adminActionsService.deactivateRecord( this.ids )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.deactivateResults = data;                
            },
            error => {
                console.log('admin: deactivateRecord error', error);
            } 
        ); 
    };
    
    reactivateRecord(): void {
        this.adminActionsService.reactivateRecord( this.elementToReactivate )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.elementToReactivate = data; 
                this.showReactivateRecordConfirm = false;                
            },
            error => {
                console.log('admin: reactivateRecord error', error);
            } 
        );
    };
    
    reactivateRecordReset(): void {
        this.showReactivateRecordConfirm = false;
        this.elementToReactivate = {errors: [], orcid:'', email:''};
    };
    
    lockRecords(): void {
        this.adminActionsService.lockRecords( this.lockRecordsParams )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.lockResults = data;                              
            },
            error => {
                console.log('admin: lockRecords error', error);
            } 
        );
    };
    
    unlockRecords(): void {
        this.adminActionsService.unlockRecords( this.ids )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.unlockResults = data;                              
            },
            error => {
                console.log('admin: lockRecords error', error);
            } 
        );
    };
    
    reviewRecords(): void {
        this.adminActionsService.reviewRecords( this.ids )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.reviewResults = data;                              
            },
            error => {
                console.log('admin: reviewRecords error', error);
            } 
        );
    };
    
    unreviewRecords(): void {
        this.adminActionsService.unreviewRecords( this.ids )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.unreviewResults = data;                              
            },
            error => {
                console.log('admin: unreviewRecords error', error);
            } 
        );
    };
    
    lookupIdOrEmails(): void {
        this.adminActionsService.lookupIdOrEmails( this.csvIdsOrEmails )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.idsString = data;                
            },
            error => {
                console.log('admin: lookupIdOrEmails error', error);
            } 
        );
    };
    
    resendClaimEmail(): void {
        this.adminActionsService.resendClaimEmail( this.ids )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.resendClaimResults = data;                
            },
            error => {
                console.log('admin: lookupIdOrEmails error', error);
            } 
        );
    };
    
    checkClaimedStatus( element ): void {
        let id = '';
        if(element == 'trusted') {
            this.trustedVerified = false;
            id = this.addDelegateParams.trusted.value;
        } else {
            this.managedVerified = false;
            id = this.addDelegateParams.managed.value;
        }
        this.adminActionsService.checkClaimedStatus( id )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data) {
                    if(element == 'trusted') {
                        this.trustedVerified = true;
                    } else {
                        this.managedVerified = true;
                    }
                }                              
            },
            error => {
                console.log('admin: checkClaimedStatus error', error);
            } 
        );
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
    }; 
}