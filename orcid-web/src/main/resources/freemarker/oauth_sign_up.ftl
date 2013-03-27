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
<@spring.bind "oAuthRegistrationForm.*" />
<#if spring.status.errors.globalErrors?? && spring.status.errors.globalErrors?size &gt; 0>
<div class="errorBox">
    <div class="errorHead">${springMacroRequestContext.getMessage("oauth_sign_up.notice")}</div>
    <div class="errorText">
        <@orcid.showGlobalErrorsUnescaped/>
    </div>
</div>
</#if>
    <div class="span6">
    <div class="page-header">
	    <h3>${springMacroRequestContext.getMessage("oauth_sign_up.h3donothaveid")}</h3>
	</div>
        <form id="self-reg-form" class="" name="selfRegForm" action="<@spring.url '/oauth-signup'/>" method="post">
            <fieldset>
                <div>
                    <label for="givenNames">${springMacroRequestContext.getMessage("oauth_sign_up.labelfirstname")}</label>
                    <div >
                        <@spring.formInput "oAuthRegistrationForm.givenNames" 'class="input-xlarge"'/>
                        <span class="required">*</span>
                        <@spring.showErrors "<br/>" "orcid-error"/>
                    </div>
                </div>
                <div>
                    <label for="familyName">${springMacroRequestContext.getMessage("oauth_sign_up.labellastname")}</label>
                    <div >
                        <@spring.formInput "oAuthRegistrationForm.familyName" 'class="input-xlarge" name="lastName"'/>
                        <span class="required">*</span>
                        <@spring.showErrors "<br/>" "orcid-error"/>
                    </div>
                </div>
                 <div>
                    <label for="email">${springMacroRequestContext.getMessage("oauth_sign_up.labelemail")}</label>
                    <div >
                        <@spring.formInput "oAuthRegistrationForm.email" 'class="input-xlarge" name="email"'/>
                        <span class="required">*</span>
                        <@orcid.showErrorsUnescaped/>
                        <@orcid.showErrorsUnescapedForPath "email"/>
                    </div>
                </div>
                <div>
                    <label for="confirmedEmail">${springMacroRequestContext.getMessage("oauth_sign_up.labelreenteremail")}</label>
                    <div >
                        <@spring.formInput "oAuthRegistrationForm.confirmedEmail" 'class="input-xlarge" name="confirmedEmail"'/>
                        <span class="required">*</span>
                        <@spring.showErrors "<br/>" "orcid-error"/>
                    </div>
                </div>
                <div>
                    <label for="confirmedEmail">${springMacroRequestContext.getMessage("oauth_sign_up.labelpassword")}</label>
                    <div >
                        <@spring.formPasswordInput "oAuthRegistrationForm.password" 'class="input-xlarge password-strength" name="confirmedEmail"'/>
                        <span class="required">*</span>
                        <@spring.showErrors "<br/>" "orcid-error"/>
                    </div>
                </div>
                 <div>
                    <label for="confirmedEmail">${springMacroRequestContext.getMessage("oauth_sign_up.labelreenterpassword")}</label>
                    <div >
                        <@spring.formPasswordInput "oAuthRegistrationForm.confirmedPassword" 'class="input-xlarge" name="confirmedEmail"'/>
                        <span class="required">*</span>
                        <@spring.showErrors "<br/>" "orcid-error"/>
                    </div>
                </div>
                <div style="margin-bottom: 20px; margin-top: 10px;">
                     <label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("oauth_sign_up.labeldefaultprivacy")}</label>
                     <label class="visibility-lbl">
                         <@spring.formSingleSelect "oAuthRegistrationForm.workVisibilityDefault", visibilities />
                     </label>
					 <@orcid.privacy "" oAuthRegistrationForm.workVisibilityDefault 'btn-group privacy-group'/>
                     <div>
                        <label>
                        ${springMacroRequestContext.getMessage("oauth_sign_up.labeladdinformationtoyourORCID")}
                        </label>
                    </div>
                </div>                    
        		<div>
        		    <label>${springMacroRequestContext.getMessage("oauth_sign_up.labelnotificationemail")}</label>
            		<div class="checky">
                		<label class="checkbox"><@spring.formCheckbox "oAuthRegistrationForm.sendOrcidChangeNotifications"/>${springMacroRequestContext.getMessage("oauth_sign_up.labelsendmenotification")}</label>
        			</div>
        		</div>
        		<div>
            		<div class="checky">
                		<label class="checkbox"><@spring.formCheckbox "oAuthRegistrationForm.sendOrcidNews"/>${springMacroRequestContext.getMessage("oauth_sign_up.labelsendmeinformation")}</label>
        			</div>
        		</div>                    		                                                                    
        		<div style="margin-top: 20px;">
        		    <label>${springMacroRequestContext.getMessage("oauth_sign_up.labeltermsofuse")} <span class="required">*</span></label>
            		<div class="checky">
                		<label class="checkbox"><@spring.formCheckbox "oAuthRegistrationForm.acceptTermsAndConditions"/>${springMacroRequestContext.getMessage("oauth_sign_up.labeliconsent")} <a href="${aboutUri}/privacy-policy">${springMacroRequestContext.getMessage("oauth_sign_up.labelprivacypolish")}</a> ${springMacroRequestContext.getMessage("oauth_sign_up.labeland")} <a href="${aboutUri}/legal">${springMacroRequestContext.getMessage("oauth_sign_up.labeltermsandconditions")}</a> ${springMacroRequestContext.getMessage("oauth_sign_up.labeluseincluding")}</label>
                		<@spring.showErrors "<br/>" "orcid-error"/>        
                	</div>
        		</div>
            </fieldset>
            <button class="btn btn-large btn-primary" type="submit">${springMacroRequestContext.getMessage("oauth_sign_up.btnregister")}</button>
        </form>
    </div>


