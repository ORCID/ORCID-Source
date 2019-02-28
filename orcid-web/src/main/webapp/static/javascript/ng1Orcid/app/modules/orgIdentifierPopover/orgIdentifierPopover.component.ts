declare var om: any;

//Import all the angular components

import { NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, ElementRef, Input, OnInit} 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service.ts';

import { OrgDisambiguated } 
    from '../orgIdentifierPopover/orgDisambiguated.ts';

@Component({
    selector: 'org-identifier-popover-ng2',
    template:  scriptTmpl("org-identifier-popover-ng2-template")
})
export class OrgIdentifierPopoverComponent implements OnInit {
    
    @Input() value: any;
    @Input() putCode: any;
    @Input() type: any;

    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    GRID_BASE_URL: any;
    TEST_BASE_URL: any;
    displayType: any;
    displayURLPopOver: any;
    link: any;

    constructor(
        private commonSrvc: CommonService,
        private elementRef: ElementRef
    ) {
        this.GRID_BASE_URL = "https://www.grid.ac/institutes/";
        this.TEST_BASE_URL = "https://orcid.org/";
        this.displayURLPopOver = {};
        this.link = null;
    }

    getDisambiguatedOrgDetails(type, value): void {
        this.commonSrvc.getDisambiguatedOrgDetails(type, value)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            (data: OrgDisambiguated[]) => {
                this.commonSrvc.orgDisambiguatedDetails[type + value] = data;
            },
            error => {
                console.log('getDisambiguatedOrgDetailsError', error);
            } 
        );
    }

    hideURLPopOver(id): void{
        this.displayURLPopOver[id] = false;
    };

    showURLPopOver(id): void {
        this.displayURLPopOver[id] = true;
    };

    ngOnInit() {
        if (this.type != null) {
            if (this.type == 'TEST') {
                this.link = this.TEST_BASE_URL + this.value;
                this.displayType = 'Test Id';
            } else if (this.type == 'FUNDREF') {
                this.link = this.value;
                this.displayType = om.get('affiliation.org_id.value.label.fundref');
            } else if (this.type == 'GRID') {
                this.link = this.GRID_BASE_URL + this.value;
                this.displayType = om.get('affiliation.org_id.value.label.grid');
            } else if (this.type == 'RINGGOLD') {
                this.link = null;
                this.displayType = om.get('affiliation.org_id.value.label.ringgold');
            } else {
                this.link = null;
                this.displayType = this.type;
            }
            
        } 
        if(this.type && this.value){
            if(!this.commonSrvc.orgDisambiguatedDetails[this.type + this.value]){
                this.getDisambiguatedOrgDetails(this.type, this.value);
            }
        }
          
    };

    isUrl (element) {
        var expression = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi;
        var regex = new RegExp(expression);
        return element.match(regex)
    }
    
    
}
