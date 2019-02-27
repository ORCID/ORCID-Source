<script type="text/ng-template" id="biography-ng2-template">
    <!-- Locked error message -->    
    <div class="workspace-inner workspace-header row" *ngIf="userInfo['LOCKED'] == 'true'">
        <div class="col-md-12 col-sm-12 col-xs-12 alert alert-error readme">
            <strong><@orcid.msg 'workspace.locked.header'/></strong>
            <p><@orcid.msg 'workspace.locked.message_1'/><a href="http://orcid.org/help/contact-us" target="Orcid_support"><@orcid.msg 'workspace.locked.message_2'/></a><@orcid.msg 'workspace.locked.message_3'/></p>
        </div>
    </div>                
    <div class="biography-controller" id="bio-section">
        <div class="row">
            <div class="col-md-9 col-sm-8 col-xs-4">
                <h3 (click)="toggleEdit()" class="workspace-title">${springMacroRequestContext.getMessage("manage_bio_settings.labelbiography")}</h3>  
                <div class="popover-help-container">
                    <i class="glyphicon glyphicon-question-sign"></i>
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
                            [dataPrivacyObj]="formData"  
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
                <div style="white-space: pre-wrap" *ngIf="!showEdit" (click)="toggleEdit()">{{formData?.biography?.value}}</div> 
            </div>
        </div>
        
        <div *ngIf="showEdit" class="biography-edit">
            <div class="row">
                <div class="col-md-12 col-xs-12 col-sm-12">
                    <textarea id="biography" name="biography" class="input-xlarge" rows="20" (change)="checkLength()" [(ngModel)]="formData.biography.value"></textarea>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <span class="orcid-error" *ngIf="lengthError==true">
                        <div>${springMacroRequestContext.getMessage("Length.changePersonalInfoForm.biography")}</div>
                    </span>
                    <span class="orcid-error" *ngIf="formData?.biography?.errors?.length > 0">
                        <div *ngFor='let error of formData?.biography?.errors'>{{error}}</div>
                    </span>
                </div>
            </div>
            <div class="row">                                   
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="pull-right full-width">
                        <a class="cancel" (click)="cancel()"><@spring.message "freemarker.btncancel"/></a>
                        <button class="btn btn-primary" (click)="setformData()"><@spring.message "freemarker.btnsavechanges"/></button>
                    </div>
                </div>
            </div>                                                          
        </div>
    </div>
    
</script>