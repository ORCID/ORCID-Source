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
    <label class="relative" for="manualWork.title">Title</label>
    <div class="relative"><@spring.formInput "manualWork.title" "placeholder='Add the full title here' class=\"input-xlarge\""/><span class="required">*</span>
    <@spring.showErrors "<br/>" "orcid-error"/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.subtitle">Subtitle</label>
    <div class="relative"><@spring.formInput "manualWork.subtitle" "placeholder='Add the subtitle here' class=\"input-xlarge\""/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.citation">Citation</label>
    <div class="relative"><@spring.formTextarea "manualWork.citation" "placeholder='Add the citation here' class=\"input-xlarge\""/>
    <@spring.showErrors "<br/>" "orcid-error"/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.citationType">Citation Type</label>
    <div class="relative"><@spring.formSingleSelect "manualWork.citationType", citationTypes  "class=\"input-xlarge\""/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.workType">Work type</label>
    <div class="relative"><@spring.formSingleSelect "manualWork.workType", workTypes  "class=\"input-xlarge\""/><span class="required">*</span>
    <@spring.showErrors "<br/>" "orcid-error"/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.day">Pub Date</label>
    <div class="relative">
        <@spring.formSingleSelect "manualWork.day", days 'class="span1"'/>
        <@spring.formSingleSelect "manualWork.month", months 'class="span1"'/>
        <@spring.formSingleSelect "manualWork.year", years 'class="span1"' />
    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.currentWorkExternalIds[0].id">ID</label>
    <div class="relative"><@spring.formInput "manualWork.currentWorkExternalIds[0].id" "placeholder=\"Enter an external ID\" class=\"input-xlarge\""/>
    <@spring.bind "manualWork.currentWorkExternalIds" />
    <@spring.showErrors "<br/>" "orcid-error"/>
    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.currentWorkExternalIds[0].type">ID type</label>
    <div class="relative"><@spring.formSingleSelect "manualWork.currentWorkExternalIds[0].type", idTypes  "class=\"input-xlarge\"" />
    <@spring.bind "manualWork.currentWorkExternalIds" />
    <@spring.showErrors "<br/>" "orcid-error"/>
    </div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.url">URL</label>
    <div class="relative"><@spring.formInput "manualWork.url" "placeholder='Enter a link to the work' class=\"input-xlarge\""/></div>
</div>
<div class="control-group">
    <label class="relative" for="manualWork.currentWorkContributors[0].role">Role</label>
    <div class="relative"><@spring.formSingleSelect "manualWork.currentWorkContributors[0].role", roles " class=\"input-xlarge\"" />
    <@spring.showErrors "<br/>" "orcid-error"/></div>
</div>
<div class="control-group">
    <label class="relative">Credited</label>
    <div class="relative">
        <@orcid.orcidFormRadioButtons "manualWork.currentWorkContributors[0].sequence", sequences, '', 'class="inline"' 'class="radio radio-inline"'/>
    </div>
</div>
<div class="control-group"><label class="relative" for="manualWork.description">
    Description</label><div class="relative"> <@spring.formTextarea "manualWork.description" "placeholder='A brief description'  class=\"input-xlarge\""/></div>
</div>
<div class="control-group"><div class="control">
<input type="reset" value="Clear" class="btn"></input>
<input type="submit" value="Add to list"></input></div></div>