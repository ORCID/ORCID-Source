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
<@base>

<div class="popover-frame">
    <@spring.bind "changeSecurityQuestionForm.*" />
    <#if securityQuestionSaved?? && securityQuestionSaved>
    <div class="alert alert-success">
        <strong><@spring.message "orcid.frontend.web.securityquestion_changed"/></strong>
    </div>
    </#if>
    <#if spring.status.error>
    <div class="alert alert-error">
        <ul class="validationerrors">
            <#list spring.status.errorMessages?sort as error> <li>${error}</li> </#list>
        </ul>
    </div>
    </#if>
    <form id="reg-form-password" class="popover-form" action="<@spring.url '/account/security-question'/>" method="post" autocomplete="off">
        <div class="">
            <label for="changeSecurityQuestionForm.securityQuestionId" class="">Security Question</label>
            <div class="relative">
            <@spring.formSingleSelect "changeSecurityQuestionForm.securityQuestionId", securityQuestions, 'class="input-xlarge"' />
                <span class="required">*</span>
            </div>
        </div>
        <div class="">
            <label for="changeSecurityQuestionForm.securityQuestionAnswer" class="">Security Answer</label>
            <div class="relative">
            <@spring.formInput "changeSecurityQuestionForm.securityQuestionAnswer", 'class="input-xlarge"' />
                <span class="required">*</span>
            </div>
        </div>
         <div class="relative">
            <button id="bottom-submit-security-question" class="btn btn-primary" type="submit">Save changes</button>
            <button id="bottom-reset-security-question" class="btn close-parent-popover" type="reset">Cancel</button>
        </div>
    </form>
</div>
</@base>
    