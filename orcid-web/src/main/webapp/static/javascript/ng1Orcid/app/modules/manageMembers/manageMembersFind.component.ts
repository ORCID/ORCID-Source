declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

//Import all the angular components

import { NgForOf, NgIf } from "@angular/common";

import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output
} from "@angular/core";

import { Observable, Subject, Subscription } from "rxjs";
import { ManageMembersService } from "../../shared/manageMembers.service";

@Component({
  selector: "manage-members-find-ng2",
  template: scriptTmpl("manage-member-find-ng2-template")
})
export class ManageMembersFindComponent
  implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  searchId;
  memberObject;
  updateMessage;
  clientObject;

  constructor(private manageMembersService: ManageMembersService) {}

  //Default init functions provid   ed by Angular Core
  ngAfterViewInit() {
    //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {}

  find(id) {
    this.updateMessage = null;
    this.memberObject = null;
    this.clientObject = null;
    this.manageMembersService.findMember(id).subscribe(
      (response: any) => {
        if (response.client) {
          this.clientObject = response.clientObject;
        } else if (response.memberObject != null) {
          this.memberObject = response.memberObject;
        }
      },
      error => {
        console.log("Error searching for value:" + id + " " + error);
      }
    );
  }

  update(state) {
    this.updateMessage = state;
  }
}
