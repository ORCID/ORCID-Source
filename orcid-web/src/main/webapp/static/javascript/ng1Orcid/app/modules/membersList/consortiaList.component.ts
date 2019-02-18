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

import { MembersListComponent }
    from './membersList.component.ts';

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
    
}