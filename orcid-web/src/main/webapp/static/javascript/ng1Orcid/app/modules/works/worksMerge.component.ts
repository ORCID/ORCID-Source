declare var $: any;

//Import all the angular components
import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription, of, forkJoin } 
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
    checkboxFlag = []
    selectAll = true
    readyToMerge = true
    orcid

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
            if (this.groupingSuggestion){
                this.worksService.notifyOther({action:'cancel', successful:true});
            }
    };

    mergeConfirm(): void {
        if(this.worksToMerge.length > 20){
            this.worksService.notifyOther({worksToMerge:this.worksToMerge});       
            this.worksService.notifyOther({mergeCount:this.mergeCount});
            this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMerge'});  
            this.modalService.notifyOther({action:'open', moduleId: 'modalWorksMergeWarning'});   
        }   else {
            this.merge();
        }
    };

    fieldsChange(event) {
        let allSelected = true 
        this.checkboxFlag.forEach(element => {
            if (!element.state) {
                allSelected = false
            }
        }); 
        this.selectAll = allSelected
    }

    fieldChangeSelectAll (event) {
        this.checkboxFlag.forEach(element => {
            element.state = this.selectAll
        }); 
    }

    atLeastOneWorksSelectForMerge() {
        let  count = 0
        this.checkboxFlag.forEach(element => {
            if (element.state) {
                count ++
            }
        }); 
        return count > 0 
    }

    workListToPutCodeString(element) {
        var putCodesAsString = '';      
        element.forEach(element => {
            if ( putCodesAsString != ''){
                putCodesAsString += ',';
            }
            putCodesAsString += element.putCode.value;
        });
        return putCodesAsString
    }

    workListToPutCodeList(element) {
        var putCodesAsString = []      
        element.forEach(element => {
            putCodesAsString.push(element.putCode.value);
        });
        return putCodesAsString
    }



    merge(): void {
        const mergeCall = []

        if (this.worksToMerge) {
            const list = this.workListToPutCodeString(this.worksToMerge)
            mergeCall.push (this.worksService.mergeWorks(list))
        }
        else if (this.groupingSuggestion) {

            this.checkboxFlag.forEach(element => { 
                if (element.state) {
                    const list = this.workListToPutCodeString(element.groupingSuggestion)
                    mergeCall.push (this.worksService.mergeWorks(list))       
                }
                else {
                    const list = this.workListToPutCodeString(element.groupingSuggestion)
                    mergeCall.push (this.worksService.markSuggestionRejected({putCodes: this.workListToPutCodeList(element.groupingSuggestion), orcid: this.orcid, putCodesAsString: list.toString()}))    
                }
            }); 

        }
        forkJoin (mergeCall)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            () => {
                this.worksService.notifyOther({action:'merge', successful:true, groupingSuggestion:this.groupingSuggestion});
                this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMerge'});
            },
            error => {
                this.showWorksMergeError = true;
                console.log('error calling mergeWorks', error);
            } 
        );
    };

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
                if( res.worksToMerge != null ) {
                    this.worksToMerge = res.worksToMerge;
                }
                if( res.externalIdsPresent != undefined ) {
                    this.externalIdsPresent = res.externalIdsPresent;
                }
                if( res.groupingSuggestion  != null) {
                    this.groupingSuggestion = res.groupingSuggestion
                    this.checkboxFlag = []
                    if (res.groupingSuggestion) {
                        
                        this.groupingSuggestion.forEach((groupingSuggestion, i)=> {
                            this.checkboxFlag.push ( {id: [i], groupingSuggestion: groupingSuggestion, state: true})
                        })
                        this.fieldsChange(null)
                    } 
                }
                if( res.orcid != undefined ) {
                    this.orcid = res.orcid;
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
