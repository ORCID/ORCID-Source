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
    selector: 'works-merge-choose-preferred-version-ng2',
    template:  scriptTmpl("works-merge-choose-preferred-version-ng2-template")
})
export class WorksMergeChoosePreferredVersionComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    mergeCount: any;
    mergeSubmit: boolean;
    worksToMerge: Array<any>;
    delCountVerify: number;
    preferredNotSelected: boolean;
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
        this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMergeChoosePreferredVersion'});
        this.worksService.notifyOther({action:'cancel', successful:true});
    };

    merge(): void {
        var putCodesAsString = '';
        var preferredPutCode;        
        for (var i in this.worksToMerge) {
            var workToMerge = this.worksToMerge[i];
            if (workToMerge.preferred) {
                preferredPutCode = workToMerge.work.putCode.value;
            } else {
                putCodesAsString += ',' + workToMerge.work.putCode.value;
            }
        }
        if (!preferredPutCode) {
            this.preferredNotSelected = true;
        } else {
            this.preferredNotSelected = false;
            putCodesAsString = preferredPutCode + putCodesAsString;
            this.worksService.mergeWorks(putCodesAsString)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.worksService.notifyOther({action:'merge', successful:true});
                    this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMergeChoosePreferredVersion'});
                },
                error => {
                    console.log('error calling mergeWorks', error);
                } 
            );
        }
    };
        
    selectPreferred(preference): void {
        for (var i in this.worksToMerge) {
            var workToMerge = this.worksToMerge[i];
            if (workToMerge.work.putCode != preference.work.putCode) {
                workToMerge.preferred = false;
            }
        }
        preference.preferred = true;   
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {
                if( res.mergeCount ) {
                    this.mergeCount = res.mergeCount;
                }
                if( res.worksToMerge ) {
                    this.worksToMerge = res.worksToMerge;
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
