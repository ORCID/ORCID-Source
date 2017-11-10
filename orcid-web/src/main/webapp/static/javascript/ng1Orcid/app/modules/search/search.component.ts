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

import { SearchSrvc } 
    from '../../shared/search.service.ts';


@Component({
    selector: 'search-ng2',
    template:  scriptTmpl("search-ng2-template"),
    providers: [SearchSrvc]
})
export class SearchComponent {
    searchResults: any;
    results: any;
    url: string;
    input: any;
    constructor(

        private searchSrvc: SearchSrvc,
       
    ) {
        this.input = [];
        this.searchResults = [];
        this.results = [];
    }

    search(input: any) {
        console.log(this.input);
        this.searchSrvc.getResults(orcidSearchUrlJs.buildUrl(this.input)).subscribe(
            searchResults => {
                this.searchResults = searchResults;
                this.results = searchResults.result;
                console.log(this.searchResults);
                console.log(searchResults['num-found']);
            }
        );
    }

}
