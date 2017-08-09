import 'reflect-metadata';

import { BrowserModule } from "@angular/platform-browser";
import { Component, NgModule } from '@angular/core';
import { RouterModule, UrlHandlingStrategy } from '@angular/router';
import { UpgradeModule } from '@angular/upgrade/static';

//import { ModalNgComponent } from '../directives/modal-ng2.component.ts';

import { BiographyNg2Module } from './biography/biography.ts';
import { CountryNg2Module } from './country/country.ts';
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

@Component({
    selector: 'root-cmp',
    /*
    //We dont have routing yet, so router-outlet is not needed
    template: `
        <router-outlet></router-outlet>
        <div class="ng-view"></div>
    `,
    */
    template: '<div class="ng-view"></div>'
}) 
export class RootCmp {
}

@NgModule({
    imports: [
        BrowserModule,
        BiographyNg2Module,
        CountryNg2Module,
        UpgradeModule,
        WidgetNg2Module
        // We don't need to provide any routes.
        // The router will collect all routes from all the registered modules.
        //RouterModule.forRoot([])
    ],
    providers: [
        { 
            provide: UrlHandlingStrategy, 
            useClass: Ng1Ng2UrlHandlingStrategy 
        }
    ],
    bootstrap: [RootCmp],
    declarations: [RootCmp]
})
export class Ng2AppModule {
    constructor( public upgrade: UpgradeModule ){}
}