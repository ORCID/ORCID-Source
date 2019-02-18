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
    selector: 'works-merge-warning-ng2',
    template:  scriptTmpl("works-merge-warning-ng2-template")
})
export class WorksMergeWarningComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

 
    mergeSubmit: boolean;
    showWorksMergeError: boolean;
    checkboxFlag
    groupingSuggestion

    constructor(
        private worksService: WorksService,
        private modalService: ModalService
    ) {
        this.mergeSubmit = false;
        this.showWorksMergeError = false;
    }

    cancelEdit(): void {
        this.mergeSubmit = false;
        this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMergeWarning'});
        if (this.groupingSuggestion){
            this.worksService.notifyOther({action:'cancel', successful:true});
        }
    };

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
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.worksService.notifyOther({action:'merge', successful:true, groupingSuggestion: this.groupingSuggestion });
                this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMergeWarning'});
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
        this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {
                if ( res.checkboxFlag) {
                    this.checkboxFlag = res.checkboxFlag
                }
                if (res.groupingSuggestion) {
                    this.groupingSuggestion = res.groupingSuggestion
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
