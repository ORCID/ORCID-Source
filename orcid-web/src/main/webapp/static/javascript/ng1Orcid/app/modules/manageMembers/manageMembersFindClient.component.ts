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
  @Output()
  update = new EventEmitter<string>();
  _client;
  scopes = [];
  selectedScopes = [];
  actTypeList = ["Articles", "Books", "Data", "Student Publications"].map(
    geo => {
      return { name: geo };
    }
  );
  geoAreaList = [
    "Global",
    "Africa",
    "Asia",
    "Australia",
    "Europe",
    "North America",
    "South America"
  ].map(geo => {
    return { name: geo };
  });

  constructor(
    private manageMembers: ManageMembersService,
    private modalService: ModalService
  ) {}

  //Default init functions provid   ed by Angular Core
  ngAfterViewInit() {
    //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
  }

  @Input()
  set client(client) {
    if (
      !client.authenticationProviderId ||
      !client.authenticationProviderId.value
    ) {
      client.authenticationProviderId = {
        value: "",
        errors: []
      };
    }
    if (!client.memberName || !client.memberName.value) {
      client.memberName = {
        value: "",
        errors: []
      };
    }
    if (!client.memberName || !client.memberName.value) {
      client.memberName = {
        value: "",
        errors: []
      };
    }
    if (client.redirectUris) {
      client.redirectUris.forEach(rUri => {
        rUri.scopes = rUri.scopes.map(scope => {
          return { name: scope };
        });
        if (rUri.type.value == "import-works-wizard" && rUri.geoArea) {
          rUri.geoArea.geoArea = {
            value: JSON.parse(rUri.geoArea.value)
          };
          rUri.geoArea.geoArea.value["import-works-wizard"] = rUri.geoArea.geoArea.value["import-works-wizard"].map(scope => {
            return {
              name: scope
            };
          });
        }
        if (rUri.type.value == "import-works-wizard" && rUri.actType) {
          rUri.actType.actType = {
            value: JSON.parse(rUri.actType.value)
          };
          rUri.actType.actType.value["import-works-wizard"] = rUri.actType.actType.value["import-works-wizard"].map(scope => {
            return {
              name: scope
            };
          });
        }
      });
    }

    this._client = client;
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  ngOnInit() {
    this.manageMembers.getAvailableScopes().subscribe((response: any) => {
      this.scopes = response.map(scope => {
        return {
          name: scope
        };
      });
    });

    this.modalService.notifyObservable$.subscribe(data => {
      if (
        data &&
        data.moduleId === "modalFindMemberConfirm" &&
        data.action === "close" &&
        data.input === "update"
      ) {
        this.manageMembers.updateClient(data.object).subscribe(
          response => {
            if (response.errors.length === 0) {
              this._client = null;
              this.update.emit(om.get("admin.edit_client.success"));
            } else {
              this.client = response;
            }
          },
          error => {
            console.log("Error updating the client ", error);
          }
        );
      }
    });
  }

  addRedirectUri() {
    this.manageMembers.getEmptyRedirectUri().subscribe((response: any) => {
      this._client.redirectUris.push(response);
    });
  }

  deleteRedirectUri(index) {
    this._client.redirectUris.splice(index, 1);
  }

  loadDefaultScopes(rUri) {
    // Empty the scopes to update the default ones
    rUri.scopes = [];
    // Fill the scopes with the default scopes
    if (rUri.type.value == "grant-read-wizard") {
      rUri.scopes.push({ name: "/read-limited" });
    } else if (rUri.type.value == "import-works-wizard") {
      rUri.scopes.push({ name: "/read-limited" });
      rUri.scopes.push({ name: "/activities/update" });
      rUri.actType = {
        actType: { errors: [], value: { "import-works-wizard": [] } }
      };
      rUri.geoArea = {
        geoArea: { errors: [], value: { "import-works-wizard": [] } }
      };
    } else if (rUri.type.value == "import-funding-wizard") {
      rUri.scopes.push({ name: "/read-limited" });
      rUri.scopes.push({ name: "/activities/update" });
    } else if (rUri.type.value == "import-peer-review-wizard") {
      rUri.scopes.push({ name: "/read-limited" });
      rUri.scopes.push({ name: "/activities/update" });
    } else if (rUri.type.value == "institutional-sign-in") {
      rUri.scopes.push({ name: "/authenticate" });
    }
  }

  confirmUpdateClient() {
    let client = JSON.parse(JSON.stringify(this._client));

    client.redirectUris.forEach(rUri => {
      rUri.scopes = rUri.scopes.map(scope => {
        return scope.name;
      });
      if (rUri.type.value == "import-works-wizard" && rUri.actType.actType) {
        let object = {};
        object[rUri.type.value] = rUri.geoArea.geoArea.value[
          rUri.type.value
        ].map(scope => {
          return scope.name;
        });
        rUri.geoArea.value = JSON.stringify(object);
      }
      if (rUri.type.value == "import-works-wizard" && rUri.actType.actType) {
        let object = {};
        object[rUri.type.value] = rUri.actType.actType.value[
          rUri.type.value
        ].map(scope => {
          return scope.name;
        });
        rUri.actType.value = JSON.stringify(object);
      }
    });

    this.modalService.notifyOther({
      action: "open",
      moduleId: "modalFindMemberConfirm",
      object: client
    });
  }
}
