import { HttpClient } from "@angular/common/http";
import { Injectable, Component, OnDestroy } from "@angular/core";
import { Observable, BehaviorSubject, Subject } from "rxjs";
import { takeUntil } from 'rxjs/operators';
import { CommonService } from './common.service.ts';

@Injectable()
export class PersonService {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  private url: string;
  private path = "/person.json";
  private endpointWasCall: boolean = false;
  private personEndpoint;
  private response: BehaviorSubject<any> = new BehaviorSubject(null);
  private userInfo: any;

  constructor(private http: HttpClient, private commonSrvc: CommonService) {
    this.url = getBaseUri();
    this.userInfo = {};
  }

  getPerson() {      
    if (!this.endpointWasCall) {        
        this.commonSrvc.getUserInfo().pipe(takeUntil(this.ngUnsubscribe)).subscribe(
                data => {
                    this.userInfo = data;
                    this.personEndpoint = this.http
                    .get(this.url + '/' + this.userInfo['REAL_USER_ORCID'] + this.path)
                    .subscribe(person => {
                        this.response.next(person);
                    });
                },
                error => {
                    console.log('ngOnInit: unable to fetch userInfo', error);
                } 
            );      
        this.endpointWasCall = true;
        return this.response.asObservable();
    } else {
      return this.response.asObservable();
    }
  }  
}
