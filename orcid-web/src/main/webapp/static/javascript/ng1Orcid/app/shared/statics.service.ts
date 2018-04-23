import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';



import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class StaticsService {
    private headers: HttpHeaders;
    private url: string;
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

    getLiveIds(): Observable<any> {
        return this.http.get(
            getBaseUri()+'/statistics/liveids.json'
        )
        
    }
}
