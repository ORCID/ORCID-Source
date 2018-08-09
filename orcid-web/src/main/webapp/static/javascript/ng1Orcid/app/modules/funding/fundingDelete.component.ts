declare var $: any;
declare var ActSortState: any;
declare var GroupedActivities: any;
declare var groupedActivitiesUtil: any;
declare var sortState: any;
declare var typeahead: any;

//Import all the angular components
import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { FundingService } 
    from '../../shared/funding.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

import { FeaturesService }
    from '../../shared/features.service.ts' 
    
import { CommonService } 
    from '../../shared/common.service.ts';

@Component({
    selector: 'funding-delete-ng2',
    template:  scriptTmpl("funding-delete-ng2-template")
})
export class FundingDeleteComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    deleteObj: any;
    constructor(
        private fundingService: FundingService,
        private modalService: ModalService
    ) {

        this.deleteObj = {
            fundingTitle: {
                title: {
                    value: null
                }
            }
        };

    }

    cancelEdit(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalFundingDelete'});
        this.modalService.notifyOther({action:'cancel', successful:true});
    };


    deleteFunding(): void {        
        this.fundingService.deleteFunding(this.deleteObj)
            .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
            .subscribe(data => {       
                
                if(data.errors.length == 0) {
                    this.modalService.notifyOther({action: 'delete', successful:true});                  
                }
                this.modalService.notifyOther({action:'close', moduleId: 'modalFundingDelete'});
            });         
        
    };
 
    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.fundingService.notifyObservable$.subscribe(
            (res) => {
                if( res.funding != undefined ) {
                    this.deleteObj = res.funding;
                }
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
    }; 
}
