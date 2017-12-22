declare var $: any;
declare var orcidSearchUrlJs: any;
declare var orcidVar: any;

import { NgFor, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { SearchService } 
    from '../../shared/search.service.ts';


@Component({
    selector: 'search-ng2',
    template:  scriptTmpl("search-ng2-template"),
    providers: [SearchService]
})
export class SearchComponent {
    areMoreResults: any;
    hasErrors: any;
    input: any;
    numFound: any;
    searchResults: any;
    results: any;
    resultsShowing: any;
    url: string;
    constructor(

        private searchSrvc: SearchService,
       
    ) {
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
        this.results = new Array();
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

        return result['affiliations'].join(", "); 
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

        if (result['given-names']) {
            name = result['given-names'];
        } else {
            name = "";
        }           
        return name; 
    }

    search(input: any) {
        this.searchSrvc.getResults(orcidSearchUrlJs.buildUrl(this.input)).subscribe(
            searchResults => {
                this.searchResults = searchResults;
                var bottom = null;
                var newSearchResults = null;
                var newSearchResultsTop = null;
                var showMoreButtonTop = null;
                $('#ajax-loader-search').hide();
                $('#ajax-loader-show-more').hide();
                var orcidList = searchResults['result'];
                
                this.numFound = searchResults['num-found'];

                this.results = this.results.concat(orcidList); 
                
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
            }
        );
    }

    areResults(): any {
        return this.results.length > 0;
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

}
