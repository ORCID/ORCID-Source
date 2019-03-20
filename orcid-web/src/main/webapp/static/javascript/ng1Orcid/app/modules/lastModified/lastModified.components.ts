//Import all the angular components
import { AfterViewInit, Component, OnDestroy, OnInit } from "@angular/core";
import { Subject } from "rxjs";
import { CommonService } from "../../shared/common.service";

@Component({
  selector: "last-modified-ng2",
  template: scriptTmpl("last-modified-ng2-template")
})
export class lastModifiedComponent implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  private isPublicPage
  private hideLastModified

  constructor(private commonSrvc: CommonService) {

    this.isPublicPage = this.commonSrvc.isPublicPage;
    if(this.isPublicPage) {
      this.commonSrvc.publicUserInfo$
      .subscribe(
          userInfo => {
              this.hideLastModified = !userInfo || userInfo.IS_LOCKED === 'true' || userInfo.IS_DEACTIVATED === 'true'   
          },
          error => {
              console.log('PrintRecordComponent.component.ts: unable to fetch publicUserInfo', error);                    
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
}
