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
                            ${springMacroRequestContext.getMessage("unsubscribe.email_link_privacy_policy")}
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