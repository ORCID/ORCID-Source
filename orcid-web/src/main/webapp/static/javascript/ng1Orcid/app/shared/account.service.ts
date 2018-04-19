declare var orcidSearchUrlJs: any;

import { Injectable } 
    from '@angular/core';

import { HttpClient } 
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
    private headers: Headers;
    private url: string;
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
    }

    getChangePassword(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/change-password.json'
        )
        .map((res:Response) => res.json()).share();
    }

    getSecurityQuestion(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/security-question.json'
        )
        .map((res:Response) => res.json()).share();
    }

    sendDeactivateEmail(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/send-deactivate-account.json'
        )
        .map((res:Response) => res.json()).share();
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
        .map((res:Response) => res.json()).share();
    }

    submitModal( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/security-question.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    delayVerifyEmail(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/delayVerifyEmail.json'
        )
        .map((res:Response) => res.json()).share();
    }

    addDelegate( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/addDelegate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    addDelegateByEmail( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            $('body').data('baseurl') + 'account/addDelegateByEmail.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    revoke( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/revokeDelegate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    getDelegates(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/delegates.json'
        )
        .map((res:Response) => res.json()).share();
    }

    getDisplayName( orcid ): Observable<any> {
        return this.http.get(
            orcidVar.pubBaseUri + '/v2.1/' + orcid + '/person'
        )
        .map((res:Response) => res.json()).share();
    }

    getResults( input ): Observable<any> {
        return this.http.get(
            orcidSearchUrlJs.buildUrl(input)+'&callback=?'
        )
        .map((res:Response) => res.json()).share();
    }

    searchByEmail( input ): Observable<any> {
        return this.http.get(
            $('body').data('baseurl') + "manage/search-for-delegate-by-email/" + encodeURIComponent(input) + '/',
        )
        .map((res:Response) => res.json()).share();
    }
}
