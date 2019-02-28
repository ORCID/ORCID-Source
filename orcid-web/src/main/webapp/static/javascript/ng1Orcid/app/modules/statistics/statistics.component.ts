//not used

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

import { GenericService } 
    from '../../shared/generic.service.ts';


@Component({
    selector: 'statistics-ng2',
    template:  scriptTmpl("statistics-ng2-template")
})
export class StatisticsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    stats: any;

    constructor(
        private statisticsService: GenericService
    ) {
    }

    getStats(): void {
        this.statisticsService.getData( '/statistics/statistics.json' )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.stats = data;
            },
            error => {
                //console.log('error fetching stats', error);
            } 
        );
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
        this.getStats();
    }; 
}