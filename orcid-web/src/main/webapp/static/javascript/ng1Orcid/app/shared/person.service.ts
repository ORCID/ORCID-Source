import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, BehaviorSubject } from "rxjs";

@Injectable()
export class PersonService {
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
      this.personEndpoint = this.http
        .get(this.url + "/" + orcidVar.orcidId + this.path)
        .subscribe(person => {
          this.response.next(person);
        });
      this.endpointWasCall = true;
      return this.response.asObservable();
    } else {
      return this.response.asObservable();
    }
  }
}
