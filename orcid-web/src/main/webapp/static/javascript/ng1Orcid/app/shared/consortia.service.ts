import { Injectable } 
    from '@angular/core';

import { HttpClient } 
     from '@angular/common/http';


import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject } 
    from 'rxjs/Subject';

@Injectable()
export class ConsortiaService {
    
    private notify = new Subject<any>();
    notifyObservable$ = this.notify.asObservable();
    
    private headers: Headers;
    private memberDetailsUrl: string;
    private validateMemberDetailsUrl: string;
    private validateMemberDetailsFieldUrl: string;
    private updateMemberDetailsUrl: string;
    private contactsUrl: string;
    private validateContactsUrl: string;
    private updateContactsUrl: string;
    private addContactUrl: string;
    private removeContactUrl: string;
    private searchByEmailUrl: string;
    private orgIdsUrl: string;
    private validateSubMemberUrl: string;
    private validateSubMemberFieldUrl: string;
    private checkExistingSubMemberUrl: string;
    private addSubMemberUrl: string;
    private cancelSubMemberAdditionUrl: string;
    private removeSubMemberUrl: string;
    private orgIdSearchUrl: string;
    private addOrgIdUrl: string;
    private removeOrgIdUrl: string;

    constructor( private http: HttpClient ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json;charset=UTF-8' 
            }
        );
        this.memberDetailsUrl = getBaseUri() + '/self-service/get-member-details.json?accountId=';
        this.validateMemberDetailsUrl = getBaseUri() + '/self-service/validate-member-details.json';
        this.validateMemberDetailsFieldUrl = getBaseUri()  + '/self-service/validate-member-details-';
        this.updateMemberDetailsUrl = getBaseUri() + '/self-service/update-member-details.json';
        this.contactsUrl = getBaseUri() + '/self-service/get-contacts.json?accountId=';
        this.validateContactsUrl = getBaseUri() + '/self-service/validate-contacts.json';
        this.updateContactsUrl = getBaseUri() + '/self-service/update-contacts.json';
        this.addContactUrl = getBaseUri() + '/self-service/add-contact-by-email.json';
        this.removeContactUrl = getBaseUri() + '/self-service/remove-contact.json';
        this.searchByEmailUrl = getBaseUri() + '/manage/search-for-delegate-by-email/';
        this.orgIdsUrl = getBaseUri() + '/self-service/get-org-ids.json?accountId=';
        this.validateSubMemberUrl = getBaseUri() + '/self-service/validate-sub-member.json';
        this.validateSubMemberFieldUrl =  getBaseUri() + '/self-service/validate-sub-member-';
        this.checkExistingSubMemberUrl = getBaseUri() + '/self-service/check-existing-sub-member.json';
        this.addSubMemberUrl = getBaseUri() + '/self-service/add-sub-member.json';
        this.removeSubMemberUrl = getBaseUri() + '/self-service/remove-sub-member.json';
        this.cancelSubMemberAdditionUrl = getBaseUri() + '/self-service/cancel-sub-member-addition.json';
        this.orgIdSearchUrl = getBaseUri() + '/self-service/disambiguated/search?q=';
        this.addOrgIdUrl = getBaseUri() + '/self-service/add-org-id.json';
        this.removeOrgIdUrl = getBaseUri() + '/self-service/remove-org-id.json';
    }
    
    notifyOther(data: any): void {
        console.log('notify');
        if (data) {
            console.log('notifyOther', data);
        }
        this.notify.next(data);
    }
    
    getAccountIdFromPath() {
        let path = window.location.pathname;
        const basepath = '/self-service/';
        return path.substring(path.indexOf(basepath) + basepath.length);
    };

    getMemberDetails(id: string): Observable<any> {
      const url = `${this.memberDetailsUrl}${id}`;
        return this.http.get(url)
            .map((res:Response) => res.json()).share();
    }
    
    validateMemberDetails(memberDetails: object) : Observable<any>{
        let encoded_data = JSON.stringify(memberDetails);
        return this.http.post( 
            this.validateMemberDetailsUrl,
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
     validateMemberDetailsField(memberDetails: object, field: string) {
         const url = `${this.validateMemberDetailsFieldUrl}${field}.json`;
         let encoded_data = JSON.stringify(memberDetails);
         return this.http.post( 
            url, 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
    updateMemberDetails(memberDetails: object) : Observable<any>{
        let encoded_data = JSON.stringify(memberDetails);
        return this.http.post( 
            this.updateMemberDetailsUrl,
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
    getContacts(id: string): Observable<any> {
        const url = `${this.contactsUrl}${id}`;
        return this.http.get(url)
            .map((res:Response) => res.json()).share();
    }
    
    validateContacts(contacts: object) : Observable<any>{
        let encoded_data = JSON.stringify(contacts);
        return this.http.post( 
            this.validateContactsUrl,
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
    updateContacts(contacts: object) : Observable<any>{
        let encoded_data = JSON.stringify(contacts);
        return this.http.post( 
            this.updateContactsUrl,
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
    addContact(contact: object) : Observable<any>{
        let encoded_data = JSON.stringify(contact);
        return this.http.post( 
            this.addContactUrl,
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
    removeContact(contact: object) : Observable<any>{
        let encoded_data = JSON.stringify(contact);
        return this.http.post( 
            this.removeContactUrl,
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
    searchByEmail(email: string): Observable<any> {
        const url = `${this.searchByEmailUrl}/${encodeURIComponent(email)}/`;
        return this.http.get(url)
            .map((res:Response) => res.json()).share();
    }
    
    getOrgIds(id: string): Observable<any> {
        const url = `${this.orgIdsUrl}${id}`;
        return this.http.get(url)
            .map((res:Response) => res.json()).share();
    }
    
    searchOrgIds(input: string): Observable<any> {
        const url = `${this.orgIdSearchUrl}${encodeURIComponent(input)}&limit=10`;
        return this.http.get(url)
            .map((res:Response) => res.json()).share();
    }
    
    addOrgId(orgId: object) : Observable<any>{
        let encoded_data = JSON.stringify(orgId);
        return this.http.post( 
            this.addOrgIdUrl,
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
    removeOrgId(orgId: object) : Observable<any>{
        let encoded_data = JSON.stringify(orgId);
        return this.http.post( 
            this.removeOrgIdUrl,
            encoded_data, 
            { headers: this.headers }
        );
    }

    validateSubMember(subMember: object) : Observable<any>{
        let encoded_data = JSON.stringify(subMember);
        return this.http.post( 
            this.validateSubMemberUrl,
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
    validateSubMemberField(subMember: object, field: string) : Observable<any> {
         const url = `${this.validateSubMemberFieldUrl}${field}.json`;
         let encoded_data = JSON.stringify(subMember);
         return this.http.post( 
            url, 
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
    checkExistingSubMember(subMember: object) : Observable<any>{
        let encoded_data = JSON.stringify(subMember);
        return this.http.post( 
            this.checkExistingSubMemberUrl,
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
    addSubMember(subMember: object) : Observable<any>{
        let encoded_data = JSON.stringify(subMember);
        return this.http.post( 
            this.addSubMemberUrl,
            encoded_data, 
            { headers: this.headers }
        )
        .map((res:Response) => res.json()).share();
    }
    
    removeSubMember(subMember: object) : Observable<any>{
        let encoded_data = JSON.stringify(subMember);
        return this.http.post( 
            this.removeSubMemberUrl,
            encoded_data, 
            { headers: this.headers }
        );
    }
    
    cancelSubMemberAddition(subMember: object) : Observable<any>{
        let encoded_data = JSON.stringify(subMember);
        return this.http.post( 
            this.cancelSubMemberAdditionUrl,
            encoded_data, 
            { headers: this.headers }
        );
    }

}
