import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';





import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class SocialNetworkService {
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
        this.url = window.location.href.split("?")[0]+".json";
    }

    checkTwitterStatus(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/manage/twitter/check-twitter-status'
        )
        
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
        
    }

    disableTwitter(): Observable<any> {
        
        return this.http.post( 
            getBaseUri() + '/manage/disable-twitter',
            { headers: this.headers }
        )
        
    }
}
