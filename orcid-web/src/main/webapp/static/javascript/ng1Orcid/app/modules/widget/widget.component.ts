declare var getBaseUri: any;
declare var scriptTmpl: any;
declare var orcidVar: any;

import * as angular from 'angular';
import { NgModule } from '@angular/core';

export class widgetCtrl {
    hash: any;
    showCode: any;
    widgetURLND: any;

    static $inject = [
        '$scope'
    ];

    constructor( 
        $scope
    ) {
        console.log('widgetCtrl loaded');
        this.hash = orcidVar.orcidIdHash.substr(0, 6);
        this.showCode = false;
        this.widgetURLND = '<a href="'+ getBaseUri() + '/' + orcidVar.orcidId + '" target="orcid.widget" rel="noopener noreferrer" style="vertical-align:top;"><img src="https://orcid.org/sites/default/files/images/orcid_16x16.png" style="width:1em;margin-right:.5em;" alt="ORCID iD icon">' + orcidVar.baseDomainRmProtocall + '/' + orcidVar.orcidId + '</a>';

    }

    hideWidgetCode(): void{
        this.showCode = false;
    };

    inputTextAreaSelectAll($event): any{
        $event.target.select();
    };
    
    toggleCopyWidget(): any{
        console.log('toggleCopyWidget');
        this.showCode = !this.showCode;
    };

}

export const widgetCmp = {
    /*
    template : './widget.component.html',
    */
    controller: widgetCtrl,
    controllerAs: 'ctrl'
};
//let testInject: any;
//testInject = document.getElementById("widgetCtrlTest").textContent;
//testInject = testInject.textContent;

import {Component, Input} from '@angular/core';

@Component({
    selector: 'widget-ng2',
    template: scriptTmpl("verify-email-modal-sent")
    //templateUrl: './app/modules/widget/widget.component.html'
})
export class widgetNg2Cmp {
}