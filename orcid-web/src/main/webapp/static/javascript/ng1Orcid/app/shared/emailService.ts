import { Injectable } 
    from '@angular/core';

import { Headers, Http, RequestOptions, Response, URLSearchParams } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class EmailService {
    private delEmail: any;
    private emails: any;
    private headers: Headers;          
    private inputEmail: any;
    private primaryEmail: any;
    private unverifiedSetPrimary: boolean;
    private url: string;

    constructor( private http: Http ){
        this.delEmail = null;
        this.emails = null;
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );     
        this.inputEmail = null;
        this.primaryEmail = null;
        this.unverifiedSetPrimary = false;
        this.url = getBaseUri() + '/account/emails.json';
    }

    addEmail(): Observable<any> {
        let encoded_data = JSON.stringify( this.inputEmail );
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        .map(
            (res:Response) => res.json()
        )
        .do(
            (data) => {
                this.inputEmail = data;
                if (this.inputEmail.errors.length == 0) {
                    this.initInputEmail();
                    this.getEmails();
                }                         
            }
        )
        .share();
    }

    deleteEmail() {
        let encoded_data = JSON.stringify( this.delEmail );
        
        return this.http.delete( 
            getBaseUri() + '/account/deleteEmail.json?' + encoded_data, 
            //encoded_data, 
            { headers: this.headers }
        )
        .map(
            (res:Response) => res.json()
        )
        .do(
            (data) => {
                this.getEmails();                       
            }
        )
        .share();
    }

    getEmailPrimary(): any {
        return this.primaryEmail;
    }

    getEmails(): Observable<any> {
        return this.http.get(
            this.url
        )
        .map(
            (res:Response) => res.json()
        )
        .do(
            (data) => {
                this.emails = data;
                
                for (let i in data.emails){
                    //console.log('data.emails[i]', data.emails[i]);
                    if (data.emails[i].primary == true){
                        this.primaryEmail = data.emails[i];
                    }
                }                                                
            }
        )
        .share();
    }

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

    saveEmail(): Observable<any> {
        let encoded_data = JSON.stringify( this.emails );
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        .map(
            (res:Response) => res.json()
        )
        .do(
            (data) => {
                this.inputEmail = data;
                if (this.inputEmail.errors.length == 0) {
                    this.initInputEmail();
                    this.getEmails();
                }                         
            }
        )
        .share();
    }

    setPrimary(email, callback): void {
        for (let i in this.emails.emails) {
            if (this.emails.emails[i] == email) {
                this.emails.emails[i].primary = true;
                this.primaryEmail = email;
                if (this.emails.emails[i].verified == false) {
                    this.unverifiedSetPrimary = true;
                } else {
                    this.unverifiedSetPrimary = false;
                }

            } else {
                this.emails.emails[i].primary = false;
            }
        }
        this.saveEmail();
    }

    verifyEmail(): Observable<any>  {
        let _email = encodeURI(this.getEmailPrimary().value);
        let myParams = new URLSearchParams();
        myParams.append('email', _email);
        let options = new RequestOptions(
            { headers: this.headers , search: myParams }
        );

        return this.http.get(
            getBaseUri() + '/account/verifyEmail.json',
            options
        )
        .map(
            (res:Response) => res.json()
        )
        .do(
            (data) => {
                                             
            }
        )
        .share();
    }
}
