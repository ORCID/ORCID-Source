declare var getBaseUri: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
    
import { takeUntil } 
    from 'rxjs/operators';

import { TwoFAStateService } 
    from '../../shared/twoFAState.service.ts';



@Component({
    selector: 'two-fa-setup-ng2',
    template:  scriptTmpl("two-fa-setup-ng2-template")
})
export class TwoFaSetupComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    recoveryCodes: string;
    qrCodeUrl: string;
    textCodeFor2FA: string;
    show2FARecoveryCodes: boolean;
    showInvalidCodeError: boolean;
    showQRCode: boolean;
    showSetup2FA: boolean;
    showTextCode: boolean;
    twoFactorAuthRegistration: any;
    sendVerificationCodeDisabled: boolean;

    constructor( 
        private twoFAStateService: TwoFAStateService,
    ) {
        this.recoveryCodes = '';
        this.qrCodeUrl = '#';
        this.textCodeFor2FA = '';
        this.show2FARecoveryCodes = false;
        this.showInvalidCodeError = false;
        this.showQRCode = false;
        this.showSetup2FA = false;
        this.showTextCode = false;
        this.twoFactorAuthRegistration = {};
    }

    cancel2FASetup(): void {
        window.location.href = getBaseUri() + "/account";
    };

    copyRecoveryCodes(): void {
        let recoveryCodesString = this.getRecoveryCodesString();
        
        if ( (<any>window).clipboardData ) { // for IE
            (<any>window).clipboardData.setData("Text", recoveryCodesString);        
        } else {
            var temp = $('<div />');
            temp.text(recoveryCodesString);
            temp.css({
                position: "absolute",
                left:     "-1000px",
                top:      "-1000px",
            });
            
            $('body').append(temp);
            
            var range = document.createRange();
            range.selectNodeContents(temp.get(0));
            
            var selection = window.getSelection();
            selection.removeAllRanges();
            selection.addRange(range);
            
            var copied = document.execCommand('copy', false, null);
            if (!copied) {
                //console.log("An error occurred copying recovery codes");
            }
            
            temp.remove();
        }
    };  

    done(): void {
        window.location.href = getBaseUri() + "/account";
    };

    downloadRecoveryCodes(): void {
        let recoveryCodesString = this.getRecoveryCodesString();
        let element = document.createElement('a');
        element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(recoveryCodesString));
        element.setAttribute('download', 'recovery-codes.txt');
    
        element.style.display = 'none';
        document.body.appendChild(element);
    
        element.click();
    
        document.body.removeChild(element);
    };

    getTextCodeClick(): void{
        this.twoFAStateService.getTextCode()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.textCodeFor2FA = data.secret;
                this.showTextCode = true;
                this.showQRCode = false;
            },
            error => {
                console.log('Error getting 2FA code', error);
            } 
        );
    }
    
    showQRCodeAgain(): void {
        this.showTextCode = false;
        this.showQRCode = true;
    }
    
    getRecoveryCodesString(): string {
        var recoveryCodesString = "";
        for (var i = 0; i < this.recoveryCodes.length; i++) {
            recoveryCodesString += this.recoveryCodes[i] + "\n";
        }
        return recoveryCodesString;
    }

    register(): void {
        this.twoFAStateService.register()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                //console.log('this.getForm', data);
                this.twoFactorAuthRegistration = data;
            },
            error => {
                //console.log('An error occurred disabling user 2FA', error);
            } 
        );
    }

    sendVerificationCode(): void {
        this.sendVerificationCodeDisabled = true;
        this.twoFAStateService.sendVerificationCode( this.twoFactorAuthRegistration )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                //console.log('this.getForm', data);
                if (data.valid) {
                    this.showSetup2FA = false;
                    this.show2FARecoveryCodes = true;
                    this.recoveryCodes = data.backupCodes;
                    console.log(data.backupCodes);
                    this.showInvalidCodeError=false;
                } else {
                    this.sendVerificationCodeDisabled = false;
                    this.showInvalidCodeError=true;
                }
            },
            error => {
                //console.log('An error occurred disabling user 2FA', error);
            } 
        );
    };

    startSetup(): void {
        this.twoFAStateService.startSetup()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                //console.log('this.getForm', data);
                this.qrCodeUrl = data.url;
                this.showSetup2FA = true;
                this.showQRCode = true;
                this.showTextCode = false;
                this.show2FARecoveryCodes = false;

                this.register();
            },
            error => {
                //console.log('getTwoFASetupFormError', error);
            } 
        );

    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.startSetup();
    };

}