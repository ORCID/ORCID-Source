<!-- Unused - merged with emails-form-ng2-template.ftl -->

<script type="text/ng-template" id="email-frecuency-ng2-template">
    <div>
        <div class="row bottomBuffer">
            <strong class="green">${springMacroRequestContext.getMessage("manage.email.email_frequency")}</strong>
        </div>              
        <div class="control-group">
            <p>${springMacroRequestContext.getMessage("manage.send_email_to_primary_1")} <a href="${baseUri}/inbox" target="manage.send_email_to_primary_2">${springMacroRequestContext.getMessage("manage.send_email_to_primary_2")}</a>${springMacroRequestContext.getMessage("manage.send_email_to_primary_3")}</p>
            <form class="form-inline">
                <div class="form-group">                            
                    <div class="input-group">
                        <!--                           
                        <select id="sendEmailFrequencyDays" name="sendEmailFrequencyDays" class="input-xlarge" [(ngModel)]="prefsSrvc.prefs['email_frequency']" (ngModelChange)="prefsSrvc.clearMessage()">
                            <#list sendEmailFrequencies?keys as key>
                                <option value="${key}">${sendEmailFrequencies[key]}</option>
                            </#list>
                        </select>
                        -->
                    </div>
                </div>
                <button (click)="updateEmailFrequency()" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.send_email_frequency_save")}</button>
                <small class="green" *ngIf="prefsSrvc.saved">${springMacroRequestContext.getMessage("manage.send_email_frequency_saved")}</small>    
            </form>
        </div>
        <div class="control-group">
            <p>${springMacroRequestContext.getMessage("manage.send_email_to_primary_4")} {{emailSrvc.primaryEmail.value}}${springMacroRequestContext.getMessage("manage.send_email_to_primary_5")}</p>
            <p>${springMacroRequestContext.getMessage("manage.service_announcements")}</p>
            <p style="line-height: 12px;"><small class="italic">${springMacroRequestContext.getMessage("manage.service_announcements.note")}</small>
            </p>
        </div>
    </div>
</script>