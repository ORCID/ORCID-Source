import { Injectable } 
    from '@angular/core';

import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable, Subject } 
    from 'rxjs';

import 'rxjs/Rx';

@Injectable()
export class EmailFrequencyService {
    private headers: Headers;
    private notify = new Subject<any>();
    notifyObservable$ = this.notify.asObservable();
    email_frequencies_url: string;
    email_frequencies_change_notifications: string;
    email_frequencies_admin_notifications: string;
    email_frequencies_member_permissions_notifications: string;
    email_frequencies_tips_notifications: string;
    
    constructor( private http: Http ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
        this.email_frequencies_url = getBaseUri() + '/notifications/frequencies/view';
        this.email_frequencies_change_notifications = getBaseUri() + '/notifications/frequencies/update/amendUpdates';
        this.email_frequencies_admin_notifications = getBaseUri() + '/notifications/frequencies/update/adminUpdates';
        this.email_frequencies_member_permissions_notifications = getBaseUri() + '/notifications/frequencies/update/memberUpdates';
        this.email_frequencies_tips_notifications = getBaseUri() + '/notifications/frequencies/update/tipsUpdates';
    }
    
    getEmailFrequencies(): Observable<any> {
        return this.http.get(
                this.email_frequencies_url
            )
            .map((res:Response) => res.json());
    }   
    
    updateFrequency( name, frequency ): Observable<any> {
        let url = null;
        if(name == 'send_change_notifications') {           
            url = this.email_frequencies_change_notifications;
        } else if(name == 'send_administrative_change_notifications') {
            url = this.email_frequencies_admin_notifications;
        } else if(name == 'send_member_update_requests') {
            url = this.email_frequencies_member_permissions_notifications;
        } else if(name == 'send_quarterly_tips') {
            url = this.email_frequencies_tips_notifications;
        }
        
        return this.http.post( 
            url, 
            frequency, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json());
    }
}