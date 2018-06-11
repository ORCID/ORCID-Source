declare var $: any;
declare var orcidSearchUrlJs: any;
declare var orcidVar: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit, ChangeDetectorRef } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import 'rxjs/add/operator/takeUntil';

import { CommonNg2Module }
    from './../common/common.ts';

import { FeaturesService }
    from '../../shared/features.service.ts'

import { SearchService } 
    from '../../shared/search.service.ts';

@Component({
    selector: 'search-ng2',
    template:  scriptTmpl("search-ng2-template")
})
export class SearchComponent implements OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    allResults: any;
    areMoreResults: any;
    hasErrors: any;
    input: any;
    newResults: any;
    numFound: any;
    searchResults: any;
    searchResultsAffiliationsFeatureEnabled: boolean = this.featuresService.isFeatureEnabled('SEARCH_RESULTS_AFFILIATIONS');
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
        this.input.text = $('#SearchCtrl').data('search-query');
        this.numFound = 0;
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

    getFirstResults(input: any){
        $('#no-results-alert').hide();
        this.allResults = new Array();
        this.numFound = 0;
        this.input.start = 0;
        this.input.rows = 10;
        this.areMoreResults = false;
        if(this.isValid()){
            this.hasErrors = false;
            $('#ajax-loader-search').show();
            this.search(this.input);
        }
        else{
            this.hasErrors = true;
        }
    };

    getMoreResults(): any {
        $('#ajax-loader-show-more').show();
        this.input.start += 10;
        this.search(this.input);
    };

    search(input: any) {
        this.searchSrvc.getResults(orcidSearchUrlJs.buildUrl(this.input)).takeUntil(this.ngUnsubscribe).subscribe(
            searchResults => {
                this.newResults = searchResults['result'];
                this.numFound = searchResults['num-found'];
                $('#ajax-loader-search').hide();
                $('#ajax-loader-show-more').hide();

                this.getDetails(this.newResults);

                this.allResults = this.allResults.concat(this.newResults); 

                this.cdr.detectChanges();
                
                if(!this.numFound){
                    $('#no-results-alert').fadeIn(1200);
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
                            1000,
                            'easeOutQuint'
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
    }

    getDetails(orcidList: any) {
       for(var i = 0; i < orcidList.length; i++){
            this.getNames(orcidList[i]);
            this.getAffiliations(orcidList[i]);
       }
    }

    areResults(): any {
        return this.allResults.length > 0;
    }

    isValid(): any {
        return orcidSearchUrlJs.isValidInput(this.input);
    };

    isValidOrcidId(): any{
        if(typeof this.input.text === 'undefined' || this.input.text === null || this.input.text === '' || orcidSearchUrlJs.isValidOrcidId(this.input.text)){
            return true;
        }
        return false;
    }

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.input.text = $('#SearchCtrl').data('search-query');
        if(typeof this.input.text !== 'undefined'){
            $('#ajax-loader-search').show();
            this.search(this.input);
        }
    }

}
