//Angular imports
import 'reflect-metadata';

import { CommonModule } 
    from '@angular/common'; 

import { Component, NgModule } 
    from '@angular/core';

import { FormsModule } 
    from '@angular/forms';

import { HttpModule, JsonpModule, Request, XSRFStrategy } 
    from '@angular/http';

import { BrowserModule } 
    from "@angular/platform-browser";

import { platformBrowserDynamic } 
    from '@angular/platform-browser-dynamic';

import { RouterModule, UrlHandlingStrategy } 
    from '@angular/router';

import { UpgradeModule } 
    from '@angular/upgrade/static';

//User generated modules imports
import { BiographyNg2Module } 
    from './biography/biography.ts';
import { CountryNg2Module } 
    from './country/country.ts';
import { ModalNg2Module }
    from './modalNg2/modal-ng.ts';
import { PrivacytoggleNg2Module }
    from './privacytoggle/privacyToggle.ts';
import { WidgetNg2Module } 
    from './widget/widget.ts';
import { WorksPrivacyPreferencesNg2Module } 
    from './worksPrivacyPreferences/worksPrivacyPreferences.ts';

//User generated services
import { BiographyService } 
    from '../shared/biographyService.ts'; 
import { CommonService }
    from '../shared/commonService.ts'
import { ConfigurationService } 
    from '../shared/configurationService.ts'; 
import { EmailService } 
    from '../shared/emailService.ts'; 
import { ModalService } 
    from '../shared/modalService.ts'; 


export class MetaXSRFStrategy implements XSRFStrategy {
    constructor() {
    }

    configureRequest(req: Request): any {
        let token = document.querySelector("meta[name='_csrf']").getAttribute("content");
        let header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
        if (token && header) {
            req.headers.set(header, token);
        }
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
        /* Ng Modules */
        BrowserModule,
        CommonModule, 
        FormsModule,
        HttpModule,
        JsonpModule,
        UpgradeModule,
        /* User Generated Modules */
        BiographyNg2Module,
        CountryNg2Module,
        ModalNg2Module,
        PrivacytoggleNg2Module,
        WidgetNg2Module,
        WorksPrivacyPreferencesNg2Module
    ],
    providers: [
        { 
            provide: XSRFStrategy, 
            useClass: MetaXSRFStrategy
        },
        BiographyService,
        CommonService,
        ConfigurationService,
        EmailService,
        ModalService
    ]

})

export class Ng2AppModule {
    constructor( public upgrade: UpgradeModule ){
        console.log('v0.65');
    }
}