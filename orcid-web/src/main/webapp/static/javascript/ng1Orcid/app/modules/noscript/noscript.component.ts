import { AfterViewInit, Component, NgModule, OnInit, ChangeDetectorRef } 
    from '@angular/core';

@Component({
    selector: 'noscript-ng2',
    template:  scriptTmpl("noscript-ng2-template")
})
export class NoscriptComponent implements OnInit {
    
    constructor() {
        
    }

    ngOnDestroy() {
        
    };

    ngOnInit() {
        
    };
    
    getBaseUri() : String {
        return getBaseUri();
    };
}