//Import all the angular components

import { NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, ElementRef, Input, OnInit} 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { catchError, map, tap } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service.ts';

@Component({
    selector: 'notification-add-activities-ng2',
    template:  scriptTmpl("notification-add-activities-ng2-template"),
})
export class NotificationAddActivitiesComponent implements OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
    
    @Input() notification: any;

    educationsCount: number;
    employmentsCount: number;
    fundingsCount: number;
    peerReviewsCount: number;
    worksCount: number;
    educationsList: string;
    employmentsList: string;
    fundingsList: string;
    peerReviewsList: string;
    worksList: string;

    constructor(
        private commonService: CommonService,
        private elementRef: ElementRef,
    ) {
        this.notification = elementRef.nativeElement.getAttribute('notification');
        this.educationsCount = 0;
        this.employmentsCount = 0;
        this.fundingsCount = 0;
        this.peerReviewsCount = 0;
        this.worksCount = 0;
        this.educationsList = "";
        this.employmentsList = "";
        this.fundingsList = "";
        this.peerReviewsList = "";
        this.worksList = "";
    }

    ngOnInit() {
        for (let activity of this.notification.items.items){
            if(activity.itemType == "WORK"){
                this.worksCount++;
                this.worksList += activity.itemName;
                this.worksList += "<br/>";
                if(activity.externalId){
                    this.worksList = this.worksList + "(" + activity.externalId.externalIdType + ":" + activity.externalId.externalIdValue + ")";
                }
            }
            if(activity.itemType == "EMPLOYMENT"){
                this.employmentsCount++;
                this.employmentsList =  this.employmentsList + activity.itemName + "<br/>";
            }
            if(activity.itemType == "EDUCATION"){
                this.educationsCount++;
                this.educationsList =  this.educationsList + activity.itemName + "<br/>";
            }
            if(activity.itemType == "FUNDING"){
                this.fundingsCount++;
                this.fundingsList += activity.itemName;
                this.fundingsList += "<br/>";
                if(activity.externalId){
                    this.fundingsList = this.fundingsList + "(" + activity.externalId.externalIdType + ":" + activity.externalId.externalIdValue + ")";
                }
            }
            if(activity.itemType == "PEER_REVIEW"){
                this.peerReviewsCount++;
                this.peerReviewsList += activity.itemName;
                this.peerReviewsList += "<br/>";
                if(activity.externalId){
                    this.peerReviewsList = this.peerReviewsList + "(" + activity.externalId.externalIdType + ":" + activity.externalId.externalIdValue + ")";
                }
            }
        }
        
    }; 
}