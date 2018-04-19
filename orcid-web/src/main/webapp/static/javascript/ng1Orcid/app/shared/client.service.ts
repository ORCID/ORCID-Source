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
export class ClientService {
    private headers: Headers;

    constructor( private http: HttpClient ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
    }

    addClient( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/group/developer-tools/add-client.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    editClient( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/group/developer-tools/edit-client.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    resetClientSecret( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/group/developer-tools/reset-client-secret.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    submitEditClient( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/group/developer-tools/edit-client.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    getClients(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/group/developer-tools/get-clients.json'
        )
        .map((res:Response) => res.json()).share();
    }

    loadAvailableScopes(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/group/developer-tools/get-available-scopes.json'
        )
        .map((res:Response) => res.json()).share();
    }

    showAddClient(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/group/developer-tools/client.json'
        )
        .map((res:Response) => res.json()).share();
    }
}
