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
import { ManageMembersService } from "../../shared/manageMembers.service.ts";
import { ModalService } from "../../shared/modal.service.ts";

@Component({
  selector: "manage-members-find-member-confirm-ng2",
  template: scriptTmpl("manage-member-find-member-confirm-ng2-template")
})
export class ManageMembersFindMemberConfirmComponent
  implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  object;

  constructor(private modalService: ModalService) {}

  //Default init functions provid   ed by Angular Core
  ngAfterViewInit() {
    //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {
    this.modalService.notifyObservable$.subscribe(data => {
      if (data && data.moduleId === "modalFindMemberConfirm") {
        this.object = data.object;
      }
    });
  }

  update() {
    this.modalService.notifyOther({
      action: "close",
      moduleId: "modalFindMemberConfirm",
      input: "update",
      object: this.object
    });
    this.object = null;
  }

  cancel() {
    this.modalService.notifyOther({
      action: "close",
      moduleId: "modalFindMemberConfirm",
      input: "cancel"
    });
    this.object = null;
  }
}
