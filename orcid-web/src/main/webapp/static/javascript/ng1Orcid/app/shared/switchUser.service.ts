import { Injectable } 
    from '@angular/core';

import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class SwitchUserService {
    private headers: Headers;
    private url: string;
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: Http ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
    }

    getDelegates(): Observable<any> {
        return this.http.get(
            getBaseUri()+'/delegators/delegators-and-me.json'
        )
        .map((res:Response) => res.json()).share();
    }

    searchDelegates(searchTerm): Observable<any> {
        return this.http.get(
            getBaseUri()+'/delegators/search/'+encodeURIComponent(searchTerm) + '?limit=10'
        )
        .map((res:Response) => res.json()).share();
    }

    switchUser(targetOrcid): Observable<any> {
        return this.http.post( 
            getBaseUri() + '/switch-user?username=' + targetOrcid, 
            { headers: this.headers }
        )
        .share();
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }
}