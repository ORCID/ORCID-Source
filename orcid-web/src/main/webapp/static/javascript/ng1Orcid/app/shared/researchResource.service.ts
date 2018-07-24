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
export class ResearchResourceService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();

    public details: any;
    public groups: any;
    public groupsLabel: any;
    public loading: boolean;
    public offset: any;
    public orgDisambiguatedDetails: any;
    public researchResource: any;
    public showLoadMore: boolean;
    public type: string;
    
    notifyObservable$ = this.notify.asObservable();
	
    constructor( 
        private http: HttpClient,

    ){
        this.researchResource = null;
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
        
        this.details = new Array();
        this.groups = new Array();
        this.groupsLabel = null;
        this.loading = true;
        this.type = '';
        this.offset = 0;
        this.showLoadMore = false;

    }

    consistentVis(group): boolean {
        for(let i = 0; i < group.researchResources.length; i++) {
            if (group.researchResources[i].visibility != group.activeVisibility) {
                return false;
            }
        }
        return true;
    }

    deleteResearchResources(putCodes): Observable<any> {
        return this.http.delete( 
            getBaseUri() + '/research-resources/' + putCodes.splice(0,150).join(),             
            { headers: this.headers }
        ) 
    }

    getResearchResourcePage(sort, sortAsc): Observable<any> {
        this.loading = true;
        return this.http.get(
            getBaseUri() + '/research-resources/researchResourcePage.json?offset=' + this.offset + '&sort=' + sort + '&sortAsc=' + sortAsc
        );
    }

    getResearchResourceById( putCode ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/research-resources/researchResource.json?id=' + putCode
        );
    }

    getPublicResearchResourceById( putCode ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/' + orcidVar.orcidId + '/researchResource.json?id=' + putCode
        );
    }

    getPublicResearchResourcePage(sort, sortAsc): Observable<any> {
        this.loading = true;
        return this.http.get(
            getBaseUri() + '/' + orcidVar.orcidId + '/researchResourcePage.json?offset=' + this.offset + '&sort=' + sort + '&sortAsc=' + sortAsc
        );
    }

    //remove callback if not needed
    handleGroupData(data, callback?): void {
        this.groups = new Array();
        this.groups = this.groups.concat(data.groups);
        this.groupsLabel = this.groups.length + " of " + data.totalGroups;
        this.showLoadMore = this.groups.length < data.totalGroups;
        this.loading = false;
        this.offset = data.nextOffset;
        if (callback != undefined) {
            callback();
        }
    }

    notifyOther(data: any): void {
        if (data) {
            this.notify.next(data);
        }
    }

    resetGroups(): void {
        this.offset = 0;
        this.groups = new Array();
    }

    updateToMaxDisplay(putCode): Observable<any> {
        return this.http.get(
            getBaseUri() + '/research-resources/updateToMaxDisplay.json?putCode=' + putCode
        )
    }

    updateVisibility(putCodes, priv): Observable<any> {
        let url = getBaseUri() + '/research-resources/' + putCodes.splice(0,150).join() + '/visibility/'+priv;

        return this.http.get(
            url
        )
    }

}