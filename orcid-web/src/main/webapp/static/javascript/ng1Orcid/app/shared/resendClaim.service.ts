import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Observable, Subject } 
    from 'rxjs';

import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class ResendClaimService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    private url: string;
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            }
        );
        this.url = getBaseUri() + '/resend-claim.json';
    }
    
    notifyOther(): void {
        this.notify.next();        
    }

    post( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )        
    }    
}
