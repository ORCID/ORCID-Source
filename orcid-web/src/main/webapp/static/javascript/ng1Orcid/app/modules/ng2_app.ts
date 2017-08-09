import 'reflect-metadata';

import { BrowserModule } from "@angular/platform-browser";
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { Component, NgModule } from '@angular/core';
import { RouterModule, UrlHandlingStrategy } from '@angular/router';
import { UpgradeModule } from '@angular/upgrade/static';


import { BiographyNg2Module } from './biography/biography.ts';
import { CountryNg2Module } from './country/country.ts';
import { ModalNg2Module } from './modalNg2/modal-ng.ts';
import { WidgetNg2Module } from './widget/widget.ts';

// This URL handling strategy is custom and application-specific.
// Using it we can tell the Angular 2 router to handle only URL starting with settings.
export class Ng1Ng2UrlHandlingStrategy implements UrlHandlingStrategy {
    shouldProcessUrl(url) { 
        //return url.toString().startsWith("/settings"); 
        return url;
    }
    extract(url) { 
        return url; 
    }
    merge(url, whole) { 
        return url; 
    }
}

@Component(
    {
        selector: 'root-cmp',
        template: '<div class="ng-view"></div>'
    }
) 
export class RootCmp {
}

@NgModule({
    bootstrap: [
        RootCmp
    ],
    declarations: [
        RootCmp
    ],
    imports: [
        BrowserModule,
        BiographyNg2Module,
        CountryNg2Module,
        ModalNg2Module,
        UpgradeModule,
        WidgetNg2Module
    ],
    providers: [
        { 
            provide: UrlHandlingStrategy, 
            useClass: Ng1Ng2UrlHandlingStrategy 
        }
    ]
})
export class Ng2AppModule {
    constructor( public upgrade: UpgradeModule ){}
}