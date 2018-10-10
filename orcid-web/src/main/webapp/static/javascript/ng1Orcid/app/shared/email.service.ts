import { EventEmitter, Injectable, Output } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Headers, Http, RequestOptions, Response, URLSearchParams } 
    from '@angular/http';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class EmailService {

    //Broadcast events from other components that cause email list to change
    @Output() emailsChange: EventEmitter<boolean> = new EventEmitter();
    
    public delEmail: any;
    private deleteEmailHeaders: HttpHeaders;
    private emails: any;
    public emailListUpdated: boolean
    private headers: HttpHeaders;          
    public inputEmail: any;
    private notify = new Subject<any>();
    private primaryEmail: any;
    private unverifiedSetPrimary: boolean;
    private url: string;

    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.delEmail = null;
        this.emails = null;
        this.emailListUpdated = false;
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );  
        this.deleteEmailHeaders = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );    
        this.inputEmail = null;
        this.primaryEmail = null;
        this.unverifiedSetPrimary = false;
        this.url = getBaseUri() + '/account/emails.json';
    }

    addEmail( email? ): Observable<any> {
        let encoded_data;
        if( email ){
            //console.log('if', email);
            encoded_data = JSON.stringify( email );
        }else{
            //console.log('else', this.inputEmail);
            encoded_data = JSON.stringify( this.inputEmail );
            
        }
        
        return this.http.post( 
            getBaseUri() + '/account/addEmail.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .pipe(
            tap(
                (data) => {
                    this.inputEmail = data;
                    if (this.inputEmail.errors.length == 0) {
                        this.initInputEmail();
                        this.getEmails();
                    }                         
                }
            )
        )
        ;
    }

    //Send change event to subscribed components
    emailsUpdated(status) {
        this.emailListUpdated = status
        this.emailsChange.emit(this.emailListUpdated);
    }

    deleteEmail() {        
        return this.http.delete( 
            getBaseUri() + '/account/deleteEmail.json?email=' + encodeURIComponent(this.delEmail.value), { headers: this.deleteEmailHeaders }
        )
        .pipe(
            tap(
                (data) => {
                    this.getEmails();                       
                }
            )
        )
        ;
    }

    getEmailFrequencies(): Observable<any> {
        return this.http.get(
            window.location.href + '/email-frequencies.json'
        )
        .pipe(
            tap(
                (data) => {
                    this.emails = data;
                    
                    for (let i in data['emails']){
                        console.log('data[i]', data['emails'][i]);
                        if (data['emails'][i].primary == true){
                            this.primaryEmail = data['emails'][i];
                        }
                    }                                              
                }
            )
        )
        ;
    }

    getEmailPrimary(): any {
        return this.primaryEmail;
    }

    getEmails(): Observable<any> {
        return this.http.get(
            this.url
        )
        .pipe(
            tap(
                (data) => {
                    this.emails = data;
                    for (let i in data['emails']){
                        if (data['emails'][i].primary == true){
                            this.primaryEmail = data['emails'][i];
                        }
                    }                                                
                }
            )
        )
        ;
    }

    getData = this.getEmails;

    initInputEmail(): void {
        this.inputEmail = {
            "current":true,
            "errors":[],
            "primary":false,
            "value":"",
            "verified":false,
            "visibility":"PRIVATE"
        };
    }

    notifyOther(): void {
        this.notify.next();        
    }

    saveEmail( obj ): Observable<any> {
        let encoded_data = JSON.stringify( obj );
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        .pipe(
            tap(
                (data) => {
                    this.inputEmail = data;
                    if (this.inputEmail.errors.length == 0) {
                        this.initInputEmail();
                        this.getEmails();
                    }                         
                }
            )
        )
        ;
    }

    saveEmailFrequencies( obj ): Observable<any> {
        let encoded_data = JSON.stringify( obj );
        
        return this.http.post( 
            window.location.href + '/email-frequencies.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .pipe(
            tap(
                (data) => {
                    this.inputEmail = data;
                    if (this.inputEmail.errors.length == 0) {
                        this.initInputEmail();
                        this.getEmails();
                    }                         
                }
            )
        )
        ;
    }

    setData( obj ): Observable<any> {
        let encoded_data = JSON.stringify( this.emails );
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        .pipe(
            tap(
                (data) => {
                    this.inputEmail = data;
                    if (this.inputEmail.errors.length == 0) {
                        this.initInputEmail();
                        this.getEmails();
                    }                         
                }
            )
        )
        ;
    }

    setEmailPrivacy(email): Observable<any> {
        let encoded_data = JSON.stringify( email );
        
        return this.http.post( 
            getBaseUri() + '/account/email/visibility', 
            encoded_data, 
            { headers: this.headers }
        )
        ;
    }

    setPrimary(email, callback?): Observable<any> {
        let encoded_data = JSON.stringify( email );
        
        return this.http.post( 
            getBaseUri() + '/account/email/setPrimary', 
            encoded_data, 
            { headers: this.headers }
        )
        .pipe(
            tap(
                (data) => {
                    this.inputEmail = data;
                    if (this.inputEmail.errors.length == 0) {
                        this.initInputEmail();
                        this.getEmails();
                    }                         
                }
            )
        )
        ;
    }

    verifyEmail(email?): Observable<any>  {
        let _email = null; 
        if(email) {
            if(typeof email === 'string') {
                _email = email;
            } else {
                _email = email.value;
            }            
        } else {
            let primary = this.getEmailPrimary();
            if(primary) {
                _email = primary.value
            }
        }
        
        return this.http.get(
            getBaseUri() + '/account/verifyEmail.json?email=' + encodeURIComponent(_email)
        )
        ;
    }

    deleteCustomEmail( obj ): Observable<any> {
        let encoded_data = JSON.stringify( obj );
        
        return this.http.post( 
            getBaseUri() + '/group/custom-emails/delete.json', 
            encoded_data, 
            { headers: this.headers }
        )
        ;
    }

    editCustomEmail( obj ): Observable<any> {
        let encoded_data = JSON.stringify( obj );
        
        return this.http.post( 
            getBaseUri() + '/group/custom-emails/update.json', 
            encoded_data, 
            { headers: this.headers }
        )
        ;
    }

    saveCustomEmail( obj ): Observable<any> {
        let encoded_data = JSON.stringify( obj );
        
        return this.http.post( 
            getBaseUri() + '/group/custom-emails/create.json', 
            encoded_data, 
            { headers: this.headers }
        )
        ;
    }

    displayCreateForm( clientId ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/group/custom-emails/get-empty.json?clientId=' + clientId
        )
        ;
    }

    getCustomEmails( clientId ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/group/custom-emails/get.json?clientId=' + clientId
        )
        ;
    }
}
