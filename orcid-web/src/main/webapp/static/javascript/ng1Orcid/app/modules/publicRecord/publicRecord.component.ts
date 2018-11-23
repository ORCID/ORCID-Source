declare var orcidVar: any;

//Import all the angular components

import { Component } 
    from '@angular/core';

import { CommonService } 
    from '../../shared/common.service.ts';

@Component({
    selector: 'public-record-ng2',
    template:  scriptTmpl("public-record-ng2-template")
})
export class PublicRecordComponent {
    popoverShowing: any;
    showSources: any;

    constructor(
        private commonService: CommonService
    ) {
        this.popoverShowing = new Array();
        this.showSources = new Array();
    }

    hidePopover(section): void{
        this.popoverShowing[section] = false;    
    };

    showPopover(section): void{
        this.popoverShowing[section] = true;
    }; 

    toggleSourcesDisplay(section): void {        
        this.showSources[section] = !this.showSources[section];     
    };
}
