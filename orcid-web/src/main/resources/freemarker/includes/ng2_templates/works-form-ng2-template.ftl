<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->

<script type="text/ng-template" id="works-form-ng2-template">
    <div class="add-work colorbox-content">

            <div class="lightbox-container-ie7">        
                <!-- Title -->
                <div class="row">           
                    <div class="col-md-9 col-sm-8 col-xs-9">    
                        <h1 class="lightbox-title pull-left">
                            <div *ngIf="editWork?.putCode?.value != null">
                                <@orcid.msg 'manual_work_form_contents.edit_work'/>
                            </div>
                            <div *ngIf="editWork?.putCode?.value == null">
                                <@orcid.msg 'manual_work_form_contents.add_work'/>
                            </div>
                        </h1>
                    </div>          
                </div>

                <!-- Main content -->       
                <div class="row">
                    <!-- Left Column -->            
                    <div class="col-md-6 col-sm-6 col-xs-12">   
                        
                        <div class="form-group">
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelworkcategory'/></label>
                            <span *ngIf="editWork?.workCategory?.value" class="required" [ngClass]="isValidClass(editWork.workCategory)">*</span>
                            <div class="relative" *ngIf="editWork?.workCategory">
                                <select id="workCategory" name="workCategory" class="form-control" [(ngModel)]="editWork.workCategory.value" (ngModelChange)="loadWorkTypes(); clearErrors(); applyLabelWorkType(); updateRelationships();">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.record.WorkCategory.empty' /></option>
                                    <#list workCategories?keys as key>
                                        <option value="${key}">${workCategories[key]}</option>
                                    </#list>
                                </select> 
                                
                                <span class="orcid-error" *ngIf="editWork?.workCategory?.errors?.length > 0">
                                    <div *ngFor='let error of editWork.workCategory.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                        </div>

                        <div class="form-group" *ngIf="editWork">
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelworktype'/></label>
                            <span *ngIf="editWork?.workType" class="required" [ngClass]="isValidClass(editWork.workType)">*</span>

                            <select id="workType" name="workType" class="form-control" [(ngModel)]="editWork.workType.value" (ngModelChange)="clearErrors(); applyLabelWorkType(); updateRelationships();">
                                <option *ngFor="let type of types" value={{type.key}}>{{type.value}}</option>
                            </select>

                            <span class="orcid-error" *ngIf="editWork?.workType?.errors.length > 0">
                                <div *ngFor='let error of editWork.workType.errors' [innerHtml]="error"></div>
                            </span>
                        </div>

                        <div class="form-group">
                            <label><@orcid.msg 'manual_work_form_contents.labeltitle'/></label>
                            <span class="required" *ngIf="editWork?.title" [ngClass]="isValidClass(editWork?.title)">*</span>
                            <div class="relative">
                                <input id="work-title" name="familyNames" type="text" class="form-control" [(ngModel)]="editWork.title.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_title'/>" (ngModelChange)="serverValidate('works/work/titleValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }"/>
                                <span class="orcid-error" *ngIf="editWork?.title?.errors?.length > 0">
                                    <div *ngFor='let error of editWork?.title?.errors' [innerHtml]="error"></div>
                                </span>
                                <div class="add-item-link clearfix">
                                    <span *ngIf="!editTranslatedTitle"><a (click)="toggleTranslatedTitle()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.labelshowtranslatedtitle'/></a></span>
                                    <span *ngIf="editTranslatedTitle"><a (click)="toggleTranslatedTitle()"><i class="glyphicon glyphicon-minus-sign"></i> <@orcid.msg 'manual_work_form_contents.labelhidetranslatedtitle'/></a></span>
                                </div>
                            </div>
                        </div>

                        <div *ngIf="editTranslatedTitle">
                            <div class="form-group" *ngIf="editWork">
                                <label><@orcid.msg 'manual_work_form_contents.labeltranslatedtitle'/></label>
                                <div class="relative">
                                    <input name="translatedTitle" type="text" class="form-control" [(ngModel)]="editWork.translatedTitle.content" placeholder="<@orcid.msg 'manual_work_form_contents.add_translated_title'/>" (ngModelChange)="serverValidate('works/work/translatedTitleValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }"/>                                                     
                                </div>                      
                                <span class="orcid-error" *ngIf="editWork?.translatedTitle?.errors.length > 0">
                                    <div *ngFor='let error of editWork.translatedTitle.errors' [innerHtml]="error"></div>
                                </span>
                            </div>

                            <div class="form-group">
                                <label class="relative"><@orcid.msg 'manual_work_form_contents.labeltranslatedtitlelanguage'/></label>
                                <div class="relative">            
                                    <select id="translatedTitleLanguage" class="form-control" name="translatedTitleLanguage" [(ngModel)]="editWork.translatedTitle.languageCode" (ngModelChange)="serverValidate('works/work/translatedTitleValidate.json')">      
                                        <#list languages?keys as key>
                                            <option value="${languages[key]}">${key}</option>
                                        </#list>
                                    </select>         
                                </div>
                            </div>                  
                        </div>

                        <div class="form-group">
                            <label><@orcid.msg 'manual_work_form_contents.labelsubtitle'/></label>
                            <div class="relative">
                                <input name="familyNames" type="text" class="form-control"  [(ngModel)]="editWork.subtitle.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_subtitle'/>" (ngModelChange)="serverValidate('works/work/subtitleValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }" />
                                <span class="orcid-error" *ngIf="editWork?.subtitle?.errors.length > 0">
                                    <div *ngFor='let error of editWork.subtitle.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>{{contentCopy?.titleLabel}}</label>
                            <div class="relative">
                                <input name="journalTitle" type="text" class="form-control"  [(ngModel)]="editWork.journalTitle.value" placeholder="{{contentCopy?.titlePlaceholder}}" (ngModelChange)="serverValidate('works/work/journalTitleValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }" />
                                <span class="orcid-error" *ngIf="editWork?.journalTitle?.errors?.length > 0">
                                    <div *ngFor='let error of editWork.journalTitle.errors' [innerHtml]="error"></div>
                                </span>                     
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="relative" for="manualWork.day"><@orcid.msg 'manual_work_form_contents.labelPubDate'/></label>
                            <div class="relative"> 
                                <select id="year" name="year" [(ngModel)]="editWork.publicationDate.year">
                                    <#list years?keys as key>
                                        <option value="${key}">${years[key]}</option>
                                    </#list>
                                </select>
                                <select id="month" name="month" [(ngModel)]="editWork.publicationDate.month">
                                    <#list months?keys as key>
                                        <option value="${key}">${months[key]}</option>
                                    </#list>
                                </select>
                                <select id="day" name="day" [(ngModel)]="editWork.publicationDate.day">
                                    <#list days?keys as key>
                                        <option value="${key}">${days[key]}</option>
                                    </#list>
                                </select>                                            
                            </div>
                            <div class="relative">
                                <span class="orcid-error" *ngIf="editWork?.publicationDate?.errors?.length > 0">
                                    <div *ngFor='let error of editWork?.publicationDate?.errors' [innerHtml]="error"></div>
                                </span>
                            </div>                
                        </div>              
                        <div class="control-group">
                            <span class="citation-title">
                                <strong><@orcid.msg 'manual_work_form_contents.titlecitation'/></strong>
                            </span>
                        </div>
                        <div class="form-group">
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelcitationtype'/></label>
                            <div class="relative">
                                <select id="citationType" name="citationType" class="form-control" [(ngModel)]="editWork.citation.citationType.value" (ngModelChange)="serverValidate('works/work/citationValidate.json')">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.CitationType.empty' /></option>
                                    <#list citationTypes?keys as key>
                                        <option value="${key}">${citationTypes[key]}</option>
                                    </#list>
                                </select> 
                                <span class="orcid-error" *ngIf="editWork?.citation?.citationType?.errors?.length > 0">
                                    <div *ngFor='let error of editWork.citation.citationType.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label><@orcid.msg 'manual_work_form_contents.labelcitation'/></label>
                            <div class="relative">
                                
                                <textarea name="citation" type="text" class="form-control"  [(ngModel)]="editWork.citation.citation.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_citation'/>" (ngModelChange)="serverValidate('works/work/citationValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }"></textarea>
                                <span class="orcid-error" *ngIf="editWork?.citation?.citation?.errors?.length > 0">
                                    <div *ngFor='let error of editWork.citation.citation.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                        </div>
                    
                        <div class="form-group">
                            <label><@orcid.msg 'manual_work_form_contents.labeldescription'/></label>
                            <div class="relative">
                                <textarea name="discription" type="text" class="form-control"  [(ngModel)]="editWork.shortDescription.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_description'/>" (ngModelChange)="serverValidate('works/work/descriptionValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }"></textarea>
                                <span class="orcid-error" *ngIf="editWork?.shortDescription?.errors?.length > 0">
                                    <div *ngFor='let error of editWork.shortDescription.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                        </div>
                        
                    </div>

                    <!-- Right column -->
                    <div class="col-md-6 col-sm-6 col-xs-12" *ngIf="editWork?.contributors?.length > 0">               

                        <div class="control-group" *ngFor="let contributor of editWork.contributors">
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelRole'/></label>
                            <div class="relative">  
                                <select id="role" name="role" [(ngModel)]="contributor.contributorRole.value">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.ContributorRole.empty' /></option>
                                    <#list roles?keys as key>
                                        <option value="${key}">${roles[key]}</option>
                                    </#list>
                                </select>
                                <button (click)="deleteContributor(contributor)" class="glyphicon glyphicon-trash grey"></button>
                                <span class="orcid-error" *ngIf="contributor?.contributorRole?.errors?.length > 0">
                                        <div *ngFor='let error of contributor.contributorRole.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelcredited'/></label>
                            <div class="relative">
                                <select id="sequence" name="sequence" [(ngModel)]="contributor.contributorSequence.value">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.SequenceType.empty'/></option>
                                    <#list sequences?keys as key>
                                        <option value="${key}">${sequences[key]}</option>
                                    </#list>
                                </select>
                                <span class="orcid-error" *ngIf="contributor?.contributorSequence?.errors?.length > 0">
                                        <div *ngFor='let error of contributor.contributorSequence.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                        </div>                                              
                    </div>
                    
                    <div class="col-md-6 col-sm-6 col-xs-12" *ngIf="editWork?.workExternalIdentifiers">                        
                        <div class="control-group">
                            <span><strong><@orcid.msg 'manual_work_form_contents.titleexternalidentifier'/></strong></span>
                        </div>
                        <div *ngFor="let workExternalIdentifier of editWork.workExternalIdentifiers;let last=last;let i=index;trackBy:trackByIndex"> 
                            <div class="form-group workExtIdType">
                                <label class="relative"><@orcid.msg 'manual_work_form_contents.labelIDtype'/></label>
                                <div class="relative">
                                    <input id="workIdType{{i}}" name="workIdType{{i}}" type="text" class="form-control" placeholder="<@orcid.msg 'org.orcid.jaxb.model.record.WorkExternalIdentifierType.empty'/>" [inputFormatter]="formatExtIdTypeInput" [(ngModel)]="editWork.workExternalIdentifiers[i].externalIdentifierType.value" [ngbTypeahead]="search" [resultFormatter]="formatExtIdTypeResult" (selectItem)="changeExtIdType(i, $event)" [focusFirst]=true [editable]=false /> 
                                    <span class="orcid-error" *ngIf="editWork?.workExternalIdentifiers[i]?.externalIdentifierType?.errors?.length > 0">
                                        <div *ngFor='let error of editWork.workExternalIdentifiers[i].externalIdentifierType.errors' [innerHtml]="error"></div>
                                    </span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label><@orcid.msg 'manual_work_form_contents.labelID'/></label>
                                <div class="relative">
                                    <input id="workIdValue{{i}}" name="workIdValue{{i}}" type="text" class="form-control action-icon-inside"  [(ngModel)]="editWork.workExternalIdentifiers[i].externalIdentifierId.value" (blur)="fillUrl(i)" placeholder="<@orcid.msg 'manual_work_form_contents.add_ID'/>"[ngModelOptions]="{ updateOn: 'blur' }" />
                                    <span *ngIf="editWork.workExternalIdentifiers[i].resolvingId">
                                        <i class="glyphicon glyphicon-refresh spin green"></i>
                                    </span>
                                    <span class="orcid-error" *ngIf="editWork?.workExternalIdentifiers[i]?.externalIdentifierId?.errors?.length > 0">
                                        <div *ngFor='let error of editWork.workExternalIdentifiers[i].externalIdentifierId.errors' [innerHtml]="error"></div>
                                    </span>
                                </div>                      
                            </div>      
                            <div class="form-group">
                                <label><@orcid.msg 'manual_work_form_contents.identifierurl'/></label>
                                <div class="relative">
                                    <input id="workIdUrl{{i}}" name="workIdUrl{{i}}" type="text" class="form-control action-icon-inside"  [(ngModel)]="editWork.workExternalIdentifiers[i].url.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL'/>" [ngModelOptions]="{ updateOn: 'blur' }" />
                                    <span class="orcid-error" *ngIf="editWork?.workExternalIdentifiers[i]?.url?.errors?.length > 0">
                                        <div *ngFor='let error of editWork.workExternalIdentifiers[i].url.errors' [innerHtml]="error"></div>
                                    </span>
                                </div>
                            </div>              
                            <div class="bottomBuffer">
                                <label><@orcid.msg 'common.ext_id.relationship'/>
                                    <div class="popover-help-container">
                                        <i class="glyphicon glyphicon-question-sign"></i>
                                        <div id="widget-help" class="popover bottom">
                                            <div class="arrow"></div>
                                            <div class="popover-content">
                                                <p><@orcid.msg 'relationship.tooltip'/></p>
                                            </div>
                                        </div>
                                    </div>
                                </label>
                                <div class="relative">                          
                                    <label class="checkbox-inline">
                                        <input type="radio" name="relationship{{i}}" [(ngModel)]="editWork.workExternalIdentifiers[i].relationship.value" value="self" />
                                        <@orcid.msg "common.self" />
                                    </label>
                                                                                            
                                    <label class="checkbox-inline">
                                        <input type="radio" name="relationship{{i}}" [(ngModel)]="editWork.workExternalIdentifiers[i].relationship.value" value="part-of" />
                                        <@orcid.msg "common.part_of" />
                                    </label>                            
                                    
                                    <label class="checkbox-inline">
                                        <input type="radio" name="relationship{{i}}" [(ngModel)]="editWork.workExternalIdentifiers[i].relationship.value" value="version-of" />
                                        <@orcid.msg "common.version_of" />
                                    </label>    
                                    <button *ngIf="editWork.workExternalIdentifiers.length > 1" (click)="deleteExternalIdentifier(i)" class="glyphicon glyphicon-trash grey action-icon-align-right"></button>                            
                                    <span class="orcid-error" *ngIf="editWork?.workExternalIdentifiers[i].relationship?.errors?.length > 0">
                                        <div *ngFor='let error of editWork.workExternalIdentifiers[i].relationship.errors' [innerHtml]="error"></div>
                                    </span>
                                </div>                                                               
                                <div *ngIf="last" class="add-item-link clearfix">   
                                    <span><button class="btn-white-no-border" (click)="addExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.add_external_identifier' /></button></span>
                                </div>
                            </div>
                        </div>
                        <div *ngIf="editWork?.workExternalIdentifiers == null || editWork.workExternalIdentifiers.length == 0">
                            <div class="add-item-link">
                                <span><button class="btn-white-no-border" (click)="addExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.add_external_identifier' /></button></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="relative"><@orcid.msg 'common.url'/></label>
                            <div class="relative">
                                <input name="url" type="text" class="form-control"  [(ngModel)]="editWork.url.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL'/>" (ngModelChange)="serverValidate('works/work/urlValidate.json')" [ngModelOptions]="{ updateOn: 'blur' }"/>
                                <span class="orcid-error" *ngIf="editWork?.url?.errors?.length > 0">
                                    <div *ngFor='let error of editWork.url.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelformlanguage'/></label>
                            <div class="relative">  
                                <select id="language" class="form-control" name="language" [(ngModel)]="editWork.languageCode.value">
                                    <option value=""></option>
                                    <#list languages?keys as key>
                                        <option value="${languages[key]}">${key}</option>
                                    </#list>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="country"><@orcid.msg 'manual_work_form_contents.labelcountry'/></label>
                            <div class="relative">
                                <select id="isoCountryCode" class="form-control" name="isoCountryCode" [(ngModel)]="editWork.countryCode.value">
                                    <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
                                    <option *ngFor="let country of sortedCountryNames" [ngValue]="countryNamesToCountryCodes[country]" >{{country}}</option>
                                </select>
                            </div>
                        </div>

                        <!-- visibility controlls -->
                        <div *ngIf="togglzDialogPrivacyOption" class="control-group visibility-container">
                                <label>
                                    Set visibility:
                                </label>
                                <div class="controlls">
                                    <privacy-toggle-ng2 
                                    [dataPrivacyObj]="editWork" 
                                    elementId="work-privacy-toggle" 
                                    privacyNodeName="visibility" 
                                    ></privacy-toggle-ng2> 
                                </div>
                        </div>
                        <!-- END visibility controlls -->

                        <div class="control-group">

                            <div *ngIf="editWork?.putCode?.value != null">
                                <ul class="inline-list margin-separator pull-left">
                                    <li>
                                        <button class="btn btn-primary" (click)="addWork()" [disabled]="addingWork" [ngClass]="{disabled:addingWork}">
                                            <@orcid.msg 'freemarker.btnsave'/>
                                        </button>
                                    </li>
                                    <li>                                
                                        <a class="cancel-option" (click)="cancelEdit()"><@orcid.msg 'freemarker.btncancel' /></a>
                                    </li>
                                    <li>
                                        <span *ngIf="addingWork">
                                            <i class="glyphicon glyphicon-refresh spin x2 green"></i>
                                        </span>
                                    </li>
                                </ul>
                            </div>

                            <div *ngIf="editWork?.putCode?.value == null">
                                <ul class="inline-list margin-separator pull-left">
                                    <li>
                                        <button class="btn btn-primary" id='save-new-work' (click)="addWork()" [disabled]="addingWork" [ngClass]="{disabled:addingWork}">
                                            <@orcid.msg 'manual_work_form_contents.btnaddtolist'/>
                                        </button>
                                    </li>
                                    <li>                                
                                        <a class="cancel-option" (click)="cancelEdit()"><@orcid.msg 'freemarker.btncancel' /></a>
                                    </li>
                                    <li>
                                        <span *ngIf="addingWork">
                                            <i class="glyphicon glyphicon-refresh spin x2 green"></i>
                                        </span>
                                    </li>
                                </ul>
                            </div>

                            <div class="control-group errors">                  
                                <span *ngIf="editWork?.errors?.length > 0" class="alert"><@orcid.msg 'common.please_fix_errors' /></span>
                            </div>
                                            
                        </div>
                    </div>
                </div>
            </div>    

    </div> 
</script>