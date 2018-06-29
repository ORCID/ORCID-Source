<script type="text/ng-template" id="security-question-edit-ng2-template">
    <div class="editTablePadCell35">
        <span class="orcid-error" *ngIf="errors?.length > 0"> <span
            *ngFor='let error of errors' [innerHTML]="error"></span>
        </span>
        <div class="control-group">
            <label for="changeSecurityQuestionForm.securityQuestionAnswer"
                class="">${springMacroRequestContext.getMessage("manage.security_question")}</label>                                    
            <div class="relative" >
                {{initSecurityQuestion([<#list securityQuestions?keys as key>'${securityQuestions[key]?js_string}',</#list>''])}}

                <select id="securityQuestionId" name="securityQuestionId"
                    class="input-xlarge"
                    [(ngModel)]="securityQuestionPojo.securityQuestionId">
                    >

                        <option *ngFor="let securityOption of securityQuestions; let i = index" value="{{i}}">{{securityOption}}</option>                                    

                </select>
            </div>
        </div>
        <div class="control-group">
            <label for="changeSecurityQuestionForm.securityQuestionAnswer"
                class="">${springMacroRequestContext.getMessage("manage.securityAnswer")}</label>
            <div class="relative">
                <input type="text" id="securityQuestionAnswer"
                    name="securityQuestionAnswer" class="input-xlarge"
                    [(ngModel)]="securityQuestionPojo.securityAnswer"
                    (keyup.enter)="checkCredentials()" />
            </div>
        </div>
        <#if isPasswordConfirmationRequired>
            <@orcid.msg 'manage.security_question.not_allowed' />
            <div style="padding: 20px;" *ngIf="showConfirmationWindow">
                <h2><@orcid.msg 'check_password_modal.confirm_password' /></h2>
               <label for="check_password_modal.password" class=""><@orcid.msg 'check_password_modal.password' /></label>
               <input id="check_password_modal.password" type="password" name="check_password_modal.password" [(ngModel)]="password" class="input-xlarge"/>
               <br />
               <button id="bottom-submit" class="btn btn-primary" (click)="submitModal()"><@orcid.msg 'check_password_modal.submit'/></button>
            </div>

        <#else>
            <div class="control-group">
                <button id="bottom-submit-security-question"
                    class="btn btn-primary" (click)="checkCredentials()"><@orcid.msg 'freemarker.btnsavechanges' /></button>                                        
                <a id="bottom-reset-security-question" class="cancel-option inner-row" (click)="getSecurityQuestion()"><@orcid.msg 'freemarker.btncancel' /></a>                                    
            </div>
        </#if>
    </div>
</script>

<script type="text/ng-template" id="check-password-modal-ng2-template">
    <div style="padding: 20px;"><h2><@orcid.msg 'check_password_modal.confirm_password' /></h2>
       <label for="check_password_modal.password" class=""><@orcid.msg 'check_password_modal.password' /></label>
       <input id="check_password_modal.password" type="password" name="check_password_modal.password" [(ngModel)]="password" class="input-xlarge"/>
       <br />
       <button id="bottom-submit" class="btn btn-primary" (click)="submitModal()"><@orcid.msg 'check_password_modal.submit'/></button>
       <button class="btn" (click)="closeModal()"><@orcid.msg 'check_password_modal.close'/></button>
    </div>
</script>
