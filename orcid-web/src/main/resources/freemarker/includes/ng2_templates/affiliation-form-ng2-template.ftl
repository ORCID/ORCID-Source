<script type="text/ng-template" id="affiliation-form-ng2-template">
    <div id="edit-affiliation" class="edit-affiliation colorbox-content">
        <form>
       
            <div class="row">
                <div class="col-md-9 col-sm-8 col-xs-12">                    
                    <h1 *ngIf="addAffType == null || addAffType == undefined " class="lightbox-title pull-left">
                        <span *ngIf="editAffiliation?.putCode?.value == null"><@orcid.msg 'manual_affiliation_form_contents.add_affiliation'/></span>
                        <span *ngIf="editAffiliation?.putCode?.value != null"><@orcid.msg 'manual_affiliation_form_contents.edit_affiliation'/></span>
                    </h1>
                    <h1 *ngIf="addAffType == 'distinction'" class="lightbox-title pull-left">
                        <span *ngIf="editAffiliation?.putCode?.value == null"><@orcid.msg 'manual_affiliation_form_contents.add_distinction'/></span>
                        <span *ngIf="editAffiliation?.putCode?.value != null"><@orcid.msg 'manual_affiliation_form_contents.edit_distinction'/></span>
                    </h1>
                    <h1 *ngIf="addAffType == 'education'" class="lightbox-title pull-left">
                        <span *ngIf="editAffiliation?.putCode?.value == null"><@orcid.msg 'manual_affiliation_form_contents.add_education'/></span>
                        <span *ngIf="editAffiliation?.putCode?.value != null"><@orcid.msg 'manual_affiliation_form_contents.edit_education'/></span>
                    </h1>
                    <h1 *ngIf="addAffType == 'employment'" class="lightbox-title pull-left">
                        <span *ngIf="editAffiliation?.putCode?.value == null"><@orcid.msg 'manual_affiliation_form_contents.add_employment'/></span>
                        <span *ngIf="editAffiliation?.putCode?.value != null"><@orcid.msg 'manual_affiliation_form_contents.edit_employment'/></span>
                    </h1>
                    <h1 *ngIf="addAffType == 'invited-position'" class="lightbox-title pull-left">
                        <span *ngIf="editAffiliation?.putCode?.value == null"><@orcid.msg 'manual_affiliation_form_contents.add_invited_position'/></span>
                        <span *ngIf="editAffiliation?.putCode?.value != null"><@orcid.msg 'manual_affiliation_form_contents.edit_invited_position'/></span>
                    </h1>
                    <h1 *ngIf="addAffType == 'membership'" class="lightbox-title pull-left">
                        <span *ngIf="editAffiliation?.putCode?.value == null"><@orcid.msg 'manual_affiliation_form_contents.add_membership'/></span>
                        <span *ngIf="editAffiliation?.putCode?.value != null"><@orcid.msg 'manual_affiliation_form_contents.edit_membership'/></span>
                    </h1>
                    <h1 *ngIf="addAffType == 'qualification'" class="lightbox-title pull-left">
                        <span *ngIf="editAffiliation?.putCode?.value == null"><@orcid.msg 'manual_affiliation_form_contents.add_qualification'/></span>
                        <span *ngIf="editAffiliation?.putCode?.value != null"><@orcid.msg 'manual_affiliation_form_contents.edit_qualification'/></span>
                    </h1>
                    <h1 *ngIf="addAffType == 'service'" class="lightbox-title pull-left">
                        <span *ngIf="editAffiliation?.putCode?.value == null"><@orcid.msg 'manual_affiliation_form_contents.add_service'/></span>
                        <span *ngIf="editAffiliation?.putCode?.value != null"><@orcid.msg 'manual_affiliation_form_contents.edit_service'/></span>
                    </h1>
                </div>
            </div>

            <div class="row">
                <!-- Left Column -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <div class="form-group" *ngIf="editAffiliation?.disambiguatedAffiliationSourceId">
                        <span >
                           <label><@orcid.msg 'manual_affiliation_form_contents.labelorganization'/></label>
                        </span>
                        <span id="remove-disambiguated" class="pull-right">
                            <a (click)="removeDisambiguatedAffiliation()">
                                <span class="glyphicon glyphicon-remove-sign"></span><@orcid.msg 'common.remove'/>
                            </a>
                        </span>
                        <div class="relative" style="font-weight: strong;" *ngIf="disambiguatedAffiliation">
                            <span>{{disambiguatedAffiliation.value}}</span> <br />
                            <div>
                                <span>{{disambiguatedAffiliation.city}}</span><span *ngIf="disambiguatedAffiliation?.region"> (<span>{{disambiguatedAffiliation?.region}}</span>)</span><span *ngIf="disambiguatedAffiliation?.orgType">, <span>{{disambiguatedAffiliation?.orgType}}</span></span>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Institution -->
                    <div class="form-group">
                        <span>
                           <label *ngIf="!disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labelorganization'/></label>
                           <label *ngIf="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labeldisplayorganization'/></label>
                            <span class="required" [ngClass]="isValidClass(editAffiliation.affiliationName)">*</span>
                        </span>
                        <div *ngIf="editAffiliation?.affiliationName">           
                            <input 
                            id="affiliationName" 
                            class="form-control" 
                            name="affiliationName" 
                            type="text" 
                            [(ngModel)]="editAffiliation.affiliationName.value" 
                            placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_name'/>" 
                            (ngModelChange)="serverValidate('affiliations/affiliation/affiliationNameValidate.json')" 
                            />                            
                            <span class="orcid-error" *ngIf="editAffiliation?.affiliationName?.errors?.length > 0">
                                <div *ngFor='let error of editAffiliation.affiliationName.errors' [innerHtml]="error"></div>
                            </span>                            
                        </div>
                    </div>
                    

                    <!-- City -->
                    <div class="form-group">
                        <label *ngIf="!disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labelcity'/></label>
                        <label *ngIf="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labeldisplaycity'/></label>
                        <span class="required" [ngClass]="isValidClass(editAffiliation.city)">*</span>
                        <div *ngIf="editAffiliation?.city">
                                            
                            <input id="city" name="city" class="form-control" type="text" [(ngModel)]="editAffiliation.city.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_city'/>" (ngModelChange)="serverValidate('affiliations/affiliation/cityValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }"/>
                            <span class="orcid-error" *ngIf="editAffiliation?.city?.errors?.length > 0">
                                <div *ngFor='let error of editAffiliation.city.errors' [innerHtml]="error"></div>
                            </span>
                            
                        </div>
                    </div>

                    <!-- State/Region -->
                    <div class="form-group">
                        <label *ngIf="!disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labelregion'/></label>
                        <label *ngIf="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labeldisplayregion'/></label>
                        
                        <div *ngIf="editAffiliation?.region">
                            
                            <input name="region" type="text" class="form-control"  [(ngModel)]="editAffiliation.region.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_region'/>" (ngModelChange)="serverValidate('affiliations/affiliation/regionValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }"/>
                            <span class="orcid-error" *ngIf="editAffiliation?.region?.errors?.length > 0">
                                <div *ngFor='let error of editAffiliation.region.errors' [innerHtml]="error"></div>
                            </span>
                        
                        </div>
                        
                    </div>

                    <!-- Country -->
                    <div class="form-group">
                        <label *ngIf="!disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labelcountry'/></label>
                        <label *ngIf="disambiguatedAffiliation"><@orcid.msg 'manual_affiliation_form_contents.labeldisplaycountry'/></label>
                        <span class="required" [ngClass]="isValidClass(editAffiliation.country)">*</span>
                        <div *ngIf="editAffiliation?.country">
                            
                            <select id="country" name="country" [(ngModel)]="editAffiliation.country.value" (ngModelChange)="serverValidate('affiliations/affiliation/countryValidate.json')" class="form-control" [ngModelOptions]="{ updateOn: 'blur' }">
                                <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
                                <#list isoCountries?keys as key>
                                        <option value="${key}">${isoCountries[key]}</option>
                                </#list>
                            </select>
                            <span class="orcid-error" *ngIf="editAffiliation?.country?.errors?.length > 0">
                                <div *ngFor='let error of editAffiliation.country.errors' [innerHtml]="error"></div>
                            </span>
                            
                        </div>
                    </div>
                </div>
                

                <!-- Right Column -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <!-- Department -->
                    <div class="form-group">
                        <label><@orcid.msg 'manual_affiliation_form_contents.labeldepartment'/></label>
                        <div *ngIf="editAffiliation?.departmentName">
                            
                            <input id="departmentName" class="form-control" name="departmentName" type="text" [(ngModel)]="editAffiliation.departmentName.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_department'/>" (ngModelChange)="serverValidate('affiliations/affiliation/departmentValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }"/>
                            <span class="orcid-error" *ngIf="editAffiliation?.departmentName?.errors?.length > 0">
                                <div *ngFor='let error of editAffiliation.departmentName.errors' [innerHtml]="error"></div>
                            </span>
                        
                        </div>
                    </div>
                    <!-- Degree/Title -->
                    <div class="form-group">
                        <label *ngIf="addAffType != 'education'"><@orcid.msg 'manual_affiliation_form_contents.labelroletitle'/></label>
                        <label *ngIf="addAffType == 'education'"><@orcid.msg 'manual_affiliation_form_contents.labeldegreetitle'/></label>
                        <div *ngIf="editAffiliation?.roleTitle">
                            
                            <input name="roletitle" type="text" class="form-control"  [(ngModel)]="editAffiliation.roleTitle.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_title'/>" (ngModelChange)="serverValidate('affiliations/affiliation/roleTitleValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }"/>
                            <span class="orcid-error" *ngIf="editAffiliation?.roleTitle?.errors?.length > 0">
                                <div *ngFor='let error of editAffiliation.roleTitle.errors' [innerHtml]="error"></div>
                            </span>
                            
                        </div>
                    </div>
                    <!-- URL -->
                    <div class="form-group" *ngIf="editAffiliation?.url">
                        <label><@orcid.msg 'manual_affiliation_form_contents.url'/></label>
                        <div>
                            
                            <input name="url" type="text" class="form-control"  [(ngModel)]="editAffiliation.url.value" placeholder="<@orcid.msg 'manual_affiliation_form_contents.add_url'/>" (ngModelChange)="serverValidate('affiliations/affiliation/urlValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }"/>
                            <span class="orcid-error" *ngIf="editAffiliation?.url?.errors?.length > 0">
                                <div *ngFor='let error of editAffiliation.url.errors' [innerHtml]="error"></div>
                            </span>
                            
                        </div>
                    </div>
                    <!--  -->
                    <div class="form-group">
                        <label class="relative" for="manualAffiliation.startDay"><@orcid.msg 'manual_affiliation_form_contents.labelStartDate'/></label>
                        <span class="required" [ngClass]="isValidStartDate(editAffiliation.startDate)">*</span>
                        <div>
                            <select id="startYear" name="startYear" [(ngModel)]="editAffiliation.startDate.year" (ngModelChange)="serverValidate('affiliations/affiliation/datesValidate.json')">
                                <#list years?keys as key>
                                    <option value="${key}">${years[key]}</option>
                                </#list>
                            </select>                          
                            <select id="startMonth" name="startMonth" [(ngModel)]="editAffiliation.startDate.month" (ngModelChange)="serverValidate('affiliations/affiliation/datesValidate.json')">
                                <#list months?keys as key>
                                    <option value="${key}">${months[key]}</option>
                                </#list>
                            </select>                   
                            <select id="startDay" name="startDay" [(ngModel)]="editAffiliation.startDate.day" (ngModelChange)="serverValidate('affiliations/affiliation/datesValidate.json')">
                                <#list days?keys as key>
                                    <option value="${key}">${days[key]}</option>
                                </#list>
                            </select>
                        </div>
                        
                        <span class="orcid-error" *ngIf="editAffiliation?.startDate?.errors?.length > 0">
                            <div *ngFor='let error of editAffiliation.startDate.errors' [innerHtml]="error"></div>
                        </span>
                                
                    </div>

                    <div class="form-group">
                        <label class="relative" for="manualAffiliation.endDay"><@orcid.msg 'manual_affiliation_form_contents.labelEndDateLeave'/></label>
                        <div class="relative">
                            
                            <select id="endYear" name="endYear" [(ngModel)]="editAffiliation.endDate.year">
                                <#list years?keys as key>
                                    <option value="${key}">${years[key]}</option>
                                </#list>
                            </select>
                            <select id="endMonth" name="endMonth" [(ngModel)]="editAffiliation.endDate.month">
                                <#list months?keys as key>
                                    <option value="${key}">${months[key]}</option>
                                </#list>
                            </select>
                            <select id="endDay" name="endDay" [(ngModel)]="editAffiliation.endDate.day">
                                <#list days?keys as key>
                                    <option value="${key}">${days[key]}</option>
                                </#list>
                            </select>
                        </div>
                        
                        <span class="orcid-error" *ngIf="editAffiliation?.endDate?.errors?.length > 0">
                            <div *ngFor='let error of editAffiliation.endDate.errors' [innerHtml]="error"></div>
                        </span>
                        
                    </div>     
                    <!-- visibility controlls -->
                    <div *ngIf="togglzDialogPrivacyOption" class="control-group visibility-container">
                            <label>
                                Set visibility:
                            </label>
                            <div class="controlls">
                                <privacy-toggle-ng2 
                                [dataPrivacyObj]="editAffiliation" 
                                elementId="affiliation-privacy-toggle" 
                                privacyNodeName="visibility" 
                                ></privacy-toggle-ng2> 
                            </div>
                    </div>
                    <!-- END visibility controlls -->
                    <div class="control-group">
                        <ul class="inline-list margin-separator pull-left">
                            <li>
                                <button id="save-affiliation" class="btn btn-primary" (click)="addAffiliation()" [disabled]="addingAffiliation" [ngClass]="{disabled:addingAffiliation}">
                                    
                                    <span *ngIf="editAffiliation?.putCode?.value == null"><@orcid.msg 'manual_affiliation_form_contents.btnaddtolist'/></span>
                                    <span *ngIf="editAffiliation?.putCode?.value != null"><@orcid.msg 'manual_affiliation_form_contents.btnedit'/></span>
                                </button>
                            </li>                       
                            <li>                      
                                <a class="cancel-option" (click)="cancelEdit()"><@orcid.msg 'freemarker.btncancel' /></a>                  
                            </li>
                            <li>
                                <span *ngIf="addingAffiliation">
                                    <i class="glyphicon glyphicon-refresh spin x2 green"></i>
                                </span>
                            </li>
                        </ul>
                    </div>
                    <div class="control-group errors">
                        <span *ngIf="editAffiliation?.errors?.length > 0" class="alert"><@orcid.msg 'common.please_fix_errors' /></span>                    
                    </div>
                </div>
            </div>
        </form>
    </div>
</script>