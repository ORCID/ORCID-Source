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
<form id="main-affiliations-form" class="form-horizontal" action="<@spring.url '/account/affiliations'/>" method="post">
<@spring.formHiddenInput "currentAffiliationsForm.orcid"/>
<fieldset>
<#if affiliationsSuccessfullyUpdated?? && affiliationsSuccessfullyUpdated>
<div class="alert alert-success">
    <strong><@spring.message "orcid.frontend.web.details_saved"/></strong>
</div>
</#if>


<legend>${springMacroRequestContext.getMessage("manage_affiliations.primaryinstitutionname")}<div class="pull-right"><label for="primaryInstitutionForm.institutionNamePublic" class="control-label legend-label">${springMacroRequestContext.getMessage("manage_affiliations.public")}</label><div class="controls"><@spring.formCheckbox "currentAffiliationsForm.primaryInstitutionForm.institutionNamePublic"/></div></div></legend>
    <div class="control-group">
        <label for="primaryInstitutionForm.institutionName" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.institutionname")}</label>
        <div class="controls">
        <@spring.formInput "currentAffiliationsForm.primaryInstitutionForm.institutionName" 'class="span4"'/>
            <span class="required">*</span>
        <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
    <div class="control-group">
        <label for="primaryInstitutionForm.departments" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.suborgdept")}</label>
        <div class="controls">
            <input type="text" id="primary-dept-name" name="subject" class="span4"/>
            <a id="add-primary-dept-name" href="#" class="btn"><i class="icon-arrow-down"></i></a>
        </div>
        <div class="controls">
            <@spring.formMultiSelect "currentAffiliationsForm.primaryInstitutionForm.departments", (currentAffiliationsForm.primaryInstitutionForm.departmentsMap)!, 'class="span4"'/>
            <a id="remove-primary-dept-name" href="#" class="btn"><i class="icon-arrow-up"></i></a>
            <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
</fieldset>
<fieldset>
    <legend>${springMacroRequestContext.getMessage("manage_affiliations.primaryinstitutionaddress")}<div class="pull-right"><label for="primaryInstitutionForm.addressPublic" class="control-label legend-label">${springMacroRequestContext.getMessage("manage_affiliations.public")}</label><div class="controls"><@spring.formCheckbox "currentAffiliationsForm.primaryInstitutionForm.addressPublic"/></div></div></legend>
    <div class="control-group">
        <label for="currentAffiliationsForm.primaryInstitutionForm.addressLine1" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labeladdress1")}</label>
        <div class="controls">
        <@spring.formInput "currentAffiliationsForm.primaryInstitutionForm.addressLine1" 'class="span4"'/>
            <span class="required">*</span>
        <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.primaryInstitutionForm.addressLine2" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labeladdress2")}</label>
        <div class="controls">
        <@spring.formInput "currentAffiliationsForm.primaryInstitutionForm.addressLine2" 'class="span4"'/>
        <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.primaryInstitutionForm.city" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labeltowncity")}</label>
        <div class="controls">
        <@spring.formInput "currentAffiliationsForm.primaryInstitutionForm.city" 'class="span4"'/>
            <span class="required">*</span>
        <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.primaryInstitutionForm.state" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labelstatecountryprovince")}</label>
        <div class="controls">
        <@spring.formInput "currentAffiliationsForm.primaryInstitutionForm.state" 'class="span4"'/>         
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.primaryInstitutionForm.zipCode" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labelpostcodezip")}</label>
        <div class="controls">
        <@spring.formInput "currentAffiliationsForm.primaryInstitutionForm.zipCode" 'class="span4"'/>    
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.primaryInstitutionForm.country" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labelcountry")}</label>
        <div class="controls">
        <@spring.formSingleSelect "currentAffiliationsForm.primaryInstitutionForm.country", countries, 'class="span4"'/>
            <span class="required">*</span>
        <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.primaryInstitutionForm.startDate" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labelstartdate")}</label>
        <div class="controls">
        	<@spring.formSingleSelect "currentAffiliationsForm.primaryInstitutionForm.startDate", allDates, 'class="span4"'/>    
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.primaryInstitutionForm.registrationRole" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labelRole")}</label>
        <div class="controls">
        <@spring.formSingleSelect "currentAffiliationsForm.primaryInstitutionForm.registrationRole", registrationRoles, 'class="span4"'/>
            <span class="required">*</span>
        <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
    <div class="form-actions">
        <button class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("manage_affiliations.btnsavechanges")}</button>
        <button class="btn" type="reset" id='main-affiliations-form-reset'>${springMacroRequestContext.getMessage("manage_affiliations.btnReset")}</button>
    </div>
</fieldset>
<fieldset>
        <legend>${springMacroRequestContext.getMessage("manage_affiliations.jointaffiliationname")} <small>${springMacroRequestContext.getMessage("manage_affiliations.optional")}</small><div class="pull-right"><label for="jointAffiliationForm.institutionNamePublic" class="control-label legend-label">${springMacroRequestContext.getMessage("manage_affiliations.labelpublic")}</label><div class="controls"><@spring.formCheckbox "currentAffiliationsForm.jointAffiliationForm.institutionNamePublic"/></div></div></legend>
        <div class="control-group">
            <label for="jointAffiliationForm.institutionName" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.institutionname")}</label>
            <div class="controls">
            <@spring.formInput "currentAffiliationsForm.jointAffiliationForm.institutionName" 'class="span4"'/>
                <span class="required">*</span>
            <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
        <div class="control-group">
            <label for="jointAffiliationForm.departments" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.suborgdept")}</label>
            <div class="controls">
                <input type="text" id="joint-dept-name" name="subject" class="span4"/>
                <a id="add-joint-dept-name" href="#" class="btn"><i class="icon-arrow-down"></i></a>
            </div>
            <div class="controls">
            <@spring.formMultiSelect "currentAffiliationsForm.jointAffiliationForm.departments", (currentAffiliationsForm.jointAffiliationForm.departmentsMap)!, 'class="span4"'/>
                <a id="remove-joint-dept-name" href="#" name="remove" class="btn"><i class="icon-arrow-up"></i></a>
            <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
</fieldset>
<fieldset>
	<@spring.bind "currentAffiliationsForm.jointAffiliationForm" />
		<#if spring.status.error>
	 		<div class="alert alert-error">
            	<@spring.showErrors "<br/>" "orcid-error"/>
        	</div>        	        	
        </#if>
	
    <legend>${springMacroRequestContext.getMessage("manage_affiliations.jointaffiliationaddress")} <small>${springMacroRequestContext.getMessage("manage_affiliations.optional")}</small><div class="pull-right"><label for="jointAffiliationForm.institutionNamePublic" class="control-label legend-label">${springMacroRequestContext.getMessage("manage_affiliations.labelpublic")}</label><div class="controls"><@spring.formCheckbox "currentAffiliationsForm.jointAffiliationForm.institutionNamePublic"/></div></div></legend>
    <div class="control-group">
        <label for="currentAffiliationsForm.jointAffiliationForm.addressLine1" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labeladdress1")}</label>
        <div class="controls">
        <@spring.bind "currentAffiliationsForm.jointAffiliationForm" />
        <@spring.formInput "currentAffiliationsForm.jointAffiliationForm.addressLine1" 'class="span4"'/>
            <span class="required">*</span>
        <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.jointAffiliationForm.addressLine2" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labeladdress2")}</label>
        <div class="controls">
        <@spring.formInput "currentAffiliationsForm.jointAffiliationForm.addressLine2" 'class="span4"'/>
        <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.jointAffiliationForm.city" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labeltowncity")}</label>
        <div class="controls">
        <@spring.formInput "currentAffiliationsForm.jointAffiliationForm.city" 'class="span4"'/>
            <span class="required">*</span>
        <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.jointAffiliationForm.state" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labelstatecountryprovince")}</label>
        <div class="controls">
        <@spring.formInput "currentAffiliationsForm.jointAffiliationForm.state" 'class="span4"'/>           
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.jointAffiliationForm.zipCode" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labelpostcodezip")}</label>
        <div class="controls">
        <@spring.formInput "currentAffiliationsForm.jointAffiliationForm.zipCode" 'class="span4"'/>          
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.jointAffiliationForm.country" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labelcountry")}</label>
        <div class="controls">
        <@spring.formSingleSelect "currentAffiliationsForm.jointAffiliationForm.country", countries, 'class="span4"'/>
          <span class="required">*</span>
        <@spring.showErrors "<br/>" "orcid-error"/>                  
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.jointAffiliationForm.startDate" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labelstartdate")}</label>
        <div class="controls">
        <@spring.formSingleSelect "currentAffiliationsForm.jointAffiliationForm.startDate", allDates, 'class="span4"'/>           
        </div>
    </div>
    <div class="control-group">
        <label for="currentAffiliationsForm.jointAffiliationForm.registrationRole" class="control-label">${springMacroRequestContext.getMessage("manage_affiliations.labelRole")}</label>
        <div class="controls">
        <@spring.formSingleSelect "currentAffiliationsForm.jointAffiliationForm.registrationRole", registrationRoles, 'class="span4"'/>
        </div>
    </div>
    <div class="form-actions">
        <button class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("manage_affiliations.btnsavechanges")}</button>
        <button class="btn" type="reset" id='joint-affiliations-form-reset'>${springMacroRequestContext.getMessage("manage_affiliations.btnReset")}</button>        
        <a id="delete-affiliate" href="#" class="btn btn-danger pull-right"><i class="icon-trash icon-white"></i> ${springMacroRequestContext.getMessage("manage_affiliations.removejointaffiliation")}</a>
    </div>
</fieldset>
<fieldset>
    <legend>${springMacroRequestContext.getMessage("manage_affiliations.externalidentifiers")} <small>${springMacroRequestContext.getMessage("manage_affiliations.optional")}</small><div class="pull-right"><label for="jointAffiliationForm.externalIdentifiersPublic" class="control-label legend-label">${springMacroRequestContext.getMessage("manage_affiliations.public")}</label><div class="controls"><@spring.formCheckbox "currentAffiliationsForm.externalIdentifiersPublic"/></div></div></legend>
    <table id="external-id-table" class="table">
        <thead>
        <tr>
            <th>${springMacroRequestContext.getMessage("manage_affiliations.thname")}</th>
            <th>${springMacroRequestContext.getMessage("manage_affiliations.thidentifier")}</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><@spring.formSingleSelect "currentAffiliationsForm.sponsorIds[0].sponsorOrcid", sponsors, 'class="input-xlarge"'/></td>
            <td><@spring.formInput "currentAffiliationsForm.sponsorIds[0].externalId" 'class="input-xlarge"'/></td>
        </tr>
        <tr>
            <td><@spring.formSingleSelect "currentAffiliationsForm.sponsorIds[1].sponsorOrcid", sponsors, 'class="input-xlarge"'/></td>
            <td><@spring.formInput "currentAffiliationsForm.sponsorIds[1].externalId" 'class="input-xlarge"'/></td>
        </tr>
        <tr>
            <td><@spring.formSingleSelect "currentAffiliationsForm.sponsorIds[2].sponsorOrcid", sponsors, 'class="input-xlarge"'/></td>
            <td><@spring.formInput "currentAffiliationsForm.sponsorIds[2].externalId" 'class="input-xlarge"'/></td>
        </tr>
        <tr>
            <td><@spring.formSingleSelect "currentAffiliationsForm.sponsorIds[3].sponsorOrcid", sponsors, 'class="input-xlarge"'/></td>
            <td><@spring.formInput "currentAffiliationsForm.sponsorIds[3].externalId" 'class="input-xlarge"'/></td>
        </tr>
        </tbody>
    </table>
</fieldset>
</form>
<#include "manage_past_affiliations.ftl">
<form id="delete-affiliates-form" class="form-horizontal" action="<@spring.url '/account/delete-affiliations'/>" method="post">
</form>

<script>
    $(function () {
    
    var originalPrimaryDepartments = listOptionValues('#primaryInstitutionForm\\.departments');
    var originalJointDepartments = listOptionValues('#jointAffiliationForm\\.departments');

    $('#add-primary-dept-name').click(function() {
            var val = $('#primary-dept-name').val();
            if (val != '') {
                $('#primaryInstitutionForm\\.departments').append( new Option(val ,val) );
                $('#primary-dept-name').val('');
            }
            return false;
        });
        $('#remove-primary-dept-name').click(function() {
            var selected = $('#primaryInstitutionForm\\.departments option:selected');
            $('#primary-dept-name').val(selected.val());
            $('#primaryInstitutionForm\\.departments option:selected').remove();
            return false;
        });

        $('#add-joint-dept-name').click(function() {
            var val = $('#joint-dept-name').val();
            if (val != '') {
                $('#jointAffiliationForm\\.departments').append( new Option(val ,val) );
                $('#joint-dept-name').val('');
            }
            return false;
        });

        $('#delete-affiliate').click(function() {
            if (confirm("This will remove all affiliate information and cannot be undone. Continue?")) {
                $('#delete-affiliates-form').submit();
            }
        });

        $('#remove-joint-dept-name').click(function() {
            var selected = $('#jointAffiliationForm\\.departments option:selected');
            $('#joint-dept-name').val(selected.val());
            $('#jointAffiliationForm\\.departments option:selected').remove();
            return false;
        });

        $('#main-affiliations-form').submit(function() {
            $('#primaryInstitutionForm\\.departments option').attr('selected','selected');
            $('#jointAffiliationForm\\.departments option').attr('selected','selected');
        });
        
          $('#main-affiliations-form-reset').click(function() {
          	var confirmed = confirm("This will reset the primary institution form.\nDo you want to continue?");
          	if (confirmed)
          	{         		
          		rebuildOptionsList('#primaryInstitutionForm\\.departments',originalPrimaryDepartments);
          	}   
          	
          	return confirmed; 	
          });
          
          $('#joint-affiliations-form-reset').click(function() {
          	var confirmed = confirm("This will reset the joint institution form.\nDo you want to continue?");
          	if (confirmed)
          	{         		
          		rebuildOptionsList('#jointAffiliationForm\\.departments',originalJointDepartments);
          	}   
          	
          	return confirmed; 	
          });
        
    });
</script>