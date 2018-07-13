<script type="text/ng-template" id="add-peer-review-modal"> 
    <div class="add-peer-review colorbox-content">
        <fn-form update-fn="">
                <div class="lightbox-container-ie7">        
                <!-- Title -->
                <div class="row">           
                    <div class="col-md-9 col-sm-8 col-xs-9">    
                        <h1 class="lightbox-title pull-left">                       
                            <div ng-show="editPeerReview.putCode.value != ''" ng-cloak>
                                <@orcid.msg 'peer_review.edit'/>
                            </div>                       
                            <div ng-show="editPeerReview.putCode.value == ''" ng-cloak>
                                <@orcid.msg 'peer_review.add'/>
                            </div>
                        </h1>
                    </div>          
                </div>
        
                <!-- Main content -->       
                <div class="row">
                    <!-- Left Column -->            
                    <div class="col-md-6 col-sm-6 col-xs-12">   
                        <!-- ROLE -->
                        <div class="control-group">
                            <label class="relative"><@orcid.msg 'peer_review.role'/></label><span class="required text-error" ng-class="isValidClass(editPeerReview.role)">*</span>                     
                            <div class="relative">
                                <select id="peerReviewRole" class="form-control" name="peerReviewRole" ng-model="editPeerReview.role.value" ng-change="serverValidate('peer-reviews/roleValidate.json');">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.record.Role.empty' /></option>
                                    <#list peerReviewRoles?keys as key>
                                        <option value="${key}">${peerReviewRoles[key]}</option>
                                    </#list>
                                </select>
                                <span class="orcid-error" ng-show="editPeerReview.role.errors.length > 0">
                                    <div ng-repeat='error in editPeerReview.role.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>
                        <!-- TYPE -->
                        <div class="control-group">
                            <label class="relative"><@orcid.msg 'peer_review.type'/></label><span class="required text-error" ng-class="isValidClass(editPeerReview.type)">*</span>                     
                            <div class="relative">
                                <select id="peerReviewType" class="form-control" name="peerReviewType" ng-model="editPeerReview.type.value" ng-change="serverValidate('peer-reviews/typeValidate.json');">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.record.PeerReviewType.empty' /></option>
                                    <#list peerReviewTypes?keys as key>
                                        <option value="${key}">${peerReviewTypes[key]}</option>
                                    </#list>
                                </select>
                                <span class="orcid-error" ng-show="editPeerReview.type.errors.length > 0">
                                    <div ng-repeat='error in editPeerReview.type.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>
    
                        <!-- ORGANIZATION -->
                        <div class="control-group">
                            <div class="control-group no-margin-bottom">
                                <strong><@orcid.msg 'peer_review.institution'/></strong>
                            </div>
                            <div class="control-group" ng-show="editPeerReview.disambiguatedOrganizationSourceId">
                                <label>Institution</label>
                                <span id="remove-disambiguated" class="pull-right">
                                    <a ng-click="removeDisambiguatedOrganization()">
                                        <span class="glyphicon glyphicon-remove-sign"></span><@orcid.msg 'common.remove'/>
                                    </a>
                                </span>
                                <div class="relative" style="font-weight: strong;">
                                    <span ng-bind="disambiguatedOrganization.value"></span>
                                </div>
                            </div>
                            <div class="control-group">
                                <span ng-hide="disambiguatedOrganization"><label><@orcid.msg 'peer_review.institution'/></label></span><span ng-show="disambiguatedOrganization"><label><@orcid.msg 'peer_review.institution.display_institution'/></label></span><span class="required text-error" ng-class="isValidClass(editPeerReview.orgName)">*</span>
                                <div class="relative">
                                    <input id="organizationName" class="form-control" name="organizationName" type="text" ng-model="editPeerReview.orgName.value" placeholder="Type name. Select from the list to fill other fields" ng-change="serverValidate('peer-reviews/orgNameValidate.json')" ng-model-onblur/>
                                    <span class="orcid-error" ng-show="editPeerReview.orgName.errors.length > 0">
                                        <div ng-repeat='error in editPeerReview.orgName.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>
                            </div>
                            <div class="control-group">
                                <label ng-hide="disambiguatedOrganization"><@orcid.msg 'peer_review.city'/></label><label ng-show="disambiguatedOrganization"><@orcid.msg 'peer_review.display_city'/></label><span class="required text-error" ng-class="isValidClass(editPeerReview.city)">*</span>
                                <div class="relative">
                                    <input name="city" type="text" class="form-control"  ng-model="editPeerReview.city.value" placeholder="Add city" ng-change="serverValidate('peer-reviews/cityValidate.json')" ng-model-onblur/>
                                    <span class="orcid-error" ng-show="editPeerReview.city.errors.length > 0">
                                        <div ng-repeat='error in editPeerReview.city.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>
                            </div>
                            <div class="control-group">
                                <label ng-hide="disambiguatedOrganization"><@orcid.msg 'peer_review.region'/></label>
                                <label ng-show="disambiguatedOrganization"><@orcid.msg 'peer_review.display'/></label>
                                <div class="relative">
                                    <input name="region" type="text" class="form-control"  ng-model="editPeerReview.region.value" placeholder="Add region" ng-change="serverValidate('peer-reviews/regionValidate.json')" ng-model-onblur/>
                                    <span class="orcid-error" ng-show="editPeerReview.region.errors.length > 0">
                                        <div ng-repeat='error in editPeerReview.region.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>
                            </div>
                            <div class="control-group">
                                <label ng-hide="disambiguatedOrganization"><@orcid.msg 'peer_review.country'/></label><label ng-show="disambiguatedOrganization"><@orcid.msg 'peer_review.display_country'/></label><span class="required text-error" ng-class="isValidClass(editPeerReview.country)">*</span>
                                <div class="relative">
                                    <select id="country" class="form-control" name="country" ng-model="editPeerReview.country.value" ng-change="serverValidate('peer-reviews/countryValidate.json')">
                                        <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
                                        <#list isoCountries?keys as key>
                                            <option value="${key}">${isoCountries[key]}</option>
                                        </#list>
                                    </select>
                                    <span class="orcid-error" ng-show="editPeerReview.country.errors.length > 0">
                                        <div ng-repeat='error in editPeerReview.country.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <!-- DATE -->               
                        <span><strong><@orcid.msg 'peer_review.completion_date'/></strong></span><span class="required text-error">*</span> 
                        <div class="control-group">                     
                            <div class="relative">                      
                                <select id="year" class="col-md-3 col-sm-3 col-xs-3 inline-input" name="year" ng-model="editPeerReview.completionDate.year">
                                    <#list years?keys as key>
                                        <option value="${key}">${years[key]}</option>
                                    </#list>
                                </select>                       
                                <select id="month" class="col-md-4 col-sm-4 col-xs-4 inline-input" name="month" ng-model="editPeerReview.completionDate.month">
                                    <#list months?keys as key>
                                        <option value="${key}">${months[key]}</option>
                                    </#list>
                                </select>
                                <select id="day" class="col-md-3 col-sm-3 col-xs-3 inline-input" name="day" ng-model="editPeerReview.completionDate.day">
                                    <#list days?keys as key>
                                        <option value="${key}">${days[key]}</option>
                                    </#list>
                                </select>                                   
                            </div>
                        </div>
                        <div class="divisor"></div>
                        <!-- External identifiers -->
                        <span><strong><@orcid.msg 'peer_review.external_identifiers'/></strong></span>
                        <div ng-repeat="extId in editPeerReview.externalIdentifiers"> 
                            <div class="control-group">
                                <label class="relative"><@orcid.msg 'peer_review.identifier_type' /></label>
                                <div class="relative">
                                    <select id="extIdType" class="form-control" name="extIdType" ng-model="extId.externalIdentifierType.value" ng-change="fillUrl(extId)">                                                                                   
                                        <option value=""><@orcid.msg 'org.orcid.jaxb.model.record.WorkExternalIdentifierType.empty' /></option>
                                        <#list idTypes?keys as key>
                                            <option value="${idTypes[key]}">${key}</option>
                                        </#list>
                                    </select>                                   
                                    <span class="orcid-error" ng-show="extId.externalIdentifierType.errors.length > 0">
                                        <div ng-repeat='error in extId.externalIdentifierType.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>  
                            </div>                                                          
                            <div class="control-group">
                                <label class="relative"><@orcid.msg 'peer_review.identifier_value'/></label>
                                <div class="relative">
                                    <input id="extIdValue" name="extIdValue" type="text" class="form-control"  ng-model="extId.externalIdentifierId.value" ng-change="fillUrl(extId)"/>
                                    <span class="orcid-error" ng-show="workExternalIdentifier.externalIdentifierId.errors.length > 0">
                                        <div ng-repeat='error in workExternalIdentifier.externalIdentifierId.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>
                            </div>
                            <!-- Ext id url -->                                                                                                                         
                            <div class="control-group">
                                <label><@orcid.msg 'manual_work_form_contents.identifierurl'/></label>
                                <div class="relative">
                                    <input name="externalIdUrl" type="text" class="form-control"  ng-model="extId.url.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL'/>" ng-model-onblur/>
                                    <span class="orcid-error" ng-show="extId.url.errors.length > 0">
                                        <div ng-repeat='error in extId.url.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>
                            </div>
                            <!-- Ext id relationship -->
                            <div class="bottomBuffer">
                                <label><@orcid.msg 'common.ext_id.relationship'/></label>
                                <div class="relative">                          
                                    <label class="checkbox-inline">
                                        <input type="radio" name="relationship{{$index}}" ng-model="extId.relationship.value" value="self">
                                        <@orcid.msg "common.self" />
                                    </label>
                                                                                            
                                    <label class="checkbox-inline">
                                        <input type="radio" name="relationship{{$index}}" ng-model="extId.relationship.value" value="part-of">
                                        <@orcid.msg "common.part_of" />
                                    </label>                            
                                    <a href ng-click="deleteExternalIdentifier(extId)" class="glyphicon glyphicon-trash grey"  ng-hide="$first && editPeerReview.externalIdentifiers.length == 1"></a>                          
                                </div>
                                <div ng-show="$last" class="add-item-link">         
                                    <span><a href ng-click="addExternalIdentifier()" ng-show="$last"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'peer_review.add_external_identifiers'/></a></span>
                                </div>
                            </div>
                        </div>
                    </div>                  
                    <!-- Right column -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <!-- URL -->    
                        <div class="control-group">
                            <div class="relative">
                            <label class="relative"><@orcid.msg 'peer_review.url'/></label><br/>
                                <input id="url" class="form-control" name="url" type="text" ng-model="editPeerReview.url.value" placeholder="Type url." ng-change="serverValidate('peer-reviews/urlValidate.json')" ng-model-onblur/>                           
                                <span class="orcid-error" ng-show="editPeerReview.url.errors.length > 0">
                                    <div ng-repeat='error in editPeerReview.url.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>                                              
                        <!-- Subject External ids -->
                        <span><strong><@orcid.msg 'peer_reveiw.subject'/></strong></span>
                        <span><@orcid.msg 'peer_reveiw.subject.external_identifiers'/></span>
                        <div> 
                            <!-- Ext id type-->
                            <div class="control-group">
                                <label class="relative"><@orcid.msg 'peer_review.subject.identifier_type'/></label>
                                <div class="relative">
                                    <select id="extIdType" class="form-control" name="extIdType" ng-model="editPeerReview.subjectExternalIdentifier.externalIdentifierType.value" ng-change="fillUrl(extId)">                                                                                    
                                        <option value=""><@orcid.msg 'org.orcid.jaxb.model.record.WorkExternalIdentifierType.empty' /></option>
                                        <#list idTypes?keys as key>
                                            <option value="${idTypes[key]}">${key}</option>
                                        </#list>
                                    </select>                                   
                                    <span class="orcid-error" ng-show="editPeerReview.subjectExternalIdentifier.externalIdentifierType.errors.length > 0">
                                        <div ng-repeat='error in editPeerReview.subjectExternalIdentifier.externalIdentifierType.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>  
                            </div>                          
                            <!-- Ext id value-->
                            <div class="control-group">
                                <label class="relative"><@orcid.msg 'peer_review.subject.identifier_value'/></label>
                                <div class="relative">
                                    <input id="extIdValue" name="extIdValue" type="text" class="form-control"  ng-model="editPeerReview.subjectExternalIdentifier.externalIdentifierId.value" ng-change="fillUrl(extId)"/>
                                    <span class="orcid-error" ng-show="editPeerReview.subjectExternalIdentifier.externalIdentifierId.errors.length > 0">
                                        <div ng-repeat='error in editPeerReview.subjectExternalIdentifier.externalIdentifierId.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>                              
                            </div>
                            <!-- Ext id url -->
                            <div class="control-group">
                                <label><@orcid.msg 'manual_work_form_contents.identifierurl'/></label>
                                <div class="relative">
                                    <input name="externalIdUrl" type="text" class="form-control"  ng-model="editPeerReview.subjectExternalIdentifier.url.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL'/>" ng-model-onblur/>
                                    <span class="orcid-error" ng-show="editPeerReview.subjectExternalIdentifier.url.errors.length > 0">
                                        <div ng-repeat='error in editPeerReview.subjectExternalIdentifier.url.errors' ng-bind-html="error"></div>
                                    </span>
                                </div>
                            </div>  
                            <!-- Ext id relationship -->
                            <div class="bottomBuffer">
                                <label><@orcid.msg 'common.ext_id.relationship'/></label>
                                <div class="relative">                          
                                    <label class="checkbox-inline">
                                        <input type="radio" name="subject_relationship{{$index}}" ng-model="editPeerReview.subjectExternalIdentifier.relationship.value" value="self">
                                        <@orcid.msg "common.self" />
                                    </label>
                                                                                            
                                    <label class="checkbox-inline">
                                        <input type="radio" name="subject_relationship{{$index}}" ng-model="editPeerReview.subjectExternalIdentifier.relationship.value" value="part-of">
                                        <@orcid.msg "common.part_of" />
                                    </label>                                                    
                                </div>                              
                            </div>
                        </div>                                       
                        <!-- Subject Type -->
                        <div class="control-group">
                            <label class="relative"><@orcid.msg 'manual_work_form_contents.labelworktype'/></label><span class="required text-error" ng-class="isValidClass(editPeerReview.subjectType)">*</span>
                            <div class="relative">
                                <select id="peerReviewSubjectType" class="form-control" name="peerReviewSubjectType" ng-model="editPeerReview.subjectType.value" ng-change="serverValidate('peer-reviews/subject/typeValidate.json');">
                                    <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.WorkType.empty' /></option>
                                    <#list workTypes?keys as key>
                                        <option value="${key}">${workTypes[key]}</option>
                                    </#list>
                                </select>
                                <span class="orcid-error" ng-show="editPeerReview.subjectType.errors.length > 0">
                                    <div ng-repeat='error in editPeerReview.subjectType.errors' ng-bind-html="error"></div>
                                </span>
                            </div>
                        </div>                      
                        <!-- Subject Name -->
                        <div class="control-group">
                           <label><@orcid.msg 'manual_work_form_contents.labeltitle'/></label><span class="required text-error" ng-class="isValidClass(editPeerReview.subjectName)">*</span>
                           <div class="relative">
                              <input name="title" type="text" class="form-control"  ng-model="editPeerReview.subjectName.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_title'/>" ng-change="serverValidate('peer-reviews/subject/subjectNameValidate.json')" ng-model-onblur/>
                              <span class="orcid-error" ng-show="editPeerReview.subjectName.errors.length > 0">
                                 <div ng-repeat='error in editPeerReview.subjectName.errors' ng-bind-html="error"></div>
                              </span>
                              <div class="add-item-link">
                                 <span ng-hide="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.labelshowtranslatedtitle'/></a></span>
                                 <span ng-show="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-minus-sign"></i> <@orcid.msg 'manual_work_form_contents.labelhidetranslatedtitle'/></a></span>
                              </div>
                           </div>
                        </div>
                        <div id="translatedTitle">
                           
                           <div class="control-group">
                              <label><@orcid.msg 'manual_work_form_contents.labeltranslatedtitle'/></label>
                              <div class="relative">
                                 <input name="translatedTitle" type="text" class="form-control" ng-model="editPeerReview.translatedSubjectName.content" placeholder="<@orcid.msg 'manual_work_form_contents.add_translated_title'/>" ng-model-onblur/>
                              </div>
                              <span class="orcid-error" ng-show="editPeerReview.translatedSubjectName.errors.length > 0">
                              <div ng-repeat='error in editPeerReview.translatedSubjectName.errors' ng-bind-html="error"></div>
                           </span>
                           </div>
                           <div class="control-group">
                              <label class="relative"><@orcid.msg 'manual_work_form_contents.labeltranslatedtitlelanguage'/></label>
                              <div class="relative">
                                 <select id="language" name="language" ng-model="editPeerReview.translatedSubjectName.languageCode">
                                    <#list languages?keys as key>
                                    <option value="${languages[key]}">${key}</option>
                                    </#list>
                                 </select>
                              </div>
                           </div>

                        </div>
                        <div class="control-group">
                            <label><@orcid.msg 'manual_peer_review_form_contents.subjectContainerName'/></label>
                            <div class="relative">
                                <input name="journalTitle" type="text" class="form-control"  ng-model="editPeerReview.subjectContainerName.value" placeholder="<@orcid.msg 'manual_peer_review_form_contents.addSubjectContainerName'/>" ng-model-onblur/>
                                <span class="orcid-error" ng-show="editPeerReview.subjectContainerName.errors.length > 0">
                                    <div ng-repeat='error in editPeerReview.subjectContainerName.errors' ng-bind-html="error"></div>
                                </span>                     
                            </div>
                        </div>  
                        <!-- Subject URL -->    
                        <div class="control-group">
                            <label class="relative"><@orcid.msg 'peer_review.subject.url'/></label><br/>
                            <div class="relative">
                                <input name="subjectUrl" type="text" class="form-control"  ng-model="editPeerReview.subjectUrl.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_URL' />" ng-change="serverValidate('peer-reviews/subject/urlValidate.json')" ng-model-onblur/>
                                <span class="orcid-error" ng-show="editPeerReview.subjectUrl.errors.length > 0">
                                    <div ng-repeat='error in editPeerReview.subjectUrl.errors' ng-bind-html="error"></div>
                                </span>                         
                            </div>
                        </div>
                        <!-- Group Id -->
                        <div class="control-group">
                            <label class="relative"><@orcid.msg 'manual_peer_review_form_contents.groupId'/></label><br/>
                            <div class="relative">
                                <input name="groupId" type="text" class="form-control"  ng-model="editPeerReview.groupId.value" placeholder="<@orcid.msg 'manual_peer_review_form_contents.addGroupId'/>" ng-model-onblur/>
                                <span class="orcid-error" ng-show="editPeerReview.groupId.errors.length > 0">
                                    <div ng-repeat='error in editPeerReview.groupId.errors' ng-bind-html="error"></div>
                                </span>                         
                            </div>
                        </div>
                        <div class="control-group">
                            <button class="btn btn-primary" ng-click="addAPeerReview()" ng-disabled="addingPeerReview" ng-class="{disabled:addingPeerReview}">
                                <span ng-show="editPeerReview.putCode.value == ''" class=""><@orcid.msg 'peer_review.add_to_list'/></span>
                                <span ng-show="editPeerReview.putCode.value != ''" class=""><@orcid.msg 'peer_review.save_changes'/></span>                             
                            </button>
                            <button id="" class="btn close-button" ng-click="closeModal()" type="reset"><@orcid.msg 'peer_review.cancel'/></button>
                        </div>
                    </div>
                </div>
            </fn-form>          
        </div>      
    </div>
 </script>