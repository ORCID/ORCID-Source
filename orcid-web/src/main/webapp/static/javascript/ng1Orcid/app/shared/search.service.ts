declare var orcidVar: any;

import { Injectable } from '@angular/core';
import { CookieXSRFStrategy, HttpModule, XSRFStrategy } from '@angular/http';
import { JsonpModule } from '@angular/http';
import { Headers, Http, Response, RequestOptions, Jsonp } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import 'rxjs/Rx';

//import { Preferences } from './preferences';

@Injectable()
export class SearchService {
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor(
        private http: HttpClient,
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

    getAffiliations(orcid): Observable<any> {
        var url = orcidVar.pubBaseUri + '/v2.1/' + orcid + '/activities';
        var options = new RequestOptions({
          headers: new Headers({
            'Accept': 'application/json'
          })
        });

        return this.http.get(url, options).map(( res: Response ) => res.json()).catch(this.handleError);
    }

    getNames(orcid): Observable<any> {
        var url = orcidVar.pubBaseUri + '/v2.1/' + orcid + '/person';
        var options = new RequestOptions({
          headers: new Headers({
            'Accept': 'application/json'
          })
        });

        return this.http.get(url, options).map(( res: Response ) => res.json()).catch(this.handleError);
    }


    getResults(url): Observable<any> {
        var options = new RequestOptions({
          headers: new Headers({
            'Accept': 'application/json'
          })
        });

        //return this.http.get(url, options).map(( res: Response ) => res.json()).catch(this.handleError);
        return this.http.get(url, options).map((res:Response) => res.json()).share();
    }

    notifyOther(): void {
        this.notify.next();
        console.log('notify');
    }

}