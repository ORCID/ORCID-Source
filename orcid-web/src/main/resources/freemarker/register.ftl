<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@public classes=['home'] nav="register">

    <@spring.bind "registrationForm.*" />
    <div class="row">
        <div class="span3"></div>
        <div class="span9">
            <h2>${springMacroRequestContext.getMessage("register.labelRegisterforanORCIDiD")}</h2>
            <p>${springMacroRequestContext.getMessage("register.labelORCIDprovides")}<br /><br /></p>
                <form id="self-reg-form" class="form-sign-up" name="selfRegForm" action="register" method="post">
                    <#include "/common/browser-checks.ftl" />
                    <div>
                        <label for="givenNames" class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelfirstname")}</label>
                        <div class="relative">
                            <@spring.formInput "registrationForm.givenNames" 'class="input-xlarge" name="firstName" data-validate="{required:true}"'/>
                            <span class="required">*</span>
                            <a href="${knowledgeBaseUri}/articles/142948-names-in-the-orcid-registry" target="_blank"><i class="icon-question-sign"></i></a>                            
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </div>
                    </div>
                    <div>
                        <label for="familyName" class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labellastname")}</label>
                        <div class="relative">
                            <@spring.formInput "registrationForm.familyName" 'class="input-xlarge" name="user.lastName" data-validate="{required:true}"'/>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </div>
                    </div>               
                    <div>
                        <label for="email" class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelemail")}</label>
                        <div class="relative">
                            <@spring.formInput "registrationForm.email" 'class="input-xlarge" name="user.email" data-validate="{required:true, email:true}"'/>
                            <span class="required">*</span>
                            <@orcid.showErrorsUnescaped/>
                            <@orcid.showErrorsUnescapedForPath "email"/>
                        </div>
                    </div>
                    <div>
                        <label for="email" class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelreenteremail")}</label>
                        <div class="relative">
                            <@spring.formInput "registrationForm.confirmedEmail" 'class="input-xlarge" name="user.email" data-validate="{required:true, email:true, equalTo:\"#email\"}"'/>
                            <span class="required">*</span>
                            <@spring.showErrors "<br/>" "orcid-error"/>                            
                        </div>
                    </div>
                    <div>
                        <div class="relative">
                            <@spring.bind "registrationForm"/>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </div>
                    </div>
                    <div class="control-group password-group">
                        <label for="password" class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelpassword")}</label>
                        <div class="relative">
                            <@spring.formPasswordInput "registrationForm.password" 'class="input-xlarge password-strength" data-validate="{required:true}"'/>
                            <span class="required">*</span>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                            <a class="password-info" href="#"><i class="icon-question-sign"></i></a>
                            <div class="popover bottom password-details">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <div class="help-block">
                                        <p>${springMacroRequestContext.getMessage("password_one_time_reset.labelmust8more")}</p>
                                        <ul>
                                            <li>${springMacroRequestContext.getMessage("password_one_time_reset.labelatleast09")}</li>
                                            <li>${springMacroRequestContext.getMessage("password_one_time_reset.labelatleast1following")}</li>
                                            <ul>
                                            	<li>${springMacroRequestContext.getMessage("password_one_time_reset.labelalphacharacter")}</li>
                                            	<li>${springMacroRequestContext.getMessage("password_one_time_reset.labelanyoffollow")}<br /> ! @ # $ % ^ * ( ) ~ `{ } [ ] | \ &amp; _</li>
                                            </ul>
                                            <li>${springMacroRequestContext.getMessage("password_one_time_reset.labeloptionallyspace")}</li>
                                        </ul>                                       
                                        <p>${springMacroRequestContext.getMessage("password_one_time_reset.examplesunmoon")}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                      <div>
                        <label for="password" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.labelconfirmpassword")}</label>
                        <div class="relative">
                            <@spring.formPasswordInput "registrationForm.confirmedPassword" 'class="input-xlarge" data-validate="{required:true, equalTo:\'#password\'}"'/>
                            <span class="required">*</span>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </div>
                    </div>
                    <div style="margin-bottom: 20px; margin-top: 10px;">
                        <label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("register.labelDefaultprivacyfornewworks")}</label>
                        <label class="visibility-lbl">
                            <@spring.formSingleSelect "registrationForm.workVisibilityDefault", visibilities />
                        </label>
                        <@orcid.privacy "" registrationForm.workVisibilityDefault 'btn-group privacy-group'/>
                        <div  style="width: 480px;">
                            <label>
                                ${springMacroRequestContext.getMessage("register.labeladdinformationtoORCID")}
                            </label>
                        </div>
                    </div>                    
                <div style="margin-bottom: 15px;">
                    <div class="relative">
                        <label>${springMacroRequestContext.getMessage("claim.notificationemail")}</label>
                        <label class="checkbox">
                            <@spring.formCheckbox "registrationForm.sendOrcidChangeNotifications"/>
                            ${springMacroRequestContext.getMessage("register.labelsendmenotifications")}
                        </label>
                        <label class="checkbox">
                            <@spring.formCheckbox "registrationForm.sendOrcidNews"/>
                            ${springMacroRequestContext.getMessage("register.labelsendinformation")}
                        </label>
                        </div>
                        </div>
                   <div>
                    <div class="relative"  style="margin-bottom: 15px;">
                        <label>${springMacroRequestContext.getMessage("register.labelTermsofUse")} <span class="required">*</span></label>
                        <label class="checkbox" style="width: 480px;">
                            <@spring.formCheckbox "registrationForm.acceptTermsAndConditions"/>
                            ${springMacroRequestContext.getMessage("register.labelconsent")} <a href="${aboutUri}/footer/privacy-policy?lang=${locale}">${springMacroRequestContext.getMessage("register.labelprivacypolicy")}</a> ${springMacroRequestContext.getMessage("register.labeland")} <a href="${aboutUri}/content/orcid-terms-use?lang=${locale}">${springMacroRequestContext.getMessage("register.labeltermsandconditions")}</a> ${springMacroRequestContext.getMessage("register.labelofuseinclude")}</p>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </label>
                    </div>
                </div>   
                <div class="relative">
                      <button type="submit" class="btn btn-primary">${springMacroRequestContext.getMessage("header.register")}</button>
                </div>           
            </form>
        </div>
    </div>
</@public>