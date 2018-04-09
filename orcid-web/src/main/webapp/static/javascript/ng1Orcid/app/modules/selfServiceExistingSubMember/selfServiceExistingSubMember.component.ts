import { NgForOf, NgIf } 
    from '@angular/common';

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { ConsortiaService }
    from '../../shared/consortia.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'self-service-existing-sub-member-ng2',
    template:  scriptTmpl("self-service-existing-sub-member-ng2-template")
})
export class SelfServiceExistingSubMemberComponent {
    
    private subscription: Subscription;
    private newSubMemberExistingOrg: any;

    constructor(
        private consortiaService: ConsortiaService,
        private modalService: ModalService
    ) { }
    
    addSubMember(subMember: any): void {
        this.consortiaService.notifyOther({action:'add', moduleId: 'selfServiceExistingSubMember'});
        this.closeModal();
    }

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalSelfServiceExistingSubMember'});
    };
    
    ngOnInit() {
        this.subscription = this.modalService.notifyObservable$.subscribe(
            (res) => { this.newSubMemberExistingOrg = res.newSubMemberExistingOrg; }
        );
    };

}
