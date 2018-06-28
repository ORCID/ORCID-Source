import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { CookieXSRFStrategy, HttpModule, XSRFStrategy } 
    from '@angular/http';

import { Headers, Http, Response, RequestOptions} 
    from '@angular/http';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
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
        return Observable.throw(errMsg);
    }

    getBlogFeed(url): Observable<any> {
        return this.http.get(url, { responseType: 'text'})
        .pipe(
            catchError(this.handleError)
        );

    }

}