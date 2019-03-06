import { NgForOf, NgIf } 
    from '@angular/common';

import { Component, Input, NgModule } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { CommonService } 
    from '../../shared/common.service';

import { ConsortiaService }
    from '../../shared/consortia.service'

import { FeaturesService }
    from '../../shared/features.service'

@Component({
    selector: 'all-consortium-contacts-ng2',
    template:  scriptTmpl("all-consortium-contacts-ng2-template")
})
export class AllConsortiumContactsComponent {
    
    private subscription: Subscription;
    
    @Input() contacts : any;
    
    effectiveUserOrcid = orcidVar.orcidId;
    realUserOrcid = orcidVar.realOrcidId;
    showInitLoader : boolean = true;
    assetsPath: String;
    
    constructor(
        private commonSrvc: CommonService,
        private consortiaService: ConsortiaService,
        private featuresService: FeaturesService
    ) {
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.assetsPath = data.messages['STATIC_PATH'];
            },
            error => {
                console.log('allConsortiumContacts.component.ts: unable to fetch configInfo', error);                
            } 
        );        
    }
  
  
    buildOrcidUri(orcid: String): string {
        return getBaseUri() + '/' + orcid;
    };
    
    getContacts() {
        this.consortiaService.getSubMemberContacts(this.consortiaService.getAccountIdFromPath())
            .subscribe(
                data => {
                this.contacts = data;
            },
            error => {
                //console.log('getAllConsortiumContacts error', error);
            } 
        );
    }
    
    ngOnInit() {
        console.log("initing all consortium contacts");
        this.getContacts();
    }
    
    getBaseUri(): String {
        return getBaseUri();
    }

}