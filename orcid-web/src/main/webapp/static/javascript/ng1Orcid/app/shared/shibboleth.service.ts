import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';



import { Headers, Http, RequestOptions, Response, URLSearchParams } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class ShibbolethService {
    private headers: HttpHeaders;          
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

    }

    submitCode( obj ): Observable<any> {
        let encoded_data = JSON.stringify( obj );
        
        return this.http.post( 
            getBaseUri() + '/shibboleth/2FA/submitCode.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .share();
    }

    init(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/shibboleth/2FA/authenticationCode.json'
        )
        .share();
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }


}
