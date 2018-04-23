import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';



import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class ClaimService {
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
        this.url = window.location.href.split("?")[0]+".json";
    }

    getClaim(): Observable<any> {
        return this.http.get(
            this.url
        )
        
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
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
