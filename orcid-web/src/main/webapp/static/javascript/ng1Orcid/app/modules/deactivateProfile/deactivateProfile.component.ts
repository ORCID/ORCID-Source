//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import 'rxjs/add/operator/takeUntil';

import { AdminDelegatesService } 
    from '../../shared/adminDelegates.service.ts'; 


@Component({
    selector: 'deactivate-profile-ng2',
    template:  scriptTmpl("deactivate-profile-ng2-template")
})
export class DeactivateProfileComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
   
    orcidsToDeactivate: string;
    result: any;
    showSection: boolean;

    constructor(
        private adminDelegatesService: AdminDelegatesService
    ) {
        this.orcidsToDeactivate = "";
        this.result = {};
        this.showSection = false;
    }

    confirmDelegatesProcess(): void {
        this.adminDelegatesService.setFormData( this.orcidsToDeactivate )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.result = data;
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

    toggleSection(): void{
        this.showSection = !this.showSection;
        $('#deactivation_modal').toggle();
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