//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { WorkspaceService } 
    from '../../shared/workspace.service.ts';

import { CommonService } 
    from '../../shared/common.service.ts'; 

@Component({
    selector: 'personal-info-ng2',
    template:  scriptTmpl("personal-info-ng2-template")
})
export class PersonalInfoComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    lastModifiedTimeString: string;
    displayInfo: any;
    lastModifiedDate: any;

    constructor(
        private workspaceSrvc: WorkspaceService,
        private utilsService: CommonService
    ) {
        this.lastModifiedTimeString = orcidVar.lastModified.replace(/,|\.|\s/g , "");
        this.displayInfo = workspaceSrvc.displayPersonalInfo;
        this.lastModifiedDate = utilsService.formatTime(Number(this.lastModifiedTimeString));
    }

    toggleDisplayInfo(): void {
        this.displayInfo = !this.displayInfo;
    };

  


    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
    }; 
}