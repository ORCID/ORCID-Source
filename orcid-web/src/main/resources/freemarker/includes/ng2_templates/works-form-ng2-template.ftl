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
        <form ><!-- update-fn="putWork()" -->
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
                            <div class="relative" *ngIf="editWork?.workCategory?.value">
                                <select id="workCategory" name="workCategory" class="form-control" [ngModel]="editWork.workCategory.value" (ngModelChange)="loadWorkTypes(); clearErrors(); applyLabelWorkType();">
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

                            <select id="workType" name="workType" class="form-control" [ngModel]="editWork?.workType?.value" ngOptions="type.key as type.value for type in types | orderBy:sortOtherLast" (ngModelChange)="clearErrors(); applyLabelWorkType();">
                            </select>

                            <span class="orcid-error" *ngIf="editWork?.workType?.errors.length > 0">
                                <div *ngFor='let error of editWork.workType.errors' [innerHtml]="error"></div>
                            </span>
                        </div>

                        <div class="form-group">
                            <label><@orcid.msg 'manual_work_form_contents.labeltitle'/></label>
                            <span class="required" *ngIf="editWork" [ngClass]="isValidClass(editWork?.title)">*</span>
                            <div class="relative">
                                <input id="work-title" name="familyNames" type="text" class="form-control"  [ngModel]="editWork?.title.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_title'/>" (ngModelChange)="serverValidate('works/work/titleValidate.json')" />
                                <span class="orcid-error" *ngIf="editWork?.title?.errors?.length > 0">
                                    <div *ngFor='let error of editWork?.title?.errors' [innerHtml]="error"></div>
                                </span>
                                <div class="add-item-link clearfix">
                                    <span *ngIf="!editTranslatedTitle"><a (click)="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.labelshowtranslatedtitle'/></a></span>
                                    <span *ngIf="editTranslatedTitle"><a (click)="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-minus-sign"></i> <@orcid.msg 'manual_work_form_contents.labelhidetranslatedtitle'/></a></span>
                                </div>
                            </div>
                        </div>

                        <div id="translatedTitle">
                            <div class="form-group">
                                <label><@orcid.msg 'manual_work_form_contents.labeltranslatedtitle'/></label>
                                <div class="relative">
                                    <input name="translatedTitle" type="text" class="form-control" [ngModel]="editWork?.translatedTitle?.content" placeholder="<@orcid.msg 'manual_work_form_contents.add_translated_title'/>" (ngModelChange)="serverValidate('works/work/translatedTitleValidate.json')" />                                                     
                                </div>                      
                                <span class="orcid-error" *ngIf="editWork?.translatedTitle?.errors.length > 0">
                                    <div *ngFor='let error of editWork.translatedTitle.errors' [innerHtml]="error"></div>
                                </span>
                            </div>

                            <div class="form-group">
                                <label class="relative"><@orcid.msg 'manual_work_form_contents.labeltranslatedtitlelanguage'/></label>
                                <div class="relative">
                                    <!--               
                                    <select id="language" class="form-control" name="language" [ngModel]="editWork?.translatedTitle?.languageCode" (ngModelChange)="serverValidate('works/work/translatedTitleValidate.json')">          
                                        <#list languages?keys as key>
                                            <option value="${languages[key]}">${key}</option>
                                        </#list>
                                    </select>
                                    -->            
                                </div>
                            </div>                  
                        </div>

                        <div class="form-group">
                            <label><@orcid.msg 'manual_work_form_contents.labelsubtitle'/></label>
                            <div class="relative">
                                <input name="familyNames" type="text" class="form-control"  [ngModel]="editWork?.subtitle?.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_subtitle'/>" (ngModelChange)="serverValidate('works/work/subtitleValidate.json')" />
                                <span class="orcid-error" *ngIf="editWork?.subtitle?.errors.length > 0">
                                    <div *ngFor='let error of editWork.subtitle.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>{{contentCopy?.titleLabel}}</label>
                            <div class="relative">
                                <input name="journalTitle" type="text" class="form-control"  [ngModel]="editWork?.journalTitle?.value" placeholder="{{contentCopy?.titlePlaceholder}}" (ngModelChange)="serverValidate('works/work/journalTitleValidate.json')"    />
                                <span class="orcid-error" *ngIf="editWork?.journalTitle?.errors?.length > 0">
                                    <div *ngFor='let error of editWork.journalTitle.errors' [innerHtml]="error"></div>
                                </span>                     
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="relative" for="manualWork.day"><@orcid.msg 'manual_work_form_contents.labelPubDate'/></label>
                            <div class="relative"> 
                                <!--                     
                                <select id="year" name="month" [ngModel]="editWorkork?.journalTitle?.value" placeholder="{{contentCopy?.publicationDate?.year">
                                    <#list years?keys as key>
                                        <option value="${key}">${years[key]}</option>
                                    </#list>
                                </select>
                                <select id="month" name="month" [ngModel]="editWork?.publicationDate?.month">
                                    <#list months?keys as key>
                                        <option value="${key}">${months[key]}</option>
                                    </#list>
                                </select>
                                <select id="day" name="day" [ngModel]="editWork?.publicationDate?.day">
                                    <#list days?keys as key>
                                        <option value="${key}">${days[key]}</option>
                                    </#list>
                                </select> 
                                -->                                              
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
                                <select id="citationType" name="citationType" class="form-control" [ngModel]="editWork?.citation?.citationType?.value" (ngModelChange)="serverValidate('works/work/citationValidate.json')">
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
                                
                                <textarea name="citation" type="text" class="form-control"  [ngModel]="editWork?.citation?.citation?.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_citation'/>" (ngModelChange)="serverValidate('works/work/citationValidate.json')"></textarea>
                                <span class="orcid-error" *ngIf="editWork?.citation?.citation?.errors?.length > 0">
                                    <div *ngFor='let error of editWork.citation.citation.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                        </div>
                    
                        <div class="form-group">
                            <label><@orcid.msg 'manual_work_form_contents.labeldescription'/></label>
                            <div class="relative">
                                <textarea name="discription" type="text" class="form-control"  [ngModel]="editWork?.shortDescription?.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_description'/>" (ngModelChange)="serverValidate('works/work/descriptionValidate.json')"></textarea>
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
                                <!--  
                                <select id="role" name="role" [ngModel]="contributor?.contributorRole?.value">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.ContributorRole.empty' /></option>
                                    <#list roles?keys as key>
                                        <option value="${key}">${roles[key]}</option>
                                    </#list>
                                </select>
                            -->
                                <a href (click)="deleteContributor(contributor)" class="glyphicon glyphicon-trash grey"></a>
                                <span class="orcid-error" *ngIf="contributor?.contributorRole?.errors?.length > 0">
                                        <div *ngFor='let error of contributor.contributorRole.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelcredited'/></label>
                            <div class="relative">
                                <!--
                                <select id="sequence" name="sequence" [ngModel]="contributor?.contributorSequence?.value">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.SequenceType.empty'/></option>
                                    <#list sequences?keys as key>
                                        <option value="${key}">${sequences[key]}</option>
                                    </#list>
                                </select>
                            -->
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
                        
                        <div *ngFor="let workExternalIdentifier of editWork.workExternalIdentifiers"> 
                            <div class="form-group">
                                <label class="relative"><@orcid.msg 'manual_work_form_contents.labelIDtype'/></label>
                                <div class="relative">
                                    
                                    <input id="worksIdType{{$index}}" name="workExternalIdentifer{{$index}}" class="form-control" type="text" 
                                    [ngModel]="workExternalIdentifier?.workExternalIdentifierType?.value" 
                                    placeholder="<@orcid.msg 'org.orcid.jaxb.model.record.WorkExternalIdentifierType.empty'/>" 
                                    uib-typeahead="eid.name as eid.description for eid in getExternalIDTypes($viewValue)" 
                                    typeahead-loading="loading" 
                                    typeahead-min-length="0" 
                                    typeahead-wait-ms="300" 
                                    typeahead-on-select="fillUrl(workExternalIdentifier);$scope.$apply();"
                                    typeahead-input-formatter="formatExternalIDType($model)"
                                    typeahead-show-hint="true"
                                    typeahead-highlight="false"
                                    typeahead-editable="false"
                                    />
                                    
                                    <span class="orcid-error" *ngIf="workExternalIdentifier?.workExternalIdentifierType?.errors?.length > 0">
                                        <div *ngFor='let error of workExternalIdentifier.workExternalIdentifierType.errors' [innerHtml]="error"></div>
                                    </span>
                                </div>
                            </div>
                            <div class="form-group">
                                <label><@orcid.msg 'manual_work_form_contents.labelID'/></label>
                                <div class="relative">
                                    <input id="worksIdValue{{$index}}" name="currentWorkExternalIds" type="text" class="form-control action-icon-inside"  [ngModel]="workExternalIdentifier?.workExternalIdentifierId?.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_ID'/>"  (ngModelChange)="fillUrl(workExternalIdentifier)" />
                                    <span class="orcid-error" *ngIf="workExternalIdentifier?.workExternalIdentifierId?.errors?.length > 0">
                                        <div *ngFor='let error of workExternalIdentifier.workExternalIdentifierId.errors' [innerHtml]="error"></div>
                                    </span>
                                </div>                      
                            </div>      
                            <div class="form-group">
                                <label><@orcid.msg 'manual_work_form_contents.identifierurl'/></label>
                                <div class="relative">
                                    <input id="worksIdUrl{{$index}}"name="currentWorkExternalIdUrl" type="text" class="form-control action-icon-inside"  [ngModel]="workExternalIdentifier?.url?.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL'/>" />
                                    <span class="orcid-error" *ngIf="workExternalIdentifier?.url?.errors?.length > 0">
                                        <div *ngFor='let error of workExternalIdentifier.url.errors' [innerHtml]="error"></div>
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
                                        <input type="radio" name="relationship{{$index}}" [ngModel]="workExternalIdentifier?.relationship?.value" value="self" />
                                        <@orcid.msg "common.self" />
                                    </label>
                                                                                            
                                    <label class="checkbox-inline">
                                        <input type="radio" name="relationship{{$index}}" [ngModel]="workExternalIdentifier?.relationship?.value" value="part-of" />
                                        <@orcid.msg "common.part_of" />
                                    </label>                            
                                    <a href (click)="deleteExternalIdentifier(workExternalIdentifier)" class="glyphicon glyphicon-trash grey action-icon-align-right" *ngIf="!($first && editWork.workExternalIdentifiers.length == 1)"></a>                            
                                </div>
                                <div *ngIf="$last" class="add-item-link clearfix">         
                                    <span><a href (click)="addExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.add_external_identifier' /></a></span>
                                </div>
                            </div>
                        </div>
                        <div *ngIf="editWork?.workExternalIdentifiers == null || editWork.workExternalIdentifiers.length == 0">
                            <div class="add-item-link">
                                <span><a href (click)="addExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.add_external_identifier' /></a></span>
                            </div>
                        </div>
                    
                        <div class="form-group">
                            <label class="relative"><@orcid.msg 'common.url'/></label>
                            <div class="relative">
                                <input name="url" type="text" class="form-control"  [ngModel]="editWork?.url?.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL'/>" (ngModelChange)="serverValidate('works/work/urlValidate.json')" />
                                <span class="orcid-error" *ngIf="editWork?.url?.errors?.length > 0">
                                    <div *ngFor='let error of editWork.url.errors' [innerHtml]="error"></div>
                                </span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelformlanguage'/></label>
                            <div class="relative">  
                                <!--
                                <select id="language" class="form-control" name="language" [ngModel]="editWork?.languageCode?.value">
                                    <option value="${currentLocaleKey}">${currentLocaleValue}</option>
                                    <#list languages?keys as key>
                                        <option value="${languages[key]}">${key}</option>
                                    </#list>
                                </select>
                            -->
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="country"><@orcid.msg 'manual_work_form_contents.labelcountry'/></label>
                            <div class="relative">
                                <!--
                                <select id="isoCountryCode" class="form-control" name="isoCountryCode" [ngModel]="editWork?.countryCode?.value">
                                    <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
                                    <#list isoCountries?keys as key>
                                        <option value="${key}">${isoCountries[key]}</option>                                
                                    </#list>
                                </select>
                            -->
                            </div>
                        </div>
                        
                        

                        <div class="control-group">

                            <div *ngIf="editWork?.putCode?.value != null">
                                <ul class="inline-list margin-separator pull-left">
                                    <li>
                                        <button class="btn btn-primary" (click)="putWork()" [disabled]="addingWork" [ngClass]="{disabled:addingWork}">
                                            <@orcid.msg 'freemarker.btnsave'/>
                                        </button>
                                    </li>
                                    <li>                                
                                        <a class="cancel-option" (click)="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
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
                                        <button class="btn btn-primary" id='save-new-work' (click)="putWork()" [disabled]="addingWork" [ngClass]="{disabled:addingWork}">
                                            <@orcid.msg 'manual_work_form_contents.btnaddtolist'/>
                                        </button>
                                    </li>
                                    <li>                                
                                        <a class="cancel-option" (click)="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
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
        </form>
    </div> 
</script>