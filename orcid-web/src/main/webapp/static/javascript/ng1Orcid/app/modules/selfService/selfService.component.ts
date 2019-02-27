import { NgForOf, NgIf } 
    from '@angular/common';

import { Component, Input, NgModule } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { CommonService } 
    from '../../shared/common.service';

import { ConsortiaService }
    from '../../shared/consortia.service'

import { FeaturesService }
    from '../../shared/features.service'

import { ModalService } 
    from '../../shared/modal.service';

@Component({
    selector: 'self-service-ng2',
    template:  scriptTmpl("self-service-ng2-template")
})
export class SelfServiceComponent {
    
    private subscription: Subscription;
    
    @Input() memberDetails : any;
    @Input() contacts : any;
    @Input() orgIds : any;
    
    addContactDisabled : boolean = false;
    addSubMemberDisabled : boolean = false;
    addSubMemberShowLoader : boolean = false;
    effectiveUserOrcid = orcidVar.orcidId;
    errorAddingSubMember : boolean = false;
    errorSubMemberExists : boolean = false;
    input: any = {};
    orgIdInput: any = {};
    newSubMember: any = {website: {errors: [], getRequiredMessage: null, required: false, value: ''}, name: {errors: [], getRequiredMessage: null, required: false, value: ''}, initialContactFirstName: {errors: [], getRequiredMessage: null, required: false, value: ''}, initialContactLastName: {errors: [], getRequiredMessage: null, required: false, value: ''}, initialContactEmail: {errors: [], getRequiredMessage: null, required: false, value: ''}};
    newSubMemberExistingOrg : any;
    realUserOrcid = orcidVar.realOrcidId;
    showInitLoader : boolean = true;
    updateContactsDisabled : boolean = false;
    updateContactsShowLoader : boolean = false;
    updateMemberDetailsDisabled : boolean = false;
    updateMemberDetailsShowLoader : boolean = false;
    successEditMemberMessage : string;
    orgIdsFeatureEnabled: boolean = this.featuresService.isFeatureEnabled('SELF_SERVICE_ORG_IDS');
    orgIdSearchResults: Array<object> = [];
    
    constructor(
        private commonSrvc: CommonService,
        private consortiaService: ConsortiaService,
        private featuresService: FeaturesService,
        private modalService: ModalService
    ) {}
  
  
    buildOrcidUri(orcid: String): string {
        return orcidVar.baseUri + '/' + orcid;
    };
    
    getMemberDetails() {
        this.consortiaService.getMemberDetails(this.consortiaService.getAccountIdFromPath())
            .subscribe(
                data => {
                this.memberDetails = data;
            },
            error => {
                //console.log('getMemberDetails error', error);
            } 
        );
    }
    
    getContacts() {
        this.consortiaService.getContacts(this.consortiaService.getAccountIdFromPath())
            .subscribe(
                data => {
                this.contacts = data;
            },
            error => {
                //console.log('getContacts error', error);
            } 
        );
    }
    
    getOrgIds() {
        this.consortiaService.getOrgIds(this.consortiaService.getAccountIdFromPath())
            .subscribe(
                data => {
                this.orgIds = data;
            },
            error => {
                //console.log('getOrgIds error', error);
            }
        );
    }

    search(){
        $('#invalid-email-alert').hide();
        if(this.commonSrvc.isEmail(this.input.text)){
            this.searchByEmail();
        }
        else{
            $('#invalid-email-alert').show();
        }
    };
    
    searchByEmail() {
        this.consortiaService.searchByEmail(this.input.text)
            .subscribe(
                data => {
                    this.confirmAddContactByEmail(data);
            },
            error => {
                //console.log('searchByEmail error', error);
            } 
        );
    }
    
    searchOrgIds() {
        this.consortiaService.searchOrgIds(this.orgIdInput.text)
            .subscribe(
                data => {
                    this.orgIdSearchResults = data;
            },
            error => {
                //console.log('searchOrgIds error', error);
            } 
        );
    }
    
     addOrgId(org: any) {
        let orgId: any = {};
        orgId.accountId = this.consortiaService.getAccountIdFromPath();
        orgId.orgIdValue = org.sourceId;
        orgId.orgIdType = org.sourceType;
        this.consortiaService.addOrgId(orgId)
            .subscribe(
                data => {
                    this.getOrgIds();
                },
                error => {
                    //console.log('addOrgId error', error);
                } 
        );
    }
    
    removeOrgId(orgId: any) {
        orgId.accountId = this.consortiaService.getAccountIdFromPath();
        this.consortiaService.removeOrgId(orgId)
            .subscribe(
                data => {
                    this.getOrgIds();
                },
                error => {
                    //console.log('removeOrgId error', error);
                } 
        );
    }
    
    confirmAddContactByEmail(emailSearchResult: any) {
        this.modalService.notifyOther({action:'open', moduleId: 'modalSelfServiceAddContact', input: this.input, emailSearchResult: emailSearchResult});
    }
    
    confirmRemoveContact(contact: any) {
        this.modalService.notifyOther({action:'open', moduleId: 'modalSelfServiceRemoveContact', contact: contact });
    }
    
    validateContacts() {
        this.consortiaService.validateContacts(this.contacts)
            .subscribe(
                data => {
                  this.contacts.errors = data.errors;
            },
            error => {
                //console.log('validateContacts error', error);
            } 
        );
    }
    
    validateMemberDetails() {
        this.consortiaService.validateMemberDetails(this.memberDetails)
            .subscribe(
                data => {
                  this.memberDetails = data;
                  if (this.memberDetails.errors == undefined || this.memberDetails.errors.length == 0) {
                      this.updateMemberDetails();
                  }
            },
            error => {
                //console.log('validateMemberDetails error', error);
            } 
        );
    }
    
    validateMemberDetailsField(field: string) {
         this.consortiaService.validateMemberDetailsField(this.memberDetails, field)
            .subscribe(
                data => {
                  this.memberDetails = data;
            },
            error => {
                //console.log('validateMemberDetailsField error', error);
            } 
        );
    }
    
    updateMemberDetails() {
        this.updateMemberDetailsShowLoader = true;
        this.updateMemberDetailsDisabled = true;
        this.consortiaService.updateMemberDetails(this.memberDetails)
            .subscribe(
                data => {
                  this.updateMemberDetailsShowLoader = false;
                  this.updateMemberDetailsDisabled = false;
                  if(data.errors.length == 0){
                      this.successEditMemberMessage = om.get('manage_member.edit_member.success');
                  }
                  else {
                     this.memberDetails = data;
                  }
            },
            error => {
                //console.log('updateMemberDetails error', error);
            } 
        );
    }
    
    updateContacts() {
        this.updateContactsShowLoader = true;
        this.updateContactsDisabled = true;
        this.contacts.accountId = this.consortiaService.getAccountIdFromPath();
        this.consortiaService.updateContacts(this.contacts)
            .subscribe(
                data => {
                   this.contacts = data;
                   this.updateContactsShowLoader = false;
                   this.updateContactsDisabled = false;
                   if(data.errors.length == 0){
                       this.successEditMemberMessage = om.get('manage_member.edit_member.success');
                   }
            },
            error => {
                //console.log('updateContacts error', error);
            }
        );
    }
    
    isPendingAddition(subMember: any) : boolean {
        return subMember.opportunity.stageName == 'Negotiation/Review';
    }
    
    isPendingRemoval(subMember: any) : boolean {
        return subMember.opportunity.removalRequested;
    }
    
    canRemoveSubMember(subMember: any) : boolean {
        return this.memberDetails.allowedFullAccess && !this.isPendingAddition(subMember) && !this.isPendingRemoval(subMember);
    }
    
    isValidClass(cur) : string{
        let valid : boolean;
        if (cur === undefined) {
            return '';
        }
        valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) {
            valid = false;
        }
        if (cur.errors !== undefined && cur.errors.length > 0) {
            valid = false;
        }
        return valid ? '' : 'text-error';
    };
    
    validateSubMember() {
        //console.log("validate sub member called");
        this.addSubMemberShowLoader = true;
        this.newSubMember.parentAccountId = this.consortiaService.getAccountIdFromPath();
        this.consortiaService.validateSubMember(this.newSubMember)
            .subscribe(
                data => {
                  this.newSubMember = data;
                  if (this.newSubMember.errors == undefined || this.newSubMember.errors.length == 0) {
                      this.checkExistingSubMember();
                   } else {
                       if(this.newSubMember.errors.indexOf("This member already exists in your consortium") !== -1) {
                           this.errorSubMemberExists = true;
                       }
                       this.addSubMemberShowLoader = false;
                  }
            },
            error => {
                //console.log('validateMemberDetails error', error);
                this.errorAddingSubMember = true;
                this.addSubMemberShowLoader = false;
            } 
        );
    }
    
    checkExistingSubMember() {
        this.addSubMemberShowLoader = true;
        this.errorAddingSubMember = false;
        this.consortiaService.checkExistingSubMember(this.newSubMember)
            .subscribe(
                data => {
                    this.newSubMemberExistingOrg = data
                    if(this.newSubMemberExistingOrg.publicDisplayName != null && this.newSubMemberExistingOrg.websiteUrl != null && this.newSubMemberExistingOrg.id){ 
                        this.modalService.notifyOther({action:'open', moduleId: 'modalSelfServiceExistingSubMember', newSubMemberExistingOrg: this.newSubMemberExistingOrg });
                    } else {
                        this.addSubMember();
                    }    
            },
            error => {
                //console.log('checkExistingSubMember error', error);
                this.errorAddingSubMember = true;
                this.addSubMemberShowLoader = false;
            }
        );
    }
    
    validateSubMemberField(field: string) {
         this.consortiaService.validateSubMemberField(this.newSubMember, field)
            .subscribe(
                data => {
                  this.newSubMember = data;
            },
            error => {
                //console.log('validateSubMemberField error', error);
            } 
        );
    }

    addSubMember() {
        this.addSubMemberDisabled = true;
        this.addSubMemberShowLoader = true;
        this.newSubMember.parentAccountId = this.consortiaService.getAccountIdFromPath();
        this.consortiaService.addSubMember(this.newSubMember)
            .subscribe(
                data => {
                    if(data.errors.length === 0){
                        this.getMemberDetails();
                        this.addSubMemberShowLoader = false;
                        this.addSubMemberDisabled = false;
                        this.newSubMember.name.value = "";
                        this.newSubMember.website.value = "";
                        this.newSubMember.initialContactEmail.value = "";
                        this.newSubMember.initialContactFirstName.value = "";
                        this.newSubMember.initialContactLastName.value = "";
                        this.newSubMember.errors = [];
                        this.newSubMember.name.errors = [];
                        this.newSubMember.website.errors = [];
                        this.newSubMember.initialContactEmail.errors = [];
                        this.newSubMember.initialContactFirstName.errors = [];
                        this.newSubMember.initialContactLastName.errors = [];
                    }
                    else{
                        this.newSubMember.errors = data.errors;
                        this.addSubMemberShowLoader = false;
                    }
                },
                error => {
                    //console.log('addSubMember error', error);
                    this.errorAddingSubMember = true;
                    this.addSubMemberShowLoader = false;
                } 
        );
    }
    
    addSubMemberClear() {
        this.addSubMemberShowLoader = false;
        this.addSubMemberDisabled = false;
        this.newSubMember.name.value = "";
        this.newSubMember.website.value = "";
        this.newSubMember.initialContactEmail.value = "";
        this.newSubMember.initialContactFirstName.value = "";
        this.newSubMember.initialContactLastName.value = "";
        this.newSubMember.errors = [];
        this.newSubMember.name.errors = [];
        this.newSubMember.website.errors = [];
        this.newSubMember.initialContactEmail.errors = [];
        this.newSubMember.initialContactFirstName.errors = [];
        this.newSubMember.initialContactLastName.errors = [];
    }
    
    confirmRemoveSubMember(subMember: any) {
        subMember.parentAccountId = this.consortiaService.getAccountIdFromPath();
        this.modalService.notifyOther({action:'open', moduleId: 'modalSelfServiceRemoveSubMember', subMember: subMember });
    }
    
    cancelSubMemberAddition(subMember: any) {
        subMember.parentAccountId =  this.consortiaService.getAccountIdFromPath();
        this.consortiaService.cancelSubMemberAddition(subMember)
            .subscribe(
                data => {
                    this.getMemberDetails();
                },
                error => {
                    //console.log('cancelSubMemberAddition error', error);
                } 
        );
    }
    
    ngOnInit() {
        this.getMemberDetails();
        this.getContacts();
        if(this.orgIdsFeatureEnabled) {
            this.getOrgIds();
        }
        this.subscription = this.consortiaService.notifyObservable$.subscribe(
            (res) => {
                if(res !== "undefined" && res.action === "add" && res.moduleId === "selfServiceExistingSubMember"){
                    this.addSubMember();
                }
                this.getMemberDetails();
                this.getContacts();
            }
        );
    }

}