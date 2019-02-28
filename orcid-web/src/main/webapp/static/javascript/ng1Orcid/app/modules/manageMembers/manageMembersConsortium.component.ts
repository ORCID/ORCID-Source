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
  selector: "manage-members-consortium-ng2",
  template: scriptTmpl("manage-member-consortium-ng2-template")
})
export class ManageMembersConsortiumComponent
  implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  salesForceId;
  consortium;
  findConsortiumError;
  successEditMemberMessage;
  modalSubscription: Subscription;

  constructor(
    private manageMembersService: ManageMembersService,
    private modalService: ModalService
  ) {}

  //Default init functions provid   ed by Angular Core
  ngAfterViewInit() {
    //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
  }

  ngOnDestroy() {
    if (this.modalSubscription)
      this.modalSubscription.unsubscribe()
  }

  findConsortium() {
    this.successEditMemberMessage = null;
    this.consortium = null; 
    this.findConsortiumError = null; 
    
    this.manageMembersService.findConsortium(this.salesForceId).subscribe(
      (response: any) => {
        this.consortium = response;
        console.log(this.consortium);
      },
      (error: any) => {
        this.findConsortiumError = false;
        console.log("Error finding the consortium");
      }
    );
  }

  confirmUpdateConsortium(work): void {
    this.modalService.notifyOther({
      action: "open",
      moduleId: "modalFindMemberConfirm",
      object: this.consortium
    });
  }

  updateConsortium() {
    this.manageMembersService
      .updateConsortium(this.consortium)
      .subscribe((response: any) => {
        if (response.errors.length == 0) {
          this.successEditMemberMessage = om.get(
            "manage_member.edit_member.success"
          );
        }
        this.consortium = response;
      });
  }

  ngOnInit() {
    this.modalSubscription = this.modalService.notifyObservable$.subscribe((data: any) => {
      if (
        data &&
        data.moduleId === "modalFindMemberConfirm" &&
        data.action === "close" &&
        data.input === "update"
      ) {
        this.updateConsortium();
      }
    });
  }
}
