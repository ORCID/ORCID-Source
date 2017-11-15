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

<script type="text/ng-template" id="biography-ng2-template">
    <div class="biography-controller" id="bio-section">
        <div class="row">
            <div class="col-md-9 col-sm-8 col-xs-4">
                <h3 (click)="toggleEdit()" class="workspace-title">${springMacroRequestContext.getMessage("manage_bio_settings.labelbiography")}</h3>  
                <div class="popover-help-container">
                    <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                    <div id="bio-help" class="popover bottom">
                        <div class="arrow"></div>
                        <div class="popover-content">
                            <p><@orcid.msg 'manage_bio_settings.helpPopoverBio'/></p>
                        </div>
                    </div>
                </div>   
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
                        <privacy-toggle-ng2 
                            [dataPrivacyObj]="biographyForm"  
                            (privacyUpdate)="privacyChange($event)"
                            elementId="bio-privacy-toggle" 
                            privacyNodeName="visibility" 
                        ></privacy-toggle-ng2>
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