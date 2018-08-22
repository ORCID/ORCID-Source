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
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
<#if (oauthRequest)??>
    <input type="hidden" name="oauthRequest" value="true"/>
<#else>
    <input type="hidden" name="oauthRequest" value="false"/>
</#if>
<div class="form-group">
    <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>
    <input type="text" id="userId" name="userId" [(ngModel)]="authorizationForm.userName.value"  class="form-control" placeholder="${springMacroRequestContext.getMessage("login.username")}" />
</div>
<div class="form-group">
    <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>                   
    <input type="password" id="password" name="password" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("login.password")}" />
</div>
<div class="form-group" id="verificationCodeFor2FA">
    <p class="bold">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.heading")}<p>
    <label for="verificationCode" class="control-label">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.label")}</label>                      
    <input id="verificationCode" name="verificationCode" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.label")}" />  
</div>
<div id="2FAInstructions" style="display:none">
    <p>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.instructions")}</p>
    <p>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.no_device1")} <a href='#' id='enterRecoveryCode'>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.no_device2")}</a></p>
    <p>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.no_device_or_recovery")} <a href='https://orcid.org/help/contact-us'>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.contact_support")}</a></p>
</div>
<div id="recoveryCodeSignin" class="form-group" style="display:none">
    <label for="recoveryCode" class="control-label">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.recoveryCode")}</label>                                       
    <input id="recoveryCode" name="recoveryCode" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.recoveryCode")}">                                        
</div>
<div class="form-group">
    <button id="form-sign-in-button" class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("login.signin")}</button>                                     
    <span id="ajax-loader" class="no-visible"><i id="ajax-loader-icon" class="glyphicon glyphicon-refresh spin x2 green"></i></span>                                        
    <#if (RequestParameters['alreadyClaimed'])??>
    <div class="alert"><@spring.message "orcid.frontend.security.already_claimed"/></div>
    </#if>   
    <#if (RequestParameters['invalidClaimUrl'])??>
    <div class="alert"><@spring.message "orcid.frontend.security.invalid_claim_url"/></div>
    </#if>
</div>
<div id="login-deactivated-error" class="orcid-error" style="display:none">
    <span *ngIf="showDeactivatedError">
        <p>
            <small>
                <@spring.message 'orcid.frontend.deactivated' /><br />
                <@spring.message 'reset_password.enterEmail_2' />
                <a href="mailto:support@orcid.org"><@spring.message 'resend_claim.labelorg' /></a>
            </small>
        </p>
        <div id="reactivate" name="emailAddressForm">            
            <div class="control-group">
                <label for="email" class="control-label"><@spring.message 'manage_bio_settings.h3email' /></label>                       
                <div class="controls"> 
                    <input id="email" name="email" type="text" class="form-control" [(ngModel)]="initReactivationRequest.email" />
                </div>
                <span class="orcid-error" *ngIf="initReactivationRequest.error != null">
                    <div [innerHTML]="initReactivationRequest.error"></div>
                </span>
                <span class="btn btn-primary" (click)="sendReactivationEmail(initReactivationRequest.email)"><@spring.message 'check_password_modal.submit' /></span>
            </div>
        </div>         
    </span> 
    <span *ngIf="showReactivationSent">
        <@spring.message 'orcid.frontend.verify.reactivation_sent.1' /><a href="mailto:support@orcid.org"><@spring.message 'orcid.frontend.verify.reactivation_sent.2' /></a><@spring.message 'orcid.frontend.verify.reactivation_sent.3' />
    </span>
</div>
<div id="loginErrors"></div>