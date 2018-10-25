import * as angular from "angular";

import { Directive, NgModule } from "@angular/core";

import { downgradeComponent } from "@angular/upgrade/static";

//User generated
import { ManageMembersComponent } from "./manageMembers.component.ts";

import { CommonNg2Module } from "./../common/common.ts";

import { ManageMemberAddFormComponent } from "./manageMembersAddForm.component.ts"

import { ManageMembersConsortiumComponent } from "./manageMembersConsortium.component.ts"

import { ManageMembersSettingsComponent } from "./manageMembersSettings.component.ts"

import { ManageMemberAddFormSuccessComponent } from "./manageMembersAddFormSuccess.component.ts"
// This is the Angular 1 part of the module
export const ManageMembersModule = angular.module("ManageMembersModule", []);

// This is the Angular 2 part of the module
@NgModule({
  imports: [CommonNg2Module],
  declarations: [ManageMembersComponent, ManageMemberAddFormComponent, ManageMembersConsortiumComponent, ManageMembersSettingsComponent, ManageMemberAddFormSuccessComponent],
  entryComponents: [ManageMembersComponent, ManageMemberAddFormComponent, ManageMembersConsortiumComponent, ManageMembersSettingsComponent, ManageMemberAddFormSuccessComponent],
  providers: []
})
export class ManageMembersNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
ManageMembersModule.directive("manageMembersNg2", <any>downgradeComponent({
  component: ManageMembersComponent
})).directive("manageMemberAddFormNg2", <any>downgradeComponent({
  component: ManageMemberAddFormComponent
})).directive("manageMembersConsortiumNg2", <any>downgradeComponent({
  component: ManageMembersConsortiumComponent
})).directive("manageMembersSettingsNg2", <any>downgradeComponent({
  component: ManageMembersSettingsComponent
})).directive("manageMemberAddFormSuccessNg2", <any>downgradeComponent({
  component: ManageMemberAddFormSuccessComponent
}));


