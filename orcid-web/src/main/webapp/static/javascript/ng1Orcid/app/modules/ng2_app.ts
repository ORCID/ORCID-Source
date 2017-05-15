import 'reflect-metadata';

import { NgModule, Component } from '@angular/core';
import { BrowserModule } from "@angular/platform-browser";
import { UpgradeModule } from '@angular/upgrade/static';
import { EmailFrequencyCtrlNg2Module } from './../controllers/EmailFrequencyCtrl.ts'

@Component({
    selector: 'ng2-comp',
    template: '<div>{{value}}</div>'
}) 
export class Ng2Comp {
    private value;

    constructor() {
        this.value = "Success ng2"
    }

    ngDoBootstrap() {}
}

@NgModule({
    imports: [
        BrowserModule,
        UpgradeModule,
        EmailFrequencyCtrlNg2Module
    ],
    bootstrap: [Ng2Comp],
    declarations: [Ng2Comp]
})
export class Ng2AppModule {
    constructor(public upgrade: UpgradeModule){}
}