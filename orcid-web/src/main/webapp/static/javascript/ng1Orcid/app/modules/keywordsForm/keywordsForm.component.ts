declare var $: any; //delete
declare var orcidVar: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, EventEmitter, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service.ts';

import { GenericService } 
    from '../../shared/generic.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'keywords-form-ng2',
    template:  scriptTmpl("keywords-form-ng2-template")
})
export class KeywordsFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    public newInput = new EventEmitter<boolean>();

    bulkEditShow: any; ///
    formData: any;
    formDataBeforeChange: any;
    newElementDefaultVisibility: string;
    orcidId: any;
    url_path: string;
 
    constructor(
        private cdr:ChangeDetectorRef,
        private commonSrvc: CommonService,
        private keywordsService: GenericService,
        private modalService: ModalService
    ) {
        this.bulkEditShow = false;
        this.formData = {};
        this.formDataBeforeChange = {};
        this.newElementDefaultVisibility = 'PRIVATE';
        this.orcidId = orcidVar.orcidId; //Do not remove
        this.url_path = '/my-orcid/keywordsForms.json';
    }

    addNew(): void {       
        var tmpObj = {
            "errors":[],
            "putCode":null,
            "content":"",
            "visibility":{
                "errors":[],
                "required":true,
                "getRequiredMessage":null,
                "visibility":this.newElementDefaultVisibility
            },
            "displayIndex":1,
            "source":this.orcidId,
            "sourceName":""
        };
        //console.log('add new keyword', tmpObj);  
        this.formData.keywords.push(tmpObj);
        this.cdr.detectChanges();       
        this.updateDisplayIndex();
        this.newInput.emit(true); 
    };

    closeEditModal(): void {
        this.formData = this.formDataBeforeChange;
        this.cdr.detectChanges();
        this.keywordsService.notifyOther();
        this.modalService.notifyOther({action:'close', moduleId: 'modalKeywordsForm'});
    };

    deleteKeyword( entry, index ): void{
        this.commonSrvc.hideTooltip('tooltip-keyword-delete-'+index);
        let keywords = this.formData.keywords;
        let len = keywords.length;
        while (len--) {
            if ( keywords[len] == entry ){
                keywords.splice(len,1);
                this.cdr.detectChanges();
            }
        }
    };

    getData(): void{
        this.keywordsService.getData( this.url_path )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formDataBeforeChange = JSON.parse(JSON.stringify(data));
                this.formData = data;
                ////console.log('keywords data', this.formData);
                this.newElementDefaultVisibility = this.formData.visibility.visibility;
                if ( this.formData.keywords.length == 0){
                    this.addNew();
                } else {
                    if(this.formData.keywords[0].putCode == null  ){
                        this.addNew();
                    }  
                }
            },
            error => {
                //console.log('getKeywordsFormError', error);
            } 
        );
    };

    setBulkGroupPrivacy(priv): void{
         for (var idx in this.formData.keywords){
            this.formData.keywords[idx].visibility.visibility = priv;        
        }
    };

    setForm( closeAfterAction ): void {

        this.keywordsService.setData( this.formData, this.url_path )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                if (this.formData.errors.length == 0){
                    this.getData();
                    this.keywordsService.notifyOther();
                    if( closeAfterAction == true ) {
                        this.closeEditModal();
                    }
                }else{
                    ////console.log(this.keywordsForm.errors);
                }

            },
            error => {
                ////console.log('setBiographyFormError', error);
            } 
        );
        this.formData.visibility = null;
    };
    
    privacyChange( obj ): any {
        this.formData.visibility.visibility = obj;  
    };
        
    swapDown(index): void{
        let temp = null;
        let tempDisplayIndex = null;

        if (index < this.formData.keywords.length - 1) {
            temp = this.formData.keywords[index];
            tempDisplayIndex = this.formData.keywords[index]['displayIndex'];
            temp['displayIndex'] = this.formData.keywords[index + 1]['displayIndex']
            this.formData.keywords[index] = this.formData.keywords[index + 1];
            this.formData.keywords[index]['displayIndex'] = tempDisplayIndex;
            this.formData.keywords[index + 1] = temp;
        }
    };

    swapUp(index): void{
        let temp = null;
        let tempDisplayIndex = null;

        if (index > 0) {
            temp = this.formData.keywords[index];
            tempDisplayIndex = this.formData.keywords[index]['displayIndex'];
            temp['displayIndex'] = this.formData.keywords[index - 1]['displayIndex']
            this.formData.keywords[index] = this.formData.keywords[index - 1];
            this.formData.keywords[index]['displayIndex'] = tempDisplayIndex;
            this.formData.keywords[index - 1] = temp;
        }
    };

    updateDisplayIndex(): void{
        let idx: any;
        for (idx in this.formData.keywords) {
            this.formData.keywords[idx]['displayIndex'] = this.formData.keywords.length - idx;
        }
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        this.subscription = this.keywordsService.notifyObservable$.subscribe(
            (res) => {
                this.getData();
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getData();
    };
}