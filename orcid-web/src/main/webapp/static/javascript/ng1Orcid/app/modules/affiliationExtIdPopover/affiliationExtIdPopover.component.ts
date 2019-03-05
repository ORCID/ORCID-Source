declare var om: any;

//Import all the angular components

import { NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, ElementRef, Input, OnInit} 
    from '@angular/core';

import { UrlProtocolPipe }
    from '../../pipes/urlProtocolNg2';

@Component({
    selector: 'affiliation-ext-id-popover-ng2',
    template:  scriptTmpl("affiliation-ext-id-popover-ng2-template"),
    providers: [ UrlProtocolPipe ]
})
export class AffiliationExtIdPopoverComponent implements OnInit {
    
    @Input() extID: any;
    @Input() putCode: any;

    displayAffiliationExtIdPopOver: any;

    constructor(
        private elementRef: ElementRef,
        private urlProtocol: UrlProtocolPipe
    ) {
        
        this.extID = elementRef.nativeElement.getAttribute('extID');        
        this.putCode = elementRef.nativeElement.getAttribute('group.activities[group.activePutCode].putCode.value+i');

        this.displayAffiliationExtIdPopOver = {};
    }

    hideAffiliationExtIdPopOver(id): void{
        this.displayAffiliationExtIdPopOver[id] = false;
    };

    showAffiliationExtIdPopOver(id): void{
        this.displayAffiliationExtIdPopOver[id] = true;
    };

    ngOnInit() {
        if(this.extID.url != null) {
            this.urlProtocol.transform(this.extID.url.value); 
        }        
    }; 
}