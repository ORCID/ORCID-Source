import * as angular from "angular";
import { NgModule } from "@angular/core";
import { downgradeComponent } from "@angular/upgrade/static";
//User generated components
import { CommonNg2Module } from "./../common/common.ts";
import { OauthAuthorizationComponent } from "./oauthAuthorization.component.ts";
import { idBannerNg2Module } from "./../idBanner/idBanner.ts";
import { RegisterDuplicatesNg2Module } from './../registerDuplicates/registerDuplicates.ts';

// This is the Angular 2 part of the module

@NgModule({
    declarations: [OauthAuthorizationComponent],
    entryComponents: [OauthAuthorizationComponent],
    imports: [CommonNg2Module, idBannerNg2Module, RegisterDuplicatesNg2Module],
    providers: []
})
export class OauthAuthorizationNg2Module {}