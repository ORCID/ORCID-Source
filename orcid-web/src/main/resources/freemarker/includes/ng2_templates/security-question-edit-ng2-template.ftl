<script type="text/ng-template" id="security-question-edit-ng2-template">
    <div class="editTablePadCell35">
        <span class="orcid-error" *ngIf="errors?.length > 0"> <span
            *ngFor='let error of errors' [innerHTML]="error"></span>
        </span>
        <div class="control-group">
            <label for="changeSecurityQuestionForm.securityQuestionAnswer"
                class="">${springMacroRequestContext.getMessage("manage.security_question")}</label>                                    
            <div class="relative" ><!-- ng-init="securityQuestions = [<#list securityQuestions?keys as key>'${securityQuestions[key]?js_string}',</#list>]"-->
                <select id="securityQuestionId" name="securityQuestionId"
                    class="input-xlarge"
                    [(ngModel)]="securityQuestionPojo.securityQuestionId">
                    >
                        <!-- ng-options="securityQuestions.indexOf(securityOption) as securityOption for securityOption in securityQuestions" -->
                        <option *ngFor="let securityOption of securityQuestions">{{securityOption}}</option>                                    
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
        <#else>
            <div class="control-group">
                <button id="bottom-submit-security-question"
                    class="btn btn-primary" (click)="checkCredentials()"><@orcid.msg 'freemarker.btnsavechanges' /></button>                                        
                <a id="bottom-reset-security-question" class="cancel-option inner-row" (click)="getSecurityQuestion()"><@orcid.msg 'freemarker.btncancel' /></a>                                    
            </div>
        </#if>
    </div>
</script>