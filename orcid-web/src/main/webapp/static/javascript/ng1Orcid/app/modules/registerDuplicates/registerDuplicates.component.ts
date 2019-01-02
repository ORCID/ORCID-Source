import { NgForOf, NgIf } 
    from '@angular/common';

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

import { OauthService } 
    from '../../shared/oauth.service.ts';

@Component({
    selector: 'register-duplicates-ng2',
    template:  scriptTmpl("register-duplicates-ng2-template")
})
export class RegisterDuplicatesComponent {
    private duplicates: any;
    private subscription: Subscription;
    showRegisterProcessing: boolean;

    constructor(
        private cdr: ChangeDetectorRef,
        private modalService: ModalService,
        private oauthService: OauthService
    ) { 
        this.showRegisterProcessing = false;
    }

    oauth2ScreensPostRegisterConfirm(): void {
        this.oauthService.notifyOther({action:'confirm', moduleId: 'registerDuplicates'});
        this.showRegisterProcessing = true;
    }

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalRegisterDuplicates'});
    };

    ngAfterViewInit() {
        console.log("after view init")
        this.subscription = this.modalService.notifyObservable$.subscribe(
            (res) => { 
                console.log(res);
                this.duplicates = res.duplicates; 
                this.cdr.detectChanges();
            }
        );
    }
    
    ngOnInit() {
        console.log("init register duplicates");
        
    };

}