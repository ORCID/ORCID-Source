declare var orcidVar: any;
declare var orcidSearchUrlJs: any;

import { Injectable } 
    from '@angular/core';

import { CookieXSRFStrategy, HttpModule, XSRFStrategy, JsonpModule, Headers, Http, Response, RequestOptions, Jsonp } 
    from '@angular/http';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Observable, Subject, ReplaySubject } 
    from 'rxjs';

import { catchError, map, tap, switchMap } 
    from 'rxjs/operators';

import { CommonService } 
    from './common.service.ts';       
    
@Injectable({
    providedIn: 'root',
})
export class SearchService {
    private publicApiHeaders: HttpHeaders;
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();
    
    private pubBaseUri: string;
    private searchBaseUri = new ReplaySubject<string>(1);
    
    constructor(
        private http: HttpClient,
        private commonSrvc: CommonService,
        private jsonp: Jsonp) {
        this.publicApiHeaders = new HttpHeaders(
            {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        );
        
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.pubBaseUri = data.messages['PUB_BASE_URI'];  
                this.searchBaseUri.next(data.messages['SEARCH_BASE']);
            },
            error => {
                console.log('search.component.ts: unable to fetch configInfo', error);                
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
        var url = this.pubBaseUri + '/v2.1/' + orcid + '/activities';

        return this.http.get(url, {headers: this.publicApiHeaders})
        .pipe(
            catchError(this.handleError)
        );
    }

    getNames(orcid): Observable<any> {
        var url = this.pubBaseUri + '/v2.1/' + orcid + '/personal-details';

        return this.http.get(url, {headers: this.publicApiHeaders})
        .pipe(
            catchError(this.handleError)
        );
    }

    getResults(url): Observable<any> {
        return this.searchBaseUri.pipe(
                switchMap((baseUrlString) => {                    
                    orcidSearchUrlJs.setBaseUrl(baseUrlString);
                    var theUrl = orcidSearchUrlJs.buildUrl(url);                    
                    return this.http.get(theUrl, {headers: this.publicApiHeaders})                   
                })
              )                
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }

    isValid(input): boolean {
        return orcidSearchUrlJs.isValidInput(input)
    }
    
    isValidOrcidId(input): boolean {
        return orcidSearchUrlJs.isValidOrcidId(input)
    }
}