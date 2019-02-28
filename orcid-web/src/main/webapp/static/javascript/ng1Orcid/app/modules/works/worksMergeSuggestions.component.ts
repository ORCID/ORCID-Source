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
    selector: 'works-merge-suggestions-version-ng2',
    template:  scriptTmpl("works-merge-suggestions-ng2-template")
})
export class WorksMergeSuggestionsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    worksToMerge: Array<any>;
    suggestionId: any;

    constructor(
        private worksService: WorksService,
        private modalService: ModalService
    ) {

    }

    cancel(): void {
        this.worksService.markSuggestionRejected(this.suggestionId)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(
            data => {
                this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMergeSuggestions'});
                this.worksService.notifyOther({action:'cancel', successful:true});
            },
            error => {
                console.log('error marking suggestion as rejected', error);
            } 
        );
    };

    accept(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalWorksMergeSuggestions'});
        this.modalService.notifyOther({action:'open', moduleId: 'modalWorksMergeChoosePreferredVersion'});
    };
        
    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {
                if( res.worksToMerge ) {
                    this.worksToMerge = res.worksToMerge;
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
