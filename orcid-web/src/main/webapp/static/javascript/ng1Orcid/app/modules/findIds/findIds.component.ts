declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { AdminDelegatesService } 
    from '../../shared/adminDelegates.service.ts'; 


@Component({
    selector: 'find-ids-ng2',
    template:  scriptTmpl("find-ids-ng2-template")
})
export class FindIdsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    
    emailIdsMap: any;
    emails: any;
    showSection: any;
    profileList: any;

    constructor(
        private adminDelegatesService: AdminDelegatesService
    ) {
        this.emailIdsMap = {};
        this.emails = "";
        this.showSection = false;
        this.profileList = {};
    }

    closeModal(): void {
        $.colorbox.close();
    };

    findIds(): void {
        this.adminDelegatesService.findIds( this.emails )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data) {
                    this.profileList = data;
                } else {
                    this.profileList = null;
                }
                this.emails='';

                ////console.log('this.getForm', this.formData);
            },
            error => {
                //console.log('getAlsoKnownAsFormError', error);
            } 
        );
    };
    
    toggleSection(): void {
        this.showSection = !this.showSection;
        $('#find_ids_section').toggle();
    };
    
    /*
    
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
    }; 
}