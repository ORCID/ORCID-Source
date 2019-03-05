import { HttpClient } from "@angular/common/http";
import { Injectable, Component, OnDestroy } from "@angular/core";
import { Observable, BehaviorSubject, Subject } from "rxjs";
import { takeUntil } from 'rxjs/operators';
import { CommonService } from './common.service.ts';

@Injectable({
  providedIn: 'root',
})
export class PersonService {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  private url: string;
  private path = "/person.json";
  private endpointWasCall: boolean = false;
  private personEndpoint;
  private response: BehaviorSubject<any> = new BehaviorSubject(null); 

  constructor(private http: HttpClient, private commonSrvc: CommonService) {
    this.url = getBaseUri();    
  }

  getPerson() {      
    if (!this.endpointWasCall) {           
        var isPublicPage = this.commonSrvc.isPublicPage
        if(isPublicPage) {
            var orcidRegex = this.commonSrvc.orcidRegex;
            var path = window.location.pathname;
            var orcidId = path.match(orcidRegex)[0];  
            this.personEndpoint = this.http
                .get(this.url + '/' + orcidId + this.path)
                .subscribe(person => {
                    this.response.next(person);
                });  
        } else {
            this.personEndpoint = this.http
            .get(this.url + this.path)
            .subscribe(person => {
                this.response.next(person);
            });  
        }
        
        this.endpointWasCall = true;
        return this.response.asObservable();
    } else {
      return this.response.asObservable();
    }
  }  
}
