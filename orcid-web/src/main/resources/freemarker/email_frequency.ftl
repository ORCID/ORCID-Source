<@base>
    <div>
        <img src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo">
        <br><br><br>
        <table class="table table-bordered settings-table" style="margin:0; padding:0;">
            <!-- Email frequency -->
            <tr>
                <th><a name="editEmailFrequency"></a>${springMacroRequestContext.getMessage("unsubscribe.email_frequency")}</th>
                <td></td>
            </tr>
            <tr ng-controller="EmailFrequencyLinkCtrl">
                <td colspan="2">
                    <div class="control-group">
                        <div>
                            ${springMacroRequestContext.getMessage('unsubscribe.email_sent_to_primary')}
                            ${primaryEmail}<br>
                            ${springMacroRequestContext.getMessage('unsubscribe.email_to_edit')}<a href="<@orcid.rootPath'/account'/>" 
                            target="unsubscribe.text.sign_into_account">
                            ${springMacroRequestContext.getMessage('unsubscribe.text.sign_into_account')}</a>
                        </div>
                        <br>
                        <label for="sendEmailFrequency"
                            class="">${springMacroRequestContext.getMessage("unsubscribe.email_how_often")}</label>
                        <div class="relative">
                            <select id="sendEmailFrequency" name="sendEmailFrequency"
                                class="input-xlarge"
                                ng-model="emailFrequency.sendEmailFrequencyDays"
                                ng-change="saveEmailFrequencies()">
                                <#list sendEmailFrequencies?keys as key>
                                <option value="${key}"
                                    ng-selected="emailFrequency.sendEmailFrequencyDays === ${key}">${sendEmailFrequencies[key]}</option>
                                </#list>
                            </select>
                        </div>
                        <br>
                        <div>
                            ${springMacroRequestContext.getMessage("manage.service_announcements")} 
                            <br>
                            ${springMacroRequestContext.getMessage("unsubscribe.email_for_more_info")}
                            <a href="https://orcid.org/privacy-policy#How_we_use_information" target="privacy_policy">
                            ${springMacroRequestContext.getMessage("unsubscribe.email_link_privacy_policy")}
                            </a>
                        </div>
                        <div>
                            <small class="italic">${springMacroRequestContext.getMessage("manage.service_announcements.note")}</small>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</@base>