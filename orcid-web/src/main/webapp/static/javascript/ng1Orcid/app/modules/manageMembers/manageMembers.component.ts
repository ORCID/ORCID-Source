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
import { ModalService } from "../../shared/modal.service.ts";

@Component({
  selector: "manage-members-ng2",
  template: scriptTmpl("manage-member-ng2-template")
})
export class ManageMembersComponent
  implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();

  collapseMenu = {
    addMember: false,
    findMember: false,
    consortia: false
  };

  constructor(private modalService: ModalService) {}

  toggleCollapse(modalName) {
    this.collapseMenu[modalName] = !this.collapseMenu[modalName];
  }

  showModal(work): void {
    this.modalService.notifyOther({
      action: "open",
      moduleId: "addMemberModal"
    });
  }

  //Default init functions provid   ed by Angular Core
  ngAfterViewInit() {
    //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {}
}
