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

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { AffiliationService } 
    from '../../shared/affiliation.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { GroupedActivitiesUtilService } 
    from '../../shared/groupedActivities.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

import { FeaturesService }
    from '../../shared/features.service.ts' 
    
import { CommonService } 
    from '../../shared/common.service.ts';

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

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalAffiliationDelete'});
    };


    deleteAffiliation(): void {        
        this.affiliationService.deleteAffiliation(this.deleteAffiliationObj)
            .takeUntil(this.ngUnsubscribe)
            .subscribe(data => {       
                
                if(data.errors.length == 0) {
                    this.affiliationService.notifyOther({action: 'delete', deleteAffiliationObj: this.deleteAffiliationObj});                  
                }

                this.closeModal();
                                            
            });         
        
    };
 
    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.affiliationService.notifyObservable$.subscribe(
            (res) => {
                if( res.affiliation != undefined ) {
                    this.deleteAffiliationObj = res.affiliation;
                    console.log('res.affiliation',this.deleteAffiliationObj);
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
