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
		<div class="errorHead">${springMacroRequestContext.getMessage("orcid_bio_search.notice")}</div>
		<div class="alert alert-error">
			<ul class="validationerrors">
				<#list spring.status.errorMessages?sort as error> <li>${error}</li> </#list>
			</ul>
		</div>
	</div>
</#if>
<div class="row">
   <div class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-offset-9 col-xs-12">
   		<div class="main-search">
	   		<h1>${springMacroRequestContext.getMessage("orcid_bio_search.h1advancedsearch")}</h1>
	   		<p><b>${springMacroRequestContext.getMessage("orcid_bio_search.pyoumustpopulate")}</b></p>
	       	<form id="searchForm" class="form-horizontal" action="<@spring.url '/orcid-search/search-for-orcid'/>" method="get">
	            <fieldset>             
	                <div class="control-group">
	                	<!-- Search by ORCID iD -->
	                    <label for="orcid" class="control-label">${springMacroRequestContext.getMessage("orcid_bio_search.labelsearchbyorcid")}</label>
	                    <div class="controls">
		                    <@spring.formInput "searchOrcidForm.orcid" 'class="input-xlarge"'/>
		                	<@spring.showErrors "<br/>" "error" />
	                    </div>
	                </div>
	            </fieldset>
	            <fieldset>
	                <div class="control-group">
	                	<!-- Given name -->
	                    <label for="givenName" class="control-label">${springMacroRequestContext.getMessage("orcid_bio_search.labelgivenname")}</label>
	                    <div class="controls">
		                    <@spring.formInput "searchOrcidForm.givenName" 'class="input-xlarge"'/>
		                	<@spring.showErrors "<br/>" "error" />
	                    </div>
	                </div>
	                <div class="control-group">
	                    <div class="controls">
	                    	<!-- Family name -->
	                        <label for="otherNamesSearchable" class="checkbox">
	                        <@spring.formCheckbox "searchOrcidForm.otherNamesSearchable"/>
	                        ${springMacroRequestContext.getMessage("orcid_bio_search.labelalsosearchothernames")}</label>
	                        <@spring.showErrors "<br/>" "error" />
	                    </div>
	                </div>
	                <div class="control-group">
	                	<!-- Family name -->
	                    <label for="familyName" class="control-label">${springMacroRequestContext.getMessage("orcid_bio_search.labelfamilyname")}</label>
	                    <div class="controls">
		                    <@spring.formInput "searchOrcidForm.familyName" 'class="input-xlarge"'/>
		                	<@spring.showErrors "<br/>" "error" />
	                    </div>
	                </div>
	                <div class="control-group">
	                	<!-- Keyword -->
	                    <label for="familyName" class="control-label">${springMacroRequestContext.getMessage("orcid_bio_search.labelkeywords")}</label>
	                    <div class="controls">
		                   	<@spring.formInput "searchOrcidForm.keyword" 'class="input-xlarge"'/>
		               		<@spring.showErrors "<br/>" "error" />
	                    </div>
	                </div>
	                <div class="control-group">
	                    <div class="controls">
	                        <button class="btn" type="submit">${springMacroRequestContext.getMessage("orcid_bio_search.btnsearch")}</button>
	                    </div>
	                </div>
	            </fieldset>
	       	</form>
       	</div>
       	<#include "search_results.ftl"/>
    </div>
</div>