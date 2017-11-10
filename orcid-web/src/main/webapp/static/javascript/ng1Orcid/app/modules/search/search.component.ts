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
    url: string;
    input: any;

    constructor(

        private searchSrvc: SearchSrvc,
       
    ) {

        this.input = {rows: 10, start: 0, text: undefined, givenNames: "lizbert"};
        this.url = orcidSearchUrlJs.buildUrl(this.input);

    }

    search(): void {
        this.searchSrvc.getResults(this.url).subscribe(
            searchResults => {
                let searchResults_parsed = null;
                this.searchResults = searchResults;
                searchResults_parsed = JSON.parse(JSON.stringify(this.searchResults, null, 2));
                console.log(searchResults_parsed);
            }
        );
    }

    /*private ngUnsubscribe: Subject<void> = new Subject<void>();

    countryForm: any;
    countryFormAddresses: any;
    emails: any;
    emailSrvc: any;

    constructor( 
        private countryService: CountryService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {

        this.countryForm = {
            addresses: {
            }
        };
        this.countryFormAddresses = [];
        this.emails = {};
    }

    getCountryForm(): void {
        this.countryService.getCountryData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.countryForm = data;
                this.countryFormAddresses = this.countryForm.addresses;
                //console.log('this.countryForm', this.countryForm);
            },
            error => {
                console.log('getCountryFormError', error);
            } 
        );
    };

    openEditModal(): void{      
        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.modalService.notifyOther({action:'open', moduleId: 'modalCountryForm'});
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                console.log('getEmails', error);
            } 
        );
    };*/

}
