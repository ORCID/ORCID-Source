import { Injectable } 
    from '@angular/core';

import { HttpClient } 
     from '@angular/common/http';


import { Headers, Http, RequestOptions, Response, URLSearchParams } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class ExternalIdentifiersService {
    private headers: Headers;
    private notify = new Subject<any>();

    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){

        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
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
        .map(
            (res:Response) => res.json()
        )
        .do(
            (data) => {                      
            }
        )
        .share();
    }



    getExternalIdentifiersForm(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/my-orcid/externalIdentifiers.json'
        )
        .map(
            (res:Response) => res.json()
        )
        .do(
            (data) => {                                              
            }
        )
        .share();
    }

     removeExternalIdentifier( data ) {
        let options = new RequestOptions(
            { headers: this.headers }
        );
        
        return this.http.delete( 
            getBaseUri() + '/my-orcid/externalIdentifiers.json?' + encodeURIComponent(data),             
            options
        )
        .map(
            (res:Response) => res.json()
        )
        .do(
            (data) => {                   
            }
        )
        .share();

    }
}
