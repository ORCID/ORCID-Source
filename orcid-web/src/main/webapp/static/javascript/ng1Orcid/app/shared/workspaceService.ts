import { Injectable } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class WorkspaceService {
    private displayEducation: boolean;
    private displayEmployment: boolean;
    private displayFunding: boolean;
    private displayPersonalInfo: boolean;
    private displayWorks: boolean;
    private displayPeerReview: boolean;

    constructor(){
        this.displayEducation = true;
        this.displayEmployment = true;
        this.displayFunding = true;
        this.displayPersonalInfo = true;
        this.displayWorks = true;
        this.displayPeerReview = true;
    }

    openEducation(): void {
        this.displayEducation = true;
    };

    openEmployment(): void {
        this.displayEmployment = true;
    };

    openFunding(): void {
        this.displayFunding = true;
    };

    openPeerReview(): void {
        this.displayPeerReview = true;
    };

    openPersonalInfo(): void {
        this.displayPersonalInfo = true;
    };

    openWorks(): void {
        this.displayWorks = true;
    };

    toggleEducation(): void {
        this.displayEducation = !this.displayEducation;
    };

    toggleEmployment(): void {
        this.displayEmployment = !this.displayEmployment;
    };

    toggleFunding(): void {
        this.displayFunding = !this.displayFunding;
    };

    togglePeerReview(): void {              
        this.displayPeerReview = !this.displayPeerReview;
    };

    togglePeerReviews(): void {
        this.displayPeerReview = !this.displayPeerReview;
    };

    togglePersonalInfo(): void {
        this.displayPersonalInfo = !this.displayPersonalInfo;
    };

    toggleWorks(): void {
        this.displayWorks = !this.displayWorks;
    };
}