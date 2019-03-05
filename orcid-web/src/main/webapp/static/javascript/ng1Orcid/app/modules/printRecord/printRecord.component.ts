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

    printRecord(url): void{
        //open window
        this.printWindow = window.open(url);  
    }

}