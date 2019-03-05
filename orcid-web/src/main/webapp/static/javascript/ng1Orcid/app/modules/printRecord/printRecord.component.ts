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

    constructor(
        private commonSrvc: CommonService
    ) {
        this.isPublicPage = this.commonSrvc.isPublicPage;
        this.printWindow = null;
    }

    printRecord(): void{
        //open window
        if(this.isPublicPage) {
             this.commonSrvc.publicUserInfo$
            .subscribe(
                data => {
                    this.printWindow = window.open(getBaseUri() + '/' + data['EFFECTIVE_USER_ORCID'] + '/print');                      
                },
                error => {
                    console.log('PrintRecordComponent.component.ts: unable to fetch publicUserInfo', error);                    
                } 
            );
        } else {
            this.commonSrvc.userInfo$
            .subscribe(
                data => {
                    this.printWindow = window.open(getBaseUri()  + '/' + data['EFFECTIVE_USER_ORCID'] + '/print');                
                },
                error => {
                    console.log('PrintRecordComponent.component.ts: unable to fetch userInfo', error);                    
                } 
            );
        }      
    }
}