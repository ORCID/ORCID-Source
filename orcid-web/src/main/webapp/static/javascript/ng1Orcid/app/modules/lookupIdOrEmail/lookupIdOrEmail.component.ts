declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import 'rxjs/add/operator/takeUntil';

import { AdminDelegatesService } 
    from '../../shared/adminDelegates.service.ts'; 


@Component({
    selector: 'look-up-id-or-email-ng2',
    template:  scriptTmpl("look-up-id-or-email-ng2-template")
})
export class LookUpIdOrEmailComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    emailIdsMap: any;
    idOrEmails: any;
    showSection: any;
   
    constructor(
        private adminDelegatesService: AdminDelegatesService
    ) {
        this.emailIdsMap = {};
        this.idOrEmails = "";
        this.showSection = false;
    }

    closeModal() {
        //$.colorbox.close();
    };

    lookupIdOrEmails = function() {
        this.adminDelegatesService.lookupIdOrEmails( this.idOrEmails )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.result = data;
                this.idOrEmails='';
                this.showEmailIdsModal();
            },
            error => {
                //console.log('getformDataError', error);
            } 
        );
    };

    showEmailIdsModal(): void {
        /*
        $.colorbox({
            html : $compile($('#lookup-email-ids-modal').html())($scope),
            scrolling: true,
            onLoad: function() {
                $('#cboxClose').remove();
            }
        });

        setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
        */
    };

    toggleSection(): void {
        this.showSection = !this.showSection;
        $('#lookup_ids_section').toggle();
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