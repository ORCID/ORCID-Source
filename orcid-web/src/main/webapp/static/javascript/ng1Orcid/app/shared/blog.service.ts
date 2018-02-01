declare var orcidVar: any;

import { Injectable } from '@angular/core';
import { CookieXSRFStrategy, HttpModule, XSRFStrategy } from '@angular/http';
import { JsonpModule } from '@angular/http';
import { Headers, Http, Response, RequestOptions, Jsonp } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import 'rxjs/Rx';
import * as xml2js from 'xml2js';

//import { Preferences } from './preferences';

@Injectable()
export class BlogService {
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor(
        private http: Http,
        private jsonp: Jsonp) {

     }

    private handleError (error: Response | any) {
        let errMsg: string;
        if (error instanceof Response) {
            const body = error.json() || '';
            const err = body.error || JSON.stringify(body);
            errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
        } else {
            errMsg = error.message ? error.message : error.toString();
        }
        console.error(errMsg);
        return Observable.throw(errMsg);
    }

    getBlogFeed(url): Observable<any> {
        
        var options = new RequestOptions({
          headers: new Headers({
            'Accept': 'application/json'
          })
        });

        //return this.http.get(url, options).map(( res: Response ) => res.json()).catch(this.handleError);

        /*return this.http.get(url, options).map(res => xml2js.parseString(res.text(), (error, result) => {
                res = result;
            }));*/
        return this.http.get(url, options).map(( res: Response ) => res.text()).catch(this.handleError);

    }

}