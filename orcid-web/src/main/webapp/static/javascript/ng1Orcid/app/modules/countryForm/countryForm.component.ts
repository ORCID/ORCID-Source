declare var $: any; //delete

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

import { CommonService } 
    from '../../shared/common.service.ts';

import { CountryService } 
    from '../../shared/country.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'country-form-ng2',
    template:  scriptTmpl("country-form-ng2-template")
})
export class CountryFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    bulkEditShow: any; ///
    formData: any;
    formDataAddresses: any;
    formDataBeforeChange: any;
    formDataErrors: any;
    newElementDefaultVisibility: any; ///
    newInput: boolean;
    orcidId: any;
    primaryElementIndex: any;  

    constructor(
        private cdr:ChangeDetectorRef,
        private commonSrvc: CommonService,
        private countryService: CountryService,
        private modalService: ModalService
    ) {
        this.bulkEditShow = false;
        this.formDataBeforeChange = {};
        this.formData = {
        };
        this.formDataAddresses = [];
        this.formDataErrors = [];
        this.newElementDefaultVisibility = 'PRIVATE';
        this.newInput = false;    
        this.orcidId = orcidVar.orcidId;
        this.primaryElementIndex = null;   
    }

    addNewCountry(): void {  
        var tmpObj = {
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
                "visibility": this.newElementDefaultVisibility
            },
            "displayIndex":1,
            "source":this.orcidId,
            "sourceName":""
        };
        //console.log('country add tmp', tmpObj);
        this.formData.addresses.push(tmpObj);
        this.updateDisplayIndex();
        this.newInput = true; 
    };

    closeEditModal(): void{
        this.formData = this.formDataBeforeChange;
        this.modalService.notifyOther({action:'close', moduleId: 'modalCountryForm'});
    };

    deleteCountry(country): void{
        var countries = this.formData.addresses;
        var len = countries.length;
        while (len--) {
            if (countries[len] == country){
                countries.splice(len,1);
                this.formData.addresses = countries;
                this.cdr.detectChanges();
            }       
        }
    };

    getformData(): void{
        this.countryService.getCountryData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formDataBeforeChange = JSON.parse(JSON.stringify(data));
                this.formData = data;
                ////console.log('country data', this.formData);
                this.formDataAddresses = this.formData.addresses;
                this.newElementDefaultVisibility = this.formData.visibility.visibility;

                if ( this.formData.addresses.length == 0 ){                  
                    this.addNewCountry();
                } else {
                    if ( this.formData.addresses.length == 1 ){
                        if( this.formData.addresses[0].source == null ){
                            //this.formData.addresses[0].source = this.orcidId;
                            //this.formData.addresses[0].sourceName = "";
                            this.addNewCountry();
                        }
                    }
                    this.updateDisplayIndex();
                } 

                if( this.formData.errors != null ) {
                    this.formDataErrors = this.formData.errors;
                }

                if( this.formData != null 
                    && this.formData.addresses != null 
                    && this.formData.addresses.length > 0) {
                    let highestDisplayIndex = null;
                    let itemVisibility = null;
                    
                    for(let i = 0; i < this.formData.addresses.length; i ++) {
                        if( this.formData.addresses[i].visibility != null 
                            && this.formData.addresses[i].visibility.visibility ) {
                            itemVisibility = this.formData.addresses[i].visibility.visibility;
                        }                                                                
                    }
                    //We have to iterate on them again to select the primary address
                    for(let i = 0; i < this.formData.addresses.length; i ++) {
                        //Set the primary element based on the display index
                        if(this.primaryElementIndex == null 
                            || highestDisplayIndex < this.formData.addresses[i].displayIndex) {
                            this.primaryElementIndex = i;
                            highestDisplayIndex = this.formData.addresses[i].displayIndex;
                        }
                    }
                }

            },
            error => {
                //console.log('getformDataError', error);
            } 
        );
    };

    setBulkGroupPrivacy(priv): void{
        for (var idx in this.formData.addresses){
            this.formData.addresses[idx].visibility.visibility = priv;        
        }
    };

    setformData( closeAfterAction ): void {

        this.countryService.setCountryData( this.formData )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                if (this.formData.errors.length == 0){
                    this.getformData();
                    this.countryService.notifyOther();
                    if( closeAfterAction == true ) {
                        this.closeEditModal();
                    }
                }else{
                    ////console.log(this.formData.errors);
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
        this.setformData( false );   
    };
    
    ///
    setPrivacyModal(priv, $event, country): void{        
        var countries = this.formData.addresses;        
        var len = countries.length;   

        $event.preventDefault();

        while (len--) {
            if (countries[len] == country){            
                countries[len].visibility.visibility = priv;
                this.formData.addresses = countries;
            }
        }
    };
    
    swapDown(index): void{
        let temp = null;
        let tempDisplayIndex = null;
        if (index < this.formData.addresses.length - 1) {
            temp = this.formData.addresses[index];
            tempDisplayIndex = this.formData.addresses[index]['displayIndex'];
            temp['displayIndex'] = this.formData.addresses[index + 1]['displayIndex']
            
            this.formData.addresses[index] = this.formData.addresses[index + 1];
            this.formData.addresses[index]['displayIndex'] = tempDisplayIndex;
            this.formData.addresses[index + 1] = temp;
        }
    };

    swapUp(index): void{
        let temp = null;
        let tempDisplayIndex = null;
        if (index > 0) {
            temp = this.formData.addresses[index];
            tempDisplayIndex = this.formData.addresses[index]['displayIndex'];
            temp['displayIndex'] = this.formData.addresses[index - 1]['displayIndex']
            
            this.formData.addresses[index] = this.formData.addresses[index - 1];
            this.formData.addresses[index]['displayIndex'] = tempDisplayIndex;
            this.formData.addresses[index - 1] = temp;
        }
    };

    updateDisplayIndex(): void{
        let displayIndex: any;
        let formDataAddressesLength: any;
        let idx: any;
        
        for ( idx in this.formData.addresses ){
            formDataAddressesLength = this.formData.addresses.length;
            displayIndex = formDataAddressesLength - idx;
            this.formData.addresses[idx]['displayIndex'] = displayIndex;
        }
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.countryService.notifyObservable$.subscribe(
            (res) => {}
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getformData();
    };
}
