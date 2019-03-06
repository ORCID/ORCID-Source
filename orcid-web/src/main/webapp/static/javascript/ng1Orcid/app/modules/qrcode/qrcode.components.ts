//Import all the angular components
import { AfterViewInit, Component, OnDestroy, OnInit } from "@angular/core";
import { Subject } from "rxjs";

@Component({
  selector: "qrcode-ng2",
  template: scriptTmpl("qrcode-ng2-template")
})
export class qrcodeComponent implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();

  constructor() {}

  //Default init functions provided by Angular Core
  ngAfterViewInit() {
    //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {}

  getBaseUri(): String {
      return getBaseUri();
  };
}
