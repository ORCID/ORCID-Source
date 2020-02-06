import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject, throwError as observableThrowError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class BlogService {
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor(
        private http: HttpClient) {

    }

    private handleError (body) {
        let errMsg: string;
        if (body instanceof HttpErrorResponse) {
            const err = body.error || JSON.stringify(body);
            errMsg = `${body.status} - ${body.statusText || ''} ${err}`;
        } else {
            errMsg = body.message ? body.message : body.toString();
        }
        console.error(errMsg);
        return observableThrowError(errMsg);
    }

    getBlogFeed(url): Observable<any> {
        return this.http.get(url, { responseType: 'text'})
        .pipe(
            catchError(this.handleError)
        );

    }

}