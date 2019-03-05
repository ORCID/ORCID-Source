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

import { MembersListComponent }
    from './membersList.component';

@Component({
    selector: 'consortia-list-ng2',
    template:  scriptTmpl("consortia-list-ng2-template")
})
export class ConsortiaListComponent extends MembersListComponent {

    getMembersList() {
        this.membersListService.getConsortiaList()
            .subscribe(data => {
                this.initMembersList(data);
            },
            error => {
                //console.log('getMembersList error', error);
            } 
        );
    }
    
    ngOnInit() {
        this.getCommunityTypes();
        this.getMembersList();
    }
    
    getBaseUri(): String {
        return getBaseUri();
    };
    
}