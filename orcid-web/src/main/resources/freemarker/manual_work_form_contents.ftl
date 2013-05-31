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
<div class="msg">
    <span class="help-block"><@spring.message "orcid.frontend.web.manually_add_work"/></span>
</div>
<@spring.bind "manualWork.*" />
<div class="control-group">
    <label class="relative" for="manualWork.title">${springMacroRequestContext.getMessage("manual_work_form_contents.labeltitle")}</label>
    <div class="relative"><@spring.formInput "manualWork.title" "placeholder='Add the full title here' class=\"input-xlarge\""/><span class="required">*</span>
    <@spring.showErrors "<br/>" "orcid-error"/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.subtitle">${springMacroRequestContext.getMessage("manual_work_form_contents.labelsubtitle")}</label>
    <div class="relative"><@spring.formInput "manualWork.subtitle" "placeholder='Add the subtitle here' class=\"input-xlarge\""/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.citation">${springMacroRequestContext.getMessage("manual_work_form_contents.labelcitation")}</label>
    <div class="relative"><@spring.formTextarea "manualWork.citation" "placeholder='Add the citation here' class=\"input-xlarge\""/>
    <@spring.showErrors "<br/>" "orcid-error"/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.citationType">${springMacroRequestContext.getMessage("manual_work_form_contents.labelcitationtype")}</label>
    <div class="relative"><@spring.formSingleSelect "manualWork.citationType", citationTypes  "class=\"input-xlarge\""/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.workType">${springMacroRequestContext.getMessage("manual_work_form_contents.labelworktype")}</label>
    <div class="relative"><@spring.formSingleSelect "manualWork.workType", workTypes  "class=\"input-xlarge\""/><span class="required">*</span>
    <@spring.showErrors "<br/>" "orcid-error"/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.day">${springMacroRequestContext.getMessage("manual_work_form_contents.labelpubdate")}</label>
    <div class="relative">
        <@spring.formSingleSelect "manualWork.day", days 'class="span1"'/>
        <@spring.formSingleSelect "manualWork.month", months 'class="span1"'/>
        <@spring.formSingleSelect "manualWork.year", years 'class="span2"' />
    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.currentWorkExternalIds[0].id">${springMacroRequestContext.getMessage("manual_work_form_contents.labelID")}</label>
    <div class="relative"><@spring.formInput "manualWork.currentWorkExternalIds[0].id" "placeholder=\"Enter an external ID\" class=\"input-xlarge\""/>
    <@spring.bind "manualWork.currentWorkExternalIds" />
    <@spring.showErrors "<br/>" "orcid-error"/>
    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.currentWorkExternalIds[0].type">${springMacroRequestContext.getMessage("manual_work_form_contents.labelIDtype")}</label>
    <div class="relative"><@spring.formSingleSelect "manualWork.currentWorkExternalIds[0].type", idTypes  "class=\"input-xlarge\"" />
    <@spring.bind "manualWork.currentWorkExternalIds" />
    <@spring.showErrors "<br/>" "orcid-error"/>
    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.url">${springMacroRequestContext.getMessage("manual_work_form_contents.labelURL")}</label>
    <div class="relative"><@spring.formInput "manualWork.url" "placeholder='Enter a link to the work' class=\"input-xlarge\""/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.currentWorkContributors[0].role">${springMacroRequestContext.getMessage("manual_work_form_contents.labelRole")}</label>
    <div class="relative"><@spring.formSingleSelect "manualWork.currentWorkContributors[0].role", roles " class=\"input-xlarge\"" />
    <@spring.showErrors "<br/>" "orcid-error"/></div>
</div>
<div class="control-group">
    <label class="relative">${springMacroRequestContext.getMessage("manual_work_form_contents.labelcredited")}</label>
    <div class="relative">
        <@orcid.orcidFormRadioButtons "manualWork.currentWorkContributors[0].sequence", sequences, '', 'class="inline"' 'class="radio radio-inline"'/>
    </div>
</div>
<div class="control-group"><label class="relative" for="manualWork.description">
    ${springMacroRequestContext.getMessage("manual_work_form_contents.labeldescription")}</label><div class="relative"> <@spring.formTextarea "manualWork.description" "placeholder='A brief description'  class=\"input-xlarge\""/></div>
</div>
<div class="control-group"><div class="control">
<input type="reset" value="${springMacroRequestContext.getMessage('manual_work_form_contents.btnclear')}" class="btn"></input>
<input type="submit" value="${springMacroRequestContext.getMessage('manual_work_form_contents.btnaddtolist')}"></input></div></div>