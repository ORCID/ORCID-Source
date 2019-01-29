import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class FundingService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    private fundingToEdit: any;
    
    public details: any;
    public groups: any;
    public loading: any;

    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json'
            }
        );
        this.details = new Array();
        this.groups = null;
        this.fundingToEdit = {};
    }

    notifyOther(data: any): void {
        if (data) {
            this.notify.next(data);
        }
    }

    consistentVis(group): boolean {
        for(let i = 0; i < group.fundings.length; i++) {
            if (group.fundings[i].visibility.visibility != group.activeVisibility) {
                return false;
            }
        }
        return true;
    }

    createNew(obj): any {
        var cloneF = JSON.parse(JSON.stringify(obj));
        cloneF.source = null;
        cloneF.putCode = null;
        for (var idx in cloneF.externalIdentifiers){
            cloneF.externalIdentifiers[idx].putCode = null;
        }
        return cloneF;
    }

    getDisambiguatedFunding( id ): Observable<any> {
        return this.http.get(
            getBaseUri() + '/fundings/disambiguated/id/' + id
        );
    }
    
    getFundingEmpty(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/fundings/funding.json'
        )
    }

    getFundingGroups(sort, sortAsc): Observable<any> {
        this.loading = true;
        return this.http.get(
            getBaseUri() + '/fundings/fundingGroups.json?sort=' + sort + '&sortAsc=' + sortAsc
        )    
    }

    getFundingImportWizardList(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/workspace/retrieve-funding-import-wizards.json'
        )
    }
    
    getFundingDetails(putCode): Observable<any> {
        this.loading = true;
        return this.http.get(
            getBaseUri() + '/fundings/fundingDetails.json?id=' + putCode
        )    
    }

    getPublicFundingDetails(putCode): Observable<any> {
        this.loading = true;
        return this.http.get(
            getBaseUri() + '/' + orcidVar.orcidId + '/fundingDetails.json?id=' + putCode
        )    
    }

    getPublicFundingGroups(sort, sortAsc): Observable<any> {
        this.loading = true;
        return this.http.get(
            getBaseUri() + '/' + orcidVar.orcidId + '/fundingGroups.json?sort=' + sort + '&sortAsc=' + sortAsc
        )
    }

    updateToMaxDisplay(group, putCode): Observable<any> {
        return this.http.get(
            getBaseUri() + '/fundings/updateToMaxDisplay.json?putCode=' + putCode
        )
    }

    putFunding(obj): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/fundings/funding.json',
            encoded_data,         
            { headers: this.headers }
        )
    }

    deleteFunding(obj): Observable<any> {
        return this.http.delete( 
            getBaseUri() + '/fundings/funding.json?id=' + encodeURIComponent(obj.putCode.value),           
            { headers: this.headers }
        )
    }

    serverValidate( obj, relativePath ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/' + relativePath, 
            encoded_data, 
            { headers: this.headers }
        );
    }

    updateVisibility(putCodes, priv): Observable<any> {
        let url = getBaseUri() + '/fundings/' + putCodes.splice(0,150).join() + '/visibility/'+priv;

        return this.http.get(
            url
        )
    }

}
