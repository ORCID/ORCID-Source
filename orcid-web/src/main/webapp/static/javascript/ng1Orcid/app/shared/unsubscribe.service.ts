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
export class UnsubscribeService {
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

    getNotificationSettingsForm(): Observable<any> {
        var urlParts = window.location.href.split('/');
        var encryptedId = urlParts[urlParts.length -1];        
        return this.http.get(
            getBaseUri() + '/unsubscribe/preferences.json' + '?id=' + encryptedId
        )
        
    }
    
    getUnsubscribeData(): Observable<any> {
        var urlParts = window.location.href.split('/');
        var encryptedId = urlParts[urlParts.length -1];        
        return this.http.get(
            getBaseUri() + '/unsubscribe/unsubscribeData.json' + '?id=' + encryptedId
        )
        
    }

    postNotificationSettings( obj ): Observable<any> {
        var urlParts = window.location.href.split('/');
        var encryptedId = urlParts[urlParts.length -1];
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/unsubscribe/preferences.json' + '?id=' + encryptedId, 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
}
