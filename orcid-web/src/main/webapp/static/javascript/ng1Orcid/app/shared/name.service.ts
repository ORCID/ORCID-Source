import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';



import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class NameService {
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
        this.url = getBaseUri() + '/account/nameForm.json';
    }

    getData(): Observable<any> {
        return this.http.get(
            this.url
        )
        
    }

    setData( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
}
