import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';


@Injectable({
    providedIn: 'root',
})
export class ShibbolethService {
    private headers: HttpHeaders;          
    private notify = new Subject<any>();

    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json'
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
    }

    init(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/shibboleth/2FA/authenticationCode.json'
        )
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }


}
