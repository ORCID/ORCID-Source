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
import { ManageMembersService } from "../../shared/manageMembers.service.ts"

@Component({
  selector: "add-member-form-ng2",
  template: scriptTmpl("add-member-form-ng2-template")
})
export class AddMemberFormComponent
  implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  memberData

  constructor(private modalService: ModalService, private manageMembers: ManageMembersService) {

  }

  //Default init functions provid   ed by Angular Core
  ngAfterViewInit() {
    //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {
    this.manageMembers.getEmptyMember().subscribe((data)=>{
      this.memberData = data; 
      console.log (this.memberData )
    })
  }

  sendForm () {
    this.manageMembers.addMember(this.memberData).subscribe (response=> {
      this.memberData = response;
      this.modalService.notifyOther({
        action: "open",
        moduleId: "modalAddMemberSuccess"
      });
    })
  }

  closeModal() {
    this.modalService.notifyOther({
      action: "close",
      moduleId: "modalAddMember"
    });
  }
}
