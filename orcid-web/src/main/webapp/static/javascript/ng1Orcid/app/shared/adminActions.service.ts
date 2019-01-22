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
                'Content-Type': 'application/json'
            }
        );
    }

    notifyOther(): void {
        this.notify.next();        
    }

    adminSwitchUserValidate( obj ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/admin-actions/admin-switch-user?orcidOrEmail=' + encodeURIComponent(obj)
        );        
    };
    
    findIds( obj ): Observable<any> {
        return this.http.post( 
            getBaseUri() + '/admin-actions/find-id.json', 
            encodeURIComponent(obj), 
            { headers: this.headers }
        )        
    }
    
    resetPasswordValidate( obj ): Observable<any> {
        return this.http.post( 
            getBaseUri() + '/admin-actions/reset-password/validate', 
            JSON.stringify(obj), 
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
                encodeURIComponent(obj), 
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
                encodeURIComponent(obj), 
                { headers: this.headers, responseType: 'text' }
        )
    }
    
    validateDeprecateRequest( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/deprecate-profile/check-orcid.json',                 
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
    
    deactivateRecord( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/deactivate-records.json', 
                encodeURIComponent(obj), 
                { headers: this.headers }
        )        
    };
    
    reactivateRecord( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/reactivate-record.json', 
                JSON.stringify(obj), 
                { headers: this.headers }
        )        
    };
    
    getLockReasons() : Observable<any> {
        return this.http.get( 
                getBaseUri() + '/admin-actions/lock-reasons.json',                
                { headers: this.headers }
        )
    };
    
    lockRecords( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/lock-records.json', 
                JSON.stringify(obj), 
                { headers: this.headers }
        )
    };
    
    unlockRecords( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/unlock-records.json', 
                encodeURIComponent(obj), 
                { headers: this.headers }
        )        
    };
    
    reviewRecords( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/review-records.json', 
                encodeURIComponent(obj), 
                { headers: this.headers }
        )        
    };
    
    unreviewRecords( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/unreview-records.json', 
                encodeURIComponent(obj), 
                { headers: this.headers }
        )        
    };
    
    lookupIdOrEmails( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/lookup-id-or-emails.json', 
                encodeURIComponent(obj), 
                { headers: this.headers, responseType: 'text' }
        )
    };
    
    resendClaimEmail( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/resend-claim.json', 
                encodeURIComponent(obj), 
                { headers: this.headers }
        )
    };
    
    checkClaimedStatus( obj ): Observable<any> {
        return this.http.post( 
                getBaseUri() + '/admin-actions/check-claimed-status.json',
                encodeURIComponent(obj), 
                { headers: this.headers }
        )
    };
}
