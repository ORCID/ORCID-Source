declare var $: any;

//Import all the angular components
import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription, of } 
    from 'rxjs';

import { takeUntil, switchMap } 
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
    showWorksMergeError: boolean;
    worksToMerge: Array<any>;
    externalIdsPresent: boolean;
    groupingSuggestion: any;
    checkboxFlag = {}
    selectAll = true
    readyToMerge = true

    constructor(
        private worksService: WorksService,
        private modalService: ModalService
    ) {
        this.mergeSubmit = false;
        this.groupingSuggestion = false;
        this.showWorksMergeError = false;
    }

    cancelEdit(): void {
            this.mergeSubmit = false;
            this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMerge'});
    };

    mergeConfirm(): void {
        if(this.worksToMerge.length > 20){
            this.worksService.notifyOther ({checkboxFlag: this.checkboxFlag}) 
            this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMerge'});   
            this.modalService.notifyOther({groupingSuggestion:this.groupingSuggestion});   
            this.modalService.notifyOther({action:'open', moduleId: 'modalWorksMergeWarning'});   
        }   else {
            this.merge();
        }
    };

    fieldsChange(event) {
        let allSelected = true 
        for (let putcode of Object.keys(this.checkboxFlag)) {
            if (!this.checkboxFlag[putcode]) {
                allSelected = false
            }
        }
        this.selectAll = allSelected
    }

    fieldChangeSelectAll (event) {
        for (let putcode of Object.keys(this.checkboxFlag)) {
            this.checkboxFlag[putcode] = this.selectAll
        }
    }

    atLeastTwoWorksSelectForMerge() {
        let  count = 0
        for (let putcode of Object.keys(this.checkboxFlag)) {
            if (this.checkboxFlag[putcode]) {
                count ++
            }
        }
        return count > 1 
    }

    merge(): void {
        var putCodesAsString = '';      
        for (let putcode of Object.keys(this.checkboxFlag)) {
            if (this.checkboxFlag[putcode] || !this.groupingSuggestion) {
                if (putCodesAsString != '') {
                    putCodesAsString += ',';
                }
                putCodesAsString += putcode;
            }
        }
        this.worksService.mergeWorks(putCodesAsString)
        .pipe(    
            takeUntil(this.ngUnsubscribe),
            switchMap (() => this.rejectSuggestion()) 
        )
        .subscribe(
            () => {
                this.worksService.notifyOther({action:'merge', successful:true,groupingSuggestion:this.groupingSuggestion});
                this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMerge'});
            },
            error => {
                this.showWorksMergeError = true;
                console.log('error calling mergeWorks', error);
            } 
        );
    };

    rejectSuggestion() {
            return this.worksService.markSuggestionRejected(this.groupingSuggestion.suggestions[0])
    }

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.worksService.notifyObservable$.pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(
            (res) => {
                if( res.mergeCount ) {
                    this.mergeCount = res.mergeCount;
                }
                if( res.worksToMerge ) {
                    this.worksToMerge = res.worksToMerge;
                    this.checkboxFlag = {}
                    this.worksToMerge.forEach(work => {
                        this.checkboxFlag[work.putCode.value] = true
                    })
                    this.fieldsChange(null)
                }
                if( res.externalIdsPresent != undefined ) {
                    this.externalIdsPresent = res.externalIdsPresent;
                }
                if( res.groupingSuggestion  != null) {
                    this.groupingSuggestion = res.groupingSuggestion
                }
            }
        )
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
    }; 
}
