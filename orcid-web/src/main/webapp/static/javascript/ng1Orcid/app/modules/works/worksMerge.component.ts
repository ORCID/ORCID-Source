declare var $: any;

//Import all the angular components
import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { WorksService } 
    from '../../shared/works.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'works-merge-ng2',
    template:  scriptTmpl("works-merge-ng2-template")
})
export class WorksMergeComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    mergeCount: any;
    mergeSubmit: boolean;
    bulkEditMap: any;
    delCountVerify: number;
    externalIdsPresent: boolean;

    constructor(
        private worksService: WorksService,
        private modalService: ModalService
    ) {
        this.mergeSubmit = false;
        this.delCountVerify = 0;
    }

    cancelEdit(): void {
        this.delCountVerify = 0;
        this.mergeSubmit = false;
        this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMerge'});
        this.worksService.notifyOther({action:'cancel', successful:true});
    };

    mergeContinue(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMerge'});
        this.modalService.notifyOther({action:'open', moduleId: 'modalWorksMergeChoosePreferredVersion'});
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {
                if( res.mergeCount ) {
                    this.mergeCount = res.mergeCount;
                }
                if( res.bulkEditMap ) {
                    this.bulkEditMap = res.bulkEditMap;
                }
                if( res.externalIdsPresent ) {
                    this.externalIdsPresent = res.externalIdsPresent;
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
