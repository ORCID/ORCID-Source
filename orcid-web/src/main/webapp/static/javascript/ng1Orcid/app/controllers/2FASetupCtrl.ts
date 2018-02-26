//Migrated

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
            
            $scope.cancel2FASetup = function() {
                window.location.href = getBaseUri() + "/account";
            };

            $scope.copyRecoveryCodes = function() {
                var recoveryCodesString = getRecoveryCodesString();
                
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

            $scope.done = function() {
                window.location.href = getBaseUri() + "/account";
            };

            $scope.downloadRecoveryCodes = function() {
                var recoveryCodesString = getRecoveryCodesString();
                var element = document.createElement('a');
                element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(recoveryCodesString));
                element.setAttribute('download', 'recovery-codes.txt');
            
                element.style.display = 'none';
                document.body.appendChild(element);
            
                element.click();
            
                document.body.removeChild(element);
            };

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
            
            $('#showQRCodeAgain').click(function() {
                $scope.showTextCode = false;
                $scope.showQRCode = true;
            });
            
            //Convert to angular...
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
            
            function getRecoveryCodesString() {
                var recoveryCodesString = "";
                for (var i = 0; i < $scope.recoveryCodes.length; i++) {
                    recoveryCodesString += $scope.recoveryCodes[i] + "\n";
                }
                return recoveryCodesString;
            }
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class _2FASetupCtrlNg2Module {}