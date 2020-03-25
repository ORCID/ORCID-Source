import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';

import {Subject} from 'rxjs';

import {ModalService} from '../../shared/modal.service';

@Component({
    selector: 'spam-success-message-ng2',
    template:  scriptTmpl("spam-success-message-ng2-template")
})
export class SpamSuccessMessageComponent implements AfterViewInit, OnDestroy, OnInit {

    private ngUnsubscribe: Subject<void> = new Subject<void>();

    constructor(private modalService: ModalService) {}

    close(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'spamSuccess'});
    }

    ngAfterViewInit() {};

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {}
}