declare var orcidVar: any;

import { Injectable } 
    from '@angular/core';

import { CookieXSRFStrategy, HttpModule, XSRFStrategy, JsonpModule, Headers, Http, Response, RequestOptions, Jsonp } 
    from '@angular/http';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map } 
    from 'rxjs/operators';

//import { Preferences } from './preferences';

@Injectable()
export class SearchService {
    private publicApiHeaders: HttpHeaders;
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor(
        private http: HttpClient,
        private jsonp: Jsonp) {
        this.publicApiHeaders = new HttpHeaders(
            {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );

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

    getAffiliations(orcid): Observable<any> {
        var url = orcidVar.pubBaseUri + '/v2.1/' + orcid + '/activities';


        return this.http.get(url, {headers: this.publicApiHeaders}).catchError(this.handleError);
    }

    getNames(orcid): Observable<any> {
        var url = orcidVar.pubBaseUri + '/v2.1/' + orcid + '/person';


        return this.http.get(url, {headers: this.publicApiHeaders}).catchError(this.handleError);
    }


    getResults(url): Observable<any> {
        return this.http.get(url, {headers: this.publicApiHeaders})
    }


    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }

}