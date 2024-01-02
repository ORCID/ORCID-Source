import { NgForOf, NgIf } 
    from '@angular/common';

import { Component, Input, NgModule } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { CommonService } 
    from '../../shared/common.service';

import { MembersListService }
    from '../../shared/membersList.service';

import { FeaturesService }
    from '../../shared/features.service';

@Component({
    selector: 'member-details-ng2',
    template:  scriptTmpl("member-details-ng2-template")
})
export class MemberDetailsComponent {
        
    communityTypes: any = {};
    badges: any = null;
    showMemberDetailsLoader: boolean = true;
    showGetMemberDetailsError: boolean = false;
    currentMemberDetails: any = null;
    badgesAwarded: any = {}
    assetsPath: String;
    
    constructor(
        protected commonSrvc: CommonService,
        protected membersListService: MembersListService,
        protected featuresService: FeaturesService,
    ) {
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.assetsPath = data.messages['STATIC_PATH'];
            },
            error => {
                console.log('memberDetails.component.ts: unable to fetch configInfo', error);                
            } 
        );
    }
    
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
        var memberId = window.location.pathname.substring(window.location.pathname.lastIndexOf('/') + 1);
        var memberIdStripped = memberId.replace(/<[^>]+>/g, '').trim();
        this.membersListService.getMemberDetailsById(memberIdStripped)
            .subscribe(data => {
                this.showMemberDetailsLoader = false;
                this.currentMemberDetails = data;
                 this.updateAwardedBadges();
                
            },
            error => {
                this.currentMemberDetails = null;
                this.showMemberDetailsLoader = false;
                this.showGetMemberDetailsError = true;
            } 
        );
    }
    
    updateAwardedBadges(): void {
        if(this.badges != null && this.currentMemberDetails != null) {
            for(let integration of this.currentMemberDetails.integrations){
                for(let achievement of integration.achievements){
                    let badgeName = this.badges[achievement.badgeId].name;
                    let integrationBadges = this.badgesAwarded[integration.id];
                    if(integrationBadges == null){
                        integrationBadges = {};
                        this.badgesAwarded[integration.id] = integrationBadges;
                    }
                    integrationBadges[badgeName] = true;
                }
            }
        }
    }
    
    getMemberPageUrl(memberId: string): string {
        return getBaseUri() + '/members/' + memberId;
    }
    
    ngOnInit(): void {
        this.getCommunityTypes();
        this.getCurrentMemberDetails();   
    }

    getBaseUri(): String {
        return getBaseUri();
    };
}