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
          this.meta.addTag({
            name: "og:title ",
            content:
              person.displayName + " (" + userInfo.EFFECTIVE_USER_ORCID + ")"
          });
          this.meta.addTag({
            name: "og:description",
            content: person.biography.content
          });
          this.meta.addTag({
            name: "og:image",
            content:
              configInfo.messages["STATIC_PATH"] + "/img/orcid-og-image.png"
          });
          this.bio = person.biography.content;
        }
      }
    );
  }
}
