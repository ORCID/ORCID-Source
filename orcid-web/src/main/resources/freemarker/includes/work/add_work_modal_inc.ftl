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
<script type="text/ng-template" id="add-work-modal">
    <div class="add-work colorbox-content">
        <fn-form update-fn="putWork()">
            <div class="lightbox-container-ie7">        
                <!-- Title -->
                <div class="row">           
                    <div class="col-md-9 col-sm-8 col-xs-9">    
                        <h1 class="lightbox-title pull-left">
                            <div ng-show="editWork.putCode.value != null">
                                <@orcid.msg 'manual_work_form_contents.edit_work'/>
                            </div>
                            <div ng-show="editWork.putCode.value == null">
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
                            <span class="required" ng-class="isValidClass(editWork.workCategory)">*</span>
                            <div class="relative">
                                <select id="workCategory" name="workCategory" class="form-control" ng-model="editWork.workCategory.value" ng-change="loadWorkTypes(); clearErrors(); applyLabelWorkType();">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.record.WorkCategory.empty' /></option>
                                    <#list workCategories?keys as key>
                                        <option value="${key}">${workCategories[key]}</option>
                                    </#list>
                                </select> 
                                
                                <span class="orcid-error" ng-show="editWork.workCategory.errors.length > 0">
                                    <div ng-repeat='error in editWork.workCategory.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelworktype'/></label>
                            <span class="required" ng-class="isValidClass(editWork.workType)">*</span>
                            <select id="workType" name="workType" class="form-control" ng-model="editWork.workType.value" ng-options="type.key as type.value for type in types | orderBy:sortOtherLast" ng-change="clearErrors(); applyLabelWorkType();">
                            </select>
                            <span class="orcid-error" ng-show="editWork.workType.errors.length > 0">
                                <div ng-repeat='error in editWork.workType.errors' ng-bind-html="error"></div>
                            </span>
                        </div>

                        <div class="form-group">
                            <label><@orcid.msg 'manual_work_form_contents.labeltitle'/></label>
                            <span class="required" ng-class="isValidClass(editWork.title)">*</span>
                            <div class="relative">
                                <input id="work-title" name="familyNames" type="text" class="form-control"  ng-model="editWork.title.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_title'/>" ng-change="serverValidate('works/work/titleValidate.json')" ng-model-onblur/>
                                <span class="orcid-error" ng-show="editWork.title.errors.length > 0">
                                    <div ng-repeat='error in editWork.title.errors' ng-bind-html="error"></div>
                                </span>
                                <div class="add-item-link">
                                    <span ng-hide="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.labelshowtranslatedtitle'/></a></span>
                                    <span ng-show="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-minus-sign"></i> <@orcid.msg 'manual_work_form_contents.labelhidetranslatedtitle'/></a></span>
                                </div>
                            </div>
                        </div>

                        <div id="translatedTitle">
                            <span class="orcid-error" ng-show="editWork.translatedTitle.errors.length > 0">
                                <div ng-repeat='error in editWork.translatedTitle.errors' ng-bind-html="error"></div>
                            </span>
                            <div class="form-group">
                                <label><@orcid.msg 'manual_work_form_contents.labeltranslatedtitle'/></label>
                                <div class="relative">
                                    <input name="translatedTitle" type="text" class="form-control" ng-model="editWork.translatedTitle.content" placeholder="<@orcid.msg 'manual_work_form_contents.add_translated_title'/>" ng-change="serverValidate('works/work/translatedTitleValidate.json')" ng-model-onblur/>                                                     
                                </div>                      
                            </div>

                            <div class="form-group">
                                <label class="relative"><@orcid.msg 'manual_work_form_contents.labeltranslatedtitlelanguage'/></label>
                                <div class="relative">                      
                                    <select id="language" class="form-control" name="language" ng-model="editWork.translatedTitle.languageCode" ng-change="serverValidate('works/work/translatedTitleValidate.json')">          
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
                                <input name="familyNames" type="text" class="form-control"  ng-model="editWork.subtitle.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_subtitle'/>" ng-change="serverValidate('works/work/subtitleValidate.json')" ng-model-onblur/>
                                <span class="orcid-error" ng-show="editWork.subtitle.errors.length > 0">
                                    <div ng-repeat='error in editWork.subtitle.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>{{contentCopy.titleLabel}}</label>
                            <div class="relative">
                                <input name="journalTitle" type="text" class="form-control"  ng-model="editWork.journalTitle.value" placeholder="{{contentCopy.titlePlaceholder}}" ng-change="serverValidate('works/work/journalTitleValidate.json')"    ng-model-onblur/>
                                <span class="orcid-error" ng-show="editWork.journalTitle.errors.length > 0">
                                    <div ng-repeat='error in editWork.journalTitle.errors' ng-bind-html="error"></div>
                                </span>                     
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="relative" for="manualWork.day"><@orcid.msg 'manual_work_form_contents.labelPubDate'/></label>
                            <div class="relative">                      
                                <select id="year" name="month" ng-model="editWork.publicationDate.year">
                                    <#list years?keys as key>
                                        <option value="${key}">${years[key]}</option>
                                    </#list>
                                </select>
                                <select id="month" name="month" ng-model="editWork.publicationDate.month">
                                    <#list months?keys as key>
                                        <option value="${key}">${months[key]}</option>
                                    </#list>
                                </select>
                                <select id="day" name="day" ng-model="editWork.publicationDate.day">
                                    <#list days?keys as key>
                                        <option value="${key}">${days[key]}</option>
                                    </#list>
                                </select>                                               
                            </div>
                            <div class="relative">
                                <span class="orcid-error" ng-show="editWork.publicationDate.errors.length > 0">
                                    <div ng-repeat='error in editWork.publicationDate.errors' ng-bind-html="error"></div>
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
                                <select id="citationType" name="citationType" class="form-control" ng-model="editWork.citation.citationType.value" ng-change="serverValidate('works/work/citationValidate.json')">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.CitationType.empty' /></option>
                                    <#list citationTypes?keys as key>
                                        <option value="${key}">${citationTypes[key]}</option>
                                    </#list>
                                </select> 
                                <span class="orcid-error" ng-show="editWork.citation.citationType.errors.length > 0">
                                    <div ng-repeat='error in editWork.citation.citationType.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label><@orcid.msg 'manual_work_form_contents.labelcitation'/></label>
                            <div class="relative">
                                <textarea name="citation" type="text" class="form-control"  ng-model="editWork.citation.citation.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_citation'/>" ng-change="serverValidate('works/work/citationValidate.json')" ng-model-onblur/>
                                <span class="orcid-error" ng-show="editWork.citation.citation.errors.length > 0">
                                    <div ng-repeat='error in editWork.citation.citation.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>
                    
                        <div class="form-group">
                            <label><@orcid.msg 'manual_work_form_contents.labeldescription'/></label>
                            <div class="relative">
                                <textarea name="discription" type="text" class="form-control"  ng-model="editWork.shortDescription.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_description'/>" ng-change="serverValidate('works/work/descriptionValidate.json')" ng-model-onblur/>
                                <span class="orcid-error" ng-show="editWork.shortDescription.errors.length > 0">
                                    <div ng-repeat='error in editWork.shortDescription.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>
                        
                    </div>

                    <!-- Right column -->
                    <div class="col-md-6 col-sm-6 col-xs-12">               
                        <!-only allow work contributor editing if there is one or more contributors in the record -->
                        <div class="control-group" ng-repeat="contributor in editWork.contributors" ng-show="editWork.contributors.length > 0">
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelRole'/></label>
                            <div class="relative">    
                                <select id="role" name="role" ng-model="contributor.contributorRole.value">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.ContributorRole.empty' /></option>
                                    <#list roles?keys as key>
                                        <option value="${key}">${roles[key]}</option>
                                    </#list>
                                </select>
                                <a href ng-click="deleteContributor(contributor)" class="glyphicon glyphicon-trash grey"></a>
                                <span class="orcid-error" ng-show="contributor.contributorRole.errors.length > 0">
                                        <div ng-repeat='error in contributor.contributorRole.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelcredited'/></label>
                            <div class="relative">    
                                <select id="sequence" name="sequence" ng-model="contributor.contributorSequence.value">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.SequenceType.empty'/></option>
                                    <#list sequences?keys as key>
                                        <option value="${key}">${sequences[key]}</option>
                                    </#list>
                                </select>
                                <span class="orcid-error" ng-show="contributor.contributorSequence.errors.length > 0">
                                        <div ng-repeat='error in contributor.contributorSequence.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>                                              
                    </div>
                    
                    <div class="col-md-6 col-sm-6 col-xs-12">                               
                        <div class="control-group">
                            <span><strong><@orcid.msg 'manual_work_form_contents.titleexternalidentifier'/></strong></span>
                        </div>
                        
                        <div ng-repeat="workExternalIdentifier in editWork.workExternalIdentifiers"> 
                            <div class="form-group">
                                <label class="relative"><@orcid.msg 'manual_work_form_contents.labelIDtype'/></label>
                                <div class="relative">
                                    
                                    <input id="worksIdType{{$index}}" class="form-control" type="text" 
                                    ng-model="workExternalIdentifier.workExternalIdentifierType.value" 
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
                                    
                                    <span class="orcid-error" ng-show="workExternalIdentifier.workExternalIdentifierType.errors.length > 0">
                                        <div ng-repeat='error in workExternalIdentifier.workExternalIdentifierType.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>
                            </div>
                            <div class="form-group">
                                <label><@orcid.msg 'manual_work_form_contents.labelID'/></label>
                                <div class="relative">
                                    <input id="worksIdValue{{$index}}" name="currentWorkExternalIds" type="text" class="form-control action-icon-inside"  ng-model="workExternalIdentifier.workExternalIdentifierId.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_ID'/>"  ng-change="fillUrl(workExternalIdentifier)" ng-model-onblur/>
                                    <span class="orcid-error" ng-show="workExternalIdentifier.workExternalIdentifierId.errors.length > 0">
                                        <div ng-repeat='error in workExternalIdentifier.workExternalIdentifierId.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>                      
                            </div>      
                            <div class="form-group">
                                <label><@orcid.msg 'manual_work_form_contents.identifierurl'/></label>
                                <div class="relative">
                                    <input id="worksIdUrl{{$index}}"name="currentWorkExternalIdUrl" type="text" class="form-control action-icon-inside"  ng-model="workExternalIdentifier.url.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL'/>" ng-model-onblur/>
                                    <span class="orcid-error" ng-show="workExternalIdentifier.url.errors.length > 0">
                                        <div ng-repeat='error in workExternalIdentifier.url.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>
                            </div>                  
                            <div class="bottomBuffer">
                                <label><@orcid.msg 'common.ext_id.relationship'/>
                                    <div class="popover-help-container">
                                        <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
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
                                        <input type="radio" name="relationship{{$index}}" ng-model="workExternalIdentifier.relationship.value" value="self">
                                        <@orcid.msg "common.self" />
                                    </label>
                                                                                            
                                    <label class="checkbox-inline">
                                        <input type="radio" name="relationship{{$index}}" ng-model="workExternalIdentifier.relationship.value" value="part-of">
                                        <@orcid.msg "common.part_of" />
                                    </label>                            
                                    <a href ng-click="deleteExternalIdentifier(workExternalIdentifier)" class="glyphicon glyphicon-trash grey action-icon-align-right" ng-hide="$first && editWork.workExternalIdentifiers.length == 1"></a>                            
                                </div>
                                <div ng-show="$last" class="add-item-link">         
                                    <span><a href ng-click="addExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.add_external_identifier' /></a></span>
                                </div>
                            </div>
                        </div>
                        <div ng-show="editWork.workExternalIdentifiers == null || editWork.workExternalIdentifiers.length == 0">
                            <div class="add-item-link">
                                <span><a href ng-click="addExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.add_external_identifier' /></a></span>
                            </div>
                        </div>
                    
                        <div class="form-group">
                            <label class="relative"><@orcid.msg 'common.url'/></label>
                            <div class="relative">
                                <input name="url" type="text" class="form-control"  ng-model="editWork.url.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL'/>" ng-change="serverValidate('works/work/urlValidate.json')" ng-model-onblur/>
                                <span class="orcid-error" ng-show="editWork.url.errors.length > 0">
                                    <div ng-repeat='error in editWork.url.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelformlanguage'/></label>
                            <div class="relative">  
                                <select id="language" class="form-control" name="language" ng-model="editWork.languageCode.value">
                                    <option value="${currentLocaleKey}">${currentLocaleValue}</option>
                                    <#list languages?keys as key>
                                        <option value="${languages[key]}">${key}</option>
                                    </#list>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="country"><@orcid.msg 'manual_work_form_contents.labelcountry'/></label>
                            <div class="relative">
                                <select id="isoCountryCode" class="form-control" name="isoCountryCode" ng-model="editWork.countryCode.value">
                                    <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
                                    <#list isoCountries?keys as key>
                                        <option value="${key}">${isoCountries[key]}</option>                                
                                    </#list>
                                </select>
                            </div>
                        </div>
                        
                        

                        <div class="control-group">

                            <div ng-show="editWork.putCode.value != null">
                                <ul class="inline-list margin-separator pull-left">
                                    <li>
                                        <button class="btn btn-primary" ng-click="putWork()" ng-disabled="addingWork" ng-class="{disabled:addingWork}">
                                            <@orcid.msg 'freemarker.btnsave'/>
                                        </button>
                                    </li>
                                    <li>                                
                                        <a class="cancel-option" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
                                    </li>
                                    <li>
                                        <span ng-show="addingWork">
                                            <i class="glyphicon glyphicon-refresh spin x2 green"></i>
                                        </span>
                                    </li>
                                </ul>
                            </div>

                            <div ng-show="editWork.putCode.value == null">
                                <ul class="inline-list margin-separator pull-left">
                                    <li>
                                        <button class="btn btn-primary" id='save-new-work' ng-click="putWork()" ng-disabled="addingWork" ng-class="{disabled:addingWork}">
                                            <@orcid.msg 'manual_work_form_contents.btnaddtolist'/>
                                        </button>
                                    </li>
                                    <li>                                
                                        <a class="cancel-option" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
                                    </li>
                                    <li>
                                        <span ng-show="addingWork">
                                            <i class="glyphicon glyphicon-refresh spin x2 green"></i>
                                        </span>
                                    </li>
                                </ul>
                            </div>

                            <div class="control-group errors">                  
                                <span ng-show="editWork.errors.length > 0" class="alert"><@orcid.msg 'common.please_fix_errors' /></span>
                            </div>
                                            
                        </div>
                    </div>
                </div>
            </div>    
        </fn-form>
    </div>      
</script>