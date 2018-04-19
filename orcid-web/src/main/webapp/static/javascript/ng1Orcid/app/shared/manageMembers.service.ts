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
export class ManageMembersService {
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

    updateConsortium( obj ): Observable<any> {
        let encoded_data = JSON.stringify( obj );
        
        return this.http.post( 
            getBaseUri()+'/manage-members/update-consortium.json', 
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

    findConsortium( obj ): Observable<any> {
        let encoded_data = JSON.stringify( obj );
        return this.http.get(
            getBaseUri()+'/manage-members/find-consortium.json?id=' + encoded_data
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

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }


}
