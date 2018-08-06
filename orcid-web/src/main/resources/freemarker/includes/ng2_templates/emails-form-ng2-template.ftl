<script type="text/ng-template" id="emails-form-ng2-template">
    <div [ngClass]="{'edit-record edit-record-emails' : popUp}" style="position: static">
        <div class="row" *ngIf="popUp">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <h1 class="lightbox-title pull-left">{{emailsEditText}}</h1>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
                <table class="settings-table" style="position: static">
                    <tr>
                        <td colspan="2" [ngClass]="{'email-pop-up' : popUp}" style="border-top:0">
                            <h2>${springMacroRequestContext.getMessage("manage.email.my_email_addresses")}</h2>
                            <div class="editTablePadCell35">
                                <!-- Unverified set primary -->
                                <div class="grey-box" *ngIf="showUnverifiedEmailSetPrimaryBox">
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
                                        <tr *ngFor="let email of formData.emails | orderBy:'value'" class="data-row-group" name="email">
                                            <!-- Primary Email -->
                                            <td [ngClass]="{primaryEmail:email.primary}" class="col-md-3 col-xs-12 email" >                                                     
                                                <span>{{email.value}}</span>
                                            </td>
                                            <td>                     
                                                <span *ngIf="!email.primary"> <a 
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
                                                <a name="delete-email-inline" class="glyphicon glyphicon-trash grey"
                                                    *ngIf="email.primary == false"
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
                                    <div  class="delete-email-box grey-box" *ngIf="showDeleteBox">               
                                        <div style="margin-bottom: 10px;">
                                            <@orcid.msg 'manage.email.pleaseConfirmDeletion' /> {{emailService.delEmail.value}}
                                        </div>
                                        <div>
                                            <ul class="pull-right inline-list">
                                                <li><a (click)="closeDeleteBox()"><@orcid.msg 'freemarker.btncancel' /></a></li>
                                                <li><button class="btn btn-danger" (click)="deleteEmailInline(delEmail)"><@orcid.msg 'manage.email.deleteEmail' /></button></li>                     
                                            </ul>
                                        </div>
                                    </div>
                                    <!-- Email confirmation -->
                                    <div *ngIf="showEmailVerifBox" class="verify-email-box grey-box bottomBuffer">                  
                                        <div>
                                            <p><strong><@orcid.msg 'manage.email.verificationEmail'/> {{verifyEmailObject.value}}</strong><br>
                                            <@orcid.msg 'workspace.check_your_email'/></p>
                                        </div>
                                        <div class="clearfix">
                                            <ul class="pull-right inline-list">
                                                <li><a (click)="closeVerificationBox()"><@orcid.msg 'freemarker.btnclose'/></a></li>
                                            </ul>
                                        </div>
                                    </div>              
                                </div>
                                <div id="addEmailNotAllowed" *ngIf="isPassConfReq" >
                                    ${springMacroRequestContext.getMessage("manage.add_another_email.not_allowed")}
                                </div>          
                                <div *ngIf="!isPassConfReq">
                                    <div class="add-email">
                                        <input type="email" placeholder="${springMacroRequestContext.getMessage("manage.add_another_email")}"
                                        (keyup.enter)="checkCredentials(popUp)" class="input-xlarge inline-input" [(ngModel)]="inputEmail.value"
                                        required />
                                        <span (click)="checkCredentials(popUp)" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>       
                                        <span class="orcid-error"
                                            *ngIf="inputEmail?.errors?.length > 0"> <span
                                            *ngFor='let error of inputEmail.errors'
                                            [innerHTML]="error"></span>
                                        </span>  
                                    </div>              
                                    <p>
                                        <small class="italic">
                                        ${springMacroRequestContext.getMessage("manage.verificationEmail.1")} <a href="${aboutUri}/content/orcid-terms-use" target="manage.verificationEmail.2">${springMacroRequestContext.getMessage("manage.verificationEmail.2")}</a>${springMacroRequestContext.getMessage("manage.verificationEmail.3")}
                                        </small>
                                    </p>             
                                </div>
                                <div  class="confirm-password-box grey-box" *ngIf="popUp && showConfirmationBox">
                                    <div style="margin-bottom: 10px;">
                                        <@orcid.msg 'check_password_modal.confirm_password' />  
                                    </div>
                                    <div>
                                        <label for=""><@orcid.msg 'check_password_modal.password' /></label>:   
                                                       
                                        <input id="check_password_modal.password" type="password" name="check_password_modal.password" [(ngModel)]="password" (keyup.enter)="submitModal()"/>
                                        
                                    </div>                  
                                    <div>
                                        <ul class="pull-right inline-list">
                                            <li><a (click)="closeModal()"><@orcid.msg 'check_password_modal.close'/></a></li>
                                            <li><button id="bottom-submit" class="btn btn-primary" (click)="submitModal()"><@orcid.msg 'check_password_modal.submit'/></button></li>
                                        </ul>   
                                    </div>
                                </div>
                            </div>
                            <#if springMacroRequestContext.requestUri?contains("/account") >
                                <div id="emailFrequency" class="bottomBuffer">
                                    <h2><@orcid.msg 'manage.email.email_frequency.notifications.header' /></h2>
                                    <div class="editTablePadCell35">
                                        <p><@orcid.msg 'manage.email.email_frequency.notifications.1' /></p>
                                        <p><@orcid.msg 'manage.email.email_frequency.notifications.2' /></p>
                                        <p><@orcid.msg 'manage.email.email_frequency.notifications.selectors.header' /></p>                                            
                                        <div class="control-group">
                                            <label for="amend-frequency"><@orcid.msg 'manage.email.email_frequency.notifications.selectors.amend' /></label>
                                            <select id="amend-frequency" name="amend-frequency" [(ngModel)]="sendChangeNotifications" (ngModelChange)="updateChangeNotificationsFrequency()">   
                                                <#list sendEmailFrequencies?keys as key>
                                                    <option value="${key}">${sendEmailFrequencies[key]}</option>
                                                </#list>
                                            </select>
                                        </div>
                                        <div class="control-group">
                                            <label for="administrative-frequency"><@orcid.msg 'manage.email.email_frequency.notifications.selectors.administrative' /></label>
                                            <select id="administrative-frequency" name="administrative-frequency" [(ngModel)]="sendAdministrativeChangeNotifications" (ngModelChange)="updateAdministrativeChangeNotificationsFrequency()">   
                                                <#list sendEmailFrequencies?keys as key>
                                                    <option value="${key}">${sendEmailFrequencies[key]}</option>
                                                </#list>
                                            </select>
                                        </div>
                                        <div class="control-group">
                                            <label for="permission-frequency"><@orcid.msg 'manage.email.email_frequency.notifications.selectors.permission' /></label>                  
                                            <select id="permission-frequency" name="permission-frequency" [(ngModel)]="sendMemberUpdateRequestsNotifications" (ngModelChange)="updateMemberUpdateRequestsFrequency()">   
                                                <#list sendEmailFrequencies?keys as key>
                                                    <option value="${key}">${sendEmailFrequencies[key]}</option>
                                                </#list>
                                            </select>
                                        </div>
                                    </div> 
                                    <h2><@orcid.msg 'manage.email.email_frequency.news.header' /></h2>
                                    <div class="alert" *ngIf="!emailService?.primaryEmail?.verified && sendQuarterlyTips">
                                        <p><strong><@orcid.msg 'manage.email.email_frequency.news.unverified_1' /></strong><br> <@orcid.msg 'manage.email.email_frequency.news.unverified_2' /> <a (click)="verifyEmail(emailService.primaryEmail, popUp, 'newsTips')"><@orcid.msg 'manage.email.email_frequency.news.unverified_3'/></a>

                                    </div>
                                    <div *ngIf="showEmailVerifBoxNewsTips" class="verify-email-box grey-box bottomBuffer">                  
                                        <div>
                                            <p><strong><@orcid.msg 'manage.email.verificationEmail'/> {{verifyEmailObject.value}}</strong><br>
                                            <@orcid.msg 'workspace.check_your_email'/></p>
                                        </div>
                                        <div class="clearfix">
                                            <ul class="pull-right inline-list">
                                                <li><a (click)="closeVerificationBox('newsTips')"><@orcid.msg 'freemarker.btnclose'/></a></li>
                                            </ul>
                                        </div>
                                    </div> 
                                    <div class="editTablePadCell35">
                                        <div class="control-group">
                                            <input id="send-orcid-news" type="checkbox" name="sendOrcidNews" [(ngModel)]="sendQuarterlyTips" (ngModelChange)="updateSendQuarterlyTips()"/>
                                            <label for="send-orcid-news">
                                            <@orcid.msg 'manage.email.email_frequency.notifications.news.checkbox.label' /></label>
                                        </div>
                                    </div>
                                    <p><small class="italic"><@orcid.msg 'manage.email.email_frequency.bottom' /> <a href="https://orcid.org/privacy-policy#How_we_use_information" target="_blank"><@orcid.msg 'public-layout.privacy_policy' /></a></small></p>  
                                </div>
                            </#if>                                                            
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <div class="row" *ngIf="popUp">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <a (click)="closeEditModal()" class="cancel-option pull-right"><@orcid.msg 'manage.email.close' /></a>
            </div>
        </div>
    </div>
</script>