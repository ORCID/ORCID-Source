import * as angular from "angular";
import { NgModule } from "@angular/core";
import { downgradeComponent, UpgradeModule } from "@angular/upgrade/static";
import { qrcodeComponent } from './qrcode.components.ts'
import { CommonNg2Module } from "../common/common.ts";

// This is the Angular 1 part of the module
export const qrcodeModule = angular.module("qrcodeModule", []);

// This is the Angular 2 part of the module
@NgModule({
  imports: [CommonNg2Module],
  declarations: [qrcodeComponent],
  entryComponents: [qrcodeComponent],
  providers: []
})
export class qrcodeNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
qrcodeModule
  .directive("qrcodeNg2", <any>downgradeComponent({
    component: qrcodeComponent
  }))