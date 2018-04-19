import { Injectable } 
    from '@angular/core';

import { HttpClient } 
     from '@angular/common/http';


import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class AdminDelegatesService {
    private headers: Headers;
    private url: string;
    private urlConfirmDelegate: string;
    private urlDeactivateProfile: string;
    private urlVerifyEmail: string;

    constructor( private http: HttpClient ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
        this.url = getBaseUri() + '/admin-actions/admin-delegates/check-claimed-status.json?orcidOrEmail=';
        this.urlConfirmDelegate = getBaseUri()+'/admin-actions/admin-delegates';
        this.urlDeactivateProfile = getBaseUri()+'/admin-actions/deactivate-profiles.json';
        this.urlVerifyEmail = getBaseUri()+'/admin-actions/admin-verify-email.json';
    }

    deactivateOrcids( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    findIds( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri()+'/admin-actions/find-id.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    lookupIdOrEmails( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri()+'/admin-actions/lookup-id-or-emails.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    getFormData( id ): Observable<any> {
        return this.http.get(
            this.url + id
        )
        .map((res:Response) => res.json()).share();
    }

    setFormData( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    verifyEmail( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
}
