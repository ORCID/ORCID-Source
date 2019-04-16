<script type="text/ng-template" id="link-account-ng2-template">
    <div *ngIf="registration">
        <div *ngIf="!loadedFeed" class="text-center">
            <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
            <!--[if lt IE 8]>
                <img src="{{assetsPath}}/img/spin-big.gif" width="85" height ="85"/>
            <![endif]-->
        </div>
        <div *ngIf="loadedFeed">
            <h2>${springMacroRequestContext.getMessage("social.link.title.1")} {{idpName}}  ${springMacroRequestContext.getMessage("social.link.title.2")}</h2>
            <h4>${springMacroRequestContext.getMessage("social.link.you_are")} {{idpName}}  ${springMacroRequestContext.getMessage("social.link.as")} ${RequestParameters.accountId!}</h4>
            <p> 
                ${springMacroRequestContext.getMessage("social.link.to_finish.register.1")}{{idpName}}${springMacroRequestContext.getMessage("social.link.to_finish.register.2")}
            </p>
            <p>
                <i>${springMacroRequestContext.getMessage("social.link.you_will_only.1")}{{idpName}} ${springMacroRequestContext.getMessage("social.link.you_will_only.2")} <a href="<@orcid.msg 'common.kb_uri_default'/>360006972693" target="social.link.visit_knowledgebase_link" >${springMacroRequestContext.getMessage("social.link.visit_knowledgebase_link")}</a></i>
            </p>
        </div>
        <div>            
            <#if "shibboleth" == (RequestParameters['linkRequest']!)><a class="reg" href="{{getBaseUri()}}/shibboleth/signin"><#else><a class="reg" href="{{getBaseUri()}}/social/access"></#if>${springMacroRequestContext.getMessage("social.link.link_this_account")}</a> &nbsp;&#124;&nbsp; ${springMacroRequestContext.getMessage("login.registerOrcidId")} &nbsp;&#124;&nbsp; <a class="reg" href="{{getBaseUri()}}/signin">${springMacroRequestContext.getMessage("social.link.return_to_signin")}</a>
            <hr />
        </div> 
    </div> 
    <div *ngIf="shibbolethSignin || socialSignin"> 
        <div *ngIf="signinData && signinData.unsupportedInstitution" class="col-md-9 col-sm-9 col-sm-push-3 col-md-push-3">
            <p class="alert">${springMacroRequestContext.getMessage("social.link.unsupported.unsuccessful.1")}<span *ngIf="signinData.accountId">${springMacroRequestContext.getMessage("social.link.unsupported.unsuccessful.2")}{{signinData.accountId}}${springMacroRequestContext.getMessage("social.link.unsupported.unsuccessful.3")}</span>${springMacroRequestContext.getMessage("social.link.unsupported.unsuccessful.4")}</p>
            <p>${springMacroRequestContext.getMessage("social.link.unsupported.we_have_made_a_note.1")}<a href="mailto:{{signinData.institutionContactEmail}}?cc=support@orcid.org&amp;?subject=${springMacroRequestContext.getMessage("social.link.unsupported.email.subject")?url}&amp;body=${springMacroRequestContext.getMessage("social.link.unsupported.email.body")?url}">${springMacroRequestContext.getMessage("social.link.unsupported.we_have_made_a_note.2")}</a>${springMacroRequestContext.getMessage("social.link.unsupported.we_have_made_a_note.3")}<a href="{{getBaseUri()}}/signin">${springMacroRequestContext.getMessage("social.link.unsupported.we_have_made_a_note.4")}</a>${springMacroRequestContext.getMessage("social.link.unsupported.we_have_made_a_note.5")}</p>
            <p class="see-more">
                ${springMacroRequestContext.getMessage("social.link.unsupported.why_cant_i")}
            </p>
            <p>
                ${springMacroRequestContext.getMessage("social.link.unsupported.you_can_find_out.1")}<a href="<@orcid.msg 'common.kb_uri_default'/>360006972693" target="social.link.unsupported.you_can_find_out.2">${springMacroRequestContext.getMessage("social.link.unsupported.you_can_find_out.2")}</a>${springMacroRequestContext.getMessage("social.link.unsupported.you_can_find_out.3")}
            </p>
        </div>
        <div *ngIf="signinData && signinData.headerCheckFailed" class="col-md-9 col-sm-9 col-sm-push-3 col-md-push-3">
            <p class="alert">${springMacroRequestContext.getMessage("social.link.header.mismatch")}</p>
        </div>
        <div *ngIf="signinData && !signinData.unsupportedInstitution && !signinData.unsupportedInstitution && !loadedFeed" class="text-center">
            <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
            <!--[if lt IE 8]>
                <img src="{{assetsPath}}/img/spin-big.gif" width="85" height ="85"/>
            <![endif]-->
        </div>
        <form class="form-social-sign-in shibboleth" id="loginForm" *ngIf="signinData && !signinData.unsupportedInstitution && !signinData.unsupportedInstitution && loadedFeed" action="{{getBaseUri()}}/shibboleth/signin/auth" method="post">
            <div class="row">
                <div class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-9 col-xs-12 bottomBuffer">
                    <div>
                        <h2>${springMacroRequestContext.getMessage("social.link.title.1")} {{idpName}} ${springMacroRequestContext.getMessage("social.link.title.2")}</h2>
                        <h4>${springMacroRequestContext.getMessage("social.link.you_are")} {{idpName}} ${springMacroRequestContext.getMessage("social.link.as")} {{signinData.accountId}}</h4>
                        <p> 
                            ${springMacroRequestContext.getMessage("social.link.to_finish")} {{idpName}} ${springMacroRequestContext.getMessage("social.link.account_to_orcid")}
                        </p>
                        <p>
                            <i>${springMacroRequestContext.getMessage("social.link.you_will_only.1")} {{idpName}} ${springMacroRequestContext.getMessage("social.link.you_will_only.2")} <a href="<@orcid.msg 'common.kb_uri_default'/>360006972693" target="social.link.visit_knowledgebase_link" >${springMacroRequestContext.getMessage("social.link.visit_knowledgebase_link")}</a></i>
                        </p>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12">                       
                    ${springMacroRequestContext.getMessage("social.link.link_this_account")} &nbsp;&#124;&nbsp; <a class="reg" href="{{getBaseUri()}}/register?linkRequest={{signinData.linkType}}&emailId={{signinData.emailEncoded}}&firstName={{signinData.firstNameEncoded}}&lastName={{signinData.lastNameEncoded}}&providerId={{signinData.providerIdEncoded}}&accountId={{signinData.accountIdEncoded}}">${springMacroRequestContext.getMessage("login.registerOrcidId")}</a> &nbsp;&#124;&nbsp; <a class="reg" href="{{getBaseUri()}}/signin">${springMacroRequestContext.getMessage("social.link.return_to_signin")}</a>
                    <hr />
                </div>                                         
                <div class="col-md-offset-3 col-md-6 col-sm-9 col-sm-offset-3 col-xs-12 col-lg-6">
                    <#include "/includes/login_personal_fields_inc_ng2.ftl"/>
                </div>
            </div>
        </form>
        <!-- RESET PASSWORD -->
        <div *ngIf="signinData && !signinData.unsupportedInstitution && !signinData.unsupportedInstitution && loadedFeed" class="row shibboleth">
            <div class="col-md-offset-3 col-md-6 col-sm-9 col-sm-offset-3 col-xs-12 col-lg-6">
                <request-password-reset-ng2 [authorizationForm]="authorizationForm"></request-password-reset-ng2>
            </div> 
        </div>
    </div>
</script>