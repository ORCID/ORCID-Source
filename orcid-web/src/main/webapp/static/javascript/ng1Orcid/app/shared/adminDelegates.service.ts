import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';



import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class AdminDelegatesService {
    private headers: HttpHeaders;
    private url: string;
    private urlConfirmDelegate: string;
    private urlDeactivateProfile: string;
    private urlVerifyEmail: string;

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
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
        
    }

    findIds( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri()+'/admin-actions/find-id.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    lookupIdOrEmails( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri()+'/admin-actions/lookup-id-or-emails.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    getFormData( id ): Observable<any> {
        return this.http.get(
            this.url + id
        )
        
    }

    setFormData( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    verifyEmail( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
}
