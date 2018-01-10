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
                    <h1 class="lightbox-title pull-left">om.get("manage.edit.emails")</h1>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
                <table class="settings-table" style="position: static">
                    <tr>
                        <td colspan="2" *ngIf="showEditEmail || emailSrvc.popUp" ng-class="{'email-pop-up' : emailSrvc.popUp}" >
                            <div class="editTablePadCell35" style="position: static">
                                <!-- we should never see errors here, but just to be safe -->
                                <div class="orcid-error" *ngIf="emailSrvc.emails.errors.length > 0">
                                    <span ng-repeat='error in emailSrvc.emails.errors' >{{error}}</span>
                                </div>
                                <!-- Start -->
                                <div class="row">
                                    <strong class="green">${springMacroRequestContext.getMessage("manage.email.my_email_addresses")}</strong>
                                </div>
                                <!-- Unverified set primary -->
                                <div *ngIf="emailSrvc.popUp && showUnverifiedEmailSetPrimaryBox" class="grey-box">
                                    <h4><@orcid.msg 'workspace.your_primary_email_new' /></h4>
                                    <p><@orcid.msg 'workspace.youve_changed' /></p>
                                    <p><@orcid.msg 'workspace.you_need_to_verify' /></p>
                                    <p><@orcid.msg 'workspace.ensure_future_access2' /><br />
                                    <p><strong>{{emailSrvc.getEmailPrimary().value}}</strong></p>
                                    <p><@orcid.msg 'workspace.ensure_future_access3' /> <a target="articles.149457" href="${knowledgeBaseUri}/articles/149457"><@orcid.msg 'workspace.ensure_future_access4' /></a> <@orcid.msg 'workspace.ensure_future_access5' /> <a target="workspace.link.email.support" href="mailto:<@orcid.msg 'workspace.link.email.support' />"><@orcid.msg 'workspace.link.email.support' /></a>.</p>
                                    <div class="topBuffer">
                                        <a ng-click="closeUnverifiedEmailSetPrimaryBox()"><@orcid.msg 'freemarker.btnclose' /></a>
                                    </div>
                                </div>                       
                                <!-- Email table -->
                                <div class="table-responsive bottomBuffer" style="position: static">
                                    <table class="table" style="position: static">
                                        <tr ng-repeat="email in emailSrvc.emails.emails | orderBy:['value']" class="data-row-group" name="email">
                                            <!-- Primary Email -->
                                            <td ng-class="{primaryEmail:email.primary}" class="col-md-3 col-xs-12 email" >                                                     
                                                <span>{{email.value}}</span>
                                            </td>
                                            <!-- Set Primary options -->
                                            <td>                           
                                                <span ng-hide="email.primary"> <a href=""
                                                    ng-click="emailSrvc.setPrimary(email)">${springMacroRequestContext.getMessage("manage.email.set_primary")}</a>
                                                </span> <span *ngIf="email.primary" class="muted" style="color: #bd362f;">
                                                    ${springMacroRequestContext.getMessage("manage.email.primary_email")}
                                                </span>
                                            </td>
                                            <td ng-init="emailStatusOptions = [{label:'<@orcid.msg "manage.email.current.true" />',val:true},{label:'<@orcid.msg "manage.email.current.false" />',val:false}];">                            
                                                <select ng-model="email.current" 
                                                    ng-options ="emailStatusOption.val as emailStatusOption.label for emailStatusOption in emailStatusOptions"
                                                    ng-change="emailSrvc.saveEmail()">                      
                                                </select>
                                            </td>
                                            <td class="email-verified">
                                                <span ng-hide="email.verified" class="left">
                                                    <a ng-click="verifyEmail(email, emailSrvc.popUp)">${springMacroRequestContext.getMessage("manage.email.verify")}</a>
                                                </span>
                                                <span *ngIf="email.verified" class="left">
                                                    ${springMacroRequestContext.getMessage("manage.email.verified")}
                                                </span>
                                            </td>
                                            <td width="26" class="tooltip-container">
                                                <a name="delete-email" class="glyphicon glyphicon-trash grey"
                                                    *ngIf="email.primary == false && !emailSrvc.popUp"
                                                    ng-click="confirmDeleteEmail(email)" >
                                                        <div class="popover small-popover popover-tooltip top">
                                                            <div class="arrow"></div>
                                                            <div class="popover-content">
                                                                <span><@spring.message "common.modals.delete"/></span>
                                                            </div>
                                                        </div>
                                                    </a>
                                                <a name="delete-email-inline" class="glyphicon glyphicon-trash grey"
                                                    *ngIf="email.primary == false && emailSrvc.popUp"
                                                    ng-click="confirmDeleteEmailInline(email, $event)" >
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
                                                    <@orcid.privacyToggle3
                                                        angularModel="email.visibility"
                                                        questionClick="toggleClickPrivacyHelp(email.value)"
                                                        clickedClassCheck="{'popover-help-container-show':privacyHelp[email.value]==true}" 
                                                        publicClick="setPrivacy(email, 'PUBLIC', $event)" 
                                                        limitedClick="setPrivacy(email, 'LIMITED', $event)" 
                                                        privateClick="setPrivacy(email, 'PRIVATE', $event)" 
                                                        elementId="email.value" />    
                                                </div>
                                            </td>
                                        </tr>
                                    </table>            
                                    <!-- Delete Email Box -->
                                    <div ng-if="emailSrvc.popUp && showDeleteBox" class="delete-email-box grey-box">                    
                                        <div style="margin-bottom: 10px;">
                                            <@orcid.msg 'manage.email.pleaseConfirmDeletion' /> {{emailSrvc.delEmail.value}}
                                        </div>
                                        <div>
                                            <ul class="pull-right inline-list">
                                                <li><a href="" ng-click="closeDeleteBox()"><@orcid.msg 'freemarker.btncancel' /></a></li>
                                                <li><button class="btn btn-danger" ng-click="deleteEmailInline(emailSrvc.delEmail)"><@orcid.msg 'manage.email.deleteEmail' /></button></li>                     
                                            </ul>
                                        </div>
                                    </div>
                                    <!-- Email confirmation -->
                                    <div *ngIf="emailSrvc.popUp && showEmailVerifBox" class="verify-email-box grey-box">                  
                                        <div style="margin-bottom: 10px;">
                                            <h4><@orcid.msg 'manage.email.verificationEmail'/> {{verifyEmailObject.value}}</h4>
                                            <p><@orcid.msg 'workspace.check_your_email'/></p>
                                        </div>
                                        <div class="clearfix">
                                            <ul class="pull-right inline-list">
                                                <li><a href="" ng-click="closeVerificationBox()"><@orcid.msg 'freemarker.btnclose'/></a></li>
                                            </ul>
                                        </div>
                                    </div>              
                                </div>
                                <div id="addEmailNotAllowed" *ngIf="isPassConfReq" >
                                    ${springMacroRequestContext.getMessage("manage.add_another_email.not_allowed")}
                                </div>          
                                <div class="row bottom-row" ng-hide="isPassConfReq" >
                                    <div class="col-md-12 add-email">
                                        <input type="email" placeholder="${springMacroRequestContext.getMessage("manage.add_another_email")}"
                                        ng-enter="checkCredentials(emailSrvc.popUp)" class="input-xlarge inline-input" ng-model="emailSrvc.inputEmail.value"
                                        required />
                                        <span ng-click="checkCredentials(emailSrvc.popUp)" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>                 
                                        <span class="orcid-error"
                                            *ngIf="emailSrvc.inputEmail.errors.length > 0"> <span
                                            ng-repeat='error in emailSrvc.inputEmail.errors'
                                            ng-bind-html="error"></span>
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
                                    <div *ngIf="emailSrvc.popUp && showConfirmationBox" class="confirm-password-box grey-box">
                                        <div style="margin-bottom: 10px;">
                                            <@orcid.msg 'check_password_modal.confirm_password' />  
                                        </div>
                                        <div>
                                            <label for=""><@orcid.msg 'check_password_modal.password' /></label>:                       
                                            <input id="check_password_modal.password" type="password" name="check_password_modal.password" ng-model="password" ng-enter="submitModal(emailSrvc.popUp)"/>
                                        </div>                  
                                        <div>
                                            <ul class="pull-right inline-list">
                                                <li><a href="" ng-click="closeModal()"><@orcid.msg 'check_password_modal.close'/></a></li>
                                                <li><button id="bottom-submit" class="btn btn-primary" ng-click="submitModal()"><@orcid.msg 'check_password_modal.submit'/></button></li>
                                            </ul>   
                                        </div>
                                    </div>
                                </div>
                                <div ng-controller="EmailFrequencyCtrl" >
                                    <div class="row bottomBuffer">
                                        <strong class="green">${springMacroRequestContext.getMessage("manage.email.email_frequency")}</strong>
                                    </div>              
                                    <div class="control-group">
                                        <p>${springMacroRequestContext.getMessage("manage.send_email_to_primary_1")} <a href="${baseUri}/inbox" target="manage.send_email_to_primary_2">${springMacroRequestContext.getMessage("manage.send_email_to_primary_2")}</a>${springMacroRequestContext.getMessage("manage.send_email_to_primary_3")}</p>
                                        <form class="form-inline">
                                            <div class="form-group">                            
                                                <div class="input-group">                               
                                                    <select id="sendEmailFrequencyDays" name="sendEmailFrequencyDays" class="input-xlarge" ng-model="prefsSrvc.prefs['email_frequency']" ng-change="prefsSrvc.clearMessage()">
                                                        <#list sendEmailFrequencies?keys as key>
                                                            <option value="${key}">${sendEmailFrequencies[key]}</option>
                                                        </#list>
                                                    </select>
                                                </div>
                                            </div>
                                            <button ng-click="prefsSrvc.updateEmailFrequency()" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.send_email_frequency_save")}</button>
                                            <small class="green" *ngIf="prefsSrvc.saved">${springMacroRequestContext.getMessage("manage.send_email_frequency_saved")}</small>    
                                        </form>
                                    </div>
                                    <div class="control-group">
                                        <p>${springMacroRequestContext.getMessage("manage.send_email_to_primary_4")} {{emailSrvc.primaryEmail.value}}${springMacroRequestContext.getMessage("manage.send_email_to_primary_5")}</p>
                                        <p>${springMacroRequestContext.getMessage("manage.service_announcements")}</p>
                                        <p style="line-height: 12px;"><small class="italic">${springMacroRequestContext.getMessage("manage.service_announcements.note")}</small>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <a ng-click="close()" class="cancel-option pull-right">om.get("manage.email.close")</a>
            </div>
        </div>
    </div>
</script>