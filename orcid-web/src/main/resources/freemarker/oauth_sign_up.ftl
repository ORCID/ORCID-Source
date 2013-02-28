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
    <div class="errorHead">Notice:</div>
    <div class="errorText">
        <@orcid.showGlobalErrorsUnescaped/>
    </div>
</div>
</#if>
    <div class="span6">
    <div class="page-header">
	    <h3>Don't have an iD? Register</h3>
	</div>
        <form id="self-reg-form" class="" name="selfRegForm" action="<@spring.url '/oauth-signup'/>" method="post">
            <fieldset>
                <div>
                    <label for="givenNames">First name</label>
                    <div >
                        <@spring.formInput "oAuthRegistrationForm.givenNames" 'class="input-xlarge"'/>
                        <span class="required">*</span>
                        <@spring.showErrors "<br/>" "orcid-error"/>
                    </div>
                </div>
                <div>
                    <label for="familyName">Last name</label>
                    <div >
                        <@spring.formInput "oAuthRegistrationForm.familyName" 'class="input-xlarge" name="lastName"'/>
                        <span class="required">*</span>
                        <@spring.showErrors "<br/>" "orcid-error"/>
                    </div>
                </div>
                 <div>
                    <label for="email">Email</label>
                    <div >
                        <@spring.formInput "oAuthRegistrationForm.email" 'class="input-xlarge" name="email"'/>
                        <span class="required">*</span>
                        <@orcid.showErrorsUnescaped/>
                        <@orcid.showErrorsUnescapedForPath "email"/>
                    </div>
                </div>
                <div>
                    <label for="confirmedEmail">Re-enter email</label>
                    <div >
                        <@spring.formInput "oAuthRegistrationForm.confirmedEmail" 'class="input-xlarge" name="confirmedEmail"'/>
                        <span class="required">*</span>
                        <@spring.showErrors "<br/>" "orcid-error"/>
                    </div>
                </div>
                <div>
                    <label for="confirmedEmail">Password</label>
                    <div >
                        <@spring.formPasswordInput "oAuthRegistrationForm.password" 'class="input-xlarge password-strength" name="confirmedEmail"'/>
                        <span class="required">*</span>
                        <@spring.showErrors "<br/>" "orcid-error"/>
                    </div>
                </div>
                 <div>
                    <label for="confirmedEmail">Re-enter Password</label>
                    <div >
                        <@spring.formPasswordInput "oAuthRegistrationForm.confirmedPassword" 'class="input-xlarge" name="confirmedEmail"'/>
                        <span class="required">*</span>
                        <@spring.showErrors "<br/>" "orcid-error"/>
                    </div>
                </div>
                <div style="margin-bottom: 20px; margin-top: 10px;">
                     <label class="privacy-toggle-lbl">Default privacy for new works</label>
                     <label class="visibility-lbl">
                         <@spring.formSingleSelect "oAuthRegistrationForm.workVisibilityDefault", visibilities />
                     </label>
					 <@orcid.privacy "" oAuthRegistrationForm.workVisibilityDefault 'btn-group privacy-group'/>
                     <div>
                        <label>
                        You will be able to add information to your ORCID record about work that you have done. This privacy setting will apply to all works/publications added to your record. You can change this any time.
                        </label>
                    </div>
                </div>                    
        		<div>
        		    <label>Notification Email</label>
            		<div class="checky">
                		<label class="checkbox"><@spring.formCheckbox "oAuthRegistrationForm.newFeatureInformationRequested"/>Send me notifications about changes to my ORCID Record.</label>
        			</div>
        		</div>
        		<div>
            		<div class="checky">
                		<label class="checkbox"><@spring.formCheckbox "oAuthRegistrationForm.relatedProductsServiceInformationRequested"/>Send me information about events ORCID is sponsoring and ORCID news.</label>
        			</div>
        		</div>                    		                                                                    
        		<div style="margin-top: 20px;">
        		    <label>Terms of Use <span class="required">*</span></label>
            		<div class="checky">
                		<label class="checkbox"><@spring.formCheckbox "oAuthRegistrationForm.termsAccepted"/>I consent to the <a href="${aboutUri}/privacy-policy">privacy policy</a> and <a href="${aboutUri}/legal">terms and conditions</a> of use, including allowing those who access the database to make commercial uses of the public data.</label>
                		<@spring.showErrors "<br/>" "orcid-error"/>        
                	</div>
        		</div>
            </fieldset>
            <button class="btn btn-large btn-primary" type="submit">Register</button>
        </form>
    </div>


