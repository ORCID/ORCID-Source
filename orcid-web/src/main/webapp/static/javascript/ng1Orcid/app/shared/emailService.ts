import { Injectable } 
    from '@angular/core';

import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class EmailService {
    private delEmail: any;
    private emails: any;            
    private inputEmail: any;
    private primaryEmail: any;
    private unverifiedSetPrimary: boolean;
    private url: string;

    constructor( private http: Http ){
        this.delEmail = null;
        this.emails = null;          
        this.inputEmail = null;
        this.primaryEmail = null;
        this.unverifiedSetPrimary = false;
        this.url = getBaseUri() + '/account/emails.json';
    }

    getEmailPrimary(): string {
        return this.primaryEmail;
    }

    getEmails(): any {
        return this.http.get(
            this.url
        )
        .map((res:Response) => res.json());
        /*
        $.ajax({
            url: getBaseUri() + '/account/emails.json',
            type: 'GET',
            dataType: 'json',
            success: function(data) { 
                serv.emails = data;
                for (var i in data.emails){
                    if (data.emails[i].primary){
                        serv.primaryEmail = data.emails[i];
                    }
                }                                                
                $rootScope.$apply();
                if (callback) {
                   callback(data);
                }
            }
        }).fail(function(e) {
            // something bad is happening!
            console.log("error with multi email");
            logAjaxError(e);
        });
        */
    }

    /* ******************* */
    getBiographyData(): Observable<any> {
        return this.http.get(
            this.url
        )
        .map((res:Response) => res.json());
    }

    setBiographyData( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        let headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
        let options = new RequestOptions(
            { 
                headers: headers 
            }
        );
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: headers }
        )
        .map((res:Response) => res.json());
    }
}

/*
angular.module('orcidApp').factory("emailSrvc", function ($rootScope, $location, $timeout) {
    var serv = {
        
        
        addEmail: function() {              
            $.ajax({
                url: getBaseUri() + '/account/addEmail.json',
                data:  angular.toJson(serv.inputEmail),
                contentType: 'application/json;charset=UTF-8',
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    serv.inputEmail = data;
                    if (serv.inputEmail.errors.length == 0) {
                        serv.initInputEmail();
                        serv.getEmails();
                    }
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with multi email");
            });
        },
        
        deleteEmail: function (callback) {
            $.ajax({
                url: getBaseUri() + '/account/deleteEmail.json',
                type: 'DELETE',
                data:  angular.toJson(serv.delEmail),
                contentType: 'application/json;charset=UTF-8',
                dataType: 'json',
                success: function(data) {
                    serv.getEmails();
                    if (callback) {
                        callback();
                    }
                }
            }).fail(function() {
                // something bad is happening!
                console.log("emailSrvc.deleteEmail() error");
            });
        },
        
        ,

        

        initInputEmail: function() {
            serv.inputEmail = {"value":"","primary":false,"current":true,"verified":false,"visibility":"PRIVATE","errors":[]};
        },

        saveEmail: function(callback) {
            $.ajax({
                url: getBaseUri() + '/account/emails.json',
                type: 'POST',
                data: angular.toJson(serv.emails),
                contentType: 'application/json;charset=UTF-8',
                dataType: 'json',
                success: function(data) {
                    serv.data;
                    $rootScope.$apply();
                    if (callback) {
                        callback(data);
                    }
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with multi email");
            });
        },

        setPrimary: function(email, callback) {
            for (var i in serv.emails.emails) {
                if (serv.emails.emails[i] == email) {
                    serv.emails.emails[i].primary = true;
                    serv.primaryEmail = email;
                    if (serv.emails.emails[i].verified == false) {
                        serv.unverifiedSetPrimary = true;
                    } else {
                        serv.unverifiedSetPrimary = false;
                    }

                    callback = function(){
                        $rootScope.$broadcast('unverifiedSetPrimary', { newValue: serv.unverifiedSetPrimary});
                    }

                } else {
                    serv.emails.emails[i].primary = false;
                }
            }
            serv.saveEmail(callback);
        },
        
        setPrivacy: function(email, priv) {
            email.visibility = priv;
            serv.saveEmail();
        },
        
        verifyEmail: function(email, callback) {
            $.ajax({
                url: getBaseUri() + '/account/verifyEmail.json',
                type: 'get',
                data:  { "email": email.value },
                contentType: 'application/json;charset=UTF-8',
                dataType: 'json',
                success: function(data) {
                    if (callback) {
                        callback(data);
                    }
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with multi email");
            });
        }

    };

    return serv;
});
*/
