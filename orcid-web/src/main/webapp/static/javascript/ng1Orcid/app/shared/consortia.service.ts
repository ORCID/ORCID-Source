import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';

@Injectable()
export class ConsortiaService {
    
    private notify = new Subject<any>();
    notifyObservable$ = this.notify.asObservable();
    
    private accountIdRegExp = new RegExp('/self-service/([^/?]+)');
    
    private addContactUrl: string;
    private addOrgIdUrl: string;
    private addSubMemberUrl: string;
    private cancelSubMemberAdditionUrl: string;
    private checkExistingSubMemberUrl: string;
    private contactsUrl: string;
    private subMemberContactsUrl: string;
    private headers: HttpHeaders;
    private memberDetailsUrl: string;
    private removeContactUrl: string;
    private searchByEmailUrl: string;
    private updateContactsUrl: string;
    private updateMemberDetailsUrl: string;
    private validateContactsUrl: string;
    private validateMemberDetailsUrl: string;
    private validateMemberDetailsFieldUrl: string;
    private validateSubMemberFieldUrl: string;
    private validateSubMemberUrl: string;
    private orgIdSearchUrl: string;
    private orgIdsUrl: string;
    private removeOrgIdUrl: string;
    private removeSubMemberUrl: string;

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
        this.memberDetailsUrl = getBaseUri() + '/self-service/get-member-details.json?accountId=';
        this.validateMemberDetailsUrl = getBaseUri() + '/self-service/validate-member-details.json';
        this.validateMemberDetailsFieldUrl = getBaseUri()  + '/self-service/validate-member-details-';
        this.updateMemberDetailsUrl = getBaseUri() + '/self-service/update-member-details.json';
        this.contactsUrl = getBaseUri() + '/self-service/get-contacts.json?accountId=';
        this.subMemberContactsUrl = getBaseUri() + '/self-service/get-sub-member-contacts.json?accountId=';
        this.validateContactsUrl = getBaseUri() + '/self-service/validate-contacts.json';
        this.updateContactsUrl = getBaseUri() + '/self-service/update-contacts.json';
        this.addContactUrl = getBaseUri() + '/self-service/add-contact-by-email.json';
        this.removeContactUrl = getBaseUri() + '/self-service/remove-contact.json';
        this.searchByEmailUrl = getBaseUri() + '/manage/search-for-delegate-by-email';
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
        let results: Array<any> = this.accountIdRegExp.exec(path);
        return results[1];
    };

    getMemberDetails(id: string): Observable<any> {
      const url = `${this.memberDetailsUrl}${id}`;
        return this.http.get(url)
            
    }
    
    validateMemberDetails(memberDetails: object) : Observable<any>{
        let encoded_data = JSON.stringify(memberDetails);
        return this.http.post( 
            this.validateMemberDetailsUrl,
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
     validateMemberDetailsField(memberDetails: object, field: string) {
         const url = `${this.validateMemberDetailsFieldUrl}${field}.json`;
         let encoded_data = JSON.stringify(memberDetails);
         return this.http.post( 
            url, 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
    updateMemberDetails(memberDetails: object) : Observable<any>{
        let encoded_data = JSON.stringify(memberDetails);
        return this.http.post( 
            this.updateMemberDetailsUrl,
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
    getContacts(id: string): Observable<any> {
        const url = `${this.contactsUrl}${id}`;
        return this.http.get(url)
            
    }
    
    getSubMemberContacts(id: string): Observable<any> {
        const url = `${this.subMemberContactsUrl}${id}`;
        return this.http.get(url)
            
    }
    
    validateContacts(contacts: object) : Observable<any>{
        let encoded_data = JSON.stringify(contacts);
        return this.http.post( 
            this.validateContactsUrl,
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
    updateContacts(contacts: object) : Observable<any>{
        let encoded_data = JSON.stringify(contacts);
        return this.http.post( 
            this.updateContactsUrl,
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
    addContact(contact: object) : Observable<any>{
        let encoded_data = JSON.stringify(contact);
        return this.http.post( 
            this.addContactUrl,
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
    removeContact(contact: object) : Observable<any>{
        let encoded_data = JSON.stringify(contact);
        return this.http.post( 
            this.removeContactUrl,
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
    searchByEmail(email: string): Observable<any> {
        const url = `${this.searchByEmailUrl}/${encodeURIComponent(email)}/`;
        return this.http.get(url)
            
    }
    
    getOrgIds(id: string): Observable<any> {
        const url = `${this.orgIdsUrl}${id}`;
        return this.http.get(url)
            
    }
    
    searchOrgIds(input: string): Observable<any> {
        const url = `${this.orgIdSearchUrl}${encodeURIComponent(input)}&limit=10`;
        return this.http.get(url)
            
    }
    
    addOrgId(orgId: object) : Observable<any>{
        let encoded_data = JSON.stringify(orgId);
        return this.http.post( 
            this.addOrgIdUrl,
            encoded_data, 
            { headers: this.headers }
        )
        
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
        
    }
    
    validateSubMemberField(subMember: object, field: string) : Observable<any> {
         const url = `${this.validateSubMemberFieldUrl}${field}.json`;
         let encoded_data = JSON.stringify(subMember);
         return this.http.post( 
            url, 
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
    checkExistingSubMember(subMember: object) : Observable<any>{
        let encoded_data = JSON.stringify(subMember);
        return this.http.post( 
            this.checkExistingSubMemberUrl,
            encoded_data, 
            { headers: this.headers }
        )
        
    }
    
    addSubMember(subMember: object) : Observable<any>{
        let encoded_data = JSON.stringify(subMember);
        return this.http.post( 
            this.addSubMemberUrl,
            encoded_data, 
            { headers: this.headers }
        )
        
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
