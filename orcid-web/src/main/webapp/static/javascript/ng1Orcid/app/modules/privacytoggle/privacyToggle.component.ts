//Import all the angular components

import { NgFor } 
    from '@angular/common'; 

import { AfterViewInit, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
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
    @Input() elementId: string;
    @Input() dataPrivacyObj: any;

    @Output() 
    privacyUpdate: EventEmitter<any> = new EventEmitter<any>();

    private ngUnsubscribe: Subject<void> = new Subject<void>();

    showElement: any;

    constructor(
    ) {
        this.showElement = {};
    }

    hideTooltip(elementId): void{
        this.showElement[elementId] = false;
    };
    
    setPrivacy(priv): void {
        let _priv = priv;
        this.dataPrivacyObj.visiblity.visibility = _priv;
        this.privacyUpdate.emit(_priv);
    };
    
    showTooltip(elementId): void{
        this.showElement[elementId] = true;
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnChanges(changes: any) {
        // only run when property "data" changed
        if (changes['dataPrivacyObj']) {
            this.dataPrivacyObj = changes['dataPrivacyObj'].currentValue;
        }
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
    }; 
}