declare var orcidVar: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, NgModule, OnInit, ChangeDetectorRef } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { BlogService } 
    from '../../shared/blog.service.ts';

import * as xml2js from 'xml2js';

@Component({
    selector: 'home-ng2',
    template:  scriptTmpl("home-ng2-template"),
})
export class HomeComponent implements OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
    blogFeed: any;
    
    constructor(
        private cdr:ChangeDetectorRef,
        private blogSrvc: BlogService,
    ) {
        this.blogFeed = {};
    }

    convertToJson(data: string): Object {
        let res;
        xml2js.parseString(data, { explicitArray: false }, (error, result) => {
            if (error) {
            throw new Error(error);
            } else {
            res = result;
            }
        });
        return res;
    }

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.blogSrvc.getBlogFeed(orcidVar.baseUri + "/blog/feed")
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            result => {
                //handle 302 response if blog is down
                if (result.indexOf('<html') < 0){
                    this.blogFeed=this.convertToJson(result);
                }
                this.cdr.detectChanges(); 
            },
            error => {
                console.log('error fetching blog feed: ', error);
            } 
        );
    }
}