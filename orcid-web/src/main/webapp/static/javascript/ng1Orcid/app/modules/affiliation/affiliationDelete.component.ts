declare var $: any;
declare var ActSortState: any;
declare var GroupedActivities: any;
declare var groupedActivitiesUtil: any;
declare var sortState: any;
declare var typeahead: any;

//Import all the angular components
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

import { AffiliationService } 
    from '../../shared/affiliation.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { GroupedActivitiesUtilService } 
    from '../../shared/groupedActivities.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

import { FeaturesService }
    from '../../shared/features.service.ts' 
    
import { CommonService } 
    from '../../shared/common.service.ts';

@Component({
    selector: 'affiliation-delete-ng2',
    template:  scriptTmpl("affiliation-delete-ng2-template")
})
export class AffiliationDeleteComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    /*
    emailSrvc: any;
    workspaceSrvc: any;
    */

    deleteAffiliationObj: any;

    

    constructor(
        private affiliationService: AffiliationService,
        private emailService: EmailService,
        //private groupedActivitiesUtilService: GroupedActivitiesUtilService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService,
        private featuresService: FeaturesService,
        private commonSrvc: CommonService,
    ) {

        this.deleteAffiliationObj = null;

    }


    close(): void {
        //$.colorbox.close();
    };

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalAffiliationDelete'});
    };


    deleteAffiliation(): void {
        
        console.log('delete', this.deleteAffiliationObj);
        
        this.affiliationService.deleteAffiliation(this.deleteAffiliationObj)
            .takeUntil(this.ngUnsubscribe)
            .subscribe(data => {       
                /*  
                if(data.errors.length == 0) {
                    if(this.deleteAffiliationObj.affiliationType != null && this.deleteAffiliationObj.affiliationType.value != null) {
                        if(this.deleteAffiliationObj.affiliationType.value == 'distinction' || this.deleteAffiliationObj.affiliationType.value == 'invited-position') {
                            this.removeFromArray(this.distinctionsAndInvitedPositions, affiliation.putCode.value);
                        } else if (affiliation.affiliationType.value == 'education' || affiliation.affiliationType.value == 'qualification'){
                            this.removeFromArray(this.educationsAndQualifications, affiliation.putCode.value);
                            if(affiliation.affiliationType.value == 'education') {
                                this.removeFromArray(this.educations, affiliation.putCode.value);
                            }                            
                        } else if (affiliation.affiliationType.value == 'employment'){
                            this.removeFromArray(this.employments, affiliation.putCode.value);                            
                        } else if(affiliation.affiliationType.value == 'membership' || affiliation.affiliationType.value == 'service') {
                            this.removeFromArray(this.membershipsAndServices, affiliation.putCode.value);                            
                        } 
                    }                    
                }
                */                            
            });         
        
    };
    
    removeFromArray(affArray, putCode): void {
        console.log("putCode: " + putCode);
        console.log(affArray);
        for(let idx in affArray) {
            if(affArray[idx].putCode.value == putCode) {
                affArray.splice(idx, 1);
                break;
            }
        }
    };

 
    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.affiliationService.notifyObservable$.subscribe(
            (res) => {
                this.deleteAffiliationObj = res.affiliation;
                console.log('res.affiliation',res);
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
