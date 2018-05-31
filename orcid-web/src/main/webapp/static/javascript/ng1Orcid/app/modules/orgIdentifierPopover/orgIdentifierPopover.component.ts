declare var om: any;

//Import all the angular components

import { NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, ElementRef, Input, OnInit} 
    from '@angular/core';

@Component({
    selector: 'org-identifier-popover-ng2',
    template:  scriptTmpl("org-identifier-popover-ng2-template")
})
export class OrgIdentifierPopoverComponent implements OnInit {
    
    @Input() value: any;
    @Input() putCode: any;
    @Input() type: any;

    GRID_BASE_URL: any;
    TEST_BASE_URL: any;
    displayType: any;
    displayURLPopOver: any;
    link: any;

    constructor(
        private elementRef: ElementRef
    ) {
        this.value = elementRef.nativeElement.getAttribute('group.activities[group.activePutCode].disambiguatedAffiliationSourceId.value');
        this.putCode = elementRef.nativeElement.getAttribute('group.activities[group.activePutCode].putCode.value');
        this.type = elementRef.nativeElement.getAttribute('group.activities[group.activePutCode].disambiguationSource.value');

        this.GRID_BASE_URL = "https://www.grid.ac/institutes/";
        this.TEST_BASE_URL = "https://orcid.org/";
        this.displayURLPopOver = {};
        this.link = null;
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
          
    }; 
}