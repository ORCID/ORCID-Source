<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->

<script type="text/ng-template" id="edit-emails">
	<!-- Email edit -->
	<td colspan="2" ng-show="showEditEmail || emailSrvc.popUp" ng-cloak>
	    <div class="editTablePadCell35" ng-controller="EmailEditCtrl">
	        <!-- we should never see errors here, but just to be safe -->
	        <div class="orcid-error" ng-show="emailSrvc.emails.errors.length > 0">
	        	<span ng-repeat='error in emailSrvc.emails.errors' ng-bind-html="error"></span>
	        </div>
	        <!-- Start -->
	        <div class="row">
	            <strong class="green">${springMacroRequestContext.getMessage("manage.email.my_email_addresses")}</strong>
	        </div>			
	        <!-- Email table -->
	        <div class="table-responsive">
	            <table class="table">
	                <tr ng-repeat="email in emailSrvc.emails.emails | orderBy:['value']" class="data-row-group">
	                    <!-- Primary Email -->
	                    <td ng-class="{primaryEmail:email.primary}" ng-bind="email.value" class="col-md-3 col-xs-12 email">
	                    </td>
	                    <!-- Set Primary options -->
	                    <td>
	                        <span ng-hide="email.primary"> <a href=""
	                            ng-click="emailSrvc.setPrimary(email)">${springMacroRequestContext.getMessage("manage.email.set_primary")}</a>
	                        </span> <span ng-show="email.primary" class="muted" style="color: #bd362f;">
	                        	${springMacroRequestContext.getMessage("manage.email.primary_email")}
	                        </span>
	                    </td>
	                    <td>
	                        <select ng-change="emailSrvc.saveEmail()" ng-model="email.current">
	                            <option value="true" ng-selected="email.current == true"><@orcid.msg 'manage.email.current.true' /></option>
	                            <option value="false" ng-selected="email.current == false"><@orcid.msg 'manage.email.current.false' /></option>
	                        </select>
	                    </td>
	                    <td class="email-verified">
	                        <span ng-hide="email.verified" class="left">
	                        <a ng-click="verifyEmail(email)">${springMacroRequestContext.getMessage("manage.email.verify")}</a></span>
	                        <span ng-show="email.verified" class="left">${springMacroRequestContext.getMessage("manage.email.verified")}</span>
	                    </td>
	                    <td width="26">
	                        <a href="" class="glyphicon glyphicon-trash grey"
	                            ng-show="email.primary == false"
	                            ng-click="confirmDeleteEmail(email)"></a>
	                    </td>
	                    <td width="100" style="padding-top: 0;">
	                        <div class="emailVisibility" style="float: right;">
	                            <@orcid.privacyToggle3
	                            angularModel="email.visibility"
	                            questionClick="toggleClickPrivacyHelp(email.value)"
	                            clickedClassCheck="{'popover-help-container-show':privacyHelp[email.value]==true}" 
	                            publicClick="setPrivacy(email, 'PUBLIC', $event)" 
	                            limitedClick="setPrivacy(email, 'LIMITED', $event)" 
	                            privateClick="setPrivacy(email, 'PRIVATE', $event)" 
	                            elementId="email.value" />    
	                        </div>
	                    </td>
	                </tr>
	            </table>
	        </div>
	        <!-- End Email table -->
			<div id="addEmailNotAllowed" ng-show="isPassConfReq" ng-cloak>
				${springMacroRequestContext.getMessage("manage.add_another_email.not_allowed")}
			</div>	        
	        <div class="row bottom-row" ng-hide="isPassConfReq" ng-cloak>
	            <div class="col-md-12 add-email">
	                <input type="email" placeholder="${springMacroRequestContext.getMessage("manage.add_another_email")}"
	                ng-enter="checkCredentials()" class="input-xlarge inline-input" ng-model="emailSrvc.inputEmail.value"
	                required /> <span
	                    ng-click="checkCredentials()" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>
	                <span class="orcid-error"
	                    ng-show="emailSrvc.inputEmail.errors.length > 0"> <span
	                    ng-repeat='error in emailSrvc.inputEmail.errors'
	                    ng-bind-html="error"></span>
	                </span>
	            </div>
	            <div class="col-md-12">
	                <p style="line-height: 12px;">
	                    <small class="italic">
	                    ${springMacroRequestContext.getMessage("manage.verificationEmail.1")} <a href="${aboutUri}/content/orcid-terms-use" target="_blank">${springMacroRequestContext.getMessage("manage.verificationEmail.2")}</a>${springMacroRequestContext.getMessage("manage.verificationEmail.3")}
	                    </small>
	                </p>
	            </div>
	        </div>

			<div ng-controller="EmailFrequencyCtrl" ng-show="notificationsEnabled" ng-cloak>
				<div class="row bottomBuffer">
    				<strong class="green">${springMacroRequestContext.getMessage("manage.email.email_frequency")}</strong>
    			</div>
    			
				<div class="control-group">
    				<p>${springMacroRequestContext.getMessage("manage.send_email_to_primary_1")} <a href="${baseUri}/inbox" target="_blank">${springMacroRequestContext.getMessage("manage.send_email_to_primary_2")}</a>${springMacroRequestContext.getMessage("manage.send_email_to_primary_3")}</p>
        			<form class="form-inline">
        				<div class="form-group">
            				<div class="input-group">
                				<select id="sendEmailFrequencyDays" name="sendEmailFrequencyDays" class="input-xlarge" ng-model="prefsSrvc.prefs.sendEmailFrequencyDays" ng-change="prefsSrvc.clearMessage()">
                    				<#list sendEmailFrequencies?keys as key>
                        				<option value="${key}" ng-selected="prefsSrvc.prefs.sendEmailFrequencyDays === ${key}">${sendEmailFrequencies[key]}</option>
                        			</#list>
                    			</select>
                			</div>
            			</div>
            			<button ng-click="prefsSrvc.savePrivacyPreferences()" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.send_email_frequency_save")}</button>
            			<small class="green" ng-show="prefsSrvc.saved">${springMacroRequestContext.getMessage("manage.send_email_frequency_saved")}</small>    
        			</form>
    			</div>

				<div>
					<p>${springMacroRequestContext.getMessage("manage.send_email_to_primary_4")} {{emailSrvc.primaryEmail.value}}${springMacroRequestContext.getMessage("manage.send_email_to_primary_5")}</p>
            		<p>${springMacroRequestContext.getMessage("manage.service_announcements")}</p>
	          		<p style="line-height: 12px;"><small class="italic">${springMacroRequestContext.getMessage("manage.service_announcements.note")}</small>
            		</p>
    			</div>


			</div>


			
	    </div><!--END .editTablePadCell35 -->
	</td>
</script>