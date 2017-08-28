declare var getBaseUri: any;

import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/Rx';

@Injectable()
export class BiographyService {
    constructor( private http: Http ){

    }

    getBiographyData(): Observable<any> {
        return this.http.get(getBaseUri() + '/account/biographyForm.json')
            .map((res:Response) => res.json());
    }
}
