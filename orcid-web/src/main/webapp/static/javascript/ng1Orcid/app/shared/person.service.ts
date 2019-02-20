import { HttpClient } from "@angular/common/http";
import { Injectable, Component, OnDestroy } from "@angular/core";
import { Observable, BehaviorSubject, Subject } from "rxjs";
import { takeUntil } from 'rxjs/operators';

@Injectable()
export class PersonService {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  private url: string;
  private path = "/person.json";
  private endpointWasCall: boolean = false;
  private personEndpoint;
  private response: BehaviorSubject<any> = new BehaviorSubject(null); 

  constructor(private http: HttpClient) {
    this.url = getBaseUri();    
  }

  getPerson() {      
    if (!this.endpointWasCall) {           
        var orcidRegex = /^(\d{4}-){3}\d{3}[\dX]$/;
        var path = window.location.pathname;
        path = path.substring(path.lastIndexOf('/') + 1);
        var isPublicPage = orcidRegex.test(path);
        if(isPublicPage) {
            var orcidId = path.substring(path.lastIndexOf('/') + 1);         
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
