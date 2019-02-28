import * as angular from "angular";
import { NgModule } from "@angular/core";
import { downgradeComponent, UpgradeModule } from "@angular/upgrade/static";
import { CommonNg2Module } from "../common/common.ts";
import { lastModifiedComponent } from './lastModified.components.ts'

// This is the Angular 1 part of the module
export const lastModifiedModule = angular.module("lastModifiedModule", []);

// This is the Angular 2 part of the module
@NgModule({
  imports: [CommonNg2Module],
  declarations: [lastModifiedComponent],
  entryComponents: [lastModifiedComponent],
  providers: []
})
export class lastModifiedNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
lastModifiedModule
  .directive("lastModifiedNg2", <any>downgradeComponent({
    component: lastModifiedComponent
  }))