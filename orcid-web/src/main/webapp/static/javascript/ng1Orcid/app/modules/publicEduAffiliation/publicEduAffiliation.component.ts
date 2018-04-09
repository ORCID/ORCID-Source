declare var ActSortState: any;
declare var GroupedActivities: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { AffiliationService } 
    from '../../shared/affiliation.service.ts';

import { WorkspaceService } 
    from '../../shared/workspace.service.ts';

import { CommonService } 
    from '../../shared/common.service.ts'; 

@Component({
    selector: 'public-edu-affiliation-ng2',
    template:  scriptTmpl("public-edu-affiliation-ng2-template")
})
export class PublicEduAffiliationComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    displayURLPopOver: any;
    moreInfo: any;
    sortState: any;
    showElement: any;
    printView: any;

    constructor(
        private affiliationsSrvc: AffiliationService,
        private workspaceSrvc: WorkspaceService,
        private utilsService: CommonService
    ) {
        this.displayURLPopOver = {};
        this.moreInfo = {};
        this.sortState = new ActSortState(GroupedActivities.AFFILIATION);
        this.showElement = {};
        this.printView = this.utilsService.isPrintView(window.location.pathname);
    }

    closeMoreInfo(key): void {
        this.moreInfo[key]=false;
    };

    hideTooltip(element): void {        
        this.showElement[element] = false;
    };

    hideURLPopOver(id): void {
        this.displayURLPopOver[id] = false;
    };

    // remove once grouping is live
    moreInfoMouseEnter(key, $event): void {
        $event.stopPropagation();
        if (document.documentElement.className.indexOf('no-touch') >= 0) {
            this.moreInfo[key]=true;
        }
    };

    showDetailsMouseClick(group, $event): void {
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];   
    };

    showTooltip(element): void {        
        this.showElement[element] = true;
    };

    showURLPopOver(id): void {
        this.displayURLPopOver[id] = true;
    };


    sort(key): void {       
        this.sortState.sortBy(key);
    };

    // remove once grouping is live
    toggleClickMoreInfo(key): void {
        if (document.documentElement.className.indexOf('no-touch') == -1 ) {
            this.moreInfo[key]=!this.moreInfo[key];
        }
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