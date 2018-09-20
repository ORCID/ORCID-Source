import { NgForOf, NgIf } 
    from '@angular/common';

import { Component, Input, NgModule } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { CommonService } 
    from '../../shared/common.service.ts';

import { MembersListService }
    from '../../shared/membersList.service.ts';

import { FeaturesService }
    from '../../shared/features.service.ts';

@Component({
    selector: 'member-details-ng2',
    template:  scriptTmpl("member-details-ng2-template")
})
export class MemberDetailsComponent {
        
    communityTypes: any = {};
    showMemberDetailsLoader: boolean = true;
    showGetMemberDetailsError: boolean = false;
    currentMemberDetails: any = null;
    
    constructor(
        protected commonSrvc: CommonService,
        protected membersListService: MembersListService,
        protected featuresService: FeaturesService,
    ) {}
    
    getCommunityTypes(): void {
        this.membersListService.getCommunityTypes()
            .subscribe(data => {
                this.communityTypes = data;
            },
            error => {
                //console.log('getCommunityTypes error', error);
            } 
        );
    }
    
    getCurrentMemberDetails(): void {
        this.membersListService.getMemberDetailsBySlug(orcidVar.memberSlug)
            .subscribe(data => {
                this.showMemberDetailsLoader = false;
                this.currentMemberDetails = data;
                
            },
            error => {
                this.currentMemberDetails = null;
                this.showMemberDetailsLoader = false;
                this.showGetMemberDetailsError = true;
            } 
        );
    }
    
    getMemberPageUrl(slug: string): string {
        return orcidVar.baseUri + '/members/' + slug;
    }
    
    ngOnInit(): void {
        this.getCommunityTypes();
        this.getCurrentMemberDetails();   
    }

}