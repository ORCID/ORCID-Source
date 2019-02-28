//Import all the angular components
import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  Input
} from "@angular/core";
import { Subject } from "rxjs";
import { PersonService } from "../../shared/person.service.ts";

@Component({
  selector: "print-id-banner-ng2",
  template: scriptTmpl("print-id-banner-ng2-template")
})
export class printIdBannerComponent implements AfterViewInit, OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  private displayName: string;

  constructor(private personService: PersonService) {}

  //Default init functions provided by Angular Core
  ngAfterViewInit() {
    //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {
    this.personService.getPerson().subscribe(person => {
      if (person) {
        this.displayName = person.displayName;
      }
    });
  }
}
