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
import { ModalService } from "../../shared/modal.service";
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: "manage-members-deactivate-client-ng2",
  template: scriptTmpl("manage-member-deactivate-client-ng2-template")
})
export class ManageMembersDeactivateClientComponent
  implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  
  clientToDeactivate: string;
  showDeactivateClientMessages: boolean;
  deactivateClientResults: any;

  constructor(
    private manageMembersService: ManageMembersService,
    private modalService: ModalService
  ) {
    this.clientToDeactivate = '';
    this.showDeactivateClientMessages = false;
  }
  
  deactivateClient(): void {
      let clientActivationRequest = {} as any;
      clientActivationRequest.clientId = this.clientToDeactivate;
      clientActivationRequest.error = '';
      
      this.manageMembersService.deactivateClient( clientActivationRequest )
      .pipe(    
          takeUntil(this.ngUnsubscribe)
      )
      .subscribe(
          data => {
              this.deactivateClientResults = data;
              this.showDeactivateClientMessages = true;
              setTimeout (() => {
                  this.showDeactivateClientMessages = false;
                  this.clientToDeactivate = '';
              }, 10000);
          },
          error => {
              console.log('admin: activateClient error', error);
          } 
      );
  };

  //Default init functions provid   ed by Angular Core
  ngAfterViewInit() {
    //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
  }

  ngOnDestroy() {
  }

  
  ngOnInit() {
    console.log(scriptTmpl("manage-member-deactivate-client-ng2-template"));
  }
}
