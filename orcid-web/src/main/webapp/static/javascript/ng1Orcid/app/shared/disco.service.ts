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

    getIdpName(entityId, feed, locale): any  {
        var name = "";
        for(var i in feed) {
            var idp = feed[i];
            if(entityId === idp.entityID) {
                name = idp.DisplayNames[0].value;
                for(var j in idp.DisplayNames){
                    var displayName = idp.DisplayNames[j];
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
    };

}
