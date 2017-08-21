//Declare all the external variables 
declare var $: any;
declare var colorbox: any;
declare var contains: any;
declare var getBaseUri: any;
declare var logAjaxError: any;
declare var orcidVar: any;
declare var scriptTmpl: any;

//Import all the angular components
import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common'; 
import { AfterViewInit, Component, Directive, Inject, Injector, Input, ViewChild, ElementRef } from '@angular/core';
import { NgModule } from '@angular/core';
import { Observable } from 'rxjs/Rx';

import { BiographyService } from '../../shared/biographyService.ts'; 

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
        console.log('BiographyComponent loaded v.0.4'); 
        //biographyService.getBiographyData();

        this.biographyForm = null;
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
        if ( this.biographyForm != null ){
            if ( this.biographyForm.biography != null ){
                if ( this.biographyForm.biography.value != null ){    
                    if ( this.biographyForm.biography.value.length > 5000 ) {
                        this.lengthError = true;
                    } else {
                        this.lengthError = false;
                    }
                }
            }
        }
        console.log('this.lengthError', this.lengthError);
        return this.lengthError;
    };

    close(): void {
        this.showEdit = false;
    };

    getBiographyForm(): void{
        this.biographyService.getBiographyData().subscribe(
            data => {
                this.biographyForm  = data;
                console.log('data returned', data);
            },
            error => console.log(error)
        );

        /* Moved to service
        $.ajax({
            url: getBaseUri() + '/account/biographyForm.json',
            dataType: 'json',
            success: function(data) {
                this.biographyForm = data;
                console.log('this.biographyForm', this.biographyForm);
            }
        }).fail(function(e){
            // something bad is happening!
            console.log("error fetching BiographyForm");
            logAjaxError(e);
        });
        */
    };

    hideTooltip(tp): void{
        this.showElement[tp] = false;
    };

    setBiographyForm(): any{
        if( this.checkLength() ){    
            return; // do nothing if there is a length error
        } 
        $.ajax({
            contentType: 'application/json;charset=UTF-8',
            data:  angular.toJson(this.biographyForm),
            dataType: 'json',
            type: 'POST',
            url: getBaseUri() + '/account/biographyForm.json',
            success: function(data) {
                this.biographyForm = data;
                if(data.errors.length == 0){
                    this.close();
                }
            }
        }).fail(function() {
            // something bad is happening!
            console.log("BiographyCtrl.serverValidate() error");
        });
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