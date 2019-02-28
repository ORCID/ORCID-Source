//Import all the angular components
import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  Input
} from "@angular/core";
import { Subject } from "rxjs";
import { PersonService } from "../../shared/person.service.ts";
import { CommonService } from "../../shared/common.service.ts";

@Component({
  selector: "id-banner-ng2",
  template: scriptTmpl("id-banner-ng2-template")
})
export class idBannerComponent implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  private displayName: string;
  private userInfo: any;

  constructor(private personService: PersonService, private commonSrvc: CommonService) {   
      var isPublicPage = this.commonSrvc.isPublicPage;
      if(isPublicPage) {
          this.userInfo = this.commonSrvc.publicUserInfo$
          .subscribe(
              data => {
                  this.userInfo = data;                
              },
              error => {
                  console.log('idBanner.component.ts: unable to fetch publicUserInfo', error);
                  this.userInfo = {};
              } 
          );
      } else {
          this.userInfo = this.commonSrvc.userInfo$
          .subscribe(
              data => {
                  this.userInfo = data;                
              },
              error => {
                  console.log('idBanner.component.ts: unable to fetch userInfo', error);
                  this.userInfo = {};
              } 
          );
      }      
  }

  //Default init functions provided by Angular Core
  ngAfterViewInit() {
    //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {
    this.personService.getPerson().subscribe(person => {
      if (person) {
        this.displayName = person.displayName;
      }
    });
  }
}
