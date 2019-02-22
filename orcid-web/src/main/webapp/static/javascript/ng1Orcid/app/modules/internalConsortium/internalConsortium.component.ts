declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var om: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { ManageMembersService } 
    from '../../shared/manageMembers.service.ts'; 

import { PreferencesService } 
    from '../../shared/preferences.service.ts'; 


@Component({
    selector: 'institutional2-f-a-ng2',
    template:  scriptTmpl("institutional2-f-a-ng2-template")
})
export class InternalConsotiumComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    consortium: any;
    findConsortiumError: any;
    showFindModal: any;
    success_edit_member_message: any;
    salesForceId: any;

    constructor(
        private manageMembersService: ManageMembersService,
        private prefsSrvc: PreferencesService
    ) {
        this.consortium = null;
        this.findConsortiumError = false;
        this.showFindModal = false;
        this.success_edit_member_message = "";
        this.salesForceId = "";
    }

    closeModal(): void {
        //$.colorbox.close();
    }; 

    confirmUpdateConsortium(): void {
        /*
        $.colorbox({
            html : $compile($('#confirm-modal-consortium').html())($scope),
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"450px" , height:"175px"});
        */
    };

    findConsortium(): void {
        this.findConsortiumError = false;
        this.success_edit_member_message = null;
        this.consortium = null;

        this.manageMembersService.findConsortium( encodeURIComponent(this.salesForceId) )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.consortium = data;
            },
            error => {
                this.findConsortiumError = true;
                //console.log('getAlsoKnownAsFormError', error);
            } 
        );
    };

    toggleFindConsortiumModal(): void {
        this.showFindModal = !this.showFindModal;
    };
    
    updateConsortium(): void {
        this.manageMembersService.updateConsortium( encodeURIComponent(this.consortium) )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.errors.length == 0){
                    this.success_edit_member_message = om.get('manage_member.edit_member.success');
                }
                this.consortium = data;
                this.closeModal();
            },
            error => {
                this.findConsortiumError = true;
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
    }; 
}