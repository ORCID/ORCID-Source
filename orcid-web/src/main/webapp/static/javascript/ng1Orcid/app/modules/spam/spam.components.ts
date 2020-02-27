//Import all the angular components
import {AfterViewInit, Component, OnDestroy, OnInit} from "@angular/core";
import {Subject} from "rxjs";
import {CommonService} from "../../shared/common.service";
import {SpamService} from "../../shared/spam.service";
import {ModalService} from "../../shared/modal.service";

@Component({
  selector: "spam-ng2",
  template: scriptTmpl("spam-ng2-template")
})
export class SpamComponent implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  private isPublicPage;
  private hideSpam;

  constructor(
      private commonSrvc: CommonService,
      private spamService: SpamService,
      private modalService: ModalService,
  ) {

    this.isPublicPage = this.commonSrvc.isPublicPage;
    if(this.isPublicPage) {
      this.commonSrvc.publicUserInfo$
      .subscribe(
          userInfo => {
              this.hideSpam = userInfo.IS_LOCKED === 'true'
          },
          error => {
              console.log('SpamComponent.component.ts: unable to fetch publicUserInfo', error);
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

  ngOnInit() {}

  reportSpam() {
      this.spamService.reportSpam(this.commonSrvc.getPublicOrcidId()).toPromise().
      then((response) => {
          if (response) {
              this.modalService.notifyOther({action:'open', moduleId: 'spamSuccess'});
          } else {
              this.modalService.notifyOther({action:'open', moduleId: 'spamError'});
          }
      })
  }
}
