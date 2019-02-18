import { AfterViewInit, Component, NgModule } 
    from '@angular/core';

import { CommonService } 
    from '../../shared/common.service.ts';    
    
@Component({
    selector: 'oauth-header-ng2',
    template:  scriptTmpl("oauth-header-ng2-template")
})
export class OauthHeaderComponent {
    
    assetsPath: String;
    
    constructor(private commonSrvc: CommonService) {
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.assetsPath = data.messages['STATIC_PATH'];
            },
            error => {
                console.log('oauthHeader.component.ts: unable to fetch configInfo', error);
            } 
        );
    }
}