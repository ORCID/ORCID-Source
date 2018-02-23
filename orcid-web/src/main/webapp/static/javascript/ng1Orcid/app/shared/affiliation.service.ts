import { Injectable } 
    from '@angular/core';

import { Headers, Http, RequestOptions, Response, URLSearchParams } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class AffiliationService {
    private headers: Headers;
    private urlAffiliation: string;
    private urlAffiliationId: string;
    private urlAffiliationById: string;
    private urlAffiliationDisambiguated: string;
    private urlAffiliations: string;

	public loading: boolean;
    public affiliationsToAddIds: any;

    public affiliation: any;
    public type: string;

    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();
	
    constructor( private http: Http ){
        this.affiliationsToAddIds = null,
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
        this.affiliation = null;
        this.type = '';
    }

    deleteAffiliation( data ) {     
        let options = new RequestOptions(
            { headers: this.headers }
        );

        return this.http.delete( 
            this.urlAffiliation + '?id=' + encodeURIComponent(data.putCode.value),             
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

    serverValidate( obj, relativePath ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/' + relativePath, 
            encoded_data, 
            { headers: this.headers }
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

    notifyOther(data: any): void {
        if (data) {
            this.notify.next(data);
        }
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