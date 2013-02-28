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
<div class="colorbox-content">
<h1 class="lightbox-title">Update Personal Information</h1>
<!-- Show errors here -->
<@spring.bind "personalInfoForm.*" />
<#if spring.status.error>
<div class="errorBox">
    <div class="errorHead">Notice:</div>
    <div class="errorText">
        <ul class="validationerrors">
            <#list spring.status.errorMessages?sort as error>
                <li>${error}</li> </#list>
        </ul>
    </div>
</div>
</#if>

<#if personalInformationSuccessfullyUpdated?? && personalInformationSuccessfullyUpdated>
<div class="alert alert-success">
    <strong><@spring.message "orcid.frontend.web.details_saved"/></strong>
</div>
</#if>

<form id="personal-info-form" class="form-horizontal" action="<@spring.url '/account/personal-info'/>" method="post">
    <fieldset>
        <legend>Personal<div class="pull-right"><label for="masterPublic" class="control-label legend-label">public</label><div class="controls"><@spring.formCheckbox "personalInfoForm.masterPublic"/></div></div></legend>       
        <div class="control-group">
            <label for="givenNames" class="control-label">Given name</label>
            <div class="controls">
            <@spring.formInput "personalInfoForm.givenNames" 'class="input-xlarge"'/>
                <span class="required">*</span>
                <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
        <div class="control-group">
            <label for="familyName" class="control-label">Family name</label>
            <div class="controls">
            <@spring.formInput "personalInfoForm.familyName" 'class="input-xlarge"'/>
                <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
        <div class="control-group">
            <label for="creditName" class="control-label">How do you normally appear on publications?</label>
            <div class="controls">
            <@spring.formInput "personalInfoForm.creditName" 'class="input-xlarge"'/>
                <span class="required">*</span>
                <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>       
    </fieldset>
    
    
    <fieldset>
        <legend>Other names
            <div class="pull-right"><label for="otherNamesPublic" class="control-label legend-label">public</label>
            <div class="controls"><@spring.formCheckbox "personalInfoForm.otherNamesPublic"/></div></div></legend>
        <div class="control-group">
            <label for="other-name" class="control-label">Other names used by you</label>
            <div class="controls">
                <input type="text" id="other-name" name="subject" class="span4"/>
                <a id="add-other-name" href="#" class="btn"><i class="icon-arrow-down"></i></a>
            </div>
            <div class="controls">
            <@spring.formMultiSelect "personalInfoForm.selectedOtherNames", personalInfoForm.otherNames, 'class="span4"'/>
            <a id="remove-other-name" href="#" class="btn"><i class="icon-arrow-up"></i></a>
            </div>
        </div>
    </fieldset>
    <fieldset>
        <legend>Your external IDs<div class="pull-right"><label for="researcherUrlsPublic" class="control-label legend-label">public</label><div class="controls"><@spring.formCheckbox "personalInfoForm.researcherUrlsPublic"/></div></div></legend>
        <div class="control-group">
            <label for="external-urls" class="control-label">External URLs</label>
            <div class="controls">
                <input type="text" id="external-urls" name="external-urls" class="span4"/>
                <a id="add-url" href="#" class="btn"><i class="icon-arrow-down"></i></a>
            </div>
            <div class="controls">
            <@spring.formMultiSelect "personalInfoForm.selectedResearcherUrls", personalInfoForm.researcherUrls,'class="span4"'/>
                <a id="remove-url" href="#" class="btn"><i class="icon-arrow-up"></i></a>
            </div>
        </div>
    </fieldset>
    
    <fieldset>
        <legend>Keywords</legend>
        <div class="control-group">
            <label for="external-urls" class="control-label">Keywords</label>
            <div class="controls">
                <input type="text" id="keywords" name="external-urls" class="span4"/>
                <a id="add-keyword" href="#" class="btn"><i class="icon-arrow-down"></i></a>
            </div>
            <div class="controls">
            <@spring.formMultiSelect "personalInfoForm.selectedKeywords", personalInfoForm.keywords, 'class="span4"' />
            <a id="remove-keyword" href="#" class="btn"><i class="icon-arrow-up"></i></a>
            </div>
        </div>
    </fieldset>
    <fieldset>
        <legend>Subjects<div class="pull-right"><label for="subjectsPublic" class="control-label legend-label">public</label><div class="controls"><@spring.formCheckbox "personalInfoForm.subjectsPublic"/></div></div></legend>
        <div class="control-group">
            <div class="controls">
            <@spring.formMultiSelect "personalInfoForm.availableRemainingSubjects", personalInfoForm.availableRemainingSubjectMap, 'class="span3"'/>
                <a id="remove-subject" href="#" class="btn"><i class="icon-arrow-left"></i></a>
                <a id="insert-subject" href="#" class="btn"><i class="icon-arrow-right"></i></a>
            <@spring.formMultiSelect "personalInfoForm.selectedResearcherSubjects", personalInfoForm.researcherSubjects, 'class="span3"'/>
            </div>
        </div>
    </fieldset>
    <fieldset>
        <legend>Short Description<div class="pull-right"><label for="shortDescriptionPublic" class="control-label legend-label">public</label><div class="controls"><@spring.formCheckbox "personalInfoForm.shortDescriptionPublic" /></div></div></legend>
        <div class="control-group">
            <label for="shortDescription" class="control-label">Short Description</label>
            <div class="controls">
            <@spring.formTextarea "personalInfoForm.shortDescription" 'class="input-xlarge" maxlength="300"'/>
                <p class="orcid-help-block help-block">A short description about you, in not more than 300 characters.</p>
            </div>
        </div>
    </fieldset>
    <div class="form-actions">
        <button class="btn btn-primary" type="submit">Save changes</button>
        <button class="btn" id="personal-info-reset" type="reset">Reset</button>
    </div>
    <@spring.formHiddenInput "personalInfoForm.orcid"/>
</form>
<!--<script>
    $(function () {
    	
    	var originalKeywords = listOptionValues('#selectedKeywords');
    	var originalOtherNames = listOptionValues('#selectedOtherNames');
    	var originalResearcherUrls = listOptionValues('#selectedResearcherUrls');    	
	
        $('#add-other-name').click(function() {
            var val = $('#other-name').val();
            if (val != '') {
                $('#selectedOtherNames').append( new Option(val ,val) );
                $('#other-name').val('');
            }
            return false;
        });
        $('#remove-other-name').click(function() {
        var selected = $('#selectedOtherNames option:selected');
            $('#other-name').val(selected.val());
            $('#selectedOtherNames option:selected').remove();
            return false;
        });
        $('#add-url').click(function() {
            var val = $('#external-urls').val();
            if (val != '') {
                $('#selectedResearcherUrls').append( new Option(val ,val) );
                $('#external-urls').val('');
            }
            return false;
        });
        $('#remove-url').click(function() {
            var selected = $('#selectedResearcherUrls option:selected');
            $('#external-urls').val(selected.val());
            $('#selectedResearcherUrls option:selected').remove();
            return false;
        });

        $('#add-keyword').click(function() {
            var val = $('#keywords').val();
            if (val != '') {
                $('#selectedKeywords').append( new Option(val ,val) );
                $('#keywords').val('');
            }
            return false;
        });

        $('#remove-keyword').click(function() {
            var selected = $('#selectedKeywords option:selected');
            $('#keywords').val(selected.val());
            $('#selectedKeywords option:selected').remove();
            return false;
        });

        $('#remove-subject').click(function() {
            var selected = $('#selectedResearcherSubjects option:selected');
            $('#selectedResearcherSubjects option:selected').remove();
            $('#availableRemainingSubjects').append(selected);
            return false;
        });
        $('#insert-subject').click(function() {
            var selected = $('#availableRemainingSubjects option:selected');
            $('#availableRemainingSubjects option:selected').remove();
            $('#selectedResearcherSubjects').append(selected);
            return false;
        });
        $('#personal-info-form').submit(function() {
            $('#selectedResearcherSubjects option').attr('selected','selected');
            $('#selectedResearcherUrls option').attr('selected','selected');
            $('#selectedOtherNames option').attr('selected','selected');
            $('#selectedKeywords option').attr('selected','selected');
        });
        
         $('#personal-info-reset').click(function() {
         	var confirmed = confirm("This will reset the personal information form.\nDo you want to continue?");
         	if (confirmed)
          	{         		
          		rebuildOptionsList('#selectedKeywords',originalKeywords);
          		rebuildOptionsList('#selectedOtherNames',originalOtherNames);
          		rebuildOptionsList('#selectedResearcherUrls',originalResearcherUrls);          		  
          	}   
          	
          	return confirmed;
         });
    })
</script>-->

</div>
</@base>