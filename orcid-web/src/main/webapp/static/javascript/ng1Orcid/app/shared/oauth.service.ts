import { Injectable, ChangeDetectorRef } 
    from '@angular/core';

import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject } 
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class OauthService {
    private formHeaders: Headers;
    private headers: Headers;
    private notify = new Subject<any>();
    private url: string;

    notifyObservable$ = this.notify.asObservable();

    constructor( private http: Http ){
        this.formHeaders = new Headers({'Content-Type': 'application/x-www-form-urlencoded'});
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
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
        .map((res:Response) => res.json()).share();
    }

    getAffiliations( url ): Observable<any> {
        return this.http.get(
            url
        )
        .map((res:Response) => res.json()).share();
    }

    getDuplicates( url ): Observable<any> {
        return this.http.get(
            url
        )
        .map((res:Response) => res.json()).share();
    }

    getFormData( id ): Observable<any> {
        return this.http.get(
            this.url + id
        )
        .map((res:Response) => res.json()).share();
    }

    loadAndInitAuthorizationForm( ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/oauth/custom/authorize/empty.json'
        )
        .map((res:Response) => res.json()).share();
    }

    loadRequestInfoForm( ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/oauth/custom/authorize/get_request_info_form.json'
        )
        .share();
    }

    oauth2ScreensLoadRegistrationForm( ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/register.json'
        )
        .map((res:Response) => res.json()).share();
    }

    oauth2ScreensRegister( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/register.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    oauth2ScreensPostRegisterConfirm( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/registerConfirm.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    sendReactivationEmail( email ): Observable<any> {
        let data = 'email=' + encodeURIComponent(email);
        return this.http.post( 
            getBaseUri() + '/sendReactivation.json', 
            data, 
            { headers: this.formHeaders}
        )
        .map((res:Response) => res.json()).share();
    }

    sendEmailsAdditionalReactivationEmail( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/sendReactivation.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    serverValidate( obj, field ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/oauth/custom/register/validate' + field + '.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
}
