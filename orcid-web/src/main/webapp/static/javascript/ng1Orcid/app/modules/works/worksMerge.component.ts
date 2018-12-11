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
    worksToMerge: Array<any>;
    delCountVerify: number;
    externalIdsPresent: boolean;
    suggestionId: any;

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
    };

    merge(): void {
        var putCodesAsString = '';       
        for (var i in this.worksToMerge) {
            var workToMerge = this.worksToMerge[i];
            putCodesAsString += workToMerge.putCode.value;
            if(Number(i) < (this.worksToMerge.length-1)){
                putCodesAsString += ',';
            }
        }
        this.worksService.mergeWorks(putCodesAsString)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (this.suggestionId) {
                    this.worksService.markSuggestionAccepted(this.suggestionId)
                    .pipe(    
                        takeUntil(this.ngUnsubscribe)
                    )
                    .subscribe(
                        data => {
                            this.worksService.notifyOther({action:'merge', successful:true});
                            this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMerge'});
                        },
                        error => {
                            console.log('error marking suggestion as accepted', error);
                        } 
                    );
                } else {
                    this.worksService.notifyOther({action:'merge', successful:true});
                    this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMerge'});
                }
            },
            error => {
                console.log('error calling mergeWorks', error);
            } 
        );
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {
                console.log(res)
                if( res.mergeCount ) {
                    this.mergeCount = res.mergeCount;
                }
                if( res.worksToMerge ) {
                    this.worksToMerge = res.worksToMerge;
                }
                if( res.externalIdsPresent != undefined ) {
                    this.externalIdsPresent = res.externalIdsPresent;
                }
                if( res.suggestionId ) {
                    this.suggestionId = res.suggestionId;
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
