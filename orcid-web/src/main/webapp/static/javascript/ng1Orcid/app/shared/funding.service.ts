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
    
    private details: any;
    private fundingToAddIds: any;
    private headers: HttpHeaders;
    private urlFundingsById: string;
    private urlFundingsId: string;
    private fundingToEdit: any;
    
    public groups: any;
    public loading: any;

    constructor( private http: HttpClient ){
        this.details = null;
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

    getFunding(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/fundings/funding.json'
        )
    }

    getFundingByPutCode(putCode): any {
        for (let j in this.groups) {
            for (var k in this.groups[j].fundings) {
                if (this.groups[j].fundings[k].putCode.value == putCode) {
                    return this.groups[j].fundings[k];
                }
            }
        }
        return null;
    }

    loadAllFundingGroups(sort, sortAsc, callback): any {
        this.details = new Object();
        this.groups = new Array();
        
        let url = getBaseUri() + '/works/allWorks.json?sort=' + sort + '&sortAsc=' + sortAsc;
        this.loading = true;

        return this.http.get(
            url
        )
        .pipe(
            tap(
                (data) => {
                    this.handleWorkGroupData(data, callback);                     
                }
            )
        );
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

    getGroup(putCode): any {
        for (var idx in this.groups) {
            for (var y in this.groups[idx].works) {
                if (this.groups[idx].works[y].putCode.value == putCode) {
                    return this.groups[idx];
                }
            }
        }
        return null;
    }

    getGroupDetails(putCode, type, callback?): void {
        let group = this.getGroup(putCode);
        let needsLoading =  new Array();
        
        let popFunct = function () {
            if (needsLoading.length > 0) {
                this.getDetails(needsLoading.pop(), type, popFunct);
            }
            else if (callback != undefined) {
                callback();
            }
        };

        for (var idx in group.works) {
            needsLoading.push(group.works[idx].putCode.value)
        }

        popFunct();
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
        console.log('setFundingToEdit service', obj);
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
