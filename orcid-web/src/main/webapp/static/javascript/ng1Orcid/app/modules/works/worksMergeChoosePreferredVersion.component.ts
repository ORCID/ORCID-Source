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
        console.log("Merging:");
        for (var i in this.worksToMerge) {
            var workToMerge = this.worksToMerge[i];
            console.log(workToMerge.work.title.value + ':' + workToMerge.preferred);
            
        }
        this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMergeChoosePreferredVersion'});
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

    mergeGroupWorks(putCodes): void {
        var rmWorks = [];
        var rmGroups = [];
        for (var i in putCodes) {
            for (var j in this.worksService.groups) {
                for (var k in this.worksService.groups[j].works) {
                    if (this.worksService.groups[j].works[k].putCode.value == putCodes[i]) {
                        rmGroups.push(j);
                        for (var y in this.worksService.groups[j].works){
                            rmWorks.push(this.worksService.groups[j].works[y].putCode.value);
                        }
                        break;
                    }
                }
            }
        }
        while (rmGroups.length > 0) {
            this.worksService.groups.splice(rmGroups.pop(),1);
        }
        this.worksService.removeWorks(rmWorks)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (rmWorks.length > 0) {
                    this.worksService.removeWorks(rmWorks);
                }
                this.delCountVerify = 0;
                this.mergeSubmit = false;
                this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMerge'});
                this.worksService.notifyOther({action:'merge', successful:true});
            },
            error => {
                console.log('Error deleting work', error);
            } 
        ); 
    }

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
