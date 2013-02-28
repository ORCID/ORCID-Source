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
<@protected>
   <div class="row">
        <div class="span3"></div>
        <div class="span9">
 
<h2>Claim your ORCID Record</h2>
<#if (profile.orcidHistory.source.sourceName.content)??>
	<p>
	    To make creating your ORCID record easier, ${profile.orcidHistory.source.sourceName.content} has already entered information into your account. Please review it.
	    <br/>
	    <br/>
	</p>
</#if>
<@spring.bind "claimForm.*" />
<#if spring.status.error>
<div class="alert alert-error">
    <ul class="validationerrors">
        <#list spring.status.errorMessages?sort as error> <li>${error}</li> </#list>
    </ul>
</div>
</#if>
<form id="claim-form" class="form-sign-up" action="<@spring.url '/claim/${encryptedEmail}'/>" method="post" autocomplete="off">
                    <div class="control-group password-group">
                        <label for="password" class="control-label">Password</label>
                        <div class="relative">
                            <@spring.formPasswordInput "claimForm.password" 'class="input-xlarge password-strength" data-validate="{required:true}"'/>
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
                            <@spring.formPasswordInput "claimForm.retypedPassword" 'class="input-xlarge" data-validate="{required:true, equalTo:\'#password\'}"'/>
                            <span class="required">*</span>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </div>
                    </div>

                    <div style="margin-bottom: 20px; margin-top: 10px;">
                        <label class="privacy-toggle-lbl">Default privacy for new works</label>
                        <label class="visibility-lbl">
                            <@spring.formSingleSelect "claimForm.workVisibilityDefault", visibilities />
                        </label>
                        <@orcid.privacy "" claimForm.workVisibilityDefault 'btn-group privacy-group'/>
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
                            <@spring.formCheckbox "claimForm.sendOrcidChangeNotifcations"/>
                            Send me notifications about changes to my ORCID Record.
                        </label>
                        <label class="checkbox">
                            <@spring.formCheckbox "claimForm.sendOrcidNews"/>
                            Send me information about events ORCID is sponsoring and ORCID news.
                        </label>
                        </div>
                        </div>
                   <div>
                    <div class="relative"  style="margin-bottom: 15px;">
                        <label>Terms of Use <span class="required">*</span></label>
                        <label class="checkbox" style="width: 480px;">
                            <@spring.formCheckbox "claimForm.acceptTermsAndConditions"/>
                            I consent to the <a href="${aboutUri}/footer/privacy-policy">privacy policy</a> and <a href="${aboutUri}/content/orcid-terms-use">terms and conditions</a> of use, including allowing those who access the database to make commercial uses of the public data.</p>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </label>
                    </div>
                </div>   
                <div class="relative">
                      <button type="submit" class="btn btn-primary">Claim</button>
                </div>           
 
  </form>
</div>
</div>
</@protected>
