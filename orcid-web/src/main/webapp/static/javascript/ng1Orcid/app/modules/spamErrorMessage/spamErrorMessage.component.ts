import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';

import {Subject} from 'rxjs';

import {ModalService} from '../../shared/modal.service';

@Component({
    selector: 'spam-error-message-ng2',
    template:  scriptTmpl("spam-error-message-ng2-template")
})
export class SpamErrorMessageComponent implements AfterViewInit, OnDestroy, OnInit {

    private ngUnsubscribe: Subject<void> = new Subject<void>();

    constructor(private modalService: ModalService) {}

    close(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'spamError'});
    }

    ngAfterViewInit() {};

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {}
}