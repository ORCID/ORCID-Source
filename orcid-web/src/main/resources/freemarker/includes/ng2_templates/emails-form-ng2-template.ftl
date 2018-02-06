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

<script type="text/ng-template" id="emails-form-ng2-template">
    <div class="edit-record edit-record-emails" style="position: static">
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <h1 class="lightbox-title pull-left">{{emailsEditText}}</h1>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
                <table class="settings-table" style="position: static">
                    <tr>
                        <td colspan="2"  [ngClass]="{'email-pop-up' : popUp}" >
                            <div *ngIf="showEditEmail || popUp" class="editTablePadCell35" style="position: static"><!--  -->

                                <!-- Start -->
                                <div class="row">
                                    <strong class="green">${springMacroRequestContext.getMessage("manage.email.my_email_addresses")}</strong>
                                </div>
                                <!-- Unverified set primary -->
                                <div class="grey-box" *ngIf="popUp && showUnverifiedEmailSetPrimaryBox">
                                    <h4><@orcid.msg 'workspace.your_primary_email_new' /></h4>
                                    <p><@orcid.msg 'workspace.youve_changed' /></p>
                                    <p><@orcid.msg 'workspace.you_need_to_verify' /></p>
                                    <p><@orcid.msg 'workspace.ensure_future_access2' /><br />
                                    <p><strong>{{primaryEmail}}</strong></p>
                                    <p><@orcid.msg 'workspace.ensure_future_access3' /> <a target="articles.149457" href="${knowledgeBaseUri}/articles/149457"><@orcid.msg 'workspace.ensure_future_access4' /></a> <@orcid.msg 'workspace.ensure_future_access5' /> <a target="workspace.link.email.support" href="mailto:<@orcid.msg 'workspace.link.email.support' />"><@orcid.msg 'workspace.link.email.support' /></a>.</p>
                                    <div class="topBuffer">
                                        <a (click)="closeUnverifiedEmailSetPrimaryBox()"><@orcid.msg 'freemarker.btnclose' /></a>
                                    </div>
                                </div>                       
                                <!-- Email table -->
                                <div class="table-responsive bottomBuffer" style="position: static">
                                    <table class="table" style="position: static">
                                        <tr *ngFor="let email of formData.emails" class="data-row-group" name="email"><!-- | orderBy:['value'] -->
                                            <!-- Primary Email -->
                                            <td [ngClass]="{primaryEmail:email.primary}" class="col-md-3 col-xs-12 email" >                                                     
                                                <span>{{email.value}}</span>
                                            </td>
                                            <!-- Set Primary options -->
                                            <!--
                                            {{email | json}}
                                            -->
                                            <td>                     
                                                <span *ngIf="!email.primary"> <a href=""
                                                    (click)="setPrimary(email)">${springMacroRequestContext.getMessage("manage.email.set_primary")}</a>
                                                </span>
                                                <span *ngIf="email.primary" class="muted" style="color: #bd362f;">
                                                    ${springMacroRequestContext.getMessage("manage.email.primary_email")}
                                                </span>
                                            </td>
                                            <!-- 
                                            <td ng-init="emailStatusOptions = [{label:'<@orcid.msg "manage.email.current.true" />',val:true},{label:'<@orcid.msg "manage.email.current.false" />',val:false}];"> 
                                            -->
                                            <td>            
                                                <!-- <select [(ngModel)]="email.current" 
                                                    ng-options ="emailStatusOption.val as emailStatusOption.label for emailStatusOption in emailStatusOptions"
                                                    (ngModelChange)="emailSrvc.saveEmail()">-->                
                                                <select 
                                                    [(ngModel)]="email.current" 
                                                    (ngModelChange)="saveEmail(false)"
                                                >
                                                    <option 
                                                        *ngFor="let emailStatusOption of emailStatusOptions"
                                                        [value]="emailStatusOption.val"
                                                    >
                                                        {{emailStatusOption.label}}   
                                                    </option>             
                                                    
                                                </select>
                                            </td>
                                            <td class="email-verified">
                                                <span *ngIf="!email.verified" class="left">
                                                    <a (click)="verifyEmail(email, popUp)">${springMacroRequestContext.getMessage("manage.email.verify")}</a>
                                                </span>
                                                <span *ngIf="email.verified" class="left">
                                                    ${springMacroRequestContext.getMessage("manage.email.verified")}
                                                </span>
                                            </td>
                                            <td width="26" class="tooltip-container">
                                                <a name="delete-email" class="glyphicon glyphicon-trash grey"
                                                    *ngIf="email.primary == false && !popUp"
                                                    (click)="confirmDeleteEmail(email)" >
                                                        <div class="popover small-popover popover-tooltip top">
                                                            <div class="arrow"></div>
                                                            <div class="popover-content">
                                                                <span><@spring.message "common.modals.delete"/></span>
                                                            </div>
                                                        </div>
                                                    </a>
                                                <a name="delete-email-inline" class="glyphicon glyphicon-trash grey"
                                                    *ngIf="email.primary == false && popUp"
                                                    (click)="confirmDeleteEmailInline(email, $event)" >
                                                    <div class="popover small-popover popover-tooltip top">
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                            <span><@spring.message "common.modals.delete"/></span>
                                                        </div>
                                                    </div>
                                                </a>
                                            </td>
                                            <td width="100" style="padding-top: 0; position: static">
                                                <div class="emailVisibility" style="float: right; position: static">
                                                    <privacy-toggle-ng2 
                                                    [dataPrivacyObj]="email" 
                                                    (privacyUpdate)="privacyChange($event, email)"
                                                    elementId="email-privacy-toggle" 
                                                    >    
                                                    </privacy-toggle-ng2>
                         
                                                </div>
                                            </td>
                                        </tr>
                                    </table>            
                                    <!-- Delete Email Box -->
                                    <div  class="delete-email-box grey-box" *ngIf="popUp && showDeleteBox">               
                                        <div style="margin-bottom: 10px;">
                                            <@orcid.msg 'manage.email.pleaseConfirmDeletion' /> {{emailService.delEmail.value}}
                                        </div>
                                        <div>
                                            <ul class="pull-right inline-list">
                                                <li><a href="" (click)="closeDeleteBox()"><@orcid.msg 'freemarker.btncancel' /></a></li>
                                                <li><button class="btn btn-danger" (click)="deleteEmailInline(delEmail)"><@orcid.msg 'manage.email.deleteEmail' /></button></li>                     
                                            </ul>
                                        </div>
                                    </div>
                                    <!-- Email confirmation -->
                                    <div *ngIf="popUp && showEmailVerifBox" class="verify-email-box grey-box">                  
                                        <div style="margin-bottom: 10px;">
                                            <h4><@orcid.msg 'manage.email.verificationEmail'/> {{verifyEmailObject.value}}</h4>
                                            <p><@orcid.msg 'workspace.check_your_email'/></p>
                                        </div>
                                        <div class="clearfix">
                                            <ul class="pull-right inline-list">
                                                <li><a href="" (click)="closeVerificationBox()"><@orcid.msg 'freemarker.btnclose'/></a></li>
                                            </ul>
                                        </div>
                                    </div>              
                                </div>
                                <div id="addEmailNotAllowed" *ngIf="isPassConfReq" >
                                    ${springMacroRequestContext.getMessage("manage.add_another_email.not_allowed")}
                                </div>          
                                <div class="row bottom-row" *ngIf="!isPassConfReq" >
                                    <div class="col-md-12 add-email">
                                        
                                        <input type="email" placeholder="${springMacroRequestContext.getMessage("manage.add_another_email")}"
                                        (keyup.enter)="checkCredentials(popUp)" class="input-xlarge inline-input" [(ngModel)]="inputEmail.value"
                                        required />
                                        
                                        <span (click)="checkCredentials(popUp)" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>
                                                    
                                        <span class="orcid-error"
                                            *ngIf="inputEmail.errors.length > 0"> <span
                                            *ngFor='let error of inputEmail.errors'
                                            [innerHTML]="error"></span>
                                        </span>
                                        
                                    </div>              
                                    <div class="col-md-12">
                                        <p style="line-height: 12px;">
                                            <small class="italic">
                                            ${springMacroRequestContext.getMessage("manage.verificationEmail.1")} <a href="${aboutUri}/content/orcid-terms-use" target="manage.verificationEmail.2">${springMacroRequestContext.getMessage("manage.verificationEmail.2")}</a>${springMacroRequestContext.getMessage("manage.verificationEmail.3")}
                                            </small>
                                        </p>
                                    </div>              
                                </div>
                                <div class="row">
                                    <div  class="confirm-password-box grey-box" *ngIf="popUp && showConfirmationBox">
                                        <div style="margin-bottom: 10px;">
                                            <@orcid.msg 'check_password_modal.confirm_password' />  
                                        </div>
                                        <div>
                                            <label for=""><@orcid.msg 'check_password_modal.password' /></label>:   
                                            <!--                    
                                            <input id="check_password_modal.password" type="password" name="check_password_modal.password" [(ngModel)]="password" (keyup.enter)="submitModal(emailSrvc.popUp)"/>
                                            -->
                                        </div>                  
                                        <div>
                                            <ul class="pull-right inline-list">
                                                <li><a href="" (click)="closeModal()"><@orcid.msg 'check_password_modal.close'/></a></li>
                                                <li><button id="bottom-submit" class="btn btn-primary" (click)="submitModal()"><@orcid.msg 'check_password_modal.submit'/></button></li>
                                            </ul>   
                                        </div>
                                    </div>
                                </div>
                                <!--
                                <email-frecuency-ng2></email-frecuency-ng2>
                                -->
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <a (click)="closeEditModal()" class="cancel-option pull-right"><@orcid.msg 'freemarker.btncancel' /></a>
            </div>
        </div>
    </div>
</script>