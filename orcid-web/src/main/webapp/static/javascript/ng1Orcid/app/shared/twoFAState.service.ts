import { Injectable } 
    from '@angular/core';

import { HttpClient } 
     from '@angular/common/http';


import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class TwoFAStateService {
    private headers: Headers;
    private url: string;
    private urlDisable: string;
    private urlRegister: string;
    private urlStartSetup: string;
    private urlVerificationCode: string;
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
        this.url = getBaseUri() + '/2FA/status.json';
        this.urlDisable = getBaseUri() + '/2FA/disable.json';
        this.urlRegister = getBaseUri() + '/2FA/register.json';
        this.urlStartSetup = getBaseUri() + '/2FA/QRCode.json';
        this.urlVerificationCode = getBaseUri() + '/2FA/register.json';
    }

    checkState(): Observable<any> {
        return this.http.get(
            this.url
        )
        .map((res:Response) => res.json()).share();
    }

    disable(): Observable<any> {        
        return this.http.post( 
            this.urlDisable,  
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    register(): Observable<any> {
        return this.http.get(
            this.urlRegister
        )
        .map((res:Response) => res.json()).share();
    }

    sendVerificationCode( obj ): Observable<any> {     
        let encoded_data = JSON.stringify(obj);

        return this.http.post( 
            this.urlVerificationCode,  
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    startSetup(): Observable<any> {
        return this.http.get(
            this.urlStartSetup
        )
        .map((res:Response) => res.json()).share();
    }

    init(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/social/2FA/authenticationCode.json'
        )
        .map((res:Response) => res.json()).share();
    }

    submitCode( obj ): Observable<any> {   
        let encoded_data = JSON.stringify(obj);

        return this.http.post( 
            getBaseUri() + '/social/2FA/submitCode.json',
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }
}
