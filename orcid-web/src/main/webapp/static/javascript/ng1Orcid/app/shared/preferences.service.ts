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
export class PreferencesService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    public prefs: any;
    public saved: boolean;
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
        this.prefs = null;
        this.saved = false;
        this.url = getBaseUri() + '/account/preferences.json';
    }

    getPrivacyPreferences(): Observable<any> {
        return this.http.get(
            this.url
        )
        
    }

    updateEmailFrequency( prefs ): Observable<any> {
        console.log('updateEmailFrequency is deprecated and does not work anymore');
        return null;
    }

    updateNotificationPreferences(): Observable<any>  {
        console.log('updateNotificationPreferences is deprecated and does not work anymore');        
        return null;
    }

    updateDefaultVisibility(newPriv): Observable<any> {
        return this.http.post( 
            getBaseUri() + '/account/default_visibility.json', 
            newPriv, 
            { headers: this.headers }
        )
        .share();
    }

    clearMessage(): void {
        this.saved = false;
    }
}

/*
angular.module('orcidApp').factory("prefsSrvc", function ($rootScope) {
    var serv = {
        prefs: null,
        saved: false,
        getPrivacyPreferences: function() {            
            $.ajax({
                url: getBaseUri() + '/account/preferences.json',
                dataType: 'json',
                success: function(data) {
                    serv.prefs = data;
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with prefs");
            });
        },
        updateEmailFrequency: function() {
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
        }, 
        updateNotificationPreferences: function() {
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
        },
        updateDefaultVisibility: function() {
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
        }, 
        clearMessage: function(){
            serv.saved = false;
        }
    };

    // populate the prefs
    serv.getPrivacyPreferences();

    return serv; 
});
*/