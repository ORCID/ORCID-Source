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
export class TwoFAStateService {
    private headers: Headers;
    private url: string;
    private urlDisable: string;
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: Http ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
        this.url = getBaseUri() + '/2FA/status.json';
        this.urlDisable = getBaseUri() + '/2FA/disable.json';
    }

    disable(): Observable<any> {        
        return this.http.post( 
            this.urlDisable,  
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }

    checkState(): Observable<any> {
        return this.http.get(
            this.url
        )
        .map((res:Response) => res.json()).share();
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }
}
