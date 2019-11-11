import { AfterViewInit, Component, NgModule } 
    from '@angular/core';

import { CommonService } 
    from '../../shared/common.service.ts';    
    
@Component({
    selector: 'footer-ng2',
    template:  scriptTmpl("footer-ng2-template"),
    preserveWhitespaces: true
})
export class FooterComponent {
    
    assetsPath: String;
    aboutUri: String;
    liveIds: String; 
    
    constructor(private commonSrvc: CommonService) {
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.assetsPath = data.messages['STATIC_PATH'];
                this.aboutUri = data.messages['ABOUT_URI'];
                this.liveIds = data.messages['LIVE_IDS'];
            },
            error => {
                console.log('footer.component.ts: unable to fetch configInfo', error);  
            } 
        );
    }
    
    getBaseUri(): String {
        return getBaseUri();
    }
}