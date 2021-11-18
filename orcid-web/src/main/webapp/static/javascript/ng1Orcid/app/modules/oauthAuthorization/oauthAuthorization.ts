import * as angular from "angular";
import { NgModule } from "@angular/core";
import { downgradeComponent } from "@angular/upgrade/static";
//User generated components
import { CommonNg2Module } from "./../common/common";
import { OauthAuthorizationComponent, DialogOverviewExampleDialog } from "./oauthAuthorization.component";
import { idBannerNg2Module } from "./../idBanner/idBanner";
import { MatDialogModule } from '@angular/material/dialog';

// This is the Angular 1 part of the module
export const OauthAuthorizationModule = angular.module(
  "OauthAuthorizationModule",
  []
);

// This is the Angular 2 part of the module

@NgModule({
    declarations: [OauthAuthorizationComponent, DialogOverviewExampleDialog],
    entryComponents: [OauthAuthorizationComponent, DialogOverviewExampleDialog],
    imports: [CommonNg2Module, idBannerNg2Module, MatDialogModule
       ],
    providers: []
})
export class OauthAuthorizationNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

OauthAuthorizationModule.directive("oauthAuthorizationNg2", <any>(
  downgradeComponent({
    component: OauthAuthorizationComponent
  })
));