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
 
<h2>${springMacroRequestContext.getMessage("claim.claimyourrecord")}</h2>
<#if (profile.orcidHistory.source.sourceName.content)??>
	<p>
	    ${springMacroRequestContext.getMessage("claim.createyourrecordeasier")} ${profile.orcidHistory.source.sourceName.content} ${springMacroRequestContext.getMessage("claim.alreadyenterinfomation")}
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

				<#include "/common/browser-checks.ftl" />
				<div ng-app="orcidApp" ng-controller="ClaimCtrl">
					<div>
						<!-- span class="orcid-error" ng-show="register.errors.length > 0">
							<div ng-repeat='error in register.errors' ng-bind-html-unsafe="error"></div>
					   	</span -->
										
	                    <div class="control-group">
	                        <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelpassword")}</label>
	                        <div class="relative">
	                            <input type="password" name="password" class="input-xlarge" ng-model="register.password.value" ng-change="fieldValidate('Password')"/>
	                            <span class="required" ng-class="isValidClass(register.password)">*</span>
					   			<div class="popover-help-container" style="display: inline; position: relative;">
	                                <a href="javascript:void(0);"><i class="icon-question-sign"></i></a>
	                                <div id="name-help" class="popover bottom">
								        <div class="arrow"></div>
								        <div class="popover-content">
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
	                                        <br />              
	                                        <p><strong>${springMacroRequestContext.getMessage("password_one_time_reset.examplesunmoon")}</strong></p>
								        </div>                
								    </div>
	                            </div>
	                            <span class="orcid-error" ng-show="register.password.errors.length > 0">
									<div ng-repeat='error in register.password.errors' ng-bind-html-unsafe="error"></div>
					   			</span>
	                        </div>
	                    </div>
	                    <div>
	                        <label class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.labelconfirmpassword")}</label>
	                        <div class="relative">
	                            <input type="password" name="confirmPassword" class="input-xlarge" ng-model="register.passwordConfirm.value" ng-change="fieldValidate('PasswordConfirm')"/>
	                            <span class="required" ng-class="isValidClass(register.passwordConfirm)">*</span>
	                            <span class="orcid-error" ng-show="register.passwordConfirm.errors.length > 0">
									<div ng-repeat='error in register.passwordConfirm.errors' ng-bind-html-unsafe="error"></div>
					   			</span>
	                        </div>
	                    </div>
	     				<div style="margin-bottom: 20px; margin-top: 10px;">
	                        <label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("register.labelDefaultprivacyfornewworks")}</label>
	                    	<div class="relative">
									<ul class="privacyToggle">
				   						<li class="publicActive" ng-class="{publicInActive: register.workVisibilityDefault.visibility != 'PUBLIC'}"><a href="" title="PUBLIC" ng-click="updateWorkVisibilityDefault('PUBLIC', $event)"></a></li>
				   						<li class="limitedActive" ng-class="{limitedInActive: register.workVisibilityDefault.visibility != 'LIMITED'}"><a href="" title="LIMITED" ng-click="updateWorkVisibilityDefault('LIMITED', $event)"></a></li>
				   						<li class="privateActive" ng-class="{privateInActive: register.workVisibilityDefault.visibility != 'PRIVATE'}"><a href="" title="PRIVATE" ng-click="updateWorkVisibilityDefault('PRIVATE', $event)"></a></li>
				   					</ul>
				   					<div class="privacyLegendHide" style="position: absolute; left: 110px; top: 5px;">
					   				    <a href="javascript:void(0);"><i class="icon-question-sign"></i></a>
					   				    <div class="privacyLegend"></div>
					   				</div>
				   			</div>
	                    </div>                    
		                <div style="margin-bottom: 15px;">
		                    <div class="relative">
		                        <label>${springMacroRequestContext.getMessage("claim.notificationemail")}</label>
		                        <label class="checkbox">
		                            <input type="checkbox" name="sendOrcidChangeNotifications" ng-model="register.sendChangeNotifications.value"/>
		                            ${springMacroRequestContext.getMessage("register.labelsendmenotifications")}
		                        </label>
		                        <label class="checkbox">
		                            <input type="checkbox" name="sendOrcidNews" ng-model="register.sendOrcidNews.value"/>
		                            ${springMacroRequestContext.getMessage("register.labelsendinformation")}
		                        </label>
		                     </div>
						</div>
	                    <div>
		                    <div class="relative"  style="margin-bottom: 15px;">
		                        <label>${springMacroRequestContext.getMessage("register.labelTermsofUse")} <span class="required"  ng-class="{'text-error':register.termsOfUse.value == false}">*</span></label>
		                        <label class="checkbox" style="width: 480px;">
		                        <input type="checkbox" name="acceptTermsAndConditions" ng-model="register.termsOfUse.value"/>
		                        ${springMacroRequestContext.getMessage("register.labelconsent")} <a href="${aboutUri}/footer/privacy-policy?lang=${locale}" target="_blank">${springMacroRequestContext.getMessage("register.labelprivacypolicy")}</a> ${springMacroRequestContext.getMessage("register.labeland")} <a href="${aboutUri}/content/orcid-terms-use?lang=${locale}" target="_blank">${springMacroRequestContext.getMessage("register.labeltermsandconditions")}</a> ${springMacroRequestContext.getMessage("register.labelofuseinclude")}</p>
		                        </label>
		                        <span class="orcid-error" ng-show="register.termsOfUse.errors.length > 0">
									<div ng-repeat='error in register.termsOfUse.errors' ng-bind-html-unsafe="error"></div>
					   			</span>
		                    </div>
	                	</div>   
		                <div class="relative">
		                      <button type="submit" class="btn btn-primary" ng-click="postRegister()">${springMacroRequestContext.getMessage("claim.btnClaim")}</button>
		                </div>  
	                </div> 
				</div>



<form id="claim-form" class="form-sign-up" action="<@spring.url '/claim/${encryptedEmail}'/>" method="post" autocomplete="off">
                    <div class="control-group password-group">
                        <label for="password" class="control-label">${springMacroRequestContext.getMessage("claim.password")}</label>
                        <div class="relative">
                            <@spring.formPasswordInput "claimForm.password" 'class="input-xlarge password-strength" data-validate="{required:true}"'/>
                            <span class="required">*</span>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                            <a class="password-info" href="#"><i class="icon-question-sign"></i></a>
                            <div class="popover bottom password-details">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <div class="help-block">
                                        <p>${springMacroRequestContext.getMessage("claim.must8morecharacters")}</p>
                                        <ul>
                                            <li>${springMacroRequestContext.getMessage("claim.atleast09")}</li>
                                            <li>${springMacroRequestContext.getMessage("claim.atleastfollowing")}</li>
                                            <ul>
                                            	<li>${springMacroRequestContext.getMessage("claim.alphacharactercase")}</li>
                                            	<li>${springMacroRequestContext.getMessage("claim.anyofsymbols")}<br /> ! @ # $ % ^ * ( ) ~ `{ } [ ] | \ &amp; _</li>
                                            </ul>
                                            <li>${springMacroRequestContext.getMessage("claim.optionallyspacecharacter")}</li>
                                        </ul>                                       
                                        <p>${springMacroRequestContext.getMessage("claim.examplesunmoon")}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                      <div>
                        <label for="password" class="control-label">${springMacroRequestContext.getMessage("claim.confirmpassword")}</label>
                        <div class="relative">
                            <@spring.formPasswordInput "claimForm.retypedPassword" 'class="input-xlarge" data-validate="{required:true, equalTo:\'#password\'}"'/>
                            <span class="required">*</span>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </div>
                    </div>

                    <div style="margin-bottom: 20px; margin-top: 10px;">
                        <label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("claim.defaultprivacynewworks")}</label>
                        <label class="visibility-lbl">
                            <@spring.formSingleSelect "claimForm.workVisibilityDefault", visibilities />
                        </label>
                        <@orcid.privacy "" claimForm.workVisibilityDefault 'btn-group privacy-group'/>
                        <div  style="width: 480px;">
                            <label>
                                ${springMacroRequestContext.getMessage("claim.youwilladdinformation")}
                            </label>
                        </div>
                    </div>                    


               <div style="margin-bottom: 15px;">
                    <div class="relative">
                        <label>${springMacroRequestContext.getMessage("claim.notificationemail")}</label>
                        <label class="checkbox">
                            <@spring.formCheckbox "claimForm.sendOrcidChangeNotifcations"/>
                            ${springMacroRequestContext.getMessage("claim.sendmenotificationchange")}
                        </label>
                        <label class="checkbox">
                            <@spring.formCheckbox "claimForm.sendOrcidNews"/>
                            ${springMacroRequestContext.getMessage("claim.sendmeinformationabout")}
                        </label>
                        </div>
                        </div>
                   <div>
                    <div class="relative"  style="margin-bottom: 15px;">
                        <label>Terms of Use <span class="required">*</span></label>
                        <label class="checkbox" style="width: 480px;">
                            <@spring.formCheckbox "claimForm.acceptTermsAndConditions"/>
                            ${springMacroRequestContext.getMessage("claim.Iconsenttothe")} <a href="${aboutUri}/footer/privacy-policy?lang=${locale}">${springMacroRequestContext.getMessage("claim.privacypolicy")}</a> ${springMacroRequestContext.getMessage("claim.and")} <a href="${aboutUri}/content/orcid-terms-use?lang=${locale}">${springMacroRequestContext.getMessage("claim.termsandconditions")}</a> ${springMacroRequestContext.getMessage("claim.ofuseincluding")}</p>
                            <@spring.showErrors "<br/>" "orcid-error"/>
                        </label>
                    </div>
                </div>   
                <div class="relative">
                      <button type="submit" class="btn btn-primary">${springMacroRequestContext.getMessage("claim.btnClaim")}</button>
                </div>           
 
  </form>
</div>
</div>
</@protected>
