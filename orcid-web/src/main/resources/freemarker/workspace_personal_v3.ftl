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
                            <div (click)="toggleEdit()" *ngIf="!showEdit" class="edit-biography edit-option">
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
                            <div class="relative" id="privacy-bar">
                                <ul class="privacyToggle" (mouseenter)="showTooltip('biography-privacy')" (mouseleave)="hideTooltip('biography-privacy')"  ><!-- showTooltip(group.groupId+'-privacy') hideTooltip(group.groupId+'-privacy') -->
                                    <li class="publicActive" [ngClass]="{publicInActive: biographyForm?.visiblity?.visibility != 'PUBLIC'}"><a (click)="setPrivacy('PUBLIC', $event)"></a></li>
                                    <li class="limitedActive limitedInActive" [ngClass]="{limitedInActive: biographyForm?.visiblity?.visibility != 'LIMITED'}"><a (click)="setPrivacy('LIMITED', $event)"></a></li>
                                    <li class="privateActive privateInActive" [ngClass]="{privateInActive: biographyForm?.visiblity?.visibility != 'PRIVATE'}"><a (click)="setPrivacy('PRIVATE', $event)"></a></li>
                                </ul>
                            </div>
                            <div class="popover-help-container">
                                <div class="popover top privacy-myorcid3" [ngClass]="showElement['biography-privacy'] == true ? 'block' : ''">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <strong>Who can see this? </strong>
                                        <ul class="privacyHelp">
                                            <li class="public" style="color: #009900;">everyone</li>
                                            <li class="limited" style="color: #ffb027;">trusted parties</li>
                                            <li class="private" style="color: #990000;">only me</li>
                                        </ul>
                                        <a href="https://support.orcid.org/knowledgebase/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information">More information on privacy settings</a>
                                    </div>                
                                </div>                              
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-12">   
                    <div style="white-space: pre-wrap" *ngIf="!showEdit" (click)="toggleEdit()">{{biographyForm?.biography?.value}}</div> 
                </div>
            </div>
            
            <div *ngIf="showEdit" class="biography-edit">
                <div class="row">
                    <div class="col-md-12 col-xs-12 col-sm-12">
                        <textarea id="biography" name="biography" class="input-xlarge" rows="20" (change)="checkLength()" [(ngModel)]="biographyForm.biography.value"></textarea>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <span class="orcid-error" *ngIf="lengthError==true">
                            <div>${springMacroRequestContext.getMessage("Length.changePersonalInfoForm.biography")}</div>
                        </span>
                        <span class="orcid-error" *ngIf="biographyForm?.biography?.errors?.length > 0">
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

</#escape>