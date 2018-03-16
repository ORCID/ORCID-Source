<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
<div class="form-group">
    <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>
    <input type="text" id="userId" name="userId" ng-model="authorizationForm.userName.value" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("login.username")}">
</div>
<div class="form-group">
    <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>					
    <input type="password" id="password" name="password" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("login.password")}">
</div>
<div class="form-group" id="verificationCodeFor2FA">
    <p class="bold">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.heading")}<p>
    <label for="verificationCode" class="control-label">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.label")}</label>                      
    <input id="verificationCode" name="verificationCode" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.label")}">  
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
    <span ng-show="showDeactivatedError">
    ${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.1")}<a href="" ng-click="sendReactivationEmail(authorizationForm.userName.value)">${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.3")}
    </span>
    <span ng-show="showReactivationSent">
    ${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.1")}<a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.3")}
    </span>
</div>
<div id="loginErrors"></div>