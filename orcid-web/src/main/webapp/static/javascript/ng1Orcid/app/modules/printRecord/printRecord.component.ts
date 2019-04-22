//Import all the angular components

import { Component } 
    from '@angular/core';

import { CommonService } 
    from '../../shared/common.service.ts';

@Component({
    selector: 'print-record-ng2',
    template:  scriptTmpl("print-record-ng2-template")
})
export class PrintRecordComponent {
    
    isPublicPage: boolean;
    printWindow: any;
    printUrl: string
    hidePrint: boolean
    constructor(
        private commonSrvc: CommonService
    ) {
        this.isPublicPage = this.commonSrvc.isPublicPage;
        this.printWindow = null;
        //open window
        if(this.isPublicPage) {
            this.commonSrvc.publicUserInfo$
            .subscribe(
                userInfo => {
                    this.hidePrint = !userInfo || userInfo.IS_LOCKED === 'true' || userInfo.IS_DEACTIVATED === 'true'
                    this.printUrl = getBaseUri() + '/' + userInfo['EFFECTIVE_USER_ORCID'] + '/print';                   
                },
                error => {
                    console.log('PrintRecordComponent.component.ts: unable to fetch publicUserInfo', error);                    
                } 
            );
        } else {
            this.commonSrvc.userInfo$
            .subscribe(
                data => {
                    this.printUrl = getBaseUri()  + '/' + data['EFFECTIVE_USER_ORCID'] + '/print';                
                },
                error => {
                    console.log('PrintRecordComponent.component.ts: unable to fetch userInfo', error);                    
                } 
            );
        }  
    }

    printRecord(): void{
        this.printWindow = window.open(this.printUrl)
    }
}