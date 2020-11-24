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
  selector: "manage-members-activate-client-ng2",
  template: scriptTmpl("manage-member-activate-client-ng2-template")
})
export class ManageMembersActivateClientComponent
  implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  
  clientToActivate: string;
  showActivateClientMessages: boolean;
  activateClientResults: any;

  constructor(
    private manageMembersService: ManageMembersService,
    private modalService: ModalService
  ) {
    this.clientToActivate = '';
    this.showActivateClientMessages = false;
  }
  
  activateClient(): void {
        let clientActivationRequest = {} as any;
        clientActivationRequest.clientId = this.clientToActivate;
        clientActivationRequest.error = '';
        
        this.manageMembersService.activateClient( clientActivationRequest )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.activateClientResults = data;
                this.showActivateClientMessages = true;
                setTimeout (() => {
                    this.showActivateClientMessages = false;
                    this.clientToActivate = '';
                }, 10000);
            },
            error => {
                console.log('manage members: activateClient error', error);
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
  }
}
