declare var $: any;

//Import all the angular components
import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { WorksService } 
    from '../../shared/works.service';

import { ModalService } 
    from '../../shared/modal.service'; 

@Component({
    selector: 'works-delete-ng2',
    template:  scriptTmpl("works-delete-ng2-template")
})
export class WorksDeleteComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    deleteGroup: any;
    fixedTitle: any;
    formData: any;
    putCode: any;
    sortState: any;

    constructor(
        private worksService: WorksService,
        private modalService: ModalService
    ) {


    }

    cancelEdit(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalWorksDelete'});
        this.worksService.notifyOther({action:'cancel', successful:true});
    };

    deleteByPutCode(putCode, deleteGroup): void {
        //this.closeAllMoreInfo();
        this.deleteWork(putCode);
    };

    deleteWork(putCodes): void {
        this.worksService.removeWorks([putCodes])
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (putCodes.length > 0) {
                    this.worksService.removeWorks([putCodes]);
                }
                this.modalService.notifyOther({action:'close', moduleId: 'modalWorksDelete'});
                this.worksService.notifyOther({action:'delete', successful:true});
            },
            error => {
                console.log('Error deleting work', error);
            } 
        ); 
    }

    //probably not used in editworksform - check and remove if not needed
    deleteGroupWorks(putCodes): void {
        var rmWorks = [];
        var rmGroups = [];
        for (var i in putCodes) {
            for (var j in this.worksService.groups) {
                for (var k in this.worksService.groups[j].works) {
                    if (this.worksService.groups[j].works[k].putCode.value == putCodes[i]) {
                        rmGroups.push(j);
                        for (var y in this.worksService.groups[j].works){
                            rmWorks.push(this.worksService.groups[j].works[y].putCode.value);
                        }
                        break;
                    }
                }
            }
        }
        while (rmGroups.length > 0) {
            this.worksService.groups.splice(rmGroups.pop(),1);
        }
        this.worksService.removeWorks(rmWorks)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (rmWorks.length > 0) {
                    this.worksService.removeWorks(rmWorks);
                }
                this.modalService.notifyOther({action:'close', moduleId: 'modalWorksDelete'});
                this.worksService.notifyOther({action:'delete', successful:true});
            },
            error => {
                console.log('Error deleting work', error);
            } 
        ); 
    }

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {
                if( res.fixedTitle ) {
                    this.fixedTitle = res.fixedTitle;
                }
                if( res.putCode ) {
                    this.putCode = res.putCode;
                }
                if( res.deleteGroup ) {
                    this.deleteGroup = res.deleteGroup;
                }
                if( res.sortState ) {
                    this.sortState = res.sortState;
                }
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
    }; 
}
