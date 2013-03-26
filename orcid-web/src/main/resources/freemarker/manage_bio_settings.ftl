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
                <h1 class="lightbox-title pull-left">${springMacroRequestContext.getMessage("manage_bio_settings.editpersonalinformation")}</h1><a class="btn pull-right close-button">X</a>
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
            		
            	    <h3>${springMacroRequestContext.getMessage("manage_bio_settings.h3names")}</h3>		
                    <div class="control-group"> 
             			<label for="firstName">${springMacroRequestContext.getMessage("manage_bio_settings.labelfirstname")}</label>             			
             			<div class="relative"><@spring.formInput "changePersonalInfoForm.firstName" 'class="input-xlarge"'/>
             			<a href="http://support.orcid.org/knowledgebase/articles/142948-names-in-the-orcid-registry" target="_blank"><i class="icon-question-sign"></i></a>
             			</div>
             		</div>
                    <div class="control-group"> 
             			<label for="lastName">${springMacroRequestContext.getMessage("manage_bio_settings.labellastname")}</label>
             			<div class="relative"><@spring.formInput "changePersonalInfoForm.lastName" 'class="input-xlarge"'/></div>
             		</div>
             		<div class="control-group"> 
                        <label for="creditName">${springMacroRequestContext.getMessage("manage_bio_settings.labelpublishedname")}</label>
             			<div class="relative">
             			    <@spring.formInput "changePersonalInfoForm.creditName" 'class="input-xlarge"'/>
             			    <label class="visibility-lbl">
             			        ${springMacroRequestContext.getMessage("manage_bio_settings.labelvisibility")}
                                <@spring.formSingleSelect "changePersonalInfoForm.creditNameVisibility", visibilities />    
                            </label>
                            <@orcid.privacy "" changePersonalInfoForm.creditNameVisibility! />
             			</div>
             		</div>
             		<div class="control-group">
                        <label for="creditName">${springMacroRequestContext.getMessage("manage_bio_settings.othernames")}</label>
                        <div class="relative">
             			<@spring.formInput "changePersonalInfoForm.otherNamesDelimited" 'class="input-xlarge"'/>
             			<label class="visibility-lbl">
             			    ${springMacroRequestContext.getMessage("manage_bio_settings.labelvisibility")}
             			    <@spring.formSingleSelect "changePersonalInfoForm.otherNamesVisibility", visibilities />
             			</label>
                        <@orcid.privacy "" changePersonalInfoForm.otherNamesVisibility! />
             			</div>
             		</div>
                
                <h3>${springMacroRequestContext.getMessage("manage_bio_settings.h3aboutme")}</h3>
                <div class="control-group">
                    <label for="biography">${springMacroRequestContext.getMessage("manage_bio_settings.labelbiography")}</label>
                    <div class="relative"><@spring.formTextarea "changePersonalInfoForm.biography" 'class="input-xlarge" maxlength="1000"'/></div>
                </div>
             			
             	<div class="control-group">
                    <label for="keywordsDelimited">${springMacroRequestContext.getMessage("manage_bio_settings.labelkeywords")}</label>
                	<div class="relative">
                	    <@spring.formInput "changePersonalInfoForm.keywordsDelimited", 'class="input-xlarge"'/>
                	    <label class="visibility-lbl">
                            ${springMacroRequestContext.getMessage("manage_bio_settings.labelvisibility")}
                            <@spring.formSingleSelect "changePersonalInfoForm.keywordsVisibility", visibilities />
                        </label>
                        <@orcid.privacy "" changePersonalInfoForm.keywordsVisibility! />
                	</div>
                </div>
                
             	<div class="control-group">
                    <label for="biography">${springMacroRequestContext.getMessage("manage_bio_settings.labelcountry")}</label>
                    <div class="relative">
                        <@spring.formSingleSelect "changePersonalInfoForm.isoCountryCode", isoCountries />
                        <label class="visibility-lbl">
                            ${springMacroRequestContext.getMessage("manage_bio_settings.labelvisibility")}
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
                            ${springMacroRequestContext.getMessage("manage_bio_settings.labelvisibility")}
                            <@spring.formSingleSelect "changePersonalInfoForm.websiteUrlVisibility", visibilities />
                        </label>
                        <@orcid.privacy "" changePersonalInfoForm.websiteUrlVisibility! />
                    </div>
                 	<h3>${springMacroRequestContext.getMessage("manage_bio_settings.h3websites")}</h3>
             		<#list changePersonalInfoForm.savedResearcherUrls.researcherUrl as savedResearcherUrl>     		  	
             			<p>
             		  		<@spring.formHiddenInput "changePersonalInfoForm.savedResearcherUrls.researcherUrl[${savedResearcherUrl_index}].urlName.content"/>
             		  		<@spring.formHiddenInput "changePersonalInfoForm.savedResearcherUrls.researcherUrl[${savedResearcherUrl_index}].url.value"/>
                      		<#if savedResearcherUrl.urlName.content = ''>
                      		    <a href="${savedResearcherUrl.url.value}">${savedResearcherUrl.url.value}</a>
                      		<#else> 
                      		    ${savedResearcherUrl.urlName.content} (<a href="${savedResearcherUrl.url.value}">${savedResearcherUrl.url.value}</a>)
                      		</#if>
                      		<button class="btn btn-link delete-url">${springMacroRequestContext.getMessage("manage_bio_settings.btnremove")}</button>		  		    		  		     		  		
             			</p>     		    
                    </#list>
             	</#if>
               	<label for="websiteUrlText">${springMacroRequestContext.getMessage("manage_bio_settings.labelwebsite")}</label>
             	<div class="control-group form-inline websites">
             	 	<@spring.formInput "changePersonalInfoForm.websiteUrlText", 'class="input-xlarge"  placeholder="Description"'/>
         			<@spring.formInput "changePersonalInfoForm.websiteUrl", 'class="input-xlarge" placeholder="URL"'/>
             	</div>
             	             	             	
             	
             	 <#if changePersonalInfoForm.externalIdentifiers?? && changePersonalInfoForm.externalIdentifiers.externalIdentifier?size != 0>
                 	<h4>External Identifiers (visibility is ${(changePersonalInfoForm.externalIdentifiers.visibility.value())!})</h4>
                 	<table class="table">     		
                 		<th>${springMacroRequestContext.getMessage("manage_bio_settings.thname")}</th>
                 		<th>${springMacroRequestContext.getMessage("manage_bio_settings.thref")}</th>   		
                 		<#list changePersonalInfoForm.externalIdentifiers.externalIdentifier as externalIdentifier>     		  	
                 			<tr>
                 				<td>${(externalIdentifier.externalIdCommonName.content)!"${springMacroRequestContext.getMessage('manage_bio_settings.informationnotprovided')}"}</td>
            					<td><a href="${(externalIdentifier.externalIdUrl.value)!}" target="_blank">${(externalIdentifier.externalIdReference.content)!"${springMacroRequestContext.getMessage('manage_bio_settings.informationnotprovided')}"}</a></td>
            					<td>
            						<@spring.formHiddenInput "changePersonalInfoForm.externalIdentifiers.externalIdentifier[${externalIdentifier_index}].orcid.value" />
            						<@spring.formHiddenInput "changePersonalInfoForm.externalIdentifiers.externalIdentifier[${externalIdentifier_index}].externalIdReference.content" />
            						<@spring.formHiddenInput "changePersonalInfoForm.externalIdentifiers.externalIdentifier[${externalIdentifier_index}].externalIdOrcid.value" />
            						<@spring.formHiddenInput "changePersonalInfoForm.externalIdentifiers.externalIdentifier[${externalIdentifier_index}].externalIdCommonName.content" />
            						<@spring.formHiddenInput "changePersonalInfoForm.externalIdentifiers.externalIdentifier[${externalIdentifier_index}].externalIdUrl.value" />
            						<button class="btn delete-external-identifier">${springMacroRequestContext.getMessage("manage_bio_settings.btnremove")}</button>
            					</td>
                 			</tr>     		    
                        </#list>
                    </table>                     
             	</#if>
             	
             	<h3>${springMacroRequestContext.getMessage("manage_bio_settings.h3email")}</h3>
             	<#if (changePersonalInfoForm.emailVerified)??>
                 	<#if !changePersonalInfoForm.emailVerified >
                	 	${springMacroRequestContext.getMessage("manage_bio_settings.emailnotverified")} <a href="<@spring.url '/manage/verify-email'/>"/>${springMacroRequestContext.getMessage("manage_bio_settings.here")}</a> ${springMacroRequestContext.getMessage("manage_bio_settings.toresend")}
                	 </#if>
                 	<#if emailAddressUpdated?? && emailAddressUpdated>
                		<div class="alert alert-success">
                    		<strong><@spring.message "orcid.frontend.web.email_updated"/></strong>
                		</div>
                	</#if>
            	</#if>	
            	<div class="control-group">
                    <label for="websiteUrlText">${springMacroRequestContext.getMessage("manage_bio_settings.emailaddress")}</label>
                    <div class="relative">
                        <@spring.formInput "changePersonalInfoForm.email", 'class="input-xlarge"'/>
                        <label class="visibility-lbl">
                            ${springMacroRequestContext.getMessage("manage_bio_settings.labelvisibility")}
                            <@spring.formSingleSelect "changePersonalInfoForm.emailVisibility", visibilities />
                        </label>
                        <@orcid.privacy "" changePersonalInfoForm.emailVisibility! />
                    </div>
                </div>
             	 		
             	<div class="controls save-btns pull-right">
                    <button id="bottom-submit-affiliates" class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("manage_bio_settings.btnsavechanges")}</button>
                    <button id="bottom-clear-affiliates" class="btn close-button" type="reset">${springMacroRequestContext.getMessage("manage_bio_settings.btncancel")}</button>
                </div>
            </div>
        </div>
    </form>
</div>
<div id="confirm-dialog"></div>
</@base>

