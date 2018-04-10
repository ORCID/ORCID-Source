declare var $: any; //delete
declare var orcidVar: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { CommonService } 
    from '../../shared/common.service.ts';

import { KeywordsService } 
    from '../../shared/keywords.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'keywords-form-ng2',
    template:  scriptTmpl("keywords-form-ng2-template")
})
export class KeywordsFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    bulkEditShow: any; ///
    formData: any;
    formDataBeforeChange: any;
    newElementDefaultVisibility: string;
    newInput: boolean;
    orcidId: any;
 
    constructor(
        private commonSrvc: CommonService,
        private keywordsService: KeywordsService,
        private modalService: ModalService
    ) {
        this.bulkEditShow = false;
        this.formData = {};
        this.formDataBeforeChange = {};
        this.newElementDefaultVisibility = 'PRIVATE';
        this.newInput = false;
        this.orcidId = orcidVar.orcidId; //Do not remove
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
        this.updateDisplayIndex();
        this.newInput = true;
    };

    closeEditModal(): void {
        this.formData = this.formDataBeforeChange;
        this.modalService.notifyOther({action:'close', moduleId: 'modalKeywordsForm'});
    };

    deleteKeyword( entry ): void{
        let keywords = this.formData.keywords;
        let len = keywords.length;
        while (len--) {
            if ( keywords[len] == entry ){
                keywords.splice(len,1);
            }
        }
    };


    getData(): void{
        this.keywordsService.getData()
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

    };

    setForm( closeAfterAction ): void {

        this.keywordsService.setData( this.formData )
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
        //this.setForm( false );   
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
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getData();
    };
}