declare var $: any;
declare var getBaseUri: any;
declare var logAjaxError: any;
declare var om: any;
declare var typeahead: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, of, Subject, Subscription } 
    from 'rxjs';

import { catchError, debounceTime, distinctUntilChanged, filter, map, switchMap, takeUntil, tap } 
    from 'rxjs/operators';

import { AccountService } 
    from '../../shared/account.service'; 

import { GenericService } 
    from '../../shared/generic.service'; 

import { CommonService } 
    from '../../shared/common.service';

@Component({
    selector: 'delegators-ng2',
    template:  scriptTmpl("delegators-ng2-template")
})
export class DelegatorsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private userInfo: any;
   
    delegators: any;
    sort: any;
    url_path: string;

    constructor(
        private delegatorsService: GenericService,
        private accountService: AccountService,
        private commonSrvc: CommonService
    ) {
        this.sort = {
            column: 'delegateSummary.giverName.value',
            descending: false
        };
        this.delegators = {};
        this.url_path = '/delegators/delegators-and-me.json';
        this.userInfo = this.commonSrvc.userInfo$
          .subscribe(
              data => {
                  this.userInfo = data; 
              },
              error => {
                  console.log('developerTools.component.ts: unable to fetch userInfo', error);
                  this.userInfo = {};
              } 
          );

    }

    changeSorting(column): void {
        var sort = this.sort;
        if (sort.column === column) {
            sort.descending = !sort.descending;
        } else {
            sort.column = column;
            sort.descending = false;
        }
    };

    formatSearchDelegatorsInput = (result: {value: string}) => result.value;

    formatSearchDelegatorsResult = (result: {value: string, orcid: string}) => result.value + " (" + result.orcid + ")";

    getDelegators(): void {
        this.delegatorsService.getData( this.url_path )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.delegators = data.delegators;
            },
            error => {
                logAjaxError(error);
            } 
        );

    };

    searchDelegators = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term =>
        this.accountService.searchDelegators(term).pipe(
          catchError(() => {
            return of([]);
          }))
        )
      );

    selectDelegator(datum): void {
        window.location.href = getBaseUri() + '/switch-user?username=' + datum.orcid;
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
        if(!this.commonSrvc.isPublicPage) {
            this.getDelegators();
        } else {
            console.log('Dont load delegators on public page');
        }    
    }; 
    
    getBaseUri(): String {
        return getBaseUri();
    };
}
