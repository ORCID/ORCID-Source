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
export class PeerReviewService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();

    public details: any;
    public groups: any;
    public groupsLabel: any;
    public loading: boolean;
    public offset: any;
    public orgDisambiguatedDetails: any;
    public peerReview: any;
    public showLoadMore: boolean;
    public type: string;
    
    notifyObservable$ = this.notify.asObservable();
	
    constructor( 
        private http: HttpClient,

    ){
        this.peerReview = null;
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
        for(let i = 0; i < group.peerReviews.length; i++) {
            if (group.peerReviews[i].visibility.visibility != group.activeVisibility) {
                return false;
            }
        }
        return true;
    }

    deletePeerReviews(putCodes): Observable<any> {
        return this.http.delete( 
            getBaseUri() + '/research-resources/' + putCodes.splice(0,150).join(),             
            { headers: this.headers }
        ) 
    }

    getPeerReviewPage(sort, sortAsc): Observable<any> {
        this.loading = true;
        return this.http.get(
            getBaseUri() + '/research-resources/peerReviewPage.json?offset=' + this.offset + '&sort=' + sort + '&sortAsc=' + sortAsc
        );
    }

    getPeerReviewById( putCode ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/research-resources/peerReview.json?id=' + putCode
        );
    }

    getPeerReviewImportWizardList(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/workspace/retrieve-peer-review-import-wizards.json'
        )
    }

    getPublicPeerReviewById( putCode ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/' + orcidVar.orcidId + '/peerReview.json?id=' + putCode
        );
    }

    getPublicPeerReviewPage(sort, sortAsc): Observable<any> {
        this.loading = true;
        return this.http.get(
            getBaseUri() + '/' + orcidVar.orcidId + '/peerReviewPage.json?offset=' + this.offset + '&sort=' + sort + '&sortAsc=' + sortAsc
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