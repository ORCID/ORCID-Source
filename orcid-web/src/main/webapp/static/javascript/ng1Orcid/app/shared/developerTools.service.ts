import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';
     
import { Headers, Http, RequestOptions, Response, URLSearchParams } 
     from '@angular/http';

import { Observable, Subject } 
    from 'rxjs';

import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class DeveloperToolsService {
    private headers: HttpHeaders;
    private url: string;
    private notify = new Subject<any>();

    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ) {
        this.headers = new HttpHeaders(
                {
                    'Access-Control-Allow-Origin':'*',
                    'Content-Type': 'application/json'
                }
            );  
        this.url = getBaseUri() + '/developer-tools';        
    };

    notifyOther(): void {
        this.notify.next();
    };
    
    getClient(): Observable<any> {
        return this.http.get(
            this.url + '/get-client.json',             
            { headers: this.headers }
        )        
    };
    
    enableDeveloperTools(): Observable<any> {
        return this.http.post(
            this.url + '/enable-developer-tools.json', 
            {},
            { headers: this.headers }
        )        
    }; 
    
    createCredentials( obj ): Observable<any> {
        return this.http.post(
            this.url + '/create-client.json', 
            JSON.stringify(obj),
            { headers: this.headers }
        )        
    }; 
    
    updateCredentials( obj ): Observable<any> {
        return this.http.post(
            this.url + '/update-client.json', 
            JSON.stringify(obj),
            { headers: this.headers }
        )        
    };
    
    resetClientSecret( obj ): Observable<any> {
        return this.http.post(
                this.url + '/reset-client-secret.json', 
                JSON.stringify(obj),
                { headers: this.headers, responseType: 'text' }
        )        
    };
}
