import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class GenericService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    private objAlsoKnownAs: any;
    private url: string;
    private storedData: any;
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
        this.objAlsoKnownAs = {
            data: null,
            hasNewData: true
        };
        this.storedData = new Array();
        this.url = getBaseUri();
    }

    notifyOther(): void {
        this.notify.next();
    }

    getData( url_path: string, requestComponent?: string ): Observable<any> {
        if( requestComponent == "alsoKnownAs" ){
            if( this.objAlsoKnownAs.data != null 
                && this.objAlsoKnownAs.hasNewData == false ){
                console.log('aka obj call');

                return this.objAlsoKnownAs.data;
                
            }
            else {
                console.log('aka http call');
                
                return this.http.get(
                    this.url + url_path
                )
                .pipe(
                    tap(
                        (data) => {
                            this.objAlsoKnownAs.data = data;
                            this.objAlsoKnownAs.hasNewData = false;              
                        }
                    )
                )
            }
        }

        return this.http.get(
                this.url + url_path
            )

        
    }

    getStoredData(objName): Observable<any> {
        return this.storedData[objName];
    }

    setData( obj, url_path ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.url + url_path, 
            encoded_data, 
            { headers: this.headers }
        )
        .pipe(
            tap(
                (data) => {
                    this.objAlsoKnownAs.hasNewData = true;                      
                }
            )
        )
        
    }

    setStoredData(objName, objValue): void {
        this.storedData[objName] = objValue;
    }

    open(id: string) {
        $('#' + id).show();
    }
 
    close(id: string) {
        $('#' + id).hide();
    }
}
