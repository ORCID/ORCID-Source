declare var orcidVar: any;

import { Observable, Subject, Subscription } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { Component } from "@angular/core";
import { CommonService } from "../../shared/common.service";
import { PersonService } from "../../shared/person.service";

@Component({
  selector: "public-record-ng2",
  template: scriptTmpl("public-record-ng2-template")
})
export class PublicRecordComponent {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  popoverShowing: any;
  showSources: any;
  personEndPoint = "/" + orcidVar.orcidId + "/person.json";
  personData;
  objectKeys = Object.keys;
  userInfo: any;

  constructor(
    private commonService: CommonService,
    private personService: PersonService
  ) {
    this.popoverShowing = new Array();
    this.showSources = new Array();
    this.userInfo = this.commonService.publicUserInfo$
    .subscribe(
        data => {
            this.userInfo = data;                
        },
        error => {
            console.log('publicRecord.component.ts: unable to fetch publicUserInfo', error);
            this.userInfo = {};
        } 
    );
  }

  ngOnInit() {
    this.personService.getPerson().subscribe(data => {
      if (data) {
        this.personData = data;
        if(this.personData.title){
          document.title=this.personData.title;
        }
      }
    });
  }

  hidePopover(section): void {
    this.popoverShowing[section] = false;
  }

  showPopover(section): void {
    this.popoverShowing[section] = true;
  }

  toggleSourcesDisplay(section): void {
    this.showSources[section] = !this.showSources[section];
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
