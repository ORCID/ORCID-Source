import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { AlsoKnownAsService } 
    from '../../shared/alsoKnownAs.service.ts';

import { CommonService } 
    from '../../shared/common.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'also-known-as-form-ng2',
    template:  scriptTmpl("also-known-as-form-ng2-template")
})
export class AlsoKnownAsFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    defaultVisibility: any;
    emails: any;
    formData: any;
    formDataBeforeChange: any;
    newElementDefaultVisibility: any;
    orcidId: any;
    privacyHelp: any;
    scrollTop: any;
    showEdit: any;
    showElement: any;

    constructor( 
        private alsoKnownAsService: AlsoKnownAsService,
        private cdr:ChangeDetectorRef,
        private commonSrvc: CommonService,
        private modalService: ModalService
    ) {
        this.defaultVisibility = null;
        this.emails = {};
        this.formData = {
        };
        this.formDataBeforeChange = {};
        this.newElementDefaultVisibility = 'PRIVATE';
        this.orcidId = orcidVar.orcidId; 
        this.privacyHelp = false;
        this.scrollTop = 0;
        this.showEdit = false;
        this.showElement = {};
    }

    addNew(): void {
        let tmpObj = {
            "errors":[],
            "url":null,
            "urlName":null,
            "putCode":null,
            "visibility":{
                "errors":[],
                "required":true,
                "getRequiredMessage":null,
                "visibility": this.newElementDefaultVisibility
            },
            "source":this.orcidId,
            "sourceName":"", 
            "displayIndex": 1
        };        
        this.formData.otherNames.push(tmpObj);        
        this.updateDisplayIndex();    
    };

    closeEditModal(): void{
        this.formData = this.formDataBeforeChange;
        this.modalService.notifyOther({action:'close', moduleId: 'modalAlsoKnownAsForm'});
    };

    deleteOtherName(otherName): void{
        let otherNames = this.formData.otherNames;
        let len = otherNames.length;
        while (len--) {            
            if (otherNames[len] == otherName){                
                otherNames.splice(len,1);
                this.cdr.detectChanges();
            }
        }        
    };

    getformData(): void {
        this.alsoKnownAsService.getData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formDataBeforeChange = JSON.parse(JSON.stringify(data));
                this.formData = data;
                this.newElementDefaultVisibility = this.formData.visibility.visibility;
                ////console.log('this.getForm', this.formData);
                if ( this.formData.otherNames.length == 0 ) {
                    this.addNew();
                }
            },
            error => {
                //console.log('getAlsoKnownAsFormError', error);
            } 
        );
    };

    privacyChange( obj ): any {
        this.formData.visibility.visibility = obj;  
    };

    setBulkGroupPrivacy(priv): void{
        for (var idx in this.formData.otherNames){
            this.formData.otherNames[idx].visibility.visibility = priv;        
        }
    };

    setFormData( closeAfterAction ): void {
        this.alsoKnownAsService.setData( this.formData )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                if (this.formData.errors.length == 0){
                    this.getformData();
                    this.alsoKnownAsService.notifyOther();
                    if( closeAfterAction == true ) {
                        this.closeEditModal();
                    }
                }else{
                    //console.log(this.formData.errors);
                }

            },
            error => {
                //console.log('setAlsoKnownAs', error);
            } 
        );
        this.formData.visibility = null;
    }

    swapDown(index): void{
        let temp;
        let tempDisplayIndex;
        if (index < this.formData.otherNames.length - 1) {
            temp = this.formData.otherNames[index];
            tempDisplayIndex = this.formData.otherNames[index]['displayIndex'];
            temp['displayIndex'] = this.formData.otherNames[index + 1]['displayIndex']
            this.formData.otherNames[index] = this.formData.otherNames[index + 1];
            this.formData.otherNames[index]['displayIndex'] = tempDisplayIndex;
            this.formData.otherNames[index + 1] = temp;
        }
    };

    swapUp(index): void{
        let temp;
        let tempDisplayIndex;
        if (index > 0) {
            temp = this.formData.otherNames[index];
            tempDisplayIndex =this.formData.otherNames[index]['displayIndex'];
            temp['displayIndex'] = this.formData.otherNames[index - 1]['displayIndex']
            this.formData.otherNames[index] = this.formData.otherNames[index - 1];
            this.formData.otherNames[index]['displayIndex'] = tempDisplayIndex;
            this.formData.otherNames[index - 1] = temp;
        }
    };

    updateDisplayIndex(): void{
        let idx: any;
        for (idx in this.formData.otherNames) {         
            this.formData.otherNames[idx]['displayIndex'] = this.formData.otherNames.length - idx;
        }
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getformData();
    };

}
