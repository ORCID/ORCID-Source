//Import all the angular components
import { AfterViewInit, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

/*
Implementation Example:
<privacy-toggle-ng2 elementId="bio-privacy-toggle" [dataPrivacyObj]="biographyForm" (privacyUpdate)="privacyChange($event)"></privacy-toggle-ng2>

@Params:
elementId: Set a unique name, to show/hide the popup
[dataPrivacyObj]: Pass the object that has the visibility data.
(privacyUpdate): Pass the function of the parent component that will manage the api call to update the privacy value. This function NEEDS to be implemented in the parent component.
*/
@Component({
    selector: 'privacy-toggle-ng2',
    template:  scriptTmpl("privacy-toggle-ng2-template")
})
export class PrivacytoggleComponent implements AfterViewInit, OnChanges, OnDestroy, OnInit {
    @Input() elementId: string;
    @Input() dataPrivacyObj: any;
    @Input() privacyNodeName: string;

    @Output() privacyUpdate: EventEmitter<any> = new EventEmitter<any>();

    showElement: any;

    constructor(
    ) {
        this.showElement = [];
    }

    hideTooltip(elementId): void{
        this.showElement[elementId] = false;
    };
    
    setPrivacy(priv?): void {
        let _priv = priv;
        ////console.log('dataPrivacyObj', this.privacyNodeName, this.dataPrivacyObj, this.dataPrivacyObj[this.privacyNodeName]);
        if( this.privacyNodeName ){
            this.dataPrivacyObj[this.privacyNodeName].visibility = _priv;
        } else {
            this.dataPrivacyObj.visibility = _priv;
        }
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