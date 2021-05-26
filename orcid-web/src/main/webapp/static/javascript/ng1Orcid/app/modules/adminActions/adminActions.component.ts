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
    from '../../shared/adminActions.service';    

import { CommonService } 
    from '../../shared/common.service';
    
@Component({
    selector: 'admin-actions-ng2',
    template:  scriptTmpl("admin-actions-ng2-template")
})
export class AdminActionsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();    
   
    // Switch user
    switchId: string;
    showSwitchUser: boolean;
    switchUserError: boolean;
    switchUserParams: any;
    
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
    emailAddressVerified: boolean;
    showResetPasswordMessages: boolean;

    // Verify email
    showVerifyEmail: boolean;
    emailToVerify: string;
    verifyEmailMessage: string;
    verifyEmailMessageShowMessages: boolean;
    
    // Add delegates
    showAddDelegates: boolean;
    trustedVerified: boolean;
    managedVerified: boolean;
    addDelegateParams: any;
    showAddDelegatesMessages: boolean;
    
    orcidOrEmail: string;
    
    // Deprecate record
    showDeprecateRecord: boolean;
    showDeprecateRecordConfirm: boolean;
    deprecateRecordParams: any;
    showDeprecateRecordMessages: boolean;

    // Deactivate record
    showDeactivateRecord: boolean;
    deactivateResults: any;
    showDeactivateRecordMessages: boolean;

    // Reactivate record
    showReactivateRecord: boolean;
    showReactivateRecordConfirm: boolean;
    elementToReactivate: any;
    showReactivateRecordMessages: boolean;

    // Lock record
    lockReasons: any;
    showLockRecord: boolean;
    lockRecordsParams: any;
    lockResults: any;
    showLockRecordMessages: boolean;

    // Unlock record
    showUnlockRecord: boolean;
    unlockResults: any;
    showUnlockRecordMessages: boolean;

    // Review record
    showReviewRecord: boolean;
    reviewResults: any;
    showReviewRecordMessages: boolean;

    // Unreview record
    showUnreviewRecord: boolean;
    unreviewResults: any;
    showUnreviewRecordMessages: boolean;

    // Lookup ids or emails
    showLookupIdOrEmail: boolean;
    idsString: string;
    showLookupIdOrEmailMessages: boolean;

    // Resend claim email
    showResendClaimEmail: boolean;
    resendClaimResults: any;
    showResendClaimEmailMessages: boolean;

    // Disable 2FA
    showDisable2FA: boolean;
    disable2FAResults: any;
    toDisableIdsOrEmails: string; 
    
    // Force indexing
    showForceIndexing: boolean;
    forceIndexingMessage: string;
    idsToIndex: string;
    forceIndexingMessageShowMessages: boolean;

    // General
    ids: string;
    
    constructor(
        private adminActionsService: AdminActionsService,
        private commonSrvc: CommonService
    ) {
        this.showSwitchUser = false;
        this.switchUserError = false;
        this.switchUserParams = {};
        
        this.csvIdsOrEmails = '';
        this.showFindIds = false;
        this.showIds = false;
        this.profileList = {};
    
        this.showResetPassword = false;
        this.resetPasswordParams = {};
        this.showResetPasswordConfirm = false;
        this.resetPasswordSuccess = false;
        this.emailAddressVerified = false;
        this.showResetPasswordMessages = false;

        this.showVerifyEmail = false;
        this.verifyEmailMessage = null;
        this.verifyEmailMessageShowMessages = false;

        this.showAddDelegates = false;
        this.trustedVerified = false;
        this.managedVerified = false;
        this.addDelegateParams = {trusted : {errors: [], value: ''}, managed : {errors: [], value: ''}};
        this.showAddDelegatesMessages = false;

        this.showDeprecateRecord = false;
        this.showDeprecateRecordConfirm = false;
        this.deprecateRecordParams = {deprecatedAccount : {errors: [], orcid:''}, primaryAccount : {errors: [], orcid:''}, errors: []};
        this.showDeprecateRecordMessages = false;
        this.showDeactivateRecord = false;
        this.deactivateResults = {};
        this.showDeactivateRecordMessages = false;

        this.showReactivateRecord =  false;
        this.showReactivateRecordConfirm = false;
        this.elementToReactivate = {errors: [], orcid:'', email:''};
        this.showReactivateRecordMessages = false;

        this.showLockRecord = false;
        this.lockResults = {};
        this.lockRecordsParams = {orcidsToLock:'', lockReason:'', description:''};
        this.showLockRecordMessages = false;

        this.showUnlockRecord = false;
        this.unlockResults = {};
        this.showUnlockRecordMessages = false;

        this.showReviewRecord = false;
        this.reviewResults = {};
        this.showReviewRecordMessages = false;

        this.showUnreviewRecord = false;
        this.unreviewResults = {};
        this.showUnreviewRecordMessages = false;
        
        this.showLookupIdOrEmail = false;
        this.idsString = '';

        this.showResendClaimEmail = false;
        this.resendClaimResults = {};
        this.showResendClaimEmailMessages = false;

        this.showDisable2FA = false;
		this.disable2FAResults = {};
		this.toDisableIdsOrEmails= '';

        this.showForceIndexing = false;
        this.forceIndexingMessage = '';
        this.forceIndexingMessageShowMessages = false;

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
                this.showLockRecordMessages = true;
                setTimeout (() => {
                    this.showLockRecordMessages = false;
                }, 3000)
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
                    this.adminActionsService.switchUserPost(data.id).subscribe(
				        data => {
				          window.location.replace(getBaseUri() + '/my-orcid');
				        },
				        error => {
				          // reload page anyway
				          // switchUser request is handled by OrcidSwitchUserFilter.java which redirects /switch-user to /my-orcid
				          // in non-local environments neither request completes successfully, although the user has been successfully switched
				          window.location.replace(getBaseUri() + '/my-orcid');
				        }
				      );
                                      
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
    
    validateResetPassword(): void {        
        if(this.resetPasswordParams != null && this.resetPasswordParams.orcidOrEmail != null) {
            this.adminActionsService.resetPasswordValidate( this.resetPasswordParams ) 
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    if(data.error == undefined || data.error == '') {
                        this.emailAddressVerified = true;
                    } else {
                        this.emailAddressVerified = false;
                    }                                  
                },
                error => {
                    console.log('admin: validateResetPassword error', error);
                } 
            );    
        }
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
                    this.emailAddressVerified = false;
                }
                this.showResetPasswordMessages = true;
                setTimeout (() => {
                    this.showResetPasswordMessages = false;
                }, 3000)
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
                this.verifyEmailMessageShowMessages = true;
                setTimeout (() => {
                    this.verifyEmailMessageShowMessages = false;
                }, 3000)
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
                this.showAddDelegatesMessages = true;
                setTimeout (() => {
                    this.showAddDelegatesMessages = false;
                }, 3000)
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
                this.showDeprecateRecordMessages = true;
                setTimeout (() => {
                    this.showDeprecateRecordMessages = false;
                }, 3000);
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
                this.showDeactivateRecordMessages = true;
                setTimeout (() => {
                    this.showDeactivateRecordMessages = false;
                }, 16000);
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
                this.showReactivateRecordMessages = true;
                setTimeout (() => {
                    this.showReactivateRecordMessages = false;
                }, 3000);
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
                this.showLockRecordMessages = true;
                setTimeout (() => {
                    this.showLockRecordMessages = false;
                }, 3000);
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
                this.showUnlockRecordMessages = true;
                setTimeout (() => {
                    this.showUnlockRecordMessages = false;
                }, 3000);
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
                this.showReviewRecordMessages = true;
                setTimeout (() => {
                    this.showReviewRecordMessages = false;
                }, 3000);
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
                this.showUnreviewRecordMessages = true;
                setTimeout (() => {
                    this.showUnreviewRecordMessages = false;
                }, 3000);
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
                this.showResendClaimEmailMessages = true;
                setTimeout (() => {
                    this.showResendClaimEmailMessages = false;
                }, 3000);
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

    disable2FA(): void {
        this.adminActionsService.disable2FA( this.toDisableIdsOrEmails )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.disable2FAResults = data;
				this.showDisable2FA = true;
                setTimeout (() => {
                    this.showDisable2FA = false;
                }, 10000);         
            },
            error => {
                console.log('admin: disable 2FA error', error);
            } 
        );
    };
    
    forceIndexing(): void {
        this.adminActionsService.forceIndexing( this.idsToIndex )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.forceIndexingMessage = 'Following ids has been sent to index, the process could take up to 30 minutes: ' + data;
				this.forceIndexingMessageShowMessages = true;
                setTimeout (() => {
                    this.forceIndexingMessageShowMessages = false;
                }, 10000);         
            },
            error => {
                console.log('admin: forceIndexing', error);
            } 
        );
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
    }; 
}