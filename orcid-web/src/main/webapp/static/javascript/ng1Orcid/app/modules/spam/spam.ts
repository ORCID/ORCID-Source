import * as angular from "angular";
import { NgModule } from "@angular/core";
import { downgradeComponent, UpgradeModule } from "@angular/upgrade/static";
import { CommonNg2Module } from "../common/common";
import { SpamComponent } from './spam.components'

// This is the Angular 1 part of the module
export const SpamModule = angular.module("spamModule", []);

// This is the Angular 2 part of the module
@NgModule({
  imports: [CommonNg2Module],
  declarations: [SpamComponent],
  entryComponents: [SpamComponent],
  providers: []
})
export class SpamNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
SpamModule
  .directive("spamNg2", <any>downgradeComponent({
    component: SpamComponent
  }))