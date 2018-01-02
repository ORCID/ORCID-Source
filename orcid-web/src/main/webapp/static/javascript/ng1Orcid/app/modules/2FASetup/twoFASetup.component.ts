declare var getBaseUri: any;

import { NgFor, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { TwoFAStateService } 
    from '../../shared/twoFAState.service.ts';

@Component({
    selector: 'two-fa-setup-ng2',
    template:  scriptTmpl("two-fa-setup-ng2-template")
})
export class TwoFASetupComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    recoveryCodes: string;

    constructor( 
        private twoFAStateService: TwoFAStateService,
    ) {
        this.recoveryCodes = '';
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
                console.log("An error occurred copying recovery codes");
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
        /*
        $('#getTextCode').click(function() {
            $.ajax({
                url: getBaseUri() + '/2FA/secret.json',
                dataType: 'json',
                success: function(data) {
                    $scope.textCodeFor2FA = data.secret;
                    $scope.showTextCode = true;
                    $scope.showQRCode = false;
                    $scope.$apply();
                }
            }).fail(function(err) {
                console.log("An error occurred getting 2FA secret");
            });
        });
        */

    }

    getRecoveryCodesString(): string {
        var recoveryCodesString = "";
        for (var i = 0; i < this.recoveryCodes.length; i++) {
            recoveryCodesString += this.recoveryCodes[i] + "\n";
        }
        return recoveryCodesString;
    }

    /*
    disable2FA(): void {
        this.twoFASetupService.disable()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                console.log('this.getForm', data);
                this.update2FAStatus( data );

            },
            error => {
                console.log('An error occurred disabling user 2FA', error);
            } 
        );
    };

    enable2FA(): void {
        window.location.href = getBaseUri() + '/2FA/setup';
    };

    update2FAStatus(status): void {
        this.showEnabled2FA = status.enabled;
        this.showDisabled2FA = !status.enabled;
        //$scope.$apply();
    };

    check2FASetup(): void {
        this.twoFASetupService.checkSetup()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                console.log('this.getForm', data);
                this.update2FAStatus( data );

            },
            error => {
                console.log('getTwoFASetupFormError', error);
            } 
        );
    };
    */


    //Default init functions provided by Angular Core
    ngAfterViewInit() {
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
    };

}

/*
declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const _2FASetupCtrl = angular.module('orcidApp').controller(
    '2FASetupCtrl', 
    [
        '$compile', 
        '$scope', 
        function (
            $compile,
            $scope
        ) {
            
            
            

            $scope.sendVerificationCode = function() {
                $('#sendVerificationCode').prop('disabled', true);
                $.ajax({
                    url: getBaseUri() + '/2FA/register.json',
                    dataType: 'json',
                    data: angular.toJson($scope.twoFactorAuthRegistration),
                    contentType: 'application/json;charset=UTF-8',
                    type: 'POST',
                    success: function(data) {               
                        if (data.valid) {
                            $scope.showSetup2FA = false;
                            $scope.show2FARecoveryCodes = true;
                            $scope.recoveryCodes = data.backupCodes;
                            $scope.showInvalidCodeError=false;
                        } else {
                            $('#sendVerificationCode').prop('disabled', false);
                            $scope.showInvalidCodeError=true;
                        }
                        $scope.$apply();
                    }
                }).fail(function() {
                    console.log("error posting 2fa registration to server");
                });
            };

            $scope.startSetup = function() {
                $.ajax({
                    url: getBaseUri() + '/2FA/QRCode.json',
                    dataType: 'json',
                    success: function(data) {
                        $("#2FA-QR-code").attr("src", data.url);
                        $scope.showSetup2FA = true;
                        $scope.showQRCode = true;
                        $scope.showTextCode = false;
                        $scope.show2FARecoveryCodes = false;
                        
                        $.ajax({
                            url: getBaseUri() + '/2FA/register.json',
                            dataType: 'json',
                            success: function(data) {
                                $scope.twoFactorAuthRegistration = data;
                                $scope.$apply();
                            }
                        }).fail(function(err) {
                            console.log("An error occurred getting 2FA registration object");
                        });
                    }
                }).fail(function(err) {
                    console.log("An error occurred getting user's 2FA QR code");
                });
            };
            
            //Convert to angular...
            
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class _2FASetupCtrlNg2Module {}
*/