declare var ActSortState: any;
declare var GroupedActivities: any;
declare var om: any;
declare var openImportWizardUrl: any;
declare var typeahead: any;
declare var workIdLinkJs: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, of, Subject, Subscription } 
    from 'rxjs';

import { catchError, debounceTime, distinctUntilChanged, filter, map, switchMap, takeUntil, tap } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service.ts';

import { WorksService } 
    from '../../shared/works.service.ts';

import { FeaturesService }
    from '../../shared/features.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts';
    
import { GenericService } 
    from '../../shared/generic.service.ts';

@Component({
    selector: 'works-external-id-form-ng2',
    template:  scriptTmpl("works-external-id-form-ng2-template")
})

export class WorksExternalIdFormComponent implements AfterViewInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    togglzDialogPrivacyOption: boolean;
    externalIdType
    serverError

    externalId = {
        DOI :{
            placeHolder: "10.1000/xyz123",
            value: "",
            url: '/works/resolve/doi?value='
        },
        arXiv : {
            placeHolder: "/arXiv:1501.00001",
            value: "",
            url : "/works/resolve/arxiv?value="
        },
        pubMed : {
            placeHolder: "arXiv:1501.00001",
            value: "",
            url: "/works/resolve/pmc?value="
        }
    }

    constructor( 
        private worksService : WorksService,
        private modalService: ModalService,
        private genericService: GenericService
    ) {

    }

    ngAfterViewInit() {
        this.modalService.notifyObservable$.subscribe(
            (res) => {
                if(res.moduleId == "modalExternalIdForm") {
                    if(res.action == "open") {
                        this.externalIdType = res.externalIdType;
                    }
                }
            }
        );
    };

    addWork() {
        this.genericService.getData(this.externalId[this.externalIdType].url + this.externalId[this.externalIdType].value).subscribe( data => {
            this.modalService.notifyOther({action:'close', moduleId: 'modalExternalIdForm'});
            this.modalService.notifyOther({action:'open', moduleId: 'modalWorksForm', edit: false, externalWork: data});
        })
    }
    cancelEdit() {
        this.modalService.notifyOther({action:'close', moduleId: 'modalExternalIdForm'});
    }

}