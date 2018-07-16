declare var $: any;
declare var ActSortState: any;
declare var GroupedActivities: any;
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

import { ResearchResourceService } 
    from '../../shared/researchResource.service.ts';

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
    selector: 'research-resource-delete-ng2',
    template:  scriptTmpl("esearch-resource-delete-ng2-template")
})
export class ResearchResourceDeleteComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    deleteResearchResourceObj: any;
    constructor(
        private researchResourceService: ResearchResourceService,
        private modalService: ModalService
    ) {

        this.deleteResearchResourceObj = {
            affiliationName: {
                value: null
            }
        };

    }

    cancelEdit(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalResearchResourceDelete'});
        this.researchResourceService.notifyOther({action:'cancel', successful:true});
    };


    deleteResearchResource(): void {        
        this.researchResourceService.deleteResearchResources(this.deleteResearchResourceObj.putCode)
            .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
            .subscribe(data => {       
                
                if(data.errors.length == 0) {
                    this.researchResourceService.notifyOther({action: 'delete', successful:true});                  
                }

                this.modalService.notifyOther({action:'close', moduleId: 'modalResearchResourceDelete'});
                                            
            });         
        
    };
 
    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.researchResourceService.notifyObservable$.subscribe(
            (res) => {
                if( res.researchResource != undefined ) {
                    this.deleteResearchResourceObj = res.researchResource;
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
