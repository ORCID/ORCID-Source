declare var $: any; //delete
declare var orcidVar: any;
declare var logAjaxError: any;
declare var getBaseUri: any;
declare var om: any;

//Import all the angular components
import {
  AfterViewInit,
  Component,
  ChangeDetectorRef,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output
} from "@angular/core";

import { Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { CommonService } from "../../shared/common.service.ts";
import { SwitchUserService } from "../../shared/switchUser.service.ts";

@Component({
  selector: "switch-user-ng2",
  template: scriptTmpl("switch-user-ng2-template")
})
export class SwitchUserComponent implements AfterViewInit, OnDestroy, OnInit {
  @Input() requestInfoForm: any;

  private ngUnsubscribe: Subject<void> = new Subject<void>();
  delegators: any;
  isDroppedDown: any;
  me: any;
  searchResultsCache: any;
  searchTerm: any;
  unfilteredLength: any;

  constructor(
    private cdr: ChangeDetectorRef,
    private commonService: CommonService,
    private switchUserService: SwitchUserService
  ) {
    this.isDroppedDown = false;
    this.searchResultsCache = new Object();
  }

  getDelegates(): void {
    this.switchUserService
      .getDelegates()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(
        data => {
          this.delegators = data.delegators;
          this.searchResultsCache[""] = this.delegators;
          this.me = data.me;
          this.unfilteredLength = this.delegators.length;
          this.cdr.detectChanges();
        },
        error => {
          // something bad is happening!
          console.log("error with delegates");
        }
      );
  }

  openMenu(event): void {
    this.isDroppedDown = true;
    event.stopPropagation();
    this.cdr.detectChanges();
  }

  search(): void {
    if (this.searchResultsCache[this.searchTerm] === undefined) {
      if (this.searchTerm === "") {
        this.getDelegates();
        this.searchResultsCache[this.searchTerm] = this.delegators;
      } else {
        this.switchUserService
          .searchDelegates(this.searchTerm)
          .pipe(takeUntil(this.ngUnsubscribe))
          .subscribe(
            data => {
              this.delegators = data;
              this.searchResultsCache[this.searchTerm] = this.delegators;
              this.cdr.detectChanges();
            },
            error => {
              // something bad is happening!
              console.log("error searching for delegates");
            }
          );
      }
    } else {
      this.delegators = this.searchResultsCache[this.searchTerm];
    }
  }

  switchUser(targetOrcid): void {
    this.switchUserService
      .switchUser(targetOrcid)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(
        data => {
          window.location.reload();
        },
        error => {
          // reload page anyway
          // switchUser request is handled by OrcidSwitchUserFilter.java which redirects /switch-user to /my-orcid
          // in non-local environments neither request completes successfully, although the user has been successfully switched
          window.location.reload();
          console.log("error switching users");
        }
      );
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
    console.log("init switch user");
    this.getDelegates();

    //close delegate dropdown
    $(document).bind(
      "click",
      function(event) {
        if (event.target.id !== "delegators-search") {
          this.isDroppedDown = false;
          this.searchTerm = "";
          this.cdr.detectChanges();
        }
      }.bind(this)
    );
  }
}
