import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class GenericService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    private url: string;
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
        this.url = getBaseUri();
    }

    notifyOther(): void {
        this.notify.next();
    }

    getData( url_path ): Observable<any> {
        return this.http.get(
            this.url + url_path
        )
        
    }

    setData( obj, url_path ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.url + url_path, 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
}
