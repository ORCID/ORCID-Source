
import {throwError as observableThrowError,  Observable, Subject } from 'rxjs';
import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { CookieXSRFStrategy, HttpModule, XSRFStrategy } 
    from '@angular/http';

import { Headers, Http, Response, RequestOptions} 
    from '@angular/http';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class BlogService {
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor(
        private http: HttpClient) {

    }

    private handleError (error: Response | any) {
        let errMsg: string;
        if (error instanceof Response) {
            const body = error.json() || '';
            const err = body.error || JSON.stringify(body);
            errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
        } else {
            errMsg = error.message ? error.message : error.toString();
        }
        console.error(errMsg);
        return observableThrowError(errMsg);
    }

    getBlogFeed(url): Observable<any> {
        return this.http.get(url, { responseType: 'text'})
        .pipe(
            catchError(this.handleError)
        );

    }

}