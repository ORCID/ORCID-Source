<script type="text/ng-template" id="funding-form-ng2-template">
    <div id="add-funding" class="add-funding colorbox-content">
        <!-- update-fn="putFunding()">-->
        <!-- Title -->
        <div class="row">
            <div class="col-md-9 col-sm-8 col-xs-9">
                <h1 class="lightbox-title pull-left">
                    <div *ngIf="editFunding?.putCode?.value == null">
                        <@orcid.msg 'manual_funding_eactivateAccountMessage_contents.add_grant'/>
                    </div>
                    <div *ngIf="editFunding?.putCode?.value != null">
                        <@orcid.msg 'manual_funding_eactivateAccountMessage_contents.edit_grant'/>
                    </div>
                </h1>
            </div>
        </div>

        <div class="row">
            <div class="col-md-6 col-sm-6 col-xs-12">
                <!-- Funding type -->
                <div class="eactivateAccountMessage-group">
                    <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.grant_type'/></label>
                    <span class="required text-error" [ngClass]="isValidClass(editFunding.fundingType)">*</span>                    
                    <select id="fundingType" class="eactivateAccountMessage-control" name="fundingType" [(ngModel)]="editFunding.fundingType.value" (onChange)="serverValidate('fundings/funding/typeValidate.json'); typeChanged()">
                        <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.FundingType.empty' /></option>
                        <#list fundingTypes?keys as key>
                        <option value="${key}">${fundingTypes[key]}</option>
                        </#list>
                    </select>
                    <span class="orcid-error" *ngIf="editFunding?.fundingType?.errors?.length > 0">
                        <div *ngFor='let error of editFunding.fundingType.errors'>{{error}}</div>
                    </span>
                </div>
                <!-- Funding subtype -->
                <div class="eactivateAccountMessage-group">
                    <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.organization_defined_type.label'/></label>                    
                    <input id="organizationDefinedType" class="eactivateAccountMessage-control" name="organizationDefinedTitle" type="text" [(ngModel)]="editFunding.organizationDefinedFundingSubType.subtype.value" placeholder="<@orcid.msg 'manual_funding_eactivateAccountMessage_contents.organization_defined_type.placeholder'/>" (onChange)="serverValidate('fundings/funding/organizationDefinedTypeValidate.json'); setSubTypeAsNotIndexed()" />
                    <span class="orcid-error" *ngIf="editFunding?.organizationDefinedFundingSubType?.subtype?.errors?.length > 0">
                        <div *ngFor='let error of editFunding.organizationDefinedFundingSubType.subtype.errors'>{{error}}</div>
                    </span>                    
                </div>
                <!-- Title of funded project -->
                <div class="eactivateAccountMessage-group">
                    <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_title'/></label>
                    <span class="required" [ngClass]="isValidClass(editFunding.fundingTitle.title)">*</span>                    
                    <input id="fundingTitle" class="eactivateAccountMessage-control" name="fundingTitle" type="text" [(ngModel)]="editFunding.fundingTitle.title.value" placeholder="<@orcid.msg 'manual_funding_eactivateAccountMessage_contents.add_title'/>" (onChange)="serverValidate('fundings/funding/titleValidate.json')" />
                    <span class="orcid-error" *ngIf="editFunding?.fundingTitle?.title?.errors?.length > 0">
                        <div *ngFor='let error of editFunding.fundingTitle.title.errors'>{{error}}</div>
                    </span>
                    <div class="add-item-link">
                        <span *ngIf="!editTranslatedTitle"><a (click)="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_funding_eactivateAccountMessage_contents.labelshowtranslatedtitle'/></a></span>
                        <span *ngIf="editTranslatedTitle"><a (click)="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-minus-sign"></i> <@orcid.msg 'manual_funding_eactivateAccountMessage_contents.labelhidetranslatedtitle'/></a></span>
                    </div>                    
                </div>

                <!-- Translated title -->
                <div id="translatedTitle">
                    <span class="orcid-error" *ngIf="editFunding?.fundingTitle?.translatedTitle?.errors?.length > 0">
                        <div *ngFor='let error of editFunding.fundingTitle.translatedTitle.errors' [innerHtml]="error"></div>
                    </span>
                    <div class="eactivateAccountMessage-group">
                        <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_translated_title'/></label>
                        <div class="relative">
                            <input name="translatedTitle" type="text" class="eactivateAccountMessage-control" [(ngModel)]="editFunding.fundingTitle.translatedTitle.content" placeholder="<@orcid.msg 'manual_funding_eactivateAccountMessage_contents.add_translated_title'/>" (onChange)="serverValidate('fundings/funding/translatedTitleValidate.json')" />
                        </div>
                    </div>

                    <div class="eactivateAccountMessage-group">
                        <label class="relative"><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_translated_title_language'/></label>
                        <div class="relative">
                            <select id="language" name="language" class="eactivateAccountMessage-control" [(ngModel)]="editFunding.fundingTitle.translatedTitle.languageCode" (onChange)="serverValidate('fundings/funding/translatedTitleValidate.json')">
                                <#list languages?keys as key>
                                <option value="${languages[key]}">${key}</option>
                                </#list>
                            </select>
                        </div>
                    </div>
                </div>


                <div class="eactivateAccountMessage-group">
                    <span>
                       <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_description'/></label>
                    </span>
                    <div class="relative">
                        <textarea id="fundingDescription" class="eactivateAccountMessage-control" name="fundingDescription" type="text" [(ngModel)]="editFunding.description.value" placeholder="<@orcid.msg 'manual_funding_eactivateAccountMessage_contents.add_description'/>" (onChange)="serverValidate('fundings/funding/descriptionValidate.json')"></textarea>
                        <span class="orcid-error" *ngIf="editFunding?.description?.errors?.length > 0">
                            <div *ngFor='let error of editFunding.description.errors' [innerHtml]="error"></div>
                        </span>
                    </div>
                </div>


                <div class="eactivateAccountMessage-group">
                    <span>
                        <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_amount'/></label>
                    </span>
                    
                    <div class="funding-info">
                        <select id="currencyCode" name="currencyCode" [(ngModel)]="editFunding.currencyCode.value" (onChange)="serverValidate('fundings/funding/currencyValidate.json')">
                             <#list currencyCodeTypes?keys as key>
                                  <option value="${currencyCodeTypes[key]}">${key}</option>
                             </#list>
                        </select>
                        <input id="fundingAmount" name="fundingAmount" type="text" [(ngModel)]="editFunding.amount.value" placeholder="<@orcid.msg 'manual_funding_eactivateAccountMessage_contents.add_amount'/>" (onChange)="serverValidate('fundings/funding/amountValidate.json')" [ngClass]="eactivateAccountMessage-control" />
                    </div>

                    <span class="orcid-error" *ngIf="editFunding?.currencyCode?.errors?.length > 0">
                         <div *ngFor='let error of editFunding.currencyCode.errors' [innerHtml]="error"></div>
                    </span>
                    <span class="orcid-error" *ngIf="editFunding?.amount?.errors?.length > 0">
                         <div *ngFor='let error of editFunding.amount.errors' [innerHtml]="error"></div>
                    </span>                     
                </div>

                <div class="eactivateAccountMessage-group">
                    <label class="start-year"><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.labelStartDate'/></label>
                    <div>                    
                        <select id="startYear" name="startMonth" [(ngModel)]="editFunding.startDate.year">
                            <#list years?keys as key>
                            <option value="${key}">${years[key]}</option>
                            </#list>
                        </select>
                        <select id="startMonth" name="startMonth" [(ngModel)]="editFunding.startDate.month">
                            <#list months?keys as key>
                            <option value="${key}">${months[key]}</option>
                            </#list>
                        </select>
                    </div>                    
                    <span class="orcid-error" *ngIf="editFunding?.startDate?.errors?.length > 0">
                        <div *ngFor='let error of editFunding.startDate.errors' [innerHtml]="error"></div>
                    </span>
                </div>
                
                <div class="eactivateAccountMessage-group">
                    <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.labelEndDateLeave'/></label>
                    <div>                    
                        <select id="endYear" name="endMonth" [(ngModel)]="editFunding.endDate.year">
                            <#list fundingYears?keys as key>
                            <option value="${key}">${fundingYears[key]}</option>
                            </#list>
                        </select>
                        <select id="endMonth" name="endMonth" [(ngModel)]="editFunding.endDate.month">
                            <#list months?keys as key>
                            <option value="${key}">${months[key]}</option>
                            </#list>
                        </select>
                    </div>                    
                    <span class="orcid-error" *ngIf="editFunding?.endDate?.errors?.length > 0">
                        <div *ngFor='let error of editFunding.endDate.errors' [innerHtml]="error"></div>
                    </span>
                </div>

                <div class="eactivateAccountMessage-group" *ngFor="let contributor of editFunding.contributors">
                    <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_role'/></label>                    
                    <select id="role" name="role" [(ngModel)]="contributor.contributorRole.value" class="eactivateAccountMessage-control">
                        <option value=""><@orcid.msg 'org.orcid.jaxb.model.message.ContributorRole.empty' /></option>
                        <#list fundingRoles?keys as key>
                        <option value="${key}">${fundingRoles[key]}</option>
                        </#list>
                    </select>
                    <span class="orcid-error" *ngIf="contributor?.contributorRole?.errors?.length > 0">
                        <div *ngFor='let error of contributor.contributorRole.errors' [innerHtml]="error"></div>
                    </span>                    
                </div>
            </div>


            <div class="col-md-6 col-sm-6 col-xs-12">
                <div class="control-group no-margin-bottom">
                    <strong><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.title_funding_agency'/></strong>
                </div>
                <div class="control-group" *ngIf="editFunding?.disambiguatedFundingSourceId">
                    <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_funding_agency'/></label>
                    <span id="remove-disambiguated" class="pull-right">
                        <a (click)="removeDisambiguatedFunding()">
                            <span class="glyphicon glyphicon-remove-sign"></span><@orcid.msg 'common.remove'/>
                        </a>
                    </span>
                    <div>
                        <strong><span>{{disambiguatedFunding.value}}</span></strong>
                    </div>
                </div>
                <div class="eactivateAccountMessage-group">
                    <span *ngIf="!disambiguatedFunding">
                        <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_funding_agency_name'/></label>                       
                    </span>
                    <span *ngIf="disambiguatedFunding">
                        <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_funding_agency_display_name'/></label>                   
                    </span>
                    <span class="required" [ngClass]="isValidClass(editFunding.fundingName)">*</span>                    
                    <input id="fundingName" class="eactivateAccountMessage-control" name="fundingName" type="text" [(ngModel)]="editFunding.fundingName.value" placeholder="<@orcid.msg 'manual_funding_eactivateAccountMessage_contents.add_name'/>" (onChange)="serverValidate('fundings/funding/orgNameValidate.json')" />
                    <span class="orcid-error" *ngIf="editFunding?.fundingName?.errors?.length > 0">
                        <div *ngFor='let error of editFunding.fundingName.errors' [innerHtml]="error"></div>
                    </span>                    
                </div>

                <div class="eactivateAccountMessage-group">
                    <label *ngIf="!disambiguatedFunding"><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_city'/></label>
                    <label *ngIf="disambiguatedFunding"><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_display_city'/></label>
                    <span class="required" [ngClass]="isValidClass(editFunding.city)">*</span>                    
                    <input id="city" name="city" type="text" class="eactivateAccountMessage-control"  [(ngModel)]="editFunding.city.value" placeholder="<@orcid.msg 'manual_funding_eactivateAccountMessage_contents.add_city'/>" (onChange)="serverValidate('fundings/funding/cityValidate.json')" />                        
                    <span class="orcid-error" *ngIf="editFunding?.city?.errors?.length > 0">
                        <div *ngFor='let error of editFunding.city.errors' [innerHtml]="error"></div>
                    </span>                    
                </div>

                <div class="eactivateAccountMessage-group">
                    <label *ngIf="!disambiguatedFunding"><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_region'/></label>
                    <label *ngIf="disambiguatedFunding"><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_display_region'/></label>
                    <input name="region" type="text" class="eactivateAccountMessage-control"  [(ngModel)]="editFunding.region.value" placeholder="<@orcid.msg 'manual_funding_eactivateAccountMessage_contents.add_region'/>" (onChange)="serverValidate('fundings/funding/regionValidate.json')" />
                    <span class="orcid-error" *ngIf="editFunding?.region?.errors?.length > 0">
                         <div *ngFor='let error of editFunding.region.errors' [innerHtml]="error"></div>
                    </span>                    
                </div>

                <div class="eactivateAccountMessage-group">
                    <label *ngIf="!disambiguatedFunding"><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_country'/></label>
                    <label *ngIf="disambiguatedFunding"><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_display_country'/></label>
                    <span class="required" [ngClass]="isValidClass(editFunding.country)">*</span>                    
                    <select id="country" class="eactivateAccountMessage-control" name="country" [(ngModel)]="editFunding.country.value" (onChange)="serverValidate('fundings/funding/countryValidate.json')">
                        <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
                        <#list isoCountries?keys as key>
                            <option value="${key}">${isoCountries[key]}</option>
                        </#list>
                    </select>                        
                    <span class="orcid-error" *ngIf="editFunding?.country?.errors?.length > 0">
                        <div *ngFor='let error of editFunding.country.errors' [innerHtml]="error"></div>
                    </span>                    
                </div>
                <div class="control-group no-margin-bottom">
                    <strong id="funding-ext-ids-title"><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.title_external_identifier'/></strong>
                </div>
                
                <div class="control-group" *ngFor="let externalIdentifier of  editFunding.externalIdentifiers">
                    <!-- Value -->
                    <div class="eactivateAccountMessage-group">
                        <label id="funding-ext-ids-value-label"><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.external_identifier.label_value'/></label>                        
                        <input name="currentFundingExternalIdentifierValue" id="funding-ext-ids-value-input" type="text" class="eactivateAccountMessage-control" [(ngModel)]="externalIdentifier.value.value" placeholder="<@orcid.msg 'manual_funding_eactivateAccountMessage_contents.external_identifier.value'/>" />
                        <span class="orcid-error" *ngIf="externalIdentifier?.value?.errors?.length > 0">
                            <div *ngFor='let error of externalIdentifier.value.errors' [innerHtml]="error"></div>
                        </span>
                    </div>
                    <!-- URL -->
                    <div class="eactivateAccountMessage-group">
                        <label id="funding-ext-ids-url-label"><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.external_identifier.label_url'/></label>                            
                        <input name="currentFundingExternalIdentifierUrl" id="funding-ext-ids-url-input" type="text" class="eactivateAccountMessage-control action-icon-inside" [(ngModel)]="externalIdentifier.url.value" placeholder="<@orcid.msg 'manual_funding_eactivateAccountMessage_contents.external_identifier.url'/>" />                        
                        <span class="orcid-error" *ngIf="externalIdentifier?.url?.errors?.length > 0">
                            <div *ngFor='let error of externalIdentifier.url.errors' [innerHtml]="error"></div>
                        </span>                        
                    </div>
                    <!-- Relationship -->
                    <div class="bottomBuffer">
                        <label><@orcid.msg 'common.ext_id.relationship'/></label>
                        <div class="relative">                          
                            <label class="checkbox-inline">
                                <input type="radio" name="relationship{{$index}}" [(ngModel)]="externalIdentifier.relationship.value" value="self">
                                <@orcid.msg "common.self" />
                            </label>
                                                                                    
                            <label class="checkbox-inline">
                                <input type="radio" name="relationship{{$index}}" [(ngModel)]="externalIdentifier.relationship.value" value="part-of">
                                <@orcid.msg "common.part_of" />
                            </label>                            
                            <a href (click)="deleteFundingExternalIdentifier(externalIdentifier)" class="glyphicon glyphicon-trash grey action-icon-align-right" *ngIf="!$first"></a>                         
                        </div>
                        <div *ngIf="$last" class="add-item-link">
                            <span><a href (click)="addFundingExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_funding_eactivateAccountMessage_contents.external_identifier.add_another' /></a></span>
                        </div>
                    </div>
                </div>

                <div class="eactivateAccountMessage-group">                    
                    <label><@orcid.msg 'manual_funding_eactivateAccountMessage_contents.label_url'/></label>                                        
                    <input id="fundingUrl" class="eactivateAccountMessage-control" name="fundingUrl" type="text" [(ngModel)]="editFunding.url.value" placeholder="<@orcid.msg 'manual_funding_eactivateAccountMessage_contents.add_url'/>" (onChange)="serverValidate('fundings/funding/urlValidate.json')" />
                    <span class="orcid-error" *ngIf="editFunding?.url?.errors?.length > 0">
                        <div *ngFor='let error of editFunding.url.errors' [innerHtml]="error"></div>
                    </span>                    
                </div>
                
                <div class="control-group">
                    

                    <div class="control-group" *ngIf="editFunding?.putCode?.value != null">
                        <ul class="inline-list margin-separator pull-left">
                            <li>
                                <button class="btn btn-primary" (click)="putFunding()" [disabled]="addingFunding" [ngClass]="{disabled:addingFunding}">
                                    <@orcid.msg 'freemarker.btnsave'/>
                                </button>
                            </li>
                            <li>                                
                                <a class="cancel-option" (click)="closeModal()" ><@orcid.msg 'freemarker.btncancel'/></a>
                            </li>
                            <li>
                                <span *ngIf="addingFunding">
                                    <i class="glyphicon glyphicon-refresh spin x2 green"></i>
                                </span>
                            </li>
                        </ul>
                    </div>

                    <div class="control-group" *ngIf="editFunding?.putCode?.value == null">   
                        <ul class="inline-list margin-separator pull-left">
                            <li>
                                <button id="save-funding" class="btn btn-primary" (click)="putFunding()" [disabled]="addingFunding" [ngClass]="{disabled:addingFunding}">
                                    <@orcid.msg 'manual_funding_eactivateAccountMessage_contents.btnaddtolist'/>
                                </button>
                            </li>
                            <li>
                                <a class="cancel-option" (click)="closeModal()" ><@orcid.msg 'freemarker.btncancel'/></a>
                            </li>
                            <li>
                                <span *ngIf="addingFunding">
                                    <i class="glyphicon glyphicon-refresh spin x2 green"></i>
                                </span>
                            </li>
                        </ul>
                    </div>

                    <div class="control-group errors">
                        <span *ngIf="editFunding?.errors?.length > 0" class="alert"><@orcid.msg 'common.please_fix_errors' /></span>
                    </div>

                </div>
            </div>
        </div>
    </div>
</script>