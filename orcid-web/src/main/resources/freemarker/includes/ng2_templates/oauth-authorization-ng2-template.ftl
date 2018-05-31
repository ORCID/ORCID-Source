<script type="text/ng-template" id="oauth-authorization-ng2-template">
    <!-- /data/orcid/git/ORCID-Source/ORCID-Source/orcid-web/src/main/resources/freemarker/confirm-oauth-access.ftl -->
    <#if springMacroRequestContext.requestUri?contains("/oauth/authorize")>
        <div class="container confirm-oauth-access oneStepWidth">     
            <!-- /Freemarker and GA variables -->
            <@security.authorize access="hasRole('ROLE_USER')">
                <div class="row top-header">
                    <div class="col-md-4 col-sm-12 col-xs-12">
                        <div class="logo">
                            <h1><a href="${aboutUri}"><img src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo" /></a></h1>
                        </div>      
                    </div>
                    <div class="col-md-8 col-sm-12 col-xs-12">
                        <#include "/includes/mini_id_banner_ng2.ftl"/>              
                    </div>     
                </div>  
                <div class="row">
                    <div class="col-md-12"> 
                        <div class="app-client-name"> 
                            <h3 (click)="toggleClientDescription()">{{requestInfoForm?.clientName}}
                                <a class="glyphicon glyphicon-question-sign oauth-question-sign"></a>               
                            </h3>
                        </div>
                        <div class="app-client-description">
                            <p *ngIf="showClientDescription">
                                <span class="uppercase gray-bold-about"><@orcid.msg 'oauth_sign_in.about'/></span> {{requestInfoForm.clientDescription}}
                            </p>
                        </div>
                        <div>
                            <p><@orcid.msg 'orcid.frontend.oauth.have_asked'/></p>
                        </div>
                        <div>
                            <#include "/includes/oauth/scopes_ng2.ftl"/>
                        </div>
                        <div>
                            <p *ngIf="gdprUiFeatureEnabled"><@orcid.msg 'confirm-oauth-access.thisApplicationWillNot'/>&nbsp;<a href="${baseUri}/account#manage-permissions" target="confirm-oauth-access.accountSettings"><@orcid.msg 'confirm-oauth-access.accountSettings'/></a>.</p>
                            <p *ngIf="!gdprUiFeatureEnabled"><@orcid.msg 'orcid.frontend.web.oauth_is_secure'/>.&nbsp;<a href="${aboutUri}/footer/privacy-policy" target="public-layout.privacy_policy"><@orcid.msg 'public-layout.privacy_policy'/></a>.</p>
                        </div>          
                        <div id="login-buttons" ng-init="loadAndInitAuthorizationForm()">
                            <div class="row">
                                <div>
                                    <button id="authorize" class="btn btn-primary topBuffer bottomBuffer" name="authorize" value="<@orcid.msg 'confirm-oauth-access.Authorize'/>" (click)="authorize()">
                                        <@orcid.msg 'confirm-oauth-access.Authorize' />
                                    </button>
                                    <a class="oauth-deny-link topBuffer" name="deny" value="<@orcid.msg 'confirm-oauth-access.Deny'/>" (click)="deny()">
                                        <@orcid.msg 'confirm-oauth-access.Deny' />
                                    </a>
                                </div>                  
                            </div>
                        </div>
                    </div>      
                </div>
            </@security.authorize>
        </div>
    </#if>
    <!-- /data/orcid/git/ORCID-Source/ORCID-Source/orcid-web/src/main/resources/freemarker/login.ftl -->
    <#if springMacroRequestContext.requestUri?contains("/signin") >
        <div class="col-md-6 col-md-offset-3">
            <div class="login">         
                <p class="title" *ngIf="!showRegisterForm" >${springMacroRequestContext.getMessage("login.signin")} ${springMacroRequestContext.getMessage("login.or")} <a id="switch-to-register-form" (click)="switchForm()">${springMacroRequestContext.getMessage("login.register")}</a></p>
                <p class="title" *ngIf="showRegisterForm" >Already have an ORCID iD? <a id = "switch-to-login-form" (click)="switchForm()">Sign In</a></p>
                <div *ngIf="!showRegisterForm">
                    <div class="personal-login" >
                        <#if shibbolethEnabled>
                            <div class="btn-group btn-group-justified" role="group">
                                <a (click)="showPersonalLogin()" class="btn btn-default" [ngClass]="{active: personalLogin == true}" role="button"><span class="glyphicon glyphicon-user"></span> ${springMacroRequestContext.getMessage("login.personalaccount")}</a>
                                <a (click)="showInstitutionLogin()" class="btn btn-default" [ngClass]="{active: personalLogin == false}" role="button"><span class="glyphicons bank"></span> ${springMacroRequestContext.getMessage("login.institutionaccount")}</a>
                            </div>
                        </#if>  
                        <div *ngIf="personalLogin == true">
                            <div class="login-box">
                                <!-- ORCID ACCOUNT LOGIN -->
                                <div class="personal-account-login">
                                    <p class="title">${springMacroRequestContext.getMessage("login.signinwithyourorcidaccount")}</p>
                                    <form class="form-sign-in" id="loginForm" action="<@orcid.rootPath '/signin/auth'/>" method="post">
                                        <#include "/includes/login_personal_fields_inc_ng2.ftl"/>
                                    </form>
                                </div>
                                <!-- RESET PASSWORD -->
                                <request-password-reset-ng2 [authorizationForm]="authorizationForm" [showDeactivatedError]="showDeactivatedError" [showReactivationSent]="showReactivationSent" (sendReactivationEmail)="sendReactivationEmail($event)"></request-password-reset-ng2>

                                <!-- SOCIAL LOGIN -->                               
                                <div class="social-login">
                                    <div class="title">
                                        ${springMacroRequestContext.getMessage("login.signinwithasocialaccount")}
                                        <div class="popover-help-container">
                                            <i class="glyphicon glyphicon-question-sign"></i>
                                            <div id="social-login-help" class="popover bottom">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <p><@orcid.msg 'login.signinwithasocialaccount.help.1'/><a href="${knowledgeBaseUri}/articles/892920" target="login.signinviayourinstitution.help.2"><@orcid.msg 'login.signinwithasocialaccount.help.2'/></a><@orcid.msg 'login.signinwithasocialaccount.help.3'/></p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <ul class="social-icons">
                                        <!--FACEBOOK-->
                                        <li>
                                            <form ngNoForm action="<@orcid.rootPath '/signin/facebook'/>" method="POST">
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                <button type="submit" class="btn btn-social-icon btn-facebook"></button>
                                                <input type="hidden" name="scope" value="email"/>
                                            </form>
                                        </li>
                                        <!--TWITTER (NOT USED)
                                        <li>
                                            <form ngNoForm action="<@orcid.rootPath '/signin/twitter'/>" method="POST">
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                <button type="submit" class="btn btn-social-icon btn-twitter"></button>
                                            </form>
                                        </li>
                                        -->
                                        <!--GOOGLE-->
                                        <li>
                                            <form ngNoForm action="<@orcid.rootPath '/signin/google'/>" method="POST">
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                <button type="submit" class="btn btn-social-icon btn-google" ></button>
                                                <input type="hidden" name="scope" value="email" />
                                            </form>
                                        </li>
                                    </ul>
                                </div><!--social login-->
                            </div><!--login box-->
                        </div><!--*ngIf personal login-->
                    </div><!--personal login-->               
                    <!-- SHIBBOLETH -->
                    <div class="institution-login" *ngIf="personalLogin == false"  >
                        <div class="login-box">
                            <div class="institution-login">
                                <div class="title">
                                ${springMacroRequestContext.getMessage('login.signinviayourinstitution')}
                                    <div class="popover-help-container">
                                        <i class="glyphicon glyphicon-question-sign"></i>
                                        <div id="institution-login-help" class="popover bottom">
                                            <div class="arrow"></div>
                                            <div class="popover-content">
                                                <p><@orcid.msg 'login.signinviayourinstitution.help.1'/><a href="${knowledgeBaseUri}/articles/892920" target="login.signinviayourinstitution.help.2"><@orcid.msg 'login.signinviayourinstitution.help.2'/></a><@orcid.msg 'login.signinviayourinstitution.help.3'/></p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div id="idpSelectContainer">
                                    <div id="idpSelectInner">
                                        <div *ngIf="!scriptsInjected" class="text-center" >
                                            <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                                        </div>
                                        <!-- Where the widget is going to be injected -->
                                        <div id="idpSelect"></div>
                                    </div>
                                </div>
                            </div><!--institution login-->
                        </div><!--login box-->
                    </div><!--institution login-->
                </div><!--ng show !registion form-->
                <!-- REGISTRATION FORM-->
                <div class="personal-account-login" id="RegistrationForm" *ngIf="personalLogin == true && showRegisterForm">
                    <p>
                        ${springMacroRequestContext.getMessage("register.labelClause_1")}
                        <a href="http://orcid.org/content/orcid-terms-use" target="terms_and_conditions">
                        ${springMacroRequestContext.getMessage("register.labelClause_2")}
                        </a>
                        ${springMacroRequestContext.getMessage("register.labelClause_3")}
                    </p>
                    <#include "/includes/register_inc_ng2.ftl"/>
                </div><!--personal-account-login-->
                <!-- END -->
            </div><!--login-->
        </div><!--col-md-offset-3-->
    </#if>
    <#if springMacroRequestContext.requestUri?contains("/register") >
        <!-- /data/orcid/git/ORCID-Source/ORCID-Source/orcid-web/src/main/resources/freemarker/register.ftl -->
        <div id="RegistrationForm">
            <#include "/includes/register_inc_ng2.ftl" />
        </div>
    </#if>
</script>
