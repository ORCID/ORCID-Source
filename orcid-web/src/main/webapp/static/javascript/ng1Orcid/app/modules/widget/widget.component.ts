import { NgForOf, NgIf } 
    from '@angular/common'; 

import { Component, Input, NgModule } 
    from '@angular/core';
    
import { CommonService } 
    from '../../shared/common.service.ts';

@Component({
    selector: 'widget-ng2',
    template:  scriptTmpl("widget-ng2-template")
})
export class WidgetComponent {
    hash: any;
    showCode: any;
    widgetURLND: any;
    domain: any;
    
    constructor(private commonSrvc: CommonService) {
        this.hash = orcidVar.orcidIdHash.substr(0, 6);
        this.showCode = false;
        
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.domain = data.messages['ABOUT_URI'];    
                this.widgetURLND = '<div itemscope itemtype="https://schema.org/Person"><a itemprop="sameAs" content="'+ getBaseUri() + '/' + orcidVar.orcidId + '" href="'+ getBaseUri() + '/' + orcidVar.orcidId + '" target="orcid.widget" rel="noopener noreferrer" style="vertical-align:top;"><img src="https://orcid.org/sites/default/files/images/orcid_16x16.png" style="width:1em;margin-right:.5em;" alt="ORCID iD icon">' + this.domain + '/' + orcidVar.orcidId + '</a></div>';
            },
            error => {
                console.log('widget.component.ts: unable to fetch configInfo', error);                
            } 
        );       
    } 

    hideWidgetCode(): void{
        this.showCode = false;
    };

    inputTextAreaSelectAll($event): any{
        $event.target.select();
    };
        
    toggleCopyWidget(): void{
        this.showCode = !this.showCode;
    };
    
    getBaseUri() : String {
        return getBaseUri();
    };
}