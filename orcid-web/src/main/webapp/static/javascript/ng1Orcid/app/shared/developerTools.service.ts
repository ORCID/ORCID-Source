import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Observable, Subject } 
    from 'rxjs';

import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class DeveloperToolsService {
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
        this.url = getBaseUri() + '/developer-tools';
    }

    notifyOther(): void {
        this.notify.next();
    }
    
    getClient(): Observable<any> {
        return this.http.post( 
            getBaseUri() + '/get-client.json',             
            { headers: this.headers }
        )        
    }
    
    enableDeveloperTools(): Observable<any> {
        return this.http.post(
            getBaseUri() + '/enable-developer-tools.json',             
            { headers: this.headers, responseType: 'text' }
        )        
    }       
}
