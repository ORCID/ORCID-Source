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
<@spring.bind "searchOrcidForm.*" />
	<#if spring.status.error>
		<div class="errorBox">
			<div class="errorHead">Notice:</div>
			<div class="alert alert-error">
				<ul class="validationerrors">
					<#list spring.status.errorMessages?sort as error> <li>${error}</li> </#list>
				</ul>
			</div>
		</div>
	</#if>
	<div class="row">
	   <div class="span9 offset3">
    <h1>Advanced Search</h1>
    <p><b>You must populate a least one field.</b></p>
        <form id="searchForm" class="form-horizontal" action="<@spring.url '/orcid-search/search-for-orcid'/>" method="get">
            <fieldset>             
                <div class="control-group">
                    <label for="orcid" class="control-label">Search by ORCID ID</label>

                    <div class="controls">
                    <@spring.formInput "searchOrcidForm.orcid" 'class="input-xlarge"'/>
                <@spring.showErrors "<br/>" "error" />
                    </div>
                </div>
            </fieldset>
            <fieldset>
                <div class="control-group">
                    <label for="givenName" class="control-label">Given name</label>

                    <div class="controls">
                    <@spring.formInput "searchOrcidForm.givenName" 'class="input-xlarge"'/>
                <@spring.showErrors "<br/>" "error" />
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <label for="otherNamesSearchable" class="checkbox">
                    <@spring.formCheckbox "searchOrcidForm.otherNamesSearchable" 'class="input-xlarge"'/>
                        Also  search other names</label>
                <@spring.showErrors "<br/>" "error" />
                    </div>
                </div>
                <div class="control-group">
                    <label for="familyName" class="control-label">Family name</label>

                    <div class="controls">
                    <@spring.formInput "searchOrcidForm.familyName" 'class="input-xlarge"'/>
                <@spring.showErrors "<br/>" "error" />
                    </div>
                </div>
                <div class="control-group">
                    <label for="institutionName" class="control-label">Institution name</label>

                    <div class="controls">
                    <@spring.formInput "searchOrcidForm.institutionName" 'class="input-xlarge"'/>
                <@spring.showErrors "<br/>" "error" />
                    </div>
                </div>
                 <div class="control-group">
                    <div class="controls">
                        <label for="pastInstitutionsSearchable" class="checkbox">
                    <@spring.formCheckbox "searchOrcidForm.pastInstitutionsSearchable" 'class="input-xlarge"'/>
                        Also search Past Institutions</label>
                <@spring.showErrors "<br/>" "error" />
                    </div>
                </div>
                  <div class="control-group">
                    <label for="familyName" class="control-label">Keyword</label>
                    <div class="controls">
                    <@spring.formInput "searchOrcidForm.keyword" 'class="input-xlarge"'/>
               		 <@spring.showErrors "<br/>" "error" />
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <button class="btn" type="submit">Search</button>
                    </div>
                </div>
            </fieldset>
        </form>
        <#include "search_results.ftl"/>
        </div>
</div>