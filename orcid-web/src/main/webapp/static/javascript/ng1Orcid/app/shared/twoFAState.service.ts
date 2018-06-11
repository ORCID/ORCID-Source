import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';





import { Observable, Subject } 
    from 'rxjs';

import 'rxjs/Rx';

@Injectable()
export class TwoFAStateService {
    private headers: HttpHeaders;
    private url: string;
    private urlDisable: string;
    private urlRegister: string;
    private urlStartSetup: string;
    private urlVerificationCode: string;
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
        
    }

    disable(): Observable<any> {        
        return this.http.post( 
            this.urlDisable,  
            { headers: this.headers }
        )
        
    }

    register(): Observable<any> {
        return this.http.get(
            this.urlRegister
        )
        
    }

    sendVerificationCode( obj ): Observable<any> {     
        let encoded_data = JSON.stringify(obj);

        return this.http.post( 
            this.urlVerificationCode,  
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    startSetup(): Observable<any> {
        return this.http.get(
            this.urlStartSetup
        )
        
    }

    init(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/social/2FA/authenticationCode.json'
        )
        
    }

    submitCode( obj ): Observable<any> {   
        let encoded_data = JSON.stringify(obj);

        return this.http.post( 
            getBaseUri() + '/social/2FA/submitCode.json',
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }
}
