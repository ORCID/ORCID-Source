//unused, merged with emailsForm.component

declare var $: any;
declare var colorbox: any;
declare var fixZindexIE7: any;
declare var isIE: any;
declare var orcidVar: any;

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

import { EmailService } 
    from '../../shared/email.service.ts'; 

import { PreferencesService } 
    from '../../shared/preferences.service.ts'; 


@Component({
    selector: 'email-edit-ng2',
    template:  scriptTmpl("email-edit-ng2-template")
})
export class EmailEditComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    baseUri: any;
    curPrivToggle: any;
    isPassConfReq: any;
    password: any;
    privacyHelp: any;
    scrollTop: any;
    showConfirmationBox: any;
    showDeleteBox: any;
    showElement: any;
    showEmailVerifBox: any;
    showUnverifiedEmailSetPrimaryBox: any;
    verifyEmailObject: any;
    position: any;
   
    constructor(
        private emailSrvc: EmailService,
        private prefsSrvc: PreferencesService
    ) {
        this.baseUri = orcidVar.baseUri;
        this.curPrivToggle = null;
        this.isPassConfReq = orcidVar.isPasswordConfirmationRequired;
        this.password = null;
        this.privacyHelp = {};
        this.scrollTop = 0;
        this.showConfirmationBox = false;
        this.showDeleteBox = false;
        this.showElement = {};
        this.showEmailVerifBox = false;
        this.showUnverifiedEmailSetPrimaryBox = false;
        this.verifyEmailObject = {};
        this.position = 0;
    }

    asianEmailTableStyleFix(): void {
        /*
        if ($cookies.get('locale_v3') == 'zh_CN' 
            || $cookies.get('locale_v3') == 'zh_TW' 
            || $cookies.get('locale_v3') == 'ja' 
            || $cookies.get('locale_v3') == 'ko'){     
            : any;.styleFix = false;


            $scope.$watch(
                function () {
                    return angular.element(document.getElementsByClassName("email-verified")[0]).length; 
                },
                function (newValue, oldValue) {
                    if(newValue > oldValue){
                        this.styleFix = true;                        
                    }        
                }
            );
            
        };
        */
    };

    checkCredentials(popup): void {
        this.password = null;
        if(orcidVar.isPasswordConfirmationRequired){
            if (!popup){
                /*
                $.colorbox({
                    html: $compile($('#check-password-modal').html())($scope)
                });
                $.colorbox.resize();
                */
            }else{
                this.showConfirmationBox = true;            
            }
        }else{
            //this.submitModal();
        }
    };

    closeDeleteBox(): void {
        this.showDeleteBox = false;
    };

    closeModal(): void {
        
        $('#cboxLoadedContent').css({         
            overflow: 'auto'
        });
        
        $.colorbox.close();
    };

    closeUnverifiedEmailSetPrimaryBox(): void{
        this.showUnverifiedEmailSetPrimaryBox = false;
    };

    closeVerificationBox(): void{
        this.showEmailVerifBox = false;
    };

    confirmDeleteEmail(email): void {
        //this.emailSrvc.delEmail = email;
        /*
        $.colorbox({
            html : $compile($('#delete-email-modal').html())($scope)
        });
        $.colorbox.resize();
        */
    };

    confirmDeleteEmailInline(email, $event): void {
        $event.preventDefault();
        this.showDeleteBox = true;
        //this.emailSrvc.delEmail = email;
        
        /*
        $scope.$watch(
            function () {
                return document.getElementsByClassName('delete-email-box').length; 
            },
            function (newValue, oldValue) {             
                $.colorbox.resize();
            }
        );
        */
    };

    deleteEmail(): void {
        this.emailSrvc.deleteEmail();
        this.closeModal();
    };

    deleteEmailInline(): void {
        this.emailSrvc.deleteEmail();
        this.showDeleteBox = false;            
    };



    getEmails(): void {
        this.emailSrvc.getEmails();
    };

    hideTooltip(el): void {
        this.showElement[el] = false;
    };

    setBulkGroupPrivacy(priv): void {
        /*
        for (var idx in this.emailSrvc.emails.emails) {
            this.emailSrvc.emails.emails[idx].visibility = priv;
        }         
        this.emailSrvc.saveEmail();
        */
    };

    setPrivacy(email, priv, $event): void {
        $event.preventDefault();
        email.visibility = priv;
        this.curPrivToggle = null;
        //this.emailSrvc.saveEmail();
    };

    showTooltip(el, event): void {       
        this.position = $(event.target.parentNode).parent().position();
        $('.edit-record-emails .popover-help-container').css({            
            top: this.position.top + 33,
            left: this.position.left
        });
        $('#cboxLoadedContent').css({         
            overflow: 'visible'
        });
        this.showElement[el] = true;
    };

    submitModal(obj, $event): void {
        /*
        this.emailSrvc.inputEmail.password = this.password;
        this.emailSrvc.addEmail();
        if(!this.emailSrvc.popUp){
            $.colorbox.close();    
        }
        */
    };

    toggleClickPrivacyHelp(key): void {
        if (document.documentElement.className.indexOf('no-touch') == -1) {
            this.privacyHelp[key]=!this.privacyHelp[key];
        }
    };

    verifyEmail(email, popup): void {
        
        this.verifyEmailObject = email;
        
                /*
        if( popup ){
            this.emailSrvc.verifyEmail(email,function(data) {
                this.showEmailVerifBox = true;
                //$.colorbox.resize();
           });    
        }else{
            this.emailSrvc.verifyEmail(email,function(data) {
                $.colorbox({
                    html : $compile($('#settings-verify-email-modal').html())($scope) 
                    //Name was changed to avoid conflicts with workspace verify email modal
                });
                $.colorbox.resize();
           });    
        }
                */
        
    };

    /*
    $scope.$on(
        'rebuildEmails', 
        function(event, data) {
            $scope.emailSrvc.emails = data;
        }
    );

    $scope.$on(
        'unverifiedSetPrimary', 
        function(event, data){
            if (data.newValue == true 
                && configuration.showModalManualEditVerificationEnabled == true) {
                $scope.showUnverifiedEmailSetPrimaryBox = true;
            }
            else {
                $scope.showUnverifiedEmailSetPrimaryBox =false;
            }
            $scope.$apply(); 
        }
    );
    */

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.emailSrvc.getEmails();
        this.emailSrvc.initInputEmail(); 
    }; 
}