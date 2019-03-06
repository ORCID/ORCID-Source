import * as angular from "angular";
import { NgModule } from "@angular/core";
import { downgradeComponent, UpgradeModule } from "@angular/upgrade/static";
import { CommonNg2Module } from "./../common/common";
import { idBannerComponent } from "./idBanner.component";
import { NameComponent } from "./name.component";
import { SwitchUserComponent } from "./switchUser.component";
import { OrderByPipe } from "../../pipes/orderByNg2Child";

// This is the Angular 1 part of the module
export const idBannerModule = angular.module("idBannerModule", []);

// This is the Angular 2 part of the module
@NgModule({
  imports: [CommonNg2Module],
  declarations: [idBannerComponent, NameComponent, OrderByPipe, SwitchUserComponent],
  entryComponents: [idBannerComponent, NameComponent, SwitchUserComponent],
  exports: [SwitchUserComponent, NameComponent],
  providers: []
})
export class idBannerNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
idBannerModule
  .directive("idBannerNg2", <any>downgradeComponent({
    component: idBannerComponent
  }))
  .directive("nameNg2", <any>downgradeComponent({
    component: NameComponent
  }))
  .directive("switchUserNg2", <any>downgradeComponent({
    component: SwitchUserComponent
  }));
