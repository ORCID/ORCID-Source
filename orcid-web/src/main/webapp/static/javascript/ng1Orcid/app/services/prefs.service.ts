import { Injectable } from '@angular/core';
import { HttpModule } from '@angular/http';
import { JsonpModule } from '@angular/http';
import { Http, Response, Jsonp } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/Rx';

import { Preferences } from './preferences';

@Injectable()
export class PrefsSrvc {

    constructor(
        private http: Http,
        private jsonp: Jsonp) { }

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

    getPreferences(): Observable<Preferences[]> {
        return this.http.get( 'account/preferences.json' ).map(( res: Response ) => res.json()).catch(this.handleError);
    }
}