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
            <h1>Are any of these you?</h1>
         <@spring.bind "registrationForm.*" />
        		
           			<h4>We've found the following records. Your institution may have already created an ORCID Record for you.</h4>
        			<table class="table table-striped">
            			<thead>
            				<tr>               				
                				<th>ORCID ID (click link to login)</th>
                				<th>Email</th>
                				<th>Given Name(s)</th>
                				<th>Family Name</th>                				
                				<th>Institution</th>
            				</tr>
            			</thead>
            			<tbody>
            				<#list potentialDuplicates as duplicate>
            				 <tr>
            				 	<td><a href="<@spring.url '/account'/>">${duplicate.orcid.value}</a></td>
                    			<td>${(duplicate.orcidBio.contactDetails.retrievePrimaryEmail().value)!"Information not available"}</td>
                    			<td>${(duplicate.orcidBio.personalDetails.givenNames.content)!"Information not available"}</td>
                    			<td>${(duplicate.orcidBio.personalDetails.familyName.content)!"Information not available"}</td>             			
                    			<td>${(duplicate.orcidBio.primaryInstitution.primaryInstitutionName.content)!"Information not available"}</td>
                			</tr>
                			</#list>
            			</tbody>
        			</table>   		
        		
        		
        		<form id="ignoreDupesAndConfirmRegForm" class="form-horizontal" action="<@spring.url '/progress-to-confirm-registration-details'/>" method="post">
			        <button class="btn btn-primary" type="submit">None of these are me - continue to registration </button>
        		<form>
    </div>
 </div>

</@public>
