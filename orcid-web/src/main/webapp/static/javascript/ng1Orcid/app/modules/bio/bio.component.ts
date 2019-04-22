//Import all the angular components
import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  Input
} from "@angular/core";
import { Subject } from "rxjs";
import { PersonService } from "../../shared/person.service";
import { CommonService } from "../../shared/common.service";

@Component({
  selector: "bio-ng2",
  template: scriptTmpl("bio-ng2-template")
})
export class bioComponent implements OnDestroy, OnInit {
  private ngUnsubscribe: Subject<void> = new Subject<void>();
  private bio
  public userInfo
  public baseUrl: string

  constructor(private personService: PersonService, private common: CommonService) {
    this.baseUrl = getBaseUri()
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {
    this.common.publicUserInfo$.subscribe(userInfo => {
      this.userInfo = userInfo 
    })

    this.personService.getPerson().subscribe(person => {
      if (person && person.biography) {
        this.bio = person.biography.content;
      }
    });
  }
}
