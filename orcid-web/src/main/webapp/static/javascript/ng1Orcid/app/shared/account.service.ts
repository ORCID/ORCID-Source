import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';

import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class AccountService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    private url: string;
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json'
            }
        );
    }

    notifyOther(data?): void {
        this.notify.next(data);
    }

    addDelegate( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/addDelegate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    addDelegateByEmail( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            $('body').data('baseurl') + 'account/addDelegateByEmail.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    delayVerifyEmail(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/delayVerifyEmail.json'
        )
        
    }

    deprecateORCID( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);

        return this.http.post(
            getBaseUri() + '/account/validate-deprecate-profile.json',
            encoded_data, 
            { headers: this.headers }
        )   
    }

    getChangePassword(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/change-password.json'
        )
        
    }

    getDelegates(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/delegates.json'
        )
        
    }
    
    getSocialAccounts(): Observable<any> {
        return this.http.get(
            getBaseUri()+'/account/socialAccounts.json'
        )
        
    }

    getTrustedOrgs(): Observable<any> {
        return this.http.get(
            getBaseUri()+'/account/get-trusted-orgs.json'
        )
        
    }

    saveChangePassword( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/change-password.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    searchByEmail( input ): Observable<any> {
        return this.http.get(
           getBaseUri() + '/manage/search-for-delegate-by-email/' + encodeURIComponent(input) + '/',
        )
        
    }

    searchDelegators( input ): Observable<any> {
        return this.http.get(
            getBaseUri()+'/delegators/search-for-data/' + input + '?limit=' + 10
        )       
    }

    sendDeactivateEmail(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/account/send-deactivate-account.json', {responseType: 'text'}
        )
        
    }

    submitModal( obj ): Observable<any> {

        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/account/security-question.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
    revoke( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/account/revokeDelegate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    revokeSocialAccount( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/account/revokeSocialAccount.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    revokeTrustedOrg(applicationSummary): Observable<any> {
        return this.http.post(
            getBaseUri() + '/account/revoke-application.json?tokenId='+ applicationSummary.tokenId, 
            null,
            { headers: this.headers }
        )
        
    }
    
    /**
     * Method is use to download file.
     * @param data - Array Buffer data
     * @param type - type of the document.
     */
    downloadBlogFile(url: string, dataType: string) {
        this.http.post(url, {}, {observe: 'response', responseType: 'blob'} )
        .subscribe((response) => {   
            var filename = (response.headers.get('filename') != null ? response.headers.get('filename') : 'orcid.zip');
            var blob = new Blob([response.body], { type: dataType });
            var link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);             
        });                        
    }
}
