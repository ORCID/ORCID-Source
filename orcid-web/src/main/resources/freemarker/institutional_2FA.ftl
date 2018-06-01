<@public classes=['home'] nav="signin">
    <div ng-controller="Institutional2FACtrl" ng-init="init()">
        <form class="form-social-sign-in" ng-submit="submitCode()" method="post">
            <div class="row">
                <div class="col-md-offset-3 col-md-6 col-sm-9 col-sm-offset-3 col-xs-12 col-lg-6">
                    <div class="orcid-error">
                        <p ng-bind="codes.errors[0]"></p>
                    </div>
                    <div class="form-group">
                        <div class="bold">${springMacroRequestContext.getMessage("2FA.orcid")} ${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.heading")}
                            <div class="popover-help-container">
                                <i class="glyphicon glyphicon-question-sign"></i>
                                <div id="social-login-help" class="popover bottom">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <p>${springMacroRequestContext.getMessage("2FA.social_inst.text")} <a href="${knowledgeBaseUri}/articles/1190068" target="common.learn_more">${springMacroRequestContext.getMessage("common.learn_more")}</a></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <label for="verificationCode" class="control-label">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.label")}</label>                                       
                        <input id="verificationCode" ng-model="codes.verificationCode" name="verificationCode" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.label")}">     
                    </div>
                    <div id="2FAInstructions">
                        <p>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.instructions")}</p>
                        <p>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.no_device1")} <a href='#' id='enterRecoveryCode'>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.no_device2")}</a></p>
                        <p>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.no_device_or_recovery")} <a href='https://orcid.org/help/contact-us'>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.contact_support")}</a></p>
                    </div>
                    <div id="recoveryCodeSignin" class="form-group" style="display:none">
                        <label for="recoveryCode" class="control-label">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.recoveryCode")}</label>                                       
                        <input id="recoveryCode" ng-model="codes.recoveryCode" name="recoveryCode" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.recoveryCode")}">                                               
                    </div>
                </div>
                <div class="col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12 col-lg-6">
                    <div class="control-group">                    
                        <ul class="inline-list">
                            <li><button id='form-sign-in-button' class="btn btn-primary social-signin-btn" type="submit">${springMacroRequestContext.getMessage("login.signin")}</button></li>
                            <li><span id="ajax-loader" class="no-visible"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x2 green"></i></span></li>
                        </ul>                
                    </div>
                </div>              
            </div>
        </form>
    </div>
</@public>