import 'reflect-metadata';

import { NgModule, Component } from '@angular/core';
import { BrowserModule } from "@angular/platform-browser";
import { UpgradeModule } from '@angular/upgrade/static';

import { EmailFrequencyCtrlNg2Module } from './../controllers/EmailFrequencyCtrl.ts';

@Component({
    selector: 'root-cmp',
    template: '<div class="ng-view"></div>'
}) 
export class RootCmp {
    /*private value;

    constructor() {
        this.value = "Success ng2"
    }*/

    //ngDoBootstrap() {}
}

@NgModule({
    imports: [
        BrowserModule,
        UpgradeModule,
        EmailFrequencyCtrlNg2Module
    ],
    bootstrap: [RootCmp],
    declarations: [RootCmp]
})
export class Ng2AppModule {
    constructor(public upgrade: UpgradeModule){}
}