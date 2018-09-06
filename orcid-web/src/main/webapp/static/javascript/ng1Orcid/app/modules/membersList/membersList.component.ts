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
    selector: 'members-list-ng2',
    template:  scriptTmpl("members-list-ng2-template")
})
export class MembersListComponent {
        
    @Input() byCountry : string = "";
    @Input() byResearchCommunity : string = "";
    @Input() activeLetter: string = "";
    
    communityTypes: any = {};
    membersList: Array<object> = [];
    unfilteredMembersList: Array<object> = [];
    alphabet: Array<string> = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    
    constructor(
        private commonSrvc: CommonService,
        private membersListService: MembersListService,
        private featuresService: FeaturesService,
    ) {}
    
    getCommunityTypes() {
        this.membersListService.getCommunityTypes()
            .subscribe(data => {
                this.communityTypes = data;
            },
            error => {
                //console.log('getCommunityTypes error', error);
            } 
        );
    }
    
    getMembersList() {
        this.membersListService.getMembersList()
            .subscribe(data => {
                data.sort(function(a, b){ return a.publicDisplayName.localeCompare(b.publicDisplayName); });
                this.unfilteredMembersList = data;
                this.membersList = this.unfilteredMembersList.slice();
            },
            error => {
                //console.log('getMembersList error', error);
            } 
        );
    }
    
    getMemberPageUrl(slug: string) : string {
        return getBaseUri() + '/members/' + slug;
    }
  
    filterSelected() : void {
        let byCountry = this.byCountry;
        let byResearchCommunity = this.byResearchCommunity;
        let activeLetter = this.activeLetter;
        if(byCountry === "" && byResearchCommunity === "" && activeLetter ===""){
           this.membersList = this.unfilteredMembersList.slice();
        }
        else{
            this.membersList = this.unfilteredMembersList.filter(
                function(member: any){
                    return (byCountry === "" || member.country === byCountry)
                           && (byResearchCommunity === "" || member.researchCommunity === byResearchCommunity)
                           && (activeLetter === "" || member.publicDisplayName.startsWith(activeLetter));
                }
            );
        }
    }
    
    clearFilters() : void {
        this.byCountry = "";
        this.byResearchCommunity = "";
        this.activeLetter = "";
        this.filterSelected();
    }
    
    activateLetter(letter: string) : void {
        this.activeLetter = letter;
        this.filterSelected();
    }
    
    ngOnInit() {
        this.getCommunityTypes();
        this.getMembersList();
    }

}