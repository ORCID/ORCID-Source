//Import all the angular components

import { NgFor } 
    from '@angular/common'; 

import { AfterViewInit, Component, Input, OnChanges, OnDestroy, OnInit } 
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
export class PrivacytoggleComponent implements AfterViewInit, OnChanges, OnDestroy, OnInit {
    @Input() name: string;
    @Input() data: any;

    private ngUnsubscribe: Subject<void> = new Subject<void>();

    showElement: any;

    constructor(
    ) {
        this.showElement = {};
    }

    hideTooltip(name): void{
        this.showElement[name] = false;
    };

    showTooltip(name): void{
        this.showElement[name] = true;
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnChanges(changes: any) {
        // only run when property "data" changed
        if (changes['data']) {
            this.data = changes['data'].currentValue;
            console.log(this.data);
        }
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