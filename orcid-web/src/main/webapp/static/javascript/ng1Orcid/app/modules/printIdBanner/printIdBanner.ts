import * as angular from "angular";
import { NgModule } from "@angular/core";
import { downgradeComponent, UpgradeModule } from "@angular/upgrade/static";
import { CommonNg2Module } from "./../common/common.ts";
import { printIdBannerComponent } from "./printIdBanner.component.ts";

// This is the Angular 1 part of the module
export const printIdBannerModule = angular.module("printIdBannerModule", []);

// This is the Angular 2 part of the module
@NgModule({
  imports: [CommonNg2Module],
  declarations: [printIdBannerComponent],
  entryComponents: [printIdBannerComponent],
  exports: [],
  providers: []
})
export class printIdBannerNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
printIdBannerModule
  .directive("printIdBannerNg2", <any>downgradeComponent({
    component: printIdBannerComponent
  }))
