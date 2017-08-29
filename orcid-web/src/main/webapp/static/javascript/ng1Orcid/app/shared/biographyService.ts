declare var getBaseUri: any;

import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/Rx';

@Injectable()
export class BiographyService {
    private header: string;
    private token: string;
    private url: string;

    constructor( private http: Http ){
        this.header = $("meta[name='_csrf_header']").attr("content");
        this.token = $("meta[name='_csrf']").attr("content");
        this.url = getBaseUri() + '/account/biographyForm.json';

        console.log('header', this.header);
        console.log('token', this.token);
    }

    getBiographyData(): Observable<any> {
        return this.http.get(
            this.url
        )
        .map((res:Response) => res.json());
    }

    setBiographyData( obj ): Observable<any> {
        //let encoded_data = JSON.stringify({ obj });
        let encoded_data = JSON.stringify(obj);
        console.log('encoded_data', encoded_data);
        /*let headers = new Headers({ 
            'Content-Type': 'application/json;charset=UTF-8' 
        });*/
        let headers = new Headers({ 
            'Content-Type': 'application/json' 
        });
        let options = new RequestOptions(
            { 
                headers: headers 
            }
        );
        console.log('options 6', options);

        return this.http.post( 
            this.url, 
            encoded_data, 
            //options
            {headers: headers}
        )
        .map((res:Response) => res.json());
    }
}
