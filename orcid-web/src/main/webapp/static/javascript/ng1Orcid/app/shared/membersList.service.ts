import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class MembersListService {
    
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    notifyObservable$ = this.notify.asObservable();
    
    private communityTypesUrl: string;
    private membersListUrl: string;
    private consortiaListUrl: string;
    private memberDetailsByIdUrl: string;    
    
    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json'
            }
        );
        this.communityTypesUrl = getBaseUri() + '/members/communityTypes.json';
        this.membersListUrl = getBaseUri() + '/members/members.json';
        this.consortiaListUrl = getBaseUri() + '/consortia/consortia.json';
        this.memberDetailsByIdUrl = getBaseUri() + '/members/details.json?memberId=';        
    }
    
    getCommunityTypes(): Observable<any> {
        return this.http.get(this.communityTypesUrl);
    }
    
    getMembersList(): Observable<any> {
        return this.http.get(this.membersListUrl);
    }
    
    getConsortiaList(): Observable<any> {
        return this.http.get(this.consortiaListUrl);
    }
    
    getMemberDetailsById(memberId: string): Observable<any> {
        const url = `${this.memberDetailsByIdUrl}${memberId}`;
        return this.http.get(url);
    }
    
}
