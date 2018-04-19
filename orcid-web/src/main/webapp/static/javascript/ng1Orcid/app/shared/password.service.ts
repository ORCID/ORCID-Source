import { Injectable } 
    from '@angular/core';

import { HttpClient } 
     from '@angular/common/http';


import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class PasswordService {
    private headers: Headers;
    private url: string;

    constructor( private http: HttpClient ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
        this.url = getBaseUri() + '/account/nameForm.json';
    }

    getResetPasswordForm(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/password-reset.json'
        )
        .map((res:Response) => res.json()).share();
    }

    postPasswordReset( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/reset-password-email.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    serverValidate( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/reset-password-form-validate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
}
