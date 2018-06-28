<script type="text/ng-template" id="notification-preference-ng2-template">
    <div>
        <p>${springMacroRequestContext.getMessage("manage.notification_header")}</p>
        <div class="editTablePadCell35">                                
            <label class="checkbox"> <input type="checkbox"
                id="sendOrcidChangeNotifcations"
                name="sendOrcidChangeNotifcations"
                [(ngModel)]="prefsSrvc.prefs['send_change_notifications']"
                (ngModelChange)="prefsSrvc.updateNotificationPreferences()" />
                ${springMacroRequestContext.getMessage("change_notification_preferences.sendnotification")}
            </label>
            <label class="checkbox"> <input type="checkbox"
                id="sendAdministrativeChangeNotifcations"
                name="sendAdministrativeChangeNotifcations"
                [(ngModel)]="prefsSrvc.prefs['send_administrative_change_notifications']"
                (ngModelChange)="prefsSrvc.updateNotificationPreferences()" />
                ${springMacroRequestContext.getMessage("change_notification_preferences.sendadministrativenotification")}
            </label>                                
            <label class="checkbox"> <input type="checkbox"
                id="sendMemberUpdateRequests" name="sendMemberUpdateRequests"
                [(ngModel)]="prefsSrvc.prefs['send_member_update_requests']"
                (ngModelChange)="prefsSrvc.updateNotificationPreferences()" />
                ${springMacroRequestContext.getMessage("change_notification_preferences.sendmemberupdaterequests")}
            </label>
            <label class="checkbox"> <input type="checkbox"
                id="sendOrcidNews" name="sendOrcidNews"
                [(ngModel)]="prefsSrvc.prefs['send_orcid_news']"
                (ngModelChange)="prefsSrvc.updateNotificationPreferences()" />
                ${springMacroRequestContext.getMessage("change_notification_preferences.news")}
            </label>
        </div>
        <p>
            ${springMacroRequestContext.getMessage("change_notification_preferences.sendinformation_1")}
            <a href="http://orcid.org/newsletter/subscriptions" target="subscribe">${springMacroRequestContext.getMessage("change_notification_preferences.sendinformation_2")}</a>${springMacroRequestContext.getMessage("change_notification_preferences.sendinformation_3")}
        </p>
        <p>
            <a href="https://support.orcid.org/knowledgebase/articles/1807645-notifications-preferences" target="learnmore">
            ${springMacroRequestContext.getMessage("change_notification_preferences.learn_more")}
            </a>
            ${springMacroRequestContext.getMessage("change_notification_preferences.about_inbox_notifications")}
        </p>
     </div>
</script>