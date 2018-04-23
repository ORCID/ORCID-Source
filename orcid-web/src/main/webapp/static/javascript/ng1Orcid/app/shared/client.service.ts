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
export class ClientService {
    private headers: HttpHeaders;

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
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
        
    }

    editClient( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/group/developer-tools/edit-client.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    resetClientSecret( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/group/developer-tools/reset-client-secret.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    submitEditClient( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/group/developer-tools/edit-client.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    getClients(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/group/developer-tools/get-clients.json'
        )
        
    }

    loadAvailableScopes(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/group/developer-tools/get-available-scopes.json'
        )
        
    }

    showAddClient(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/group/developer-tools/client.json'
        )
        
    }
}
