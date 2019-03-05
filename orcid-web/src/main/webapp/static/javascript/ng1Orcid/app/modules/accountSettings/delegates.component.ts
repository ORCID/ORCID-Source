declare var $: any;
declare var isEmail: any;
declare var orcidVar: any;
declare var orcidSearchUrlJs: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { AccountService } 
    from '../../shared/account.service';

import { ModalService } 
    from '../../shared/modal.service';  

import { SearchService } 
    from '../../shared/search.service';

@Component({
    selector: 'delegates-ng2',
    template:  scriptTmpl("delegates-ng2-template")
})
export class DelegatesComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
   
    allResults: any;
    areMoreResults: any;
    delegateIdx: any;
    delegateNameToAdd: any;
    delegatesByOrcid: any;
    delegateToAdd: any;
    delegateToRevoke: any;
    delegation: any;
    effectiveUserOrcid: any;
    emailSearchResult: any;
    input: any;
    isPasswordConfirmationRequired: any;
    newResults: any;
    noResults: boolean;
    numFound: any;
    realUserOrcid: any;
    rows: any;
    showLoader: any;
    sort: any;
    start: any;

    constructor(
        private cdr: ChangeDetectorRef,
        private accountService: AccountService,
        private modalService: ModalService,
        private searchService: SearchService
    ) {
        this.allResults = new Array();
        this.areMoreResults = false;
        this.delegateIdx = "";
        this.delegateNameToAdd = "";
        this.delegatesByOrcid = null;
        this.delegateToAdd = "";
        this.delegateToRevoke = null;
        this.delegation = null;
        this.effectiveUserOrcid = orcidVar.orcidId;
        this.emailSearchResult = null;
        this.input = {};
        this.input.rows = 10;
        this.input.start = 0;
        this.isPasswordConfirmationRequired = orcidVar.isPasswordConfirmationRequired;
        this.noResults = false;
        this.numFound = 0;
        this.realUserOrcid = orcidVar.realOrcidId;
        this.rows = null;
        this.showLoader = false;
        this.sort = {
            column: 'receiverName.value',
            descending: false
        };
        this.start = null;
    }

    areResults(): boolean{
        return this.numFound != 0;
    };

    changeSorting(column): void {
        var sort = this.sort;
        if (sort.column === column) {
            sort.descending = !sort.descending;
        } else {
            sort.column = column;
            sort.descending = false;
        }
    };

    confirmAddDelegate(delegateCreditName, delegateGivenNames, delegateFamilyName, delegateId, delegateIdx): void {
        if(delegateCreditName){
            this.delegateNameToAdd = delegateCreditName;
        } else{
            this.delegateNameToAdd = delegateGivenNames + " " + delegateFamilyName;
        }
        this.delegateToAdd = delegateId;
        this.delegateIdx = delegateIdx;
        this.accountService.notifyOther({delegateNameToAdd:this.delegateNameToAdd, delegateToAdd:this.delegateToAdd, delegateIdx:this.delegateIdx});
        this.modalService.notifyOther({action:'open', moduleId: 'modalAddDelegate'});
    };

    confirmAddDelegateByEmail(emailSearchResult): void {
        this.emailSearchResult = emailSearchResult;
        this.accountService.notifyOther({emailSearchResult:this.emailSearchResult, input:this.input});
        this.modalService.notifyOther({action:'open', moduleId: 'modalAddDelegate'});
    };

    confirmRevoke = function(delegateName, delegateId) {
        this.delegateNameToRevoke = delegateName;
        this.delegateToRevoke = delegateId;
        this.accountService.notifyOther({delegateNameToRevoke:this.delegateNameToRevoke, delegateToRevoke:this.delegateToRevoke});
        this.modalService.notifyOther({action:'open', moduleId: 'modalRevokeDelegate'});
    };

    getDelegates(): void {
        this.accountService.getDelegates(  )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.delegatesByOrcid = {};
                this.delegation = data;
                if(data != null){
                    for(var i=0; i < data.length; i++){
                        var delegate = data[i];
                        this.delegatesByOrcid[delegate.receiverOrcid.value] = delegate;
                    }
                }
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

    getMoreResults(): void {
        this.showLoader = true;
        this.start += 10;
        this.getResults();
    };

    getNames(result: any){
        if(!result['namesRequestSent']){
            result['namesRequestSent'] = true;
            var name="";
            var orcid = result['orcid-identifier'].path;
            this.searchService.getNames(orcid).subscribe(
                namesResult => {
                    if (namesResult['name']['given-names']){
                        result['given-names'] = namesResult['name']['given-names']['value'];
                    }
                    if(namesResult['name']['family-name']){
                        result['family-name'] = namesResult['name']['family-name']['value'];
                    }
                    if(namesResult['name']['credit-name']) {
                        result['credit-name'] = namesResult['name']['credit-name']['value'];
                    }
                }
            );
        } 
    }

    getResults(): void {
        this.searchService.getResults( orcidSearchUrlJs.buildUrl(this.input) )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.newResults = data['result'];
                this.numFound = data['num-found'];
                console.log(this.numFound);
                console.log(typeof this.numFound);

                for(var i = 0; i < this.newResults.length; i++){
                    this.getNames(this.newResults[i]);
                }

                this.allResults = this.allResults.concat(this.newResults); 
                this.cdr.detectChanges();
                
                this.showLoader = false;

                if(!this.numFound){
                    this.noResults = true;
                }

                this.areMoreResults = this.numFound > (this.input.start + this.input.rows);
                
                var bottom = null;
                var newSearchResults = null;
                var newSearchResultsTop = null;
                var showMoreButtonTop = null;
                newSearchResults = $('.new-search-result');
                
                if(newSearchResults.length > 0){
                    newSearchResults.fadeIn(1200);
                    newSearchResults.removeClass('new-search-result');
                    newSearchResultsTop = newSearchResults.offset().top;
                    showMoreButtonTop = $('#show-more-button-container').offset().top;
                    bottom = $(window).height();
                    if(showMoreButtonTop > bottom){
                        $('html, body').animate(
                            {
                                scrollTop: newSearchResultsTop
                            },
                            1000,
                            'easeOutQuint'
                        );
                    }
                }
                this.cdr.detectChanges();
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

    search(): void {
        this.allResults = new Array();
        this.showLoader = true;
        this.noResults = false;
        if(isEmail(this.input.text)){
            this.numFound = 0;
            this.start = 0;
            this.areMoreResults = 0;
            this.searchByEmail();
        }
        else{
            this.getResults();
        }
    };

    searchByEmail(): void {
        this.accountService.searchByEmail( this.input.text )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.confirmAddDelegateByEmail(data);
                this.showLoader = false;
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directivesload
        this.subscription = this.accountService.notifyObservable$.subscribe(
            (res) => {                
                if(res.action == 'add') {
                    if(res.successful == true) {
                        this.allResults.splice(this.delegateIdx, 1);
                        this.input = {};
                        this.getDelegates();
                    }
                } 
                if(res.action == 'revoke') {
                    if(res.successful == true) {
                        this.getDelegates();
                    }
                }               
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getDelegates();
    }; 
}