<script type="text/ng-template" id="unsubscribe-ng2-template">
    <div>
        <form id="unsubscribe-form" autocomplete="off">
            <div id="emailFrequency" class="bottomBuffer">
                <h2><@orcid.msg 'manage.email.email_frequency.unsubscribe.title' /></h2>
                <div class="editTablePadCell35">
                    <p><@orcid.msg 'manage.email.email_frequency.unsubscribe.1.1' /> ${email_address} <@orcid.msg 'manage.email.email_frequency.unsubscribe.1.2' /></p>
                    <p><@orcid.msg 'manage.email.email_frequency.unsubscribe.2' /> <a href="https://orcid.org/account" target="_blank"><@orcid.msg 'manage.email.email_frequency.unsubscribe.2.url.text' /></a> <@orcid.msg 'manage.email.email_frequency.unsubscribe.3' /> <a href="https://support.orcid.org/knowledgebase/articles/1807645-notifications-preferences#02-tips" target="_blank"><@orcid.msg 'manage.email.email_frequency.unsubscribe.3.url.text' /></a></p>
                    <p><@orcid.msg 'manage.email.email_frequency.notifications.selectors.header' /></p>                                            
                    <div class="control-group">
                        <label for="amend-frequency"><@orcid.msg 'manage.email.email_frequency.notifications.selectors.amend' /></label>
                        <select id="amend-frequency" name="amend-frequency" [(ngModel)]="frequencies['sendChangeNotifications']">   
                            <#list sendEmailFrequencies?keys as key>
                                <option value="${key}">${sendEmailFrequencies[key]}</option>
                            </#list>
                        </select>
                    </div>
                    <div class="control-group">
                        <label for="administrative-frequency"><@orcid.msg 'manage.email.email_frequency.notifications.selectors.administrative' /></label>
                        <select id="administrative-frequency" name="administrative-frequency" [(ngModel)]="frequencies['sendAdministrativeChangeNotifications']">   
                            <#list sendEmailFrequencies?keys as key>
                                <option value="${key}">${sendEmailFrequencies[key]}</option>
                            </#list>
                        </select>
                    </div>
                    <div class="control-group">
                        <label for="permission-frequency"><@orcid.msg 'manage.email.email_frequency.notifications.selectors.permission' /></label>                  
                        <select id="permission-frequency" name="permission-frequency" [(ngModel)]="frequencies['sendMemberUpdateRequestsNotifications']">   
                            <#list sendEmailFrequencies?keys as key>
                                <option value="${key}">${sendEmailFrequencies[key]}</option>
                            </#list>
                        </select>
                    </div>
                </div> 
                <h2><@orcid.msg 'manage.email.email_frequency.news.header' /></h2>
                <div class="editTablePadCell35">
                    <div class="control-group">
                        <input id="send-orcid-news" type="checkbox" name="sendOrcidNews" [(ngModel)]="frequencies['sendQuarterlyTips']"/>
                        <label for="send-orcid-news">
                        <@orcid.msg 'manage.email.email_frequency.notifications.news.checkbox.label' /></label>
                    </div>
                </div>
                <p><small class="italic"><@orcid.msg 'manage.email.email_frequency.bottom' /> <a href="https://orcid.org/privacy-policy#How_we_use_information" target="_blank"><@orcid.msg 'public-layout.privacy_policy' /></a></small></p>  
            </div>    
        </form>
    </div>
</script>