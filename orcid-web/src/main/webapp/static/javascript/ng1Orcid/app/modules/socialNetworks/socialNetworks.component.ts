declare var $window: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { SocialNetworkService } 
    from '../../shared/socialNetwork.service.ts';


@Component({
    selector: 'social-networks-ng2',
    template:  scriptTmpl("social-networks-ng2-template")
})
export class SocialNetworksComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    twitter: boolean;

    constructor(
        private socialNetworkService: SocialNetworkService
    ) {
        this.twitter = false;
    }

    checkTwitterStatus(): void {

        this.socialNetworkService.checkTwitterStatus()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data == "true"){
                    this.twitter = true;
                } else {
                    this.twitter = false;
                }
            },
            error => {
                //console.log('getWebsitesFormError', error);
            } 
        );
    };

    updateTwitter(): void {
        if(this.twitter == true) {
            this.socialNetworkService.enableTwitter()
            .takeUntil(this.ngUnsubscribe)
            .subscribe(
                data => {
                    window.location.href = data;
                },
                error => {
                    //console.log('getWebsitesFormError', error);
                } 
            );

        } else {
            this.socialNetworkService.disableTwitter()
            .takeUntil(this.ngUnsubscribe)
            .subscribe(
                data => {
                    if(data == "true"){
                        this.twitter = false;
                    } else {
                        this.twitter = true;
                    }
                },
                error => {
                    //console.log('getWebsitesFormError', error);
                } 
            );
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
        this.checkTwitterStatus();
    }; 
}