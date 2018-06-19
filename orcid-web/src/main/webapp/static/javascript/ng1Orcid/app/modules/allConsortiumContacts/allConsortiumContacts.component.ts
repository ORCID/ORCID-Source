import { NgForOf, NgIf } 
    from '@angular/common';

import { Component, Input, NgModule } 
    from '@angular/core';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { CommonService } 
    from '../../shared/common.service.ts';

import { ConsortiaService }
    from '../../shared/consortia.service.ts'

import { FeaturesService }
    from '../../shared/features.service.ts'

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
    
    constructor(
        private commonSrvc: CommonService,
        private consortiaService: ConsortiaService,
        private featuresService: FeaturesService
    ) {}
  
  
    buildOrcidUri(orcid: String): string {
        return orcidVar.baseUri + '/' + orcid;
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

}