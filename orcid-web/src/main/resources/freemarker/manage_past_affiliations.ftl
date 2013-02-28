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
<form id="past-affiliations-update" class="form-horizontal" action="<@spring.url '/account/update-past-institution-information'/>" method="post">
<fieldset>
<legend>Past institutions <small>optional</small><div class="pull-right"><label for="institutionNamePublic" class="control-label legend-label">public</label><div class="controls"><@spring.formCheckbox "pastInstitutionsForm.institutionNamePublic"/></div></div></legend>
<table class="table">
    <thead>
    <tr>
        <th><button class="btn btn-danger" type="submit" name="action" value="remove">Remove</button></th>
        <th>Name</th>
        <th>Department</th>
        <th>Address</th>
        <th>Role</th>
        <th>Start</th>
        <th>End</th>
    </tr>
    </thead>
    <tbody>
    <#if pastInstitutionsForm.allFormSummaries?? && pastInstitutionsForm.allFormSummaries?has_content>     	   	
        <#list pastInstitutionsForm.allFormSummaries as commonDetails>
            <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].institutionName"/>
            <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].formattedDepartments"/>
            <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].addressLine1"/>
            <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].addressLine2"/>
            <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].city"/>
            <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].state"/>
            <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].zipCode"/>
            <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].country"/>
            <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].startDate"/>
            <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].endDate"/>
            <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].registrationRole"/>
        <tr>
            <td class="remove-past-affiliate-header"><@spring.formCheckbox "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].formSelected" 'id="masterCheckBox_${commonDetails_index}"'/></td>
            <td>${commonDetails.institutionName}</td>
            <td>${commonDetails.formattedDepartments}</td>
            <td>${commonDetails.formattedAddress}</td>
            <td>${commonDetails.registrationRole}</td>
            <td>${commonDetails.startDate}</td>
            <td>${commonDetails.endDate}</td>
        </tr>         
        </#list>
        <div>
        <div class="pull-right">
        <tr>
        	<td colspan="7">
        		<button class="btn btn-primary pull-right" type="submit" name="action" value="update-past-visiblility">Update Past Institutions Visiblity</button>        		
        	</td>        	        	    	
        </tr>
        </div>
        </div>
    </#if>
    </tbody>
</table>
</fieldset>
</form>
<form id="past-affiliation-add" class="form-horizontal" action="<@spring.url '/account/add-past-institution'/>" method="post">
<fieldset>
    <legend>Add new past institution <small>To edit a past institution, add a new one with correct information and delete the incorrect one.</small></legend>    
<@spring.bind "pastInstitutionsForm.*" />
<@spring.formHiddenInput "currentAffiliationsForm.orcid"/>
<#if pastInstitutionsForm.allFormSummaries??>
    <#list pastInstitutionsForm.allFormSummaries as commonDetails>
        <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].institutionName"/>
        <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].formattedDepartments"/>
        <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].addressLine1"/>
        <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].addressLine2"/>
        <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].city"/>
        <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].state"/>
        <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].zipCode"/>
        <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].country"/>
        <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].startDate"/>
        <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].endDate"/>
        <@spring.formHiddenInput "pastInstitutionsForm.allFormSummaries[${commonDetails_index}].registrationRole"/>
    </#list>   
</#if>
    <div class="control-group">
        <label for="institutionName" class="control-label">Institution name</label>
        <div class="controls">
        <@spring.formInput "pastInstitutionsForm.institutionName" 'class="span4"'/>
            <span class="required">*</span>
        <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
    <div class="control-group">
        <label for="past-dept-name" class="control-label">Sub-org/Dept</label>
        <div class="controls">
            <input type="text" id="past-dept-name" name="past-dept-name" class="span4"/>
            <a id="add-past-dept-name" href="#" class="btn"><i class="icon-arrow-down"></i></a>
        </div>
        <div class="controls">
        <@spring.formMultiSelect "pastInstitutionsForm.departments", (pastInstitutionsForm.departmentsMap)!, 'class="span4"'/>
            <a id="remove-past-dept-name" href="#" class="btn"><i class="icon-arrow-up"></i></a>
        <@spring.showErrors "<br/>" "orcid-error"/>
        </div>
    </div>
</fieldset>
    <fieldset>
        <div class="control-group">
            <label for="addressLine1" class="control-label">Address 1</label>
            <div class="controls">
            <@spring.formInput "pastInstitutionsForm.addressLine1" 'class="span4"'/>               
            <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
        <div class="control-group">
            <label for="addressLine2" class="control-label">Address 2</label>
            <div class="controls">
            <@spring.formInput "pastInstitutionsForm.addressLine2" 'class="span4"'/>
            <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
        <div class="control-group">
            <label for="city" class="control-label">Town/City</label>
            <div class="controls">
            <@spring.formInput "pastInstitutionsForm.city" 'class="span4"'/>
                <span class="required">*</span>
            <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
        <div class="control-group">
            <label for="state" class="control-label">State/County/Province</label>
            <div class="controls">
            <@spring.formInput "pastInstitutionsForm.state" 'class="span4"'/>
            </div>
        </div>
        <div class="control-group">
            <label for="zipCode" class="control-label">Postcode/Zip</label>
            <div class="controls">
            <@spring.formInput "pastInstitutionsForm.zipCode" 'class="span4"'/>
            <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
        <div class="control-group">
            <label for="country" class="control-label">Country</label>
            <div class="controls">
            <@spring.formSingleSelect "pastInstitutionsForm.country", countries, 'class="span4"'/>
                <span class="required">*</span>
            <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
        <div class="control-group">
            <label for="startDate" class="control-label">Start date</label>
            <div class="controls">
            <@spring.formSingleSelect "pastInstitutionsForm.startDate", allDates, 'class="span4"'/>
                <span class="required">*</span>
            <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
        <div class="control-group">
            <label for="endDate" class="control-label">End date</label>
            <div class="controls">
            <@spring.formSingleSelect "pastInstitutionsForm.endDate", allDates, 'class="span4"'/>
                <span class="required">*</span>
            <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
        <div class="control-group">
            <label for="registrationRole" class="control-label">Role</label>
            <div class="controls">
            <@spring.formSingleSelect "pastInstitutionsForm.registrationRole", registrationRoles, 'class="span4"'/>
            <@spring.showErrors "<br/>" "orcid-error"/>
            </div>
        </div>
        <div class="form-actions">
            <button class="btn" type="submit">Add past institution</button>
        </div>
</fieldset>
</form>
<script>
    $(function () {
        $('#add-past-dept-name').click(function() {
            var val = $('#past-dept-name').val();
            if (val != '') {
                $('#departments').append( new Option(val ,val) );
                $('#past-dept-name').val('');
            }
            return false;
        });
        $('#remove-past-dept-name').click(function() {
            var selected = $('#departments option:selected');
            $('#past-dept-name').val(selected.val());
            $('#departments option:selected').remove();
            return false;
        });

        $('#past-affiliation-add').submit(function() {
            $('#departments option').attr('selected','selected');
        });        	
        
    });
</script>