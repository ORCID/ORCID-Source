declare var OrcidCookie: any;

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

///////////////////
import {Injectable} 
    from '@angular/core';

import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } 
    from '@angular/common/http';

import { Observable } 
    from 'rxjs';

import { HTTP_INTERCEPTORS, HttpHeaders } from '@angular/common/http';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
   constructor() {}
   
   intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
       if(request.method == 'GET') {
           return next.handle(request)
       } else {
           // Add CSRF headers for non GET requests
           const newHeaders: {[name: string]: string | string[]; } = {};
           for (const key of request.headers.keys()) {
               newHeaders[key] = request.headers.getAll(key);
           }          
           newHeaders['x-xsrf-token'] = OrcidCookie.getCookie('XSRF-TOKEN');           
           let _request = request.clone({headers: new HttpHeaders(newHeaders)});

           return next.handle(_request);
       }            
   }
}
///////////////////
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
        HeaderComponent, 
        AlertBannerComponent, 
        HomeComponent,
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
    ],
    providers: [
        { 
            provide: HTTP_INTERCEPTORS,
            useClass: TokenInterceptor,
            multi: true
        }
    ]
})

export class HomeAppModule {}