import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class UnsubscribeService {
    private headers: HttpHeaders;    
    private url: string;

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
        this.url = getBaseUri() + '/unsubscribe/preferences.json';
    }

    getNotificationSettingsForm(): Observable<any> {
        var urlParts = window.location.href.split('/');
        var encryptedId = urlParts[urlParts.length -1];
        return this.http.get(
            this.url + '?id=' + encryptedId
        )
        
    }

    postNotificationSettings( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
}
