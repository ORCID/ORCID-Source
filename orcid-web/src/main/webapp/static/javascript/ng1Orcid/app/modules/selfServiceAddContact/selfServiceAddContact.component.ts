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
    selector: 'self-service-add-contact-ng2',
    template:  scriptTmpl("self-service-add-contact-ng2-template")
})
export class SelfServiceAddContactComponent {
    
    private subscription: Subscription;
    private input: any;
    private emailSearchResult: any;
    private addContactDisabled: boolean;

    constructor(
        private consortiaService: ConsortiaService,
        private modalService: ModalService
    ) { }
    
    addContact(): void {
        this.addContactDisabled = true;
        let contact : any = { email: this.input.text, accountId: this.consortiaService.getAccountIdFromPath() };
        this.consortiaService.addContact(contact)
            .subscribe(
                data => {
                    this.addContactDisabled = false;
                    this.input.text = "";
                    this.consortiaService.notifyOther({action:'close', moduleId: 'selfServiceAddContact'});
                    this.closeModal();
                },
                error => {
                    //console.log('addContact error', error);
                } 
        );
    }

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalSelfServiceAddContact'});
    };
    
    ngOnInit() {
        this.subscription = this.modalService.notifyObservable$.subscribe(
            (res) => { this.input = res.input; this.emailSearchResult = res.emailSearchResult; }
        );
    };

}
