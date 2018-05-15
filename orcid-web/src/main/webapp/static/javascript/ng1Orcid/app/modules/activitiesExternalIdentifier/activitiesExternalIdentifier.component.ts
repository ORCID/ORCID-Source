declare var $: any; //delete
declare var orcidVar: any;
declare var getBaseUri: any;
declare var om: any;
declare var om: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, ChangeDetectorRef, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

import { CommonService } 
    from '../../shared/common.service.ts';


@Component({
    selector: 'activities-external-identifier-ng2',
    template:  scriptTmpl("activities-external-identifier-ng2-template")
})
export class ActivitiesExternalIdentifierComponent implements AfterViewInit, OnDestroy, OnInit {
    
    @Input('extID') externalIdentifier: any;
    @Input() putCode: any;
    @Input() index: any;

    displayActivityExtIdPopOver: boolean;
    isPartOf: boolean;
    link: any;
    value: any;
    type: any;

    constructor(
        private commonService: CommonService
    ) {
        //this.authorizationForm = elementRef.nativeElement.getAttribute('authorizationForm');
        //this.showDeactivatedError = elementRef.nativeElement.getAttribute('showDeactivatedError');
        //this.showReactivationSent = elementRef.nativeElement.getAttribute('showReactivationSent');
        this.isPartOf = false;
        this.displayActivityExtIdPopOver = false;
        this.link = '';

    }

    hideActivityExtIdPopOver(id): void{
        console.log("hide popover " + id);
        this.displayActivityExtIdPopOver[id] = false;
    };

    showActivityExtIdPopOver(id): void{
        console.log("show popover " + id);
        this.displayActivityExtIdPopOver[id] = true;
    };
        
        

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {

    };

    ngOnInit() {
        console.log(this.externalIdentifier);
        console.log(this.putCode);
        console.log(this.index);

        if(this.externalIdentifier.relationship != null && this.externalIdentifier.relationship.value == 'part-of') {
            this.isPartOf = true;     
        }

        if(this.externalIdentifier.value != null){
            this.value = this.externalIdentifier.value.value;
        }
        
        if(this.externalIdentifier.url != null) {
            this.link = this.externalIdentifier.url.value;
        }

        if (this.externalIdentifier.type != null) {
            this.type = this.externalIdentifier.type.value;        
        }

        if (this.type != null && typeof this.type != 'undefined') {
            this.type.escapeHtml().toUpperCase();
        }
        
        if(this.link != null) {
            this.link = this.commonService.addUrlProtocol(this.link);
            
            if(this.value != null) {
                this.value.escapeHtml()
            }
        }

          
    }; 
}