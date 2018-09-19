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

import { PeerReviewService } 
    from '../../shared/peerReview.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'peer-review-delete-ng2',
    template:  scriptTmpl("peer-review-delete-ng2-template")
})
export class PeerReviewDeleteComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    peerReview: any;

    constructor(
        private peerReviewService: PeerReviewService,
        private modalService: ModalService
    ) {


    }

    cancelEdit(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalPeerReviewDelete'});
        this.peerReviewService.notifyOther({action:'cancel', successful:true});
    };

    deletePeerReview(putCode): void {
        console.log(putCode);
        this.peerReviewService.deletePeerReviews([putCode])
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.modalService.notifyOther({action:'close', moduleId: 'modalPeerReviewDelete'});
                this.peerReviewService.notifyOther({action:'delete', successful:true});
            },
            error => {
                console.log('Error deleting work', error);
            } 
        ); 
    }

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.peerReviewService.notifyObservable$.subscribe(
            (res) => {
                if( res.peerReview ) {
                    this.peerReview = res.peerReview;
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
