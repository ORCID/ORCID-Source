declare var $: any;

import {  Component, OnDestroy, OnInit, ChangeDetectorRef } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs'; 

import { takeUntil } 
    from 'rxjs/operators';

import { FeaturesService }
    from '../../shared/features.service'

import { SearchService } 
    from '../../shared/search.service';

@Component({
    selector: 'search-ng2',
    template:  scriptTmpl("search-ng2-template")
})
export class SearchComponent implements OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    quickSearchEDisMax = '{!edismax qf="given-and-family-names^50.0 family-name^10.0 given-names^5.0 credit-name^10.0 other-names^5.0 text^1.0" pf="given-and-family-names^50.0" mm=1}';
    orcidPathRegex = new RegExp("(\\d{4}-){3,}\\d{3}[\\dX]");
    orcidFullRegex = new RegExp(
            "^\\s*((http://)?([^/]*orcid\\.org|localhost.*/orcid-web)/)?(\\d{4}-){3,}\\d{3}[\\dX]\\s*$");

    allResults: any;
    areMoreResults: any;
    hasErrors: any;
    input: any;
    newResults: any;
    numFound: any;
    searchResults: any;
    searchResultsAffiliationsFeatureEnabled: boolean = this.featuresService.isFeatureEnabled('SEARCH_RESULTS_AFFILIATIONS');
    searchResultsLoading: boolean;
    showMoreLoading: boolean;
    showNoResultsAlert: boolean;
    results: any;
    resultsWithNames: any;
    resultsObservable: any;
    resultsShowing: any;
    url: string;
    constructor(
        private cdr:ChangeDetectorRef,
        private featuresService: FeaturesService,
        private searchSrvc: SearchService,
       
    ) {
        this.allResults = new Array();
        this.areMoreResults = false;
        this.hasErrors = false;
        this.input = {};
        this.input.rows = 10;
        this.input.start = 0;
        this.numFound = 0;
        this.searchResultsLoading = false;
        this.showMoreLoading = false;
        this.showNoResultsAlert = false;
        this.results = new Array();
        this.resultsShowing = 0;
    }

    concatPropertyValues(array: any, propertyName: any){
        if(typeof array === 'undefined'){
            return '';
        }
        else{
            return $.map(array, function(o){ return o[propertyName]; }).join(', ');
        }
    };

    areResults(): any {
        return this.allResults.length > 0;
    };

    buildAdvancedSearchQuery(input: any) {
        var escapedAffiliationOrg;
        var escapedFamilyName;
        var escapedGivenNames;
        var escapedGridOrg;
        var escapedKeyword;
        var query = '';
        var doneSomething = false;
        if (this.hasValue(input.givenNames)) {
            escapedGivenNames = this.escapeReservedChar(input.givenNames);
            query += 'given-names:' + escapedGivenNames;
            doneSomething = true;
        }
        if (this.hasValue(input.familyName)) {
            if (doneSomething) {
                query += ' AND ';
            }
            escapedFamilyName = this.escapeReservedChar(input.familyName);
            query += 'family-name:' + escapedFamilyName;
            doneSomething = true;
        }
        if (this.hasValue(input.searchOtherNames) && this.hasValue(input.givenNames)) {
            query += ' OR other-names:' + escapedGivenNames;
        }
        if (this.hasValue(input.keyword)) {
            if (doneSomething) {
                query += ' AND ';
            }
            escapedKeyword = this.escapeReservedChar(input.keyword);
            query += 'keyword:' + escapedKeyword;
            doneSomething = true;
        }
        if (this.hasValue(input.affiliationOrg)) {
            if (doneSomething) {
                query += ' AND ';
            }
            
            //if all chars are numbers, assume it's a ringgold id
            if (input.affiliationOrg.match(/^[0-9]*$/)) {
                query += 'ringgold-org-id:' + input.affiliationOrg;
            } else if(input.affiliationOrg.startsWith('grid.')) {
                escapedGridOrg = this.escapeReservedChar(input.affiliationOrg);
                query += 'grid-org-id:' + escapedGridOrg;
            } else {
                escapedAffiliationOrg = this.escapeReservedChar(input.affiliationOrg);
                query += 'affiliation-org-name:' + escapedAffiliationOrg;
            }
            doneSomething = true;
        }
        
        return doneSomething ? '?q=' + encodeURIComponent(query)
                + this.offset(input) : '?q=';
    }

    buildQuery(input: any) {
        if (this.hasValue(input.text)) {
            var orcidId = this.extractOrcidId(input.text);
            if (orcidId) {
                // Search for iD specifically
                return "?q=orcid:" + orcidId + this.offset(input);
            }
            // General quick search
            return '?q='
                    + encodeURIComponent(this.quickSearchEDisMax + input.text)
                    + this.offset(input);
        } else {
            // Advanced search
            return this.buildAdvancedSearchQuery(input);
        }
    };

    escapeReservedChar(inputText: any){
        //escape all reserved chars except double quotes
        //per https://lucene.apache.org/solr/guide/6_6/the-standard-query-parser.html#TheStandardQueryParser-EscapingSpecialCharacters
        var escapedText = inputText.replace(/([!^&*()+=\[\]\\/{}|:?~])/g, "\\$1");
        return escapedText.toLowerCase();
    }

    extractOrcidId(string: any) {
        var regexResult = this.orcidPathRegex.exec(string);
        if (regexResult) {
            return regexResult[0];
        }
        return null;
    }

    getBaseUri(): String {
        return getBaseUri();
    };

    getFirstResults(input: any){        
        this.showNoResultsAlert = false;
        this.allResults = new Array();
        this.numFound = 0;
        this.input.start = 0;
        this.input.rows = 10;
        this.areMoreResults = false;
        if(this.isValid()){
            this.hasErrors = false;
            this.searchResultsLoading = true;
            this.search(this.input);
        }
        else{
            this.hasErrors = true;
        }
    };

    getMoreResults(): any {
        this.showMoreLoading = true;
        this.input.start += 10;
        this.search(this.input);
    };

    search(input: any) {
        this.searchSrvc.getResults(this.buildQuery(this.input)).pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(
            searchResults => {
                this.newResults = searchResults['result'];
                this.numFound = searchResults['num-found'];
                this.searchResultsLoading = false;
                this.showMoreLoading = false;

                if (this.newResults) {
                    this.getDetails(this.newResults);
                    this.allResults = this.allResults.concat(this.newResults); 
                    this.cdr.detectChanges();
                }
                
                if(!this.numFound){
                    this.showNoResultsAlert = true;
                }

                this.areMoreResults = this.numFound > (this.input.start + this.input.rows);
                
                //if less than 10 results, show total number found
                if(this.numFound && this.numFound <= this.input.rows){
                    this.resultsShowing = this.numFound;
                }

                //if more than 10 results increment num found by 10
                if(this.numFound && this.numFound > this.input.rows){
                    if(this.numFound > (this.input.start + this.input.rows)){
                        this.resultsShowing = this.input.start + this.input.rows;
                    } else {
                        this.resultsShowing = (this.input.start + this.input.rows) - (this.input.rows - (this.numFound % this.input.rows));
                    }
                }
                
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
                            1000
                        );
                    }
                }

                this.cdr.detectChanges();
                
            }

        );
    }

    getAffiliations(result: any){
        if(!result['affiliationsRequestSent']){
            result['affiliationsRequestSent'] = true;
            result['affiliations'] = [];
            var orcid = result['orcid-identifier'].path;
            this.searchSrvc.getAffiliations(orcid).subscribe(
                affiliationsResult => {
                    if(affiliationsResult.employments){
                        for(var i in affiliationsResult.employments['employment-summary']){
                            if (result['affiliations'].indexOf(affiliationsResult.employments['employment-summary'][i]['organization']['name']) < 0){
                                result['affiliations'].push(affiliationsResult.employments['employment-summary'][i]['organization']['name']);
                            }
                        }
                    }
                    if(affiliationsResult.educations){
                        for(var i in affiliationsResult.educations['education-summary']){
                            if (result['affiliations'].indexOf(affiliationsResult.educations['education-summary'][i]['organization']['name']) < 0){
                                result['affiliations'].push(affiliationsResult.educations['education-summary'][i]['organization']['name']);
                            }
                        }
                    }
                }
            );
        } 
    };

    getNames(result: any){
        if(!result['namesRequestSent']){
            result['namesRequestSent'] = true;
            var name="";
            var orcid = result['orcid-identifier'].path;
            this.searchSrvc.getNames(orcid).subscribe(
                namesResult => {
                    if (namesResult['name']['given-names']){
                        result['given-names'] = namesResult['name']['given-names']['value'];
                    }
                    if(namesResult['name']['family-name']){
                        result['family-name'] = namesResult['name']['family-name']['value'];
                    }
                    if(namesResult['other-names']['other-name']) {
                        result['other-name'] = namesResult['other-names']['other-name'];
                    }
                }
            );
        } 
    };

    getDetails(orcidList: any) {
       for(var i = 0; i < orcidList.length; i++){
            this.getNames(orcidList[i]);
            this.getAffiliations(orcidList[i]);
       }
    };

    hasValue(ref: any) {
        return typeof ref !== 'undefined' && ref !== null && ref !== '';
    };

    isValid(): any {
        var fieldsToCheck = [ this.input.text, this.input.givenNames, this.input.familyName,
                this.input.keyword, this.input.affiliationOrg ];
        for ( var i = 0; i < fieldsToCheck.length; i++) {
            if (this.hasValue(fieldsToCheck[i])) {
                return true;
            }
        }
        return false;
    };

    isValidOrcidId(): any{
        if(typeof this.input.text === 'undefined' || this.input.text === null || this.input.text === '' || this.orcidFullRegex.exec(this.input.text.toUpperCase())){
            return true;
        }
        return false;
    }

    offset(input: any) {
        var start = this.hasValue(input.start) ? input.start : 0;
        var rows = this.hasValue(input.rows) ? input.rows : 10;
        return '&start=' + start + '&rows=' + rows;
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        var urlParams = new URLSearchParams(location.search);
        this.input.text = urlParams.get('searchQuery');
        if(typeof this.input.text !== 'undefined' && this.input.text != null){
            this.searchResultsLoading = true;
            this.search(this.input);
        }
    }

}
