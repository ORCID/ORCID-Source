declare var orcidVar: any;

import { Observable, Subject, Subscription } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { Component } from "@angular/core";
import { CommonService } from "../../shared/common.service.ts";
import { GenericService } from "../../shared/generic.service.ts";

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

  constructor(
    private commonService: CommonService,
    private genericService: GenericService
  ) {
    this.popoverShowing = new Array();
    this.showSources = new Array();
  }

  ngOnInit() {
    this.genericService
      .getData(this.personEndPoint)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(data => {
        this.personData= data;
        console.log (this.personData)
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
