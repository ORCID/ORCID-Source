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
export class ReactivationService {
    private headers: HttpHeaders;
    private url: string;

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json'
            }
        );
        this.url = getBaseUri() + '/oauth/custom/authorize/get_request_info_form.json';
    }

    postReactivationConfirm( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/reactivationConfirm.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
    getReactivationData( resetParams ): Observable<any> {
        return this.http.get(getBaseUri() + '/reactivationData.json?params=' + resetParams)
    }

    serverValidate( obj, field ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        if(field == 'EmailsAdditional') {
            return this.http.post( 
                    getBaseUri() + '/reactivateAdditionalEmailsValidate.json', 
                    encoded_data, 
                    { headers: this.headers }
                )
        } else {
            return this.http.post( 
                    getBaseUri() + '/register' + field + 'Validate.json', 
                    encoded_data, 
                    { headers: this.headers }
                )
        }                
    }
}
