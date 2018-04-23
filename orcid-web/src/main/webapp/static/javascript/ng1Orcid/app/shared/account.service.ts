declare var orcidSearchUrlJs: any;

import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';



import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class AccountService {
    private headers: HttpHeaders;
    private url: string;
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
    }

    getChangePassword(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/change-password.json'
        )
        
    }

    getSecurityQuestion(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/security-question.json'
        )
        
    }

    sendDeactivateEmail(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/send-deactivate-account.json'
        )
        
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }

    saveChangePassword( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/change-password.json', 
            encoded_data, 
            { headers: this.headers }
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

    delayVerifyEmail(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/delayVerifyEmail.json'
        )
        
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

    revoke( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/revokeDelegate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    getDelegates(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/delegates.json'
        )
        
    }

    getDisplayName( orcid ): Observable<any> {
        return this.http.get(
            orcidVar.pubBaseUri + '/v2.1/' + orcid + '/person'
        )
        
    }

    getResults( input ): Observable<any> {
        return this.http.get(
            orcidSearchUrlJs.buildUrl(input)+'&callback=?'
        )
        
    }

    searchByEmail( input ): Observable<any> {
        return this.http.get(
            $('body').data('baseurl') + "manage/search-for-delegate-by-email/" + encodeURIComponent(input) + '/',
        )
        
    }
}
