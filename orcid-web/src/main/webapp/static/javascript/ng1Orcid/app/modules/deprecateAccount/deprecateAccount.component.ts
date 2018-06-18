//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { GenericService } 
    from '../../shared/generic.service.ts'; 

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'deprecate-account-ng2',
    template:  scriptTmpl("deprecate-account-ng2-template")
})
export class DeprecateAccountComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    deprecateProfilePojo: any;
    url_path: string;

    constructor(
        private deprecateProfileService: GenericService,
        private modalService: ModalService
    ) {
        this.deprecateProfilePojo = {};
        this.url_path = '/account/deprecate-profile.json';
    }

    closeEditModal(): void{
        this.modalService.notifyOther({action:'close', moduleId: 'modalDeprecateAccountForm'});
    };

    getDeprecateProfile(): void {

        this.deprecateProfileService.getData( this.url_path )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data) {
                    this.deprecateProfilePojo = data;
                }
            },
            error => {
                //console.log('getformDataError', error);
            } 
        );
    };

    submitModal(): void {
        this.deprecateProfileService.setData( this.deprecateProfilePojo, this.url_path )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data) {
                    /*
                        emailSrvc.getEmails(function(emailData) {
                        $rootScope.$broadcast('rebuildEmails', emailData);
                    });
                    $.colorbox({
                        html : $compile($('#deprecate-account-confirmation-modal').html())($scope),
                        escKey:false,
                        overlayClose:true,
                        close: '',
                        onClosed: function(){ $scope.deprecateProfilePojo = null; $scope.$apply(); },
                        });
                    $scope.$apply();
                    $.colorbox.resize();
                    */
                }
            },
            error => {
                //console.log('getformDataError', error);
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
        this.getDeprecateProfile();
    }; 
}
