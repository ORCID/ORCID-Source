import { NgForOf, NgIf } 
    from '@angular/common';

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { ConsortiaService }
    from '../../shared/consortia.service';

import { ModalService } 
    from '../../shared/modal.service'; 

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
