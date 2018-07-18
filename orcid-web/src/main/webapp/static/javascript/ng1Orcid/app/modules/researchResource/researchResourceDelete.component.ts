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

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'research-resource-delete-ng2',
    template:  scriptTmpl("research-resource-delete-ng2-template")
})
export class ResearchResourceDeleteComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    researchResource: any;

    constructor(
        private researchResourceService: ResearchResourceService,
        private modalService: ModalService
    ) {


    }

    cancelEdit(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalResearchResourceDelete'});
        this.researchResourceService.notifyOther({action:'cancel', successful:true});
    };

    deleteResearchResource(putCode): void {
        this.researchResourceService.deleteResearchResources([putCode])
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.modalService.notifyOther({action:'close', moduleId: 'modalResearchResourceDelete'});
                this.researchResourceService.notifyOther({action:'delete', successful:true});
            },
            error => {
                console.log('Error deleting work', error);
            } 
        ); 
    }

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.researchResourceService.notifyObservable$.subscribe(
            (res) => {
                if( res.researchResource ) {
                    this.researchResource = res.researchResource;
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
