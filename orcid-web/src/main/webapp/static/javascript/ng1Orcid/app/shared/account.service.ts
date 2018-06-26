declare var orcidSearchUrlJs: any;

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';

import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class AccountService {
    private headers: HttpHeaders;
    private publicApiHeaders: HttpHeaders;
    private notify = new Subject<any>();
    private url: string;
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
        this.publicApiHeaders = new HttpHeaders(
            {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
    }

    notifyOther(): void {
        this.notify.next();
    }

    addDelegate( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/addDelegate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    addDelegateByEmail( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            $('body').data('baseurl') + 'account/addDelegateByEmail.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    delayVerifyEmail(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/delayVerifyEmail.json'
        )
        
    }

    deprecateORCID( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);

        return this.http.post(
            getBaseUri() + '/account/validate-deprecate-profile.json',
            encoded_data, 
            { headers: this.headers }
        )   
    }

    getChangePassword(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/change-password.json'
        )
        
    }

    getDelegates(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/delegates.json'
        )
        
    }

    getDisplayName( orcid ): Observable<any> {
        return this.http.get(
            orcidVar.pubBaseUri + '/v2.1/' + orcid + '/person',
            {headers: this.publicApiHeaders}
        )
        
    }

    getResults( input ): Observable<any> {
        return this.http.get(
            orcidSearchUrlJs.buildUrl(input)+'&callback=?'
        )
        
    }

    getSecurityQuestion(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/security-question.json'
        )
        
    }

    saveChangePassword( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/change-password.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    searchByEmail( input ): Observable<any> {
        return this.http.get(
            $('body').data('baseurl') + "manage/search-for-delegate-by-email/" + encodeURIComponent(input) + '/',
        )
        
    }

    sendDeactivateEmail(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/send-deactivate-account.json'
        )
        
    }

    submitModal( obj ): Observable<any> {

        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/security-question.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
    revoke( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/revokeDelegate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
}
