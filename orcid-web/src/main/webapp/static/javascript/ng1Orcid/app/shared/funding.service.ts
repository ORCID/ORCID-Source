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
    
    private fundingToAddIds: any;
    private headers: HttpHeaders;
    private urlFundingsById: string;
    private urlFundingsId: string;
    private fundingToEdit: any;
    
    public groups: any;
    public loading: any;

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

    getFundingsById( idList ): Observable<any> {
        this.loading = true;
        this.fundingToAddIds = null;
        //console.log('getFundingsById', this.urlFundingsById + idList);
        return this.http.get(
            this.urlFundingsById + idList
        )
    }

    getFundingsId(): Observable<any> {
        this.loading = true;
        this.fundingToAddIds = null;
        //this.groups.length = 0;
        return this.http.get(
            this.urlFundingsId
        )
        
    }

    getFundingToEdit(): any {
        return this.fundingToEdit;
    }

    makeDefault(group, putCode): Observable<any> {
        group.makeDefault(putCode);

        return this.http.get(
            getBaseUri() + '/fundings/updateToMaxDisplay.json?putCode=' + putCode
        )
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
    }

    updateProfileFunding(obj) {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/fundings/funding.json',           
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

}
