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
    selector: 'self-service-remove-contact-ng2',
    template:  scriptTmpl("self-service-remove-contact-ng2-template")
})
export class SelfServiceRemoveContactComponent {
    
    private subscription: Subscription;
    private contact: any;

    constructor(
        private consortiaService: ConsortiaService,
        private modalService: ModalService
    ) { }
    
    removeContact(contact: any): void {
        this.consortiaService.removeContact(contact)
            .subscribe(
                data => {
                    this.consortiaService.notifyOther({action:'close', moduleId: 'selfServiceRemoveContact'});
                    this.closeModal();
                },
                error => {
                    //console.log('removeContact error', error);
                } 
        );
    }

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalSelfServiceRemoveContact'});
    };
    
    ngOnInit() {
        this.subscription = this.modalService.notifyObservable$.subscribe(
            (res) => { this.contact = res.contact; }
        );
    };

}
