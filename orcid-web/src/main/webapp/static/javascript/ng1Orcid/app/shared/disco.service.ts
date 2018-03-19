import { Injectable} 
    from '@angular/core';

import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

import { CommonNg2Module }
    from './../modules/common/common.ts';

import { WidgetService } 
    from './widget.service.ts';

@Injectable()
export class DiscoService {
    
    private headers: Headers;
    private notify = new Subject<any>();
    private widgetService: WidgetService;
    
    notifyObservable$ = this.notify.asObservable();

    
    constructor(private http: Http){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );  
    }

    getDiscoFeed(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/Shibboleth.sso/DiscoFeed'
        )
        .map((res:Response) => res.json()).share();
    };

    getIdpName(entityId, feed): any  {
        /*var 
            displayName = "",
            idp = "",
            locale = widgetService.locale != null ? widgetService.locale : "en",
            name = ""
        ;
        for(var i in feed) {
            idp = feed[i];
            if(entityId === idp.entityID) {
                name = idp.DisplayNames[0].value;
                for(j in idp.DisplayNames){
                    displayName = idp.DisplayNames[j];
                    if(locale === displayName.lang){
                        name = displayName.value;
                    }
                }
                return name;
            }
        }*/
        if(entityId === "facebook" || entityId === "google"){
            return entityId.charAt(0).toUpperCase() + entityId.slice(1);
        }
        return entityId;
    };


        /*getDiscoFeed: function() {
            $.ajax({
                url: getBaseUri() + '/Shibboleth.sso/DiscoFeed',
                dataType: 'json',
                cache: true,
                success: function(data) {
                    serv.feed = data;
                    $rootScope.$apply();
                }
            }).fail(function(e) {
                // something bad is happening!
                console.log("error with disco feed");
                logAjaxError(e);
                serv.feed = [];
                $rootScope.$apply();
            });
        },
        getIdPName: function(entityId,) {
            var 
                displayName = "",
                idp = "",
                locale = widgetSrvc.locale != null ? widgetSrvc.locale : "en",
                name = ""
            ;
            for(var i in serv.feed) {
                idp = serv.feed[i];
                if(entityId === idp.entityID) {
                    name = idp.DisplayNames[0].value;
                    for(j in idp.DisplayNames){
                        displayName = idp.DisplayNames[j];
                        if(locale === displayName.lang){
                            name = displayName.value;
                        }
                    }
                    return name;
                }
            }
            if(entityId === "facebook" || entityId === "google"){
                return entityId.charAt(0).toUpperCase() + entityId.slice(1);
            }
            return entityId;
        }
    };*/


}
