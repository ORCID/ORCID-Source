import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class PasswordService {
    private headers: HttpHeaders;
    private url: string;

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json'
            }
        );
        this.url = getBaseUri() + '/account/nameForm.json';
    }

    getResetPasswordForm(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/password-reset.json'
        )
        
    }

    postPasswordReset( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/reset-password-email.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    serverValidate( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/reset-password-form-validate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
}
