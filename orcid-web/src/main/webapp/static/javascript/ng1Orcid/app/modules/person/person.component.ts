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

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'person-ng2',
    template:  scriptTmpl("person-ng2-template")
})
export class PersonComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
    private urlPath = {
        addresses: '/account/countryForm.json',
        externalIdentifiers: '/my-orcid/externalIdentifiers.json',
        keywords: '/my-orcid/keywordsForms.json',
        otherNames: '/my-orcid/otherNamesForms.json',
        websites: '/my-orcid/websitesForms.json'
    }
    public newInput = new EventEmitter<boolean>();

    emails: any;
    elementHeight: any;
    elementWidth: any;
    formData: any;
    formDataBeforeChange: any
    newElementDefaultVisibility: any;
    orcidId: any;
    privacyHelp: any;
    scrollTop: any;
    setFocus: boolean;
    
    constructor( 
        private cdr:ChangeDetectorRef,
        private commonSrvc: CommonService,
        private genericService: GenericService,
        private emailService: EmailService,
        private modalService: ModalService,
    ) {

        this.emails = {};
        this.elementHeight = "645";
        this.elementWidth = "645";
        this.formData = {
            addresses: {},
            externalIdentifiers: {},
            keywords: {},
            otherNames: {},
            websites: {}
        }
        this.formDataBeforeChange = {
            addresses: {},
            externalIdentifiers: {},
            keywords: {},
            otherNames: {},
            websites: {}
        }
        this.newElementDefaultVisibility = {
            addresses: null,
            externalIdentifiers: null,
            keywords: null,
            otherNames: null,
            websites: null
        }
        this.orcidId = orcidVar.orcidId; 
        this.privacyHelp = false;
        this.scrollTop = 0;
        this.setFocus = true;
    }

    addSectionItem(sectionName): void {
        let tmpObj;
        switch(sectionName){
            case 'otherNames':
            case 'keywords': { 
                tmpObj = {
                    "errors":[],
                    "putCode":null,
                    "content":"",
                    "visibility":{
                        "errors":[],
                        "required":true,
                        "getRequiredMessage":null,
                        "visibility":this.newElementDefaultVisibility[sectionName]
                    },
                    "displayIndex":1,
                    "source":this.orcidId,
                    "sourceName":""
                };
            break;
            } 
            case 'addresses': {
                tmpObj = {
                    "errors":[],
                    "iso2Country": {
                        "value": ""
                    },
                    "countryName": null,
                    "putCode": null,
                    "visibility":{
                        "errors":[],
                        "required":true,
                        "getRequiredMessage":null,
                        "visibility": this.newElementDefaultVisibility[sectionName]
                    },
                    "displayIndex":1,
                    "source":this.orcidId,
                    "sourceName":""
                };
            break;
            }
            case 'websites': {
                tmpObj = {
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
                        "visibility":this.newElementDefaultVisibility[sectionName]
                    },
                    "source":this.orcidId,
                    "sourceName":"", 
                    "displayIndex": 1
                }; 
            break;
            }
        }    
        this.formData[sectionName][sectionName].push(tmpObj); 
        this.cdr.detectChanges();       
        this.updateDisplayIndex(sectionName);
        this.newInput.emit(true); 
    };

    deleteSectionItem(itemToDelete, sectionName, index, toolTipSelector): void{
        this.commonSrvc.hideTooltip(toolTipSelector);
        let len = this.formData[sectionName][sectionName].length;
        while (len--) {            
            if (this.formData[sectionName][sectionName][len] == itemToDelete){                
                this.formData[sectionName][sectionName].splice(len,1);
                this.cdr.detectChanges();
            }
        } 
        if(this.formData[sectionName][sectionName].length==0 && sectionName != 'externalIdentifiers' ){
            this.addSectionItem(sectionName);    
        }       
    };

    getFormData(sectionName): void {
        this.genericService.getData( this.urlPath[sectionName], sectionName )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.formDataBeforeChange[sectionName] = JSON.parse(JSON.stringify(data));
                this.formData[sectionName] = data;
                this.newElementDefaultVisibility[sectionName] = data.visibility.visibility;

                if(sectionName!="externalIdentifiers"){
                    if ( data[sectionName].length == 0){
                        this.addSectionItem(sectionName);
                    } else {
                        if(data[sectionName][0].putCode == null  ){
                            this.addSectionItem(sectionName);
                        }  
                    }
                } 

            },
            error => {
                console.log('error getting person data: ', error);
            } 
        );
    };

    setFormData( closeAfterAction, sectionName, modalId ): void {
        for(var i in this.formData[sectionName][sectionName]){
            switch(sectionName){
                case 'otherNames':
                case 'keywords': { 
                    if(this.formData[sectionName][sectionName][i].content==""){
                        this.formData[sectionName][sectionName].splice(i,1);
                    }
                    break;
                } 
                case 'addresses': {
                    if(this.formData[sectionName][sectionName][i].iso2Country.value==""){
                        this.formData[sectionName][sectionName].splice(i,1);
                    }
                    break;
                }
                case 'websites': {
                    if(!this.formData[sectionName][sectionName][i].url.value){
                        this.formData[sectionName][sectionName].splice(i,1);
                    }
                }  
            } 

        }
        
        this.genericService.setData( this.formData[sectionName], this.urlPath[sectionName] )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.formData[sectionName] = data;
                if (this.formData[sectionName].errors.length == 0){
                    this.getFormData(sectionName);
                    if( closeAfterAction == true ) {
                        this.closeEditModal(modalId);
                    }
                }else{
                    console.log('error updating person data');
                }

            },
            error => {
                console.log('error updating person data: ', error);
            } 
        );
        this.formData[sectionName].visibility = null;
    }

    openEditModal(modalId): void{      
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.genericService.open(modalId);
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                console.log('error getting email data: ', error);
            } 
        );
    };

    cancelEditModal(sectionName, id): void{
        this.genericService.close(id);
        this.getFormData(sectionName);
    };

    openModal(id: string){
        this.genericService.open(id);
    }
 
    closeEditModal(id: string){
        this.genericService.close(id);
    }

    privacyChange(obj, sectionName): any {
        this.formData[sectionName].visibility.visibility = obj;  
    };

    setBulkGroupPrivacy(priv, sectionName): void{
        console.log(this.formData[sectionName][sectionName]);
        for (var idx in this.formData[sectionName][sectionName]){
            this.formData[sectionName][sectionName][idx].visibility.visibility = priv;        
        }
    };

    swapDown(index, sectionName): void{
        let temp;
        let tempDisplayIndex;
        if (index < this.formData[sectionName][sectionName].length - 1) {
            temp = this.formData[sectionName][sectionName][index];
            tempDisplayIndex = this.formData[sectionName][sectionName][index]['displayIndex'];
            temp['displayIndex'] = this.formData[sectionName][sectionName][index + 1]['displayIndex']
            this.formData[sectionName][sectionName][index] = this.formData[sectionName][sectionName][index + 1];
            this.formData[sectionName][sectionName][index]['displayIndex'] = tempDisplayIndex;
            this.formData[sectionName][sectionName][index + 1] = temp;
        }
    };

    swapUp(index, sectionName): void{
        console.log(index, sectionName);
        let temp;
        let tempDisplayIndex;
        if (index > 0) {
            temp = this.formData[sectionName][sectionName][index];
            tempDisplayIndex = this.formData[sectionName][sectionName][index]['displayIndex'];
            temp['displayIndex'] = this.formData[sectionName][sectionName][index - 1]['displayIndex']
            this.formData[sectionName][sectionName][index] = this.formData[sectionName][sectionName][index - 1];
            this.formData[sectionName][sectionName][index]['displayIndex'] = tempDisplayIndex;
            this.formData[sectionName][sectionName][index - 1] = temp;
        }
    };

    updateDisplayIndex(sectionName): void{
        let idx: any;
        for (idx in this.formData[sectionName][sectionName]) {         
            this.formData[sectionName][sectionName][idx]['displayIndex'] = this.formData[sectionName][sectionName].length - idx;
        }
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives

    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getFormData('otherNames');
        this.getFormData('addresses');
        this.getFormData('keywords');
        this.getFormData('websites');
        this.getFormData('externalIdentifiers');
    };

}