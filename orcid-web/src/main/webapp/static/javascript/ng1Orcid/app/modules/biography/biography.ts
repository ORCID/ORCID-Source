declare var getBaseUri: any;
declare var logAjaxError: any;

import * as angular from 'angular';
import { NgModule } from '@angular/core';
import { downgradeComponent, UpgradeModule } from '@angular/upgrade/static';

import { biographyService } from './biography.service.ts';
import { biographyCmp, BiographyCtrl } from './biography.component.ts';


export function exportRepository(m: UpgradeModule): biographyService {
    return m.$injector.get('biographyService');
};

// This is the Angular 1 part of the module
export const BiographyModule = angular.module(
    'BiographyModule', 
    []
);

BiographyModule.service('biographyService', biographyService);
BiographyModule.controller('BiographyCtrl', BiographyCtrl);
BiographyModule.component('biographyCmp', biographyCmp);


// This is the Angular 2 part of the module
@NgModule(
    {
        providers: [
            {
                deps: [
                    UpgradeModule
                ],
                provide: biographyService, 
                useFactory: exportRepository
            }
        ]
    }
)
export class BiographyNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
/*
BiographyModule.directive(
    'messageText', 
    <any>downgradeComponent(
        {
            component: MessageTextCmp,
            inputs: ['text']
        }
    )
);
*/