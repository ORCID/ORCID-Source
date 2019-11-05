declare var orcidVar: any;

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
        var url = this.pubBaseUri + '/v3.0/' + orcid + '/activities';

        return this.http.get(url, {headers: this.publicApiHeaders})
        .pipe(
            catchError(this.handleError)
        );
    }

    getNames(orcid): Observable<any> {
        var url = this.pubBaseUri + '/v3.0/' + orcid + '/personal-details';

        return this.http.get(url, {headers: this.publicApiHeaders})
        .pipe(
            catchError(this.handleError)
        );
    }

    getResults(input): Observable<any> {
        return this.searchBaseUri.pipe(
            switchMap((baseUrlString) => {
                const searchUrlAndParameters = this.buildUrl(input, baseUrlString)  
                return this.http.get(searchUrlAndParameters, {headers: this.publicApiHeaders})                   
            })
        )                   
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }

    orcidPathRegex = new RegExp("(\\d{4}-){3,}\\d{3}[\\dX]");
    orcidFullRegex = new RegExp(
            "^\\s*((http://)?([^/]*orcid\\.org|localhost.*/orcid-web)/)?(\\d{4}-){3,}\\d{3}[\\dX]\\s*$");
    quickSearchEDisMax = '{!edismax qf="given-and-family-names^50.0 family-name^10.0 given-names^5.0 credit-name^10.0 other-names^5.0 text^1.0" pf="given-and-family-names^50.0" mm=1}';

    buildUrl (input, baseUrl) {	
        if (this.hasValue(input.text)) {	
            var orcidId = this.extractOrcidId(input.text);	
            if (orcidId) {	
                // Search for iD specifically	
                return baseUrl + "?q=orcid:" + orcidId + this.offset(input);	
            }	
            // General quick search	
            return baseUrl + '?q='	
                    + encodeURIComponent(   this.quickSearchEDisMax  + input.text)	
                    + this.offset(input);	
        } else {	
            // Advanced search	
            return  this.buildAdvancedSearchUrl(input, baseUrl);	
        }	
    };


    hasValue(ref) {	
        return typeof ref !== 'undefined' && ref !== null && ref !== '';	
    }

    extractOrcidId(string: any) {
        var regexResult = this.orcidPathRegex.exec(string);
        if (regexResult) {
            return regexResult[0].toUpperCase();
        }
        return null;
    }

    offset(input: any) {
        var start = this.hasValue(input.start) ? input.start : 0;
        var rows = this.hasValue(input.rows) ? input.rows : 10;
        return '&start=' + start + '&rows=' + rows;
    };

    buildAdvancedSearchUrl(input: any, baseUrl: string) {
        var escapedAffiliationOrg;
        var escapedFamilyName;
        var escapedGivenNames;
        var escapedGridOrg;
        var escapedKeyword;
        var query = '';
        var doneSomething = false;
        if (this.hasValue(input.givenNames)) {
            escapedGivenNames = this.escapeReservedChar(input.givenNames);
            query += 'given-names:' + escapedGivenNames;
            doneSomething = true;
        }
        if (this.hasValue(input.familyName)) {
            if (doneSomething) {
                query += ' AND ';
            }
            escapedFamilyName = this.escapeReservedChar(input.familyName);
            query += 'family-name:' + escapedFamilyName;
            doneSomething = true;
        }
        if (this.hasValue(input.searchOtherNames) && this.hasValue(input.givenNames)) {
            query += ' OR other-names:' + escapedGivenNames;
        }
        if (this.hasValue(input.keyword)) {
            if (doneSomething) {
                query += ' AND ';
            }
            escapedKeyword = this.escapeReservedChar(input.keyword);
            query += 'keyword:' + escapedKeyword;
            doneSomething = true;
        }
        if (this.hasValue(input.affiliationOrg)) {
            if (doneSomething) {
                query += ' AND ';
            }

            //if all chars are numbers, assume it's a ringgold id
            if (input.affiliationOrg.match(/^[0-9]*$/)) {
                query += 'ringgold-org-id:' + input.affiliationOrg;
            } else if(input.affiliationOrg.startsWith('grid.')) {
                escapedGridOrg = this.escapeReservedChar(input.affiliationOrg);
                query += 'grid-org-id:' + escapedGridOrg;
            } else {
                escapedAffiliationOrg = this.escapeReservedChar(input.affiliationOrg);
                query += 'affiliation-org-name:' + escapedAffiliationOrg;
            }
            doneSomething = true;
        }

        return doneSomething ? baseUrl + '?q=' + encodeURIComponent(query)
                + this.offset(input) : '?q=';
    }

    
    escapeReservedChar(inputText: any){
        //escape all reserved chars except double quotes
        //per https://lucene.apache.org/solr/guide/6_6/the-standard-query-parser.html#TheStandardQueryParser-EscapingSpecialCharacters
        var escapedText = inputText.replace(/([!^&*()+=\[\]\\/{}|:?~])/g, "\\$1");
        return escapedText.toLowerCase();
    }

    isValidOrcidId(input): any{
        if(typeof input.text === 'undefined' || input.text === null || input.text === '' || this.orcidFullRegex.exec(input.text.toUpperCase())){
            return true;
        }
        return false;
    }

    isValid = function(input) {
        var fieldsToCheck = [ input.text, input.givenNames, input.familyName,	
                input.keyword, input.affiliationOrg ];	
        for ( var i = 0; i < fieldsToCheck.length; i++) {	
            if (this.hasValue(fieldsToCheck[i])) {	
                return true;	
            }	
        }	
        return false;	
    };
}