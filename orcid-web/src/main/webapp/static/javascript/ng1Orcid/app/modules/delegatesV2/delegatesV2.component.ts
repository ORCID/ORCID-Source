declare var $: any;
declare var colorbox: any;
declare var delegateEmail: any;
declare var getBaseUri: any;
declare var isEmail: any;
declare var orcidVar: any;
declare var orcidSearchUrlJs: any;
//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { AccountService } 
    from '../../shared/account.service.ts'; 


@Component({
    selector: 'delegates-v2-ng2',
    template:  scriptTmpl("delegates-v2-ng2-template")
})
export class DelegatesV2Component implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
   
    effectiveUserOrcid: any;
    input: any;
    numFound: any;
    realUserOrcid: any;
    results: any;
    showInitLoader: any;
    showLoader: any;
    sort: any;
    isPasswordConfirmationRequired: any;
    areMoreResults: any;
    start: any;
    emailSearchResult: any;
    errors: any;
    delegateIdx: any;
    delegateToAdd: any;
    delegateNameToAdd: any;
    rows: any;
    password: any;
    delegateToRevoke: any;
    delegatesByOrcid: any;
    delegation: any;

    constructor(
        private accountService: AccountService
    ) {
        this.effectiveUserOrcid = orcidVar.orcidId;
        this.input = {};
        this.input.rows = 10;
        this.input.start = 0;
        this.numFound = 0;
        this.realUserOrcid = orcidVar.realOrcidId;
        this.results = new Array();
        this.showInitLoader = true;
        this.showLoader = false;
        this.sort = {
            column: 'receiverName.value',
            descending: false
        };
        this.isPasswordConfirmationRequired = orcidVar.isPasswordConfirmationRequired;
        this.areMoreResults = false;
        this.start = null;
        this.emailSearchResult = null;
        this.errors = {};
        this.delegateIdx = "";
        this.delegateToAdd = "";
        this.delegateNameToAdd = "";
        this.rows = null;
        this.password = "";
        this.delegateToRevoke = null;
        this.delegatesByOrcid = null;
        this.delegation = null;
    }

    addDelegate(): void {

        var addDelegate = {
            delegateToManage: null,
            password: null
        };
        addDelegate.delegateToManage = this.delegateToAdd;
        addDelegate.password = this.password;

        this.accountService.addDelegate( addDelegate )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.errors.length === 0){
                    this.getDelegates();
                    this.results.splice(this.delegateIdx, 1);
                    this.closeModal();
                }
                else{
                    this.errors = data.errors;
                }
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

    addDelegateByEmail(): void {

        var addDelegate = {
            delegateEmail: null,
            password: null
        };
        
        this.errors = [];
        
        addDelegate.delegateEmail = this.input.text;
        addDelegate.password = this.password;

        this.accountService.addDelegateByEmail( addDelegate )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.errors.length === 0){
                    this.getDelegates();
                    this.closeModal();
                }
                else{
                    this.errors = data.errors;
                }
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
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
                this.showInitLoader = false;
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

    revoke(): void {

        var revokeDelegate = {
            delegateToManage: null,
            password: null
        };
        revokeDelegate.delegateToManage = this.delegateToRevoke;
        revokeDelegate.password = this.password;

        this.accountService.revoke( revokeDelegate )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.errors.length === 0){
                    this.getDelegates();
                    this.closeModal();
                }
                else{
                    this.errors = data.errors;
                }
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
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

    getResults(): void {
        this.accountService.getResults( this.input )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                var bottom = null;
                var newSearchResults = null;
                var newSearchResultsTop = null;
                var orcidList = data['result'];
                var showMoreButtonTop = null;
                var tempResults = null;
                this.numFound = data['num-found'];

                this.results = this.results.concat(orcidList); 
                
                if(!this.numFound){
                    $('#no-results-alert').fadeIn(1200);
                }
                
                this.areMoreResults = this.numFound >= (this.start + this.rows);
                this.showLoader = false;
                
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
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

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

    closeModal(): void {
        //$.colorbox.close();
    };

    concatPropertyValues(array, propertyName): string{
        if(typeof array === 'undefined'){
            return '';
        }
        else{
            return $.map(array, function(o){ return o[propertyName]; }).join(', ');
        }
    };

    confirmAddDelegate(delegateName, delegateId, delegateIdx): void {

        this.errors = [];
        this.delegateNameToAdd = delegateName;
        this.delegateToAdd = delegateId;
        this.delegateIdx = delegateIdx;
        /*
        $.colorbox({
                    html : $compile($('#confirm-add-delegate-modal').html())($scope),
                    transition: 'fade',
                    close: '',
                    onLoad: function() {
                        $('#cboxClose').remove();
                    },
                    onComplete: function() {$.colorbox.resize();},
                    scrolling: true
                });
        */
    };

    confirmAddDelegateByEmail(emailSearchResult): void {

        this.errors = [];
        this.emailSearchResult = emailSearchResult;
        /*
        $.colorbox({
                    html : $compile($('#confirm-add-delegate-by-email-modal').html())($scope),
                    transition: 'fade',
                    close: '',
                    onLoad: function() {
                        $('#cboxClose').remove();
                    },
                    onComplete: function() {$.colorbox.resize();},
                    scrolling: true
                });
        */
    };

    confirmRevoke = function(delegateName, delegateId) {
        this.errors = [];
        this.delegateNameToRevoke = delegateName;
        this.delegateToRevoke = delegateId;
        /*
        $.colorbox({
            html : $compile($('#revoke-delegate-modal').html())($scope)

        });
        $.colorbox.resize();
        */
    };

    getDisplayName(result): any {
        if(!result['namesRequestSent']){
            result['namesRequestSent'] = true;
            var name="";
            var orcid = result['orcid-identifier'].path;
            var url = orcidVar.pubBaseUri + '/v2.1/' + orcid + '/person';
            
            this.accountService.getDisplayName( orcid )
            .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
            .subscribe(
                data => {
                    if (data['name']['given-names']){
                        result['given-names'] = data['name']['given-names']['value'];
                    }
                    if(data['name']['family-name']){
                        result['family-name'] = data['name']['family-name']['value'];
                    }
                    if(data['name']['credit-name']) {
                        result['credit-name'] = data['name']['credit-name']['value'];
                    }

                    if(result['credit-name']) {
                        name = result['credit-name'];
                    } else if (result['given-names'] && result['given-names']){
                        name = result['given-names'] + " " + result['family-name'];
                    } else if (result['given-names']) {
                        name = result['given-names'];
                    } else {
                        name = "";
                    }             
                    return name; 
                },
                error => {
                    //console.log('setformDataError', error);
                } 
            );

        }

    };

    getMoreResults(): void {
        this.showLoader = true;
        this.start += 10;
        this.getResults();
    };

    search(): void {
        this.results = new Array();
        this.showLoader = true;
        $('#no-results-alert').hide();
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



    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getDelegates();
    }; 
}