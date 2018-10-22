import * as angular from "angular";

import { Directive, NgModule } from "@angular/core";

import { downgradeComponent } from "@angular/upgrade/static";

//User generated
import { ManageMembersComponent } from "./manageMembers.component.ts";

import { CommonNg2Module } from "./../common/common.ts";

import { AddMemberFormComponent } from "./addMemberForm.component.ts"

// This is the Angular 1 part of the module
export const ManageMembersModule = angular.module("ManageMembersModule", []);

// This is the Angular 2 part of the module
@NgModule({
  imports: [CommonNg2Module],
  declarations: [ManageMembersComponent, AddMemberFormComponent],
  entryComponents: [ManageMembersComponent, AddMemberFormComponent],
  providers: []
})
export class ManageMembersNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
ManageMembersModule.directive("manageMembersNg2", <any>downgradeComponent({
  component: ManageMembersComponent
}));
