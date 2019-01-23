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

    externalIdType
    metadataNotFound
    loading

    externalId = {
        DOI :{
            placeHolder: "10.1000/xyz123",
            value: "",
            url: () => '/works/resolve/doi?value='
        },
        arXiv : {
            placeHolder: "1501.00001",
            value: "",
            url: () => "/works/resolve/arxiv?value="
        },
        pubMed : {
            placeHolder: "12345678",
            value: "",
            url: (value) => {
                // Looks for a "pmc" string at the end of a url or a "PMC" string follow by at least 5 numbers 
                var regex = new RegExp(/((.*[\/,\\](pmc))|(PMC)\d{5})/g)
                var result = regex.exec(value)
                return result? "/works/resolve/pmc/?value=":  "/works/resolve/pmid?value=";
            }
            
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
                        this.externalId[this.externalIdType].value = ""
                        this.metadataNotFound = false;
                    }
                }
            }
        );
    };

    addWork() {
        this.metadataNotFound = false
        this.loading = true
        this.genericService.getData(this.externalId[this.externalIdType].url(this.externalId[this.externalIdType].value) + this.externalId[this.externalIdType].value).subscribe( data => {
            this.loading = false;
            if (!data) {
                this.metadataNotFound = true;
            } else {
                this.modalService.notifyOther({action:'close', moduleId: 'modalExternalIdForm'});
                this.worksService.removeBadContributors(data);
                this.worksService.removeBadExternalIdentifiers(data);
                this.worksService.addBibtexJson(data);
                this.modalService.notifyOther({action:'open', moduleId: 'modalWorksForm', edit: false, externalWork: data, bibtexWork: false});
            }
        }, 
        (error)=> {
            this.loading = false
        })
    }
    cancelEdit() {
        this.modalService.notifyOther({action:'close', moduleId: 'modalExternalIdForm'});
    }

}