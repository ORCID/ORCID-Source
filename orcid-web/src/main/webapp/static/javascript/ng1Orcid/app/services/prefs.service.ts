declare var getBaseUri: any;

import { Injectable } from '@angular/core';
import { CookieXSRFStrategy, HttpModule, XSRFStrategy } from '@angular/http';
import { JsonpModule } from '@angular/http';
import { Headers, Http, Response, RequestOptions, Jsonp } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/Rx';

import { Preferences } from './preferences';

@Injectable()
export class PrefsSrvc {
    
    saved: boolean;

    constructor(
        private http: Http,
        private jsonp: Jsonp) {

            this.saved = false;

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

    getPreferences(): Observable<Preferences[]> {
        return this.http.get( 'account/preferences.json' ).map(( res: Response ) => res.json()).catch(this.handleError);
    }

    //TODO: update email frequency

    //TODO: update notification prefs

    //TODO: update default visibility

    updateDefaultVisibility( data: any ): Observable<any> {
        const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8', 'Accept': 'text/plain'});
        const options = new RequestOptions({ headers: headers });
        return this.http.post(
            getBaseUri() + '/account/default_visibility.json',
            data.default_visibility,
            options
        ).map(( res: Response ) => res.text()).catch(this.handleError);


    }

    /*updateDefaultVisibility: function() {
            $.ajax({
                url: getBaseUri() + '/account/default_visibility.json',
                type: 'POST',
                data: serv.prefs['default_visibility'],
                contentType: 'application/json;charset=UTF-8',
                dataType: 'text',
                success: function(data) {                    
                    serv.saved = true;
                    $rootScope.$apply();
                }
            }).fail(function(jqXHR, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
                console.log(jqXHR);
                // something bad is happening!
                console.log("error with prefs");
            });
        }*/ 

    //TODO: clear message
}