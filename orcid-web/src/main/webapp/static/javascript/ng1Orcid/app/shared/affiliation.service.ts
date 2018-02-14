import { Injectable } 
    from '@angular/core';

import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class AffiliationService {
    private headers: Headers;
    private urlAffiliation: string;
    private urlAffiliationId: string;
    private urlAffiliationById: string;
    private urlAffiliationDisambiguated: string;
    private urlAffiliations: string;

	public educations: any;
    public employments: any;
    public loading: boolean;
    public affiliationsToAddIds: any;

	public educationsAndQualifications: any;

    constructor( private http: Http ){
        this.affiliationsToAddIds = null,
        this.educations = new Array(),
        this.employments = new Array(),
        this.educationsAndQualifications = new Array(),
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );
        this.loading = true,
        this.urlAffiliation = getBaseUri() + '/affiliations/affiliation.json';
        this.urlAffiliationId = getBaseUri() + '/affiliations/affiliationIds.json';
        this.urlAffiliationById = getBaseUri() + '/affiliations/affiliations.json?affiliationIds=';
        this.urlAffiliationDisambiguated = getBaseUri() + '/affiliations/disambiguated/id/';
        this.urlAffiliations = getBaseUri() + '/affiliations/affiliations.json';
    }

    deleteAffiliation( data ) {
        let encoded_data = JSON.stringify( data );
        let arr = null;
        let idx;
        
        let tmpArr = null;
        let tmpIdx = null;
        
        if(data.affiliationType != null && data.affiliationType.value != null) {
        	if (data.affiliationType.value == 'education'){
            	arr = this.educations;
	        	tmpArr = this.educationsAndQualifications;
	        } else if (data.affiliationType.value == 'employment'){
	            arr = this.employments;
	        } else if(data.affiliationType.value == 'distinction') {
	        	//TODO
	        } else if(data.affiliationType.value == 'invited-position') {
	        	//TODO
	        } else if(data.affiliationType.value == 'membership') {
	        	//TODO
	        } else if(data.affiliationType.value == 'qualification') {
	        	tmpArr = this.educationsAndQualifications;
	        } else if(data.affiliationType.value == 'service') {
	        	//TODO
	        }
        }
        
        for (idx in arr) {
            if (arr[idx].activePutCode == data.putCode.value) {
                break;
            }
        }
        
        arr.splice(idx, 1);
        
        //TODO: remove when new affiliation types get live
        if(tmpArr != null){
	        for (tmpIdx in tmpArr) {
	            if (tmpArr[tmpIdx].activePutCode == data.putCode.value) {
	                break;
	            }
	        }
	        
	        tmpArr.splice(tmpIdx, 1);        
        }
        
        
        return this.http.delete( 
            this.urlAffiliations, 
            encoded_data, 
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

    updateVisibility( affiliation ): Observable<any> {
        let encoded_data = JSON.stringify( affiliation );         
        return this.http.put(
                this.urlAffiliation,
                encoded_data,
                { headers: this.headers }
            )
            .map((res:Response) => res.json()).share();
    }
    
    getAffiliationsId() {
        this.loading = true;
        this.affiliationsToAddIds = null;
        this.educations.length = 0;
        this.employments.length = 0;
        this.educationsAndQualifications.lenght = 0;
        return this.http.get(
            this.urlAffiliationId
        )
        .map((res:Response) => res.json()).share();        
    }

    getAffiliationsById( idList ) {
        return this.http.get(
            this.urlAffiliationById + idList
        )
        .map((res:Response) => res.json()).share();
    }

    getData(): Observable<any> {
        return this.http.get(
            this.urlAffiliation
        )
        .map((res:Response) => res.json()).share();
    }

    getDisambiguatedAffiliation( id ): Observable<any> {
        return this.http.get(
            this.urlAffiliationDisambiguated + id
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
        
        ,
        setIdsToAdd: function(ids) {
            serv.affiliationsToAddIds = ids;
        },
        ,
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