import { Injectable } 
    from '@angular/core';

import { HttpClient } 
     from '@angular/common/http';


import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class SocialNetworkService {
    private headers: Headers;
    private url: string;
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
        this.url = window.location.href.split("?")[0]+".json";
    }

    checkTwitterStatus(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/manage/twitter/check-twitter-status'
        )
        .map((res:Response) => res.json()).share();
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }

    enableTwitter(  ): Observable<any> {
        
        return this.http.post( 
            getBaseUri() + '/manage/twitter',
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    disableTwitter(): Observable<any> {
        
        return this.http.post( 
            getBaseUri() + '/manage/disable-twitter',
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
}
