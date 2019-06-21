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
    
import { FeaturesService }
    from '../../shared/features.service';    

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
    elementsModifiedCount: number;
    educationsList: string;
    employmentsList: string;
    fundingsList: string;
    peerReviewsList: string;
    worksList: string;
    addedList: string[];
    updatedList: string[];
    deletedList: string[];
    unknownList: string[];
    TOGGLZ_VERBOSE_NOTIFICATIONS: boolean;
    MAX_ELEMENTS_TO_SHOW: number;
    
    constructor(
        private commonService: CommonService,
        private notificationsService: NotificationsService,
        private elementRef: ElementRef,
        private featuresService: FeaturesService,
    ) {
        this.notification = elementRef.nativeElement.getAttribute('notification');
        this.TOGGLZ_VERBOSE_NOTIFICATIONS = this.featuresService.isFeatureEnabled('VERBOSE_NOTIFICATIONS');
        this.MAX_ELEMENTS_TO_SHOW = 20;
        this.educationsCount = 0;
        this.employmentsCount = 0;
        this.fundingsCount = 0;
        this.peerReviewsCount = 0;
        this.elementsModifiedCount = 0;
        this.educationsList = "";
        this.employmentsList = "";
        this.fundingsList = "";
        this.peerReviewsList = "";
        this.worksList = "";
        this.addedList = [];
        this.updatedList = [];
        this.deletedList = [];
        this.unknownList = [];
    }

    archive(putCode): void {
        this.notificationsService.notifyOther({action:'archive', putCode:putCode});
    };

    ngOnInit() {        
        if(this.notification.items) {
            var affiliationTypes = ["DISTINCTION","EDUCATION","EMPLOYMENT","INVITED_POSITION","MEMBERSHIP","QUALIFICATION","SERVICE"];        
            for (let activity of this.notification.items.items) {
                console.log(activity.itemType + ' - ' + this.elementsModifiedCount)
                console.log(activity.additionalInfo)
                console.log(activity.additionalInfo['org_name'])
                
                var elementDescription = '';
                if(activity.itemType == "WORK" || activity.itemType == "FUNDING") {
                    elementDescription = activity.itemName;
                } else if(affiliationTypes.indexOf(activity.itemType) > 0) {
                    elementDescription = activity.itemName + ' (' + activity.additionalInfo['org_name'] + ')';
                } else if(activity.itemType == "PEER_REVIEW") {
                    elementDescription = activity.itemName;
                    if(activity.additionalInfo['group_name'] != undefined) {
                        elementDescription = elementDescription + ' (' + activity.additionalInfo['group_name'] + ')';
                    } else if(activity.additionalInfo['subject_container_name'] != undefined) {
                        elementDescription = elementDescription + ' (' + activity.additionalInfo['subject_container_name'] + ')';
                    }
                } else if(activity.itemType == "RESEARCH_RESOURCE") {
                    elementDescription = activity.proposal.title.title.content;
                } 
                
                if(this.elementsModifiedCount < this.MAX_ELEMENTS_TO_SHOW) {
                    if(activity.type == "CREATE") {
                        this.addedList.push(elementDescription);
                    } else if(activity.type == "UPDATE") {
                        this.updatedList.push(elementDescription);
                    } else if(activity.type == "DELETE") {
                        this.deletedList.push(elementDescription);
                    } else {
                        this.unknownList.push(elementDescription);
                    }
                }
                
                this.elementsModifiedCount++;
                
                if(activity.itemType == "WORK"){
                    this.worksList =  this.worksList + "<strong>" + activity.itemName + "</strong>";
                    if(activity.externalIdentifier){
                        this.worksList = this.worksList + " (" + activity.externalIdentifier.type + ": " + activity.externalIdentifier.value + ")";
                    }
                    this.worksList += "<br/>";                                        
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