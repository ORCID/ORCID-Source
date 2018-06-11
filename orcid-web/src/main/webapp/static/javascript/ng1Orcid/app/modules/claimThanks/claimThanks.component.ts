declare var $: any;
declare var orcidGA: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';


@Component({
    selector: 'claim-thanks-ng2',
    template:  scriptTmpl("claim-thanks-ng2-template")
})
export class ClaimThanksComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    sourceGrantReadWizard: any;

    constructor(
    ) {
        this.sourceGrantReadWizard = null;
    }

    close(): void {
        $.colorbox.close();
    };

    /* *** */
    getSourceGrantReadWizard(): void {
        /*
        $.ajax({
            url: getBaseUri() + '/my-orcid/sourceGrantReadWizard.json',
            dataType: 'json',
            success: function(data) {
                $scope.sourceGrantReadWizard = data;
                $scope.$apply();
                $scope.showThanks();
            }
        }).fail(function(){
            // something bad is happening!
            //console.log("error fetching external identifiers");
        });
        */
    };

    showThanks(): void {
        var colorboxHtml;
        if (this.sourceGrantReadWizard.url == null) {
            //colorboxHtml = $compile($('#claimed-record-thanks').html())($scope);
        }
        else {
            //colorboxHtml = $compile($('#claimed-record-thanks-source-grand-read').html())($scope);
        }
        /*
        $.colorbox({
            html : colorboxHtml,
            escKey: true,
            overlayClose: true,
            transition: 'fade',
            close: '',
            scrolling: false
                    });
        $scope.$apply(); // this seems to make sure angular renders in the colorbox
        $.colorbox.resize();
        */
    };

    yes(): void {
        /*
        $.colorbox.close();
        var newWin = window.open($scope.sourceGrantReadWizard.url);
        if (!newWin) {
            window.location.href = $scope.sourceGrantReadWizard.url;
        }
        else {
            newWin.focus();
        }
        */
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
        this.getSourceGrantReadWizard();
    }; 
}