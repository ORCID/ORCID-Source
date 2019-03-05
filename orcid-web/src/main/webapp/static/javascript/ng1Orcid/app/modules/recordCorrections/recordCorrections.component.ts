import { AfterViewInit, Component, OnDestroy, OnInit } from "@angular/core";
import {  Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { CommonService } from "../../shared/common.service";
import { GenericService } from "../../shared/generic.service";

@Component({
  selector: "record-corrections-ng2",
  template: scriptTmpl("record-corrections-ng2-template")
})
export class RecordCorrectionsComponent
  implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  nextPageUrl = "/record-corrections/next";
  previousPageUrl = "/record-corrections/previous";
  currentPage;

  constructor(
    private commonSrvc: CommonService,
    private genericService: GenericService
  ) {}

  ngAfterViewInit() {}

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {
    this.nextPage();
  }

  nextPage() {
    this.genericService
      .getData(
        this.nextPageUrl +
          (this.currentPage ? "/" + this.currentPage.lastElementId : "")
      )
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(
        data => {
          this.currentPage = data;
        },
        error => {
          console.log("error fetching next page");
        }
      );
  }

  previousPage() {
    this.genericService
      .getData(
        this.previousPageUrl +
          (this.currentPage ? "/" + this.currentPage.firstElementId : "")
      )
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(
        data => {
          this.currentPage = data;
        },
        error => {
          console.log("error fetching previous page");
        }
      );
  }
}
