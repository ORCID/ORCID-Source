import { Injectable } 
    from '@angular/core';

import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class AffiliationService {
    private educations: any;
    private employments: any;
    private headers: Headers;
    private urlAffiliation: string;
    private urlAffiliations: string;


    constructor( private http: Http ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
        //this.url = getBaseUri() + '/affiliations/affiliations.json';
        this.educations = new Array(),
        this.employments = new Array(),
        this.urlAffiliation = getBaseUri() + '/affiliations/affiliation.json';
        this.urlAffiliations = getBaseUri() + '/affiliations/affiliations.json';
    }

    deleteData( data ) {
        let encoded_data = JSON.stringify( data );
        let arr = null;
        let idx;
        if (
            data.affiliationType != null 
            && data.affiliationType.value != null
            && data.affiliationType.value == 'education'
        ){
            arr = this.educations;
        }
        if (
            data.affiliationType != null 
            && data.affiliationType.value != null    
            && data.affiliationType.value == 'employment'){
            arr = this.employments;
        }
        for (idx in arr) {
            if (arr[idx].activePutCode == data.putCode.value) {
                break;
            }
        }
        arr.splice(idx, 1);
        
        return this.http.delete( 
            this.urlAffiliations + encoded_data, 
            //encoded_data, 
            { headers: this.headers }
        )
        .map(
            (res:Response) => res.json()
        )
        .do(
            (data) => {
                this.getData();                       
            }
        )
        .share();
    }

    /*
     deleteAffiliation: function(affiliation) {
            var arr = null;
            var idx;
            if (affiliation.affiliationType != null && affiliation.affiliationType.value != null
                    && affiliation.affiliationType.value == 'education'){
                arr = serv.educations;
            }
            if (affiliation.affiliationType != null && affiliation.affiliationType.value != null
                    && affiliation.affiliationType.value == 'employment'){
                arr = serv.employments;
            }
            for (idx in arr) {
                if (arr[idx].activePutCode == affiliation.putCode.value) {
                    break;
                }
            }
            arr.splice(idx, 1);
            $.ajax({
                url: getBaseUri() + '/affiliations/affiliations.json',
                type: 'DELETE',
                data: angular.toJson(affiliation),
                contentType: 'application/json;charset=UTF-8',
                dataType: 'json',
                success: function(data) {
                    if(data.errors.length != 0){
                        console.log("Unable to delete affiliation.");
                    }
                    $rootScope.$apply();
                }
            }).fail(function() {
                console.log("Error deleting affiliation.");
            });
        }
        */

    getData(): Observable<any> {
        return this.http.get(
            this.urlAffiliation
        )
        .map((res:Response) => res.json()).share();
    }

    setData( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.urlAffiliation, 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
}

/*
angular.module('orcidApp').factory("affiliationsSrvc", ['$rootScope', function ($rootScope) {
    var serv = {
        educations: new Array(),
        employments: new Array(),
        loading: false,
        affiliationsToAddIds: null,
        
        addAffiliationToScope: function(path) {
            if( serv.affiliationsToAddIds.length != 0 ) {
                var affiliationIds = serv.affiliationsToAddIds.splice(0,20).join();
                var url = getBaseUri() + '/' + path + '?affiliationIds=' + affiliationIds;                
                $.ajax({
                    url: url,                        
                    headers : {'Content-Type': 'application/json'},
                    method: 'GET',
                    success: function(data) {
                        for (i in data) {
                            if (data[i].affiliationType != null && data[i].affiliationType.value != null
                                    && data[i].affiliationType.value == 'education'){
                                groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,serv.educations);
                            }
                            else if (data[i].affiliationType != null && data[i].affiliationType.value != null
                                    && data[i].affiliationType.value == 'employment'){
                                groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,serv.employments);
                            }
                        };
                        if (serv.affiliationsToAddIds.length == 0) {
                            serv.loading = false;
                            $rootScope.$apply();
                        } else {
                            $rootScope.$apply();
                            setTimeout(
                                function () {
                                    serv.addAffiliationToScope(path);
                                },
                                50
                            );
                        }
                    }
                }).fail(function(e) {
                    console.log("Error adding affiliations to scope")
                    logAjaxError(e);
                });
            } else {
                serv.loading = false;
            };
        },
        setIdsToAdd: function(ids) {
            serv.affiliationsToAddIds = ids;
        },
        getAffiliations: function(path) {
            //clear out current affiliations
            serv.loading = true;
            serv.affiliationsToAddIds = null;
            serv.educations.length = 0;
            serv.employments.length = 0;
            //get affiliation ids
            $.ajax({
                url: getBaseUri() + '/' + path,
                dataType: 'json',
                success: function(data) {
                    serv.affiliationsToAddIds = data;
                    serv.addAffiliationToScope('affiliations/affiliations.json');
                    $rootScope.$apply();
                }
            }).fail(function(e){
                // something bad is happening!
                console.log("error fetching affiliations");
                logAjaxError(e);
            });
        },
        updateProfileAffiliation: function(aff) {
            $.ajax({
                url: getBaseUri() + '/affiliations/affiliation.json',
                type: 'PUT',
                data: angular.toJson(aff),
                contentType: 'application/json;charset=UTF-8',
                dataType: 'json',
                success: function(data) {
                    if(data.errors.length != 0){
                        console.log("Unable to update profile affiliation.");
                    }
                    $rootScope.$apply();
                }
            }).fail(function() {
                console.log("Error updating profile affiliation.");
            });
        },
       
    };
    return serv;
}]);
*/