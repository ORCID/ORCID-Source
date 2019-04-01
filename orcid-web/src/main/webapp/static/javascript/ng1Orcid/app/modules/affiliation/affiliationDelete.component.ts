declare var $: any;
declare var ActSortState: any;
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

import { AffiliationService } 
    from '../../shared/affiliation.service';

import { EmailService } 
    from '../../shared/email.service';

import { ModalService } 
    from '../../shared/modal.service'; 

import { WorkspaceService } 
    from '../../shared/workspace.service'; 

import { FeaturesService }
    from '../../shared/features.service' 
    
import { CommonService } 
    from '../../shared/common.service';

@Component({
    selector: 'affiliation-delete-ng2',
    template:  scriptTmpl("affiliation-delete-ng2-template")
})
export class AffiliationDeleteComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    deleteAffiliationObj: any;
    constructor(
        private affiliationService: AffiliationService,
        private modalService: ModalService
    ) {

        this.deleteAffiliationObj = {
            affiliationName: {
                value: null
            }
        };

    }

    cancelEdit(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalAffiliationDelete'});
        this.affiliationService.notifyOther({action:'cancel', successful:true});
    };


    deleteAffiliation(): void {        
        this.affiliationService.deleteAffiliation(this.deleteAffiliationObj)
            .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
            .subscribe(data => {       
                
                if(data.errors.length == 0) {
                    this.affiliationService.notifyOther({action: 'delete', successful:true});                  
                }
                this.modalService.notifyOther({action:'close', moduleId: 'modalAffiliationDelete'});
            });         
        
    };
 
    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.affiliationService.notifyObservable$.subscribe(
            (res) => {
                if( res.affiliation != undefined ) {
                    this.deleteAffiliationObj = res.affiliation;
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
