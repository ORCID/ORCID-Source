import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';

import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class AdminActionsService {
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

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }

    findIds( obj ): Observable<any> {
        return this.http.post( 
            getBaseUri() + '/admin-actions/find-id.json', 
            encodeURI(obj), 
            { headers: this.headers }
        )        
    }
    
    resetPassword( obj ): Observable<any> {
        return this.http.post( 
            getBaseUri() + '/admin-actions/reset-password.json', 
            JSON.stringify(obj), 
            { headers: this.headers }
        )        
    }

    verifyEmail( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/admin-verify-email.json', 
                encodeURI(obj), 
                { headers: this.headers, responseType: 'text' }
        )  
    }
    
    addDelegate( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/add-delegate.json', 
                JSON.stringify(obj), 
                { headers: this.headers }
        )  
    }
     
    removeSecurityQuestion( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/remove-security-question.json', 
                encodeURI(obj), 
                { headers: this.headers, responseType: 'text' }
        )
    }
    
    validateDeprecateRequest( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/deprecate-profile/check-orcid.json',                 
                JSON.stringify(obj),
                { headers: this.headers }
            )
    }
    
    deprecateRecord( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/deprecate-profile/deprecate-profile.json', 
                JSON.stringify(obj), 
                { headers: this.headers }
        )
    };
}
