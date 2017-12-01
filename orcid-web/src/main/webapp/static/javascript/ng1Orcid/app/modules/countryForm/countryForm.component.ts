declare var $: any; //delete

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

import { CommonService } 
    from '../../shared/commonService.ts';

import { CountryService } 
    from '../../shared/countryService.ts';

import { ModalService } 
    from '../../shared/modalService.ts'; 

@Component({
    selector: 'country-form-ng2',
    template:  scriptTmpl("country-form-ng2-template")
})
export class CountryFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    bulkEditShow: any; ///
    countryForm: any;
    countryFormAddresses: any;
    countryFormErrors: any;
    defaultVisibility: any; ///
    newInput: boolean;
    orcidId: any;
    primaryElementIndex: any;  

    constructor(
        private commonSrvc: CommonService,
        private countryService: CountryService,
        private modalService: ModalService
    ) {
        this.bulkEditShow = false;
        this.countryForm = null;
        this.countryFormAddresses = [];
        this.countryFormErrors = [];
        this.defaultVisibility = null;
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
                "visibility": 'PUBLIC'
            },
            "displayIndex":1,
            "source":this.orcidId,
            "sourceName":""
        };
        this.countryForm.addresses.push(tmpObj);
        this.updateDisplayIndex();
        this.newInput = true; 
    };

    closeEditModal(): void{
        this.modalService.notifyOther({action:'close', moduleId: 'modalCountryForm'});
    };

    deleteCountry(country): void{
        var countries = this.countryForm.addresses;
        var len = countries.length;
        while (len--) {
            if (countries[len] == country){
                countries.splice(len,1);
                this.countryForm.addresses = countries;
            }       
        }
    };

    getCountryForm(): void{
        this.countryService.getCountryData()
        //.takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.countryForm = data;
                this.countryFormAddresses = this.countryForm.addresses;
                
                if ( this.countryForm.addresses.length == 0 ){                  
                    this.addNewCountry();
                } else {
                    if ( this.countryForm.addresses.length == 1 ){
                        if( this.countryForm.addresses[0].source == null ){
                            this.countryForm.addresses[0].source = this.orcidId;
                            this.countryForm.addresses[0].sourceName = "";
                        }
                    }
                    this.updateDisplayIndex();
                } 

                if( this.countryForm.errors != null ) {
                    this.countryFormErrors = this.countryForm.errors;
                }
                //console.log('this.countryForm', this.countryForm);

                if( this.countryForm != null 
                    && this.countryForm.addresses != null 
                    && this.countryForm.addresses.length > 0) {
                    let highestDisplayIndex = null;
                    let itemVisibility = null;
                    
                    for(let i = 0; i < this.countryForm.addresses.length; i ++) {
                        if( this.countryForm.addresses[i].visibility != null 
                            && this.countryForm.addresses[i].visibility.visibility ) {
                            itemVisibility = this.countryForm.addresses[i].visibility.visibility;
                        }
                        /**
                         * The default visibility should be set only when all elements have the same visibility, so, we should follow this rules: 
                         * 
                         * Rules: 
                         * - If the default visibility is null:
                         *  - If the item visibility is not null, set the default visibility to the item visibility
                         * - If the default visibility is not null:
                         *  - If the default visibility is not equals to the item visibility, set the default visibility to null and stop iterating 
                         * */
                        if(this.defaultVisibility == null) {
                            if(itemVisibility != null) {
                                this.defaultVisibility = itemVisibility;
                            }                           
                        } else {
                            if(itemVisibility != null) {
                                if(this.defaultVisibility != itemVisibility) {
                                    this.defaultVisibility = null;
                                    break;
                                }
                            } else {
                                this.defaultVisibility = null;
                                break;
                            }
                        }                                                                   
                    }
                    //We have to iterate on them again to select the primary address
                    for(let i = 0; i < this.countryForm.addresses.length; i ++) {
                        //Set the primary element based on the display index
                        if(this.primaryElementIndex == null 
                            || highestDisplayIndex < this.countryForm.addresses[i].displayIndex) {
                            this.primaryElementIndex = i;
                            highestDisplayIndex = this.countryForm.addresses[i].displayIndex;
                        }
                    }
                } else {
                    this.defaultVisibility = this.countryForm.visibility.visibility;                    
                }

                //console.log('this.countryForm2', this.countryForm); 
            },
            error => {
                console.log('getCountryFormError', error);
            } 
        );
    };

    setBulkGroupPrivacy(priv): void{
        for (var idx in this.countryForm.addresses){
            this.countryForm.addresses[idx].visibility.visibility = priv;        
        }
    };

    setCountryForm( closeAfterAction ): void {

        this.countryService.setCountryData( this.countryForm )
        //.takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.countryForm = data;
                if (this.countryForm.errors.length == 0){
                    this.getCountryForm();
                    this.countryService.notifyOther({action:'close', moduleId: 'modalCountryForm'});
                    if( closeAfterAction == true ) {
                        this.closeEditModal();
                    }
                }else{
                    console.log(this.countryForm.errors);
                }

            },
            error => {
                console.log('setBiographyFormError', error);
            } 
        );
        this.countryForm.visibility = null;
    };
    
    privacyChange( obj ): any {
        console.log('privacyChange', obj);
        this.countryForm.visibility.visibility = obj;
        this.setCountryForm( false );   
    };
    
    ///
    setPrivacyModal(priv, $event, country): void{        
        var countries = this.countryForm.addresses;        
        var len = countries.length;   

        $event.preventDefault();

        while (len--) {
            if (countries[len] == country){            
                countries[len].visibility.visibility = priv;
                this.countryForm.addresses = countries;
            }
        }
    };
    
    swapDown(index): void{
        let temp = null;
        let tempDisplayIndex = null;
        if (index < this.countryForm.addresses.length - 1) {
            temp = this.countryForm.addresses[index];
            tempDisplayIndex = this.countryForm.addresses[index]['displayIndex'];
            temp['displayIndex'] = this.countryForm.addresses[index + 1]['displayIndex']
            
            this.countryForm.addresses[index] = this.countryForm.addresses[index + 1];
            this.countryForm.addresses[index]['displayIndex'] = tempDisplayIndex;
            this.countryForm.addresses[index + 1] = temp;
        }
    };

    swapUp(index): void{
        let temp = null;
        let tempDisplayIndex = null;
        if (index > 0) {
            temp = this.countryForm.addresses[index];
            tempDisplayIndex = this.countryForm.addresses[index]['displayIndex'];
            temp['displayIndex'] = this.countryForm.addresses[index - 1]['displayIndex']
            
            this.countryForm.addresses[index] = this.countryForm.addresses[index - 1];
            this.countryForm.addresses[index]['displayIndex'] = tempDisplayIndex;
            this.countryForm.addresses[index - 1] = temp;
        }
    };

    updateDisplayIndex(): void{
        let displayIndex: any;
        let countryFormAddressesLength: any;
        let idx: any;
        
        for ( idx in this.countryForm.addresses ){
            countryFormAddressesLength = this.countryForm.addresses.length;
            displayIndex = countryFormAddressesLength - idx;
            this.countryForm.addresses[idx]['displayIndex'] = displayIndex;
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
        //this.ngUnsubscribe.next();
        //this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getCountryForm();
    };
}
