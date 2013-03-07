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
<@base>
<div class="colorbox-content manage-bio" id="manage-bio">
    <form id="bio-settings-form" class="" action="<@spring.url '/account/save-bio-settings'/>" method="post" autocomplete="off">
        <div class="row">
            <div class="span12">
                <h1 class="lightbox-title pull-left">Edit Personal Information</h1><a class="btn pull-right close-button">X</a>
            </div>
        </div>
        <@spring.bind "changePersonalInfoForm.*" /> 
        <#if spring.status.error>
            <div class="alert alert-error">
                <ul class="validationerrors">
                    <#list spring.status.errors.fieldErrors?sort as error> <li>${springMacroRequestContext.getMessage(error, false)}</li> </#list>
                </ul>
            </div>
        </#if>
        <#if duplicateEmailFound?? && duplicateEmailFound>
            <div class="alert alert-error">
                    <strong><@orcid.unescapedMessage "orcid.frontend.verify.duplicate_email"/></strong>
            </div>
        </#if>  
        <#if verificationEmailSent?? && verificationEmailSent>
            <div class="alert alert-success">
                    <strong><@spring.message "orcid.frontend.verify.email_sent"/></strong>
            </div>
        </#if>  
        <#if emailUpdated?? && emailUpdated>
            <div class="alert alert-success">
                <strong><@spring.message "orcid.frontend.web.email_updated"/></strong>
            </div>
        </#if>  
        <#if changesSaved?? && changesSaved>
            <div class="alert alert-success">
                <strong><@spring.message "orcid.frontend.web.details_saved"/></strong>
            </div>
        </#if>
        <div class="row">
            <div class="span6">
            		
            	    <h3>Names</h3>		
                    <div class="control-group"> 
             			<label for="firstName">First Name</label>             			
             			<div class="relative"><@spring.formInput "changePersonalInfoForm.firstName" 'class="input-xlarge"'/>
             			<a href="http://support.orcid.org/knowledgebase/articles/142948-names-in-the-orcid-registry" target="_blank"><i class="icon-question-sign"></i></a>
             			</div>
             		</div>
                    <div class="control-group"> 
             			<label for="lastName">Last Name</label>
             			<div class="relative"><@spring.formInput "changePersonalInfoForm.lastName" 'class="input-xlarge"'/></div>
             		</div>
             		<div class="control-group"> 
                        <label for="creditName">Published Name</label>
             			<div class="relative">
             			    <@spring.formInput "changePersonalInfoForm.creditName" 'class="input-xlarge"'/>
             			    <label class="visibility-lbl">
             			        Visibility
                                <@spring.formSingleSelect "changePersonalInfoForm.creditNameVisibility", visibilities />    
                            </label>
                            <@orcid.privacy "" changePersonalInfoForm.creditNameVisibility! />
             			</div>
             		</div>
             		<div class="control-group">
                        <label for="creditName">Other Names</label>
                        <div class="relative">
             			<@spring.formInput "changePersonalInfoForm.otherNamesDelimited" 'class="input-xlarge"'/>
             			<label class="visibility-lbl">
             			    Visibility
             			    <@spring.formSingleSelect "changePersonalInfoForm.otherNamesVisibility", visibilities />
             			</label>
                        <@orcid.privacy "" changePersonalInfoForm.otherNamesVisibility! />
             			</div>
             		</div>
                
                <h3>About Me</h3>
                <div class="control-group">
                    <label for="biography">Biography</label>
                    <div class="relative"><@spring.formTextarea "changePersonalInfoForm.biography" 'class="input-xlarge" maxlength="1000"'/></div>
                </div>
             			
             	<div class="control-group">
                    <label for="keywordsDelimited">Keywords</label>
                	<div class="relative">
                	    <@spring.formInput "changePersonalInfoForm.keywordsDelimited", 'class="input-xlarge"'/>
                	    <label class="visibility-lbl">
                            Visibility
                            <@spring.formSingleSelect "changePersonalInfoForm.keywordsVisibility", visibilities />
                        </label>
                        <@orcid.privacy "" changePersonalInfoForm.keywordsVisibility! />
                	</div>
                </div>
                
             	<div class="control-group">
                    <label for="biography">Country</label>
                    <div class="relative">
                        <@spring.formSingleSelect "changePersonalInfoForm.isoCountryCode", isoCountries />
                        <label class="visibility-lbl">
                            Visibility
                            <@spring.formSingleSelect "changePersonalInfoForm.isoCountryVisibility", visibilities />
                        </label>
                        <@orcid.privacy "" changePersonalInfoForm.isoCountryVisibility! />
                    </div>
                </div>
                
            </div>
            <div class="span6">
             	
                <#if changePersonalInfoForm.savedResearcherUrls?? && changePersonalInfoForm.savedResearcherUrls.researcherUrl??>
                    <div class="websites-vis">
                        <label class="visibility-lbl">
                            Visibility
                            <@spring.formSingleSelect "changePersonalInfoForm.websiteUrlVisibility", visibilities />
                        </label>
                        <@orcid.privacy "" changePersonalInfoForm.websiteUrlVisibility! />
                    </div>
                 	<h3>Websites</h3>
             		<#list changePersonalInfoForm.savedResearcherUrls.researcherUrl as savedResearcherUrl>     		  	
             			<p>
             		  		<@spring.formHiddenInput "changePersonalInfoForm.savedResearcherUrls.researcherUrl[${savedResearcherUrl_index}].urlName.content"/>
             		  		<@spring.formHiddenInput "changePersonalInfoForm.savedResearcherUrls.researcherUrl[${savedResearcherUrl_index}].url.value"/>
                      		<#if savedResearcherUrl.urlName.content = ''>
                      		    <a href="${savedResearcherUrl.url.value}">${savedResearcherUrl.url.value}</a>
                      		<#else> 
                      		    ${savedResearcherUrl.urlName.content} (<a href="${savedResearcherUrl.url.value}">${savedResearcherUrl.url.value}</a>)
                      		</#if>
                      		<button class="btn btn-link delete-url">Remove</button>		  		    		  		     		  		
             			</p>     		    
                    </#list>
             	</#if>
               	<label for="websiteUrlText">Website</label>
             	<div class="control-group form-inline websites">
             	 	<@spring.formInput "changePersonalInfoForm.websiteUrlText", 'class="input-xlarge"  placeholder="Description"'/>
         			<@spring.formInput "changePersonalInfoForm.websiteUrl", 'class="input-xlarge" placeholder="URL"'/>
             	</div>
             	 <#if changePersonalInfoForm.externalIdentifiers?? && changePersonalInfoForm.externalIdentifiers.externalIdentifier?size != 0>
                 	<h4>External Identifiers (visibility is ${(changePersonalInfoForm.externalIdentifiers.visibility.value())!})</h4>
                 	<table class="table">     		
                 		<th>Name</th>
                 		<th>Ref</th>   		
                 		<#list changePersonalInfoForm.externalIdentifiers.externalIdentifier as externalIdentifier>     		  	
                 			<tr>
                 				<td>${(externalIdentifier.externalIdCommonName.content)!"Information not provided"}</td>
            					<td><a href="${(externalIdentifier.externalIdUrl.value)!}" target="_blank">${(externalIdentifier.externalIdReference.content)!"Information not provided"}</a></td>
                 			</tr>     		    
                        </#list>
                    </table>                     
             	</#if>
             	<h3>Email</h3>
             	<#if (changePersonalInfoForm.emailVerified)??>
                 	<#if !changePersonalInfoForm.emailVerified >
                	 	Your email has not been verified - please click <a href="<@spring.url '/manage/verify-email'/>"/>here</a> to resend a verification email. 
                	 </#if>
                 	<#if emailAddressUpdated?? && emailAddressUpdated>
                		<div class="alert alert-success">
                    		<strong><@spring.message "orcid.frontend.web.email_updated"/></strong>
                		</div>
                	</#if>
            	</#if>	
            	<div class="control-group">
                    <label for="websiteUrlText">Email Address</label>
                    <div class="relative">
                        <@spring.formInput "changePersonalInfoForm.email", 'class="input-xlarge"'/>
                        <label class="visibility-lbl">
                            Visibility
                            <@spring.formSingleSelect "changePersonalInfoForm.emailVisibility", visibilities />
                        </label>
                        <@orcid.privacy "" changePersonalInfoForm.emailVisibility! />
                    </div>
                </div>
             	 		
             	<div class="controls save-btns pull-right">
                    <button id="bottom-submit-affiliates" class="btn btn-primary" type="submit">Save changes</button>
                    <button id="bottom-clear-affiliates" class="btn close-button" type="reset">Cancel</button>
                </div>
            </div>
        </div>
    </form>
</div>
</@base>

