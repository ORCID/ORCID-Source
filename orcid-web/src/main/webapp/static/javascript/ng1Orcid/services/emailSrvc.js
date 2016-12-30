angular.module('orcidApp').factory("emailSrvc", function ($rootScope, $location, $timeout) {
    var serv = {
        emails: null,            
        delEmail: null,
        displayModalWarningFlag: false,
        inputEmail: null,
        popUp: false,
        primaryEmail: null,
        
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
                    if (callback)
                           callback();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("emailSrvc.deleteEmail() error");
            });
        },
        
        getEmails: function(callback) {
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
        },
        

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
                    if (callback)
                        callback(data);
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with multi email");
            });
        },

        setPrimary: function(email) {
            for (i in serv.emails.emails) {
                if (serv.emails.emails[i] == email) {
                    serv.emails.emails[i].primary = true;
                } else {
                    serv.emails.emails[i].primary = false;
                }
            }
            serv.saveEmail();
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
                    if (callback)
                        callback(data);
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with multi email");
            });
        }

    };

    return serv;
});