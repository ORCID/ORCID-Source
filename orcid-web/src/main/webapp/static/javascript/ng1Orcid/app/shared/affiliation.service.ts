import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Headers, Http, RequestOptions, Response, URLSearchParams } 
    from '@angular/http';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class AffiliationService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    private urlAffiliation: string;
    private urlAffiliationId: string;
    private urlAffiliationById: string;
    private urlAffiliationDisambiguated: string;
    private urlAffiliations: string;

    public loading: boolean;
    public affiliationsToAddIds: any;
    public affiliation: any;
    public type: string;
    
    notifyObservable$ = this.notify.asObservable();
	
    constructor( private http: HttpClient ){
        this.affiliation = null;
        this.affiliationsToAddIds = null,
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );

        this.loading = true,
        this.type = '';
        this.urlAffiliation = getBaseUri() + '/affiliations/affiliation.json';
        this.urlAffiliationId = getBaseUri() + '/affiliations/affiliationIds.json';
        this.urlAffiliationById = getBaseUri() + '/affiliations/affiliations.json?affiliationIds=';
        this.urlAffiliationDisambiguated = getBaseUri() + '/affiliations/disambiguated/id/';
        this.urlAffiliations = getBaseUri() + '/affiliations/affiliations.json';
    }

    notifyOther(data: any): void {
        if (data) {
            this.notify.next(data);
        }
    }

    deleteAffiliation( data ): Observable<any> {     
        return this.http.delete( 
            this.urlAffiliation + '?id=' + encodeURIComponent(data.putCode.value),             
            { headers: this.headers }
        )
        .pipe(
            tap(
                (data) => {
                    this.getData();                       
                }
            )
        )  
        ;
    }

    getAffiliationGroups(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/affiliations/affiliationGroups.json'
        );
    }

    getPublicAffiliationGroups(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/' + orcidVar.orcidId + '/affiliationGroups.json'
        );
    }
    
    getAffiliationsId(): Observable<any> {
        this.loading = true;
        this.affiliationsToAddIds = null;
        return this.http.get(
            this.urlAffiliationId
        );       
    }

    getAffiliationsById( idList ): Observable<any> {
        return this.http.get(
            this.urlAffiliationById + idList
        );
    }

    getPublicAffiliationsById( idList ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/' + orcidVar.orcidId + '/affiliations.json?affiliationIds=' + idList
        );
    }
    
    getData(): Observable<any> {
        return this.http.get(
            this.urlAffiliation
        );
    }

    getDisambiguatedAffiliation( id ): Observable<any> {
        return this.http.get(
            this.urlAffiliationDisambiguated + id
        );
    }

    serverValidate( obj, relativePath ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/' + relativePath, 
            encoded_data, 
            { headers: this.headers }
        );
    }

    setData( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.urlAffiliation, 
            encoded_data, 
            { headers: this.headers }
        );
    }

    updateVisibility( obj ): Observable<any> {
        let encoded_data = JSON.stringify( obj );         
        
        return this.http.put(
            this.urlAffiliation,
            encoded_data,
            { headers: this.headers }
        );
    }
}