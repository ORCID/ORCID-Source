//Angular imports
import 'reflect-metadata';

import { CommonModule } 
    from '@angular/common'; 

import { HttpClientModule } 
    from '@angular/common/http';

import { Component, NgModule } 
    from '@angular/core';

import { FormsModule, ReactiveFormsModule } 
    from '@angular/forms';

import { HttpModule, JsonpModule, Request, XSRFStrategy } 
    from '@angular/http';

import { BrowserModule } 
    from "@angular/platform-browser";

import { platformBrowserDynamic } 
    from '@angular/platform-browser-dynamic';

import { RouterModule, UrlHandlingStrategy } 
    from '@angular/router';

import {NgbModule} from '@ng-bootstrap/ng-bootstrap'

//User generated modules imports
import { CommonNg2Module } from "./common/common.ts";

import { AlertBannerNg2Module } 
    from './alertBanner/alertBanner.ts';

import { AlertBannerComponent} 
    from './alertBanner/alertBanner.component.ts';

import { HeaderNg2Module } 
    from './header/header.ts';

import { HeaderComponent } 
    from './header/header.component.ts';

import { LanguageNg2Module }
    from './language/language.ts';

import { ModalNg2Module } 
    from './modalNg2/modal-ng.ts';

import { OauthAuthorizationNg2Module } 
    from './oauthAuthorization/oauthAuthorization.ts';

import { OauthAuthorizationComponent } 
    from './oauthAuthorization/oauthAuthorization.component.ts';

import { RegisterDuplicatesNg2Module } 
    from './registerDuplicates/registerDuplicates.ts';

import { RequestPasswordResetNg2Module } 
    from './requestPasswordReset/requestPasswordReset.ts';

@NgModule({
    bootstrap: [    
        HeaderComponent, 
        AlertBannerComponent, 
        OauthAuthorizationComponent,
    ],
    imports: [
        /* Ng Modules */
        BrowserModule,
        CommonModule, 
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule, //angular5
        HttpModule, //Angular2
        JsonpModule,
        NgbModule.forRoot(),
        AlertBannerNg2Module,
        CommonNg2Module,
        HeaderNg2Module,
        LanguageNg2Module,
        ModalNg2Module,
        OauthAuthorizationNg2Module,
        RegisterDuplicatesNg2Module,
        RequestPasswordResetNg2Module
    ]
})

export class SigninAppModule {}