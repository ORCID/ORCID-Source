import { NgFor, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { EmailService } 
    from '../../shared/email.service.ts';

import { CommonService } 
    from '../../shared/common.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'emails-form-ng2',
    template:  scriptTmpl("emails-form-ng2-template")
})
export class EmailsFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    /* On the template:
    <td ng-init="emailStatusOptions = [{label:'<@orcid.msg "manage.email.current.true" />',val:true},{label:'<@orcid.msg "manage.email.current.false" />',val:false}];">
    */
    @Input() emailStatusOptionsObj: any;

    defaultVisibility: any;
    emails: any;
    emailStatusOptions: any;
    formData: any;
    formDataBeforeChange: any;
    newElementDefaultVisibility: any;
    orcidId: any;
    privacyHelp: any;
    scrollTop: any;
    showEdit: any;
    showElement: any;

    popUp: boolean;

    constructor( 
        private emailService: EmailService,
        private commonSrvc: CommonService,
        private modalService: ModalService
    ) {
        this.defaultVisibility = null;
        this.emails = {};
        this.emailStatusOptions = null;
        this.formData = {
            emails: null,
            visibility: {
                visibility: this.defaultVisibility
            }
        };
        this.formDataBeforeChange = {};
        this.newElementDefaultVisibility = 'PRIVATE';
        this.orcidId = orcidVar.orcidId; 
        this.privacyHelp = false;
        this.scrollTop = 0;
        this.showEdit = false;
        this.showElement = {};

        this.popUp = true;
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
        this.formData.emails.push(tmpObj);        
        this.updateDisplayIndex();    
    };

    closeEditModal(): void{
        this.formData = this.formDataBeforeChange;
        this.modalService.notifyOther({action:'close', moduleId: 'modalEmailsForm'});
    };

    getformData(): void {
        this.emailService.getData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formDataBeforeChange = JSON.parse(JSON.stringify(data));
                this.formData = data;
                //this.newElementDefaultVisibility = this.formData.visibility.visibility;
                console.log('this.getForm emails', this.formData);
                if ( this.formData.emails.length == 0 ) {
                    this.addNew();
                }
            },
            error => {
                console.log('getEmailsFormError', error);
            } 
        );
    };

    privacyChange( obj ): any {
        this.formData.visibility.visibility = obj;
        this.saveEmail( false );   
    };

    saveEmail( closeAfterAction ): void {
        this.emailService.setData( this.formData )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                if (this.formData.errors.length == 0){
                    this.getformData();
                    this.emailService.notifyOther();
                    if( closeAfterAction == true ) {
                        this.closeEditModal();
                    }
                }else{
                    console.log(this.formData.errors);
                }

            },
            error => {
                console.log('setEmailsKnownAs', error);
            } 
        );
        this.formData.visibility = null;
    }

    swapDown(index): void{
        let temp;
        let tempDisplayIndex;
        /*
        if (index < this.formData.otherNames.length - 1) {
            temp = this.formData.otherNames[index];
            tempDisplayIndex = this.formData.otherNames[index]['displayIndex'];
            temp['displayIndex'] = this.formData.otherNames[index + 1]['displayIndex']
            this.formData.otherNames[index] = this.formData.otherNames[index + 1];
            this.formData.otherNames[index]['displayIndex'] = tempDisplayIndex;
            this.formData.otherNames[index + 1] = temp;
        }
        */
    };

    swapUp(index): void{
        let temp;
        let tempDisplayIndex;
        /*
        if (index > 0) {
            temp = this.formData.otherNames[index];
            tempDisplayIndex =this.formData.otherNames[index]['displayIndex'];
            temp['displayIndex'] = this.formData.otherNames[index - 1]['displayIndex']
            this.formData.otherNames[index] = this.formData.otherNames[index - 1];
            this.formData.otherNames[index]['displayIndex'] = tempDisplayIndex;
            this.formData.otherNames[index - 1] = temp;
        }
        */
    };

    updateDisplayIndex(): void{
        let idx: any;
        /*
        for (idx in this.formData.otherNames) {         
            this.formData.otherNames[idx]['displayIndex'] = this.formData.otherNames.length - idx;
        }
        */
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
        this.emailStatusOptions = this.emailStatusOptionsObj;
    };

}
