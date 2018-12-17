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
import { AlertBannerNg2Module } 
    from './alertBanner/alertBanner.ts';

import { AlertBannerComponent} 
    from './alertBanner/alertBanner.component.ts';

import { HomeNg2Module } 
    from './home/home.ts';

import { HomeComponent } 
    from './home/home.component.ts';

import { HeaderNg2Module } 
    from './header/header.ts';

import { HeaderComponent } 
    from './header/header.component.ts';

import { LanguageNg2Module }
    from './language/language.ts';

@NgModule({
    bootstrap: [    
        HeaderComponent, 
        AlertBannerComponent, 
        HomeComponent
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
        /* User Generated Modules */
        AlertBannerNg2Module,
        HeaderNg2Module,
        HomeNg2Module,
        LanguageNg2Module,
    ]
})

export class HomeAppModule {}