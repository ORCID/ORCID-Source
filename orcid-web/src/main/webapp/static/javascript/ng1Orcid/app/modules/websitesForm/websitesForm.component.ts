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

import { WebsitesService } 
    from '../../shared/websites.service.ts';

import { CommonService } 
    from '../../shared/common.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'websites-form-ng2',
    template:  scriptTmpl("websites-form-ng2-template")
})

export class WebsitesFormComponent implements AfterViewInit, OnDestroy, OnInit {
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
        private cdr:ChangeDetectorRef,
        private commonSrvc: CommonService,
        private modalService: ModalService,
        private websitesService: WebsitesService
    ) {
        this.defaultVisibility = null;
        this.emails = {};
        this.formData = {
            websites: null
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
            "url": {
                value: null
            },
            "urlName":null,
            "putCode":null,
            "visibility":{
                "errors":[],
                "required":true,
                "getRequiredMessage":null,
                "visibility":this.newElementDefaultVisibility
            },
            "source":this.orcidId,
            "sourceName":"", 
            "displayIndex": 1
        };         
        this.formData.websites.push(tmpObj);        
        this.updateDisplayIndex();    
    };

    closeEditModal(): void{
        this.formData = this.formDataBeforeChange;
        this.modalService.notifyOther({action:'close', moduleId: 'modalWebsitesForm'});
    };

    deleteEntry( website ): void{
        let websites = this.formData.websites;
        let len = websites.length;
        while (len--) {
            if (websites[len] == website){
                websites.splice(len,1);
                this.cdr.detectChanges();
            }
        }     
    };

    setBulkGroupPrivacy(priv): void{
        for (var idx in this.formData.websites){
            this.formData.websites[idx].visibility.visibility = priv;        
        }
    };

    getformData(): void {
        this.websitesService.getData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formDataBeforeChange = JSON.parse(JSON.stringify(data));
                this.formData = data;
                this.newElementDefaultVisibility = this.formData.visibility.visibility;

                ////console.log('this.getForm websites', this.formData);

                if( this.formData.websites == null ) {
                    this.formData.websites = { value: null };
                }

                let itemVisibility = null;
                let len = null;
                let websites = null;

                websites = this.formData.websites;
                len = websites.length;
                //Iterate over all elements to:
                // -> see if they have the same visibility, to set the default visibility element
                // -> set the default protocol when needed
                if(len > 0) {
                    while (len--) {
                        if(websites[len].url != null && websites[len].url.value != null) {
                            if (!websites[len].url.value.toLowerCase().startsWith('http')) {
                                websites[len].url.value = 'http://' + websites[len].url.value;
                            }                            
                        }     

                                     
                    }
                }

                if ( this.formData.websites.length == 0 ) {
                    this.addNew();
                }
            },
            error => {
                //console.log('getWebsitesFormError', error);
            } 
        );
    };

    privacyChange( obj ): any {
        this.formData.visibility.visibility = obj;  
    };

    setFormData( closeAfterAction ): void {
        this.websitesService.setData( this.formData )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                if (this.formData.errors.length == 0){
                    this.getformData();
                    this.websitesService.notifyOther();
                    if( closeAfterAction == true ) {
                        this.closeEditModal();
                    }
                }else{
                    //console.log(this.formData.errors);
                }

            },
            error => {
                //console.log('setWebsites', error);
            } 
        );
        this.formData.visibility = null;
    }

    swapDown(index): void{
        let temp =  null;
        let tempDisplayIndex = null;
        if (index < this.formData.websites.length - 1) {
            temp = this.formData.websites[index];
            tempDisplayIndex = this.formData.websites[index]['displayIndex'];
            temp['displayIndex'] = this.formData.websites[index + 1]['displayIndex']
            this.formData.websites[index] = this.formData.websites[index + 1];
            this.formData.websites[index]['displayIndex'] = tempDisplayIndex;
            this.formData.websites[index + 1] = temp;
        }
    };

    swapUp(index): void{
        let temp =  null;
        let tempDisplayIndex = null;
        if (index > 0) {
            temp = this.formData.websites[index];
            tempDisplayIndex = this.formData.websites[index]['displayIndex'];
            temp['displayIndex'] = this.formData.websites[index - 1]['displayIndex']
            this.formData.websites[index] = this.formData.websites[index - 1];
            this.formData.websites[index]['displayIndex'] = tempDisplayIndex;
            this.formData.websites[index - 1] = temp;
        }
    };

    updateDisplayIndex(): void {
        let idx: any;
        for (idx in this.formData.websites) {         
            this.formData.websites[idx]['displayIndex'] = this.formData.websites.length - idx;
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