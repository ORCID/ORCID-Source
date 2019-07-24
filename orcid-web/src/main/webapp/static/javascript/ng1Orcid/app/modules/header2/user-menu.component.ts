declare var getWindowWidth: any;

//Import all the angular components

import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ChangeDetectorRef,
  HostListener
} from "@angular/core";

import { CommonService } from "../../shared/common.service";
import { GenericService } from "../../shared/generic.service";
import { NotificationsService } from "../../shared/notifications.service";

@Component({
  selector: "user-menu",
  template: scriptTmpl("user-menu-template")
})
export class UserMenuComponent {
  state = false;
  assetsPath = "";
  isMobile = false;
  userInfo: string;
  isPublicPage: boolean;
  getUnreadCount: 0
  nameForm: {
    creditName: { value: string };
    familyName: { value: string };
    givenNames: { value: string };
  };
  constructor(
    private notificationsSrvc: NotificationsService,
    private commonSrvc: CommonService,
    private nameService: GenericService
  ) {
    this.retrieveUnreadCount()
    this.commonSrvc.configInfo$.subscribe(
      data => {
        this.assetsPath = data.messages["STATIC_PATH"];
      },
      error => {
        console.log("header.component.ts: unable to fetch configInfo", error);
      }
    );
    this.onResize();

    this.isPublicPage = this.commonSrvc.isPublicPage;
    if (this.isPublicPage) {
      this.commonSrvc.publicUserInfo$.subscribe(
        data => {
          this.userInfo = data;
        },
        error => {
          console.log(
            "header.component.ts: unable to fetch publicUserInfo",
            error
          );
          this.userInfo = null;
        }
      );
    } else {
      this.commonSrvc.userInfo$.subscribe(
        data => {
          this.userInfo = data;
        },
        error => {
          console.log("header.component.ts: unable to fetch userInfo", error);
          this.userInfo = null;
        }
      );
    }

    this.nameService.getData("/account/nameForm.json").subscribe(
      data => {
        this.nameForm = data;
        if (this.nameForm.creditName == null) {
          this.nameForm.creditName = { value: null };
        }
        if (this.nameForm.familyName == null) {
          this.nameForm.familyName = { value: null };
        }
        if (this.nameForm.givenNames == null) {
          this.nameForm.givenNames = { value: null };
        }
        console.log("this.nameForm", this.nameForm);
      },
      error => {
        //console.log('getNameForm Error', error);
      }
    );
  }

  displayFullName(): boolean {
    let display = false;

    if (
      !(
        this.nameForm != null && (this.nameForm.creditName == null || !this.nameForm.givenNames.value || this.nameForm.givenNames.value.length == 0)
      )
    ) {
      display = true;
    }

    return display;
  }

  displayPublishedName(): boolean {
    let display = false;

    if (
      this.nameForm != null &&
      (this.nameForm.creditName == null ||
        this.nameForm.creditName.value == null ||
        this.nameForm.creditName.value.length == 0)
    ) {
      display = true;
    }

    return display;
  }

  getBaseUri(): String {
    return getBaseUri();
  }
  @HostListener("window:resize", ["$event"])
  onResize() {
    this.isMobile = window.innerWidth < 840.999;
  }

  retrieveUnreadCount(): any {
    if( this.notificationsSrvc.retrieveCountCalled == false ) {
        this.notificationsSrvc.retrieveUnreadCount()
        .subscribe(
            data => {
                this.getUnreadCount = data;
            },
            error => {
                //console.log('verifyEmail', error);
            } 
        );
    }
};
}
