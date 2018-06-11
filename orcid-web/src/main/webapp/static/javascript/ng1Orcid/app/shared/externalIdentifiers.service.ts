import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Headers, Http, RequestOptions, Response, URLSearchParams } 
    from '@angular/http';

import { Observable, Subject } 
    from 'rxjs';

import 'rxjs/Rx';
import 'rxjs/add/operator/do';

@Injectable()
export class ExternalIdentifiersService {
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



    setExternalIdentifiersForm( obj ): Observable<any> {
        let encoded_data = JSON.stringify( obj );
        
        return this.http.post( 
            getBaseUri() + '/my-orcid/externalIdentifiers.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .do(
            (data) => {                      
            }
        )
        ;
    }



    getExternalIdentifiersForm(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/my-orcid/externalIdentifiers.json'
        )
        .do(
            (data) => {                                              
            }
        )
        ;
    }

     removeExternalIdentifier( data ) {
        /*let options = new RequestOptions(
            { headers: this.headers }
        );*/
        
        return this.http.delete( 
            getBaseUri() + '/my-orcid/externalIdentifiers.json?' + encodeURIComponent(data)
        )
        .do(
            (data) => {                   
            }
        )
        ;

    }
}
