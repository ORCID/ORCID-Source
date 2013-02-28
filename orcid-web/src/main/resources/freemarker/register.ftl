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
            <h2>Register for an ORCID iD</h2>
            <p>ORCID provides a persistent digital identifier that distinguishes you from 
every other researcher and, through integration in key research workflows 
such as manuscript and grant submission, supports automated linkages 
between you and your professional activities ensuring that your work is 
recognized.<br /><br /></p>
                <form id="self-reg-form" class="form-sign-up" name="selfRegForm" action="register" method="post">
                    <#include "/common/browser-checks.ftl" />
                    <div>
                        <label for="givenNames" class="control-label">First name</label>
                        <div class="relative">
                            <@spring.formInput "registrationForm.givenNames" 'class="input-xlarge" name="firstName" data-validate="{required:true}"'/>
                            <span class="required">*</span>
                            <a href="${knowledgeBaseUri}/articles/142948-names-in-the-orcid-registry" target="_blank"><i class="icon-question-sign"></i></a>                            
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </div>
                    </div>
                    <div>
                        <label for="familyName" class="control-label">Last name</label>
                        <div class="relative">
                            <@spring.formInput "registrationForm.familyName" 'class="input-xlarge" name="user.lastName" data-validate="{required:true}"'/>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </div>
                    </div>               
                    <div>
                        <label for="email" class="control-label">Email</label>
                        <div class="relative">
                            <@spring.formInput "registrationForm.email" 'class="input-xlarge" name="user.email" data-validate="{required:true, email:true}"'/>
                            <span class="required">*</span>
                            <@orcid.showErrorsUnescaped/>
                            <@orcid.showErrorsUnescapedForPath "email"/>
                        </div>
                    </div>
                    <div>
                        <label for="email" class="control-label">Re-enter email</label>
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
                        <label for="password" class="control-label">Password</label>
                        <div class="relative">
                            <@spring.formPasswordInput "registrationForm.password" 'class="input-xlarge password-strength" data-validate="{required:true}"'/>
                            <span class="required">*</span>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                            <a class="password-info" href="#"><i class="icon-question-sign"></i></a>
                            <div class="popover bottom password-details">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <div class="help-block">
                                        <p>Must be 8 or more characters and contain:</p>
                                        <ul>
                                            <li>at least 1 numeral: 0 - 9</li>
                                            <li>at least 1 of the following:</li>
                                            <ul>
                                            	<li>alpha character, case-sensitive a-Z</li>
                                            	<li>any of the following symbols:<br /> ! @ # $ % ^ * ( ) ~ `{ } [ ] | \ &amp; _</li>
                                            </ul>
                                            <li>optionally the space character, i.e ' ' and other punctuation such as . , ;</li>
                                        </ul>                                       
                                        <p>Example: sun% moon2</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                      <div>
                        <label for="password" class="control-label">Confirm password</label>
                        <div class="relative">
                            <@spring.formPasswordInput "registrationForm.retypedPassword" 'class="input-xlarge" data-validate="{required:true, equalTo:\'#password\'}"'/>
                            <span class="required">*</span>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </div>
                    </div>
                    <div style="margin-bottom: 20px; margin-top: 10px;">
                        <label class="privacy-toggle-lbl">Default privacy for new works</label>
                        <label class="visibility-lbl">
                            <@spring.formSingleSelect "registrationForm.workVisibilityDefault", visibilities />
                        </label>
                        <@orcid.privacy "" registrationForm.workVisibilityDefault 'btn-group privacy-group'/>
                        <div  style="width: 480px;">
                            <label>
                                You will be able to add information to your ORCID record about work that you have done. This privacy setting will apply to all works/publications added to your record. You can change this any time.
                            </label>
                        </div>
                    </div>                    
                <div style="margin-bottom: 15px;">
                    <div class="relative">
                        <label>Notification Email</label>
                        <label class="checkbox">
                            <@spring.formCheckbox "registrationForm.sendOrcidChangeNotifcations"/>
                            Send me notifications about changes to my ORCID Record.
                        </label>
                        <label class="checkbox">
                            <@spring.formCheckbox "registrationForm.sendOrcidNews"/>
                            Send me information about events ORCID is sponsoring and ORCID news.
                        </label>
                        </div>
                        </div>
                   <div>
                    <div class="relative"  style="margin-bottom: 15px;">
                        <label>Terms of Use <span class="required">*</span></label>
                        <label class="checkbox" style="width: 480px;">
                            <@spring.formCheckbox "registrationForm.acceptTermsAndConditions"/>
                            I consent to the <a href="${aboutUri}/footer/privacy-policy">privacy policy</a> and <a href="${aboutUri}/content/orcid-terms-use">terms and conditions</a> of use, including allowing those who access the database to make commercial uses of the public data.</p>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </label>
                    </div>
                </div>   
                <div class="relative">
                      <button type="submit" class="btn btn-primary">Register</button>
                </div>           
            </form>
        </div>
    </div>
</@public>