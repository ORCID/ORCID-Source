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
    private fundingToAddIds: any;
    private urlFundingsById: string;
    private urlFundingsId: string;
    private fundingToEdit: any;
    
    public groups: any;
    public loading: any;

    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
        this.fundingToAddIds = null;
        this.groups = null;
        this.urlFundingsById = getBaseUri() + '/fundings/fundings.json?fundingIds=';
        this.urlFundingsId = getBaseUri() + '/fundings/fundingIds.json';
        this.fundingToEdit = {};
    }

    notifyOther(data: any): void {
        if (data) {
            this.notify.next(data);
        }
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

    getEditable( putCode, groups ): any {
        // first check if they are the current source
        var funding = this.getFunding(putCode, groups);
        //console.log('getEditable', funding);

        if (funding.source == orcidVar.orcidId){
            return funding;
        } else {
            var bestMatch = null;
            var group = this.getGroup(putCode);
            for (var idx in group.activities) {
                if (group[idx].source == orcidVar.orcidId) {
                    //bestMatch = callback(group[idx]);
                    break;
                }
            }
            if (bestMatch == null) {
                bestMatch = this.createNew(funding);
            }
            return bestMatch;
        }

    }

    getFunding(putCode?, groups?): any {
        //console.log('getFunding', putCode, groups);
        if( putCode ){
            for (var idx in groups) {
                if (groups[idx].hasPut(putCode)){
                    return groups[idx].getByPut(putCode);
                }
            }
            return null;
            
        } else {
            this.getFundingEmpty();

        }
    }
    
    getFundingEmpty(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/fundings/funding.json'
        )
    }
    

    getFundingsById( idList ): Observable<any> {
        this.loading = true;
        return this.http.get(
            this.urlFundingsById + idList
        )
    }

    getFundingsId(): Observable<any> {
        this.loading = true;
        this.fundingToAddIds = null;
        return this.http.get(
            this.urlFundingsId
        )    
    }

    getFundingToEdit(): any {
        return this.fundingToEdit;
    }

    getGroup(putCode): any {
        for (var idx in this.groups) {
            if (this.groups[idx].hasPut(putCode)){
                return this.groups[idx];
            }
        }
        return null;
    }

    makeDefault(group, putCode): Observable<any> {
        group.makeDefault(putCode);

        return this.http.get(
            getBaseUri() + '/fundings/updateToMaxDisplay.json?putCode=' + putCode
        )
    }

    putFunding(obj) {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/fundings/funding.json',
            encoded_data,         
            { headers: this.headers }
        )
        .pipe(
            tap(
                (data) => {
                    //this.getData();
                    //groupedActivitiesUtil.rmByPut(funding.putCode.value, GroupedActivities.FUNDING,fundingSrvc.groups);                      
                }
            )
        )  
        ;
    }

    removeFunding(obj): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.delete( 
            getBaseUri() + '/fundings/funding.json?' + encoded_data,           
            { headers: this.headers }
        )
        .pipe(
            tap(
                (data) => {
                    //this.getData();
                    //groupedActivitiesUtil.rmByPut(funding.putCode.value, GroupedActivities.FUNDING,fundingSrvc.groups);                      
                }
            )
        )  
        ;
    }

    setFundingToEdit(obj): void {
        this.fundingToEdit = obj;
        console.log('setFundingToEdit service', obj);
    }

    updateProfileFunding(obj) {
        let encoded_data = JSON.stringify(obj);

        return this.http.post( 
            getBaseUri() + '/fundings/funding.json', 
            encoded_data,           
            { headers: this.headers }
        )
        .pipe(
            tap(
                (data) => {                    
                }
            )
        )  
        ;
    }

    updateVisibility(putCodes, priv): Observable<any> {
        let url = getBaseUri() + '/fundings/' + putCodes.splice(0,150).join() + '/visibility/'+priv;

        return this.http.get(
            url
        )
    }

}
