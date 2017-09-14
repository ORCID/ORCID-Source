//Import all the angular components

import { NgFor } 
    from '@angular/common'; 

import { AfterViewInit, Component, Input, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { BiographyService } 
    from '../../shared/biographyService.ts'; 

import { ConfigurationService } 
    from '../../shared/configurationService.ts';

import { EmailService } 
    from '../../shared/emailService.ts';

import { ModalService } 
    from '../../shared/modalService.ts'; 

@Component({
    selector: 'privacy-toggle-ng2',
    template:  scriptTmpl("privacy-toggle-ng2-template")
})
export class PrivacytoggleComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() name: string;

    private ngUnsubscribe: Subject<void> = new Subject<void>();

    showElement: any;

    constructor(
    ) {
        this.showElement = {};
        console.log('name', this.name);
    }

    hideTooltip(): void{
        this.showElement[this.name] = false;
    };

    showTooltip(): void{
        this.showElement[this.name] = true;
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        //this.getBiographyForm();
        //this.configuration = this.configurationService.getInitialConfiguration();
    }; 
}