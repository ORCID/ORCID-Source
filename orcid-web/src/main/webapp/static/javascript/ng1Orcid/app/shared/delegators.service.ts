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
export class DelegatorsService {
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
    }


    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }


    getDelegators(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/delegators/delegators-and-me.json'
        )
        .map((res:Response) => res.json()).share();
    }

}
