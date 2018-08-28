declare var $: any;
declare var ActSortState: any;
declare var GroupedActivities: any;
declare var groupedActivitiesUtil: any;
declare var sortState: any;
declare var typeahead: any;

//Import all the angular components
import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { AffiliationService } 
    from '../../shared/affiliation.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

import { FeaturesService }
    from '../../shared/features.service.ts' 
    
import { CommonService } 
    from '../../shared/common.service.ts';



@Component({
    selector: 'affiliation-form-ng2',
    template:  scriptTmpl("affiliation-form-ng2-template")
})
export class AffiliationFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
    private viewSubscription: Subscription;

    addingAffiliation: boolean;
    disambiguatedAffiliation: any;
    editAffiliation: any;
    addAffType: any;
    

    constructor(
        private affiliationService: AffiliationService,
        private commonSrvc: CommonService,
        private modalService: ModalService,
    ) {
 
        this.addingAffiliation = false;
        this.disambiguatedAffiliation = null;
        this.editAffiliation = this.getEmptyAffiliation();
        this.addAffType = null;
    }

    getEmptyAffiliation(): any {
        return {
            affiliationType: {
                errors: [],
                value: ""
            },
            affiliationName: {
                errors: [],
                value: ""
            },
            city: {
                errors: [],
                value: ""
            },
            country: {
                errors: [],
                value: ""
            },
            departmentName: {
                errors: [],
                value: ""
            },
            disambiguatedAffiliationSourceId: "",
            disambiguationSource: "",
            endDate: {
                errors: [],
                month: "",
                day: "",
                year: ""
            },
            errors: [],
            orgDisambiguatedId: {
                value: ""
            },
            putCode: {
                value: null
            },
            region: {
                errors: [],
                value: ""
            },
            roleTitle: {
                errors: [],
                value: ""
            },
            startDate: {
                errors: [],
                month: "",
                day: "",
                year: ""
            },
            url: {
                errors: [],
                value: ""
            },
        };
    }
    
     addAffiliation(): void {
        if (this.addingAffiliation == true) {
            return; // don't process if adding affiliation
        }

        this.addingAffiliation = true;
        this.editAffiliation.errors.length = 0;
        this.affiliationService.setData( this.editAffiliation )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.editAffiliation = data;
                this.addingAffiliation = false;
                if (data.errors.length > 0){                    
                    this.editAffiliation = data;
                    this.commonSrvc.copyErrorsLeft(this.editAffiliation, data);
                } else {
                    this.unbindTypeahead();
                    this.modalService.notifyOther({action:'close', moduleId: 'modalAffiliationForm'});
                    this.removeDisambiguatedAffiliation();
                    this.editAffiliation = this.getEmptyAffiliation();
                    this.affiliationService.notifyOther({action:'add', successful:true});
                }
            },
            error => {
                console.log('affiliationForm.component.ts addAffiliation Error', error);
            } 
        );
    };

    bindTypeahead(): void {
        let numOfResults = 100;

        $("#affiliationName").typeahead({
            name: 'affiliationName',
            limit: numOfResults,
            remote: {
                url: getBaseUri()+'/affiliations/disambiguated/name/%QUERY?limit=' + numOfResults
            },
            template: function (datum) {
                var forDisplay =
                    '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value+ '</span>'
                    +'<span style=\'font-size: 80%;\'>'
                    + ' <br />' + datum.city;
                if(datum.region){
                    forDisplay += ", " + datum.region;
                }
                if (datum.orgType != null && datum.orgType.trim() != ''){
                    forDisplay += ", " + datum.orgType;
                }
                forDisplay += '</span><hr />';
                return forDisplay;
            }
        });

        $('#affiliationName').bind(
            "typeahead:selected", 
            (
                function(obj, datum) {
                    this.selectAffiliation(datum);
                }
            ).bind(this)
        );
    };

    cancelEdit(): void {
        this.unbindTypeahead();
        this.modalService.notifyOther({action:'close', moduleId: 'modalAffiliationForm'});
        this.affiliationService.notifyOther({action:'cancel', successful:true});
    };

    getDisambiguatedAffiliation = function(id) {
        this.affiliationService.getDisambiguatedAffiliation(id)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (data != null) {
                    this.disambiguatedAffiliation = data;
                    this.editAffiliation.orgDisambiguatedId.value = id;
                    this.editAffiliation.disambiguatedAffiliationSourceId = data.sourceId;
                    this.editAffiliation.disambiguationSource = data.sourceType;
                }
            },
            error => {
                console.log("getAffiliationsId", id, error);
            } 
        );
    };

    isValidClass(cur): any {
        let valid = true;

        if (cur === undefined) {
            return '';
        }
        if ( 
            ( cur.required && (cur.value == null || cur.value.trim() == '') ) 
            || 
            ( cur.errors !== undefined && cur.errors.length > 0 ) 
        ) {
            valid = false;
        }

        return valid ? '' : 'text-error';
    };

    isValidStartDate(start): any {
        if (start === undefined) {
            return '';
        }
        
        if (start.errors !== undefined && start.errors.length > 0) {
            return 'text-error';
        }
        
        return '';
    };

    removeDisambiguatedAffiliation(): void {
        this.bindTypeahead();
        
        if (this.disambiguatedAffiliation != undefined) {
            delete this.disambiguatedAffiliation;
        }
        
        if (this.editAffiliation != undefined && this.editAffiliation.disambiguatedAffiliationSourceId != undefined) {
            delete this.editAffiliation.disambiguatedAffiliationSourceId;
        }
        
        if (this.editAffiliation != undefined && this.editAffiliation.orgDisambiguatedId != undefined) {
            delete this.editAffiliation.orgDisambiguatedId;
        }

        this.disambiguatedAffiliation = null;
    };

    selectAffiliation(datum): void {        
        if (datum != undefined && datum != null) {
            this.editAffiliation.affiliationName.value = datum.value;
            this.editAffiliation.city.value = datum.city;
            
            if(datum.city) {
                this.editAffiliation.city.errors = [];
            }

            this.editAffiliation.region.value = datum.region;
            
            if(datum.region){
                this.editAffiliation.region.errors = [];
            }
            
            if(datum.country != undefined && datum.country != null) {
                this.editAffiliation.country.value = datum.country;
                this.editAffiliation.country.errors = [];
            }

            if (datum.disambiguatedAffiliationIdentifier != undefined && datum.disambiguatedAffiliationIdentifier != null) {
                this.getDisambiguatedAffiliation(datum.disambiguatedAffiliationIdentifier);
                this.unbindTypeahead();
            }
        }
    };

    serverValidate(relativePath): void {
        if( relativePath == 'affiliations/affiliation/datesValidate.json' ){
            if( this.editAffiliation.startDate.month == "" 
                || this.editAffiliation.startDate.day == ""
                || this.editAffiliation.startDate.year == ""
                || this.editAffiliation.endDate.month == "" 
                || this.editAffiliation.endDate.day == ""
                || this.editAffiliation.endDate.year == ""
                || this.editAffiliation.startDate.month == null 
                || this.editAffiliation.startDate.day == null
                || this.editAffiliation.startDate.year == null
                || this.editAffiliation.endDate.month == null 
                || this.editAffiliation.endDate.day == null
                || this.editAffiliation.endDate.year == null  ){
                return;
            }
        }
        this.affiliationService.serverValidate(this.editAffiliation, relativePath)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (data != null) {
                    this.commonSrvc.copyErrorsLeft(this.editAffiliation, data);
                }
            },
            error => {
            } 
        );
    }

    setAddAffiliationPrivacy(priv, $event): void {
        $event.preventDefault();
        this.editAffiliation.visibility.visibility = priv;
    };

    checkAvailableDays(day, month, year): boolean {
        if( day > new Date(year, month, 0).getDate() ){
            return true
        }
        return false;
    }

    unbindTypeahead(): void {
        $('#affiliationName').typeahead('destroy');
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.affiliationService.notifyObservable$.subscribe(
            (res) => {
                this.bindTypeahead();
                this.addAffType = res.type;
                if( res.affiliation != undefined ) {
                    this.editAffiliation = res.affiliation;
                    if(this.editAffiliation.orgDisambiguatedId != null){
                        this.getDisambiguatedAffiliation(this.editAffiliation.orgDisambiguatedId.value);
                    }
                } else {
                    this.editAffiliation = this.getEmptyAffiliation();
                    this.editAffiliation.affiliationType.value = this.addAffType;
                }
            }
        );
        
        this.viewSubscription = this.modalService.notifyObservable$.subscribe(
                (res) => {

                    if(res.moduleId == "modalAffiliationForm") {
                        if(res.action == "open" && res.edit == false) {
                            this.editAffiliation = this.getEmptyAffiliation();
                            this.editAffiliation.affiliationType.value = this.addAffType;
                        }
                    }
                }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        if( !this.affiliationService.affiliation ){
            this.addAffType = this.affiliationService.type;     
        } 
    }; 
}
