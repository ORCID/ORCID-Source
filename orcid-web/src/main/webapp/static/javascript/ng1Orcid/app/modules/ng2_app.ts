import 'reflect-metadata';

import { BrowserModule } from "@angular/platform-browser";
import { CommonModule } from '@angular/common';
import { Component, NgModule } from '@angular/core';
import { HttpModule, Request, XSRFStrategy } from '@angular/http';
import { JsonpModule } from '@angular/http';
import { RouterModule, UrlHandlingStrategy } from '@angular/router';
import { UpgradeModule } from '@angular/upgrade/static';

import { BiographyNg2Module } from './biography/biography.ts';
import { WidgetNg2Module } from './widget/widget.ts';
import { WorksPrivacyPreferencesNg2Module } from './worksPrivacyPreferences/worksPrivacyPreferences.ts';

// This URL handling strategy is custom and application-specific.
// Using it we can tell the Angular 2 router to handle only URL starting with settings.
export class Ng1Ng2UrlHandlingStrategy implements UrlHandlingStrategy {
    shouldProcessUrl(url) { 
        return url.toString().startsWith("/settings"); 
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

export class MetaXSRFStrategy implements XSRFStrategy {
    constructor(

    ) { 


    }

  configureRequest(req: Request): void {
    var token = document.querySelector("meta[name='_csrf']").getAttribute("content");
    var header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
    //this._headerName = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
    //var xsrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    if (token && header) {
      req.headers.set(header, token);
    }
  }
}

/*export class CookieXSRFStrategy implements XSRFStrategy {
  constructor(
      private _cookieName: string = 'XSRF-TOKEN', private _headerName: string = 'X-XSRF-TOKEN') {}

  configureRequest(req: Request): void {
    const xsrfToken = getDOM().getCookie(this._cookieName);
    if (xsrfToken) {
      req.headers.set(this._headerName, xsrfToken);
    }
  }
}*/

@NgModule({
    imports: [
        BrowserModule,
        CommonModule,
        HttpModule,
        JsonpModule,
        UpgradeModule,
        BiographyNg2Module,
        WidgetNg2Module,
        WorksPrivacyPreferencesNg2Module
        // We don't need to provide any routes.
        // The router will collect all routes from all the registered modules.
        //RouterModule.forRoot([])
    ],
    providers: [
        { 
            provide: UrlHandlingStrategy, 
            useClass: Ng1Ng2UrlHandlingStrategy 
        },
        { 
            provide: XSRFStrategy, 
            //useValue: new CookieXSRFStrategy('_csrf', '_csrf_header')
            useClass: MetaXSRFStrategy

        },

    ],

    bootstrap: [RootCmp],
    declarations: [RootCmp],

})

export class Ng2AppModule {
    constructor( public upgrade: UpgradeModule ){}
}