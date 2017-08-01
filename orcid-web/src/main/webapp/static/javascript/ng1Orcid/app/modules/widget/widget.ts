declare var getBaseUri: any;
declare var logAjaxError: any;

import * as angular from 'angular';
import { NgModule } from '@angular/core';
import { downgradeComponent, UpgradeModule } from '@angular/upgrade/static';

import { widgetCmp, widgetCtrl } from './widget.component.ts';

// This is the Angular 1 part of the module
export const WidgetModule = angular.module(
    'WidgetModule', 
    []
);

WidgetModule.component('widgetCmp', widgetCmp);
WidgetModule.controller('widgetCtrl', widgetCtrl);

// This is the Angular 2 part of the module
@NgModule(
    {
    }
)
export class WidgetNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
/*
widgetModule.directive(
    'messageText', 
    <any>downgradeComponent(
        {
            component: MessageTextCmp,
            inputs: ['text']
        }
    )
);
*/