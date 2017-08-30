//Import all the angular components

import { NgFor } 
    from '@angular/common'; 

import { AfterViewInit, Component } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { BiographyService } 
    from '../../shared/biographyService.ts'; 

@Component({
    selector: 'biography-ng2',
    template:  scriptTmpl("biography-ng2-template")
})
export class BiographyComponent implements AfterViewInit {
    biographyForm: any;
    configuration: any;
    emails: any;
    emailSrvc: any;
    emailVerified: any;
    lengthError: any;
    showEdit: any;
    showElement: any;

    constructor(
        private biographyService: BiographyService
    ) {
        this.biographyForm = {
            biography: {
                value: ''
            }
        };
        //this.configuration = initialConfigService.getInitialConfiguration();;
        this.emails = {};
        //this.emailSrvc = emailSrvc;
        this.emailVerified = true; //change to false once service is ready
        this.lengthError = false;
        this.showEdit = false;
        this.showElement = {};
    }

    cancel(): void {
        this.getBiographyForm();
        this.showEdit = false;
    };

    checkLength(): any {
        if ( this.biographyForm.biography.value.length > 5000 ) {
            this.lengthError = true;
        } else {
            this.lengthError = false;
        }

        return !this.lengthError; //Negating the error, if error is present will be true and return false to avoid user input
    };

    close(): void {
        this.showEdit = false;
    };

    getBiographyForm(): void{
        this.biographyService.getBiographyData().subscribe(
            data => {
                this.biographyForm  = data;
            },
            error => {
                console.log('getBiographyFormError', error);
            } 
        );
    };

    hideTooltip(tp): void{
        this.showElement[tp] = false;
    };

    setBiographyForm(): any{
        if( this.checkLength() == false ){    
            return; // do nothing if there is a length error
        }
        this.biographyService.setBiographyData( this.biographyForm )
        .subscribe(
            data => {
                this.biographyForm  = data;
                this.close();
            },
            error => {
                console.log('setBiographyFormError', error);
            } 
        );
    };

    setPrivacy(priv, $event:any): void {
        $event.preventDefault();
        this.biographyForm.visiblity.visibility = priv;
        this.setBiographyForm();        
    };

    showTooltip(tp): void{
        this.showElement[tp] = true;
    };
    
    toggleEdit(): void {
        if( this.emailVerified === true || this.configuration.showModalManualEditVerificationEnabled == false){
            this.showEdit = !this.showEdit;
        }else{
            //this.showEmailVerificationModal();
        }
    };

    //Default init function provided by Angular Core
    ngAfterViewInit() {
        this.getBiographyForm();
    };
/*
export const BiographyCtrl = angular.module('orcidApp').controller(
    'BiographyCtrl',
    [
        '$scope',
        '$rootScope',
        '$compile',
        'emailSrvc',
        'initialConfigService', 
        function (
            $scope, 
            $rootScope, 
            $compile, 
            emailSrvc, 
            initialConfigService
        ) {
            $scope.biographyForm = null;
            $scope.emailSrvc = emailSrvc;
            $scope.lengthError = false;
            $scope.showEdit = false;
            $scope.showElement = {};

            /////////////////////// Begin of verified email logic for work
            var configuration = initialConfigService.getInitialConfiguration();;
            var emails = {};
            var emailVerified = false;

            var showEmailVerificationModal = function(){
                $rootScope.$broadcast('emailVerifiedObj', {flag: emailVerified, emails: emails});
            };
            
            $scope.emailSrvc.getEmails(
                function(data) {
                    emails = data.emails;
                    if( $scope.emailSrvc.getEmailPrimary().verified == true ) {
                        emailVerified = true;
                    }
                }
            );
            /////////////////////// End of verified email logic for work

        }
    ]
);
*/    
}