import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';

@Injectable()
export class MembersListService {
    
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    notifyObservable$ = this.notify.asObservable();
    
    private communityTypesUrl: string;
    private membersListUrl: string;
    private consortiaListUrl: string;
    private memberDetailsBySlugUrl: string;
    private badgesUrl: string;
    
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
        this.memberDetailsBySlugUrl = getBaseUri() + '/members/detailsBySlug.json?memberSlug=';
        this.badgesUrl = getBaseUri() + '/members/badges.json';
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
    
    getMemberDetailsBySlug(slug: string): Observable<any> {
        const url = `${this.memberDetailsBySlugUrl}${slug}`;
        return this.http.get(url);
    }
    
    getBadges(): Observable<any> {
        return this.http.get(this.badgesUrl);
    }
    
}
