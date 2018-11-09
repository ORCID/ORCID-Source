//Import all the angular components

import { NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, ElementRef, Input, OnInit} 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { catchError, map, tap } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service.ts';

import { UrlProtocolPipe }
    from '../../pipes/urlProtocolNg2.ts';

@Component({
    selector: 'ext-id-popover-ng2',
    template:  scriptTmpl("ext-id-popover-ng2-template"),
    providers: [ UrlProtocolPipe ]
})
export class ExtIdPopoverComponent implements OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
    
    @Input() extID: any;
    @Input() putCode: any;
    @Input() activityType: any;

    displayAffiliationExtIdPopOver: any;
    relationship: string;
    type: string;
    url: string;
    value: string;

    constructor(
        private commonService: CommonService,
        private elementRef: ElementRef,
        private urlProtocol: UrlProtocolPipe
    ) {
        this.extID = elementRef.nativeElement.getAttribute('extID');
        this.putCode = elementRef.nativeElement.getAttribute('putCode');
        this.activityType = elementRef.nativeElement.getAttribute('activityType');
        this.displayAffiliationExtIdPopOver = {};
    }

    hideAffiliationExtIdPopOver(id): void{
        this.displayAffiliationExtIdPopOver[id] = false;
    };

    showAffiliationExtIdPopOver(id): void{
        this.displayAffiliationExtIdPopOver[id] = true;
    };

    ngOnInit() {
        switch(this.activityType){
            case "affiliation":
                if(this.extID.relationship){
                    if(this.extID.relationship.value){
                        this.relationship = JSON.parse(JSON.stringify(this.extID.relationship.value));
                    }
                }
                this.type = JSON.parse(JSON.stringify(this.extID.externalIdentifierType.value));
                if(this.extID.url){
                    if(this.extID.url.value){
                        this.url = JSON.parse(JSON.stringify(this.extID.url.value));
                    }
                }
                this.value = JSON.parse(JSON.stringify(this.extID.externalIdentifierId.value));
                break;
            case "funding":
                if(this.extID.relationship){
                    if(this.extID.relationship.value){
                        this.relationship = JSON.parse(JSON.stringify(this.extID.relationship.value));
                    }
                }
                this.type = JSON.parse(JSON.stringify(this.extID.externalIdentifierType.value));
                if(this.extID.url){
                    if(this.extID.url.value){
                        this.url = JSON.parse(JSON.stringify(this.extID.url.value));
                    }
                }
                this.value = JSON.parse(JSON.stringify(this.extID.externalIdentifierId.value));
                break;
            case "researchResource":
                if(this.extID.relationship){
                    if(this.extID.relationship.value){
                        this.relationship = JSON.parse(JSON.stringify(this.extID.relationship.value));
                    }
                }
                this.type = JSON.parse(JSON.stringify(this.extID.externalIdentifierType.value));
                if(this.extID.url){
                    if(this.extID.url.value){
                        this.url = JSON.parse(JSON.stringify(this.extID.url.value));
                    }
                }
                this.value = JSON.parse(JSON.stringify(this.extID.externalIdentifierId.value));
                break;
            case "work":
                this.relationship = JSON.parse(JSON.stringify(this.extID.relationship.value));
                if(this.extID.relationship){
                    if(this.extID.relationship.value){
                        this.relationship = JSON.parse(JSON.stringify(this.extID.relationship.value));
                    }
                }
                if(this.extID.url){
                    if(this.extID.url.value){
                        this.url = JSON.parse(JSON.stringify(this.extID.url.value));
                    }
                }
                this.value = JSON.parse(JSON.stringify(this.extID.externalIdentifierId.value));
                break;
            default:
                this.type = JSON.parse(JSON.stringify(this.extID.externalIdentifierType.value));
                if(this.extID.relationship){
                    if(this.extID.relationship.value){
                        this.relationship = JSON.parse(JSON.stringify(this.extID.relationship.value));
                    }
                }
                if(this.extID.url){
                    if(this.extID.url.value){
                        this.url = JSON.parse(JSON.stringify(this.extID.url.value));
                    }
                }
                this.value = JSON.parse(JSON.stringify(this.extID.externalIdentifierId.value));
                break;
        }
        if(this.url){
            this.urlProtocol.transform(this.url); 
        } else {
            if(this.extID.normalizedUrl){
                if(this.extID.normalizedUrl.value){
                    this.url = this.extID.normalizedUrl.value;
                }
            }
        }
        
    }; 
}