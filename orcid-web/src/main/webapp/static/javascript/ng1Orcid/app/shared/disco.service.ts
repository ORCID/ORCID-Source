import { Injectable} 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Observable, Subject } 
    from 'rxjs';

import { catchError, map, tap } 
    from 'rxjs/operators';

import { CommonNg2Module }
    from './../modules/common/common.ts';

import { WidgetService } 
    from './widget.service.ts';

@Injectable()
export class DiscoService {
    
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    private widgetService: WidgetService;
    
    notifyObservable$ = this.notify.asObservable();
    
    constructor(private http: HttpClient){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json'
            }
        );  
    }

    getDiscoFeed(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/Shibboleth.sso/DiscoFeed'
        )
        
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
        return entityId;
    };

}
