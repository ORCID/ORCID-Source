import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class ClaimService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    private url: string;
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json'
            }
        );
        this.url = window.location.href.split("?")[0]+".json";
    }
    
    notifyOther(): void {
        this.notify.next();        
    }

    getClaim(): Observable<any> {
        return this.http.get(
            this.url
        )        
    }

    postClaim( obj ): Observable<any> {
        return this.http.post( 
            this.url, 
            JSON.stringify(obj), 
            { headers: this.headers }
        )        
    }

    serverValidate( obj, field ): Observable<any> {
        return this.http.post( 
            getBaseUri() + '/claim' + field + 'Validate.json', 
            JSON.stringify(obj), 
            { headers: this.headers }
        )        
    }
}
