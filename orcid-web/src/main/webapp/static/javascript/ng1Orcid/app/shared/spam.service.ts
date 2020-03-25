import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class SpamService {
    private headers: HttpHeaders ;
    spam_url: string;

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders (
            {
                'Content-Type': 'application/json'
            }
        );
        this.spam_url = getBaseUri() + '/spam';
    }

    reportSpam(orcidId): Observable<any> {
        return this.http.post(
            this.spam_url + "/report-spam.json",
            orcidId,
            { headers: this.headers }
        );
    }
}