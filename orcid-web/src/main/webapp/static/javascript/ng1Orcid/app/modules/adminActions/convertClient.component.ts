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
import { ModalService } from "../../shared/modal.service";
import { AdminActionsService } from "../../shared/adminActions.service";  

@Component({
  selector: "convert-client-confirm-ng2",
  template: scriptTmpl("convert-client-confirm-ng2-template")
})
export class ConvertClientConfirmComponent
  implements AfterViewInit, OnDestroy {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  private subscription: Subscription;
  clientConversionData;

  constructor(
    private modalService: ModalService,
    private adminActionsService: AdminActionsService
  ) {
    this.clientConversionData = {};
    this.clientConversionData.success = false;
    this.clientConversionData.error = '';
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngAfterViewInit() {
    this.subscription = this.modalService.notifyObservable$.subscribe(
        (res) => {
            if( res.convertClient ) {
                this.clientConversionData = res.convertClient;
            }
        }
    );
  };
  
  confirmConvertClient() {
    this.adminActionsService.convertClient(this.clientConversionData).subscribe(
      response => {
        this.clientConversionData = response;
        if (this.clientConversionData.success) {
            setTimeout (() => {
                this.modalService.notifyOther({
                    action: "close",
                    moduleId: "confirmConvertClient"
                });
                this.clientConversionData = null;
            }, 4000);     
        }
      },
      error => {
        console.log("Error converting client ", error);
      }
    );
  }

  closeModal() {
    this.modalService.notifyOther({
      action: "close",
      moduleId: "confirmConvertClient"
    });
  }
}
