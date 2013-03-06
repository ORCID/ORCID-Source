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
<@public classes=['home'] >
<div class="row">
    <div class="offset2 span8">
        <div class="page-header">
            <h1>Info <small>We've already got some details on record - are any of these you?</small></h1>
        </div>
        <div>
         <@spring.bind "oAuthRegistrationForm.*" />               
        	
        		
           			<h3 class="search-result-head">Existing Orcid Users</h3>
        			<table class="table table-striped">
            			<thead>
            				<tr>               				
                				<th>ORCID ID</th>
                				<th>Email</th>
                				<th>Given Name(s)</th>
                				<th>Family Name</th>
            				</tr>
            			</thead>
            			<tbody>
            				<#list potentialDuplicates as duplicate>
            				 <tr>
            				 	<td><a href="<@spring.url '/account'/>">${duplicate.orcid.value}</a></td>
                    			<td>${(duplicate.orcidBio.contactDetails.retrievePrimaryEmail().value)!"Information not available"}</td>
                    			<td>${(duplicate.orcidBio.personalDetails.givenNames.content)!"Information not available"}</td>
                    			<td>${(duplicate.orcidBio.personalDetails.familyName.content)!"Information not available"}</td>
                			</tr>
                			</#list>
            			</tbody>
        			</table>   		
        		
        		
        		<form id="ignoreDupesAndConfirmRegForm" class="form-horizontal" action="<@spring.url '/oauth-complete-signup'/>" method="post">
			        <div class="form-actions">
			        	<@spring.formHiddenInput "oAuthRegistrationForm.familyName"/>			        	
			        	<@spring.formHiddenInput "oAuthRegistrationForm.email"/>
			        	<@spring.formHiddenInput "oAuthRegistrationForm.givenNames"/>
			        	<button class="btn btn-large btn-primary" type="submit">None of these are me - continue to registration </button>
			        </div>
        		<form>
        </div>
    </div>
 	</div>
</body>

</@public>
