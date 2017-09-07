import { Injectable } from '@angular/core';
import { CookieXSRFStrategy, HttpModule, XSRFStrategy } from '@angular/http';
import { JsonpModule } from '@angular/http';
import { Headers, Http, Response, RequestOptions, Jsonp } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/Rx';

import { Preferences } from './preferences';

@Injectable()
export class PrefsSrvc {

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

    getPreferences(): Observable<Preferences[]> {
        return this.http.get( 'account/preferences.json' ).map(( res: Response ) => res.json()).catch(this.handleError);
    }

    //NOT TESTED: update email frequency
    updateEmailFrequency ( data: any ): Observable<any> {
        const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8', 'Accept': 'text/plain'});
        const options = new RequestOptions({ headers: headers });
        return this.http.post(
            getBaseUri() + '/account/email_preferences.json',
            data.email_frequency,
            options
        ).map(( res: Response ) => res.text()).catch(this.handleError);
    }

    /*updateEmailFrequency: function() {
            $.ajax({
                url: getBaseUri() + '/account/email_preferences.json',
                type: 'POST',
                data: serv.prefs['email_frequency'],
                contentType: 'application/json;charset=UTF-8',
                dataType: 'text',
                success: function(data) {                    
                    serv.saved = true;
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with prefs");
            });
    }*/

    //NOT TESTED: update notification prefs
    updateNotificationPreferences( data: any ): Observable<any> {
        const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8', 'Accept': 'application/json'});
        const options = new RequestOptions({ headers: headers });
        return this.http.post(
            getBaseUri() + '/account/notification_preferences.json',
            JSON.stringify(data),
            options
        ).map(( res: Response ) => res.text()).catch(this.handleError);
    }

    /*updateNotificationPreferences: function() {
            $.ajax({
                url: getBaseUri() + '/account/notification_preferences.json',
                type: 'POST',
                data: angular.toJson(serv.prefs),
                contentType: 'application/json;charset=UTF-8',
                dataType: 'json',
                success: function(data) {                    
                    serv.saved = true;
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with prefs");
            });
    }*/

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

    //NOT TESTED: clear message
    clearMessage(): boolean{
        return false;
    }

    /*clearMessage: function(){
            serv.saved = false;
    }*/
}