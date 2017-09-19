//Import all the angular components
import { AfterViewInit, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

import { BiographyService } 
    from '../../shared/biographyService.ts'; 

import { ConfigurationService } 
    from '../../shared/configurationService.ts';

@Component({
    selector: 'privacy-toggle-ng2',
    template:  scriptTmpl("privacy-toggle-ng2-template")
})
export class PrivacytoggleComponent implements AfterViewInit, OnChanges, OnDestroy, OnInit {
    @Input() elementId: string;
    @Input() dataPrivacyObj: any;

    @Output() privacyUpdate: EventEmitter<any> = new EventEmitter<any>();

    showElement: any;

    constructor(
    ) {
        this.showElement = {};
    }

    hideTooltip(elementId): void{
        this.showElement[elementId] = false;
    };
    
    setPrivacy(priv): void {
        let _priv = priv;
        this.dataPrivacyObj.visiblity.visibility = _priv;
        this.privacyUpdate.emit(_priv);
    };
    
    showTooltip(elementId): void{
        this.showElement[elementId] = true;
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnChanges(changes: any) {
        // only run when property "data" changed
        if (changes['dataPrivacyObj']) {
            this.dataPrivacyObj = changes['dataPrivacyObj'].currentValue;
        }
    };

    ngOnDestroy() {
    };

    ngOnInit() {
    }; 
}