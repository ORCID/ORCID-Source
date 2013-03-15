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
<@public classes=['home']>
<div class="row">
<div class="span3"></div>
    <div class="span9">
            <h1>${springMacroRequestContext.getMessage("duplicate_researcher.Areanyoftheseyou")}</h1>
         <@spring.bind "registrationForm.*" />
        		
           			<h4>${springMacroRequestContext.getMessage("duplicate_researcher.wefoundfollowingrecords")}</h4>
        			<table class="table table-striped">
            			<thead>
            				<tr>               				
                				<th>${springMacroRequestContext.getMessage("duplicate_researcher.thORCID")}</th>
                				<th>${springMacroRequestContext.getMessage("duplicate_researcher.thEmail")}</th>
                				<th>${springMacroRequestContext.getMessage("duplicate_researcher.thgivennames")}</th>
                				<th>${springMacroRequestContext.getMessage("duplicate_researcher.thFamilyName")}</th>                				
                				<th>${springMacroRequestContext.getMessage("duplicate_researcher.thInstitution")}</th>
            				</tr>
            			</thead>
            			<tbody>
            				<#list potentialDuplicates as duplicate>
            				 <tr>
            				 	<td><a href="<@spring.url '/account'/>">${duplicate.orcid.value}</a></td>
                    			<td>${(duplicate.orcidBio.contactDetails.retrievePrimaryEmail().value)!"${springMacroRequestContext.getMessage('duplicate_researcher.thInformationNotavailable')}"}</td>
                    			<td>${(duplicate.orcidBio.personalDetails.givenNames.content)!"${springMacroRequestContext.getMessage('duplicate_researcher.thInformationNotavailable')}"}</td>
                    			<td>${(duplicate.orcidBio.personalDetails.familyName.content)!"${springMacroRequestContext.getMessage('duplicate_researcher.thInformationNotavailable')}"}</td>             			
                    			<td>${(duplicate.orcidBio.primaryInstitution.primaryInstitutionName.content)!"${springMacroRequestContext.getMessage('duplicate_researcher.thInformationNotavailable')}"}</td>
                			</tr>
                			</#list>
            			</tbody>
        			</table>   		
        		
        		
        		<form id="ignoreDupesAndConfirmRegForm" class="form-horizontal" action="<@spring.url '/progress-to-confirm-registration-details'/>" method="post">
			        <button class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("duplicate_researcher.btncontinuetoregistration")}</button>
        		<form>
    </div>
 </div>

</@public>
