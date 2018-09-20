//Import all the angular components

import { Component } 
    from '@angular/core';

@Component({
    selector: 'print-record-ng2',
    template:  scriptTmpl("print-record-ng2-template")
})
export class PrintRecordComponent {

    printWindow: any;

    constructor(
    ) {
        this.printWindow = null;
    }

    printRecord(url): void{
        //open window
        this.printWindow = window.open(url);  
    }

}