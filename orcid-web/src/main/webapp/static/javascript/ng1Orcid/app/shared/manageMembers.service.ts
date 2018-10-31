import { Injectable } from "@angular/core";

import {
  HttpClient,
  HttpClientModule,
  HttpHeaders
} from "@angular/common/http";

import {
  Headers,
  Http,
  RequestOptions,
  Response,
  URLSearchParams
} from "@angular/http";

import { Observable, Subject } from "rxjs";

import { catchError, map, tap } from "rxjs/operators";

@Injectable()
export class ManageMembersService {
  private headers: HttpHeaders;
  private notify = new Subject<any>();

  notifyObservable$ = this.notify.asObservable();

  constructor(private http: HttpClient) {
    this.headers = new HttpHeaders({
      "Access-Control-Allow-Origin": "*",
      "Content-Type": "application/json",
      "X-CSRF-TOKEN": document
        .querySelector("meta[name='_csrf']")
        .getAttribute("content")
    });
  }

  updateConsortium(obj): Observable<any> {
    let encoded_data = JSON.stringify(obj);

    return this.http.post(
      getBaseUri() + "/manage-members/update-consortium.json",
      encoded_data,
      { headers: this.headers }
    );
  }

  findConsortium(obj): Observable<any> {
    let encoded_data = JSON.stringify(obj);
    return this.http.get(
      getBaseUri() + "/manage-members/find-consortium.json?id=" + encoded_data
    );
  }

  getEmptyMember() {
    return this.http.get(getBaseUri() + "/manage-members/member.json");
  }

  addMember(data) {
    return this.http.post(
      getBaseUri() + "/manage-members/create-member.json",
      data,
      { headers: this.headers }
    );
  }

  updateMember(data) {
    return this.http.post(
      getBaseUri() + "/manage-members/update-member.json",
      data,
      { headers: this.headers }
    );
  }

  updateClient(data) {
    return this.http.post(
      getBaseUri() + "/manage-members/update-client.json",
      data,
      { headers: this.headers }
    );
  }

  findMember(id) {
    return this.http.get(getBaseUri() + "/manage-members/find.json?id=" + id, {
      headers: this.headers
    });
  }

  getAvailableScopes() {
    return this.http.get(getBaseUri() + "/group/developer-tools/get-available-scopes.json", {
      headers: this.headers
    });
  }

  getEmptyRedirectUri() {
    return this.http.get(getBaseUri() + "/manage-members/empty-redirect-uri.json", {
      headers: this.headers
    });
  }

  notifyOther(): void {
    this.notify.next();
  }
}
