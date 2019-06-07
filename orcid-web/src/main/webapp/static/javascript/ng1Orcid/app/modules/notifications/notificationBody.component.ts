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
    from '../../shared/common.service';

import { NotificationsService } 
    from '../../shared/notifications.service';

@Component({
    selector: 'notification-body-ng2',
    template:  scriptTmpl("notification-body-ng2-template"),
})
export class NotificationBodyComponent implements OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
    
    @Input() notification: any;

    educationsCount: number;
    employmentsCount: number;
    encodedUrl: string;
    fundingsCount: number;
    peerReviewsCount: number;
    worksCount: number;
    educationsList: string;
    employmentsList: string;
    fundingsList: string;
    peerReviewsList: string;
    worksList: string;
    addedWorksList: string;
    updatedWorksList: string;
    deletedWorksList: string;
    
    constructor(
        private commonService: CommonService,
        private notificationsService: NotificationsService,
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
        this.addedWorksList = "";
        this.updatedWorksList = "";
        this.deletedWorksList = "";
    }

    archive(putCode): void {
        this.notificationsService.notifyOther({action:'archive', putCode:putCode});
    };

    ngOnInit() {
        if(this.notification.items){
            for (let activity of this.notification.items.items){
                if(activity.itemType == "WORK"){
                    this.worksCount++;
                    var workNameHtml = "<strong>" + activity.itemName + "</strong>";
                    this.worksList =  this.worksList + workNameHtml;
                    if(activity.externalIdentifier){
                        this.worksList = this.worksList + " (" + activity.externalIdentifier.type + ": " + activity.externalIdentifier.value + ")";
                    }
                    this.worksList += "<br/>";
                    
                    if(activity.type == "CREATE") {
                        this.addedWorksList = this.addedWorksList + workNameHtml + "<br />";
                    } else if(activity.type == "UPDATE") {
                        this.updatedWorksList = this.updatedWorksList + workNameHtml + "<br />";
                    } else if(activity.type == "DELETE") {
                        this.deletedWorksList = this.deletedWorksList + workNameHtml + "<br />";
                    }
                }
                if(activity.itemType == "EMPLOYMENT"){
                    this.employmentsCount++;
                    this.employmentsList =  this.employmentsList + "<strong>" + activity.itemName + "</strong><br/>";
                }
                if(activity.itemType == "EDUCATION"){
                    this.educationsCount++;
                    this.educationsList =  this.educationsList + "<strong>" + activity.itemName + "</strong><br/>";
                }
                if(activity.itemType == "FUNDING"){
                    this.fundingsCount++;
                    this.fundingsList = this.fundingsList + "<strong>" + activity.itemName + "</strong>";
                    if(activity.externalIdentifier){
                        this.fundingsList = this.fundingsList + " (" + activity.externalIdentifier.type + ": " + activity.externalIdentifier.value + ")";
                    }
                    this.fundingsList += "<br/>";
                }
                if(activity.itemType == "PEER_REVIEW"){
                    this.peerReviewsCount++;
                    this.peerReviewsList = this.peerReviewsList + "<strong>" + activity.itemName + "</strong>";
                    if(activity.externalIdentifier){
                        this.peerReviewsList = this.peerReviewsList + " (" + activity.externalIdentifier.type + ": " + activity.externalIdentifier.value + ")";
                    }
                    this.peerReviewsList += "<br/>";
                }
            }
        }
        if (this.notification.authorizationUrl){
            this.encodedUrl = encodeURIComponent(this.notification.authorizationUrl.uri);
        }        
    }; 
    
    getBaseUri() : String {
        return getBaseUri();
    };
}