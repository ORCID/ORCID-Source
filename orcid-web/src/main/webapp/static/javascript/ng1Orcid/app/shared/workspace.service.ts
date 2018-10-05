import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class WorkspaceService {
    public displayDistinctionAndInvitedPosition: boolean;
    public displayEducation: boolean;
    public displayEducationAndQualification: boolean;
    public displayEmployment: boolean;
    public displayFunding: boolean;
    public displayMembershipAndService: boolean;
    public displayPersonalInfo: boolean;
    public displayPeerReview: boolean; 
    public displayResearchResource: boolean;
    public displayWorks: boolean;   

    constructor(){
        this.displayDistinctionAndInvitedPosition = true;
        this.displayEducation = true;
        this.displayEducationAndQualification = true;
        this.displayEmployment = true;
        this.displayFunding = true;
        this.displayMembershipAndService = true;
        this.displayPeerReview = true;
        this.displayPersonalInfo = true;
        this.displayResearchResource = true;
        this.displayWorks = true;
        
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

    openResearchResource(): void {
        this.displayResearchResource = true;
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

    togglePeerReview(): void {              
        this.displayPeerReview = !this.displayPeerReview;
    };

    togglePeerReviews(): void {
        this.displayPeerReview = !this.displayPeerReview;
    };

    toggleResearchResource(): void {
        this.displayResearchResource = !this.displayResearchResource;
    };

    togglePersonalInfo(): void {
        this.displayPersonalInfo = !this.displayPersonalInfo;
    };
}