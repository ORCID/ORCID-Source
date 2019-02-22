import { AfterViewInit, Component, NgModule } 
    from '@angular/core';

import { CommonService } 
    from '../../shared/common.service.ts';    
    
@Component({
    selector: 'footer-ng2',
    template:  scriptTmpl("footer-ng2-template")
})
export class FooterComponent {
    
    assetsPath: String;
    aboutUri: String;
    
    constructor(private commonSrvc: CommonService) {
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.assetsPath = data.messages['STATIC_PATH'];
                this.aboutUri = data.messages['ABOUT_URI'];
            },
            error => {
                console.log('header.component.ts: unable to fetch userInfo', error);
            } 
        );
    }
    
    getBaseUri(): String {
        return getBaseUri();
    }
}