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
  selector: "manage-members-find-client-ng2",
  template: scriptTmpl("manage-member-find-client-ng2-template")
})
export class ManageMembersFindClientComponent
  implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  @Output() update = new EventEmitter<string> ();
  _client
  

  scopes = []
  selectedScopes = []
  
  constructor(private manageMembers: ManageMembersService, private modalService: ModalService) {}

  //Default init functions provid   ed by Angular Core
  ngAfterViewInit() {
    //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
  }


  @Input() 
  set client (client)  {
    if (!client.authenticationProviderId || !client.authenticationProviderId.value) {
      client.authenticationProviderId = {
        value : "",
        errors: []
      }
    } if (!client.memberName || !client.memberName  .value) {
      client.memberName = {
        value : "",
        errors: []
      }
    }
    this._client = client
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {

      this.manageMembers.getAvailableScopes().subscribe(response=> {
        response.map( (scope) => {
          this.scopes.push ({
            name: scope
          })
        })
        console.log (this.scopes)
      })
    
    // this.modalService.notifyObservable$.subscribe ( (data)=>{
    //   if (data && data.moduleId === "modalFindMemberConfirm" && data.action === "close" && data.input === "update") {
    //     this.manageMembers.updateMember(this.member).subscribe(
    //       response => {
    //         this.member = response;
    //         if (this.member.errors.length === 0) {
    //           console.log (this.member)
    //           this.update.emit(om.get('manage_member.edit_member.success'))
    //         }
    //       },
    //       error => {
    //         console.log("Error updating the member ", error);
    //       }
    //     );

    //   }
    // })
  }

  confirmUpdateClient () {
    console.log (this._client); 
    console.log (this.selectedScopes);
  }

}
