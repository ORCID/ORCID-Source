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
<@base>
    <div>
    	<img src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo">
		<br><br><br>
		<table class="table table-bordered settings-table" style="margin: 0px, padding:  0px;">
	        <tr>
	            <th><a name="editNotificationPreference"></a>${springMacroRequestContext.getMessage("manage.notification_preferences")}</th>
	            <td></td>
	        </tr>
	        <tr ng-controller="NotificationPreferencesCtrl" >
	            <td colspan="2">
	                <div class="editTablePadCell35">
	                    <h5>${springMacroRequestContext.getMessage("change_notification_preferences.changestitle")}</h5>
	                    <label class="checkbox"> <input type="checkbox"
	                        id="sendOrcidChangeNotifcations"
	                        name="sendOrcidChangeNotifcations"
	                        ng-model="prefsSrvc.prefs.sendChangeNotifications.value"
	                        ng-change="prefsSrvc.savePrivacyPreferences()" />
	                        ${springMacroRequestContext.getMessage("change_notification_preferences.sendnotification")}
	                    </label>
	                    <h5>${springMacroRequestContext.getMessage("change_notification_preferences.requeststitle")}</h5>
	                    <label class="checkbox"> <input type="checkbox"
	                        id="sendMemberUpdateRequests" name="sendMemberUpdateRequests"
	                        ng-model="prefsSrvc.prefs.sendMemberUpdateRequests"
	                        ng-change="prefsSrvc.savePrivacyPreferences()" />
	                        ${springMacroRequestContext.getMessage("change_notification_preferences.sendmemberupdaterequests")}
	                    </label>
	                    <h5>${springMacroRequestContext.getMessage("change_notification_preferences.newstitle")}</h5>
	                    <label class="checkbox"> <input type="checkbox"
	                        id="sendOrcidNews" name="sendOrcidNews"
	                        ng-model="prefsSrvc.prefs.sendOrcidNews.value"
	                        ng-change="prefsSrvc.savePrivacyPreferences()" />
	                        ${springMacroRequestContext.getMessage("change_notification_preferences.sendinformation")}
	                    </label>
	                </div>
	            </td>
	        </tr>
	        <!-- Email frequency -->
	        <tr>
	            <th><a name="editEmailFrequency"></a>${springMacroRequestContext.getMessage("manage.email_frequency")}</th>
	            <td></td>
	        </tr>
	        <tr ng-controller="EmailFrequencyCtrl">
	            <td colspan="2">
	                <div class="control-group">
	                    <div>Log into your account @<a href="http://localhost:8080/orcid-web/signin" target="_blank">orcid</a> to change your email address.</div>
	                    <label for="sendEmailFrequencyDays"
	                        class="">${springMacroRequestContext.getMessage("manage.send_email_frequency")}</label>
	                    <div class="relative">
	                        <select id="sendEmailFrequencyDays" name="sendEmailFrequencyDays"
	                            class="input-xlarge"
	                            ng-model="prefsSrvc.prefs.sendEmailFrequencyDays"
	                            ng-change="prefsSrvc.savePrivacyPreferences()">
	                            <#list sendEmailFrequencies?keys as key>
	                            <option value="${key}"
	                                ng-selected="prefsSrvc.prefs.sendEmailFrequencyDays === ${key}">${sendEmailFrequencies[key]}</option>
	                            </#list>
	                        </select>
	                    </div>
	                    <div>${springMacroRequestContext.getMessage("manage.service_announcements")}</div>
	                </div>
	                <br>
	                <span onclick="window.close()" class="btn btn-primary">Close</span>
	            </td>
	        </tr>
		</table>
	</div>
</@base>