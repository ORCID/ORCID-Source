import { Injectable } 
    from '@angular/core';

import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class ReactivationService {
    private headers: Headers;
    private url: string;

    constructor( private http: Http ){
        this.headers = new Headers(
            { 
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
        .map((res:Response) => res.json()).share();
    }

    serverValidate( obj, field ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/register' + field + 'Validate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
}
