import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map } 
    from 'rxjs/operators';

@Injectable()
export class ClaimService {
    private headers: HttpHeaders;
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
        this.url = window.location.href.split("?")[0]+".json";
    }
    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }

    getClaim(): Observable<any> {
        return this.http.get(
            this.url
        )
        
    }

    postClaim( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    serverValidate( obj, field ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/claim' + field + 'Validate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
}
