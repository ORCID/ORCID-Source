import { Injectable } 
    from '@angular/core';

import { HttpClient } 
     from '@angular/common/http';


import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class WorkspaceService {
    public displayEducation: boolean;
    public displayEmployment: boolean;
    public displayFunding: boolean;
    public displayPersonalInfo: boolean;
    public displayWorks: boolean;
    public displayPeerReview: boolean;    
    public displayEducationAndQualification: boolean;
    public displayDistinctionAndInvitedPosition: boolean;
    public displayMembershipAndService: boolean;

    constructor(){
        this.displayEducation = true;
        this.displayEmployment = true;
        this.displayFunding = true;
        this.displayPersonalInfo = true;
        this.displayWorks = true;
        this.displayPeerReview = true;
        this.displayEducationAndQualification = true;
        this.displayDistinctionAndInvitedPosition = true;
        this.displayMembershipAndService = true;
    }

    openEducation(): void {
        this.displayEducation = true;
    };

    openEmployment(): void {
        this.displayEmployment = true;
    };

	openEducationAndQualification(): void {
		this.displayEducationAndQualification = true;
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
    
    toggleEducationAndQualification(): void {
    	this.displayEducationAndQualification = !this.displayEducationAndQualification;
    };
    
    toggleDistinctionAndInvitedPosition(): void {
        this.displayDistinctionAndInvitedPosition = !this.displayDistinctionAndInvitedPosition;
    };
    
    toggleMembershipAndService(): void {
        this.displayMembershipAndService = !this.displayMembershipAndService;
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