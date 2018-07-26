import { HttpClient, HttpClientModule, HttpHeaders, HttpParams } 
     from '@angular/common/http';

import { Injectable, ChangeDetectorRef } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class OauthService {
    private formHeaders: HttpHeaders;
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    private url: string;

    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.formHeaders = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
        this.url = getBaseUri() + '/oauth/custom/authorize/get_request_info_form.json';
    }

    notifyOther(data: any): void {
        console.log('oauth notify');
        if (data) {
            console.log('notifyOther', data);
        }
        this.notify.next(data);
    }

    authorizeRequest( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/oauth/custom/authorize.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    getAffiliations( url ): Observable<any> {
        return this.http.get(
            url
        )
        
    }

    getDuplicates( url ): Observable<any> {
        return this.http.get(
            url
        )
        
    }

    getFormData( id ): Observable<any> {
        return this.http.get(
            this.url + id
        )
        
    }

    loadAndInitAuthorizationForm( ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/oauth/custom/authorize/empty.json'
        )
        
    }

    loadRequestInfoForm( ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/oauth/custom/authorize/get_request_info_form.json'
        )
        ;
    }

    oauth2ScreensLoadRegistrationForm( ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/register.json'
        )
        
    }

    oauth2ScreensRegister( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/register.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    oauth2ScreensPostRegisterConfirm( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/registerConfirm.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    sendReactivationEmail( email ): Observable<any> {        
        const params = new HttpParams().set('email', encodeURIComponent(email)).toString();
        console.log('params:' + params)
        return this.http.post( 
            getBaseUri() + '/sendReactivation.json', 
            params, 
            { headers: this.formHeaders}
        )
        
    }

    serverValidate( obj, field ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/oauth/custom/register/validate' + field + '.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
}
