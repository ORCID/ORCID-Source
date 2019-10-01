//Import all the angular components
import { Component, OnDestroy, OnInit } from "@angular/core";
import { Subject, forkJoin } from "rxjs";
import { PersonService } from "../../shared/person.service";
import { CommonService } from "../../shared/common.service";
import { Meta } from "@angular/platform-browser";
import { filter, take } from "rxjs/operators";

@Component({
  selector: "bio-ng2",
  template: scriptTmpl("bio-ng2-template")
})
export class bioComponent implements OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  private bio;
  public userInfo;
  public baseUrl: string;

  constructor(
    private commonSrvc: CommonService,
    private personService: PersonService,
    private common: CommonService,
    private meta: Meta
  ) {
    this.baseUrl = getBaseUri();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {
    forkJoin(
      this.commonSrvc.configInfo$,
      this.common.publicUserInfo$,
      this.personService.getPerson().pipe(
        filter(x => x !== null),
        take(1)
      )
    ).subscribe(
      ([configInfo, userInfo, person]) => {
        this.userInfo = userInfo;
        if (person && person.biography) {
          this.bio = person.biography.content;
        }
      }
    );
  }
}
