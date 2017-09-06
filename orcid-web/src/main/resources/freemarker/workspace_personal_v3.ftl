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
<#escape x as x?html>
    <script type="text/ng-template" id="biography-ng2-template">
        <div class="biography-controller" id="bio-section">
            <div class="row">
                <div class="col-md-9 col-sm-8 col-xs-4">
                    <h3 (click)="toggleEdit()" class="workspace-title">${springMacroRequestContext.getMessage("manage_bio_settings.labelbiography")}</h3>      
                </div>
                <div class="col-md-3 col-sm-4 col-xs-8">
                    <ul class="inline-list bio-edit right">
                        <li>
                            <div (click)="toggleEdit()" [hidden]="showEdit" class="edit-biography edit-option">
                                <span class="glyphicon glyphicon-pencil"></span>
                                <div class="popover popover-tooltip top">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span><@orcid.msg 'manage_bio_settings.editBio' /></span>
                                    </div>                
                                </div>
                            </div>
                        </li>
                        <li>
                            <@orcid.privacyToggle2 angularModel="biographyForm.visiblity.visibility"
                                questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
                                clickedClassCheck="{'popover-help-container-show':privacyHelp[privacyHelp==true}" 
                                publicClick="setPrivacy('PUBLIC', $event)" 
                                limitedClick="setPrivacy('LIMITED', $event)" 
                                privateClick="setPrivacy('PRIVATE', $event)" />
                        </li>
                    </ul>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-12">   
                    <div style="white-space: pre-wrap" [hidden]="showEdit" (click)="toggleEdit()">{{biographyForm?.biography?.value}}</div> 
                </div>
            </div>
            
            <div [hidden]="!showEdit" class="biography-edit">
                <div class="row">
                    <div class="col-md-12 col-xs-12 col-sm-12">
                        <textarea id="biography" name="biography" class="input-xlarge" rows="20" (change)="checkLength()" (keypress)="checkLength()" [(ngModel)]="biographyForm.biography.value"></textarea>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <span class="orcid-error" *ngIf="lengthError==true">
                            <div>${springMacroRequestContext.getMessage("Length.changePersonalInfoForm.biography")}</div>
                        </span>
                        <span class="orcid-error" [hidden]="biographyForm?.biography?.errors?.length == 0">
                            <div *ngFor='let error of biographyForm?.biography?.errors'>{{error}}</div>
                        </span>
                    </div>
                </div>
                <div class="row">                                   
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <div class="pull-right full-width">
                            <a class="cancel" (click)="cancel()"><@spring.message "freemarker.btncancel"/></a>
                            <button class="btn btn-primary" (click)="setBiographyForm()"><@spring.message "freemarker.btnsavechanges"/></button>
                        </div>
                    </div>
                </div>                                                          
            </div>
        </div>
        
    </script>

    <biography-ng2></biography-ng2>
    <!--
    <div class="biography-controller" ng-controller="BiographyCtrl" id="bio-section">
        <div class="row">
            <div class="col-md-9 col-sm-8 col-xs-4">
                <h3 ng-click="toggleEdit()" class="workspace-title">${springMacroRequestContext.getMessage("manage_bio_settings.labelbiography")}</h3>      
            </div>
            <div class="col-md-3 col-sm-4 col-xs-8">
                <ul class="inline-list bio-edit right">
                    <li>
                        <div ng-click="toggleEdit()" ng-hide="showEdit == true" class="edit-biography edit-option">
                            <span class="glyphicon glyphicon-pencil"></span>
                            <div class="popover popover-tooltip top">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <span><@orcid.msg 'manage_bio_settings.editBio' /></span>
                                </div>                
                            </div>
                        </div>
                    </li>
                    <li ng-cloak>
                        <@orcid.privacyToggle2 angularModel="biographyForm.visiblity.visibility"
                            questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
                            clickedClassCheck="{'popover-help-container-show':privacyHelp[privacyHelp==true}" 
                            publicClick="setPrivacy('PUBLIC', $event)" 
                            limitedClick="setPrivacy('LIMITED', $event)" 
                            privateClick="setPrivacy('PRIVATE', $event)" />
                    </li>
                </ul>
            </div>
        </div>
        
        <div class="row">
            <div class="col-md-12">
                <div style="white-space: pre-wrap" ng-hide="showEdit == true" ng-bind="biographyForm.biography.value" ng-click="toggleEdit()"></div>                    
            </div>
        </div>
        
        <div ng-hide="showEdit == false"  class="biography-edit" ng-cloak>
            <div class="row">
                <div class="col-md-12 col-xs-12 col-sm-12">
                    <textarea id="biography" name="biography" class="input-xlarge" rows="20" ng-model="biographyForm.biography.value" ng-change="checkLength()">
                    </textarea>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <span class="orcid-error" ng-show="lengthError">
                        <div>${springMacroRequestContext.getMessage("Length.changePersonalInfoForm.biography")}</div>
                    </span>
                    <span class="orcid-error" ng-show="biographyForm.biography.errors.length > 0">
                        <div ng-repeat='error in biographyForm.biography.errors' ng-bind-html="error"></div>
                    </span>
                </div>
            </div>
            <div class="row">                                   
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="pull-right full-width">
                        <a class="cancel" ng-click="cancel()"><@spring.message "freemarker.btncancel"/></a>
                        <button class="btn btn-primary" ng-click="setBiographyForm()"><@spring.message "freemarker.btnsavechanges"/></button>
                    </div>
                </div>
            </div>                                                          
        </div>
    </div>
    -->
</#escape>